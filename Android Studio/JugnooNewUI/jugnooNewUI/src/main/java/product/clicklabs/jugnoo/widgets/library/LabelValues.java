package product.clicklabs.jugnoo.widgets.library;

import android.os.Parcel;
import android.os.Parcelable;

public class LabelValues implements Parcelable {

    private int key;
    private String value;
    private int selected;

    public LabelValues(int key, String value, int selected) {
        this.key = key;
        this.value = value;
        this.selected = selected;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.key);
        dest.writeString(this.value);
        dest.writeInt(this.selected);
    }

    private LabelValues(Parcel in) {
        this.key = in.readInt();
        this.value = in.readString();
        this.selected = in.readInt();
    }

    public static final Creator<LabelValues> CREATOR
            = new Creator<LabelValues>() {
        public LabelValues createFromParcel(Parcel source) {
            return new LabelValues(source);
        }

        public LabelValues[] newArray(int size) {
            return new LabelValues[size];
        }
    };

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
