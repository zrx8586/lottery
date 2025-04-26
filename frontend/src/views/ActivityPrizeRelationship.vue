<template>
  <div class="activity-management">
    <h1>活动奖品关系管理</h1>

    <!-- 搜索和创建按钮 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索活动名称..." class="search-input" />
      <button class="btn btn-create" @click="createNewBinding">创建新的绑定关系</button>
    </div>

    <!-- 活动列表 -->
    <div class="activity-list-container">
      <table class="activity-table">
        <thead>
        <tr>
          <th>活动名称</th>
          <th>描述</th>
          <th>开始日期</th>
          <th>结束日期</th>
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
            <button class="btn btn-view" @click="viewActivity(activity.activityId)">查看</button>
            <button class="btn btn-edit" @click="editActivity(activity)">编辑</button>
            <button class="btn btn-delete" @click="deleteActivity(activity.activityId)">删除</button>
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

    <!-- 活动详情模态框 -->
    <div v-if="selectedActivity && !showCreateForm" class="modal">
      <div class="modal-content">
        <span class="close" @click="selectedActivity = null">&times;</span>
        <h2>活动奖品关系</h2>
        <p><strong>活动名称:</strong> {{ selectedActivity.activityName }}</p>
        <p><strong>描述:</strong> {{ selectedActivity.activityDesc }}</p>
        <p><strong>开始日期:</strong> {{ formatDate(selectedActivity.startDate) }}</p>
        <p><strong>结束日期:</strong> {{ formatDate(selectedActivity.endDate) }}</p>
        <h3>奖品列表</h3>
        <ul>
          <li v-for="prize in selectedActivity.prizes" :key="prize.prizeId">
            {{ prize.prizeName }} | 概率: {{ prize.probability }}% | 库存: {{ prize.quantity }}
          </li>
        </ul>
      </div>
    </div>

    <!-- 创建/编辑活动表单 -->
    <div v-if="showCreateForm" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeForm">&times;</span>
        <h2>{{ editingActivity ? '编辑活动奖品关系' : '创建活动奖品关系' }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>选择活动:</label>
            <select v-model="selectedActivityId" required>
              <option v-for="activity in availableActivities" :key="activity.activityId" :value="activity.activityId">
                {{ activity.activityName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>可用奖品:</label>
            <select v-model="selectedPrizeId">
              <option v-for="prize in availablePrizes" :key="prize.prizeId" :value="prize.prizeId">
                {{ prize.prizeName }}
              </option>
            </select>
            <button type="button" @click="addPrize">添加奖品</button>
          </div>
          <div class="form-group">
            <label>奖品列表:</label>
            <div v-for="(prize, index) in formData.prizes" :key="index" class="prize-item">
              <label>奖品名称:</label>
              <span>{{ prize.prizeName }}</span>
              <label> 概率:</label>
              <input
                  type="number"
                  v-model="prize.probability"
                  placeholder="概率"
                  min="0"
                  max="1"
                  step="0.00001"
                  required
                  style="width: 80px"
              />
              <label> 库存:</label>
              <input
                  type="number"
                  v-model="prize.quantity"
                  placeholder="库存"
                  required
                  style="width: 80px"
              />
              <button type="button" @click="removePrize(index)">删除</button>
            </div>
          </div>
          <button type="submit" class="btn btn-submit">{{ editingActivity ? '更新' : '创建' }}</button>
        </form>
      </div>
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
      availableActivities: [], // 可用活动列表
      availablePrizes: [], // 可用奖品列表
      selectedActivityId: null, // 用户选择的活动ID
      selectedPrizeId: null, // 用户选择的奖品ID
      selectedActivity: null,
      showCreateForm: false,
      editingActivity: null,
      formData: {
        prizes: [] // 初始化为空数组
      },
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 10
    };
  },
  computed: {
    filteredActivities() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.activities.filter(activity => {
        return activity.activityName && activity.activityName.toLowerCase().includes(query);
      });
    },
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredActivities.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredActivities.length / this.itemsPerPage);
    }
  },
  methods: {
    async fetchAvailableActivities() {
      try {
        const response = await axios.get("/api/activity/all");
        this.availableActivities = response.data;
      } catch (error) {
        console.error("加载可用活动列表失败：", error);
      }
    },
    async fetchAvailablePrizes() {
      try {
        const response = await axios.get("/api/prize/available");
        this.availablePrizes = response.data;
      } catch (error) {
        console.error("加载可用奖品列表失败：", error);
      }
    },
    createNewBinding() {
      this.showCreateForm = true; // 显示奖品选择表单
    },
    addPrize() {
      const selectedPrize = this.availablePrizes.find(prize => prize.prizeId === this.selectedPrizeId);
      if (selectedPrize) {
        this.formData.prizes.push({
          prizeId: selectedPrize.prizeId,
          prizeName: selectedPrize.prizeName,
          probability: 0,
          quantity: 0
        });
      }
    },
    removePrize(index) {
      this.formData.prizes.splice(index, 1);
    },
    async fetchActivities() {
      try {
        const response = await axios.get("/api/activity-prize-relationship/all");
        this.activities = response.data;
      } catch (error) {
        console.error("获取活动列表失败：", error);
      }
    },
    async viewActivity(activityId) {
      try {
        const response = await axios.get(`/api/activity-prize-relationship/${activityId}`);
        this.selectedActivity = response.data;
      } catch (error) {
        console.error("获取活动详情失败：", error);
      }
    },
    async editActivity(activity) {
      try {
        const response = await axios.get(`/api/activity-prize-relationship/${activity.activityId}/details`);
        const activityDetails = response.data;

        this.editingActivity = activity;
        this.selectedActivityId = activity.activityId;
        this.formData.prizes = activityDetails.prizes || [];

        await this.fetchAvailablePrizes();
        this.showCreateForm = true;
      } catch (error) {
        console.error("编辑活动时加载活动详情失败：", error);
      }
    },
    async deleteActivity(activityId) {
      if (confirm("确定要删除这个活动吗？")) {
        try {
          await axios.delete(`/api/activity-prize-relationship/${activityId}`);
          this.activities = this.activities.filter(a => a.activityId !== activityId);
        } catch (error) {
          console.error("删除活动失败：", error);
        }
      }
    },
    async submitForm() {
      try {
        const payload = {
          activityId: this.selectedActivityId,
          prizes: this.formData.prizes.map(item => ({
            prizeId: item.prizeId,
            probability: item.probability,
            quantity: item.quantity
          }))
        };

        if (this.editingActivity) {
          await axios.put(`/api/activity-prize-relationship/${this.editingActivity.activityId}`, payload);
        } else {
          await axios.post("/api/activity-prize-relationship/create", payload);
        }

        this.closeForm();
        await this.fetchActivities(); // 刷新活动列表
      } catch (error) {
        console.error("保存活动奖品关系失败：", error);
      }
    },
    closeForm() {
      this.showCreateForm = false;
      this.editingActivity = null;
      this.formData.prizes = [];
      this.selectedActivityId = null;
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD");
    }
  },
  mounted() {
    this.fetchActivities();
    this.fetchAvailableActivities();
    this.fetchAvailablePrizes();
  }
};
</script>


<style scoped>
@import "@/assets/styles/buttons.css";
@import "@/assets/styles/common.css";

.activity-management {
  font-family: Arial, sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
}

h1 {
  text-align: center;
  color: #333;
}

.search-input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 70%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-table th,
.activity-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid #ddd;
}

.activity-table th {
  background-color: #f4f4f4;
  color: #333;
}

.activity-table tr:hover {
  background-color: #f1f1f1;
}

</style>