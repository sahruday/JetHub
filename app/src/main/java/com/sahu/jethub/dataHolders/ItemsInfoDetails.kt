package com.sahu.jethub.dataHolders

data class ItemsInfoDetails(
    val items: List<ItemDetails>,
    /**
     * Total count of items for query, Not list size.
     */
    val totalCount: Int = 0,
) {
    /**
     * True if total count and items size were not same.
     */
    var hasLoadMore: Boolean = items.size != totalCount && items.size < 100
        private set
}
