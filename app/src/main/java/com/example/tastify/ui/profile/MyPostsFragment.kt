package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.databinding.FragmentMyPostsBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository())
    }

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

        adapter = ReviewsAdapter(mutableListOf(), isEditable = true)
        binding.recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReviews.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // טוען את הביקורות של המשתמש
            reviewViewModel.getReviewsByUser(userId)

            // מאזין לשינויים ב־StateFlow
            lifecycleScope.launch {
                reviewViewModel.userReviews.collect { reviews ->
                    adapter.updateData(reviews.toMutableList())
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }


            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = true
                reviewViewModel.getReviewsByUser(userId)
            }
        } else {
            binding.tvMyPostsTitle.text = "אין משתמש מחובר"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
