import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import ActivityManagement from "../views/ActivityManagement.vue";
import PrizeManagement from "@/views/PrizeManagement.vue";
import ActivityPrizeRelationship from "../views/ActivityPrizeRelationship.vue";
import CacheManagement from "../views/CacheManagement.vue";
import Lottery from "../views/Lottery.vue";
// 已删除的页面移除对应的路由组件引入

import axios from "axios";

const routes = [
    { path: "/login", component: Login },
    { path: "/activity", component: ActivityManagement, meta: { requiresAuth: true } },
    { path: "/prizes", component: PrizeManagement, meta: { requiresAuth: true } },
    { path: "/activityPrizeRelationship", component: ActivityPrizeRelationship, meta: { requiresAuth: true } },
    { path: "/cache", component: CacheManagement, meta: { requiresAuth: true } },
    { path: "/lottery", component: Lottery, meta: { requiresAuth: true } },
    // 根路径重定向到登录或已有业务页面，这里重定向到登录
    { path: "/", redirect: "/login" }
];

const router = createRouter({
    history: createWebHistory(), // 改回history模式
    routes,
});

// 全局导航守卫
router.beforeEach(async (to, from, next) => {
    const token = localStorage.getItem("token");

    if (to.path === "/") {
        next("/login");
        return;
    }

    if (to.meta && to.meta.requiresAuth) {
        if (!token) {
            next("/login");
            return;
        }
        try {
            await axios.get("/api/auth/validate", {
                headers: { Authorization: `Bearer ${token}` },
            });
            next();
        } catch (error) {
            localStorage.removeItem("token");
            next("/login");
        }
        return;
    }

    next();
});

export default router;
