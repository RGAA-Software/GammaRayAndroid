package com.tc.client

class Result<T>(c: Int, t: T) {
    var code = 0;
    var value = t

    companion object {
        val OK = 0
        val ERR = -1;

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