package com.horus.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
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
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import static android.widget.Toast.LENGTH_SHORT;

public class NewsFeed extends AppCompatActivity {

    public  ArrayList<Post> dataarr = new ArrayList<Post>();
    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    String TAG = "Success";
    String user_id="";
    //String user_id="5afa2de18024b6bbc5d01fb1";
    int flag=0;
    File newfile = null;
    TextView post_text;
    File sourceFile;

    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;
    private String imgPath;

    RecyclerView postsRV;

    ImageView imageView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_feed);

        user_id = getSharedPreferences("User", MODE_PRIVATE).getString("id", "");

        postsRV = findViewById(R.id.List_Of_Posts);

        Log.i("Entered news feed page", "onCreate: ");
        Button makepost = findViewById(R.id.makePost);
        Button pick_image = findViewById(R.id.getPic);
        Button home = findViewById(R.id.Home_Button);
        Button profile = findViewById(R.id.Profile_Button);
        Button Chat = findViewById(R.id.Chat_Button);
        post_text=findViewById(R.id.Post_Text);
        imageView=findViewById(R.id.postimage);
        posts_data(user_id);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        postsRV.setLayoutManager(llm);
        PostsAdapter adapter = new PostsAdapter(this,dataarr);
        postsRV.setAdapter(adapter);

        pick_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //pickImage();
                requestStoragePermission();
            }
        });
        makepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePost();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posts_data(user_id);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), ProfileActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(NewsFeed.this, AllMembers.class));

            }
        });


        TextView nameTV = findViewById(R.id.Name);
        ImageView userImg = findViewById(R.id.Profile_Picture);

        nameTV.setText(getSharedPreferences("User", MODE_PRIVATE).getString("name", ""));

        Picasso.get().load("http://64.52.86.76:5000/" + getSharedPreferences("User", MODE_PRIVATE).getString("image", "")).into(userImg);

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream inputStream=new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
                //ImageView imageView = findViewById(R.id.imageView);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
               // imageView.setImageBitmap(bmp);
               //postimage.setImageBitmap(bmp);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...

            try {
                newfile = whenConvertingInProgressToFile_thenCorrect(inputStream);
            } catch (IOException e) {
                Log.i("File Convertion", "onActivityResult: " + e.toString());
            }
            //write here the code of posting image
            }
        }*/


    public void makePost(){
      //  EditText Post_text = findViewById(R.id.Post_Text);
      //  String Post_txt = Post_text.getText().toString();

        if(flag==1){
            sourceFile = new File(imgPath);
            //post with image
            AndroidNetworking.upload("http://64.52.86.76:5000/api/post/newPost")
                    .addMultipartFile("image",sourceFile)
                    .addMultipartParameter("text", post_text.getText().toString())
                    .addMultipartParameter("id", user_id)
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress

                            Log.d("progrerss", String.valueOf(bytesUploaded) + " --- " + String.valueOf(totalBytes));
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            Toast.makeText(getApplicationContext(),"Posted",LENGTH_SHORT).show();

                            post_text.setText("");
                            imageView.setImageResource(android.R.color.transparent);

                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.i(TAG, "onError: "+error.getErrorBody());
                        }
                    });
            flag=0;
        }
        else {
            //post without image
            AndroidNetworking.post("http://64.52.86.76:5000/api/post/newPost")
                    .addBodyParameter("text", post_text.getText().toString())
                    .addBodyParameter("id", user_id)
                    .setTag("post")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            //Toast.makeText(getApplicationContext(),"Posted",LENGTH_SHORT).show();

                            post_text.setText("");
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.i(TAG, "onError: "+error.toString());
                        }
                    });


        }
    }
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
        flag=1;
    }


    public File whenConvertingInProgressToFile_thenCorrect(InputStream inputStream)
            throws IOException {
        File targetFile = new File("src/main/resources/targetFile.tmp");
        OutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        return targetFile;
    }


    void posts_data(String user_id){
        AndroidNetworking.get("http://64.52.86.76:5000/api/post/getNewsFeed/"+user_id)
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

                            dataarr = new ArrayList<>();

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

                            PostsAdapter adapter = new PostsAdapter(NewsFeed.this, dataarr, new PostsAdapter.OnClick() {
                                @Override
                                public void onLike(int position, TextView counterTV, Button likeBtn) {

                                    likePost(position, counterTV, likeBtn);
                                }
                            });
                            postsRV.setAdapter(adapter);

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


    private void likePost(int position, final TextView counterTV, final Button likeBtn){


        if (likeBtn.getText().toString().equals("Liked")){

            ANRequest.PostRequestBuilder postRequestBuilder = AndroidNetworking.post("http://64.52.86.76:5000/api/post/unlike");

            postRequestBuilder
                    .addBodyParameter("postID", dataarr.get(position).postID)
                    .addBodyParameter("userID", getSharedPreferences("User", MODE_PRIVATE).getString("id", ""))
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getBoolean("success")){

                                    Toast.makeText(NewsFeed.this, "post unliked", Toast.LENGTH_SHORT).show();

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
                    .addBodyParameter("postID", dataarr.get(position).postID)
                    .addBodyParameter("userID", getSharedPreferences("User", MODE_PRIVATE).getString("id", ""))
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getBoolean("success")){

                                    Toast.makeText(NewsFeed.this, "post liked", Toast.LENGTH_SHORT).show();

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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    @Override
    public void onResume() {
        super.onResume();
        if (filePath != null){
            imageView.setImageURI(filePath);
        }
        posts_data(user_id);
    }

    private void uploadImg(){

        sourceFile = new File(imgPath);

        ANRequest.MultiPartBuilder multiPartBuilder = AndroidNetworking.upload("API_URL");

        multiPartBuilder
                .addMultipartFile("image", sourceFile)
                .setTag("uploadImg")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress

                        Log.d("Progress", String.valueOf(bytesUploaded) + " -- " + String.valueOf(totalBytes));

                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // below code will be executed in the executor provided
                        // do anything with response
                        Log.d("Response", response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("errrr", error.getErrorBody());
                    }
                });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Requesting permission
    private void requestStoragePermission() {
        flag=1;
        if (ContextCompat.checkSelfPermission(NewsFeed.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            showFileChooser();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(NewsFeed.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(NewsFeed.this, "You have to enable storage permission", Toast.LENGTH_SHORT).show();
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(NewsFeed.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(NewsFeed.this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                showFileChooser();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(NewsFeed.this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            try {
                imgPath = getPath(NewsFeed.this, data.getData());
                Log.d("imgPathh", imgPath);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            filePath = data.getData();
            imageView.setImageURI(filePath);
           // uploadImg();
        }
    }


    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
