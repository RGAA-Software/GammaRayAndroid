package com.tc.client

public class ScanInfo {

    class IpInfo {
        var ip: String = ""
        var type: String = ""

        override fun toString(): String {
            return "IpInfo(ip='$ip', type='$type')"
        }
    }

    var sysUniqueId: String = ""

    var httpServerPort: Int = 0

    var wsServerPort: Int = 0

    var udpServerPort: Int = 0

    var ipInfo: MutableList<IpInfo> = mutableListOf<IpInfo>()

    var targetIp: String = ""
    var targetIpType: String = ""

    fun valid(): Boolean {
        return sysUniqueId.isNotEmpty() && httpServerPort > 0 && wsServerPort > 0 && udpServerPort > 0;
    }

    fun validIp(): Boolean {
        return targetIp.isNotEmpty();
    }

    override fun toString(): String {
        return "ScanInfo(sysUniqueId='$sysUniqueId', httpServerPort=$httpServerPort, wsServerPort=$wsServerPort, udpServerPort=$udpServerPort, ipInfo=$ipInfo)"
    }

}