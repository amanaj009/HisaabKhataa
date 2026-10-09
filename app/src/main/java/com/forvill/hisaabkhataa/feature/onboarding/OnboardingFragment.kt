package com.forvill.hisaabkhataa.feature.onboarding

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.forvill.hisaabkhataa.R
import com.forvill.hisaabkhataa.app.MainViewModel
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.databinding.FragmentOnboardingBinding
import com.forvill.hisaabkhataa.databinding.ItemOnboardingBinding

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strings = viewModel.getStrings()
        val pages = viewModel.onboardingPages

        binding.onboardingTitle.text = strings.onboardingTitle
        binding.btnContinue.text = strings.continueLabel

        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        setupIndicators(pages.size)
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })

        binding.btnContinue.setOnClickListener {
            if (binding.viewPager.currentItem < pages.size - 1) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                findNavController().navigate(R.id.action_onboarding_to_login)
            }
        }
    }

    private fun setupIndicators(size: Int) {
        val indicators = arrayOfNulls<View>(size)
        val layoutParams = LinearLayout.LayoutParams(24, 10)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = View(requireContext())
            indicators[i]?.layoutParams = layoutParams
            indicators[i]?.setBackgroundResource(R.drawable.bg_indicator_inactive)
            binding.indicatorContainer.addView(indicators[i])
        }
        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorContainer.childCount) {
            val view = binding.indicatorContainer.getChildAt(i)
            if (i == position) {
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.width = 64
                view.layoutParams = params
                view.setBackgroundResource(R.drawable.bg_indicator_active)
            } else {
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.width = 24
                view.layoutParams = params
                view.setBackgroundResource(R.drawable.bg_indicator_inactive)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class OnboardingAdapter(private val pages: List<OnboardingPage>) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val page = pages[position]
            holder.binding.pageTitle.text = page.title
            holder.binding.pageTitle.setTextColor(page.accent)
            holder.binding.pageDescription.text = page.description
            holder.binding.accentCard.setCardBackgroundColor(page.accent.copy(alpha = 0.15f))
        }

        override fun getItemCount() = pages.size

        inner class ViewHolder(val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root)
    }
}

// Helper to support Color.copy(alpha) like in Compose
fun Int.copy(alpha: Float): Int {
    val a = (alpha * 255).toInt()
    return Color.argb(a, Color.red(this), Color.green(this), Color.blue(this))
}
