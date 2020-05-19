package ru.hlil

import java.io.OutputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

//Класс, отвечающий за доступ к данным из базы данных
class MatrixDao {
    //@Throws(IOException::class)
    fun writeMatrix(tableName: String, outputStream: OutputStream) {
        try {
            val c: Connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Client_Server?serverTimezone=UTC",
                "Liliya",
                "12345"
            )
            val selectAllStatement: Statement = c.createStatement()
            val s: Statement = c.createStatement()
            var countRes =
                s.executeQuery("SELECT MAX(column_index) FROM $tableName") //максимальный индекс столбца
            countRes.next()
            val columnCount = countRes.getInt(1) //количество столбцов
            countRes = s.executeQuery("SELECT MAX(record_index) FROM $tableName") //максимальный индекс строки
            countRes.next()
            val recordCount = countRes.getInt(1) //количество строк
            s.close()
            outputStream.write(" $tableName $columnCount $recordCount\n".toByteArray())//отправляем в поток название матрицы и ее параметры
            val resultSet =
                selectAllStatement.executeQuery("SELECT * FROM $tableName ORDER BY record_index")
            while (resultSet.next()) {//поэлементно все считываем с таблиц,  записывая все через пробел
                outputStream.write(
                    (resultSet.getInt("value").toString() + " " +
                            resultSet.getInt("column_index") + " " +
                            resultSet.getInt("record_index") + "\n")
                        .toByteArray()
                )
            }
            countRes.close()
            outputStream.write(" $ ".toByteArray())
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val instance = MatrixDao()

        fun getInstance(): MatrixDao {
            return instance
        }

        fun isExistsTable(table1: String?): Boolean {//проверяем существование таблицы в бд
            try {
                val c: Connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Client_Server?serverTimezone=UTC",
                    "Liliya",
                    "12345"
                )
                val metaData = c.metaData
                if (metaData.getTables(null, null, table1, null).next()) {
                    return true
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return false
        }
    }
}
