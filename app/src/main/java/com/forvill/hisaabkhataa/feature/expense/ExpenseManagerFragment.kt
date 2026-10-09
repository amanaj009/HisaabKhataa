package com.forvill.hisaabkhataa.feature.expense

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.model.ExpenseType
import com.forvill.hisaabkhataa.core.model.TransactionExpense
import com.forvill.hisaabkhataa.core.sms.SmsTransactionScanner
import com.forvill.hisaabkhataa.databinding.FragmentExpenseManagerBinding
import com.forvill.hisaabkhataa.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseManagerFragment : Fragment() {
    private var _binding: FragmentExpenseManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scanAndRefresh()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpenseManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()

        binding.toolbar.title = strings.expenseManagerTitle

        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            updateSummary(expenses)
            setupTransactionsList(expenses)
        }

        binding.btnScanSms.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                scanAndRefresh()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        }
    }

    private fun scanAndRefresh() {
        // This would usually be in a background thread or ViewModel
        val scanned = scanSmsMessages(requireContext())
        // viewModel.addExpenses(scanned) // This method should exist in ViewModel
        viewModel.refreshExpenses()
    }

    private fun updateSummary(expenses: List<TransactionExpense>) {
        val income = expenses.filter { it.type == ExpenseType.Income }.sumOf { it.amount }
        val expense = expenses.filter { it.type == ExpenseType.Expense }.sumOf { it.amount }
        val balance = income - expense

        binding.tvTotalBalance.text = "₹ $balance"
        binding.tvTotalIncome.text = "₹ $income"
        binding.tvTotalExpense.text = "₹ $expense"
    }

    private fun setupTransactionsList(expenses: List<TransactionExpense>) {
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = TransactionAdapter(expenses.sortedByDescending { it.timestamp })
    }

    private fun scanSmsMessages(context: Context): List<TransactionExpense> {
        val expenses = mutableListOf<TransactionExpense>()
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("body", "date"),
            null,
            null,
            "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")
            var count = 0
            while (it.moveToNext() && count < 200) {
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)
                val expense = SmsTransactionScanner.scanMessage(body, date)
                if (expense != null) {
                    expenses.add(expense)
                }
                count++
            }
        }
        return expenses
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class TransactionAdapter(private val transactions: List<TransactionExpense>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
        private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tx = transactions[position]
            holder.binding.tvMerchant.text = tx.merchant ?: tx.note
            holder.binding.tvCategoryDate.text = "${tx.categoryId} • ${dateFormat.format(Date(tx.timestamp))}"
            
            if (tx.type == ExpenseType.Income) {
                holder.binding.tvCategoryIcon.text = "💰"
                holder.binding.tvAmount.text = "₹ ${tx.amount}"
                holder.binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green))
                holder.binding.iconContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.finance_blue_light))
            } else {
                holder.binding.tvCategoryIcon.text = "💸"
                holder.binding.tvAmount.text = "- ₹ ${tx.amount}"
                holder.binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_red))
                holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#1AE91E63"))
            }
        }

        override fun getItemCount() = transactions.size

        inner class ViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
