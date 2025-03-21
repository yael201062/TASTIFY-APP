package com.example.tastify.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.R
import com.example.tastify.databinding.FragmentMyPostsBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.MyPostsViewModel
import com.google.firebase.auth.FirebaseAuth

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!
    private val myPostsViewModel: MyPostsViewModel by viewModels()

    private lateinit var adapter: ReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת RecyclerView
        binding.recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReviewsAdapter(mutableListOf()) // ✅ יצירת אדפטר עם רשימה ריקה שניתנת לשינוי
        binding.recyclerReviews.adapter = adapter

        // טעינת ביקורות של המשתמש המחובר
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val userId = firebaseUser.uid
            Log.d("MyPostsFragment", "🔹 User ID: $userId") // ✅ בדיקת ה-UID

            myPostsViewModel.getMyReviews(userId) // 🔹 טעינת הביקורות של המשתמש

            myPostsViewModel.myReviews.observe(viewLifecycleOwner, Observer { reviews ->
                Log.d("MyPostsFragment", "✅ Loaded ${reviews.size} reviews") // ✅ בדיקה אם הנתונים מגיעים
                adapter.updateData(reviews.toMutableList()) // ✅ עדכון הנתונים ברשימה
            })
        } else {
            binding.tvMyPostsTitle.text = "אין משתמש מחובר"
        }

        // כפתור חזרה לעמוד הפרופיל
        binding.btnUpdateProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
