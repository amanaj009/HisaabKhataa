package com.forvill.hisaabkhataa.feature.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        binding.appName.text = strings.appName
        binding.tagline.text = strings.splashTagline
        binding.btnContinue.text = strings.continueLabel

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_splash_to_language)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
