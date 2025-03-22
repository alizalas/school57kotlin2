package school57kotlin2.demo.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service
class ClockService(
    // maybe use @Value for timeout and url ? https://www.baeldung.com/spring-value-annotation
//    @Value("${'$'}{my.config.timeout}")
//    val timeout: Long,
//
//    @Value("${'$'}{my.config.url}")
//    val url: String

    val myProperties: MyProperties
) {

    // just counter
    var count = 1

    // Execute every 1000 ms
    @Scheduled(fixedRate = 1000)
    fun ping() {
        println("Выполняем запрос ${count++} к ресурсу ${myProperties.url} с таймаутом ${myProperties.timeout} секунд")
    }


}

@Configuration
@ConfigurationProperties("my.config")
open class MyProperties {
    open var timeout: Long = 13455
    open lateinit var url: String
}

