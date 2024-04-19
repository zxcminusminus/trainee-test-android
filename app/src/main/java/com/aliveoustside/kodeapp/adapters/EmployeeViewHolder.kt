package com.aliveoustside.kodeapp.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.databinding.ListItemBinding
import com.aliveoustside.kodeapp.databinding.ListItemYearDividerBinding
import com.aliveoustside.kodeapp.model.ItemToShow
import com.aliveoustside.kodeapp.ui.fragments.ListFragment
import com.aliveoustside.kodeapp.utility.Util
import com.bumptech.glide.Glide

sealed class EmployeeViewHolder( binding: ViewBinding):RecyclerView.ViewHolder(binding.root) {

    class ItemViewHolder(val context: ListFragment, private val binding: ListItemBinding): EmployeeViewHolder(binding){

        fun bind(employee:ItemToShow.Employee,listener: (ItemToShow) -> Unit){
            Glide.with(context)
                .load(employee.avatarUrl)
                .placeholder(R.drawable.shimmer_ava)
                .error(R.drawable.goostavo_placeholder)
                .into(binding.ivAvatar)
            binding.tvName.text = employee.firstName + " " + employee.lastName
            binding.tvNameTag.text = employee.userTag.lowercase()
            binding.tvDepartment.text = employee.department
            binding.tvBDOpt.text = employee.birthdayOpt?.let { Util.formatDateToDMMM(it) }
        }
    }

    class DividerViewHolder(private val binding: ListItemYearDividerBinding) : EmployeeViewHolder(binding) {
        fun bind(info: ItemToShow.Divider){
            binding.tvYear.text = info.dividerInfo
        }
    }
}