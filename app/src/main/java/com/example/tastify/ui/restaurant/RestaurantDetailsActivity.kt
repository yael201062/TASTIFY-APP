package com.example.tastify.ui.restaurant

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tastify.R
import com.example.tastify.data.api.PexelsApiClient
import com.example.tastify.data.api.PexelsResponse
import com.example.tastify.databinding.ActivityRestaurantDetailsBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.squareup.picasso.Callback as PicassoCallback // ✅ תיקון הייבוא של Picasso

class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailsBinding
    private val pexelsApiKey = "gFSlPVkAj4QaAGCeNH3jvmcMeikyuaubFHMKRfGrdh8xQQqfKAnkM8ni" // ✅ API Key שלך

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantName = intent.getStringExtra("restaurantName") ?: "Restaurant"
        val cuisine = intent.getStringExtra("cuisine") ?: "Food"

        binding.textViewRestaurantName.text = restaurantName
        binding.textViewCuisine.text = "Cuisine: $cuisine"

        fetchRestaurantImage(restaurantName, cuisine)
    }

    private fun fetchRestaurantImage(restaurantName: String, cuisine: String) {
        val query = "$restaurantName $cuisine"

        binding.progressBarImageLoading.visibility = View.VISIBLE // ✅ הצגת progress bar

        PexelsApiClient.apiService.searchRestaurantImages(pexelsApiKey, query)
            .enqueue(object : Callback<PexelsResponse> {
                override fun onResponse(call: Call<PexelsResponse>, response: Response<PexelsResponse>) {
                    binding.progressBarImageLoading.visibility = View.GONE // ✅ הסתרת progress bar

                    if (response.isSuccessful && response.body()?.photos?.isNotEmpty() == true) {
                        val imageUrl = response.body()!!.photos[0].src.original
                        loadImage(imageUrl)
                    } else {
                        binding.imageViewRestaurant.setImageResource(R.drawable.ic_launcher_foreground) // ✅ תמונת ברירת מחדל
                    }
                }

                override fun onFailure(call: Call<PexelsResponse>, t: Throwable) {
                    binding.progressBarImageLoading.visibility = View.GONE // ✅ הסתרת progress bar במקרה של כישלון
                    Toast.makeText(this@RestaurantDetailsActivity, "Failed to load image", Toast.LENGTH_SHORT).show()
                    binding.imageViewRestaurant.setImageResource(R.drawable.ic_launcher_foreground) // ✅ תמונת ברירת מחדל במקרה של שגיאה
                }
            })
    }

    private fun loadImage(imageUrl: String) {
        Picasso.get().load(imageUrl)
            .into(binding.imageViewRestaurant, object : PicassoCallback { // ✅ שימוש נכון ב-Picasso Callback
                override fun onSuccess() {
                    binding.progressBarImageLoading.visibility = View.GONE // ✅ הסתרת progress bar אחרי טעינה מוצלחת
                }

                override fun onError(e: Exception?) {
                    binding.progressBarImageLoading.visibility = View.GONE // ✅ הסתרת progress bar במקרה של כישלון
                    binding.imageViewRestaurant.setImageResource(R.drawable.ic_launcher_foreground) // ✅ תמונת ברירת מחדל במקרה של שגיאה
                }
            })
    }
}
