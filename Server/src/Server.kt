package ru.hlil

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*


class Server(port: Int, maxConnections: Int, address: String?) : Runnable {
    /*
    * Пул свободных клиентов - готовых принять на обработку
    * очередную вычислительную задачу
    * */
    private var freeSockets: Queue<Socket>? = null//очередь
    private var serverSocket: ServerSocket? = null
    private val dao: MatrixDao = MatrixDao.getInstance()
    private var running = false

    init {
        try {
            serverSocket = ServerSocket(port, maxConnections, InetAddress.getByName(address))//определяет Socket с портом, масимальным соединением и адресом
            freeSockets = LinkedList()//очередь
            running = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {//добавляем свободные Socket в очередь
        while (running) {
            try {
                freeSockets!!.add(serverSocket!!.accept())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(InterruptedException::class, IllegalArgumentException::class, IOException::class)
    fun multiply(table1: String?, table2: String?): Socket {//перемножение матриц

        // если таблицы с заданным именем нет - выбрасывается исключение
        if(!MatrixDao.isExistsTable(table1) || !MatrixDao.isExistsTable(table2)){
            throw IllegalArgumentException("table not found");
        }

        // Ждем, пока появится свободный клиент
        while (freeSockets!!.isEmpty()) {
            Thread.sleep(100)
        }
        val socket = freeSockets!!.remove()
        /*
        * забираем сокет свободного клиента, отправляем ему запрос в виде двух таблиц
        * и возвращаем этот сокет - по нему клиент передаст результат вычислений, когда посчитает его.
        * между отправкой таблиц ждем от клиента сигнала, что первая таблица сохранена - знак $
        * далее отправляем вторую таблицу
        * */
        val outputStream = socket.getOutputStream()
        outputStream.flush()
        dao.writeMatrix(table1!!, outputStream)
        val inputStream = socket.getInputStream()
        while (inputStream.read() != '$'.toInt()) {
            Thread.sleep(10)
        }
        dao.writeMatrix(table2!!, outputStream)
        return socket
    }

    fun stop() {
        running = false
    }

    fun returnSocket(socket: Socket) {
        freeSockets!!.add(socket)
    }

}
