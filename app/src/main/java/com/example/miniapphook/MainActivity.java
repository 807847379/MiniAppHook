package com.example.miniapphook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * MiniAppHook 主界面
 * 
 * 本模块主要用于后台服务，不需要复杂的 UI
 * 此 Activity 仅作为占位符，用于满足 AndroidManifest 要求
 */
public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 创建简单的文本视图
        TextView textView = new TextView(this);
        textView.setText("MiniAppHook LSPosed Module\n\n" +
                         "模块已激活\n" +
                         "请打开微信使用自动化功能\n\n" +
                         "HTTP 服务端口: 8888");
        textView.setTextSize(18);
        setContentView(textView);
    }
}
