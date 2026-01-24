package com.vykna.importer

import com.vykna.importer.ui.MainWindow
import javafx.application.Application
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(primaryStage: Stage) {
        MainWindow().start(primaryStage)
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}
