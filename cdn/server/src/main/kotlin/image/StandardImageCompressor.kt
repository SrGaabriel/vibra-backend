package io.github.vibraplatform.cdn.server.image

import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

class StandardImageCompressor: ImageCompressor {
    override fun compress(inputStream: InputStream, quality: Float): ByteArrayOutputStream {
        val bufferedImage = ImageIO.read(inputStream)
        val writers = ImageIO.getImageWritersByFormatName("jpg")

        if (!writers.hasNext()) throw IllegalStateException("No writers found")

        val writer = writers.next()
        val byteArrayOutputStream = ByteArrayOutputStream()
        val imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)
        writer.output = imageOutputStream

        val imageWriteParam = writer.defaultWriteParam
        imageWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
        imageWriteParam.compressionQuality = quality

        writer.write(null, IIOImage(bufferedImage, null, null), imageWriteParam)

        imageOutputStream.close()
        writer.dispose()

        return byteArrayOutputStream
    }
}