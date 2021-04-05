package com.justinblank.coverage.examples;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.quicktheories.coverage.ClassloaderByteArraySource;
import org.quicktheories.coverage.Installer;

public class Loader {

    private static volatile boolean loaded = false;

    public static void init() {
        if (!loaded) {
            synchronized (Loader.class) {
                if (!loaded) {
                    try {
                        Installer in = new Installer(new ClassloaderByteArraySource(Thread.currentThread().getContextClassLoader()));
                        ByteBuddyAgent.attach(in.createJar(), ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    // TODO: Decide what would make sense here
                    loaded = true;
                }
            }
        }

    }
}
