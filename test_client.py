#!/usr/bin/env python3
"""
MiniAppHook 测试客户端

用法:
    python test_client.py status
    python test_client.py click 发送
    python test_client.py page
    python test_client.py input "#input" "hello"
"""

import requests
import sys

BASE_URL = "http://127.0.0.1:8888"


def get_status():
    """获取模块状态"""
    resp = requests.get(f"{BASE_URL}/status")
    print(f"状态: {resp.json()}")


def click_button(text):
    """点击按钮"""
    resp = requests.post(
        f"{BASE_URL}/click",
        json={"text": text}
    )
    result = resp.json()
    print(f"点击结果: {result}")
    return result.get("success", False)


def input_text(selector, text):
    """输入文本"""
    resp = requests.post(
        f"{BASE_URL}/input",
        json={"selector": selector, "text": text}
    )
    result = resp.json()
    print(f"输入结果: {result}")
    return result.get("success", False)


def get_page_info():
    """获取页面信息"""
    resp = requests.get(f"{BASE_URL}/page")
    print(f"页面信息: {resp.json()}")


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)

    cmd = sys.argv[1].lower()

    if cmd == "status":
        get_status()
    elif cmd == "click":
        if len(sys.argv) < 3:
            print("用法: python test_client.py click <按钮文字>")
            sys.exit(1)
        click_button(sys.argv[2])
    elif cmd == "page":
        get_page_info()
    elif cmd == "input":
        if len(sys.argv) < 4:
            print("用法: python test_client.py input <选择器> <文本>")
            sys.exit(1)
        input_text(sys.argv[2], sys.argv[3])
    else:
        print(f"未知命令: {cmd}")
        print(__doc__)
        sys.exit(1)


if __name__ == "__main__":
    main()
