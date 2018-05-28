package com.horus.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MiddleMan extends AppCompatActivity {

    TextView Test;
    String userIDCaller;
    String userIDSharedPreferences;
    boolean condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_man);
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    public void check()
    {
        Intent ahlnYaOrtega = getIntent();

        // El yb3aat el intent yt2ked en el name "_id"
        // 3amelha Comment 3shan a test bas....
        userIDCaller = ahlnYaOrtega.getStringExtra("_id");
        //userIDCaller = "5afa2de18024b6bbc5d01fb1";
        // Ahmed AbdElRahmaan el mafrood 3amel save lel ID, nt2ked en 3amlo b same name w same key
        // 3amelha Comment 3shan a test bas...
        userIDSharedPreferences = getSharedPreferences("User", MODE_PRIVATE).getString("id", "");
//        userIDSharedPreferences = "5afa2de18024b6bbc5d01fb1";
        //Test = findViewById(R.id.Test);
        //Test.setText(userIDCaller);
        if (userIDSharedPreferences.equals(userIDCaller))
        {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else
        {
            String url = "http://64.52.86.76:5000/api/user/getuser/" + userIDSharedPreferences;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            Log.i("Hamada", response.toString());
                            try {
                                JSONArray following = response.getJSONObject("user").getJSONArray("following");
                                String dummyID;
                                for (int i = 0; i < following.length(); i++)
                                {
                                    dummyID = following.getJSONObject(i).getString("_id");
                                    if (dummyID.equals(userIDCaller))
                                    {
                                        condition = true;
                                        break;
                                    }
                                    else
                                        condition = false;
                                }
                                if (condition == false)
                                {
                                    Intent intent = new Intent(getApplicationContext(), NonFriendNonFollowing.class);
                                    intent.putExtra("_id",userIDCaller);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), FriendFollowing.class);
                                    intent.putExtra("_id",userIDCaller);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error)
                        {
                            Log.i("Hamada", error.getErrorDetail());
                            Log.i("Hamada",error.getErrorBody());
                        }
                    });
        }
    }

}
