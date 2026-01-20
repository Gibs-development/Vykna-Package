package com.client.utilities;

import com.client.graphics.interfaces.RSInterface;

public final class InterfaceIdStartupReport {

    private InterfaceIdStartupReport() {}

    public static void printFreeRangesToConsole(int from, int to, int minLen) {
        RSInterface[] cache = RSInterface.interfaceCache;
        if (cache == null) {
            System.out.println("[ifree] interfaceCache is null (interfaces not loaded yet?)");
            return;
        }

        from = Math.max(0, from);
        to = Math.min(cache.length - 1, to);

        System.out.println("---- Free interface ID ranges (len >= " + minLen + ") ----");
        boolean any = false;

        int start = -1;
        for (int i = from; i <= to; i++) {
            boolean free = (cache[i] == null);
            if (free) {
                if (start == -1) start = i;
            } else {
                if (start != -1) {
                    int len = i - start;
                    if (len >= minLen) {
                        any = true;
                        System.out.println(start + " - " + (i - 1) + " (" + len + ")");
                    }
                    start = -1;
                }
            }
        }

        if (start != -1) {
            int len = (to + 1) - start;
            if (len >= minLen) {
                any = true;
                System.out.println(start + " - " + to + " (" + len + ")");
            }
        }

        if (!any) {
            System.out.println("(none found in " + from + ".." + to + ")");
        }
        System.out.println("----------------------------------------------");
    }
}
