package br.com.letscode.mybank.msboleto.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table
data class Boleto (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val idCliente: UUID,
    val codAgBeneficiario: Int,
    val codContaBeneficiario: String,
    val nossoNumero: String,
    val especie: String,
    val valor: BigDecimal,
    val multaDia: Float,
    val jurosDia: Float,
    val vencimento: LocalDate,
    val pgtoAposVencimento: Boolean



        )