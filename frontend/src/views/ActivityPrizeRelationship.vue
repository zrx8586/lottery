<template>
  <div class="activity-prize-relationship">
    <h1>活动奖品关系管理</h1>

    <!-- 搜索框 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索活动名称..." class="search-input" />
    </div>

    <!-- 活动列表 -->
    <div class="activity-list-container">
      <table class="activity-table">
        <thead>
        <tr>
          <th>活动名称</th>
          <th>活动描述</th>
          <th>开始时间</th>
          <th>结束时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="activity in paginatedActivities" :key="activity.activityId">
          <td>{{ activity.activityName }}</td>
          <td>{{ activity.activityDesc }}</td>
          <td>{{ formatDate(activity.startDate) }}</td>
          <td>{{ formatDate(activity.endDate) }}</td>
          <td>
            <button class="btn btn-view" @click="viewActivity(activity.activityId)">查看奖品</button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 分页控件 -->
      <div class="pagination">
        <button :disabled="currentPage === 1" @click="currentPage--" class="btn btn-pagination">上一页</button>
        <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++" class="btn btn-pagination">下一页</button>
      </div>
    </div>

    <!-- 奖品列表 -->
    <div v-if="selectedActivity" class="prize-list-container">
      <h2>活动奖品列表: {{ selectedActivity.activityName }}</h2>
      <table class="prize-table">
        <thead>
        <tr>
          <th>奖品名称</th>
          <th>概率</th>
          <th>库存</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="prize in selectedActivity.prizes" :key="prize.activityPrizeId">
          <td>{{ prize.prizeName }}</td>
          <td>{{ prize.probability }}%</td>
          <td>{{ prize.quantity }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment";

export default {
  data() {
    return {
      activities: [],
      selectedActivity: null,
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 5, // 每页显示的活动数量
    };
  },
  computed: {
    filteredActivities() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.activities.filter(activity => {
        const activityName = activity.activityName.trim().toLowerCase();
        return activityName.includes(query);
      });
    },
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredActivities.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredActivities.length / this.itemsPerPage);
    },
  },
  methods: {
    async fetchActivities() {
      try {
        const response = await axios.get("/api/activity-prize-relationship/all");
        this.activities = response.data;
      } catch (error) {
        console.error("加载活动列表失败：", error);
      }
    },
    async viewActivity(activityId) {
      try {
        const response = await axios.get(`/api/activity-prize-relationship/${activityId}`);
        this.selectedActivity = response.data;
      } catch (error) {
        console.error("加载活动详情失败：", error);
      }
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD");
    },
  },
  mounted() {
    this.fetchActivities();
  },
};
</script>

<style scoped>
.activity-prize-relationship {
  font-family: Arial, sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
}

h1 {
  text-align: center;
  color: #333;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 70%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-table,
.prize-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-table th,
.activity-table td,
.prize-table th,
.prize-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid #ddd;
}

.activity-table th,
.prize-table th {
  background-color: #f4f4f4;
  color: #333;
}

.activity-table tr:hover,
.prize-table tr:hover {
  background-color: #f1f1f1;
}

.btn {
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-view {
  background: linear-gradient(45deg, #007bff, #0056b3);
  color: white;
}

.btn-view:hover {
  background: linear-gradient(45deg, #0056b3, #003f7f);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.btn-pagination {
  margin: 0 10px;
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-pagination:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-pagination:hover:not(:disabled) {
  background-color: #0056b3;
}
</style>