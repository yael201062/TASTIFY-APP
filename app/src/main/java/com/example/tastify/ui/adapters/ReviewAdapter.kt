package com.example.tastify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.model.Review
import com.squareup.picasso.Picasso

class ReviewsAdapter(private var reviews: MutableList<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtComment: TextView = view.findViewById(R.id.tvPostContent)
        val imgReview: ImageView = view.findViewById(R.id.ivPostImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.txtComment.text = review.comment
        review.imagePath?.let {
            Picasso.get().load(it).into(holder.imgReview)
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
