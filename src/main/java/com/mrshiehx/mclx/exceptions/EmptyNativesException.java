package com.mrshiehx.mclx.exceptions;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.mrshiehx.mclx.MinecraftLauncherX.getString;

public class EmptyNativesException extends LaunchException{
    public final JSONArray libraries;
    public EmptyNativesException(@Nullable JSONArray libraries) {
        super(getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"));
        this.libraries=libraries;
    }
}
