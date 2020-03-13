package product.clicklabs.jugnoo.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentData implements Parcelable {

    @Expose
    @SerializedName("document_id")
    private int documentId;

    @Expose
    @SerializedName("operator_id")
    private String operatorId;

    @Expose
    @SerializedName("document_name")
    private String documentName;

    @Expose
    @SerializedName("document_type")
    private String documentType;

    @Expose
    @SerializedName("num_images_required")
    private int numImagesRequired;

    @Expose
    @SerializedName("is_active")
    private String isActive;

    @Expose
    @SerializedName("status")
    private int status;

    @Expose
    @SerializedName("images")
    private List<String> imagesList;

    @Expose
    @SerializedName("reason")
    private String reason;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String document_name) {
        this.documentName = document_name;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getNumImagesRequired() {
        return numImagesRequired;
    }

    public void setNumImagesRequired(int numImagesRequired) {
        this.numImagesRequired = numImagesRequired;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.documentId);
        dest.writeString(this.operatorId);
        dest.writeString(this.documentName);
        dest.writeString(this.documentType);
        dest.writeInt(this.numImagesRequired);
        dest.writeString(this.isActive);
        dest.writeInt(this.status);
        dest.writeStringList(this.imagesList);
        dest.writeString(this.reason);
    }

    public DocumentData() {
    }

    protected DocumentData(Parcel in) {
        this.documentId = in.readInt();
        this.operatorId = in.readString();
        this.documentName = in.readString();
        this.documentType = in.readString();
        this.numImagesRequired = in.readInt();
        this.isActive = in.readString();
        this.status = in.readInt();
        this.imagesList = in.createStringArrayList();
        this.reason = in.readString();
    }

    public static final Creator<DocumentData> CREATOR = new Creator<DocumentData>() {
        @Override
        public DocumentData createFromParcel(Parcel source) {
            return new DocumentData(source);
        }

        @Override
        public DocumentData[] newArray(int size) {
            return new DocumentData[size];
        }
    };
}
