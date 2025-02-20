package com.tc.client

import com.tc.client.db.DBServer

public class ScanInfo {

    class IpInfo {
        var ip: String = ""
        var type: String = ""

        override fun toString(): String {
            return "IpInfo(ip='$ip', type='$type')"
        }
    }

    var sysUniqueId: String = ""

    var iconIndex: Int = 0

    var httpServerPort: Int = 0

    var wsServerPort: Int = 0

    var udpServerPort: Int = 0

    var streamWsPort: Int = 0

    var ipInfo: MutableList<IpInfo> = mutableListOf<IpInfo>()

    var targetIp: String = ""
    var targetIpType: String = ""

    fun valid(): Boolean {
        return sysUniqueId.isNotEmpty() && httpServerPort > 0 && wsServerPort > 0 && udpServerPort > 0;
    }

    fun canConnect(): Boolean {
        return targetIp.isNotEmpty();
    }

    fun asDBServer(): DBServer {
        val s = DBServer();
        s.serverId = this.sysUniqueId
        s.iconIndex = this.iconIndex
        s.serverName = ""
        s.serverIp = this.targetIp
        s.serverVersion = ""
        s.httpServerPort = this.httpServerPort
        s.wsServerPort = this.wsServerPort
        s.udpCastServerPort = this.udpServerPort
        s.streamWsPort = this.streamWsPort
        s.coverUrl = ""
        return s;
    }

    fun hasTargetIp(ip: String): Boolean {
        ipInfo.forEach {
            if (it.ip == ip) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "ScanInfo(sysUniqueId='$sysUniqueId', httpServerPort=$httpServerPort, wsServerPort=$wsServerPort, udpServerPort=$udpServerPort, ipInfo=$ipInfo)"
    }

}