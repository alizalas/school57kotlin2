//package school57kotlin2.demo
//
//import java.sql.Connection
//
//class MyRepository<T : Any>(
//    val connection: Connection,
//) {
//
//    init {
//        //Тут можно проверить наличие таблицы
//        TODO()
//    }
//
//    inline fun <reified T : Any> create(obj: T): T {
//        //return T::class.primaryConstructor!!.call(....)
//        // Название таблички можно брать с названия класса
//        TODO()
//    }
//
//    inline fun <reified T : Any> read(id: Long): T {
//        TODO()
//    }
//
//    inline fun <reified T : Any> update(obj: T): T {
//        TODO()
//    }
//
//    inline fun <reified T : Any> delete(id: Long): T {
//        TODO()
//    }
//
//}
//
//fun main() {
////    val connection: Connection = TODO()
////    val repository = MyRepository<...>(connection)
////
////    val entity = repository.create(...)
////    repository.read(entity.id)
////        ..
//}




package school57kotlin2.demo

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

class MyRepository<T : Any>(
    val connection: Connection,
    val table: String,
) {

    inline fun <reified T : Any> create(obj: T): T {
        val crStatement: Statement = connection.createStatement()
        val propertiesList = mutableListOf<Pair<String, Any?>>()

        obj::class.declaredMemberProperties.forEach { properties ->
            propertiesList.add(properties.name to properties.getter.call(obj))
        }

        val insertSQL = """
        INSERT INTO ${table} (${propertiesList.map { it.first }.joinToString(", ")})
        VALUES ('${propertiesList.map { it.second }.joinToString("', '")}')
        """.trimIndent()

        crStatement.executeUpdate(insertSQL)

        return obj
        //return T::class.primaryConstructor!!.call(....)
    }

    inline fun <reified T : Any> read(id: Long): T {
        val rdStatement: Statement = connection.createStatement()
        val propertiesList = mutableListOf<Pair<String, Any?>>()

        val selectSQL = """
        SELECT * FROM ${table}
        WHERE id = ${id}
        """.trimIndent()

        val rdResultSet = rdStatement.executeQuery(selectSQL)

        if (rdResultSet.next()) {
            val metaData = rdResultSet.metaData
            for (i in 1..metaData.columnCount) {
                propertiesList.add(metaData.getColumnName(i) to rdResultSet.getObject(i))
            }
        } else {
            return T::class.primaryConstructor!!.call(-1, null, null, null, null, null)
        }

        return T::class.primaryConstructor!!.call(*propertiesList.map { it.second }.toTypedArray())
    }

    inline fun <reified T : Any> update(obj: T): T {
        val upStatement: Statement = connection.createStatement()
        val propertiesList = mutableListOf<Pair<String, Any?>>()

        obj::class.declaredMemberProperties.forEach { properties ->
            propertiesList.add(properties.name to properties.getter.call(obj))
        }

        val id = propertiesList.find { it.first == "id" }?.second

        val updateSQL = """
        UPDATE ${table} 
        SET ${propertiesList.map { (first, second) -> "$first = '$second'" }.joinToString(", ")}
        WHERE id = ${id}
        """.trimIndent()

        upStatement.executeUpdate(updateSQL)

        return obj
        //return T::class.primaryConstructor!!.call(....)
    }

    inline fun <reified T : Any> delete(id: Long): T {
        val delStatement: Statement = connection.createStatement()
        val propertiesList = mutableListOf<Pair<String, Any?>>()

        val selectSQL = """
        SELECT * FROM ${table}
        WHERE id = ${id}
        """.trimIndent()

        val delResultSet = delStatement.executeQuery(selectSQL)

        if (delResultSet.next()) {
            val metaData = delResultSet.metaData
            for (i in 1..metaData.columnCount) {
                propertiesList.add(metaData.getColumnName(i) to delResultSet.getObject(i))
            }
        } else {
            return T::class.primaryConstructor!!.call(-1, null, null, null, null, null)
        }

        val deleteSQL = """
        DELETE FROM ${table}
        WHERE id = ${id}
        """.trimIndent()

        delStatement.executeUpdate(deleteSQL)

        return T::class.primaryConstructor!!.call(*propertiesList.map { it.second }.toTypedArray())
    }

}

fun main() {
    val url = "jdbc:postgresql://localhost:5432/postgres"
    val props = Properties().apply {
        setProperty("user", "myuser")
        setProperty("password", "mypassword")
    }

    val connection = DriverManager.getConnection(url, props)
    val repository = MyRepository<User>(connection, "users")

    val newUser = User(id = 3, username = "user_2", password = "password", email = "user@yandex.ru", firstname = "User2", lastname = "User2")
    val createdUser = repository.create(newUser)
    println("Created user: $createdUser")

    val userId = createdUser.id
    val readUser = repository.read<User>(userId)
    println("Read user: $readUser")

    val updatedUser = readUser.copy(username = "User3", email = "sdfsf")
    repository.update(updatedUser)
    println("Updated user: $updatedUser")

    val deletedUser = repository.delete<User>(2)
    println("Deleted user: $deletedUser")
}