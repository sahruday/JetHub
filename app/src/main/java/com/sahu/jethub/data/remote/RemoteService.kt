package com.sahu.jethub.data.remote

import com.sahu.jethub.data.remote.api.GitHubApi
import com.sahu.jethub.dataHolders.ItemDetails
import com.sahu.jethub.dataHolders.ItemsInfoDetails
import com.sahu.jethub.dataHolders.PRItemDetails
import com.sahu.jethub.dataHolders.toItemsInfoDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteService @Inject constructor(
    private val api: GitHubApi,
) {
    companion object {
        const val COUNT_PER_PAGE = 100
    }

    suspend fun getPublicQueryData(query: String, currentListSize: Int = 0): Flow<ItemsInfoDetails> = flow {
        val response = api.queryFilter(query.toEncodedQuery(), (currentListSize / COUNT_PER_PAGE) + 1, COUNT_PER_PAGE)
        if (response.isSuccessful) {
            emit(response.body()!!.toItemsInfoDetails())
        } else {
            emit(ItemsInfoDetails(dummyValues(), dummyValues().size))
        }
    }

    private fun dummyValues(): List<ItemDetails> = arrayListOf<ItemDetails>().apply {
        repeat(20) {
            add(PRItemDetails(it.toLong(), 1, "Title$it", createdAt = "", state = if (it % 2 == 0) "open" else "closed",
                isDraft = Random.nextBoolean()
            ))
        }
    }

    private fun String.toEncodedQuery(): String =
        this.split(" ")
            .filter { it.isNotBlank() }
            .joinToString("+")
}