package com.wix.reactnativeuilib.keyboardinput.utils;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.wix.reactnativeuilib.keyboardinput.ReactContextHolder;

public class RuntimeUtils {

    // TODO Switch to GuardedRunnable when upgrading RN's minimal ver
    private static final Runnable sUIUpdateClosure = new Runnable() {
        @Override
        public void run() {
            try {
                ReactContext context = ReactContextHolder.getContext();
                if (context != null) {
                    UIManagerModule uiManager = context.getNativeModule(UIManagerModule.class);
                    if (uiManager != null) {
                        uiManager.onBatchComplete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static void runOnUIThread(Runnable runnable) {
        try {
            if (ReactContextHolder.getContext() != null) {
                ReactContextHolder.getContext().runOnUiQueueThread(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dispatchUIUpdates(final Runnable userRunnable) {
        if (ReactContextHolder.getContext() == null) {
            return; // Skip if context is null
        }
        
        try {
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Re-check context before running user code
                        if (ReactContextHolder.getContext() != null) {
                            userRunnable.run();
                            
                            // Get a fresh context reference before queue operation
                            ReactContext context = ReactContextHolder.getContext();
                            if (context != null) {
                                context.runOnNativeModulesQueueThread(sUIUpdateClosure);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
