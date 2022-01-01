package com.sahu.jethub.dataHolders

import com.google.gson.annotations.SerializedName

sealed class ItemDetails(
    open val id: Long,
    open val number: Int,
    open val title: String,
    open val user: UserDetails? = null,
    open val createdAt: String,
    open val state: String,
    open val closedAt: String? = null,
    open val updatedAt: String? = null,
) {
    data class UserDetails(
        val id: Long,
        @SerializedName("login")
        val name: String,
        @SerializedName("avatar_url")
        val avatarUrl: String)
}

class PRItemDetails(
    override val id: Long,
    override val number: Int,
    override val title: String,
    override val user: UserDetails? = null,
    override val createdAt: String,
    override val state: String,
    override val closedAt: String? = null,
    override val updatedAt: String? = null,
    val mergedAt: String? = null,
    val isDraft: Boolean,
) : ItemDetails(id, number, title, user, createdAt, state, closedAt, updatedAt)



class IssueItemDetails(
    override val id: Long,
    override val number: Int,
    override val title: String,
    override val user: UserDetails? = null,
    override val createdAt: String,
    override val state: String,
    override val closedAt: String? = null,
    override val updatedAt: String? = null,
) : ItemDetails(id, number, title, user, createdAt, state, closedAt, updatedAt)
