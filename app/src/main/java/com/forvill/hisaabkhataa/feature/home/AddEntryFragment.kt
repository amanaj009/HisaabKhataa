package com.forvill.hisaabkhataa.feature.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.model.TransactionType
import com.forvill.hisaabkhataa.databinding.FragmentAddEntryBinding

class AddEntryFragment : Fragment() {
    private var _binding: FragmentAddEntryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var partyId: String? = null
    private var initialType: TransactionType = TransactionType.Udhaar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        partyId = arguments?.getString("partyId")
        initialType = TransactionType.valueOf(arguments?.getString("type") ?: TransactionType.Udhaar.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        
        val party = viewModel.parties.value?.find { it.id == partyId }
        party?.let {
            binding.addEntrySubtitle.text = "${strings.addEntrySubtitlePrefix} ${it.name}."
            binding.tvPreviewParty.text = "Party: ${it.name}"
        }

        binding.addEntryTitle.text = strings.addEntryTitle
        binding.btnUdhaar.text = strings.addUdhaar
        binding.btnPayment.text = strings.addPayment
        binding.btnSave.text = strings.addEntrySave

        if (initialType == TransactionType.Payment) {
            binding.toggleGroup.check(R.id.btn_payment)
            binding.tvPreviewType.text = "Type: ${strings.addPayment}"
        } else {
            binding.toggleGroup.check(R.id.btn_udhaar)
            binding.tvPreviewType.text = "Type: ${strings.addUdhaar}"
        }

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btn_payment) {
                    binding.tvPreviewType.text = "Type: ${strings.addPayment}"
                } else {
                    binding.tvPreviewType.text = "Type: ${strings.addUdhaar}"
                }
            }
        }

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvPreviewAmount.text = "Amount: Rs. ${s ?: "0"}"
                checkSaveButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkSaveButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSave.setOnClickListener {
            // Save logic...
            viewModel.refreshParties()
            findNavController().popBackStack()
        }
        
        checkSaveButton()
    }

    private fun checkSaveButton() {
        val amount = binding.etAmount.text.toString()
        val note = binding.etNote.text.toString()
        binding.btnSave.isEnabled = amount.isNotBlank() && note.isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
