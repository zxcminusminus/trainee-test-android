package com.aliveoustside.kodeapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.databinding.BottomSheetBinding
import com.aliveoustside.kodeapp.ui.VM
import com.aliveoustside.kodeapp.utility.Util
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    val vm: VM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBinding.inflate(inflater,container,false)
        binding.radioGroup.check(when (vm.sortType.value) {
            Util.SortType.ALPHABET -> R.id.rbtnAlfavit
            Util.SortType.BIRTHDAY -> R.id.rbtnBirthday
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.rbtnAlfavit ->{
                    vm.sortType.value = Util.SortType.ALPHABET
                    dismiss()
                }
                R.id.rbtnBirthday->{
                    vm.sortType.value = Util.SortType.BIRTHDAY
                    dismiss()
                }
            }
        }
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}