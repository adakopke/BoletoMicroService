package br.com.letscode.mybank.msboleto.handle

import br.com.letscode.mybank.msboleto.exception.Exceptions
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

data class Error(
    val message: String?,
    val code: Int
)

@ControllerAdvice
class HandleException : ResponseEntityExceptionHandler () {

    @ExceptionHandler(value = [Exceptions.TokenExpiradoException::class, Exceptions.TokenInvalidoException::class, Exceptions.AcessoNegadoException::class])
    protected fun handleToken(
        ex: RuntimeException, request: WebRequest
    ): ResponseEntity<Error> = ResponseEntity.status(401).body(
        Error(
            ex.message,
            401
        )
    )

    @ExceptionHandler(value = [Exceptions.LimiteVencimentoException::class])
    protected fun handleVencimento(
        ex: RuntimeException, request: WebRequest
    ): ResponseEntity<Error> = ResponseEntity.status(400).body(
        Error(
            ex.message,
            400
        )
    )

    @ExceptionHandler(value = [Exceptions.FalhaNaComunicacaoInterna::class])
    protected fun handleComunicacaoEntreServicos(
        ex: RuntimeException, request: WebRequest
    ): ResponseEntity<Error> = ResponseEntity.status(503).body(
        Error(
            ex.message,
            503
        )
    )


}