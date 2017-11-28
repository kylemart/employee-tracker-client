package group19.employeetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

//TODO: If you change anything in here make sure to generate parcelable
// The plugin for this can be found here: https://github.com/mcharmas/android-parcelable-intellij-plugin

public class User extends Admin implements Parcelable {
    public boolean isAdmin;
    public String firstName;
    public String lastName;
    public String email;

    private Bitmap pic;

    User(boolean isAdmin, String firstName, String lastName, String email) {
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void addPic(Bitmap pic) {
        this.pic = pic;
    }

    public Bitmap getPic() {
        return pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeParcelable(this.pic, flags);
    }

    protected User(Parcel in) {
        this.isAdmin = in.readByte() != 0;
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.pic = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User getUser(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("User", MODE_PRIVATE);

        return new User(pref.getBoolean("type", false),
                pref.getString("firstName", "FirstName"),
                pref.getString("lastName", "LastName"),
                pref.getString("email", "Default@gmail.com"));
    }

    public static void deleteUser(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("User", MODE_PRIVATE);

        pref.edit().clear().commit();
    }

    public static void createUser(Context context, boolean type, String firstName, String lastName, String email)
    {
        SharedPreferences pref = context.getSharedPreferences("User", MODE_PRIVATE);

        pref.edit().putBoolean("type", type).apply();
        pref.edit().putString("email", email).apply();
        pref.edit().putString("firstName", firstName).apply();
        pref.edit().putString("lastName", lastName).apply();
    }
}
