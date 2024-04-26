package com.tc.client

class ServerApi {

    companion object {
        const val ping = "/v1/ping";
        const val supportedApis = "/v1/apis";
        const val games = "/v1/games";
        const val gameStart = "/v1/game/start";
        const val gameStop = "/v1/game/stop";
    }

}