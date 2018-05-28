package com.horus.app;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<Comment> comments = new ArrayList<Comment>();
    Context context;
    public CommentAdapter (Context context, ArrayList<Comment> commentData){
        comments=commentData;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (comments.get(position) != null && comments.size() > 0) {

            Log.d("commentData", comments.get(position).getCommentText());
            Log.d("commentData", comments.get(position).getCommenterName());
            Log.d("commentData", comments.get(position).getCommenterProfilePic());

            if(comments.get(position).getCommentText() != null)
            holder.textView.setText(comments.get(position).getCommentText());

            if(comments.get(position).getCommenterName() != null)
            holder.comment_name.setText(comments.get(position).getCommenterName());

            if (comments.get(position).getCommenterProfilePic() != null)
            Picasso.get().load("http://64.52.86.76:5000/" + comments.get(position).getCommenterProfilePic()).placeholder(R.mipmap.like).into(holder.comment_pic);

        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView comment_pic;
        public TextView comment_name;

        public ViewHolder(View itemView) {
            super(itemView);
           // cardView=itemView.findViewById(R.id.card_view);
            textView = itemView.findViewById(R.id.comment_text);
            comment_pic= itemView.findViewById(R.id.comment_pic);
            comment_name=itemView.findViewById(R.id.comment_name);

        }
    }
}