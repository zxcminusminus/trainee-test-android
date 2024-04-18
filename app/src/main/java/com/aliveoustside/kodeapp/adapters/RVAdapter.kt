package com.aliveoustside.kodeapp.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.databinding.ListItemBinding
import com.aliveoustside.kodeapp.databinding.ListItemYearDividerBinding
import com.aliveoustside.kodeapp.model.ItemToShow
import com.aliveoustside.kodeapp.ui.fragments.ListFragment

class RVAdapter(val context: ListFragment) : RecyclerView.Adapter<EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return when(viewType){
            R.layout.list_item->{
                EmployeeViewHolder.ItemViewHolder(
                    context,
                    ListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.list_item_year_divider->{
                EmployeeViewHolder.DividerViewHolder(
                    ListItemYearDividerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            else -> throw IllegalArgumentException("Неизвестный объект")
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<ItemToShow>() {
        override fun areItemsTheSame(oldItem: ItemToShow, newItem: ItemToShow): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: ItemToShow, newItem: ItemToShow): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var onItemClickListener: ((ItemToShow) -> Unit)? = null

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val item = differ.currentList[position]
        when(holder){
            is EmployeeViewHolder.DividerViewHolder -> holder.bind(item as ItemToShow.Divider)
            is EmployeeViewHolder.ItemViewHolder -> {
                holder.bind(item as ItemToShow.Employee,onItemClickListener!!)
                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { it(item) }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnItemClickListener(listener: (ItemToShow) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position]){
            is ItemToShow.Employee -> R.layout.list_item
            is ItemToShow.Divider -> R.layout.list_item_year_divider
        }
    }
}