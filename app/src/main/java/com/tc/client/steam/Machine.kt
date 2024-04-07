package com.tc.client.steam

import android.text.TextUtils

class Machine {

    var id: String = "";
    var name: String = "";
    var cover: String = "";

    companion object {
        fun create(id: String, name: String): Machine {
            val app = Machine();
            app.id = id;
            app.name = name;
            return app;
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return id == (other as Machine).id;
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return if (TextUtils.isEmpty(id)) super.hashCode() else id.hashCode();
    }
}