package com.vhenriquez.txwork.screens.companies

import com.vhenriquez.txwork.model.CompanyEntity

data class CompaniesUiState(
    val selectedCompany: CompanyEntity? = null,
    val showDeleteCompanyDialog: Boolean = false,
)