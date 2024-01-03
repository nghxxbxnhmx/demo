package com.ftel.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class IpConfigHelper {
    public static String getIPAddress(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface intf = interfaces.nextElement();
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if ((useIPv4 && addr instanceof Inet4Address) || (!useIPv4 && addr instanceof Inet6Address)) {
                            return sAddr.toUpperCase(Locale.getDefault());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("IpConfigHelper", "Lỗi khi lấy địa chỉ IP", e);
        }
        return null;
    }

    // Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask - Start Subnet Mask
    public static String getSubnetMask() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface intf = interfaces.nextElement();
                if (intf.isUp() && !intf.isLoopback()) {
                    for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                        if (addr instanceof Inet4Address) {
                            int prefixLength = getIpv4PrefixLength(intf);
                            if (prefixLength >= 0 && prefixLength <= 32) {
                                return convertPrefixLengthToSubnetMask((short) prefixLength);
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private static String convertPrefixLengthToSubnetMask(short prefixLength) {
        int netmask = 0xffffffff << (32 - prefixLength);
        return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                (netmask >> 24) & 0xff, (netmask >> 16) & 0xff, (netmask >> 8) & 0xff, netmask & 0xff);
    }

    // End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask - End Subnet Mask
    private static int getIpv4PrefixLength(NetworkInterface networkInterface) {
        List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
        for (InterfaceAddress address : addresses) {
            InetAddress inetAddress = address.getAddress();
            if (inetAddress instanceof Inet4Address) {
                return address.getNetworkPrefixLength();
            }
        }
        return -1;
    }

    public static String getDefaultGateway() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface intf = interfaces.nextElement();
                if (intf.isUp() && !intf.isLoopback()) {
                    String gateway = findDefaultGateway(intf);
                    if (gateway != null) {
                        return gateway;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private static String findDefaultGateway(NetworkInterface networkInterface) {
        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress address = interfaceAddress.getAddress();
            if (address instanceof Inet4Address) {
                String networkAddress = calculateNetworkAddress(address, interfaceAddress.getNetworkPrefixLength());
                return networkAddress.substring(0, networkAddress.lastIndexOf(".")) + ".1";
            }
        }
        return null;
    }

    private static String calculateNetworkAddress(InetAddress address, short prefixLength) {
        byte[] ipBytes = address.getAddress();
        int ipInt = ((ipBytes[0] & 0xFF) << 24) |
                ((ipBytes[1] & 0xFF) << 16) |
                ((ipBytes[2] & 0xFF) << 8) |
                (ipBytes[3] & 0xFF);

        int networkInt = ipInt & (0xFFFFFFFF << (32 - prefixLength));

        int gatewayInt = networkInt + 1;

        byte[] networkBytes = new byte[4];
        networkBytes[0] = (byte) ((gatewayInt & 0xFF000000) >>> 24);
        networkBytes[1] = (byte) ((gatewayInt & 0x00FF0000) >>> 16);
        networkBytes[2] = (byte) ((gatewayInt & 0x0000FF00) >>> 8);
        networkBytes[3] = (byte) (gatewayInt & 0x000000FF);

        try {
            return InetAddress.getByAddress(networkBytes).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address - Start Mac Address
    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } else {
            return "Unknown";
        }
    }

    public static String getSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        } else {
            return "Unknown";
        }
    }

    public static String getBSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getBSSID();
        } else {
            return "Unknown";
        }
    }

    public static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled() ? wifiManager.getConnectionInfo() : null;
    }
}
