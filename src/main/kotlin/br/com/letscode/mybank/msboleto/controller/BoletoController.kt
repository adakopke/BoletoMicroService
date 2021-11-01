package br.com.letscode.mybank.msboleto.controller

import br.com.letscode.mybank.msboleto.dto.BoletoDTO
import br.com.letscode.mybank.msboleto.model.Autorizacoes
import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.service.BoletoService
import br.com.letscode.mybank.msboleto.utils.Autorizacao
import com.auth0.jwt.JWT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("api")
class BoletoController (val boletoService: BoletoService) {

    @PostMapping("pagar")
    suspend fun pgtoBoleto(@RequestBody boletoRequest: BoletoDTO,
                           @RequestHeader (value = "Authorization", required = true) token : String
    ) : ResponseEntity<String> = run {

//        if (getDataExpiracao(token).time < Timestamp.valueOf(LocalDateTime.now()).time) {
//            throw RuntimeException("Token expirado")
//        }

        val tokenResultado: String = extrairUUID(token)

        //TODO Validar esta funcionalidade para saber se a permissão obtida é suficiente
        val permissoes : Autorizacoes = Autorizacao.getPermission(token)

        if (!permissoes.permissions.contains("WRITE")) {
            throw RuntimeException("Perfil não ter permissão para realizar pagamentos")
        }

        val boleto = Boleto (

            idCliente = UUID.fromString(tokenResultado),
            codAgBeneficiario = boletoRequest.codAgBeneficiario,
            codContaBeneficiario = boletoRequest.codContaBeneficiario,
            idContaPagador  = boletoRequest.idContaPagador,
            nossoNumero = boletoRequest.nossoNumero,
            especie = boletoRequest.especie,
            valor = boletoRequest.valor,
            multa = boletoRequest.multa,
            jurosDia = boletoRequest.jurosDia,
            vencimento = boletoRequest.vencimento,
            pgtoAposVencimento = boletoRequest.pgtoAposVencimento,
            registroCriadoEm = LocalDateTime.now())

        boletoService.validarPgto(boleto, token)

    }


    @GetMapping("pagamentos")
    suspend fun consultar (@RequestHeader (value = "Authorization", required = true) token : String) : ResponseEntity<List<Boleto>> = run  {

        //        if (getDataExpiracao(token).time < Timestamp.valueOf(LocalDateTime.now()).time) {
//            throw RuntimeException("Token expirado")
//        }


        val permissoes : Autorizacoes = Autorizacao.getPermission(token)
        if (!permissoes.permissions.contains("READ")) {
            throw RuntimeException("Perfil não ter permissão para realizar consulta de pagamentos efetivados")
        }

        val tokenResultado: String = extrairUUID(token)
        val idCliente: UUID = UUID.fromString(tokenResultado)
        ResponseEntity.ok(boletoService.consultar(idCliente))
    }

    private fun extrairUUID(token: String): String {
        val requestTokenHeader: String = token
        val tokenResultado: String = if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            val jwtToken = requestTokenHeader.substring(7)
            JWT.decode(jwtToken).subject

        } else {
            throw RuntimeException()
        }
        return tokenResultado
    }


    private fun getDataExpiracao(token: String): Date {
        val requestTokenHeader: String = token
        val tokenResultado: Date = if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            val jwtToken = requestTokenHeader.substring(7)
            JWT.decode(jwtToken).expiresAt
        } else {
            throw RuntimeException()
        }
        return tokenResultado
    }


}