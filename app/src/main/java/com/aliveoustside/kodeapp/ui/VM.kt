package com.aliveoustside.kodeapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aliveoustside.kodeapp.API.RetrofitInstance
import com.aliveoustside.kodeapp.model.ItemToShow
import com.aliveoustside.kodeapp.model.ServerResponse
import com.aliveoustside.kodeapp.utility.ListFragmentState
import com.aliveoustside.kodeapp.utility.Util
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import retrofit2.Response

class VM(app: Application) : AndroidViewModel(app) {

    val allListsToShow: MutableStateFlow<Map<Util.Dep, List<ItemToShow>>> =
        MutableStateFlow(mutableMapOf())
    val listFragmentState: MutableStateFlow<ListFragmentState<ServerResponse>> =
        MutableStateFlow(ListFragmentState.StandardState())
    val sortType: MutableStateFlow<Util.SortType> = MutableStateFlow(Util.SortType.ALPHABET)
    val oneTimeEvent: MutableSharedFlow<Util.OneTimeEvent> = MutableSharedFlow()
    private var allListsByALPHABET: Map<Util.Dep, List<ItemToShow>> = mutableMapOf()
    private var allListsByBIRTHDAY: Map<Util.Dep, List<ItemToShow>> = mutableMapOf()
    val searchRequest:MutableStateFlow<String?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            if (hasInternet()) {
                getEmployees()
            } else {
                oneTimeEvent.emit(Util.OneTimeEvent.NO_INTERNET)
                isInternetAvailable()
                    .filter { it }
                    .collect {
                        listFragmentState.value = ListFragmentState.Loading()
                        getEmployees()
                    }
            }
            sortType.collect {
                when (it) {
                    Util.SortType.ALPHABET -> allListsToShow.value = allListsByALPHABET
                    Util.SortType.BIRTHDAY -> allListsToShow.value = allListsByBIRTHDAY
                }
            }
        }
    }

    fun getEmployees() = viewModelScope.launch {
        if (hasInternet()) {
            oneTimeEvent.emit(Util.OneTimeEvent.NOTHING)
            listFragmentState.value = ListFragmentState.Loading()
            val response = RetrofitInstance.api.getEmployees()
            listFragmentState.value = handleResponse(response)
        } else {
            listFragmentState.value = ListFragmentState.StandardState()
            oneTimeEvent.emit(Util.OneTimeEvent.NO_INTERNET)
        }
    }

    private suspend fun handleResponse(response: Response<ServerResponse>): ListFragmentState<ServerResponse> {
        if (!response.isSuccessful) {
            oneTimeEvent.emit(Util.OneTimeEvent.REQUEST_ERROR)
        }
        response.body()?.let { resultResponse ->
            allListsByALPHABET = Util.sortListsBySortType(
                Util.makeAllLists(resultResponse.items),
                Util.SortType.ALPHABET
            )
            allListsByBIRTHDAY = Util.sortListsBySortType(
                Util.makeAllLists(resultResponse.items),
                Util.SortType.BIRTHDAY
            )
            when (sortType.value) {
                Util.SortType.ALPHABET -> {
                    allListsToShow.value = allListsByALPHABET
                }

                Util.SortType.BIRTHDAY -> {
                    allListsToShow.value = allListsByBIRTHDAY
                }
            }
        }
        return ListFragmentState.StandardState()
    }

    fun refresh() = viewModelScope.launch {
        if (hasInternet()) {
            val response: Response<ServerResponse> = if ((1..20).random() < 10) {
                RetrofitInstance.api.getEmployeesError()
            } else {
                RetrofitInstance.api.getEmployees()
            }
            oneTimeEvent.emit(handleRefreshResponse(response))
        } else {
            oneTimeEvent.emit(Util.OneTimeEvent.REFRESH_ERROR_INTERNET)
        }
    }

    private suspend fun handleRefreshResponse(response: Response<ServerResponse>): Util.OneTimeEvent {
        if (response.isSuccessful) {
            oneTimeEvent.emit(Util.OneTimeEvent.SECUNDOCHKU)
            response.body()?.let { resultResponse ->
                allListsByALPHABET = Util.sortListsBySortType(
                    Util.makeAllLists(resultResponse.items),
                    Util.SortType.ALPHABET
                )
                allListsByBIRTHDAY = Util.sortListsBySortType(
                    Util.makeAllLists(resultResponse.items),
                    Util.SortType.BIRTHDAY
                )
                when (sortType.value) {
                    Util.SortType.ALPHABET -> {
                        allListsToShow.value = allListsByALPHABET
                    }

                    Util.SortType.BIRTHDAY -> {
                        allListsToShow.value = allListsByBIRTHDAY
                    }
                }
            }
            return Util.OneTimeEvent.NOTHING
        }
        return Util.OneTimeEvent.REFRESH_ERROR_API
    }

    fun hasInternet(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun isInternetAvailable() = callbackFlow {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(true) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(false) }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                launch { send(false) }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}