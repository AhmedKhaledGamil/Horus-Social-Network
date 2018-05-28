package com.horus.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    ArrayList<Post> myDataSet = new ArrayList<Post>();
    Context context;
    // e3ml constructor lel adapter hena 3shan tb3at feh l array mn l main activity

    OnClick onClick;

    public interface OnClick{
        void onLike(int position, TextView counterTV, Button likeBtn);
    }

    public PostsAdapter (Context context, ArrayList<Post> data)
    {
        this.context = context;
        myDataSet=data;
    }

    public PostsAdapter (Context context, ArrayList<Post> data, OnClick onClick)
    {
        this.context = context;
        myDataSet=data;
        this.onClick = onClick;
    }


// da constructor
// nfs el 7war

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // link l view holder bel xml layout bta3to
        // t2sod hna el card ?
        // kda asdy, btrbot l viewholder bel xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String userID = context.getSharedPreferences("User", Context.MODE_PRIVATE).getString("id", "");
        // hamada msh bttktb asln w

       // System.out.println("hmada " + myDataSet.get(position).PersonName);

        // set l values fel design objects mn l array
        holder.textView.setText(myDataSet.get(position).getContext());
        Picasso.get().load("http://64.52.86.76:5000/"+myDataSet.get(position).getPostPic()).placeholder(R.mipmap.comment).into(holder.imageView);
        Picasso.get().load("http://64.52.86.76:5000/"+ myDataSet.get(position).getUserPic()).placeholder(R.mipmap.like).into(holder.userPic);
        holder.userName.setText(myDataSet.get(position).getUserName());
        holder.likeCounter.setText(myDataSet.get(position).getNumOfLikes());

        if (myDataSet.get(position).getPostPic().isEmpty())
            holder.imageView.setVisibility(View.GONE);

        for (String like: myDataSet.get(position).getLikers_id()){

            if (like.equals(userID)){

                holder.likeBtn.setText("Liked");
                holder.likeBtn.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
                break;
            }

        }

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClick.onLike(holder.getAdapterPosition(), holder.likeCounter, holder.likeBtn);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("post", myDataSet.get(holder.getAdapterPosition()));
               // intent.putParcelableArrayListExtra("comment",myDataSet.get(holder.getPost_comments()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // hena ht3arf el objects eli 3ndak fel design zy l textview wel imgview w hakza
        // w hna htrbot kol item bel id bta3o zy mbt3ml fel activity 3adi.
        public ImageView imageView;
        public TextView textView;
        public ImageView userPic;
        public TextView userName;
        public TextView likeCounter;

        Button likeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
             imageView = itemView.findViewById(R.id.Post_Picture);
             textView = itemView.findViewById(R.id.Post_Text);
             userPic = itemView.findViewById(R.id.Profile_Picture);
            userName = itemView.findViewById(R.id.Name);
            likeCounter = itemView.findViewById(R.id.likeCounter);

            likeBtn = itemView.findViewById(R.id.Like);

        }
    }
}
