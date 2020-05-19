package ru.hlil

import java.net.Socket
fun main() {
    val running = true
    // @Throws(IOException::class, InterruptedException::class)
    // @JvmStatic
    val socket = Socket("localhost", 5703)
    val inputStream = socket.getInputStream()
    while (running) {
        val matrix1 = Matrix.readMatrix(inputStream)//получаем первую матрицу
        socket.getOutputStream().write("$".toByteArray())//говорим серверу что готовы принять следующую матрицу
        val matrix2 = Matrix.readMatrix(inputStream)//получаем вторую матрицу
        val outputStream = socket.getOutputStream()
        try {
            val result = matrix1.multiply(matrix2)//перемножаем матрицы
            outputStream.flush()
            outputStream.write("$result $ ".toByteArray())//отправляем в поток матрицу
            println("таблицы успешно перемножены, размерность результирующей таблицы " + result.columnCount + " x " + result.recordCount)
        } catch (e: IllegalArgumentException) {
            outputStream.write((e.message + " $ ").toByteArray())//если ошибка,отправляем сооьщение с исключением
            println(e.message)
        }
    }
}



