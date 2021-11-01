package br.com.letscode.mybank.msboleto.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class RespostaSaldo (

    val id : String,
    val accountId : String,
    val money : BigDecimal,
    val operationType : String,
    val operationStatus : String,
    val date : LocalDateTime
        )