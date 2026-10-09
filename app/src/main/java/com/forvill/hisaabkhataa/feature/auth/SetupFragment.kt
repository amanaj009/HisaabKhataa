package com.forvill.hisaabkhataa.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {
    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        binding.setupTitle.text = strings.setupTitle
        binding.setupSubtitle.text = strings.setupSubtitle
        binding.btnEnter.text = strings.enterDashboard

        binding.btnEnter.setOnClickListener {
            val profile = BusinessProfile(
                ownerName = binding.etOwnerName.text.toString(),
                businessName = binding.etBusinessName.text.toString(),
                businessType = binding.etBusinessType.text.toString(),
                city = binding.etCity.text.toString()
            )
            viewModel.updateProfile(profile)
            findNavController().navigate(R.id.action_setup_to_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
