package com.example.tastify.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.ui.profile.MyPostsFragmentDirections
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.model.Review
import com.example.tastify.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ReviewsAdapter(private var reviews: MutableList<Review>,  private val isEditable: Boolean = false) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

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
        val context = holder.itemView.context

        holder.txtUserName.text = "טוען שם..."

        // יצירת UserViewModel באופן ידני בתוך האדפטר
        val userDao = AppDatabase.getDatabase(context).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModel.Factory(repository)

        val userViewModel = ViewModelProvider(
            context as androidx.lifecycle.ViewModelStoreOwner,
            factory
        )[UserViewModel::class.java]

        userViewModel.getUserName(review.userId)
            .observe(context as LifecycleOwner) { name ->
                holder.txtUserName.text = name ?: "משתמש לא ידוע"
            }


        if (isEditable) {
            holder.itemView.setOnClickListener {
                val action = MyPostsFragmentDirections.actionMyPostsFragmentToEditReviewFragment(review.id.toString())
                it.findNavController().navigate(action)
            }
        }

        holder.txtRestaurantName.text = "מסעדה: ${review.restaurantId}"
        holder.ratingBar.rating = review.rating
        holder.txtComment.text = review.comment

        if (!review.imagePath.isNullOrEmpty()) {
            holder.imgReview.visibility = View.VISIBLE
            Picasso.get().load(review.imagePath).into(holder.imgReview)
        } else {
            // טען תמונה רנדומלית של אוכל מ-Unsplash אם אין imagePath
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
                        val json = it.body?.string() ?: return
                        val imageUrl = JSONObject(json)
                            .getJSONObject("urls")
                            .getString("regular")

                        holder.imgReview.post {
                            Picasso.get().load(imageUrl).into(holder.imgReview)
                            holder.imgReview.visibility = View.VISIBLE
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