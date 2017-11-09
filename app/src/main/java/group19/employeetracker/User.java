package group19.employeetracker;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

//TODO: If you change anything in here make sure to generate parcelable
// The plugin for this can be found here: https://github.com/mcharmas/android-parcelable-intellij-plugin

public class User extends Admin implements Parcelable {
    public boolean type;
    public String name;
    public String email;

    private Bitmap pic;

    User(boolean type, String name, String email) {
        this.type = type;
        this.name = name;
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
        dest.writeByte(this.type ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeParcelable(this.pic, flags);
    }

    protected User(Parcel in) {
        this.type = in.readByte() != 0;
        this.name = in.readString();
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
}
