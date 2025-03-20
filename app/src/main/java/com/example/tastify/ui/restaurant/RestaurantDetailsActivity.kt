package com.example.tastify.ui.restaurant

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tastify.R
import com.example.tastify.data.api.OpenTableApiClient
import com.example.tastify.data.api.OpenTableRestaurant
import com.example.tastify.databinding.ActivityRestaurantDetailsBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantId = intent.getStringExtra("restaurantId")

        Log.d("RestaurantDetailsActivity", "Received restaurantId: $restaurantId")

        if (!restaurantId.isNullOrEmpty()) {
            fetchRestaurantDetails(restaurantId)
        } else {
            Toast.makeText(this, "Restaurant ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchRestaurantDetails(restaurantId: String) {
        OpenTableApiClient.apiService.getRestaurantDetails(restaurantId)
            .enqueue(object : Callback<OpenTableRestaurant> {
                override fun onResponse(
                    call: Call<OpenTableRestaurant>,
                    response: Response<OpenTableRestaurant>
                ) {
                    Log.d("RestaurantDetailsActivity", "API Response Code: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        Log.d("RestaurantDetailsActivity", "API Response: ${response.body()}")
                        displayRestaurantDetails(response.body()!!)
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Log.e("RestaurantDetailsActivity", "Failed to load data. Error: $errorMessage")
                        Toast.makeText(this@RestaurantDetailsActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<OpenTableRestaurant>, t: Throwable) {
                    Log.e("RestaurantDetailsActivity", "API Request Failed: ${t.message}")
                    Toast.makeText(this@RestaurantDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayRestaurantDetails(restaurant: OpenTableRestaurant) {
        binding.textViewRestaurantName.text = restaurant.name
        binding.textViewRestaurantAddress.text = "${restaurant.address}, ${restaurant.city}, ${restaurant.country}"
        binding.textViewRating.text = "Price Level: ${restaurant.price}"
        binding.textViewCuisine.text = "Cuisine: ${restaurant.area}"

        if (!restaurant.image_url.isNullOrEmpty()) {
            Picasso.get().load(restaurant.image_url).into(binding.imageViewRestaurant)
        } else {
            binding.imageViewRestaurant.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
