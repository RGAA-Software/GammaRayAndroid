package com.tc.client.db

import android.content.Context

class DBManager(val context: Context) {

    companion object {
        const val DB_NAME = "tc_game.db";
    }

    public var daoMaster: DaoMaster = DaoMaster(DaoMaster.DevOpenHelper(context, DB_NAME, null).writableDatabase)
    public var daoSession: DaoSession = daoMaster.newSession();

    fun close() {
        daoSession.clear();
    }

    fun insertOrUpdateServer(s: DBServer) {
        val servers = daoSession.queryBuilder(DBServer::class.java)
            .where(DBServerDao.Properties.ServerId.eq(s.serverId)).list()
        if (servers.isNotEmpty()) {
            // update
            s.id = servers[0].id;
            daoSession.dbServerDao.update(s)
        } else {
            // insert
            daoSession.dbServerDao.insert(s)
        }
    }

    fun queryServers(): List<DBServer> {
        return daoSession.dbServerDao.loadAll()
    }

    fun deleteServer(dbServer: DBServer) {
        daoSession.dbServerDao.delete(dbServer)
    }
}