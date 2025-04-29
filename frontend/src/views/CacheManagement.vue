<template>
  <div class="cache-management">
    <h1>缓存管理</h1>
    <div class="actions">
      <button class="btn btn-cache" @click="cachePrizes">缓存奖品信息</button>
      <button class="btn btn-clear" @click="clearCache">清除缓存</button>
    </div>
    <ul class="cache-list">
      <li v-for="prize in cachedPrizes" :key="prize.id" class="cache-item">
        <div class="cache-info">
          <span class="cache-name">{{ prize.name }}</span>
          <span class="cache-details">概率: {{ prize.probability }}% | 库存: {{ prize.quantity }}</span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      cachedPrizes: [],
    };
  },
  methods: {
    async cachePrizes() {
      try {
        await axios.post("/api/activity-prizes/redis/1/cache");
        alert("奖品信息已缓存");
        this.fetchCachedPrizes();
      } catch (error) {
        console.error("缓存奖品信息失败：", error);
      }
    },
    async fetchCachedPrizes() {
      try {
        const response = await axios.get("/api/activity-prizes/redis/1/cache");
        this.cachedPrizes = response.data;
      } catch (error) {
        console.error("加载缓存奖品数据失败：", error);
      }
    },
    async clearCache() {
      try {
        await axios.delete("/api/activity-prizes/redis/1/cache");
        alert("缓存已清除");
        this.cachedPrizes = [];
      } catch (error) {
        console.error("清除缓存失败：", error);
      }
    },
  },
  mounted() {
    this.fetchCachedPrizes();
  },
};
</script>

<style scoped>
@import "../assets/styles/buttons.css";
@import "../assets/styles/common.css";

.cache-management {
  padding: 20px;
  font-family: Arial, sans-serif;
}

.actions {
  margin-bottom: 20px;
}

.cache-list {
  list-style: none;
  padding: 0;
}

.cache-item {
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.cache-info {
  display: flex;
  flex-direction: column;
}

.cache-name {
  font-weight: bold;
}

.cache-details {
  font-size: 0.9em;
  color: #666;
}

.btn {
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.btn-cache {
  background-color: #ffc107;
  color: black;
}

.btn-cache:hover {
  background-color: #e0a800;
}

.btn-clear {
  background-color: #dc3545;
  color: white;
}

.btn-clear:hover {
  background-color: #c82333;
}
</style>