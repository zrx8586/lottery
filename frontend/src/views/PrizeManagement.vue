<template>
  <div class="prize-management">
    <h1>奖品管理</h1>
    <ul class="prize-list">
      <li v-for="prize in prizes" :key="prize.id" class="prize-item">
        <div class="prize-info">
          <span class="prize-name">{{ prize.name }}</span>
          <span class="prize-details">概率: {{ prize.probability }}% | 库存: {{ prize.quantity }}</span>
        </div>
        <button class="btn btn-update" @click="updatePrize(prize.id)">更新</button>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      prizes: [],
    };
  },
  methods: {
    async fetchPrizes() {
      try {
        const response = await axios.get("/api/activity-prizes/1");
        this.prizes = response.data;
      } catch (error) {
        console.error("加载奖品数据失败：", error);
      }
    },
    updatePrize(prizeId) {
      alert(`更新奖品信息: ${prizeId}`);
    },
  },
  mounted() {
    this.fetchPrizes();
  },
};
</script>

<style scoped>
.prize-management {
  padding: 20px;
  font-family: Arial, sans-serif;
}

.prize-list {
  list-style: none;
  padding: 0;
}

.prize-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.prize-info {
  display: flex;
  flex-direction: column;
}

.prize-name {
  font-weight: bold;
}

.prize-details {
  font-size: 0.9em;
  color: #666;
}

.btn {
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.btn-update {
  background-color: #28a745;
  color: white;
}

.btn-update:hover {
  background-color: #218838;
}
</style>