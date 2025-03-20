package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tastify.R
import com.example.tastify.databinding.FragmentProfileBinding
import com.example.tastify.viewmodel.UserViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = "1" // יש להחליף ב-ID הנכון של המשתמש המחובר
        userViewModel.loadUser(userId)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                binding.tvUserEmail.text = it.email

                if (!it.profileImageUrl.isNullOrEmpty()) {
                    Picasso.get().load(it.profileImageUrl).error(R.drawable.default_profile).into(binding.ivProfileImage)
                } else {
                    binding.ivProfileImage.setImageResource(R.drawable.default_profile)
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }
}
