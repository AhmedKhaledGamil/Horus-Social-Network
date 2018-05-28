package com.horus.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class AllMembers extends AppCompatActivity {

    RecyclerView allMembersRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        allMembersRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        allMembersRecyclerView.setLayoutManager(llm);

        final ArrayList<User> allMembers = new ArrayList<>();

        AndroidNetworking.get("http://64.52.86.76:5000/api/user/getAllusers")
                .setTag("getUsers")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    //Log.d("ALLMEMBERSLISTENER", "onResponse: "+"Gone");

                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        int userCount=0;

                        Log.d("ALLMEMBERSRESPONCE", "onResponse: "+response.toString());
                        try {
                            userCount = response.getInt("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray arr;
                        try {
                            arr = response.getJSONArray("users");
                            for (int i = 0; i<userCount; i++) {
                                User user = new User();
                                user.ID = arr.getJSONObject(i).getJSONObject("user").getString("_id");
                                user.name = arr.getJSONObject(i).getJSONObject("user").getString("name");
                                user.picture = "http://64.52.86.76:5000/"+arr.getJSONObject(i).getJSONObject("user").getString("profileImage");
                                Log.d("elarray", "onResponse: "+user.getPicture());
                                user.numberOfFollowers = arr.getJSONObject(i).getJSONArray("followers").length();
                                allMembers.add(user);
                            }
                            Collections.sort(allMembers);

                            UserAdapter adapter=new UserAdapter(AllMembers.this, allMembers);
                            allMembersRecyclerView.setAdapter(adapter );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ALLMEMBERSRESPONCE", "onResponse: "+error.getErrorBody());

                    }
                });


    }
}

class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private ArrayList<User> userList;
    Context context;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userimage;

        public MyViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.userItemName);
            userimage = view.findViewById(R.id.userItemImage);
        }
    }


    public UserAdapter(Context context,ArrayList<User> userList) {
        this.context=context;
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userviewitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.username.setText(user.getName());
        Picasso.get().load(user.getPicture()).placeholder(R.drawable.ic_launcher_background).into(holder.userimage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yallaYaOrtega = new Intent(context, MiddleMan.class);
                yallaYaOrtega.putExtra("_id", user.getID());
                context.startActivity(yallaYaOrtega);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}