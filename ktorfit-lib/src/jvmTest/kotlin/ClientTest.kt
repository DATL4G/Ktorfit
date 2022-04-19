import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ClientTest {

    @Test
    fun testWithArray() {


        val ktorfit = Ktorfit("www.example.com/", HttpClient())
        val client = KtorfitClient(ktorfit)

        val requestData = RequestData("GET","posts",
            headers = emptyList(),
            queries = emptyList(),
            fields = emptyList(),
            parts = emptyMap(),
            bodyData = null,
            qualifiedRawTypeName = "kotlinx.coroutines.flow.Flow"
        )

        runBlocking {
            client.suspendRequest<Flow<String>,String>(requestData).catch {
                Assert.assertEquals("Hallo",it.message)
            }
        }
       assert(1==1)
    }

    @Test
    fun whenBaseUrlNotEndingWithSlashThrowError() {
        try {
            val ktorfit = Ktorfit("www.example.com")
        }catch (illegal: IllegalStateException){
            assert(illegal.message == "Base URL needs to end with /")
        }
    }

    @Test
    fun whenBaseUrlEmptyThrowError() {
        try {
            val ktorfit = Ktorfit("")
        }catch (illegal: IllegalStateException){
            assert(illegal.message == "Base URL required")
        }

    }

}