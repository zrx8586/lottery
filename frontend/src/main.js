import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";

// 添加 Axios 响应拦截器
axios.interceptors.response.use(
    (response) => {
        // 如果响应成功，直接返回数据
        return response;
    },
    (error) => {
        // 如果服务端返回 401 Unauthorized
        if (error.response && error.response.status === 401) {
            // 清除本地存储中的 Token
            localStorage.removeItem("token");

            // 跳转到登录页
            router.push("/login");
        }
        // 返回错误以便在具体请求中处理
        return Promise.reject(error);
    }
);

// 从 localStorage 加载 Token
const token = localStorage.getItem("token");
if (token) {
    // 设置 Axios 默认请求头
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

const app = createApp(App);
app.use(router); // 注册路由
app.mount("#app");