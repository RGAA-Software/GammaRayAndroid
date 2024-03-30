package com.tc.client.steam

class SteamApp {
    var appId: Int = 0;
    var appName: String = "";
    var coverName: String = "";
    var engine: String = "";

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return appId == (other as SteamApp).appId;
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return appId.hashCode()
    }
}
