package com.example.tastify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.data.model.Post
import com.example.tastify.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<Post> = listOf()

    fun setPosts(posts: List<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvPostUserName.text = post.userName
            binding.tvRestaurantName.text = post.restaurantName
            binding.tvPostContent.text = post.content
            binding.ratingBar.rating = post.rating.toFloat()

            if (!post.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(post.imageUrl).into(binding.ivPostImage)
            } else {
                binding.ivPostImage.visibility = View.GONE
            }
        }
    }
}
