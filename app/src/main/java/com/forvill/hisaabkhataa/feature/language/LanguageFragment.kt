package com.forvill.hisaabkhataa.feature.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {
    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.selectedLanguage.observe(viewLifecycleOwner) { language ->
            updateUI(language)
        }

        binding.cardEnglish.setOnClickListener {
            viewModel.updateSelectedLanguage("English")
        }

        binding.cardHindi.setOnClickListener {
            viewModel.updateSelectedLanguage("Hindi")
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_language_to_onboarding)
        }
    }

    private fun updateUI(language: String) {
        val strings = viewModel.getStrings()
        binding.title.text = strings.languageTitle
        binding.subtitle.text = strings.languageSubtitle
        binding.btnContinue.text = strings.continueLabel

        val primaryColor = ContextCompat.getColor(requireContext(), R.color.clay)
        val mossColor = ContextCompat.getColor(requireContext(), R.color.moss)
        
        if (language == "English") {
            binding.cardEnglish.strokeWidth = 4
            binding.cardEnglish.setStrokeColor(android.content.res.ColorStateList.valueOf(primaryColor))
            binding.cardHindi.strokeWidth = 1
            binding.cardHindi.setStrokeColor(android.content.res.ColorStateList.valueOf(mossColor))
        } else {
            binding.cardHindi.strokeWidth = 4
            binding.cardHindi.setStrokeColor(android.content.res.ColorStateList.valueOf(primaryColor))
            binding.cardEnglish.strokeWidth = 1
            binding.cardEnglish.setStrokeColor(android.content.res.ColorStateList.valueOf(mossColor))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
