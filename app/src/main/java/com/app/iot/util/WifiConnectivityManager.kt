package com.app.iot.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiConnectivityManager @Inject constructor(
    private val context: Context
) {

    /**
     * Checks if the device is currently connected to a Wi-Fi network.
     */
    fun isWifiConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Returns the IPv4 address of the Wi-Fi connection, or "0.0.0.0" if not available.
     */
    fun getWifiIpAddress(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "0.0.0.0"

        val activeNetwork = connectivityManager.activeNetwork ?: return "0.0.0.0"
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "0.0.0.0"

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return "0.0.0.0"
        }

        val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
        return linkProperties?.linkAddresses
            ?.firstOrNull { it.address is Inet4Address }
            ?.address
            ?.hostAddress
            ?: "0.0.0.0"
    }

    /**
     * Returns the SSID of the connected Wi-Fi network.
     * Note: Requires location permission and location to be enabled on many Android versions.
     */
    fun getWifiSsid(): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return "WiFi Connected"
        
        val info = wifiManager.connectionInfo
        val ssid = info.ssid
        
        return if (ssid.isNullOrEmpty() || ssid == "<unknown ssid>") {
            "WiFi Connected"
        } else {
            ssid.replace("\"", "")
        }
    }
}
