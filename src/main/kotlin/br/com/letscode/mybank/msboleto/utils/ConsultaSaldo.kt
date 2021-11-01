package br.com.letscode.mybank.msboleto.utils
import br.com.letscode.mybank.msboleto.model.Autorizacoes
import br.com.letscode.mybank.msboleto.model.RequisitaSaldo
import br.com.letscode.mybank.msboleto.model.RespostaSaldo
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

object ConsultaSaldo {

    val client = WebClient
        .builder()
        .baseUrl("https://dff8335c-88c3-457a-abf9-ba9d05938cf1.mock.pstmn.io")
        .build()

    suspend fun getSaldo(token : String, saldo : RequisitaSaldo) = client
        .post()
        .uri("/withdraw")
        .header("Authorization", "Bearer $token")
        .body(BodyInserters.fromValue(saldo))
        .retrieve()
        .awaitBody<RespostaSaldo>()
}
