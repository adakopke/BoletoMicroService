package br.com.letscode.mybank.msboleto.controller

import br.com.letscode.mybank.msboleto.dto.BoletoRequest
import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.service.BoletoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("api")
class BoletoController (val boletoService: BoletoService) {

    @PostMapping("pagar")
    fun pgtoBoleto(@RequestBody boletoRequest: BoletoRequest,
                   @RequestHeader (value = "Authorization", required = true) token : String
    ) : ResponseEntity<String> = run {

        //TODO criar validação do TOKEN em boletoservice

        val boleto = Boleto (

            //TODO capturar UUID do token
            idCliente = UUID.randomUUID(),
            codAgBeneficiario = boletoRequest.codAgBeneficiario,
            codContaBeneficiario = boletoRequest.codContaBeneficiario,
            nossoNumero = boletoRequest.nossoNumero,
            especie = boletoRequest.especie,
            valor = boletoRequest.valor,
            multaDia = boletoRequest.multaDia,
            jurosDia = boletoRequest.jurosDia,
            vencimento = boletoRequest.vencimento,
            pgtoAposVencimento = boletoRequest.pgtoAposVencimento,
            registroCriadoEm = LocalDateTime.now()
                )

        //TODO Criar condicional
        boletoService.validarPgto(boleto)
        ResponseEntity.ok("Pagamento realizado com sucesso \n $boleto")
    }

}