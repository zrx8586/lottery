<template>
  <div class="activity-management">
    <h1>活动奖品关系管理</h1>

    <!-- 搜索和创建按钮 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索活动名称..." class="search-input" />
      <button class="btn btn-create" @click="showCreateForm = true">创建新的绑定关系</button>
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
        <h2>活动奖品关系</h2>
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
        <h2>{{ editingActivity ? '编辑活动奖品关系' : '创建活动奖品关系' }}</h2>
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
              <select v-model="prize.prize.prizeId" required>
                <option v-for="availablePrize in availablePrizes" :key="availablePrize.prizeId" :value="availablePrize.prizeId">
                  {{ availablePrize.prizeName }}
                </option>
              </select>
              <input
                  type="number"
                  v-model="prize.probability"
                  placeholder="概率"
                  min="0"
                  max="1"
                  step="0.00001"
                  required
              />
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
      availablePrizes: [], // 可用奖品列表
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
      // 添加一个新的奖品选择项
      this.formData.prizes.push({ prizeId: null, probability: 0, quantity: 0 });
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
          activityDesc: activityDetails.activityDesc,
          startDate: moment(activityDetails.startDate).format("YYYY-MM-DD"),
          endDate: moment(activityDetails.endDate).format("YYYY-MM-DD"),
          prizes: activityDetails.prizes || [] // 如果没有奖品，初始化为空数组
        };

        // 加载可用奖品列表
        await this.fetchAvailablePrizes();

        this.showCreateForm = true;
      } catch (error) {
        console.error("编辑活动时加载活动详情失败：", error);
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
        const payload = {
          activityId: this.editingActivity ? this.editingActivity.activityId : null,
          activityName: this.formData.activityName,
          activityDesc: this.formData.activityDesc,
          startDate: this.formData.startDate,
          endDate: this.formData.endDate,
          prizes: this.formData.prizes.map(item => ({
            prizeId: item.prize.prizeId,
            prizeName: item.prize.prizeName,
            probability: item.probability,
            quantity: item.quantity
          }))
        };

        if (this.editingActivity) {
          // 编辑模式
          const response = await axios.put(`/api/activity-prize-relationship/${this.editingActivity.activityId}`, payload);
          const index = this.activities.findIndex(a => a.activityId === this.editingActivity.activityId);
          this.activities.splice(index, 1, response.data);
        } else {
          // 创建模式
          const response = await axios.post("/api/activity-prize-relationship/create", payload);
          this.activities.push(response.data);
        }

        this.closeForm();
      } catch (error) {
        console.error("保存活动奖品关系失败：", error);
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