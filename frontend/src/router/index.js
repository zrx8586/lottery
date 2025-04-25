import { createRouter, createWebHistory } from "vue-router";
import ActivityManagement from "../views/ActivityManagement.vue";
import PrizeManagement from "../views/PrizeManagement.vue";
import CacheManagement from "../views/CacheManagement.vue";

const routes = [
    { path: "/", component: ActivityManagement },
    { path: "/prizes", component: PrizeManagement },
    { path: "/cache", component: CacheManagement },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;