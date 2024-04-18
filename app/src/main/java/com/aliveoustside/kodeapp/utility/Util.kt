package com.aliveoustside.kodeapp.utility

import com.aliveoustside.kodeapp.model.Item
import com.aliveoustside.kodeapp.model.ItemToShow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Util {

    enum class Dep(val title: String) {
        ALL("Все"),
        ANDROID("android"),
        IOS("ios"),
        DESIGN("design"),
        MANAGEMENT("management"),
        QA("qa"),
        BACK_OFFICE("back_office"),
        FRONTEND("frontend"),
        HR("hr"),
        PR("pr"),
        BACKEND("backend"),
        SUPPORT("support"),
        ANALYTICS("analytics");

        override fun toString(): String {
            return title
        }
    }

    enum class SortType {
        ALPHABET,
        BIRTHDAY
    }

    enum class OneTimeEvent {
        NOTHING,
        NO_INTERNET,
        REQUEST_ERROR,
        REFRESH_ERROR_API,
        REFRESH_ERROR_INTERNET,
        SECUNDOCHKU
    }

    fun makeAllLists(list: List<Item>): Map<Dep, List<Item>> {
        val listsByDepartments: MutableMap<Dep, List<Item>> = mutableMapOf()
        for (dep in Dep.entries) {
            val depList = list.filter {
                it.department == dep.toString()
            }
            listsByDepartments[dep] = depList
        }
        listsByDepartments[Dep.ALL] = list
        return listsByDepartments.toMap()
    }

    fun sortListsBySortType(
        lists: Map<Dep, List<Item>>,
        sortType: SortType
    ): Map<Dep, List<ItemToShow>> {
        return when (sortType) {
            SortType.ALPHABET -> {
                val sortedLists: MutableMap<Dep, List<ItemToShow.Employee>> = mutableMapOf()
                lists.forEach { (dep, itemlist) ->
                    val sortedItemList = itemlist.sortedBy { it.firstName }
                    sortedLists[dep] = sortedItemList.map {
                        ItemToShow.Employee(
                            avatarUrl = it.avatarUrl,
                            birthdayOpt = null,
                            birthday = it.birthday,
                            department = it.department,
                            firstName = it.firstName,
                            id = it.id,
                            lastName = it.lastName,
                            userTag = it.userTag,
                            phone = it.phone
                        )
                    }
                }
                sortedLists.toMap()
            }

            SortType.BIRTHDAY -> {
                sortListsByBirthday(lists)
            }
        }
    }

    private fun sortListsByBirthday(lists: Map<Dep, List<Item>>): Map<Dep, List<ItemToShow>> {
        val listsSortedByBirthday: MutableMap<Dep, List<ItemToShow>> = mutableMapOf()
        val today = LocalDate.now()

        lists.forEach { (dep, itemlist) ->
            var indexForDivider: Int
            val sortedItemList: MutableList<ItemToShow> = itemlist.sortedBy {
                val bd = LocalDate.parse(it.birthday).dayOfYear
                val srok = if (today.dayOfYear < bd) {
                    bd - today.dayOfYear
                } else {
                    val endOfYear = LocalDate.of(today.year, 12, 31).dayOfYear
                    val daysUntilEndYear = endOfYear - today.dayOfYear
                    daysUntilEndYear + bd
                }
                srok
            }.also {
                indexForDivider = it.indexOfFirst {
                    today.dayOfYear > LocalDate.parse(it.birthday).dayOfYear
                }
            }.map {
                ItemToShow.Employee(
                    avatarUrl = it.avatarUrl,
                    birthdayOpt = it.birthday,
                    birthday = it.birthday,
                    department = it.department,
                    firstName = it.firstName,
                    id = it.id,
                    lastName = it.lastName,
                    userTag = it.userTag,
                    phone = it.phone
                )
            }
                .toMutableList()
            if (indexForDivider >= 0) sortedItemList.add(
                indexForDivider,
                ItemToShow.Divider((LocalDate.now().year + 1).toString())
            )
            listsSortedByBirthday[dep] = sortedItemList
        }
        return listsSortedByBirthday.toMap()
    }

    fun formatDateToDMMM(apiDate:String):String{
        val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
        val date = LocalDate.parse(apiDate)
        return formatter.format(date)
    }
}