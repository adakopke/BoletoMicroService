package br.com.letscode.mybank.msboleto.controller

import br.com.letscode.mybank.msboleto.dto.BoletoDTO
import br.com.letscode.mybank.msboleto.model.Autorizacoes
import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.service.BoletoService
import br.com.letscode.mybank.msboleto.utils.Autorizacao
import com.auth0.jwt.JWT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*


@RestController
@RequestMapping("api")
class BoletoController (val boletoService: BoletoService) {

    @PostMapping("pagar")
    suspend fun pgtoBoleto(@RequestBody boletoRequest: BoletoDTO,
                           @RequestHeader (value = "Authorization", required = true) token : String
    ) : ResponseEntity<String> = run {


        val requestTokenHeader: String = token
        val tokenResultado : String = if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            val jwtToken = requestTokenHeader.substring(7)
             JWT.decode(jwtToken).subject

        } else {
            throw RuntimeException()
        }

        //TODO Validar esta funcionalidade
        val permissoes : Autorizacoes = Autorizacao.getPermission(token)
        println(permissoes)

        //Perguntar na aula se o trecho abaixo pode ser melhorado (let)
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
            registroCriadoEm = LocalDateTime.now()
                )

        boletoService.validarPgto(boleto)

    }

    @GetMapping("pagamentos")
    fun consultar (@RequestHeader (value = "Authorization", required = true) token : String) : ResponseEntity<List<Boleto>> = run  {

        //TODO extrair idCliente do token

        val idCliente: UUID = UUID.fromString("b3f33891-d380-4f8b-bcb9-0dbeb10b4eb8")
        ResponseEntity.ok(boletoService.consultar(idCliente))

    }

}