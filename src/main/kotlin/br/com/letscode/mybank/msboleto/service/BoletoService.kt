package br.com.letscode.mybank.msboleto.service

import br.com.letscode.mybank.msboleto.exception.Exceptions
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

        PgtoAposVencimento(boleto)

        if (boleto.vencimento.isBefore(LocalDate.now()) && boleto.pgtoAposVencimento.equals(true)) {
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

        if (retornoSaldo.operationStatus.equals("OK")){
            boleto.idRegistroOperacao = retornoSaldo.id
            boleto.pgtoStatus = "PENDING-CONFIRMATION"
            boletoRepository.save(boleto)

            if (ConfirmaTransacao.getConfirmacao(boleto.idRegistroOperacao).response != "Transação de id ${boleto.idRegistroOperacao} confirmada") {
                 atualizaBoleto = boletoRepository.findByIdRegistroOperacao(boleto.idRegistroOperacao)
                 atualizaBoleto.pgtoStatus = "DENIED"
                 boletoRepository.save(atualizaBoleto)
                 throw Exceptions.FalhaNaComunicacaoInterna("Operação não concluída por falta de comunicação entre os serviços - Tente novamente")
            }

            atualizaBoleto = boletoRepository.findByIdRegistroOperacao(boleto.idRegistroOperacao)
            atualizaBoleto.pgtoStatus = "ALLOWED"
            boletoRepository.save(atualizaBoleto)
            return ResponseEntity.ok("Operação realizada com sucesso \n $atualizaBoleto")

        }

        ResponseEntity.ok("Saldo insuficiente para realizar a operação")

    }

    private fun PgtoAposVencimento(boleto: Boleto) {
        if (boleto.vencimento.isBefore(LocalDate.now()) && !boleto.pgtoAposVencimento) {
          throw Exceptions.LimiteVencimentoException("Pagamento só pode ser realizado até o vencimento")
        }
    }

    fun consultar(idCliente: UUID): List<Boleto> = boletoRepository.findAllByIdCliente(idCliente)
}