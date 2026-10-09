package com.forvill.hisaabkhataa.feature.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.databinding.ItemPartyBinding
import kotlin.math.abs

class PartyAdapter(
    private val parties: List<Party>,
    private val strings: AppStrings,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPartyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val party = parties[position]
        holder.binding.tvPartyName.text = party.name
        holder.binding.tvInitials.text = party.name.take(1).uppercase()
        holder.binding.tvLastActivity.text = party.lastActivity
        holder.binding.tvBalance.text = "₹ ${abs(party.balance)}"

        val isGet = party.balance > 0
        val isGive = party.balance < 0

        when {
            isGet -> {
                holder.binding.tvBalance.setTextColor(Color.parseColor("#E91E63"))
                holder.binding.amountContainer.setBackgroundColor(Color.parseColor("#0DE91E63"))
                holder.binding.tvRemindLabel.visibility = View.VISIBLE
                holder.binding.tvRemindLabel.text = strings.remind.replaceFirstChar { it.uppercase() } + " >"
            }
            isGive -> {
                holder.binding.tvBalance.setTextColor(Color.parseColor("#4CAF50"))
                holder.binding.amountContainer.setBackgroundColor(Color.parseColor("#0D4CAF50"))
                holder.binding.tvRemindLabel.visibility = View.GONE
            }
            else -> {
                holder.binding.tvBalance.setTextColor(Color.BLACK)
                holder.binding.amountContainer.setBackgroundColor(Color.TRANSPARENT)
                holder.binding.tvRemindLabel.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener { onClick(party.id) }
    }

    override fun getItemCount() = parties.size

    inner class ViewHolder(val binding: ItemPartyBinding) : RecyclerView.ViewHolder(binding.root)
}
