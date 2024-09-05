package jp.toastkid.yobidashi4.domain.service.archive

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipArchiver {

    operator fun invoke(paths: Collection<Path>) {
        try {
            ZipOutputStream(BufferedOutputStream(Files.newOutputStream(Path.of(DESTINATION)))).use {
                createZip(it, paths)
            }
        } catch (e: IOException) {
            // TODO LoggerFactory.getLogger(javaClass).warn("Zip error.", e)
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun createZip(zos: ZipOutputStream, paths: Collection<Path>) {
        val buffer = ByteArray(1024)
        paths.forEach { path ->
            val entry = ZipEntry(path.fileName.toString())
            entry.time = Files.getLastModifiedTime(path).toMillis()
            zos.putNextEntry(entry)
            BufferedInputStream(Files.newInputStream(path)).use { stream ->
                var len: Int
                while (stream.read(buffer).also { len = it } != -1) {
                    zos.write(buffer, 0, len)
                }
            }
        }
    }

    companion object {
        private const val DESTINATION = "articles.zip"
    }
}