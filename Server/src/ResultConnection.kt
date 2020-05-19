package ru.hlil

import java.io.IOException
import java.net.Socket

//Этот класс используется для приема результата вычислений от клиента

class ResultConnection(private val socket: Socket, private val gui: Gui) : Runnable {
    override fun run() {
        try {
            val inputStream = socket.getInputStream()
            var c : Char
            while (!Character.isAlphabetic(inputStream.read())) {
                Thread.sleep(100)//пока результат не получили, спим
            }
            c = inputStream.read().toChar()//считываем результат поэлементно
            val resultBuilder = StringBuilder()
            while (c != '$') {//пока не конец, считываем
                resultBuilder.append(c)
                c = inputStream.read().toChar()
            }
            gui.returnResult(socket, resultBuilder.toString())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
