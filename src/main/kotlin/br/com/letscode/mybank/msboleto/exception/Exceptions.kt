package br.com.letscode.mybank.msboleto.exception


class Exceptions(message: String) : RuntimeException(message) {

    class TokenExpiradoException(message: String): RuntimeException(message)

    class TokenInvalidoException(message: String): RuntimeException(message)

    class AcessoNegadoException(message: String): RuntimeException(message)

    class LimiteVencimentoException(message: String): RuntimeException(message)

    class FalhaNaComunicacaoInterna(message: String) : RuntimeException(message)

}