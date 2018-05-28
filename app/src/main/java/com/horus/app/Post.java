package com.horus.app;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Post implements Parcelable{

    String postID;
    String context;
    String postPic;
    String userID;
    String userName;
    String userPic;

    Like like;
    int numOfLikes;

    public ArrayList<Comment> post_comments = new ArrayList<Comment>();

    public ArrayList<String> likers_id = new ArrayList<String>();


    public  Post(){}

    public String getUserName() {
        return userName;
    }

    public ArrayList<Comment> getPost_comments() {
        return post_comments;
    }

    public ArrayList<String> getLikers_id() {
        return likers_id;
    }

    public String getNumOfLikes() {
        return String.valueOf(numOfLikes);
    }

    public String getPostPic() {
        return postPic;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getPostID() {
        return postID;
    }

    public String getUserID() {
        return userID;
    }


    protected Post(Parcel in) {
        postID = in.readString();
        postPic = in.readString();
        userID = in.readString();
//      picture = in.readParcelable(Bitmap.class.getClassLoader());
        context = in.readString();
        userName = in.readString();
        userPic = in.readString();

        in.readTypedList(post_comments, Comment.CREATOR);

    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    void Post(String cont, String name, String profile_pic){
        context=cont;
        userName=name;
        userPic=profile_pic;
    }


    String getContext(){
        return context;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
//      parcel.writeParcelable(picture, i);
        parcel.writeString(postID);
        parcel.writeString(postPic);
        parcel.writeString(userID);
        parcel.writeString(context);
        parcel.writeString(userName);
        parcel.writeString(userPic);
        parcel.writeTypedList(post_comments);
    }
}
