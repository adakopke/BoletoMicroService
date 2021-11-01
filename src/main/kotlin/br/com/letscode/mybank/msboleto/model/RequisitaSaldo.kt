package br.com.letscode.mybank.msboleto.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

data class RequisitaSaldo (

    @JsonProperty("accountId")
    val idContaPagador: String,
    @JsonProperty("money")
    var valor: BigDecimal,
    )