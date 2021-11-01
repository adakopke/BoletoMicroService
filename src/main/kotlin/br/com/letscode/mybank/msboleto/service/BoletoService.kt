package br.com.letscode.mybank.msboleto.service

import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.model.RequisitaSaldo
import br.com.letscode.mybank.msboleto.model.RespostaSaldo
import br.com.letscode.mybank.msboleto.repository.BoletoRepository
import br.com.letscode.mybank.msboleto.utils.ConsultaSaldo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
class BoletoService (val boletoRepository: BoletoRepository ) {
    suspend fun validarPgto(boleto: Boleto, token : String): ResponseEntity<String> = run {

        //TODO TRocar para fun
        if (boleto.vencimento.isBefore(LocalDate.now()) && boleto.pgtoAposVencimento.equals(false)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pagamento só pode ser realizado até o vencimento")
        } else {

        val diasVencidos = boleto.vencimento.until(LocalDate.now()).days
        val valorJuros = boleto.valor.multiply(BigDecimal.valueOf(boleto.jurosDia * diasVencidos))
        val valorMulta = boleto.valor.multiply(BigDecimal.valueOf(boleto.multa))
        boleto.valor = boleto.valor.add(valorJuros).add(valorMulta)
        }

        //TODO Implementar consulta ao MS Saldo

        val requisitaSaldo = RequisitaSaldo (

            idContaPagador = boleto.idContaPagador,
            valor = boleto.valor
                )


        val retornoSaldo : RespostaSaldo = ConsultaSaldo.getSaldo(token, requisitaSaldo)

        if (retornoSaldo.operationStatus.equals("ALLOWED")){

            boletoRepository.save(boleto)

            //TODO Criar aqui a validação do retorno da operação no MS de saldo
            //TODO Lembrar de incluir um campo no boleto para salvar o status da operacao ALLOWED or DENIED

            return ResponseEntity.ok("Operação realizada com sucesso \n $boleto")



        }


        ResponseEntity.ok("Saldo insuficiente para realizar a operação")

    }

    //Perguntar na aula se está certo ou se merece um exception
    fun consultar(idCliente: UUID): List<Boleto> = boletoRepository.findAllByIdCliente(idCliente)
}