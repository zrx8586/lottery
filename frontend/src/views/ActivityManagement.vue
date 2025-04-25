<template>
  <div class="activity-management">
    <h1>活动管理</h1>

    <!-- 搜索和创建按钮 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索活动名称..." class="search-input" />
      <button class="btn btn-create" @click="showCreateForm = true">创建新活动</button>
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
    <div v-if="selectedActivity" class="modal">
      <div class="modal-content">
        <span class="close" @click="selectedActivity = null">&times;</span>
        <h2>活动详情</h2>
        <p><strong>活动名称:</strong> {{ selectedActivity.activityName }}</p>
        <p><strong>描述:</strong> {{ selectedActivity.activityDesc }}</p>
        <p><strong>开始日期:</strong> {{ formatDate(selectedActivity.startDate) }}</p>
        <p><strong>结束日期:</strong> {{ formatDate(selectedActivity.endDate) }}</p>
        <h3>奖品列表</h3>
        <ul>
          <li v-for="prize in selectedActivity.prizes" :key="prize.prizeId">
            {{ prize.prize.prizeName }} | 概率: {{ prize.probability }}% | 库存: {{ prize.quantity }}
          </li>
        </ul>
      </div>
    </div>

    <!-- 创建/编辑活动表单 -->
    <div v-if="showCreateForm || editingActivity" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeForm">&times;</span>
        <h2>{{ editingActivity ? '编辑活动' : '创建新活动' }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>活动名称:</label>
            <input v-model="formData.activityName" required />
          </div>
          <div class="form-group">
            <label>描述:</label>
            <textarea v-model="formData.activityDesc" required></textarea>
          </div>
          <div class="form-group">
            <label>开始日期:</label>
            <input type="date" v-model="formData.startDate" required />
          </div>
          <div class="form-group">
            <label>结束日期:</label>
            <input type="date" v-model="formData.endDate" required />
          </div>
          <div class="form-group">
            <label>奖品列表:</label>
            <div v-for="(prize, index) in formData.prizes" :key="index" class="prize-item">
              <input v-model="prize.prizeName" placeholder="奖品名称" required />
              <input type="number" v-model="prize.probability" placeholder="概率" required />
              <input type="number" v-model="prize.quantity" placeholder="库存" required />
              <button type="button" @click="removePrize(index)">删除</button>
            </div>
            <button type="button" @click="addPrize">添加奖品</button>
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
      selectedActivity: null,
      showCreateForm: false,
      editingActivity: null,
      formData: {
        activityName: "",
        activityDesc: "",
        startDate: "",
        endDate: "",
        prizes: [] // 初始化为空数组
      },
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 10
    };
  },
  computed: {
    filteredActivities() {
      return this.activities.filter(activity =>
          activity.activityName.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
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
    addPrize() {
      this.formData.prizes.push({ prizeName: "", probability: 0, quantity: 0 });
    },
    removePrize(index) {
      this.formData.prizes.splice(index, 1);
    },
    async fetchActivities() {
      try {
        const response = await axios.get("/api/activity/all");
        this.activities = response.data;
      } catch (error) {
        console.error("获取活动列表失败：", error);
      }
    },
    async viewActivity(activityId) {
      try {
        const response = await axios.get(`/api/activity/${activityId}`);
        this.selectedActivity = response.data;
      } catch (error) {
        console.error("获取活动详情失败：", error);
      }
    },
    async editActivity(activity) {
      try {
        // 调用后端接口获取活动详情（包括奖品列表）
        const response = await axios.get(`/api/activity/${activity.activityId}/details`);
        const activityDetails = response.data;

        this.editingActivity = activity;
        this.formData = {
          activityName: activityDetails.activityName,
          activityDesc: activityDetails.description,
          startDate: moment(activityDetails.startDate).format("YYYY-MM-DD"),
          endDate: moment(activityDetails.endDate).format("YYYY-MM-DD"),
          prizes: activityDetails.prizes // 加载奖品列表
        };
        this.showCreateForm = true;
      } catch (error) {
        console.error("编辑活动时加载活动详情失败：", error);
      }
    },
    async deleteActivity(activityId) {
      if (confirm("确定要删除这个活动吗？")) {
        try {
          await axios.delete(`/api/activity/${activityId}`);
          this.activities = this.activities.filter(a => a.activityId !== activityId);
        } catch (error) {
          console.error("删除活动失败：", error);
        }
      }
    },
    async submitForm() {
      try {
        if (this.editingActivity) {
          const response = await axios.put(`/api/activity/${this.editingActivity.activityId}`, this.formData);
          const index = this.activities.findIndex(a => a.activityId === this.editingActivity.activityId);
          this.activities.splice(index, 1, response.data);
        } else {
          const response = await axios.post("/api/activity/create", this.formData);
          this.activities.push(response.data);
        }
        this.closeForm();
      } catch (error) {
        console.error("保存活动失败：", error);
      }
    },
    closeForm() {
      this.showCreateForm = false;
      this.editingActivity = null;
      this.formData = {
        activityName: "",
        activityDesc: "",
        startDate: "",
        endDate: ""
      };
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD");
    }
  },
  mounted() {
    this.fetchActivities();
  }
};
</script>

<style scoped>
.activity-management {
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

.btn {
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-create {
  background: linear-gradient(45deg, #28a745, #218838);
  color: white;
}

.btn-create:hover {
  background: linear-gradient(45deg, #218838, #1e7e34);
}

.btn-edit {
  background: linear-gradient(45deg, #ffc107, #e0a800);
  color: black;
}

.btn-edit:hover {
  background: linear-gradient(45deg, #e0a800, #d39e00);
}

.btn-delete {
  background: linear-gradient(45deg, #dc3545, #c82333);
  color: white;
}

.btn-delete:hover {
  background: linear-gradient(45deg, #c82333, #bd2130);
}

.btn-pagination {
  background-color: #007bff;
  color: white;
  border-radius: 5px;
  padding: 8px 16px;
  margin: 0 5px;
}

.btn-pagination:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  width: 500px;
  max-width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.close {
  float: right;
  cursor: pointer;
  font-size: 1.5em;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>