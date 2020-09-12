package test.example.volunteer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Hospital implements Parcelable {
    private String Name;
    private String Email;
    private String Password;
    private String Phone;
    private String Address;
    private String City;
    private String AddInfo;
    private String UID;

    public Hospital() {
    } // Needed for Firebase

    Hospital(String name, String email, String password, String phone, String address, String city, String addInfo, String UID) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        Address = address;
        City = city;
        AddInfo = addInfo;
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String gender) {
        Address = gender;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAddInfo() {
        return AddInfo;
    }

    public void setAddInfo(String addInfo) {
        AddInfo = addInfo;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getType() {
        return "Hospital";
    }

    @NonNull
    @Override
    public String toString() {
        return Name;
    }

    private Hospital(Parcel in) {
        Name     = in.readString();
        Email    = in.readString();
        Password = in.readString();
        Phone    = in.readString();
        Address  = in.readString();
        City     = in.readString();
        AddInfo  = in.readString();
        UID      = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(Phone);
        dest.writeString(Address);
        dest.writeString(City);
        dest.writeString(AddInfo);
        dest.writeString(UID);
    }

    @SuppressWarnings("unused")
    public static final Creator<Hospital> CREATOR = new Creator<Hospital>() {
        @Override
        public Hospital createFromParcel(Parcel in) {
            return new Hospital(in);
        }

        @Override
        public Hospital[] newArray(int size) {
            return new Hospital[size];
        }
    };

}