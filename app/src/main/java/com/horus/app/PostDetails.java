package com.horus.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class PostDetails  extends AppCompatActivity {

    TextView userNameTV, postContentTV;
    ImageView userImg, postImg;
    RecyclerView comment;
    String TAG = "Failed";
    ArrayList<Comment> comm = new ArrayList<Comment>();
    Post post = new Post();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);
        comment = findViewById(R.id.comment_area);
        post = (Post) getIntent().getParcelableExtra("post");

        userNameTV =findViewById(R.id.user_Name);
        userImg =findViewById(R.id.Pro_Pic);
        postContentTV =findViewById(R.id.Post_Text);
        postImg = findViewById(R.id.Post_Picture);

        userNameTV.setText(post.getUserName());
        Picasso.get().load("http://64.52.86.76:5000/" + post.getUserPic()).placeholder(R.mipmap.like).into(userImg);
        postContentTV.setText(post.getContext());
        Picasso.get().load("http://64.52.86.76:5000/" +post.getPostPic()).placeholder(R.mipmap.comment).into(postImg);

        if (post.getPostPic().isEmpty()){
            postImg.setVisibility(View.GONE);
        }

        final Button likeBtn = findViewById(R.id.likeBtn);
        final TextView likeCounter = findViewById(R.id.Like);
        String userID = getSharedPreferences("User", Context.MODE_PRIVATE).getString("id", "");

        for (String like: post.getLikers_id()){

            if (like.equals(userID)){

                likeBtn.setText("Liked");
                likeBtn.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                break;
            }

        }

        likeCounter.setText(String.valueOf(post.numOfLikes));

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                likePost(post.postID, likeCounter, likeBtn);

            }
        });


        posts_data(post.postID);

        Button Comment_Button = findViewById(R.id.Comment_Button);
        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post_data();
            }
        });
    }

    private void likePost(String postID, final TextView counterTV, final Button likeBtn){


        if (likeBtn.getText().toString().equals("Liked")){

            ANRequest.PostRequestBuilder postRequestBuilder = AndroidNetworking.post("http://64.52.86.76:5000/api/post/unlike");

            postRequestBuilder
                    .addBodyParameter("postID", postID)
                    .addBodyParameter("userID", getSharedPreferences("User", MODE_PRIVATE).getString("id", ""))
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getBoolean("success")){

                                    Toast.makeText(PostDetails.this, "post unliked", Toast.LENGTH_SHORT).show();

                                    likeBtn.setTextColor(getResources().getColor(android.R.color.black));
                                    likeBtn.setText("Like");
                                    counterTV.setText(String.valueOf(Integer.parseInt(counterTV.getText().toString().trim()) - 1));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });


        }
        else {

            ANRequest.PostRequestBuilder postRequestBuilder = AndroidNetworking.post("http://64.52.86.76:5000/api/post/like");

            postRequestBuilder
                    .addBodyParameter("postID", postID)
                    .addBodyParameter("userID", getSharedPreferences("User", MODE_PRIVATE).getString("id", ""))
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getBoolean("success")){

                                    Toast.makeText(PostDetails.this, "post liked", Toast.LENGTH_SHORT).show();

                                    likeBtn.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                                    likeBtn.setText("Liked");
                                    counterTV.setText(String.valueOf(Integer.parseInt(counterTV.getText().toString().trim()) + 1));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });

        }




    }



    void posts_data(String user_id){
        AndroidNetworking.get("http://64.52.86.76:5000/api/post/getPost/"+user_id)
                .setTag("newsfeed")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {


                            Log.i("postsResp", response.toString());

                            JSONObject postObj = response.getJSONObject("post");
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

                                    comm.add(comment);
                            }


                            LinearLayoutManager x = new LinearLayoutManager(PostDetails.this);
                            x.setOrientation(LinearLayoutManager.VERTICAL);
                            comment.setLayoutManager(x);
                            CommentAdapter commentAdapter = new CommentAdapter(PostDetails.this, comm);
                            comment.setAdapter(commentAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i(TAG, "onError: "+error.getErrorBody());
                    }
                });
    }

    protected String Post_data() {

        final EditText comment_text = findViewById(R.id.comment_field);
        String Comment_txt = comment_text.getText().toString();
        AndroidNetworking.post("http://64.52.86.76:5000/api/post/comment")
                .addBodyParameter("text", Comment_txt)
                .addBodyParameter("userID", getSharedPreferences("User", MODE_PRIVATE).getString("id", ""))
                .addBodyParameter("postID",post.postID)
                .setTag("post")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Toast.makeText(getApplicationContext(),"Commented",LENGTH_SHORT).show();

                        comment_text.setText("");
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });

        return null;
    }
}
