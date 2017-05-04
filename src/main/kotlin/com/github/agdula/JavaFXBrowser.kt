
package com.github.agdula

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView

import javax.swing.*
import java.awt.*
import java.net.URL

import javaslang.control.Try
import mu.KotlinLogging


class JavaFXBrowser : JFrame() {

    private val log = KotlinLogging.logger {}

    private val jfxPanel = JFXPanel()

    private val panel = JPanel(BorderLayout())
    private val lblStatus = JLabel()

    private val btnGo = JButton("Go")
    private val txtURL = JTextField()
    private val progressBar = JProgressBar()

    private var view : WebView? = null;

    init {
        initComponents()
        handleEvents()
        pack()
    }

    private fun initComponents() {
        
        Platform.runLater {
            view = WebView() // has to be initialized in JavaFX thread
            jfxPanel.scene = Scene(view)
        }

        with(progressBar) {
            preferredSize = Dimension(150, 18)
            isStringPainted = true
        }
        
        val topBar = JPanel(BorderLayout(5, 0))
        val statusBar = JPanel(BorderLayout(5, 0))

        with(topBar) {
            border = BorderFactory.createEmptyBorder(3, 5, 3, 5)
            add(txtURL, BorderLayout.CENTER)
            add(btnGo, BorderLayout.EAST)
        }

        with(statusBar) {
            border = BorderFactory.createEmptyBorder(3, 5, 3, 5)
            add(lblStatus, BorderLayout.CENTER)
            add(progressBar, BorderLayout.EAST)
        }

        with(panel) {
            add(topBar, BorderLayout.NORTH)
            add(jfxPanel, BorderLayout.CENTER)
            add(statusBar, BorderLayout.SOUTH)
        }

        contentPane.add(panel)

        preferredSize = Dimension(1024, 600)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    }

    fun loadURL(url: String) {
        Platform.runLater {
            Try.of { URL(url).toExternalForm() }
                    .recover { log.info{ "fallback loading of $url " } ;  URL("http://" + it).toExternalForm() }
                    .map { view!!.engine.load(it) }
        }
    }

    private fun handleEvents() {

        Platform.runLater {

            btnGo.addActionListener { loadURL(txtURL.text) }
            txtURL.addActionListener { loadURL(txtURL.text) }

            with(view!!.engine) {
                
                titleProperty().addListener { _, _, newValue ->
                    SwingUtilities.invokeLater { this@JavaFXBrowser.title = newValue }
                }

                setOnStatusChanged { event ->
                    SwingUtilities.invokeLater { lblStatus.text = event.data }
                }

                locationProperty().addListener { _, _, newValue ->
                    SwingUtilities.invokeLater { txtURL.text = newValue }
                }

                loadWorker.workDoneProperty().addListener { _, _, newValue ->
                    SwingUtilities.invokeLater { progressBar.value = newValue.toInt() }
                }

                loadWorker
                        .exceptionProperty()
                        .addListener { _, _, value ->
                            log.info { "${location}: ${value?.message}" }
                        }
            }
        }

    }


}



