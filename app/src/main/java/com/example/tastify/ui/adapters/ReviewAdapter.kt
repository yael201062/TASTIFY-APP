package com.example.tastify.ui.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

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
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
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

        holder.imgReview.visibility = View.INVISIBLE
        holder.progressBar.visibility = View.VISIBLE

        if (!review.imagePath.isNullOrBlank()) {
            loadImage(holder, review.imagePath!!)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val fallbackUrl = fetchRandomFoodImageUrl()
                withContext(Dispatchers.Main) {
                    loadImage(holder, fallbackUrl)
                }
            }
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

    private fun loadImage(holder: ReviewViewHolder, imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) {
            holder.progressBar.visibility = View.GONE
            holder.imgReview.visibility = View.VISIBLE
            holder.imgReview.setImageResource(R.drawable.image_error)
            return
        }

        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                holder.progressBar.visibility = View.GONE
                holder.imgReview.visibility = View.VISIBLE
                holder.imgReview.setImageBitmap(bitmap)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                holder.progressBar.visibility = View.GONE
                holder.imgReview.visibility = View.VISIBLE
                holder.imgReview.setImageResource(R.drawable.image_error)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                holder.progressBar.visibility = View.VISIBLE
            }
        }

        holder.imgReview.tag = target
        Picasso.get().load(imageUrl).into(target)
    }

    private fun fetchRandomFoodImageUrl(): String? {
        return try {
            val request = Request.Builder()
                .url("https://api.unsplash.com/photos/random?query=food&client_id=0c3PEFtuji3Yu2TyMg9M4XKB-dh1KWKFrK2ldqy--mk")
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string()
                val jsonObject = JSONObject(body ?: return null)
                jsonObject.getJSONObject("urls").getString("regular")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
