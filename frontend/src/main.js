import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import eventBus from './eventBus'

// 修复 iOS 微信等移动端浏览器 100vh 异常：使用 --vh 变量
function setVh() {
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
}
setVh();
window.addEventListener('resize', setVh);
window.addEventListener('orientationchange', setVh);

// 导入全屏工具函数
import { initFullscreen, addFullscreenClass } from './utils/fullscreen.js';

// 页面加载完成后初始化全屏
document.addEventListener('DOMContentLoaded', () => {
    initFullscreen();
    addFullscreenClass();
});

const app = createApp(App);

// 添加全局事件总线
app.config.globalProperties.$eventBus = eventBus;

// Axios 配置
axios.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem("token");
            router.push("/login");
        }
        return Promise.reject(error);
    }
);

const token = localStorage.getItem("token");
if (token) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

app.use(router);
app.mount("#app");