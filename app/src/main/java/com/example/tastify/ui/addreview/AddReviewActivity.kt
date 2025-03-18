package com.example.tastify.ui.addreview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tastify.R
import com.example.tastify.data.model.Review

class AddReviewActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private lateinit var imageView: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var etRestaurantName: EditText
    private lateinit var etLocation: EditText
    private lateinit var etReviewContent: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        imageView = findViewById(R.id.ivSelectedImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        etRestaurantName = findViewById(R.id.etRestaurantName)
        etLocation = findViewById(R.id.etLocation)
        etReviewContent = findViewById(R.id.etReviewContent)
        ratingBar = findViewById(R.id.ratingBar)
        btnSubmit = findViewById(R.id.btnSubmitReview)

        btnSelectImage.setOnClickListener {
            openGallery()
        }

        btnSubmit.setOnClickListener {
            submitReview()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun submitReview() {
        val restaurantName = etRestaurantName.text.toString()
        val location = etLocation.text.toString()
        val reviewContent = etReviewContent.text.toString()
        val rating = ratingBar.rating

        if (restaurantName.isEmpty() || location.isEmpty() || reviewContent.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        // כאן ניתן לשלב שמירת הביקורת במסד הנתונים או בהעברת הנתונים ל-ViewModel
        // כרגע ניצור אובייקט Review ונציג הודעת הצלחה
        val review = Review(
            id = System.currentTimeMillis().toString(),
            userId = "currentUserId",  // יש להחליף עם מזהה המשתמש האמיתי במידת הצורך
            restaurantId = restaurantName, // כאן אנו מניחים שהשם מהווה זיהוי; ניתן לשנות בהתאם
            rating = rating,
            comment = reviewContent,
            timestamp = System.currentTimeMillis()
        )

        // TODO: שמירת הביקורת (למשל, במסד נתונים או בעדכון ב-ViewModel)

        Toast.makeText(this, "ביקורת נוספה בהצלחה!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
