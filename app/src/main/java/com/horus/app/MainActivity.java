package com.horus.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    int counter=0;
    int x=0;
    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        Button button = findViewById(R.id.button_get);
        Button secondPage = findViewById(R.id.button4);
        Button newsfeedButton = findViewById(R.id.newsFeed);
     //   final TextView data_text = findViewById(R.id.data_textView);


        newsfeedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
               //get_data();
               // Post_data();
                pickImage();
                counter++;
            }
        });

        secondPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent myIntent = new Intent(v.getContext(), Chat.class);
//                startActivityForResult(myIntent, 0);
            }
        });
    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                ImageView imageView=findViewById(R.id.imageView);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
               imageView.setImageBitmap(bmp);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
           File newfile=  null;
            try {
               newfile= whenConvertingInProgressToFile_thenCorrect(inputStream);
            }catch (IOException e){
                Log.i("File Convertion", "onActivityResult: "+e.toString());
            }

        AndroidNetworking.upload("https://drive.google.com/drive/my-drive")
                .addMultipartFile("image",newfile )
                .addMultipartParameter("key","value")
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.i("Picture", "onResponse: Uploaded");
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("Picture", "onError: "+error.toString());
                    }
                });
        }
    }

 /*  protected String get_data() {
        AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "1")
                .addQueryParameter("limit", "20")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response

                        Log.i("ds", "onResponse of get: "+response);
                        TextView data_text = findViewById(R.id.data_textView);
                        TextView FirstName = findViewById(R.id.FirtNameTextView);
                        TextView SecondName = findViewById(R.id.SecondNameTextView);

                        try {
                            JSONArray arr =response;
                            int count=arr.length();

                            if(count>x){
                                JSONObject onePieceOfData = response.getJSONObject(x);
                                String id= onePieceOfData.getString("id");
                                String firstName= onePieceOfData.getString("firstname");
                                String secondName= onePieceOfData.getString("lastname");
                                data_text.setText(id);
                                FirstName.setText(firstName);
                                SecondName.setText(secondName);
                                x++;
                                Log.d("got id", id);
                                Log.i("got firstname", firstName);
                                Log.i("got secondname", secondName);
                            }

                        }
                        catch (JSONException e){
                            Log.i("error of parsing", "can't get id"+e);
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("ds", "onError: "+error);
                    }
                });
        return null;
    }*/


    protected String Post_data() {

        EditText first_post= findViewById(R.id.editText2_fn);
        final String firststring = first_post.getText().toString();
        EditText second_post=findViewById(R.id.editText3_ln);
        String secondstring = second_post.getText().toString();

        AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                .addBodyParameter("firstname", firststring)
                .addBodyParameter("lastname", secondstring)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                       // Log.d(" id", "posted");
                        Log.i(" firstname", "posted");
                        Log.i(" secondname", "posted");
                        Log.i("finally", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("Post error: ",error.toString());
                        Log.i("Post error: ",firststring);
                    }
                });
        return null;
    }




}

