//package school57kotlin2.demo
//
//import java.sql.DriverManager
//import java.util.*
//
//
//
//fun main() {
//
//
//    val url = "jdbc:postgresql://localhost:5432/postgres"
//    val props = Properties().apply {
//        setProperty("user", "baeldung")
//        setProperty("password", "baeldung")
//    }
//
//    DriverManager.getConnection(url, props).use { connection ->
//        println(connection.metaData.databaseProductVersion)
//
//        connection.createStatement().use { statement ->
//            val rs = statement.executeQuery("SELECT 1+1;")
//
//            rs.next()
//            println(rs.getObject(1))
//        }
//    }
//}
//
//





package school57kotlin2.demo

import java.sql.DriverManager
import java.sql.Statement
import java.util.*

fun main() {
    val url = "jdbc:postgresql://localhost:5432/postgres"
    val props = Properties().apply {
        setProperty("user", "myuser")
        setProperty("password", "mypassword")
    }

    val connection = DriverManager.getConnection(url, props)
    val statement: Statement = connection.createStatement()

    val dropTableSQL = "DROP TABLE IF EXISTS users".trimIndent()

    statement.executeUpdate(dropTableSQL)

    val createTableSQL = """
        CREATE TABLE IF NOT EXISTS users
        (
          id SERIAL PRIMARY KEY,
          username VARCHAR(255),
          password VARCHAR(255),
          email VARCHAR(255),
          firstname VARCHAR(255),
          lastname VARCHAR(255)
        )
    """.trimIndent()

    statement.executeUpdate(createTableSQL)

    val insertSQL = """
        INSERT INTO users (username, password, email, firstname, lastname)
        VALUES ('user_1', 'password', 'user@example.com', 'User', 'User')
    """.trimIndent()

    statement.executeUpdate(insertSQL)

    val selectSQL = """
        SELECT * FROM users WHERE username = 'user_1'
    """.trimIndent()

    val resultSet = statement.executeQuery(selectSQL)

    while (resultSet.next()) {
        val id = resultSet.getInt("id")
        val username = resultSet.getString("username")
        val password = resultSet.getString("password")
        val email = resultSet.getString("email")
        val firstname = resultSet.getString("firstname")
        val lastname = resultSet.getString("lastname")

        println("ID: $id, Username: $username, Password: $password, Email: $email, Firstname: $firstname, Lastname: $lastname")
    }

    resultSet.close()
    statement.close()
    connection.close()
}