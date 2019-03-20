package test.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HospitalHomeActivity extends AppCompatActivity {

    private static final String TAG = "HospitalHomeActivity";
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.show();
                        break;

                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addOpportunity = new Intent(getBaseContext(), OpportunityEditorActivity.class);
                addOpportunity.putExtra("editMode", false);
                startActivity(addOpportunity);
            }
        });

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ServicesFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return OpportunityFragment.newInstance("Hospital_Opportunities_Open", 0);
                case 1:
                    return OpportunityFragment.newInstance("Hospital_Opportunities_Completed", 0);
                default:
                    return OpportunityFragment.newInstance("Hospital_Applications", 0);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_opportunities_open);
                case 1:
                    return getString(R.string.title_opportunities_completed);
                case 2:
                    return getString(R.string.title_applications);
                default:
                    return "New Frag";
            }
        }
    }

    // Menu Functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemRemoveOpportunities = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.remove_all_opportunities));
        itemRemoveOpportunities.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem itemRemoveAcc = menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.remove_account));
        itemRemoveAcc.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem itemSignOut = menu.add(Menu.NONE, 10, Menu.NONE, getString(R.string.sign_out));
        itemSignOut.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                deleteCurrentUserOpportunities();
                return true;
            case 1:
                deleteCurrentUser();
                return true;
            case 10:
                userSingOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper method for user sign out
     */
    public void userSingOut() {
        try {
            firebaseAuth.signOut();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes currently signed in user from both firebaseAuth and DB
     */
    private void deleteCurrentUser() {
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "User Removed from firebaseAuth");
                    deleteCurrentUserFromDB();
                    deleteCurrentUserOpportunities();

                    Intent Intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(Intent);
                    Toast.makeText(HospitalHomeActivity.this,
                            "Account Removed Successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    private void deleteCurrentUserFromDB() {
        // Once user is removed from firebaseAuth, delete it from DB as well
        FirebaseFirestore.getInstance().
                collection("users")
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User document successfully deleted!");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void deleteCurrentUserOpportunities() {
        FirebaseFirestore.getInstance()
                .collection("opportunities")
                .whereEqualTo("hospitalUID", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            ds.getReference().delete();
                        }
                        Toast.makeText(HospitalHomeActivity.this,
                                getString(R.string.opportunities_deleted), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "All opportunities successfully deleted!");
                    }
                });
    }

}
