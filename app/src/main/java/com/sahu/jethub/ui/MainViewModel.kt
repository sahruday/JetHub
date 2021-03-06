package com.sahu.jethub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahu.jethub.data.remote.RemoteService
import com.sahu.jethub.dataHolders.ItemDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteService: RemoteService,
) : ViewModel() {

    companion object{
        const val DEFAULT_REPO = "PhilJay/MPAndroidChart"//"sahruday/JetHub"
        const val DEFAULT_QUERY = "is:open is:pr"
    }
    //Repo
    var repo = MutableStateFlow(DEFAULT_REPO)

    //Filter Query
    var query = MutableStateFlow(DEFAULT_QUERY)

    //Display items
    private val _data: MutableStateFlow<List<ItemDetails>> = MutableStateFlow(emptyList())
    val data = _data.asStateFlow()

    //Load more check
    private val _hasLoadMore: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val hasLoadMore = _hasLoadMore.asStateFlow()

    fun fetchQueryData() =
        viewModelScope.launch {
            resetData()
            remoteService.getPublicQueryData(query = "repo:${repo.value} ${query.value}", 0).collect {
                _data.value = it.items
                _hasLoadMore.value = _data.value.size < it.totalCount
            }
        }

    fun resetData(isLoading: Boolean = true) {
        isNotLoading(isLoading.not())
        _data.value = emptyList()
    }

    fun isNotLoading(isNotLoading: Boolean = true) {
        _hasLoadMore.value = isNotLoading.not()
    }

    fun resetQueries() {
        repo.value = DEFAULT_REPO
        query.value = DEFAULT_QUERY
    }

    fun loadMoreQueryData() =
        viewModelScope.launch {
            remoteService.getPublicQueryData(query = "repo:${repo.value} ${query.value}", _data.value.size).collect {
                _data.value = arrayListOf<ItemDetails>().apply {
                    addAll(_data.value)
                    addAll(it.items)
                }
                _hasLoadMore.value = _data.value.size < it.totalCount
            }
        }

}