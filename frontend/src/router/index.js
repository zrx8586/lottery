import { createRouter, createWebHistory } from "vue-router";
import ActivityManagement from "../views/ActivityManagement.vue";
import ActivityPrizeRelationship from "../views/ActivityPrizeRelationship.vue";
import CacheManagement from "../views/CacheManagement.vue";
import PrizeManagement from "@/views/PrizeManagement.vue";

const routes = [
    { path: "/activity", component: ActivityManagement },
    { path: "/prizes", component: PrizeManagement },
    { path: "/cache", component: CacheManagement },
    { path: "/activityPrizeRelationship", component: ActivityPrizeRelationship },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;