package com.vhenriquez.txwork.screens.certificates

import com.vhenriquez.txwork.model.CertificateEntity

data class CertificatesUiState(
    val selectedCertificate: CertificateEntity? = null,
    val showDeleteCertificateDialog: Boolean = false,
)