package com.forvill.hisaabkhataa.feature.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentReportsBinding

class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        binding.toolbar.title = strings.reportsTitle

        viewModel.parties.observe(viewLifecycleOwner) { parties ->
            val totalGet = parties.filter { it.balance > 0 }.sumOf { it.balance }
            binding.tvTotalReceivables.text = "Rs. $totalGet"
        }

        binding.btnExport.setOnClickListener {
            // Export logic
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
