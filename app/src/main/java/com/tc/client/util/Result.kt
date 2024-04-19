package com.tc.client.util

class Result<T>(c: Int, t: T) {
    var code = c;
    var value = t

    companion object {
        const val OK = 0
        const val ERR = -1;

        fun error(): Result<Int> {
            return Result(ERR, -1);
        }

        fun error(msg: String): Result<String> {
            return Result(ERR, msg);
        }
    }

    fun ok(): Boolean {
        return code == OK;
    }
}