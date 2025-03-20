package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.Post
import com.example.tastify.data.repository.ReviewRepository
import com.example.tastify.databinding.FragmentMyPostsBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.ui.home.PostAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository(AppDatabase.getDatabase(requireContext()).reviewDao()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        // ניווט לעדכון פרטים אישיים
        binding.btnUpdateProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        // הגדרת רשימת פוסטים
        val recyclerView = binding.recyclerReviews
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ReviewsAdapter(mutableListOf()) // אתחל עם רשימה ריקה
        binding.recyclerReviews.adapter = adapter

        // מאזין לשינויים בביקורות ומעדכן את ה-Adapter
        lifecycleScope.launch {
            reviewViewModel.reviews.collect { reviewsList ->
                adapter.updateData(reviewsList)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
