package org.godotengine.godot;

public class GodotCallbacksUtils {

    public static final String SIGNIN_SUCCESSFUL = "_on_sign_in_success";
    public static final String SIGNIN_FAILED = "_on_sign_in_failed";
    public static final String SIGN_OUT_SUCCESS = "_on_sign_out_success";
    public static final String SIGN_OUT_FAILED = "_on_sign_out_failed";

    public void invokeGodotCallback(int instanceId, String callbackName, Object[] args) {
        GodotLib.calldeferred(instanceId, callbackName, args);
    }
}
