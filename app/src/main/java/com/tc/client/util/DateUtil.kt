package com.tc.reading.util

import java.text.SimpleDateFormat
import java.util.Date

class DateUtil {

    companion object {

        // format to: 2024-03-14
        fun fmtCurrentDay(): String {
            val fmt = SimpleDateFormat("yyyy-MM-dd");
            return fmt.format(Date());
        }
    }

}