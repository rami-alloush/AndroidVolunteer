package test.example.volunteer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolunteerEditorActivity extends AppCompatActivity {

    private String TAG = "EditorActivity";
    /**
     * Declare the TextViews
     */
    private EditText firstNameView;
    private EditText lastNameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText mobileView;
    private EditText ageView;
    private RadioGroup genderView;
    private EditText cityView;
    private EditText degreeView;
    private EditText skillsView;
    private ImageView volunteerCVview;
    private Bitmap bitmap;

    private String FName;
    private String LName;
    private String Email;
    private String Password;
    private String Mobile;
    private String Age;
    private String Gender;
    private String City;
    private String Degree;
    private String Skills;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseUser newUser;
    private int RESULT_LOAD_IMG = 100;
    private  Boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_editor);

        // Find relevant views that will display data
        firstNameView = findViewById(R.id.volunteerFirstName);
        lastNameView = findViewById(R.id.volunteerLastName);
        emailView = findViewById(R.id.volunteerEmail);
        passwordView = findViewById(R.id.volunteerPassword);
        mobileView = findViewById(R.id.volunteerMobile);
        ageView = findViewById(R.id.volunteerAge);
        genderView = findViewById(R.id.volunteerGender);
        cityView = findViewById(R.id.volunteerCity);
        degreeView = findViewById(R.id.volunteerDegree);
        skillsView = findViewById(R.id.volunteerSkills);
        volunteerCVview = findViewById(R.id.volunteerCV);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        editMode = Objects.requireNonNull(getIntent().getExtras()).getBoolean("editMode");
        if (editMode) {
            setTitle(getString(R.string.title_edit_profile));
        } else {
            setTitle(getString(R.string.register_volunteer));
        }

        Button loadImage = findViewById(R.id.loadCV);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        Button setChanges = findViewById(R.id.saveVolunteerBtn);
        setChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an Image is picked
        if (requestCode == RESULT_LOAD_IMG) {

            if (resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                try {
                    assert selectedImage != null;
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
//                    GlideApp.with(this)
//                            .load(bitmap)
//                            .into(labTestImageView);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                volunteerCVview.setImageBitmap(bitmap);

            } else {
                Toast.makeText(this, R.string.no_image_picked,
                        Toast.LENGTH_LONG).show();
            }

        }
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
                registerUser();
                return true;
            case android.R.id.home:
                if (!editMode) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
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

    /*
    Register User with FirebaseAuth
     */
    private void registerUser() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        FName = firstNameView.getText().toString().trim();
        LName = lastNameView.getText().toString().trim();
        Email = emailView.getText().toString().trim();
        Password = passwordView.getText().toString().trim();
        Mobile = mobileView.getText().toString().trim();
        Age = ageView.getText().toString().trim();
        RadioButton checkedGender = findViewById(genderView.getCheckedRadioButtonId());
        Gender = checkedGender.getText().toString();
        City = cityView.getText().toString().trim();
        Degree = degreeView.getText().toString().trim();
        Skills = skillsView.getText().toString().trim();

        // Check if all the fields in the editor are filled
        if (TextUtils.isEmpty(FName) && TextUtils.isEmpty(LName)
                && TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password)) {
            Toast.makeText(this, R.string.fill_all_fields,
                    Toast.LENGTH_LONG).show();
            // make sure we don't continue the code
            return;
        }

        //checking if any field is empty
        if (TextUtils.isEmpty(FName) || TextUtils.isEmpty(LName)) {
            Toast.makeText(this, R.string.please_enter_name, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Email) || !isEmailValid(Email)) {
            Toast.makeText(this, R.string.enter_valid_email, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
            return;
        }

        // Connect to Firebase Auth to create new user using Email and Password
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            // Sign in success
                            //display some message here
                            newUser = firebaseAuth.getCurrentUser();
                            if (firebaseAuth.getCurrentUser() != null) {
                                // Update Display Name
                                String fullName = FName + " " + LName;
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName).build();
                                if (newUser != null) {
                                    newUser.updateProfile(profileUpdates);
                                }
                                saveUser(newUser.getUid());
                            }

                        } else {
                            //display some message here
                            Toast.makeText(getBaseContext(), getString(R.string.error) + Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Save new Volunteer from EditText fields to Firestore
     */
    private void saveUser(String newUID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(newUID);
        userRef.set(new Volunteer(FName, LName, Email, Password, Mobile, Age, Gender, City, Degree, Skills, newUID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VolunteerEditorActivity.this, R.string.volunter_add, Toast.LENGTH_LONG).show();
                        }
                    });

        // Check if new image loaded
        if(bitmap != null) {
            // Get the data from an ImageView as bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Upload lab test image to Firestore Storage
            StorageReference mountainsRef = FirebaseStorage.getInstance().getReference(newUID + ".jpg");
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
        // Go home for redirect
        finish();
        Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(homeIntent);
    }

    /**
     * method is used for checking valid email id format.
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}