package com.example.tastify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.model.Review
import com.example.tastify.ui.profile.MyPostsFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ReviewsAdapter(
    private var reviews: MutableList<Review>,
    private val isEditable: Boolean = false
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvPostUserName)
        val txtRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val txtComment: TextView = view.findViewById(R.id.tvPostContent)
        val imgReview: ImageView = view.findViewById(R.id.ivPostImage)
        val imgUserProfile: ImageView = view.findViewById(R.id.ivProfileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        val userId = review.userId
        val db = FirebaseFirestore.getInstance()

        // טען שם ותמונת פרופיל של המשתמש
        if (userId.isNotBlank()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: "משתמש לא ידוע"
                    val profileImageUrl = document.getString("profileImageUrl")

                    holder.txtUserName.text = name

                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .transform(CropCircleTransformation())
                            .into(holder.imgUserProfile)
                    } else {
                        holder.imgUserProfile.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                }
                .addOnFailureListener {
                    holder.txtUserName.text = "שגיאה בטעינת משתמש"
                    holder.imgUserProfile.setImageResource(R.drawable.ic_profile_placeholder)
                }
        } else {
            holder.txtUserName.text = "משתמש לא ידוע"
            holder.imgUserProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // עריכה אם ניתן
        if (isEditable) {
            holder.itemView.setOnClickListener {
                val action = MyPostsFragmentDirections
                    .actionMyPostsFragmentToEditReviewFragment(review.id)
                it.findNavController().navigate(action)
            }
        }

        holder.txtRestaurantName.text = "מסעדה: ${review.restaurantId}"
        holder.ratingBar.rating = review.rating
        holder.txtComment.text = review.comment

        // טעינת תמונת ביקורת אם קיימת
        val imageUrl = review.imagePath
        if (!imageUrl.isNullOrEmpty() && imageUrl.startsWith("https://")) {
            holder.imgReview.visibility = View.VISIBLE
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(holder.imgReview)
        } else {
            holder.imgReview.visibility = View.GONE
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
