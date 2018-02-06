package product.clicklabs.jugnoo.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by socomo on 11/19/15.
 */
public class ContactBean implements Parcelable {

    private String name;
    private String phone;
    private String email;

    public ContactBean(){
    }

    protected ContactBean(Parcel in) {
        name = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel in) {
            return new ContactBean(in);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
    }
}
