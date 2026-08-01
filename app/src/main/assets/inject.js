/**
 * MiniAppHook - 微信小程序注入脚本
 *
 * 功能：
 * 1. 暴露全局方法供外部调用
 * 2. 获取页面元素信息
 * 3. 执行按钮点击
 * 4. 监听页面事件
 */

(function() {
    'use strict';

    // 防止重复注入
    if (window.__MiniAppHook__) {
        console.log('[MiniAppHook] Already injected');
        return;
    }

    window.__MiniAppHook__ = {
        version: '1.0'
    };

    /**
     * 根据文字查找并点击按钮
     * @param {string} text - 按钮文字
     * @returns {boolean} - 是否成功
     */
    window.__MiniAppHook__.clickByText = function(text) {
        console.log('[MiniAppHook] Click by text:', text);

        // 1. 优先查找 button 元素
        var buttons = document.querySelectorAll('button');
        for (var i = 0; i < buttons.length; i++) {
            var btn = buttons[i];
            var btnText = (btn.innerText || btn.textContent || '').trim();
            if (btnText === text || btnText.includes(text)) {
                btn.click();
                console.log('[MiniAppHook] Button clicked:', btnText);
                return true;
            }
        }

        // 2. 查找 view/touchable 元素
        var views = document.querySelectorAll('view,touchable');
        for (var j = 0; j < views.length; j++) {
            var view = views[j];
            var viewText = (view.innerText || view.textContent || '').trim();
            if (viewText === text || viewText.includes(text)) {
                // 模拟点击事件
                var event = new MouseEvent('click', {
                    bubbles: true,
                    cancelable: true,
                    view: window
                });
                view.dispatchEvent(event);
                console.log('[MiniAppHook] View clicked:', viewText);
                return true;
            }
        }

        // 3. 查找带指定 class 的元素
        var classBtns = document.querySelectorAll('[class*="btn"],[class*="button"]');
        for (var k = 0; k < classBtns.length; k++) {
            var classBtn = classBtns[k];
            var classText = (classBtn.innerText || classBtn.textContent || '').trim();
            if (classText === text || classText.includes(text)) {
                classBtn.click();
                return true;
            }
        }

        console.log('[MiniAppHook] Button not found:', text);
        return false;
    };

    /**
     * 获取页面所有可点击元素
     * @returns {Array} - 元素列表
     */
    window.__MiniAppHook__.getClickableElements = function() {
        var elements = [];
        var allElements = document.querySelectorAll('button,view,touchable,[class*="btn"],[class*="button"]');

        for (var i = 0; i < allElements.length; i++) {
            var el = allElements[i];
            var text = (el.innerText || el.textContent || '').trim();
            if (text) {
                elements.push({
                    index: i,
                    tagName: el.tagName.toLowerCase(),
                    text: text.substring(0, 50),
                    className: el.className || '',
                    id: el.id || ''
                });
            }
        }

        return elements;
    };

    /**
     * 获取页面信息
     * @returns {Object} - 页面信息
     */
    window.__MiniAppHook__.getPageInfo = function() {
        return {
            title: document.title,
            url: window.location.href,
            readyState: document.readyState,
            elements: window.__MiniAppHook__.getClickableElements(),
            timestamp: Date.now()
        };
    };

    /**
     * 输入文本到指定元素
     * @param {string} selector - CSS 选择器
     * @param {string} text - 要输入的文本
     * @returns {boolean}
     */
    window.__MiniAppHook__.inputText = function(selector, text) {
        var el = document.querySelector(selector);
        if (!el) {
            console.log('[MiniAppHook] Element not found:', selector);
            return false;
        }

        // 触发输入事件
        var inputEvent = new Event('input', { bubbles: true });
        el.value = text;
        el.dispatchEvent(inputEvent);

        // 触发变化事件
        var changeEvent = new Event('change', { bubbles: true });
        el.dispatchEvent(changeEvent);

        console.log('[MiniAppHook] Input:', text, 'to', selector);
        return true;
    };

    /**
     * 滑动页面
     * @param {string} direction - 'up', 'down', 'left', 'right'
     * @param {number} distance - 滑动距离(px)
     */
    window.__MiniAppHook__.swipe = function(direction, distance) {
        distance = distance || 300;
        var startX, startY, endX, endY;

        switch(direction) {
            case 'up':
                startX = startY = 0.5;
                endX = 0.5;
                endY = 0.2;
                break;
            case 'down':
                startX = 0.5;
                startY = 0.2;
                endX = 0.5;
                endY = 0.5;
                break;
            case 'left':
                startX = 0.8;
                startY = 0.5;
                endX = 0.2;
                endY = 0.5;
                break;
            case 'right':
                startX = 0.2;
                startY = 0.5;
                endX = 0.8;
                endY = 0.5;
                break;
        }

        var touchStart = new TouchEvent('touchstart', {
            touches: [createTouch(startX, startY)]
        });
        var touchEnd = new TouchEvent('touchend', {
            touches: [createTouch(endX, endY)]
        });

        document.dispatchEvent(touchStart);
        setTimeout(function() {
            document.dispatchEvent(touchEnd);
        }, 100);

        console.log('[MiniAppHook] Swipe:', direction, distance);
    };

    function createTouch(x, y) {
        return {
            clientX: window.innerWidth * x,
            clientY: window.innerHeight * y,
            identifier: 0
        };
    }

    console.log('[MiniAppHook] Injected successfully');
    console.log('[MiniAppHook] Methods available:');
    console.log('  - clickByText(text)');
    console.log('  - getClickableElements()');
    console.log('  - getPageInfo()');
    console.log('  - inputText(selector, text)');
    console.log('  - swipe(direction, distance)');

})();
