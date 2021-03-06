package br.com.letscode.mybank.msboleto.utils
import br.com.letscode.mybank.msboleto.model.RequisitaAutorizacoes
import br.com.letscode.mybank.msboleto.model.RespostaAutorizacoes
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

object Autorizacao {

    val client = WebClient
        .builder()
        .baseUrl("https://dff8335c-88c3-457a-abf9-ba9d05938cf1.mock.pstmn.io")
        .build()

    suspend fun getPermission(requisitaAutorizacoes: RequisitaAutorizacoes) = client
        .post()
        .uri("/authorization")
        .body(BodyInserters.fromValue(requisitaAutorizacoes))
        .retrieve()
        .awaitBody<RespostaAutorizacoes>()
}
