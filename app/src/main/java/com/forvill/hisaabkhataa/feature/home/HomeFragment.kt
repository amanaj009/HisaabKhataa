package com.forvill.hisaabkhataa.feature.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.TransactionType
import com.forvill.hisaabkhataa.databinding.FragmentHomeBinding
import com.forvill.hisaabkhataa.databinding.ItemPartyBinding
import kotlin.math.abs

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        viewModel.businessProfile.observe(viewLifecycleOwner) { profile ->
            binding.tvBusinessName.text = profile.businessName
        }

        viewModel.parties.observe(viewLifecycleOwner) { parties ->
            updateSummary(parties, strings)
            setupPartiesList(parties, strings)
        }

        binding.fabAddParty.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_partyEditor)
        }

        binding.btnViewReport.setOnClickListener {
            findNavController().navigate(R.id.reportsFragment)
        }
    }

    private fun updateSummary(parties: List<Party>, strings: AppStrings) {
        val totalGive = parties.filter { it.balance < 0 }.sumOf { abs(it.balance) }
        val totalGet = parties.filter { it.balance > 0 }.sumOf { it.balance }
        val pendingPartiesCount = parties.count { it.balance != 0 }

        val todayMillis = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        val todayCollection = parties.flatMap { it.entries }
            .filter { it.type == TransactionType.Payment && it.createdAtMillis > todayMillis }
            .sumOf { it.amount }

        binding.tvTodayCollection.text = "₹ $todayCollection"
        binding.tvPendingParties.text = pendingPartiesCount.toString()
        binding.tvTotalGive.text = "₹ $totalGive"
        binding.tvTotalGet.text = "₹ $totalGet"
    }

    private fun setupPartiesList(parties: List<Party>, strings: AppStrings) {
        binding.rvParties.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParties.adapter = PartyAdapter(parties.sortedByDescending { abs(it.balance) }, strings) { partyId ->
            val bundle = Bundle().apply { putString("partyId", partyId) }
            findNavController().navigate(R.id.action_home_to_partyDetail, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
