package br.com.letscode.mybank.msboleto.utils
import br.com.letscode.mybank.msboleto.model.Autorizacoes
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.util.MimeTypeUtils.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.awt.PageAttributes

object Autorizacao {

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
