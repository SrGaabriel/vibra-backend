package io.github.vibraplatform.cdn.server.image

import java.io.ByteArrayOutputStream
import java.io.InputStream

interface ImageCompressor {
    fun compress(inputStream: InputStream, quality: Float): ByteArrayOutputStream
}