package group19.employeetracker;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.HashSet;

//TODO: If you change anything in here make sure to generate parcelable
// The plugin for this can be found here: https://github.com/mcharmas/android-parcelable-intellij-plugin

public class Employee implements Parcelable {
    // The first and last name of the employee
    public String fullName, firstName, lastName;

    // Denoting the group(s) the employee is in
    public HashSet<String> group;

    // A tuple of latitude and longitude
    public LatLng coords;

    // The most recent picture of the employee
    public Bitmap pic;

    // If the employee is public
    private boolean visible;

    // If the employee has used the software recently
    public  boolean active;

    Employee(String fullName, String group, LatLng coords, Bitmap pic) {
        this.fullName = fullName;

        String[] names = fullName.split(" ", 2);
        this.firstName = names[0];

        if(names.length > 1)
            this.lastName = names[1];

        this.group = new HashSet<>(Arrays.asList(group.split(",")));
        this.coords = coords;
        this.pic = pic;
        this.active = false;
    }

    public void setVisibility(boolean visibility) {
        visible = visibility;
    }

    public boolean getVisibility() {
        return visible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullName);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeSerializable(this.group);
        dest.writeParcelable(this.coords, flags);
        dest.writeParcelable(this.pic, flags);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
    }

    protected Employee(Parcel in) {
        this.fullName = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.group = (HashSet<String>) in.readSerializable();
        this.coords = in.readParcelable(LatLng.class.getClassLoader());
        this.pic = in.readParcelable(Bitmap.class.getClassLoader());
        this.visible = in.readByte() != 0;
        this.active = in.readByte() != 0;
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel source) {
            return new Employee(source);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };
}
