package com.horus.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BioActivity extends AppCompatActivity {

    String userID;
    String name;
    String email;
    String mobile;
    String bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        Intent intent = getIntent();
        TextView userName = findViewById(R.id.textView2);
        name = intent.getStringExtra("name");
        userName.setText(name);
        TextView userEmail = findViewById(R.id.textView3);
        email = intent.getStringExtra("email");
        userEmail.setText("Email: " + email);
        TextView userNumber = findViewById(R.id.textView4);
        mobile = intent.getStringExtra("mobile");
        userNumber.setText("Mobile: " + mobile);
        userID = intent.getStringExtra("userID");
        bio = intent.getStringExtra("bio");
        TextView userBio = findViewById(R.id.textView6);
        userBio.setText(bio);
    }

    public void submitButton(View view)
    {
        EditText userName = findViewById(R.id.editText);
        EditText userEmail = findViewById(R.id.editText2);
        EditText userMobile = findViewById(R.id.editText3);
        EditText userBio = findViewById(R.id.editText5);

        if (!emailValidator(userEmail.getText().toString()) && !userEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Invalid Email Format!", Toast.LENGTH_SHORT).show();
        }
        else if (!userMobile.getText().toString().isEmpty()&&((userMobile.getText().toString().length()!= 11)
                ||(!userMobile.getText().toString().substring(0,3).equals("010")&&!userMobile.getText().toString().substring(0,3).equals("011")
                &&!userMobile.getText().toString().substring(0,3).equals("012")
                &&!userMobile.getText().toString().substring(0,3).equals("015"))))
        {
            Toast.makeText(this, "Invalid Mobile Format!", Toast.LENGTH_SHORT).show();
        }
        else if (  userName.getText().toString().isEmpty()
                && userEmail.getText().toString().isEmpty()
                && userMobile.getText().toString().isEmpty()
                && userBio.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Nothing Entered!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!userName.getText().toString().isEmpty())
            {
                name = userName.getText().toString();
            }
            if (!userEmail.getText().toString().isEmpty())
            {
                email = userEmail.getText().toString();
            }
            if (!userMobile.getText().toString().isEmpty())
            {
                mobile = userMobile.getText().toString();
            }
            if (!userBio.getText().toString().isEmpty())
            {
                bio = userBio.getText().toString();
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("email",email);
                jsonObject.put("mobile",mobile);
                jsonObject.put("bio",bio);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            AndroidNetworking.post("http://64.52.86.76:5000/api/user/updateProfile/" + userID)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success"))
                                {
                                    //String dummy = response.getJSONObject("user").getString("name");
                                    //Log.i("Hamada",dummy);
                                    Log.i("Hamada",response.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.i("Hamada",anError.getErrorBody());
                            Toast toast = Toast.makeText(BioActivity.this, "Update process failed",Toast.LENGTH_LONG);
                        }
                    });
            finish();
        }
    }

    public void backButton(View view)
    {
        finish();
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }
}
