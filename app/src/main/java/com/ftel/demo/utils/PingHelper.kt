package com.ftel.demo.utils

import com.ftel.demo.dto.PingResult
import java.io.IOException
import java.net.InetAddress

import java.net.InetSocketAddress

class PingHelper(private val ipAddress: String) {
    fun sendPing(ttl: Int, timeout: Int, packetSize: Int) : PingResult {
        val result = PingResult()

        try {
            val address: InetAddress = InetAddress.getByName(ipAddress)
            val startTime = System.currentTimeMillis()
            val reachable = try {
                val socket = java.net.Socket()
                socket.connect(InetSocketAddress(address, 80), timeout)
                socket.close()
                true
            } catch (e : IOException) {
                false
            }
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime

            result.ipAddress = address.hostAddress
            result.replyStatus = if (reachable) "Success" else "Timeout"
            result.bytesSent = packetSize
            result.responseTime = responseTime
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }
}