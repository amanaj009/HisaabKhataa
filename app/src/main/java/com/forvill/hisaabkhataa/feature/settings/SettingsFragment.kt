package com.forvill.hisaabkhataa.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        
        binding.toolbar.title = strings.settingsTitle

        viewModel.businessProfile.observe(viewLifecycleOwner) { profile ->
            binding.tvBusinessName.text = profile.businessName
            binding.tvOwnerName.text = profile.ownerName
            binding.tvProfileInitial.text = profile.businessName.take(1).uppercase()
        }

        viewModel.selectedLanguage.observe(viewLifecycleOwner) { language ->
            binding.tvSelectedLanguage.text = language
        }

        binding.btnLanguage.setOnClickListener {
            findNavController().navigate(R.id.languageFragment)
        }

        binding.btnPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_privacyPolicy)
        }
        
        binding.btnLogout.setOnClickListener {
            // Logout logic
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
