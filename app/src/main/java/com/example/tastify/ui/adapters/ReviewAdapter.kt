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
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

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

        // ניווט לעריכת פוסט
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

        if (!review.imagePath.isNullOrEmpty()) {
            val file = File(review.imagePath)
            if (file.exists()) {
                holder.imgReview.visibility = View.VISIBLE
                Picasso.get().load(file).into(holder.imgReview)
            }
        } else {
            // בקשת תמונת אוכל רנדומלית
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.unsplash.com/photos/random?query=food&client_id=0c3PEFtuji3Yu2TyMg9M4XKB-dh1KWKFrK2ldqy--mk")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val bodyString = it.body?.string() ?: return
                        try {
                            val imageUrl = JSONObject(bodyString)
                                .getJSONObject("urls")
                                .getString("regular")

                            holder.imgReview.post {
                                Picasso.get().load(imageUrl).into(holder.imgReview)
                                holder.imgReview.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
