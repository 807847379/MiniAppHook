#!/bin/bash
# MiniAppHook - 部署到 GitHub 并编译

echo "=========================================="
echo "MiniAppHook 部署脚本"
echo "=========================================="

# 检查 git
if ! command -v git &> /dev/null; then
    echo "错误: 需要安装 Git"
    echo "下载地址: https://git-scm.com/download/win"
    exit 1
fi

# 检查 GitHub CLI
if ! command -v gh &> /dev/null; then
    echo "警告: 未安装 GitHub CLI"
    echo "你可以手动操作:"
    echo "1. 访问 https://github.com/new 创建仓库"
    echo "2. 上传 MiniAppHook 文件夹内容"
    echo "3. 访问 Actions 页面触发构建"
    exit 1
fi

# 创建仓库
echo ""
echo "步骤 1: 创建 GitHub 仓库..."
gh repo create MiniAppHook --private --source=. --push

echo ""
echo "=========================================="
echo "完成!"
echo "=========================================="
echo ""
echo "下一步:"
echo "1. 访问 https://github.com/YOUR_USERNAME/MiniAppHook/actions"
echo "2. 点击 'I understand my workflows, go ahead and enable them'"
echo "3. 等待构建完成（约 5-10 分钟）"
echo "4. 下载 Artifacts 中的 APK"
