package test.example.volunteer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ViewApplicationActivity extends AppCompatActivity {

    private String TAG = "ViewApplicationActivityTag";
    public static final int APP_ACCEPTED = 1;
    public static final int APP_REJECTED = 2;

    /**
     * Declare the TextViews
     */
    private EditText firstNameView;
    private EditText lastNameView;
    private EditText emailView;
    private EditText mobileView;
    private EditText ageView;
    private EditText genderView;
    private EditText cityView;
    private EditText degreeView;
    private EditText skillsView;
    private ImageView volunteerCVview;
    private String opportunityUID;
    private Volunteer volunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_application);

        // Find relevant views that will display data
        firstNameView = findViewById(R.id.volunteerFirstName);
        lastNameView = findViewById(R.id.volunteerLastName);
        emailView = findViewById(R.id.volunteerEmail);
        mobileView = findViewById(R.id.volunteerMobile);
        ageView = findViewById(R.id.volunteerAge);
        genderView = findViewById(R.id.volunteerGender);
        cityView = findViewById(R.id.volunteerCity);
        degreeView = findViewById(R.id.volunteerDegree);
        skillsView = findViewById(R.id.volunteerSkills);
        volunteerCVview = findViewById(R.id.volunteerCV);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            volunteer = extras.getParcelable("volunteer");
            opportunityUID = extras.getString("opportunityUID");
            Log.d(TAG, opportunityUID);
            if (volunteer != null) {
                populateUserData();
            }
        }

    }

    private void populateUserData() {
        firstNameView.setEnabled(false);
        firstNameView.setText(volunteer.getFname());

        lastNameView.setEnabled(false);
        lastNameView.setText(volunteer.getLname());

        emailView.setEnabled(false);
        emailView.setText(volunteer.getEmail());

        mobileView.setEnabled(false);
        mobileView.setText(volunteer.getMobile());

        ageView.setEnabled(false);
        ageView.setText(volunteer.getAge());

        genderView.setEnabled(false);
        genderView.setText(volunteer.getGender());

        cityView.setEnabled(false);
        cityView.setText(volunteer.getCity());

        degreeView.setEnabled(false);
        degreeView.setText(volunteer.getDegree());

        skillsView.setEnabled(false);
        skillsView.setText(volunteer.getSkills());

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().
                getReference()
                .child(volunteer.getUID() + ".jpg");

        // Download directly from StorageReference using Glide
        GlideApp.with(this)
                .load(storageReference)
                .into(volunteerCVview);

        Button acceptAppBtn = findViewById(R.id.acceptAppBtn);
        acceptAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppStatus(APP_ACCEPTED);
            }
        });

        Button rejectAppBtn = findViewById(R.id.rejectAppBtn);
        rejectAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppStatus(APP_REJECTED);
            }
        });

    }

    private void updateAppStatus(int status) {
        // Code to update Application Status
        final Map<String, Object> addUserToArrayMap = new HashMap<>();
        Map<String, Object> newApplicant = new HashMap<>();
        newApplicant.put(volunteer.getUID(), status);
        addUserToArrayMap.put("applicantsUIDs", newApplicant);
        addUserToArrayMap.put("hasApplications", true);

        FirebaseFirestore.getInstance()
                .collection("opportunities")
                .document(opportunityUID)
                .set(addUserToArrayMap, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ViewApplicationActivity.this,
                                getString(R.string.application_status_updated),
                                Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(ViewApplicationActivity.this, HospitalHomeActivity.class);
                        startActivity(intent);
                    }
                });
    }

    // Menu Methods
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
