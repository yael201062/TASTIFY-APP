package com.example.tastify.data.dao.repository

import com.example.tastify.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReviewRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    fun getAllReviews(): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(reviews).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("restaurantId", restaurantId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val filtered = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(filtered).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getReviewsByUser(userId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val userReviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(userReviews).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun getReviewById(reviewId: String): Review? {
        val snapshot = reviewsCollection.document(reviewId).get().await()
        return snapshot.toObject(Review::class.java)
    }

    suspend fun insertReview(review: Review) {
        val docRef = reviewsCollection.document()
        val reviewWithId = review.copy(id = docRef.id)
        docRef.set(reviewWithId).await()
    }

    suspend fun updateReview(review: Review) {
        reviewsCollection.document(review.id).set(review).await()
    }

    suspend fun deleteReview(review: Review) {
        reviewsCollection.document(review.id).delete().await()
    }
}
