package br.com.letscode.mybank.msboleto.service

import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.model.RequisitaSaldo
import br.com.letscode.mybank.msboleto.model.RespostaSaldo
import br.com.letscode.mybank.msboleto.repository.BoletoRepository
import br.com.letscode.mybank.msboleto.utils.ConfirmaTransacao
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


        val requisitaSaldo = RequisitaSaldo (

            idContaPagador = boleto.idContaPagador,
            valor = boleto.valor
                )


        val retornoSaldo : RespostaSaldo = ConsultaSaldo.getSaldo(token, requisitaSaldo)

        val atualizaBoleto : Boleto

        if (retornoSaldo.operationStatus.equals("ALLOWED")){
            boleto.idRegistroOperacao = retornoSaldo.id
            boleto.pgtoStatus = "PENDING-CONFIRMATION"
            boletoRepository.save(boleto)

            if (ConfirmaTransacao.getConfirmacao(boleto.idRegistroOperacao).response != "Transação feita") {
                 atualizaBoleto = boletoRepository.findByIdRegistroOperacao(boleto.idRegistroOperacao)
                 atualizaBoleto.pgtoStatus = "DENIED"
                 boletoRepository.save(atualizaBoleto)
                 return ResponseEntity.internalServerError().body("Operação não concluída - Tente novamente")
            }

            atualizaBoleto = boletoRepository.findByIdRegistroOperacao(boleto.idRegistroOperacao)
            atualizaBoleto.pgtoStatus = "ALLOWED"
            boletoRepository.save(atualizaBoleto)
            return ResponseEntity.ok("Operação realizada com sucesso \n $atualizaBoleto")

        }

        ResponseEntity.ok("Saldo insuficiente para realizar a operação")

    }

    fun consultar(idCliente: UUID): List<Boleto> = boletoRepository.findAllByIdCliente(idCliente)
}