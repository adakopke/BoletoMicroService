package br.com.letscode.mybank.msboleto.controller

import br.com.letscode.mybank.msboleto.dto.BoletoDTO
import br.com.letscode.mybank.msboleto.exception.Exceptions
import br.com.letscode.mybank.msboleto.model.RespostaAutorizacoes
import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.model.RequisitaAutorizacoes
import br.com.letscode.mybank.msboleto.service.BoletoService
import br.com.letscode.mybank.msboleto.utils.Autorizacao
import com.auth0.jwt.JWT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("api")
class BoletoController (val boletoService: BoletoService) {

    @PostMapping("pagar")
    suspend fun pgtoBoleto(@RequestBody boletoRequest: BoletoDTO,
                           @RequestHeader (value = "Authorization", required = true) token : String
    ) : ResponseEntity<String> = run {

        validarToken(token)
        validarPermissoes(token, "WRITE")

        val boleto = Boleto (

            idCliente = UUID.fromString(extrairUUID(token)),
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
            registroCriadoEm = LocalDateTime.now()
        )

        boletoService.validarPgto(boleto, token)
    }

    @GetMapping("pagamentos")
    suspend fun consultar (@RequestHeader (value = "Authorization", required = true) token : String) : ResponseEntity<List<Boleto>> = run  {

        validarToken(token)
        validarPermissoes(token, "READ")
        val idCliente: UUID = UUID.fromString(extrairUUID(token))
        ResponseEntity.ok(boletoService.consultar(idCliente))
    }

    private fun extrairUUID(token: String) = JWT.decode(token.substring(7)).subject

    private suspend fun validarPermissoes(token: String, permissao: String) {
        val requisitaAutorizacoes = RequisitaAutorizacoes (
            clienteId = extrairUUID(token),
            expiresIn = JWT.decode(token.substring(7)).expiresAt.time,
            tipoOperacao = "Boleto")
        if (!Autorizacao.getPermission(requisitaAutorizacoes).permissions.contains(permissao)) {

            throw Exceptions.AcessoNegadoException("Perfil não ter permissão para realizar a operação")
        }

    }

    private fun validarToken(token: String): Unit {
     if (token.startsWith("Bearer ")) {
            val jwtToken = token.substring(7)
            if(JWT.decode(jwtToken).expiresAt.time < Timestamp.valueOf(LocalDateTime.now()).time) throw Exceptions.TokenExpiradoException("Token expirado")
         } else { throw Exceptions.TokenInvalidoException("Token inválido") }
        }

}