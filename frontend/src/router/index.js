import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import ActivityManagement from "../views/ActivityManagement.vue";
import PrizeManagement from "@/views/PrizeManagement.vue";
import ActivityPrizeRelationship from "../views/ActivityPrizeRelationship.vue";
import CacheManagement from "../views/CacheManagement.vue";
import LotteryView from "../views/LotteryView.vue";
import Game from '../views/Game.vue'

import axios from "axios";

const routes = [
    { path: "/login", component: Login },
    { path: "/activity", component: ActivityManagement, meta: { requiresAuth: true } },
    { path: "/prizes", component: PrizeManagement, meta: { requiresAuth: true } },
    { path: "/activityPrizeRelationship", component: ActivityPrizeRelationship, meta: { requiresAuth: true } },
    { path: "/cache", component: CacheManagement, meta: { requiresAuth: true } },
    { path: "/lottery", component: LotteryView, meta: { requiresAuth: true } },
    { path: "/game", name: 'Game', component: Game }, // 游戏页面，无需认证
    { path: "/", redirect: "/game" } // 根路径重定向到游戏页面
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 全局导航守卫
router.beforeEach(async (to, from, next) => {
    // 游戏页面不需要认证，直接放行
    if (to.path === "/game") {
        next();
        return;
    }

    const token = localStorage.getItem("token");

    if (to.path === "/") {
        // 根路径重定向到游戏页面
        next("/game");
    } else if (to.meta.requiresAuth) {
        // 需要认证的页面
        if (!token) {
            next("/login");
        } else {
            try {
                await axios.get("/api/auth/validate", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                next();
            } catch (error) {
                localStorage.removeItem("token");
                next("/login");
            }
        }
    } else {
        next(); // 不需要认证的页面直接放行
    }
});

export default router;