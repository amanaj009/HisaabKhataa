package com.forvill.hisaabkhataa.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.databinding.FragmentRemindersBinding
import com.forvill.hisaabkhataa.databinding.ItemReminderBinding

class RemindersFragment : Fragment() {
    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        binding.toolbar.title = strings.remindersTitle

        // In a real app, you'd have a LiveData for reminders in ViewModel
        val reminders = listOf<ReminderItem>() // Placeholder
        
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = ReminderAdapter(reminders)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ReminderAdapter(private val reminders: List<ReminderItem>) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = reminders[position]
            holder.binding.tvPartyName.text = item.partyName
            holder.binding.tvDueInfo.text = "${item.dueLabel} - ${item.channel}"
            holder.binding.tvDueAmount.text = "Rs. ${item.dueAmount}"
        }

        override fun getItemCount() = reminders.size

        inner class ViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
