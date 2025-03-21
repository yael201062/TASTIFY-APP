package com.example.tastify.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tastify.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsViewModel : ViewModel() {

    private val _myReviews = MutableLiveData<List<Review>>() // שדה שמחזיק ביקורות
    val myReviews: LiveData<List<Review>> get() = _myReviews

    fun getMyReviews(userId: String) {
        Log.d("MyPostsViewModel", "🔹 Fetching reviews for userId: $userId") // בדיקת ה-UID
        FirebaseFirestore.getInstance()
            .collection("reviews") // 🔹 מחפש בביקורות (ולא בפוסטים)
            .whereEqualTo("userId", userId) // 🔹 מסנן לפי `userId`
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.documents.mapNotNull { it.toObject(Review::class.java) }
                Log.d("MyPostsViewModel", "✅ Fetched ${reviews.size} reviews") // ✅ בדיקה אם הנתונים קיימים
                _myReviews.value = reviews
            }
            .addOnFailureListener { e ->
                Log.e("MyPostsViewModel", "❌ Failed to fetch reviews: ${e.message}")
                _myReviews.value = emptyList() // אם אין נתונים, מחזירים רשימה ריקה
            }
    }
}
