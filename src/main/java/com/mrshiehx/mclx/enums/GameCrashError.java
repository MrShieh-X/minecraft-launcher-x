package com.mrshiehx.mclx.enums;

import static com.mrshiehx.mclx.MinecraftLauncherX.*;

public enum GameCrashError {
    URLClassLoader(getString("MESSAGE_GAME_CRASH_CAUSE_URLCLASSLOADER")),
    LWJGLFailedLoad(getString("MESSAGE_GAME_CRASH_CAUSE_LWJGL_FAILED_LOAD")),
    MemoryTooSmall(getString("MESSAGE_GAME_CRASH_CAUSE_MEMORY_TOO_SMALL"));
    public final String cause;

    GameCrashError(String cause) {
        this.cause = cause;
    }
}
