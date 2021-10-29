package br.com.letscode.mybank.msboleto.utils
import br.com.letscode.mybank.msboleto.model.Autorizacoes
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

object BoletoClient {

    val client = WebClient
        .builder()
        .baseUrl("https://dff8335c-88c3-457a-abf9-ba9d05938cf1.mock.pstmn.io")
        .build()

    suspend fun getPermission(token : String) = client
        .post()
        .uri("/authorization")
        .header("Authorization", "Bearer $token")
        .retrieve()
        .awaitBody<Autorizacoes>()
}
