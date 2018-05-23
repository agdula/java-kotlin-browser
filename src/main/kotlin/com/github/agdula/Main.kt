package com.github.agdula

import com.github.agdula.localsrv.Server
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val server = Server()
        server.run()
        val browser = JavaFXBrowser()
        browser.setVisible(true)
        browser.loadURL("http://127.0.0.1:${server.port}/")
    }
}
