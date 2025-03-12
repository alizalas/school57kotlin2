package school57kotlin2.demo.initialize

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

@Component
class DbInit {

    @PostConstruct
    fun initialize() {
        val url = "jdbc:postgresql://localhost:5432/mydatabase"
        val props = Properties().apply {
            setProperty("user", "myuser")
            setProperty("password", "mypassword")
        }

        val connection = DriverManager.getConnection(url, props)
        val statement: Statement = connection.createStatement()

        try {
            val createTableSQL = """
                CREATE TABLE IF NOT EXISTS users
                (
                  id BIGSERIAL PRIMARY KEY,
                  name VARCHAR(255),
                  balance BIGINT
                )
            """.trimIndent()

            statement.executeUpdate(createTableSQL)
        } catch (e: Exception) {
            println("Ошибка при создании таблицы: ${e.message}")
        } finally {
            statement.close()
            connection.close()
        }
    }
}