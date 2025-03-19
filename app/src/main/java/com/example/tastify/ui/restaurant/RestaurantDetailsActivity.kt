package com.example.tastify.ui.restaurant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tastify.data.api.OpenTableApiClient
import com.example.tastify.data.api.OpenTableRestaurant
import com.example.tastify.data.api.OpenTableResponse
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

        val restaurantName = intent.getStringExtra("restaurant_name") ?: ""

        if (restaurantName.isNotEmpty()) {
            fetchRestaurantDetails(restaurantName)
        } else {
            Toast.makeText(this, "Restaurant name not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchRestaurantDetails(name: String) {
        OpenTableApiClient.apiService.getRestaurantsByName(name)
            .enqueue(object : Callback<OpenTableResponse> {
                override fun onResponse(
                    call: Call<OpenTableResponse>,
                    response: Response<OpenTableResponse>
                ) {
                    if (response.isSuccessful) {
                        val restaurant = response.body()?.restaurants?.firstOrNull()
                        if (restaurant != null) {
                            displayRestaurantDetails(restaurant)
                        } else {
                            Toast.makeText(this@RestaurantDetailsActivity, "Restaurant not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RestaurantDetailsActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OpenTableResponse>, t: Throwable) {
                    Toast.makeText(this@RestaurantDetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayRestaurantDetails(restaurant: OpenTableRestaurant) {
        binding.textViewRestaurantName.text = restaurant.name
        binding.textViewRestaurantAddress.text = "${restaurant.address}, ${restaurant.city}, ${restaurant.country}"
        binding.textViewRating.text = "Price Level: ${restaurant.price}"
        binding.textViewCuisine.text = "Area: ${restaurant.area}"
        Picasso.get().load(restaurant.image_url).into(binding.imageViewRestaurant)
    }
}
