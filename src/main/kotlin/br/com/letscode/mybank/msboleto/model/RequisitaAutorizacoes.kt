package br.com.letscode.mybank.msboleto.model

data class RequisitaAutorizacoes(

    val clienteId: String,
    val expiresIn: Long,
    val tipoOperacao: String
        )