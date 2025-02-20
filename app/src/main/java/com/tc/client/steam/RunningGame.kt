package com.tc.client.steam

class RunningGame {
    var gameId: Int = 0
    var gameExes: String = ""

    fun isSteamGame(): Boolean {
        return gameId > 0
    }

    override fun toString(): String {
        return "RunningGame(gameId=$gameId, gameExes='$gameExes')"
    }

}