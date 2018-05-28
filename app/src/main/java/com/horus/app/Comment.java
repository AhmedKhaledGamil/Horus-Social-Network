package com.horus.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    String commentID;
    String commentText;
    String commenterID;
    String commenterName;
    String commenterProfilePic;

    public Comment(){}
    protected Comment(Parcel in) {
        commentID = in.readString();
        commentText = in.readString();
        commenterID = in.readString();
        commenterName = in.readString();
        commenterProfilePic = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getCommentID() {
        return commentID;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getCommenterID() {
        return commenterID;
    }

    public String getCommenterProfilePic() {
        return commenterProfilePic;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentID);
        dest.writeString(commentText);
        dest.writeString(commenterID);
        dest.writeString(commenterName);
        dest.writeString(commenterProfilePic);
    }
}
