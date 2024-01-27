package com.tc.client;

import java.io.Closeable;
import java.io.IOException;

public class Closer {

    public static void close(Closeable c) {
        if (c  != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
