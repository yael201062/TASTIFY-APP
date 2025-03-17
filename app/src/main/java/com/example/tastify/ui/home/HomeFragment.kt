package com.example.tastify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val restaurantViewModel: RestaurantViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת RecyclerView לפוסטים
        postAdapter = PostAdapter()
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = postAdapter

        // צפייה בנתונים מה-PostViewModel
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.setPosts(posts)
        }

        // חיפוש מסעדות
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                restaurantViewModel.searchRestaurants(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        // כפתור התנתקות - להוסיף לוגיקה אם יש
        binding.btnLogout.setOnClickListener {
            // TODO: הוסף כאן לוגיקה להתנתקות אם צריך
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
