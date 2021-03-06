package test.example.volunteer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApplyActivity extends AppCompatActivity {

    private String TAG = "ApplyActivityTAG";
    public static final int APP_ACCEPTED = 1;
    public static final int APP_REJECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null && auth.getUid() != null) {
            final Opportunity opportunity = extras.getParcelable("opportunity");

            if (opportunity != null) {
                populateOpportunity(opportunity);

                final Map<String, Object> addUserToArrayMap = new HashMap<>();
                Map<String, Object> newApplicant = new HashMap<>();
                newApplicant.put(auth.getUid(), 0);
                addUserToArrayMap.put("applicantsUIDs", newApplicant);
                addUserToArrayMap.put("hasApplications", true);

                Button applyBtn = findViewById(R.id.applyBtn);
                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Code to add Applications
                        Map<String, Boolean> application = new HashMap<>();
                        db.collection("opportunities")
                                .document(opportunity.getUID())
                                .set(addUserToArrayMap, SetOptions.merge()) //"hasApplications", true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ApplyActivity.this,
                                                getString(R.string.application_submitted),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });

                // Changes in case just viewing application status (not applying)
                if ((Boolean) extras.get("viewStatus")) {
                    setTitle(R.string.your_application);
                    applyBtn.setVisibility(View.GONE);
                    TextView appStatus = findViewById(R.id.appStatus);
                    appStatus.setVisibility(View.VISIBLE);

                    Integer myAppStatus = opportunity.getApplicantsUIDs().get(auth.getUid());
                    switch (myAppStatus) {
                        case APP_ACCEPTED:
                            appStatus.setText(R.string.app_accepted);
                            appStatus.setBackgroundColor(0xFF008000); // (Green) 0xFF + HEX RGB
                            break; // You have to break so that code execution stops
                        case APP_REJECTED:
                            appStatus.setText(R.string.app_rejected);
                            appStatus.setBackgroundColor(0xFFFF0000); // (Red) 0xFF + HEX RGB
                            break;
                    }

                }

                // Connect to database and get the Hospital information
                DocumentReference docRef = db.collection("users")
                        .document(opportunity.getHospitalUID());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot hospitalDoc = task.getResult();

                            if (hospitalDoc != null) {
                                Hospital hospital = hospitalDoc.toObject(Hospital.class);
                                populateHospital(hospital);
                            }
                        }
                    }
                });
            }
        }
    }

    private void populateOpportunity(Opportunity opportunity) {
        TextView opportunityTitle = findViewById(R.id.opportunityTitle);
        opportunityTitle.setText(opportunity.getTitle());

        TextView opportunityDescription = findViewById(R.id.opportunityDescription);
        opportunityDescription.setText(opportunity.getDescription());

        TextView opportunityLocation = findViewById(R.id.opportunityLocation);
        opportunityLocation.setText(opportunity.getLocation());

        TextView opportunityDuration = findViewById(R.id.opportunityDuration);
        opportunityDuration.setText(String.valueOf(opportunity.getDuration()));

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        TextView opportunityStartDate = findViewById(R.id.opportunityStartDate);
        opportunityStartDate.setText(sfd.format((opportunity.getStartDate().toDate())));

        TextView opportunityEndDate = findViewById(R.id.opportunityEndDate);
        opportunityEndDate.setText(sfd.format((opportunity.getEndDate().toDate())));
    }

    private void populateHospital(Hospital hospital) {
        TextView hospitalName = findViewById(R.id.hospitalName);
        hospitalName.setText(hospital.getName());

        TextView hospitalAddress = findViewById(R.id.hospitalAddress);
        hospitalAddress.setText(hospital.getAddress());

        TextView hospitalPhone = findViewById(R.id.hospitalPhone);
        hospitalPhone.setText(hospital.getPhone());

        TextView hospitalEmail = findViewById(R.id.hospitalEmail);
        hospitalEmail.setText(hospital.getEmail());
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
