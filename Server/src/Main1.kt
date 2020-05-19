package ru.hlil

import java.io.IOException
import java.net.Socket
import java.util.*

class Dao{
    companion object{
        val dao = MatrixDao.getInstance()
    }
}

fun main() {

    val dao = Dao
    /* Сервер запускается в отдельном потоке
    * чтобы он мог в любой момент принять нового клиента
    * */
    val server = Server(5703, 100, "localhost")
    val serverThread = Thread(server)//поток
    serverThread.start()
    val gui = Gui(server)
    println("команда для перемножения двух матриц:")
    println("%tablename1% %tablename2%")
    println("команда остановки сервера:")
    println("/stop")
    val scanner = Scanner(System.`in`)
    var tablename1: String?
    /*
        * в основном потоке программы мы считываем команды -
        * какие две таблицы нужно перемножить
        * */
    while (scanner.next().also { tablename1 = it } != "/stop") {//считывает то, что мы вписали, если это /stop, значит это название матрицы
        try {
            val tablename2 = scanner.next()
            /*
                 * просим сервер перемножить две таблицы -
                 * в ответ получаем сокет, через который нам придет результат
                 * */
            var resultSocket: Socket? = null
            while (resultSocket==null) {
                try {
                    resultSocket = server.multiply(tablename1, tablename2)
                } catch (e: IOException) { //Если произошла ошибка подключения - забываем про этого клиента, ждем следующего
                    null
                }
            }
            /* запускаем новый поток, ждем, когда придет результат через resultSocket
            * чтобы передать его в форму gui*/
            Thread(ResultConnection(resultSocket, gui)).start()
        } catch (e: IllegalArgumentException) {
            gui.append(e.message)
        }
    }
    server.stop()
}

