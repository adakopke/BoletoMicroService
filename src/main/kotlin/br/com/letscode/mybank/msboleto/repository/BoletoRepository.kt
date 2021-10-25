package br.com.letscode.mybank.msboleto.repository

import br.com.letscode.mybank.msboleto.model.Boleto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BoletoRepository : JpaRepository <Boleto, Int> {
    fun findAllByIdCliente(idCliente: UUID) : List<Boleto>?
}