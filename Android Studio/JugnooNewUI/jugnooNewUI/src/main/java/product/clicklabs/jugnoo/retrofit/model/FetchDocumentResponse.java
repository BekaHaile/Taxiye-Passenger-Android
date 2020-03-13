package product.clicklabs.jugnoo.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.ArrayList;

public class FetchDocumentResponse extends FeedCommonResponse implements Parcelable {

    @Expose
    @SerializedName("data")
    ArrayList<DocumentData> documentDataList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.documentDataList);
    }

    public FetchDocumentResponse() {
    }

    protected FetchDocumentResponse(Parcel in) {
        this.documentDataList = in.createTypedArrayList(DocumentData.CREATOR);
    }

    public static final Creator<FetchDocumentResponse> CREATOR = new Creator<FetchDocumentResponse>() {
        @Override
        public FetchDocumentResponse createFromParcel(Parcel source) {
            return new FetchDocumentResponse(source);
        }

        @Override
        public FetchDocumentResponse[] newArray(int size) {
            return new FetchDocumentResponse[size];
        }
    };

    public ArrayList<DocumentData> getDocumentDataList() {
        return documentDataList;
    }

    public void setDocumentDataList(ArrayList<DocumentData> documentDataList) {
        this.documentDataList = documentDataList;
    }
}

