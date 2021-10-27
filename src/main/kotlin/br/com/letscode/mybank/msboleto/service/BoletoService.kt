package br.com.letscode.mybank.msboleto.service

import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.repository.BoletoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
class BoletoService (val boletoRepository: BoletoRepository ) {
    fun validarPgto(boleto: Boleto): ResponseEntity<String> = run {
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

        boletoRepository.save(boleto)
        ResponseEntity.ok("Operação realizada com sucesso \n $boleto")

    }

    //Perguntar na aula se está certo ou se merece um exception
    fun consultar(idCliente: UUID): List<Boleto> = boletoRepository.findAllByIdCliente(idCliente)
}