package com.justinblank.examples;

public class Example {

    // There is no complex theory, I just threw out a bunch of booleans
    public static boolean exampleTest(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
        if (a) {
            if (b && c) {
                if (!d) {
                    if (e || f) {
                        if (g) {
                            if (h) {
                                return true;
                            }
                        }
                    }
                }
                if (g && h) {
                    return true;
                }
            }
        }
        return false;
    }



    public static boolean collatz(int i) {
        while (i != 1) {
            if (i % 2 == 0) {
                i /= 2;
            }
            else {
                i = i * 3 + 1;
            }
        }
        return true;
    }
}
