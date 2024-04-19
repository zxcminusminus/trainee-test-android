package com.aliveoustside.kodeapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliveoustside.kodeapp.adapters.RVAdapter
import com.aliveoustside.kodeapp.databinding.FragmentListBinding
import com.aliveoustside.kodeapp.model.ItemToShow
import com.aliveoustside.kodeapp.ui.VM
import com.aliveoustside.kodeapp.utility.ListFragmentState
import com.aliveoustside.kodeapp.utility.Util
import kotlinx.coroutines.launch
import java.io.Serializable

class ListFragment : Fragment() {
    private lateinit var adapter:RVAdapter
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var dep:Util.Dep
    val vm: VM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        setupAdapter()
        arguments?.let {
            dep = it.serializable("dep")!!
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    vm.listFragmentState.collect {
                        when (it) {
                            is ListFragmentState.StandardState -> {
                                stopLoadingAnim()
                            }

                            is ListFragmentState.Loading -> {
                                startLoadingAnim()
                            }
                        }
                    }
                }
                launch {
                    vm.allListsToShow.collect {
                        adapter.differ.submitList(it[dep])
                    }
                }
                launch {
                    vm.searchRequest.collect { query ->
                        if (!query.isNullOrEmpty()) {
                            val spisok = vm.allListsToShow.value[dep]
                            spisok?.filter{ item ->
                                //println("zxcc item $item")
                                when (item) {
                                    is ItemToShow.Employee -> {
                                        item.firstName.lowercase().startsWith(query.lowercase()) ||
                                                item.lastName.lowercase().startsWith(query.lowercase()) ||
                                                item.userTag.lowercase().startsWith(query.lowercase())
                                    }
                                    is ItemToShow.Divider -> true
                                }
                            }?.toMutableList()?.also { list ->
                                val ind = list.indexOfFirst { it is ItemToShow.Divider }
                                if (ind == list.size - 1 && ind >= 0) {
                                    list.removeAt(ind)
                                }
                            }?.also { list ->
                                if (list.isEmpty()) {
                                    showCantFindScreen()
                                } else {
                                    hideCantFindScreen()
                                    adapter.differ.submitList(list)
                                }
                            }
                        } else {
                            hideCantFindScreen()
                            adapter.differ.submitList(vm.allListsToShow.value[dep])
                        }
                    }
                }
            }
        }

        adapter.setOnItemClickListener { item ->
            val action =
                MainFragmentDirections.actionMainFragmentToDetailsFragment(item as ItemToShow.Employee)
            view.findNavController().navigate(action)
        }
    }

    fun hideCantFindScreen() {
        binding.apply {
            when (vm.listFragmentState.value) {
                is ListFragmentState.Loading -> {
                    rvList.visibility = View.INVISIBLE
                    shimmerFrameLayout.visibility = View.VISIBLE
                    cantFindBanner.visibility = View.GONE
                }

                is ListFragmentState.StandardState -> {
                    rvList.visibility = View.VISIBLE
                    shimmerFrameLayout.visibility = View.GONE
                    cantFindBanner.visibility = View.GONE
                }
            }
        }
    }

    fun showCantFindScreen() {
        binding.apply {
            rvList.visibility = View.INVISIBLE
            shimmerFrameLayout.visibility = View.GONE
            cantFindBanner.visibility = View.VISIBLE
        }
    }

    fun stopLoadingAnim() {
        binding.apply {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            rvList.visibility = View.VISIBLE
        }
    }

    fun startLoadingAnim() {
        binding.apply {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            rvList.visibility = View.INVISIBLE
        }
    }

    fun setupAdapter() {
        adapter = RVAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(context)
    }
}
//боже храни стаковерплов
inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}