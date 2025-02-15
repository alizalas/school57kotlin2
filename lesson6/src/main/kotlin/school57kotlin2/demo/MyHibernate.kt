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
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

class MyRepository<T : Any>(
    val connection: Connection,
    val table: String,
) {

    inline fun <reified T : Any> create(obj: T): T {
        // Создаём список, где будем хранить атрибуты класса
        val propertiesList = mutableListOf<Pair<String, String>>()

        // Находим все поля в классе и итерируемся по ним
        obj::class.declaredMemberProperties.forEach { properties ->
            // Сохраняем имя и тип свойства в список
            propertiesList.add(properties.name to properties.returnType.toString())

            // Выводим в консоль название и тип поля
            println("${properties.name} : ${properties.returnType}")
        }

        val statement: Statement = connection.createStatement()

        val insertSQL = """
        INSERT INTO ${table} (${propertiesList.map { it.first }.joinToString(", ")})
        VALUES ('${propertiesList.map { it.second }.joinToString("', '")}')
        """.trimIndent()

        statement.executeUpdate(insertSQL)
        connection.close()

        return obj
        //return T::class.primaryConstructor!!.call(....)
    }

    inline fun <reified T : Any> read(id: Long): T {
        val statement: Statement = connection.createStatement()

        val selectSQL = """
        SELECT * FROM ${table} WHERE id = ${id}
        """.trimIndent()

        val resultSet = statement.executeQuery(selectSQL)
        connection.close()

        val metaData = resultSet.metaData
        val propertiesList = mutableListOf<Pair<String, Any>>()

        for (i in 1..metaData.columnCount) {
            propertiesList.add(metaData.getColumnName(i) to resultSet.getObject(i))
        }

        return T::class.primaryConstructor!!.call(propertiesList.map { it.second })

    }

    inline fun <reified T : Any> update(obj: T): T {
        // Создаём список, где будем хранить атрибуты класса
        val propertiesList = mutableListOf<Pair<String, String>>()

        // Находим все поля в классе и итерируемся по ним
        obj::class.declaredMemberProperties.forEach { properties ->
            // Сохраняем имя и тип свойства в список
            propertiesList.add(properties.name to properties.returnType.toString())

            // Выводим в консоль название и тип поля
            println("${properties.name} : ${properties.returnType}")
        }

        val statement: Statement = connection.createStatement()

        val selectSQL = """
        SELECT id FROM ${table} 
        WHERE ${propertiesList.map { (first, second) -> "$first = $second" }.joinToString(", ")}
        """.trimIndent()

        val resultSet = statement.executeQuery(selectSQL)

        val insertSQL = """
        INSERT INTO ${table} (${propertiesList.map { it.[0] }.joinToString(", ")})
        VALUES ('${propertiesList.map { it.[1] }.joinToString("', '")}')
        """.trimIndent()

        statement.executeUpdate(insertSQL)
        connection.close()

        return obj
        //return T::class.primaryConstructor!!.call(....)
    }

    inline fun <reified T : Any> delete(id: Long): T {
        TODO()
    }

}

fun main() {
    val url = "jdbc:postgresql://localhost:5432/postgres"
    val props = Properties().apply {
        setProperty("user", "myuser")
        setProperty("password", "mypassword")
    }

    val connection = DriverManager.getConnection(url, props)
    val connection: Connection = TODO()
    val repository = MyRepository<...>(connection)

    val entity = repository.create(...)
    repository.read(entity.id)
    ..
}