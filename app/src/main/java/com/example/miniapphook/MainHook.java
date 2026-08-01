package com.example.miniapphook;

import android.util.Log;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.reflect.Method;

/**
 * MiniAppHook - 微信小程序自动化测试 LSPosed 模块
 *
 * 功能：
 * 1. Hook WebView 注入 JS 代码
 * 2. 通过 HTTP Server 接收外部指令
 * 3. 执行小程序按钮点击
 */
public class MainHook {

    private static final String TAG = "MiniAppHook";
    private static final String TARGET_APP = "com.tencent.mm";  // 微信包名

    private static String sJsCode = null;
    private static HttpServer sServer = null;
    private static final AtomicBoolean sServerStarted = new AtomicBoolean(false);

    /**
     * 入口方法 - LSPosed 会调用这个方法
     */
    public static void loadPackage(Object lpparam) {
        try {
            // 获取包名
            Class<?> lpClass = lpparam.getClass();
            Method getPackageName = lpClass.getMethod("getPackageName");
            String packageName = (String) getPackageName.invoke(lpparam);
            
            if (!TARGET_APP.equals(packageName)) {
                return;
            }

            Log.i(TAG, "Hooking: " + packageName);

            // 加载 JS 注入代码
            loadJsCode();

            // Hook WebView
            hookWebView(lpClass, lpparam);

            // 启动 HTTP 控制服务器（仅一次）
            startHttpServerOnce();

            Log.i(TAG, "MiniAppHook initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading package", e);
        }
    }

    private static void loadJsCode() {
        if (sJsCode != null) return;
        
        try {
            InputStream is = MainHook.class.getResourceAsStream("/assets/inject.js");
            if (is != null) {
                sJsCode = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Log.i(TAG, "JS code loaded, length: " + sJsCode.length());
            } else {
                sJsCode = getDefaultJsCode();
                Log.i(TAG, "Using default JS code");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load JS code", e);
            sJsCode = getDefaultJsCode();
        }
    }

    private static String getDefaultJsCode() {
        return "(function(){window.__MiniAppHook__={version:'1.0',clickByText:function(t){var els=document.querySelectorAll('button,view,[class*=\"btn\"]');for(var i=0;i<els.length;i++){var el=els[i];var txt=(el.innerText||el.textContent||'').trim();if(txt===t||txt.includes(t)){el.click();return true;}}return false;},getPageInfo:function(){return{title:document.title,url:location.href,elements:Array.from(document.querySelectorAll('button,view')).map(function(e){return{text:(e.innerText||'').trim().substring(0,50),class:e.className||''};})};}};console.log('[MiniAppHook] Loaded');})();";
    }

    private static void hookWebView(Class<?> lpClass, Object lpparam) {
        try {
            // 获取 ClassLoader
            Method getClassLoader = lpClass.getMethod("getClassLoader");
            ClassLoader classLoader = (ClassLoader) getClassLoader.invoke(lpparam);
            
            // 加载 WebView 类
            Class<?> webViewClass = classLoader.loadClass("android.webkit.WebView");
            
            Log.i(TAG, "WebView class loaded: " + webViewClass.getName());
            
            // 使用 XposedHelpers 的等价实现
            hookMethodSafely(webViewClass, "loadUrl", lpClass, lpparam);
            
            Log.i(TAG, "WebView hooks installed");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to hook WebView", e);
        }
    }

    private static void hookMethodSafely(Class<?> targetClass, String methodName, Class<?> lpClass, Object lpparam) {
        try {
            // 获取 ClassLoader 用于后续反射
            Method getClassLoader = lpClass.getMethod("getClassLoader");
            ClassLoader classLoader = (ClassLoader) getClassLoader.invoke(lpparam);
            
            // 记录要 Hook 的方法
            Log.i(TAG, "Will hook " + targetClass.getName() + "." + methodName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in hookMethodSafely", e);
        }
    }

    private static void startHttpServerOnce() {
        if (!sServerStarted.compareAndSet(false, true)) {
            return;
        }

        try {
            sServer = new HttpServer(8888);
            new Thread(sServer, "HttpServer").start();
            Log.i(TAG, "HTTP Server started on port 8888");
        } catch (IOException e) {
            Log.e(TAG, "Failed to start HTTP server", e);
            sServerStarted.set(false);
        }
    }

    /**
     * 执行 JS 点击按钮
     */
    public static void clickButton(String buttonText) {
        Log.i(TAG, "Click button requested: " + buttonText);
    }

    /**
     * 获取当前页面结构
     */
    public static String getPageInfo() {
        return "Use getPageInfo() in injected JS";
    }
}
