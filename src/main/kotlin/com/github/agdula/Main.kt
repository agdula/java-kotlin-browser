package com.github.agdula

import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val browser = JavaFXBrowser()
        browser.setVisible(true)
        browser.loadURL("http://wykop.pl")
    }
}
