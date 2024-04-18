package com.aliveoustside.kodeapp.utility

sealed class ListFragmentState<T>() {
    class StandardState<T>():ListFragmentState<T>()
    class Loading<T> : ListFragmentState<T>()
}