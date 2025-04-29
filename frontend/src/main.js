import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import eventBus from './eventBus'

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