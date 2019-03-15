package test.example.volunteer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivityTAG";
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object and get current user
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //check if user is signed in or not
        if (currentUser != null) {
            // user already signed in
            // get user type and redirect if user already signed in
            getUserTypeAndRedirect();
        } else {
            //initializing views
            progressDialog = new ProgressDialog(this);
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextPassword = findViewById(R.id.editTextPassword);

            // hide the progressbar
            ProgressBar signInProgressBar = findViewById(R.id.signInProgressBar);
            signInProgressBar.setVisibility(View.GONE);

            // define the sign in button and its action
            Button signInButton = findViewById(R.id.signInButton);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //calling sign in method on click
                    signInUser();
                }
            });

            // define the register button and its action
            Button registerVolunteerButton = findViewById(R.id.registerVolunteerButton);
            registerVolunteerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //calling sign in method on click
                    Intent volunteerEditor = new Intent(getBaseContext(), VolunteerEditorActivity.class);
                    volunteerEditor.putExtra("editMode", false);
                    startActivity(volunteerEditor);
                }
            });

            // define the register button and its action
            Button registerHospitalButton = findViewById(R.id.registerHospitalButton);
            registerHospitalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //calling sign in method on click
                    Intent hospitalEditor = new Intent(getBaseContext(), HospitalEditorActivity.class);
                    hospitalEditor.putExtra("editMode", false);
                    startActivity(hospitalEditor);
                }
            });
        }
    }

    /*
    Helper method to Sign In user using Firebase Auth
    Using Email and Password
     */
    private void signInUser() {
        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.please_enter_email, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setTitle(getString(R.string.signing_in));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        // Connect to Firebase Auth to sign in user using Email and Password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            Toast.makeText(getBaseContext(), getString(R.string.successfully_signed_in), Toast.LENGTH_LONG).show();
                            getUserTypeAndRedirect();
                        } else {
                            //display some message here
                            Toast.makeText(getBaseContext(), getString(R.string.error) + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    /*
    Redirect user based on his account type
     */
    private void getUserTypeAndRedirect() {
        // Get user type
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

            // Connect to database and get the currentUser information
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
            docRef.get().

                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    // if the user exists on the database, check his type
                                    String userType = document.getString("type");
                                    Log.d(TAG, "DocumentSnapshot data: " + userType);
                                    currentUserType = userType;
                                    // redirect the user based on his type
                                    redirectUser();
                                } else {
                                    Log.d(TAG, "No such document");
                                    userSingOut();
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                                userSingOut();
                            }
                        }
                    });
        }
    }

    /*
    Helper method to redirect the user to the proper Activity
    based on the user type
     */
    private void redirectUser() {
        Log.d(TAG, "CurrentUserType: " + currentUserType);
        if (currentUserType != null) {
            switch (currentUserType) {
                case "Volunteer":
                    // goto Volunteer home activity
                    Intent vIntent = new Intent(getBaseContext(), VolunteerHomeActivity.class);
                    startActivity(vIntent);
                    finish();
                    break;
                case "Hospital":
                    // goto Hospital home activity
                    Intent hIntent = new Intent(getBaseContext(), HospitalHomeActivity.class);
                    startActivity(hIntent);
                    finish();
                    break;
                default:
                    Toast.makeText(this, R.string.invalid_user_type, Toast.LENGTH_LONG).show();
                    userSingOut();
                    break;
            }
        }
    }

    /*
    Helper method for user sign ou
     */
    public void userSingOut() {
        try {
            firebaseAuth.signOut();
            Toast.makeText(this, R.string.successfully_signed_out, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
