package com.forvill.hisaabkhataa.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.databinding.FragmentPartiesBinding

class PartiesFragment : Fragment() {
    private var _binding: FragmentPartiesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPartiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        binding.toolbar.title = strings.partyListTitle

        viewModel.parties.observe(viewLifecycleOwner) { parties ->
            binding.rvParties.layoutManager = LinearLayoutManager(requireContext())
            binding.rvParties.adapter = PartyAdapter(parties.sortedBy { it.name }, strings) { partyId ->
                val bundle = Bundle().apply { putString("partyId", partyId) }
                findNavController().navigate(R.id.action_parties_to_partyDetail, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
