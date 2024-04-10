package com.tc.client

class ServerApi {

    companion object {
        const val ping = "/v1/ping";
        const val apis = "/v1/apis";
        const val apps = "/v1/apps";
        const val startApp = "/v1/start/app";
        const val stopApp = "/v1/stop/app";
        const val queryApp = "/v1/query/app"
    }

}