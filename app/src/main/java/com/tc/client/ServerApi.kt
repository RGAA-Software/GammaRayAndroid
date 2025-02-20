package com.tc.client

class ServerApi {

    companion object {
        const val API_PING = "/v1/ping";
        const val API_SUPPORTED_APIS = "/v1/apis";
        const val API_GAMES = "/v1/games";
        const val API_START_GAME = "/v1/game/start";
        const val API_STOP_GAME = "/v1/game/stop";
        const val API_RUNNING_GAMES = "/v1/running/games";
        const val API_STOP_SERVER = "/v1/stop/server"
        const val API_RUNNING_PROCESSES = "/v1/all/running/processes"
        const val API_KILL_PROCESS = "/v1/kill/process"
        const val API_SIMPLE_INFO = "/v1/simple/info"
    }

}