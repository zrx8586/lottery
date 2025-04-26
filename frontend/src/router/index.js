import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import ActivityManagement from "../views/ActivityManagement.vue";
import PrizeManagement from "@/views/PrizeManagement.vue";
import ActivityPrizeRelationship from "../views/ActivityPrizeRelationship.vue";
import CacheManagement from "../views/CacheManagement.vue";

const routes = [
    { path: "/login", component: Login }, // 登录页面路由
    { path: "/activity", component: ActivityManagement }, // 活动管理页面路由
    { path: "/prizes", component: PrizeManagement }, // 奖品管理页面路由
    { path: "/activityPrizeRelationship", component: ActivityPrizeRelationship }, // 活动奖品关系页面路由
    { path: "/cache", component: CacheManagement }, // 缓存管理页面路由
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 全局导航守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem("token"); // 检查是否有登录令牌
    if (!isAuthenticated && to.path !== "/login" && to.path !== "/register") {
        next("/login"); // 未登录，跳转到登录页面
    } else {
        next(); // 已登录或访问登录/注册页面，继续导航
    }
});

export default router;