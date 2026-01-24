package com.vykna.importer.core

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Tiny logger wrapper with **zero** external deps (Java 11 compatible).
 */
object Log {
    private val log: Logger = Logger.getLogger("VyknaImporter")

    fun info(msg: String) {
        log.log(Level.INFO, msg)
    }

    fun warn(msg: String) {
        log.log(Level.WARNING, msg)
    }

    fun error(msg: String, t: Throwable? = null) {
        if (t != null) log.log(Level.SEVERE, msg, t) else log.log(Level.SEVERE, msg)
    }
}
