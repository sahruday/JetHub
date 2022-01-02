package com.sahu.jethub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahu.jethub.data.remote.RemoteService
import com.sahu.jethub.dataHolders.ItemDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteService: RemoteService,
) : ViewModel() {

    //Repo
    var owner: StateFlow<String> = MutableStateFlow("PhilJay")//"sahruday")
    var repo: StateFlow<String> = MutableStateFlow("MPAndroidChart")//"JetHub")

    //Filter Query
    var query: StateFlow<String> = MutableStateFlow("is:closed is:pr")

    //Display items
    private val _data: MutableStateFlow<List<ItemDetails>> = MutableStateFlow(emptyList())
    val data = _data.asStateFlow()

    //Load more check
    private val _hasLoadMore: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val hasLoadMore = _hasLoadMore.asStateFlow()

    fun fetchQueryData() =
        viewModelScope.launch {
            remoteService.getPublicQueryData(query = "repo:${owner.value}/${repo.value} ${query.value}", 0).collect {
                _data.value = it.items
                _hasLoadMore.value = it.totalCount != _data.value.size
            }
        }

    fun loadMoreQueryData() =
        viewModelScope.launch {
            remoteService.getPublicQueryData(query = "repo:${owner.value}/${repo.value} ${query.value}", _data.value.size).collect {
                _data.value = arrayListOf<ItemDetails>().apply {
                    addAll(_data.value)
                    addAll(it.items)
                }
                _hasLoadMore.value = _data.value.size < it.totalCount
            }
        }

}