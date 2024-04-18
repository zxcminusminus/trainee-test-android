package com.aliveoustside.kodeapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.databinding.FragmentErrorBinding
import com.aliveoustside.kodeapp.databinding.FragmentListBinding
import com.aliveoustside.kodeapp.ui.VM

class ErrorFragment : Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!
    val vm:VM by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErrorBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTryAgain.setOnClickListener {
            vm.getEmployees()
            view.findNavController().navigate(R.id.action_errorFragment_to_mainFragment)
        }
    }
}