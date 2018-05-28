package com.horus.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {


    EditText userEmailEditText, userPasswordEditText;
    Button loginTextView, createAccountTextView;
    ProgressDialog mProgressDialog;
    String TAG = "1:";
    JSONObject person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        userEmailEditText = findViewById(R.id.emailLoginEditText);
        userPasswordEditText = findViewById(R.id.passwordLoginEditText);
        loginTextView = findViewById(R.id.loginButtonMain);
        createAccountTextView = findViewById(R.id.createAccountButtonMain);
        mProgressDialog = new ProgressDialog(this);



        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this, RegisterUserActivity.class));
            }
        });



        loginTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mProgressDialog.setTitle("logging in");
                mProgressDialog.setMessage("wait till you log in");
                mProgressDialog.show();
                loginUser();
            }
        });

    }

    private void loginUser() {
        String userEmail, userPassword;
        userEmail = userEmailEditText.getText().toString().trim();
        userPassword = userPasswordEditText.getText().toString().trim();


        if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
        {

            // Android Request
            AndroidNetworking.post("http://64.52.86.76:5000/api/user/login")
                    .addHeaders("content-type", "application/json")
                    .addBodyParameter("email", userEmail)
                    .addBodyParameter("password", userPassword)
                    .setTag("Signup")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            Intent moveToHome = new Intent(LogIn.this, NewsFeed.class);
                            moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            person= response;
                            String ID="", name = "", image = "";
                            try {
                                ID = person.getJSONObject("user").getString("_id");
                                name = person.getJSONObject("user").getString("name");
                                image = person.getJSONObject("user").getString("profileImage");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name", name);
                            editor.putString("id", ID);
                            editor.putString("image", image);
                            editor.putBoolean("login", true);
                            editor.apply();

                            moveToHome.putExtra("_id", ID);
                            Log.d("3:", "onResponse: "+ID);
                            startActivity(moveToHome);
                            mProgressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Toast.makeText(LogIn.this, "unable to login user", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    });

        }else
        {
            Toast.makeText(LogIn.this, "please enter email and password", Toast.LENGTH_LONG).show();
            mProgressDialog.dismiss();
        }

    }
}
