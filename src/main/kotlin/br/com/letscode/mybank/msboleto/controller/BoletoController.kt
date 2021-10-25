package br.com.letscode.mybank.msboleto.controller

import br.com.letscode.mybank.msboleto.dto.BoletoRequest
import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.service.BoletoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("api")
class BoletoController (val boletoService: BoletoService) {

    @PostMapping("pagar")
    fun pgtoBoleto(@RequestBody boletoRequest: BoletoRequest,
                   @RequestHeader (value = "Authorization", required = true) token : String
    ) : ResponseEntity<String> = run {

        //TODO Incluir consulta ao MS Autenticacao para retornar permissões

        val boleto = Boleto (
            //TODO capturar UUID do token
            idCliente = UUID.randomUUID(),
            codAgBeneficiario = boletoRequest.codAgBeneficiario,
            codContaBeneficiario = boletoRequest.codContaBeneficiario,
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