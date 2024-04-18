package com.aliveoustside.kodeapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.aliveoustside.kodeapp.R
import com.aliveoustside.kodeapp.databinding.FragmentDetailsBinding
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args:DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater,container,false)
        Log.d("zxc","details oncreate")
        return binding.root
    }

    //@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.rabotnik.let {
            Glide.with(this)
                .load(it.avatarUrl)
                .placeholder(R.drawable.shimmer_ava)
                .error(R.drawable.goostavo_placeholder)
                .into(binding.ivAvatar)
            binding.tvName.text = it.firstName + " " + it.lastName
            binding.tvBirthDay.text = formatDate(it.birthday)
            binding.tvAge.text= getAge(it.birthday).toString() + " лет"
            binding.tvNameTag.text = it.userTag.lowercase()
            binding.tvDepartment.text = it.department.substring(0,1).uppercase()+it.department.substring(1)
            binding.tvPhoneNumber.text = "+7 " + it.phone
        }
        binding.tvPhoneNumber.setOnClickListener {
            val phoneNum = binding.tvPhoneNumber.text
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: $phoneNum"))
            startActivity(intent)
        }
    }
    private fun getAge(birthday:String):Int{
        val date = LocalDate.parse(birthday)
        return Period.between(
            LocalDate.of(date.year, date.monthValue, date.dayOfMonth),
            LocalDate.now()
        ).years
    }

    fun formatDate(apiDate:String):String{
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))
        val date = LocalDate.parse(apiDate)
        return formatter.format(date)
    }

}