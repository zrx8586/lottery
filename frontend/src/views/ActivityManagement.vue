<template>
  <div class="activity-management">
    <h1>活动管理</h1>
    <ul class="activity-list">
      <li v-for="activity in activities" :key="activity.activityId" class="activity-item">
        <div class="activity-info">
          <span class="activity-name">活动名称: {{ activity.activityName }}</span>
          <span class="activity-description">描述: {{ activity.description }}</span>
          <span class="activity-start-date">开始日期: {{ activity.startDate }}</span>
          <span class="activity-end-date">结束日期: {{ activity.endDate }}</span>
        </div>
        <button class="btn btn-view" @click="viewActivity(activity.activityId)">查看详情</button>
      </li>
    </ul>

    <div v-if="selectedActivity" class="activity-details">
      <h2>活动详情</h2>
      <p>活动名称: {{ selectedActivity.activityName }}</p>
      <p>描述: {{ selectedActivity.description }}</p>
      <p>开始日期: {{ selectedActivity.startDate }}</p>
      <p>结束日期: {{ selectedActivity.endDate }}</p>
      <h3>奖品列表</h3>
      <ul>
        <li v-for="prize in selectedActivity.prizes" :key="prize.prizeId">
          奖品名称: {{ prize.prizeName }} | 概率: {{ prize.probability }}% | 库存: {{ prize.quantity }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      activities: [],
      selectedActivity: null, // Store the selected activity details
    };
  },
  methods: {
    async fetchActivities() {
      try {
        const response = await axios.get("/api/activity/all");
        this.activities = response.data;
      } catch (error) {
        console.error("加载活动数据失败：", error);
      }
    },
    async viewActivity(activityId) {
      try {
        const response = await axios.get(`/api/activity/${activityId}/details`);
        this.selectedActivity = response.data; // Set the selected activity details
      } catch (error) {
        console.error("加载活动详情失败：", error);
      }
    },
  },
  mounted() {
    this.fetchActivities();
  },
};
</script>

<style scoped>
.activity-management {
  padding: 20px;
  font-family: Arial, sans-serif;
}

.activity-list {
  list-style: none;
  padding: 0;
}

.activity-item {
  display: flex;
  flex-direction: column;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.activity-details {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f1f1f1;
}

.activity-info {
  margin-bottom: 10px;
}

.activity-name,
.activity-description,
.activity-start-date,
.activity-end-date {
  font-size: 0.9em;
  color: #333;
}

.btn {
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.btn-view {
  background-color: #007bff;
  color: white;
}

.btn-view:hover {
  background-color: #0056b3;
}
</style>