package com.horus.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static android.widget.Toast.LENGTH_SHORT;

public class FriendFollowing extends AppCompatActivity {

    String mainAccountUserID;
    String userID;
    String name;
    String email;
    String mobile;
    String profilePicture;
    String coverPhoto;
    String bio;

    public ArrayList<Post> dataarr = new ArrayList<Post>();
    String TAG = "Success";
    RecyclerView userPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_following);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        userID = intent.getStringExtra("_id");
        loadUserData();
        loadUserPosts();
    }

    public void loadUserData()
    {
        String url = "http://64.52.86.76:5000/api/user/getuser/" + userID;

        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.i("Hamada",response.toString());
                        TextView userNameTextView = findViewById(R.id.userNameTextView);
                        TextView emailTextView = findViewById(R.id.emailTextView);
                        TextView mobileTextView = findViewById(R.id.mobileTextView);
                        TextView bioTextView = findViewById(R.id.bioTextViewID);
                        ImageView profilePictureImageView = findViewById(R.id.profilePicture);
                        ImageView coverPhotoImageView = findViewById(R.id.coverPicture);
                        try
                        {
                            name = response.getJSONObject("user").getString("name");
                            if (name.equals(""))
                                userNameTextView.setText("Username");
                            else
                                userNameTextView.setText(name);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            userNameTextView.setText("Username");
                        }
                        try
                        {
                            email = response.getJSONObject("user").getString("email");
                            if (email.equals(""))
                                emailTextView.setText("Email: ");
                            else
                                emailTextView.setText("Email: " + email);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            emailTextView.setText("Email: ");
                        }
                        try
                        {
                            mobile = response.getJSONObject("user").getString("mobile");
                            if (mobile.equals(""))
                                mobileTextView.setText("Mobile: ");
                            else
                                mobileTextView.setText("Mobile: " + mobile);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            mobileTextView.setText("Mobile: ");
                        }
                        try
                        {
                            bio = response.getJSONObject("user").getString("bio");
                            if (bio.equals(""))
                                bioTextView.setText("You can enter your bio here.");
                            else
                                bioTextView.setText(bio);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            bioTextView.setText("You can enter your bio here.");
                        }
                        try
                        {
                            profilePicture = response.getJSONObject("user").getString("profileImage");
                            if (profilePicture.equals("img"))
                                Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Default_profile_picture_%28male%29_on_Facebook.jpg/600px-Default_profile_picture_%28male%29_on_Facebook.jpg").into(profilePictureImageView);
                            else
                                Picasso.get().load("http://64.52.86.76:5000/" + profilePicture).into(profilePictureImageView);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Default_profile_picture_%28male%29_on_Facebook.jpg/600px-Default_profile_picture_%28male%29_on_Facebook.jpg").into(profilePictureImageView);
                        }
                        try
                        {
                            coverPhoto = response.getJSONObject("user").getString("cover");
                            if (coverPhoto.equals("cover"))
                                Picasso.get().load("https://ibridgenetwork.org/images/content/default-cover.jpg").into(coverPhotoImageView);
                            else
                                Picasso.get().load("http://64.52.86.76:5000/" + coverPhoto).into(coverPhotoImageView);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Picasso.get().load("https://ibridgenetwork.org/images/content/default-cover.jpg").into(coverPhotoImageView);
                        }
                    }
                    @Override
                    public void onError(ANError error)
                    {
                        Log.i("Hamada", error.getErrorDetail());
                        Log.i("Hamada",error.getErrorBody());
                        // handle error
                        Toast.makeText(FriendFollowing.this, "Unable to load profile", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void unFollowUserButton (View view)
    {
        //Test Case
//        mainAccountUserID = "5afa2de18024b6bbc5d01fb1";
        //Default Case
        mainAccountUserID = getSharedPreferences("User", MODE_PRIVATE).getString("id", "");
        AndroidNetworking.post("http://64.52.86.76:5000/api/user/unfollow")
                .addHeaders("content-type", "application/json")
                .addBodyParameter("myID", mainAccountUserID)
                .addBodyParameter("followingID", userID)
                .setTag("unfollow")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.i("Hamada",response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("Hamada", error.getErrorDetail());
                        Log.i("Hamada", error.getErrorBody());
                    }
                });

        Intent intent = new Intent(getApplicationContext(),NonFriendNonFollowing.class);
        intent.putExtra("_id",userID);
        startActivity(intent);
    }

    public void loadUserPosts()
    {
        //userID = "5afa2cc88024b6bbc5d01fad";
        AndroidNetworking.get("http://64.52.86.76:5000/api/post/getUserPosts/"+ userID)
                .setTag("newsfeed")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONObject arr = new JSONObject(response.toString());
                            String count= arr.getString("count");
                            int num_of_posts = Integer.parseInt(count);
                            if(num_of_posts==0){
                                String mess =response.getString("message");
                                Toast.makeText(getApplicationContext(),mess, LENGTH_SHORT).show();
                                return;
                            }

                            Log.i("postsResp", response.toString());
                            Log.i("posts_num", "onResponse: "+num_of_posts);

                            JSONArray posts = arr.getJSONArray("posts");
                            for (int i = 0; i < num_of_posts; i++)
                            {
                                Post post = new Post();
                                JSONObject postObj = posts.getJSONObject(i);
                                post.postID = postObj.getString("_id");
                                // post.userID = postObj.getString("user");
                                post.context = postObj.getString("text");
                                post.postPic = postObj.getString("image");

                                JSONObject userObj = postObj.getJSONObject("user");
                                post.userID=userObj.getString("_id");
                                post.userName=userObj.getString("name");
                                post.userPic=userObj.getString("profileImage");

                                JSONArray likes = postObj.getJSONArray("likes");
                                int num_of_likes = likes.length();
                                post.numOfLikes=num_of_likes;

                                for (int y=0;y<num_of_likes;y++){
                                    JSONObject likeObj = likes.getJSONObject(y);
                                    String liker_id = likeObj.getString("_id");
                                    post.likers_id.add(liker_id);
                                }

                                JSONArray comments = postObj.getJSONArray("comments");
                                int num_of_comments = comments.length();

                                Log.i("commentsNum", String.valueOf(num_of_comments));

                                for (int y=0;y<num_of_comments;y++){
                                    Comment comment =  new  Comment();
                                    JSONObject commentObj = comments.getJSONObject(y);
                                    comment.commentID= commentObj.getString("_id");
                                    comment.commentText = commentObj.getString("text");

                                    Log.i("comment", comment.commentText);
                                    JSONObject commentList = commentObj.getJSONObject("user");

                                    comment.commenterID = commentList.getString("_id");
                                    comment.commenterName = commentList.getString("name");
                                    comment.commenterProfilePic= commentList.getString("profileImage");

                                    post.post_comments.add(comment);
                                }
                                dataarr.add(post);
                            }

                            Collections.reverse(dataarr);

                            userPosts = findViewById(R.id.userPostsRecyclerViewID);
                            LinearLayoutManager llm = new LinearLayoutManager(FriendFollowing.this);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            userPosts.setLayoutManager(llm);
                            PostsAdapter adapter = new PostsAdapter(FriendFollowing.this,dataarr);
                            userPosts.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i(TAG, "onError: "+error.toString());
                    }
                });
    }
}
