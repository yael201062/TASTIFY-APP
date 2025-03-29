package com.example.tastify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ReviewsAdapter(
    private var reviews: MutableList<Review>,
    private val isEditable: Boolean = false,
    private val onEditClicked: ((Review) -> Unit)? = null,
    private val onDeleteClicked: ((Review) -> Unit)? = null
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvPostUserName)
        val txtRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val txtComment: TextView = view.findViewById(R.id.tvPostContent)
        val imgReview: ImageView = view.findViewById(R.id.ivPostImage)
        val imgUserProfile: ImageView = view.findViewById(R.id.ivProfileImage)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(review.userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "משתמש"
                val profileUrl = document.getString("profileImageUrl")

                holder.txtUserName.text = name
                if (!profileUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(profileUrl)
                        .transform(CropCircleTransformation())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(holder.imgUserProfile)
                }
            }

        holder.txtRestaurantName.text = "מסעדה: ${review.restaurantId}"
        holder.ratingBar.rating = review.rating
        holder.txtComment.text = review.comment

        if (!review.imagePath.isNullOrEmpty()) {
            holder.imgReview.visibility = View.VISIBLE
            Picasso.get().load(review.imagePath).into(holder.imgReview)
        } else {
            holder.imgReview.visibility = View.GONE
        }

        if (isEditable) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE

            holder.btnEdit.setOnClickListener {
                onEditClicked?.invoke(review)
            }

            holder.btnDelete.setOnClickListener {
                onDeleteClicked?.invoke(review)
            }
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
