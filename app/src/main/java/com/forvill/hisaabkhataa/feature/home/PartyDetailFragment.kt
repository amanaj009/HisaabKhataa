package com.forvill.hisaabkhataa.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.LedgerEntry
import com.forvill.hisaabkhataa.core.model.TransactionType
import com.forvill.hisaabkhataa.databinding.FragmentPartyDetailBinding
import com.forvill.hisaabkhataa.databinding.ItemLedgerEntryBinding

class PartyDetailFragment : Fragment() {
    private var _binding: FragmentPartyDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var partyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        partyId = arguments?.getString("partyId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPartyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        viewModel.parties.observe(viewLifecycleOwner) { parties ->
            val party = parties.find { it.id == partyId }
            party?.let { updateUI(it, strings) }
        }

        binding.btnEditParty.setOnClickListener {
            val bundle = Bundle().apply { putString("partyId", partyId) }
            findNavController().navigate(R.id.action_partyDetail_to_partyEditor, bundle)
        }

        binding.btnAddUdhaar.setOnClickListener {
            val bundle = Bundle().apply { 
                putString("partyId", partyId)
                putString("type", TransactionType.Udhaar.name)
            }
            findNavController().navigate(R.id.action_partyDetail_to_addEntry, bundle)
        }

        binding.btnAddPayment.setOnClickListener {
            val bundle = Bundle().apply { 
                putString("partyId", partyId)
                putString("type", TransactionType.Payment.name)
            }
            findNavController().navigate(R.id.action_partyDetail_to_addEntry, bundle)
        }
    }

    private fun updateUI(party: Party, strings: AppStrings) {
        binding.toolbar.title = party.name
        binding.tvPartyName.text = party.name
        binding.tvPartyPhone.text = party.phone
        
        if (party.balance >= 0) {
            binding.tvBalanceLabel.text = "Due balance Rs. ${party.balance}"
            binding.tvBalanceLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.clay))
        } else {
            binding.tvBalanceLabel.text = "Advance balance Rs. ${-party.balance}"
            binding.tvBalanceLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.moss))
        }

        binding.tvCreditInfo.text = "Credit limit Rs. ${party.creditLimit} - ${party.tags.joinToString(", ")}"
        
        val totalUdhaar = party.entries.filter { it.type == TransactionType.Udhaar }.sumOf { it.amount }
        val totalPayment = party.entries.filter { it.type == TransactionType.Payment }.sumOf { it.amount }

        binding.tvTotalUdhaar.text = "Rs. $totalUdhaar"
        binding.tvTotalPayment.text = "Rs. $totalPayment"
        binding.tvHistoryTitle.text = strings.ledgerHistory

        binding.rvLedger.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLedger.adapter = LedgerAdapter(party.entries)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class LedgerAdapter(private val entries: List<LedgerEntry>) : RecyclerView.Adapter<LedgerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemLedgerEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = entries[position]
            holder.binding.tvTitle.text = entry.title
            holder.binding.tvNoteTime.text = "${entry.note} - ${entry.time}"
            
            if (!entry.dueDateLabel.isNullOrBlank()) {
                holder.binding.tvDueDate.visibility = View.VISIBLE
                holder.binding.tvDueDate.text = "Due: ${entry.dueDateLabel}"
            } else {
                holder.binding.tvDueDate.visibility = View.GONE
            }

            if (entry.type == TransactionType.Udhaar) {
                holder.binding.tvAmount.text = "+Rs. ${entry.amount}"
                holder.binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.clay))
            } else {
                holder.binding.tvAmount.text = "-Rs. ${entry.amount}"
                holder.binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.moss))
            }
        }

        override fun getItemCount() = entries.size

        inner class ViewHolder(val binding: ItemLedgerEntryBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
