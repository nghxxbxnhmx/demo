import okhttp3.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


private val client = OkHttpClient()

fun fetchDhcpInfo(): String {
    val dhcpServerAddress = getDhcpServerAddress()
    if (dhcpServerAddress != null) {
        val url = "http://${dhcpServerAddress.hostAddress}/dhcp-info"
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return response.body()?.string() ?: "Unknown DHCP Offer Info"
            } else {
                return "Failed to fetch DHCP Offer Info: ${response.code()}"
            }
        } catch (e: Exception) {
            return "Exception while fetching DHCP Offer Info: ${e.message}"
        }
    } else {
        return "Unable to determine DHCP Server Address"
    }
}

fun getDhcpServerAddress(): InetAddress? {
    try {
        val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in networkInterfaces) {
            if (networkInterface.isUp) {
                val inetAddresses = Collections.list(networkInterface.inetAddresses)
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        // Assume the DHCP server is on the same subnet, modify if needed
                        return InetAddress.getByName(
                            inetAddress.hostAddress.substringBeforeLast('.') + ".1"
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


fun main() {
    val dhcpInfoFetcher : InetAddress? = getDhcpServerAddress()
    if (dhcpInfoFetcher != null) {
        println(dhcpInfoFetcher.hostAddress)
    }
}
