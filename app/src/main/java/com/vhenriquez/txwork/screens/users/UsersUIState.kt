package com.vhenriquez.txwork.screens.users

import com.vhenriquez.txwork.model.UserEntity

data class UsersUiState(
    val selectedUser: UserEntity? = null,
    val showDeleteUserDialog: Boolean = false,
)