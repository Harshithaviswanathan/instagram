package com.example.instagram

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class StoriesAdapter(private var stories: MutableList<Story>) : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]

        Glide.with(holder.itemView)
            .load(story.imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .transform(CircleCrop())
            .into(holder.storyImage)

        holder.statusIndicator.setBackgroundResource(
            if (story.isActive) R.drawable.circle_green else R.drawable.circle_red
        )
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    fun updateStatus(newStories: List<Story>) {
        stories.clear()
        stories.addAll(newStories)
        notifyDataSetChanged()
    }

    fun clearStories() {
        stories.clear()
        notifyDataSetChanged()
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyImage: ImageView = itemView.findViewById(R.id.storyImage)
        val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
    }
}
