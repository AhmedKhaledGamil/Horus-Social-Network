package com.horus.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;


public class ProfileActivity extends AppCompatActivity
{

    public  ArrayList<Post> dataarr = new ArrayList<Post>();
    String TAG = "Success";
    RecyclerView userPosts;

    String userID;
    String name;
    String email;
    String mobile;
    String profilePicture;
    String coverPhoto;
    String bio;

    // Used to upload images
    String imgPath;

    public void loadUserData()
    {
        // Test Case
        userID = getSharedPreferences("User", MODE_PRIVATE).getString("id", ""); //"5afa2de18024b6bbc5d01fb1";

        Log.d("userIDDD", userID);

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
                        Toast.makeText(ProfileActivity.this, "Unable to load profile", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userID = getSharedPreferences("User", MODE_PRIVATE).getString("id", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Default Case
        userID = getSharedPreferences("user", MODE_PRIVATE).getString("_id", "");
        loadUserData();
        loadUserPosts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto(0);
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto(2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Cover Photo From Gallery
        if (requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            getPath(selectedImage);
            uploadImg("updateProfileCover");
        }
        // Cover Photo From Camera
        else if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            if (extras != null)
            {
                imageBitmap = (Bitmap) extras.get("data");
            }
            ImageView imageView = findViewById(R.id.coverPicture);
            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            File destination = new File(getApplicationContext().getFilesDir(), "temp.png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(destination);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPath = destination.getAbsolutePath();
            uploadImg("updateProfileCover");
        }
        // Profile Picture From Gallery
        else if (requestCode == 2 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ImageView imageView = findViewById(R.id.profilePicture);
                //imageView.setImageBitmap(bitmap);
                // Uploading it to server
                getPath(selectedImage);
                uploadImg("updateProfileImage");

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        // Profile Picture From Camera
        else if (requestCode == 3 && resultCode == RESULT_OK && data != null)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            if (extras != null)
            {
                imageBitmap = (Bitmap) extras.get("data");
            }
            ImageView imageView = findViewById(R.id.profilePicture);
            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            File destination = new File(getApplicationContext().getFilesDir(), "temp.png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(destination);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPath = destination.getAbsolutePath();
            uploadImg("updateProfileImage");
        }
    }

    public void getPhoto(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch (requestCode) {
            case 0:
                startActivityForResult(intent, 0);
                break;
            case 1:
                startActivityForResult(intent, 1);
                break;
            case 2:
                startActivityForResult(intent, 2);
                break;
            case 3:
                startActivityForResult(intent, 3);
                break;
        }

    }

    public void changePicture(final View view) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Gallery")) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    } else {
                        if (view.getTag().equals("CP")) {
                            getPhoto(0);
                            return true;
                        } else if (view.getTag().equals("PP")) {
                            getPhoto(2);
                            return true;
                        }
                    }
                } else if (item.getTitle().equals("Camera")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (view.getTag().equals("CP")) {
                        startActivityForResult(takePicture, 1);
                        return true;
                    } else if (view.getTag().equals("PP")) {
                        startActivityForResult(takePicture, 3);
                        return true;
                    }
                } else if (item.getTitle().equals("Cancel")) {
                    return true;
                }
                return true;
            }
        });
        popup.show();
    }

    public void editBioInformation(final View view)
    {
        Intent intent = new Intent(getApplicationContext(),BioActivity.class);

        intent.putExtra("name",name);
        intent.putExtra("email",email);
        intent.putExtra("mobile",mobile);
        intent.putExtra("userID",userID);
        intent.putExtra("bio",bio);
        startActivity(intent);
    }


    public void uploadImg(String type)
    {
        String url = "http://64.52.86.76:5000/api/user/" + type + "/" + userID;

        String key;

        if (type.equals("updateProfileImage") )
        {
            key = "profileImage";
        }
        else if (type.equals("updateProfileCover"))
        {
            key = "cover";
        }
        else
        {
            key = "";
        }

        File sourceFile = new File(imgPath);
        if (!sourceFile.exists())
        {
            Log.i("Hamada","Leeh keda !");
        }

        AndroidNetworking.upload(url)
                .addMultipartFile(key,sourceFile)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("Progress", String.valueOf(bytesUploaded) + " -- " + String.valueOf(totalBytes));
                        if (bytesUploaded == totalBytes)
                        {
                            loadUserData();
                        }
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("Response", response.toString());
                        loadUserData();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("errrr", error.getErrorBody());
                        Log.d("errrr", error.getErrorDetail());
                    }
                });
    }

    public void getPath(Uri uri)
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        imgPath = cursor.getString(idx);
        cursor.close();
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
                            LinearLayoutManager llm = new LinearLayoutManager(ProfileActivity.this);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            userPosts.setLayoutManager(llm);
                            PostsAdapter adapter = new PostsAdapter(ProfileActivity.this,dataarr);
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