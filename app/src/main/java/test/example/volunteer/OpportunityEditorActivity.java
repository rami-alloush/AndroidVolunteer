package test.example.volunteer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class OpportunityEditorActivity extends AppCompatActivity {

    private String TAG = "EditorActivity";
    /**
     * Declare the TextViews
     */
    private EditText titleView;
    private EditText descriptionView;
    private EditText locationView;
    private EditText durationView;
    private Button setDates;

    private String title;
    private String description;
    private String location;
    private int duration;
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean completed;
    private String hospitalUID;
    private ArrayList<String> applicantsUIDs;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private Boolean editMode;

    // For date picker
    private Calendar now = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            startDate = new Timestamp(new Date(calendar.getTimeInMillis()));

            // Open date and time pickers on current date and time
            DatePickerDialog myEndDate = new DatePickerDialog(OpportunityEditorActivity.this, endDatePickerListener,
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            // force future dates only
            myEndDate.getDatePicker().setMinDate(calendar.getTimeInMillis());
            myEndDate.show();
        }
    };
    DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            endDate = new Timestamp(new Date(calendar.getTimeInMillis()));

            // Display dates on the button
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            setDates.setText(new StringBuilder()
                    .append(sfd.format(startDate.toDate()))
                    .append(" - ")
                    .append(sfd.format(endDate.toDate())));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_editor);

        // Find relevant views that will display data
        titleView = findViewById(R.id.opportunityTitle);
        descriptionView = findViewById(R.id.opportunityDescription);
        locationView = findViewById(R.id.opportunityLocation);
        durationView = findViewById(R.id.opportunityDuration);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        editMode = Objects.requireNonNull(getIntent().getExtras()).getBoolean("editMode");
        if (editMode) {
            setTitle(getString(R.string.title_edit_profile));
        } else {
            setTitle(getString(R.string.add_opportunity));
        }

        Button setChanges = findViewById(R.id.saveOpportunityBtn);
        setChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOpportunity();
            }
        });

        setDates = findViewById(R.id.setDates);
        setDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open date pickers on current date
                DatePickerDialog myStartDate = new DatePickerDialog(OpportunityEditorActivity.this, startDatePickerListener,
                        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                // force future dates only
                myStartDate.getDatePicker().setMinDate(now.getTimeInMillis());
                myStartDate.show();
            }
        });
    }

    // Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.save));
        item.setIcon(android.R.drawable.ic_menu_save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                saveOpportunity();
                return true;
            case android.R.id.home:
                if (!editMode) {
//                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                    startActivity(intent);
                    finish();
                } else {
                    break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save new Opportunity from EditText fields to Firestore
     */
    private void saveOpportunity() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        title = titleView.getText().toString().trim();
        description = descriptionView.getText().toString().trim();
        location = locationView.getText().toString().trim();
        String durationString =durationView.getText().toString().trim();
        completed = false;
        hospitalUID = firebaseAuth.getCurrentUser().getUid();
        applicantsUIDs =new ArrayList<>();

        // Check if all the fields in the editor are filled
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(location) || TextUtils.isEmpty(durationString)) {
            Toast.makeText(this, R.string.fill_all_fields,
                    Toast.LENGTH_LONG).show();
            // make sure we don't continue the code
            return;
        }
        duration = Integer.parseInt(durationString);

        if (endDate == null){
            Toast.makeText(this, R.string.set_dates,
                    Toast.LENGTH_LONG).show();
            // make sure we don't continue the code
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference opportunitiesRef = db.collection("opportunities");
        opportunitiesRef
                .add(new Opportunity(title, description, location, duration, startDate, endDate, completed, hospitalUID, applicantsUIDs))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(OpportunityEditorActivity.this, R.string.opportunity_add, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}