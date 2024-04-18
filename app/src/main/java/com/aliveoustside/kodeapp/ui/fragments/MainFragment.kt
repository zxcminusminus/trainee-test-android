package com.aliveoustside.kodeapp.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.adapters.ViewPagerAdapter
import com.aliveoustside.kodeapp.databinding.FragmentMainBinding
import com.aliveoustside.kodeapp.ui.VM
import com.aliveoustside.kodeapp.utility.Util
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class MainFragment : Fragment() {
    private var btnCancelWidth by Delegates.notNull<Int>()
    private var llSearchRightMargin by Delegates.notNull<Int>()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val vm: VM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var focused = false
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                launch{
                    vm.oneTimeEvent.collect {
                        when (it) {
                            Util.OneTimeEvent.NOTHING -> {
                                binding.swiper.isRefreshing = false
                            }
                            Util.OneTimeEvent.NO_INTERNET -> {
                                val snek = Snackbar.make(binding.root,"Нет интернета", Snackbar.LENGTH_LONG)
                                snek.setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.red_snek))
                                snek.show()
                            }
                            Util.OneTimeEvent.REQUEST_ERROR -> {
                                view.findNavController().navigate(R.id.action_mainFragment_to_errorFragment)
                            }
                            Util.OneTimeEvent.REFRESH_ERROR_API -> {
                                val snek = Snackbar.make(binding.root,"Не могу обновить данные. Что-то пошло не так",
                                    Snackbar.LENGTH_LONG)
                                snek.setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.red_snek))
                                snek.show()
                                binding.swiper.isRefreshing = false
                            }
                            Util.OneTimeEvent.REFRESH_ERROR_INTERNET -> {
                                val snek = Snackbar.make(binding.root,"Не могу обновить данные. Проверь соединение с интернетом.",
                                    Snackbar.LENGTH_LONG)
                                snek.setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.red_snek))
                                snek.show()
                                binding.swiper.isRefreshing = false
                            }
                            Util.OneTimeEvent.SECUNDOCHKU -> {
                                val snek = Snackbar.make(binding.root,"Секундочку, гружусь....",
                                    Snackbar.LENGTH_LONG)
                                snek.setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.blu))
                                snek.show()
                            }
                        }
                    }
                }
                launch {
                    vm.sortType.collect{
                        when(it){
                            Util.SortType.BIRTHDAY ->{
                                binding.btnSort.setImageResource(R.drawable.sort_btn_active)
                            }
                            Util.SortType.ALPHABET ->{
                                binding.btnSort.setImageResource(R.drawable.sort_btn_inactive)
                            }
                        }
                    }
                }
            }
        }

        binding.pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Все"
                1 -> tab.text = "Дизайн"
                2 -> tab.text = "Аналитика"
                3 -> tab.text = "Менеджмент"
                4 -> tab.text = "Бэк-офис"
                5 -> tab.text = "Backend"
                6 -> tab.text = "PR"
                7 -> tab.text = "HR"
                8 -> tab.text = "Frontend"
                9 -> tab.text = "Техподдержка"
                10 -> tab.text = "QA"
                11 -> tab.text = "Android"
                12 -> tab.text = "iOS"
            }
        }.attach()

        binding.edSearch.post {
            btnCancelWidth = binding.btnCancel.width
            llSearchRightMargin = binding.llSearchWrapper.marginRight
        }

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()){
                    vm.searchRequest.value = null
                }
                else{
                    vm.searchRequest.value = s.toString()
                }
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.btnCross.visibility = View.GONE
                } else {
                    binding.btnCross.visibility = View.VISIBLE
                }
            }
        })
        binding.edSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                animateMarginRight(binding.llSearchWrapper, btnCancelWidth, 300, focused)
                binding.edSearch.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.loopa_active,
                    0,
                    0,
                    0
                )
                focused = !focused
            }
            if (!hasFocus) {
                animateMarginRight(binding.llSearchWrapper, llSearchRightMargin, 300, focused)
                binding.edSearch.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.loopa_inactive,
                    0,
                    0,
                    0
                )
                focused = !focused
            }
        }
        binding.btnCancel.setOnClickListener {
            binding.edSearch.apply {
                clearFocus()
                hideKeyboard()
                text.clear()
            }
        }
        binding.btnCross.setOnClickListener {
            binding.edSearch.text.clear()
        }
        binding.btnSort.setOnClickListener {
            val modalBottomSheet = BottomSheetFragment()
            modalBottomSheet.show(parentFragmentManager, BottomSheetFragment.TAG)
        }
        binding.swiper.setOnRefreshListener {
            vm.refresh()
        }

    }
    private fun animateMarginRight(view: ViewGroup, targetMarginRight: Int, duration: Long,hasFocus:Boolean) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        val animatron = ValueAnimator.ofInt(params.rightMargin,targetMarginRight).apply{
            addUpdateListener { valueAnimatron ->
                val value = valueAnimatron.animatedValue as Int
                params.rightMargin = value
                binding.llSearchWrapper.layoutParams = params
            }
            addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator) {
                    binding.btnSort.visibility = View.GONE
                    binding.btnCross.visibility = View.GONE
                }
                override fun onAnimationEnd(animation: Animator) {
                    binding.btnSort.visibility = showOrNot(hasFocus)
                }
            })
        }
        animatron.duration = duration
        animatron.start()
    }
    fun showOrNot(hasFocus:Boolean):Int{
        return if(hasFocus) View.VISIBLE
        else View.GONE
    }

    override fun onStop() {
        super.onStop()
        binding.swiper.isRefreshing = false
    }
}
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
