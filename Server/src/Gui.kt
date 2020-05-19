package ru.hlil

import java.awt.Dimension
import java.net.Socket
import javax.swing.*


class Gui(private val server: Server): JFrame() {
    //текстовое поле, куда выводится результат
    private var mainTextArea: JTextArea = JTextArea()
    private val dim: Dimension

    init {

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        dim = Dimension(500, 500)
        minimumSize = dim

        val gl = GroupLayout(contentPane)
        layout = gl
        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addComponent(
                    mainTextArea,
                    (dim.height).toInt(),
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE
                )
                .addGap(4)
        )
        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(
                            mainTextArea,
                            (dim.width).toInt(),
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE
                        )
                    //.addComponent(controlPanel)
                )
                .addGap(4)
        )
        pack()
        isVisible = true

    }
    /*
    * synchronized означает, что если два или более потока не могут использовать этот метод одновременно
    * вместо этого они вызовут этот метод по очереди.*/
    @Synchronized
    fun returnResult(socket: Socket?, result: String?) {
        /* получили от подключения результат вычисления - вывели его в поле,
        * а само подключение вернули серверу как "освободившееся"*/
        mainTextArea!!.append(result)
        server.returnSocket(socket!!)
    }

    fun append(s: String?) {
        mainTextArea!!.append(s)
    }
}