package br.com.letscode.mybank.msboleto.dto

import java.math.BigDecimal
import java.time.LocalDate

data class BoletoRequest (

    val codAgBeneficiario: Int,
    val codContaBeneficiario: String,
    val codAgPagador: Int,
    val codContaPagador: String,
    val nossoNumero: String,
    val especie: String,
    val valor: BigDecimal,
    val multa: Double,
    val jurosDia: Double,
    val vencimento: LocalDate,
    val pgtoAposVencimento: Boolean

        )
