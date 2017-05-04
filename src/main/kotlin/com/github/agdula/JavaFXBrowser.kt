
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

    init {
        initComponents()
    }

    private fun initComponents() {
        createScene()

        progressBar.setPreferredSize(Dimension(150, 18))
        progressBar.setStringPainted(true)

        val topBar = JPanel(BorderLayout(5, 0))
        topBar.border = BorderFactory.createEmptyBorder(3, 5, 3, 5)
        topBar.add(txtURL, BorderLayout.CENTER)
        topBar.add(btnGo, BorderLayout.EAST)

        val statusBar = JPanel(BorderLayout(5, 0))
        statusBar.border = BorderFactory.createEmptyBorder(3, 5, 3, 5)
        statusBar.add(lblStatus, BorderLayout.CENTER)
        statusBar.add(progressBar, BorderLayout.EAST)

        panel.add(topBar, BorderLayout.NORTH)
        panel.add(jfxPanel, BorderLayout.CENTER)
        panel.add(statusBar, BorderLayout.SOUTH)

        contentPane.add(panel)

        preferredSize = Dimension(1024, 600)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        pack()
    }


    var _engine : WebEngine? = null;

    fun loadURL(url: String) {
        Platform.runLater {
            Try.of { URL(url).toExternalForm() }
                    .recover { log.info{ "fallback loading of $url " } ;  URL("http://" + it).toExternalForm() }
                    .map { _engine!!.load(it) }


        }
    }

    private fun createScene() {

        Platform.runLater {

            val view = WebView()
            val engine = view.getEngine()
            _engine = engine;

            btnGo.addActionListener{ loadURL(txtURL.getText()) }
            txtURL.addActionListener { loadURL(txtURL.getText()) }


            engine.titleProperty().addListener { _, _, newValue ->
                SwingUtilities.invokeLater { this@JavaFXBrowser.title = newValue }
            }

            engine.setOnStatusChanged { event ->
                SwingUtilities.invokeLater { lblStatus.text = event.data }
            }

            engine.locationProperty().addListener { _, _, newValue ->
                SwingUtilities.invokeLater { txtURL.text = newValue }
            }

            engine.loadWorker.workDoneProperty().addListener { _, _, newValue ->
                SwingUtilities.invokeLater { progressBar.value = newValue.toInt() }
            }

            engine.loadWorker
                    .exceptionProperty()
                    .addListener { _, _, value ->
                            log.info { "${engine.location}: ${value?.message}" }
                    }

            jfxPanel.setScene(Scene(view))
        }
    }


}



