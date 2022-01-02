package com.sahu.jethub.data.remote

import com.google.gson.annotations.SerializedName
import com.sahu.jethub.dataHolders.IssueItemDetails
import com.sahu.jethub.dataHolders.ItemDetails
import com.sahu.jethub.dataHolders.ItemDetails.UserDetails
import com.sahu.jethub.dataHolders.ItemsInfoDetails
import com.sahu.jethub.dataHolders.PRItemDetails

data class RemoteDataObject(
    @SerializedName("total_count")
    val totalCount: Int,
    val items: List<RemoteItemDetails>
)

open class RemoteItemDetails(
    open val id: Long,
    open val number: Int,
    open val title: String,
    open val user: UserDetails? = null,
    @SerializedName("created_at")
    open val createdAt: String,
    open val state: String,
    @SerializedName("closed_at")
    open val closedAt: String? = null,
    @SerializedName("updated_at")
    open val updatedAt: String? = null,
    @SerializedName("pull_request")
    val pr: PR? = null,
    @SerializedName("draft")
    val isDraft: Boolean? = null,
) {
    data class PR(
        @SerializedName("merged_at")
        val mergedAt: String? = null

    )
}

fun RemoteDataObject.toItemsInfoDetails() : ItemsInfoDetails =
    ItemsInfoDetails(items.map { it.toItemDetail() },this.totalCount)

private fun RemoteItemDetails.toItemDetail(): ItemDetails {
    return if(this.isDraft == null)
        IssueItemDetails(id, number, title, user, createdAt, state, closedAt, updatedAt)
    else
        PRItemDetails(id, number, title, user, createdAt, state, closedAt, updatedAt, pr?.mergedAt, isDraft)
}
