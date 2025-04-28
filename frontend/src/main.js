import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
// import axios from "axios";
//
// // 从 localStorage 加载 Token
// const token = localStorage.getItem("token");
// if (token) {
//     // 设置 Axios 默认请求头
//     axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
// }

const app = createApp(App);
app.use(router); // 注册路由
app.mount("#app");