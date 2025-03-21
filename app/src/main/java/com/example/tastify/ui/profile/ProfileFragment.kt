package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tastify.R
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.databinding.FragmentProfileBinding
import com.example.tastify.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // אתחול ה-ViewModel עם UserRepository
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserRepository(userDao)

        userViewModel = ViewModelProvider(this, UserViewModel.Factory(userRepository))
            .get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val userId = firebaseUser.uid  // 🔹 קבלת ה-UID של המשתמש המחובר

            // טוען את הנתונים של המשתמש מהמסד
            userViewModel.loadUser(userId)

            // מאזין לעדכונים ומציג נתונים
            userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
                user?.let {
                    binding.tvUserName.text = it.name ?: "משתמש"
                    binding.tvEmail.text = it.email ?: "אין אימייל"

                    if (!it.profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(it.profileImageUrl).into(binding.ivProfileImage)
                    } else {
                        binding.ivProfileImage.setImageResource(R.drawable.default_profile)
                    }
                }
            }
        } else {
            binding.tvUserName.text = "אין משתמש מחובר"
            binding.tvEmail.text = ""
        }

        // מעבר למסך עריכת פרופיל
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
