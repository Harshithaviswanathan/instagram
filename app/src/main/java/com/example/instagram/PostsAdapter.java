package com.example.instagram;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> posts;
    private Context context;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = new ArrayList<>(posts);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getcontent());

        // Set likes and shares count
        holder.likesCount.setText(String.valueOf(post.getLikesCount()));
        holder.sharesCount.setText(String.valueOf(post.getSharesCount()));

        // Set video content if available
        if (!post.getContentUrl().isEmpty()) {
            holder.postVideo.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(post.getContentUrl());
            holder.postVideo.setVideoURI(videoUri);
            holder.postVideo.start();
        } else {
            holder.postVideo.setVisibility(View.GONE);
        }

        // Set click listener for video view
        holder.postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the video again when clicked
                if (!post.getContentUrl().isEmpty()) {
                    holder.postVideo.start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    public Post getPostAtPosition(int position) {
        if (position >= 0 && position < posts.size()) {
            return posts.get(position);
        }
        return null;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        TextView postContent;
        TextView likesCount; // Added for likes count
        TextView sharesCount; // Added for shares count
        VideoView postVideo;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            likesCount = itemView.findViewById(R.id.likesCount); // Initialize likes count TextView
            sharesCount = itemView.findViewById(R.id.sharesCount); // Initialize shares count TextView
            postVideo = itemView.findViewById(R.id.postVideo);
        }
    }
}
