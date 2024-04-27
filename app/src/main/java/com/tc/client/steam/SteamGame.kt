package com.tc.client.steam

class SteamGame {
    var gameId: Int = 0;
    var gameName: String = "";
    var coverName: String = "";
    var engine: String = "";
    var exePath: String = "";

     companion object {
         fun create(appId: Int, appName: String): SteamGame {
             val app = SteamGame();
             app.gameId = appId;
             app.gameName = appName;
             return app;
         }
     }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return gameId == (other as SteamGame).gameId;
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return gameId.hashCode()
    }

    fun getGamePath(): String {
        if (gameId > 0) {
            if (gameId == 2) {
                return "steam://open/bigpicture";
            }
            return "steam://rungameid/${gameId}"
        }
        return exePath
    }
}
