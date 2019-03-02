package test.example.volunteer;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;

public class Opportunity implements Parcelable {
    private String title;
    private String description;
    private String location;
    private int duration;
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean completed;
    private String hospitalUID;
    private String applicantsUIDs;
    private String mUID;

    public Opportunity() {
    } // Needed for Firestore

    public Opportunity(String title, String description, String location, int duration, Timestamp startDate, Timestamp endDate, Boolean completed, String hospitalUID, String applicantsUIDs) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completed = completed;
        this.hospitalUID = hospitalUID;
        this.applicantsUIDs = applicantsUIDs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getApplicantsUIDs() {
        return applicantsUIDs;
    }

    public void setApplicantsUIDs(String applicantsUIDs) {
        this.applicantsUIDs = applicantsUIDs;
    }

    public String getmUID() {
        return mUID;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    protected Opportunity(Parcel in) {
        title = in.readString();
        description = in.readString();
        location = in.readString();
        duration = in.readInt();
        startDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        endDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        byte completedVal = in.readByte();
        completed = completedVal == 0x02 ? null : completedVal != 0x00;
        hospitalUID = in.readString();
        applicantsUIDs = in.readString();
        mUID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(duration);
        dest.writeValue(startDate);
        dest.writeValue(endDate);
        if (completed == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (completed ? 0x01 : 0x00));
        }
        dest.writeString(hospitalUID);
        dest.writeString(applicantsUIDs);
        dest.writeString(mUID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Opportunity> CREATOR = new Parcelable.Creator<Opportunity>() {
        @Override
        public Opportunity createFromParcel(Parcel in) {
            return new Opportunity(in);
        }

        @Override
        public Opportunity[] newArray(int size) {
            return new Opportunity[size];
        }
    };
}