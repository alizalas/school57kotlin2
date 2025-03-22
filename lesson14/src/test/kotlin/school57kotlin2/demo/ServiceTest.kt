package school57kotlin2.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration


@SpringBootTest(
    properties = ["spring.profiles.active=test"]
)
class ServiceTest {


    @Test
    fun contextLoads() {
        Thread.sleep(3000)
    }


}