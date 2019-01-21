package com.fugu;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 14/06/18.
 *
 * @author gurmail
 */

public class GroupingTag implements Parcelable {

    public static Creator<GroupingTag> CREATOR = new Creator<GroupingTag>() {
        @Override
        public GroupingTag createFromParcel(Parcel source) {
            return new GroupingTag(source);
        }

        @Override
        public GroupingTag[] newArray(int size) {
            return new GroupingTag[size];
        }
    };

    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("reseller_team_id")
    @Expose
    private Integer teamId;

    public GroupingTag() {
    }

    public GroupingTag(Parcel source) {
        if(!TextUtils.isEmpty(tagName))
            tagName = source.readString();
        else
            tagName = null;
        if(teamId != null)
            teamId = source.readInt();
        else
            teamId = null;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(!TextUtils.isEmpty(tagName))
            dest.writeString(tagName);
        else
            dest.writeString("");
        if(teamId != null)
            dest.writeInt(teamId);
        else
            dest.writeInt(-1);
    }
}
