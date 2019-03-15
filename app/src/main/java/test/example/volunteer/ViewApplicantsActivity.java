package test.example.volunteer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewApplicantsActivity extends AppCompatActivity {

    private static final String TAG = "ViewApplicantsActivity";
    private ApplicantsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicants);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final Opportunity opportunity = extras.getParcelable("opportunity");

            // data to populate the RecyclerView with
            final ArrayList<Volunteer> volunteers = new ArrayList<>();
            final ArrayList<Integer> volunteersAppsStatus = new ArrayList<>();

            // set up the RecyclerView
            final RecyclerView recyclerView = findViewById(R.id.rvVolunteers);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            if (opportunity != null) {
                // Get all applicants
                opportunity.getApplicantsUIDs();
                final HashMap<String, Integer> applicantsUIDs = opportunity.getApplicantsUIDs();

                if (applicantsUIDs != null) {
                    for (final Map.Entry<String, Integer> volunteerMap : applicantsUIDs.entrySet()) {
                        String voluntteerUID = volunteerMap.getKey();
                        final Integer voluntteerAppStatus = volunteerMap.getValue();
                        DocumentReference docRef = db.collection("users")
                                .document(voluntteerUID);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc != null) {
                                        volunteers.add(doc.toObject(Volunteer.class));
                                        volunteersAppsStatus.add(voluntteerAppStatus);
                                        // make sure adapter is not set until data is retrieved
                                        adapter = new ApplicantsRecyclerViewAdapter(ViewApplicantsActivity.this, volunteers, volunteersAppsStatus, opportunity);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }
                        });

                    }

                }
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
