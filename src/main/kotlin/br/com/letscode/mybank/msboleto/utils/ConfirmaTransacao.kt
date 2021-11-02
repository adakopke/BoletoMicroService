package br.com.letscode.mybank.msboleto.utils
import br.com.letscode.mybank.msboleto.model.RespostaConfirmacaoTransacao
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

object ConfirmaTransacao {

    val client = WebClient
        .builder()
        .baseUrl("https://dff8335c-88c3-457a-abf9-ba9d05938cf1.mock.pstmn.io")
        .build()

    suspend fun getConfirmacao(idTransacao : String) = client
        .post()
        .uri("/operation/confirmTransaction")
        .body(BodyInserters.fromValue(idTransacao))
        .retrieve()
        .awaitBody<RespostaConfirmacaoTransacao>()
}
