package com.example.horseinacoat.presentation.viewModel.custom

data class PaginationState(
    val currentPage: Int = 0,
    val pageSize: Int = 20,
    val isLastPage: Boolean = false,
    val totalItems: Int = 0,
    val isLoading: Boolean = false
) {
    val canLoadMore: Boolean
        get() = !isLastPage && !isLoading
}