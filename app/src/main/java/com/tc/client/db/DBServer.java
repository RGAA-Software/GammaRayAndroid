package com.tc.client.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class DBServer {
    @Id(autoincrement = true)
    public long id;
    @Unique
    public String serverId;
    public String serverName;
    public String serverIp;
    public String serverVersion;
    public int httpServerPort;
    public int wsServerPort;
    public int udpCastServerPort;
    public String coverUrl;

    @Generated(hash = 249968845)
    public DBServer(long id, String serverId, String serverName, String serverIp,
            String serverVersion, int httpServerPort, int wsServerPort,
            int udpCastServerPort, String coverUrl) {
        this.id = id;
        this.serverId = serverId;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.serverVersion = serverVersion;
        this.httpServerPort = httpServerPort;
        this.wsServerPort = wsServerPort;
        this.udpCastServerPort = udpCastServerPort;
        this.coverUrl = coverUrl;
    }

    @Generated(hash = 2091586576)
    public DBServer() {
    }

    public static DBServer create(String serverId) {
        DBServer s = new DBServer();
        s.serverId = serverId;
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBServer dbServer = (DBServer) o;
        return Objects.equals(serverId, dbServer.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public int getHttpServerPort() {
        return this.httpServerPort;
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public int getWsServerPort() {
        return this.wsServerPort;
    }

    public void setWsServerPort(int wsServerPort) {
        this.wsServerPort = wsServerPort;
    }

    public int getUdpCastServerPort() {
        return this.udpCastServerPort;
    }

    public void setUdpCastServerPort(int udpCastServerPort) {
        this.udpCastServerPort = udpCastServerPort;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

     
}
