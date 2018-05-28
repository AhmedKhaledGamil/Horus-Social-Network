package com.horus.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity {

    EditText userEmailCreateEditText, userPasswordCreateEditText, userNameCreateEditText, userMobileCreateEditText;
    Button createAccountButton;
    ProgressDialog mProgressDialog;
    String TAG = "1:";
    JSONObject person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        userEmailCreateEditText = findViewById(R.id.emailRegisterEditText);
        userPasswordCreateEditText = findViewById(R.id.passwordRegisterEditText);
        userNameCreateEditText = findViewById(R.id.nameRegisterEditText);
        userMobileCreateEditText = findViewById(R.id.mobileRegisterEditText);
        createAccountButton = findViewById(R.id.createAccountSubmitButton);
        mProgressDialog = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setTitle("creating account");
                mProgressDialog.setMessage("wait till the account is created");
                mProgressDialog.show();
                createUserAccount();

            }
        });

    }

    private void createUserAccount() {

        String emailUser, passUser, nameUser, mobileUser;
        emailUser = userEmailCreateEditText.getText().toString().trim();
        passUser = userPasswordCreateEditText.getText().toString().trim();
        nameUser = userNameCreateEditText.getText().toString().trim();
        mobileUser = userMobileCreateEditText.getText().toString().trim();



        if(!TextUtils.isEmpty(emailUser) && !TextUtils.isEmpty(passUser))
        {

            // Android Request
            AndroidNetworking.post("http://64.52.86.76:5000/api/user/signup")
                    .addHeaders("content-type", "application/json")
                    .addBodyParameter("name", nameUser)
                    .addBodyParameter("email", emailUser)
                    .addBodyParameter("password", passUser)
                    .addBodyParameter("mobile", mobileUser)
                    .setTag("Signup")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            Toast.makeText(RegisterUserActivity.this,"account created",Toast.LENGTH_LONG).show();
                            Intent moveToHome = new Intent(RegisterUserActivity.this, NewsFeed.class);
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
                            startActivity(moveToHome);
                            mProgressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Toast.makeText(RegisterUserActivity.this,"account was not created",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    });
        }
    }
}
