package com.forvill.hisaabkhataa.feature.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentOtpBinding

class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        binding.otpTitle.text = strings.otpTitle
        binding.otpSubtitle.text = strings.otpSubtitle
        binding.btnVerify.text = strings.verifyContinue.uppercase()

        setupOtpInputs()

        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.action_otp_to_setup)
        }
    }

    private fun setupOtpInputs() {
        val edits = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        edits.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < edits.size - 1) {
                        edits[index + 1].requestFocus()
                    }
                    checkVerifyButton()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun checkVerifyButton() {
        val code = binding.etOtp1.text.toString() + binding.etOtp2.text.toString() + 
                   binding.etOtp3.text.toString() + binding.etOtp4.text.toString()
        binding.btnVerify.isEnabled = code.length == 4
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
