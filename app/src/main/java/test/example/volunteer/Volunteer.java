package test.example.volunteer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Volunteer implements Parcelable {
    private String Fname;
    private String Lname;
    private String Email;
    private String Password;
    private String Mobile;
    private String Age;
    private String Gender;
    private String City;
    private String Degree;
    private String Skills;
    private String UID;

    public Volunteer() {
    } // Needed for Firebase

    public Volunteer(String fname, String lname, String email, String password, String mobile, String age, String gender, String city, String degree, String skills, String UID) {
        Fname = fname;
        Lname = lname;
        Email = email;
        Password = password;
        Mobile = mobile;
        Age = age;
        Gender = gender;
        City = city;
        Degree = degree;
        Skills = skills;
        this.UID = UID;
    }

    public Volunteer(String fname, String lname, String email, String password) {
        Fname = fname;
        Lname = lname;
        Email = email;
        Password = password;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public String getSkills() {
        return Skills;
    }

    public void setSkills(String skills) {
        Skills = skills;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getType() {
        return "Volunteer";
    }

    @NonNull
    @Override
    public String toString() {
        return Fname + " " + Lname;
    }

    private Volunteer(Parcel in) {
        Fname    = in.readString();
        Lname    = in.readString();
        Email    = in.readString();
        Password = in.readString();
        Mobile   = in.readString();
        Age      = in.readString();
        Gender   = in.readString();
        City     = in.readString();
        Degree   = in.readString();
        Skills   = in.readString();
        UID      = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Fname);
        dest.writeString(Lname);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(Mobile);
        dest.writeString(Age   );
        dest.writeString(Gender);
        dest.writeString(City  );
        dest.writeString(Degree);
        dest.writeString(Skills);
        dest.writeString(UID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Volunteer> CREATOR = new Parcelable.Creator<Volunteer>() {
        @Override
        public Volunteer createFromParcel(Parcel in) {
            return new Volunteer(in);
        }

        @Override
        public Volunteer[] newArray(int size) {
            return new Volunteer[size];
        }
    };

}