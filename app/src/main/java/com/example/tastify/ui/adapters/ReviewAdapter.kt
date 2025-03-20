package com.example.tastify.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.model.Review
import com.example.tastify.ui.restaurant.RestaurantDetailsActivity
import com.squareup.picasso.Picasso

class ReviewsAdapter(
    private val context: Context,
    private var reviews: MutableList<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvPostUserName)
        val txtRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
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

        holder.txtUserName.text = " ${review.userId}"
        holder.txtRestaurantName.text = "Restaurant Name: ${review.restaurantId}"
        holder.ratingBar.rating = review.rating
        holder.txtComment.text = review.comment

        // הצגת תמונה אם קיימת
        if (!review.imagePath.isNullOrEmpty()) {
            holder.imgReview.visibility = View.VISIBLE
            Picasso.get().load(review.imagePath).into(holder.imgReview)
        } else {
            holder.imgReview.visibility = View.GONE
        }

        // הוספת לחיצה על הפריט
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java).apply {
                putExtra("restaurantId", review.restaurantId) // שליחת ID של המסעדה
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
