// 全屏显示工具函数

/**
 * 检测是否在微信浏览器中
 */
export function isWeChat() {
  return /MicroMessenger/i.test(navigator.userAgent);
}

/**
 * 检测是否在移动端
 */
export function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}

/**
 * 检测是否在iOS设备上
 */
export function isIOS() {
  return /iPad|iPhone|iPod/.test(navigator.userAgent);
}

/**
 * 检测是否在Android设备上
 */
export function isAndroid() {
  return /Android/.test(navigator.userAgent);
}

/**
 * 隐藏移动端浏览器的地址栏
 */
export function hideAddressBar() {
  if (window.scrollY === 0) {
    window.scrollTo(0, 1);
  }
}

/**
 * 强制全屏显示（适用于游戏页面）
 */
export function forceFullscreen() {
  if (!isMobile()) return;
  
  // 隐藏地址栏
  hideAddressBar();
  
  // 延迟再次隐藏地址栏，确保生效
  setTimeout(hideAddressBar, 100);
  setTimeout(hideAddressBar, 500);
  setTimeout(hideAddressBar, 1000);
  
  // 强制设置视口高度
  const vh = window.innerHeight * 0.01;
  document.documentElement.style.setProperty('--vh', `${vh}px`);
  
  // 微信浏览器特殊处理
  if (isWeChat()) {
    // 禁用微信浏览器的下拉刷新
    document.addEventListener('touchstart', function(e) {
      if (e.touches.length > 1) {
        e.preventDefault();
      }
    }, { passive: false });
    
    document.addEventListener('touchmove', function(e) {
      if (e.touches.length > 1) {
        e.preventDefault();
      }
    }, { passive: false });
    
    // 微信浏览器中禁用长按菜单
    document.addEventListener('contextmenu', function(e) {
      e.preventDefault();
    }, false);
  }
  
  // 添加全屏类名
  addFullscreenClass();
}

/**
 * 初始化全屏显示
 */
export function initFullscreen() {
  if (!isMobile()) return;
  
  // 页面加载完成后隐藏地址栏
  window.addEventListener('load', forceFullscreen);
  
  // 屏幕旋转时重新隐藏地址栏
  window.addEventListener('orientationchange', () => {
    setTimeout(forceFullscreen, 500);
  });
  
  // 窗口大小改变时重新隐藏地址栏
  window.addEventListener('resize', () => {
    setTimeout(forceFullscreen, 100);
  });
  
  // 防止双击缩放
  let lastTouchEnd = 0;
  document.addEventListener('touchend', function(e) {
    const now = (new Date()).getTime();
    if (now - lastTouchEnd <= 300) {
      e.preventDefault();
    }
    lastTouchEnd = now;
  }, false);
}

/**
 * 为游戏页面添加全屏类名
 */
export function addFullscreenClass() {
  if (isMobile()) {
    document.body.classList.add('mobile-fullscreen');
  }
  if (isWeChat()) {
    document.body.classList.add('wechat-fullscreen');
  }
  if (isIOS()) {
    document.body.classList.add('ios-fullscreen');
  }
  if (isAndroid()) {
    document.body.classList.add('android-fullscreen');
  }
}

/**
 * 移除全屏类名
 */
export function removeFullscreenClass() {
  document.body.classList.remove('mobile-fullscreen', 'wechat-fullscreen', 'ios-fullscreen', 'android-fullscreen');
}
