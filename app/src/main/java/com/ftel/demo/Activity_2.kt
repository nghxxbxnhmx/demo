package com.ftel.demo

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileWriter
import java.io.IOException
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class Activity_2 : AppCompatActivity() {
    private lateinit var ipConfig: TextView
    private lateinit var connectivityManager: ConnectivityManager

    class Activity_2 : Application() {
        companion object {
            lateinit var instance: Activity_2
                private set
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_2)

        ipConfig = findViewById(R.id.ipConfigTextView)
        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        lifecycleScope.launch {
            try {
                val resultStringBuilder = StringBuilder()

                // Nhóm 1: Mạng và Địa chỉ IP
                val ipInfo = withContext(Dispatchers.IO) {
                    val ipAddress = getIPv4Address()
                    val subnetMask = getSubnetMask()
                    val defaultGateway = getDefaultGateway()
                    val physicalAddress = getPhysicalAddress()

                    resultStringBuilder.append(
                        """
                        Nhóm 1: Mạng và Địa chỉ IP
                        1.  IPv4 Address: $ipAddress
                        2.  Subnetmask: $subnetMask
                        3.  Default Gateway: $defaultGateway
                        4.  Physical Address: $physicalAddress
                        ----------------------------------------
                    
                    """.trimIndent()
                    )
                }

                // Nhóm 2: Truyền tin và Ping
                val host = "youtube.com"
                val tcpPingResult = withContext(Dispatchers.IO) {
                    doTcpPing(host, 80)
                }

                resultStringBuilder.append(
                    """
                    Nhóm 2: Truyền tin và Ping
                    1.  IP Destination: $host
                    2.  Bytes: Unknown
                    3.  Time: Unknown
                    4.  TTL: Unknown
                    5.  Status: $tcpPingResult
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 3: Trace Route
                val traceRouteInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin Trace Route
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 3: Trace Route
                    1.  IP address: A.B.C.D
                    2.  Time: T
                    3.  Status: $traceRouteInfo
                    4.  Hop: X
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 4: Kiểm tra Port
                val portInfo = withContext(Dispatchers.IO) {
                    checkPort("example.com", 80, 5000) // Thay thế bằng đối số thích hợp
                }

                resultStringBuilder.append(
                    """
                    Nhóm 4: Kiểm tra Port
                    1.  IP Addresss: Unknown
                    2.  Port: Unknown
                    3.  Protocol: Unknown
                    4.  Time: Unknown
                    5.  Status: $portInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 5: Kiểm tra DNS
                val dnsInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin DNS
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 5: Kiểm tra DNS
                    1.  Non-authoritative answer: $dnsInfo
                    2.  Authoritative answer: $dnsInfo
                    3.  Server: $dnsInfo
                    4.  Address: $dnsInfo
                    5.  Name: $dnsInfo
                    6.  Aliases: $dnsInfo
                    7.  Timeout: $dnsInfo
                    8.  Server can't find: $dnsInfo
                    9.  Time: $dnsInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 6: Kiểm tra Kết nối Internet
                val internetConnectionInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin kết nối Internet
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 6: Kiểm tra Kết nối Internet
                    1.  IP address or Hostname: Unknown
                    2.  Port: Unknown
                    3.  Status: $internetConnectionInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 7: Kiểm tra Mạng và Tốc độ
                val networkSpeedInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin tốc độ mạng
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 7: Kiểm tra Mạng và Tốc độ
                    1.  Ping: Unknown
                    2.  Download Speed: Unknown
                    3.  Upload Speed: Unknown
                    4.  Jitter: Unknown
                    5.  Server Location: Unknown
                    6.  ISP: Unknown
                    7.  IP Address: Unknown
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 8: Thông tin WiFi
                val wifiInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin Wi-Fi
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 8: Thông tin WiFi
                    1.  SSID: $wifiInfo
                    2.  Channel: $wifiInfo
                    3.  Signal: $wifiInfo
                    4.  Chane With: $wifiInfo
                    5.  Security Type: $wifiInfo
                    6.  Encryption Type: $wifiInfo
                    7.  802.11.mode: $wifiInfo
                    8.  Download Rate: $wifiInfo
                    9.  Upload Rate: $wifiInfo
                    10.  Ping: $wifiInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 9: Quản lý Mạng và Giao thức
                val networkProtocolInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin giao thức mạng
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 9: Quản lý Mạng và Giao thức
                    1.  IP Source: $networkProtocolInfo
                    2.  IP Destination: $networkProtocolInfo
                    3.  Mac Source: $networkProtocolInfo
                    4.  Mac Destination: $networkProtocolInfo
                    5.  Protocol: $networkProtocolInfo
                    6.  Info DHCP Offer: $networkProtocolInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Nhóm 10: Tốc độ và Tín hiệu WiFi
                val wifiSpeedInfo = withContext(Dispatchers.IO) {
                    // Thực hiện logic để lấy thông tin tốc độ và tín hiệu Wi-Fi
                    "Unknown"
                }

                resultStringBuilder.append(
                    """
                    Nhóm 10: Tốc độ và Tín hiệu WiFi
                    1.  Wifi Link Speed: $wifiSpeedInfo
                    2.  SNR: $wifiSpeedInfo
                    3.  Signal Strength: $wifiSpeedInfo
                    ----------------------------------------
                    
                """.trimIndent()
                )

                // Gán giá trị cho TextView hoặc làm cái gì đó với kết quả
                ipConfig.text = resultStringBuilder.toString()
            } catch (e: IOException) {
                handleException("An unexpected error occurred: ${e.message}")
            }
        }
    }

    private fun handleException(message: String) {
        ipConfig.text = message
    }

    private fun getIPv4Address(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(":") == -1) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    private fun getDefaultGateway(): String {
        return "Unknown"
    }

    private fun getSubnetMask(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                for (interfaceAddress in networkInterface.interfaceAddresses) {
                    val inetAddress = interfaceAddress.address
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(":") == -1) {
                        val subnetPrefixLength = interfaceAddress.networkPrefixLength
                        return calculateSubnetMask(subnetPrefixLength)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    private fun calculateSubnetMask(subnetPrefixLength: Short): String? {
        val subnetMask = -0x1 shl 32 - subnetPrefixLength
        return String.format(
            "%d.%d.%d.%d",
            subnetMask shr 24 and 255,
            subnetMask shr 16 and 255,
            subnetMask shr 8 and 255,
            subnetMask and 255
        )
    }

    private fun getPhysicalAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val mac = networkInterface.hardwareAddress
                if (mac != null && mac.size == 6) {
                    val macAddress = StringBuilder()
                    for (i in mac.indices) {
                        macAddress.append(
                            String.format(
                                "%02X%s",
                                mac[i],
                                if (i < mac.size - 1) "-" else ""
                            )
                        )
                    }
                    return macAddress.toString()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    private suspend fun doTcpPing(host: String, port: Int): String {
        return withContext(Dispatchers.IO) {
            val resultStringBuilder = StringBuilder()
            val numPackets = 5

            for (i in 0 until numPackets) {
                val startTime = System.currentTimeMillis()
                val isReachable = tcpPing(host, port)
                val endTime = System.currentTimeMillis()

                if (isReachable) {
                    val roundTripTime = endTime - startTime
                    resultStringBuilder.append("Ping successful. Time: $roundTripTime ms\n")
                } else {
                    resultStringBuilder.append("Ping failed.\n")
                }
            }

            // Returning the result as a String
            resultStringBuilder.toString()
        }
    }

    private fun tcpPing(host: String?, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                // Set a timeout for the socket connection
                socket.connect(InetSocketAddress(host, port), 5000) // 5-second timeout
                true
            }
        } catch (e: SocketTimeoutException) {
            false
        } catch (e: IOException) {
            false
        }
    }

    private fun checkPort(host: String, port: Int, timeout: Int): String {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout)
                "Open"
            }
        } catch (e: UnknownHostException) {
            "Unknown Host"
        } catch (e: SocketTimeoutException) {
            "Timeout"
        } catch (e: IOException) {
            val message = e.message
            when {
                message?.contains("Connection refused") == true -> "Refused"
                message?.contains("No route to host") == true -> "Filtered"
                else -> "Closed"
            }
        }
    }

    private fun getWifiInfo(): String {
        val wifiInfoStringBuilder = StringBuilder()

        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }

        if (networkCapabilities != null) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // Kết nối qua Wi-Fi
                val downloadSpeed = getDownloadSpeed()
                val uploadSpeed = getUploadSpeed()

                wifiInfoStringBuilder.append(
                    "Connected to Wi-Fi\n" +
                            "Download Rate: $downloadSpeed Mbps\n" +
                            "Upload Rate: $uploadSpeed Mbps\n"
                )
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                // Kết nối qua mạng di động
                wifiInfoStringBuilder.append("Connected to Cellular network\n")
            }
            // Các loại kết nối khác có thể được xử lý ở đây
        } else {
            wifiInfoStringBuilder.append("Not connected to any network")
        }

        return wifiInfoStringBuilder.toString()
    }

    private fun getDownloadSpeed(): Int {
        // Thực hiện logic để lấy tốc độ download
        // Đây chỉ là ví dụ, bạn cần thay thế bằng cách phù hợp với ứng dụng của bạn
        return 10 // Giả sử tốc độ là 10 Mbps
    }

    private fun getUploadSpeed(): Int {
        // Thực hiện logic để lấy tốc độ upload
        // Đây chỉ là ví dụ, bạn cần thay thế bằng cách phù hợp với ứng dụng của bạn
        return 5 // Giả sử tốc độ là 5 Mbps
    }

    fun getCurrentSsid(context: Context): String? {
        var ssid: String? = null
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfo!!.isConnected) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo: WifiInfo? = wifiManager.connectionInfo
            if (connectionInfo != null) {
                ssid = connectionInfo.getSSID()
            }
        }
        return ssid
    }
}