package com.example.tastify.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.databinding.FragmentHomeBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import kotlinx.coroutines.launch



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository(AppDatabase.getDatabase(requireContext()).reviewDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReviewsAdapter(mutableListOf())
        binding.recyclerReviews.adapter = adapter

        observeAllReviews()

        // כפתור חיפוש לפי restaurantId
        binding.btnSearch.setOnClickListener {
            val searchQuery = binding.etSearch.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                observeReviewsByRestaurantId(searchQuery)
            } else {
                observeAllReviews()
            }
        }

        // ניווטים
        binding.fabAddReview.setOnClickListener {
            findNavController().navigate(R.id.addReviewFragment)
        }

        binding.btnPersonalArea.setOnClickListener {
            findNavController().navigate(R.id.myPostsFragment)
        }

        return binding.root
    }

    private fun observeAllReviews() {
        lifecycleScope.launch {
            reviewViewModel.reviews.collect { reviewsList ->
                adapter.updateData(reviewsList)
            }
        }
    }

    private fun observeReviewsByRestaurantId(restaurantId: String) {
        lifecycleScope.launch {
            reviewViewModel.getReviewsByRestaurant(restaurantId).collect { filtered ->
                adapter.updateData(filtered)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reviewViewModel.loadAllReviews()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

