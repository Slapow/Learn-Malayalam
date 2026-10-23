package com.example.web

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.example.model.UserStats
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Lightweight embedded HTTP server so the user can open http://<phone-ip>:8088 on their laptop
 * or share/export the standalone Cloud-Synced HTML Web App directly to their laptop.
 */
class WebCompanionServer(
    private val context: Context,
    private val getSessionSnapshot: () -> JSONObject,
    private val onRemoteSessionUpdated: (JSONObject) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    private val isRunning = AtomicBoolean(false)
    private val executor = Executors.newCachedThreadPool()

    val port: Int = 8088

    fun startServer(): String {
        if (isRunning.get()) {
            return getLocalUrl()
        }
        try {
            serverSocket = ServerSocket(port)
            isRunning.set(true)
            executor.execute {
                while (isRunning.get()) {
                    try {
                        val client = serverSocket?.accept() ?: break
                        executor.execute { handleClient(client) }
                    } catch (e: Exception) {
                        if (isRunning.get()) {
                            Log.w("WebCompanionServer", "Socket accept closed: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WebCompanionServer", "Failed to start local web server", e)
        }
        return getLocalUrl()
    }

    fun stopServer() {
        isRunning.set(false)
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
    }

    fun isServerRunning(): Boolean = isRunning.get()

    fun getLocalUrl(): String {
        val ip = getDeviceIpAddress() ?: "127.0.0.1"
        return "http://$ip:$port"
    }

    private fun getDeviceIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    private fun handleClient(socket: Socket) {
        socket.use { client ->
            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val output: OutputStream = client.getOutputStream()

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            val method = parts.getOrNull(0) ?: "GET"
            val path = parts.getOrNull(1) ?: "/"

            var contentLength = 0
            var line: String?
            while (reader.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                if (line!!.lowercase().startsWith("content-length:")) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            if (method == "GET" && path.startsWith("/api/session")) {
                val jsonBytes = getSessionSnapshot().toString().toByteArray(Charsets.UTF_8)
                sendResponse(output, "200 OK", "application/json; charset=utf-8", jsonBytes)
            } else if (method == "POST" && path.startsWith("/api/sync")) {
                val bodyChars = CharArray(contentLength)
                var totalRead = 0
                while (totalRead < contentLength) {
                    val read = reader.read(bodyChars, totalRead, contentLength - totalRead)
                    if (read == -1) break
                    totalRead += read
                }
                val bodyStr = String(bodyChars, 0, totalRead)
                try {
                    val json = JSONObject(bodyStr)
                    onRemoteSessionUpdated(json)
                } catch (_: Exception) {}
                val okBytes = """{"status":"synced"}""".toByteArray(Charsets.UTF_8)
                sendResponse(output, "200 OK", "application/json; charset=utf-8", okBytes)
            } else {
                val html = buildRenderedWebAppHtml()
                sendResponse(output, "200 OK", "text/html; charset=utf-8", html.toByteArray(Charsets.UTF_8))
            }
        }
    }

    fun buildRenderedWebAppHtml(): String {
        val rawHtml = try {
            context.assets.open("parayu_web_companion.html").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "<html><body><h1>Parayu Web Companion</h1></body></html>"
        }
        val apiKey = extractFirebaseApiKey()
        return rawHtml.replace("__FIREBASE_API_KEY__", apiKey)
    }

    private fun extractFirebaseApiKey(): String {
        return try {
            val resId = context.resources.getIdentifier("google_api_key", "string", context.packageName)
            if (resId != 0) context.getString(resId) else ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun sendResponse(
        output: OutputStream,
        status: String,
        contentType: String,
        body: ByteArray
    ) {
        val headers = "HTTP/1.1 $status\r\n" +
            "Content-Type: $contentType\r\n" +
            "Content-Length: ${body.size}\r\n" +
            "Access-Control-Allow-Origin: *\r\n" +
            "Connection: close\r\n\r\n"
        output.write(headers.toByteArray(Charsets.UTF_8))
        output.write(body)
        output.flush()
    }
}
