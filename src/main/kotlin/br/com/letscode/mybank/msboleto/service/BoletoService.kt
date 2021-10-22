package br.com.letscode.mybank.msboleto.service

import br.com.letscode.mybank.msboleto.model.Boleto
import br.com.letscode.mybank.msboleto.repository.BoletoRepository
import org.springframework.stereotype.Service

@Service
class BoletoService (val boletoRepository: BoletoRepository ) {
    fun validarPgto(boleto: Boleto) = run {

        //TODO programar toda a lógica conforme fluxo desenhado

        boletoRepository.save(boleto)

    }
}