package com.tc.client.steam

class SteamGame : Comparable<SteamGame> {
    var gameId: Int = 0;
    var gameName: String = "";
    var coverName: String = "";
    var engine: String = "";
    var exePath: String = "";
    var gameTag: Int = TAG_IDLE;

     companion object {

         const val TAG_PRESET = 0;
         const val TAG_RUNNING = 1;
         const val TAG_IDLE = 2;

         fun create(appId: Int, appName: String, tag: Int): SteamGame {
             val app = SteamGame();
             app.gameId = appId;
             app.gameName = appName;
             app.gameTag = tag
             return app;
         }
     }

    override fun compareTo(other: SteamGame): Int {
        return this.gameTag - other.gameTag
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
