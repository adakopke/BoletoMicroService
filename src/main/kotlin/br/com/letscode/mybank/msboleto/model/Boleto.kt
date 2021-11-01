package br.com.letscode.mybank.msboleto.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table
data class Boleto (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val idCliente: UUID,
    val codAgBeneficiario: Int,
    val codContaBeneficiario: String,
    val idContaPagador: String,
    val nossoNumero: String,
    val especie: String,
    var valor: BigDecimal,
    val multa: Double,
    val jurosDia: Double,
    val vencimento: LocalDate,
    val pgtoAposVencimento: Boolean,
    val registroCriadoEm: LocalDateTime,
    var pgtoStatus : String = "",
    var idRegistroOperacao : String = ""



        )