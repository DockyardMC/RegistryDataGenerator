package io.github.dockyardmc.registrydatagenerator

import java.io.File

object FileManager {
    val path = "./out/"
    val outFolder = File(path)

    fun delete() {
        outFolder.deleteRecursively()
        outFolder.delete()
    }

    fun create() {
        outFolder.mkdirs()
    }
}