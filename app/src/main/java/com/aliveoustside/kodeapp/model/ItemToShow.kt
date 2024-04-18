package com.aliveoustside.kodeapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val avatarUrl: String,
    val birthday: String,
    val department: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val phone: String,
    val position: String,
    val userTag: String
):Parcelable

sealed class ItemToShow{

    @Parcelize
    data class Employee(val avatarUrl: String,
                        val birthdayOpt: String?,
                        val birthday: String,
                        val department: String,
                        val firstName: String,
                        val id: String,
                        val lastName: String,
                        val userTag: String,
                        val phone: String):Parcelable,ItemToShow()

    data class Divider(val dividerInfo:String):ItemToShow()
}

