<template>
  <div class="page-container">
    <div class="page-header">
      <h2>活动奖品关系管理</h2>
      <button class="add-btn" @click="createNewBinding">
        <i class="icon-add"></i> 创建新的绑定关系
      </button>
    </div>

    <div class="content-box">
      <div class="search-bar">
        <div class="search-group">
          <i class="icon-search"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索活动名称..." 
            @input="handleSearch"
          />
        </div>
      </div>

      <div class="table-container">
        <table class="data-table">
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
                <div class="action-buttons">
                  <button class="action-btn view-btn" @click="viewActivity(activity.activityId)">
                    <i class="icon-view"></i>
                  </button>
                  <button class="action-btn edit-btn" @click="editActivity(activity)">
                    <i class="icon-edit"></i>
                  </button>
                  <button class="action-btn delete-btn" @click="deleteActivity(activity.activityId)">
                    <i class="icon-delete"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页控件 -->
      <div class="pagination">
        <button 
          :disabled="currentPage === 1" 
          @click="currentPage--" 
          class="pagination-btn"
        >
          上一页
        </button>
        <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button 
          :disabled="currentPage === totalPages" 
          @click="currentPage++" 
          class="pagination-btn"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 活动详情模态框 -->
    <div v-if="selectedActivity && !showCreateForm" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>活动奖品关系</h3>
          <button class="close-btn" @click="selectedActivity = null">&times;</button>
        </div>
        <div class="modal-body">
          <div class="info-group">
            <label>活动名称:</label>
            <span>{{ selectedActivity.activityName }}</span>
          </div>
          <div class="info-group">
            <label>描述:</label>
            <span>{{ selectedActivity.activityDesc }}</span>
          </div>
          <div class="info-group">
            <label>开始日期:</label>
            <span>{{ formatDate(selectedActivity.startDate) }}</span>
          </div>
          <div class="info-group">
            <label>结束日期:</label>
            <span>{{ formatDate(selectedActivity.endDate) }}</span>
          </div>
          <div class="prize-list">
            <h4>奖品列表</h4>
            <div v-for="prize in selectedActivity.prizes" :key="prize.prizeId" class="prize-item">
              <div class="prize-info">
                <span class="prize-name">{{ prize.prizeName }}</span>
                <div class="prize-inputs">
                  <div class="input-group">
                    <label>概率:</label>
                    <span>{{ prize.probability }}%</span>
                  </div>
                  <div class="input-group">
                    <label>库存:</label>
                    <span>{{ prize.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑活动表单 -->
    <div v-if="showCreateForm" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingActivity ? '编辑活动奖品关系' : '创建活动奖品关系' }}</h3>
          <button class="close-btn" @click="closeForm">&times;</button>
        </div>
        <div class="modal-body">
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
              <div class="prize-select-group">
                <select v-model="selectedPrizeId">
                  <option value="" disabled>请选择奖品</option>
                  <option v-for="prize in availablePrizes" :key="prize.prizeId" :value="prize.prizeId">
                    {{ prize.prizeName }}
                  </option>
                </select>
                <button type="button" class="add-prize-btn" @click="addPrize">添加奖品</button>
              </div>
            </div>
            <div class="form-group">
              <label>奖品列表:</label>
              <div v-for="(prize, index) in formData.prizes" :key="index" class="prize-item">
                <div class="prize-info">
                  <span class="prize-name">{{ prize.prizeName }}</span>
                  <div class="prize-inputs">
                    <div class="input-group">
                      <label>概率:</label>
                      <input
                        type="number"
                        v-model="prize.probability"
                        placeholder="概率"
                        min="0"
                        max="1"
                        step="0.00001"
                        required
                      />
                    </div>
                    <div class="input-group">
                      <label>库存:</label>
                      <input
                        type="number"
                        v-model="prize.quantity"
                        placeholder="库存"
                        required
                      />
                    </div>
                  </div>
                </div>
                <button type="button" class="remove-prize-btn" @click="removePrize(index)">
                  <i class="icon-delete"></i>
                </button>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeForm">取消</button>
              <button type="submit" class="save-btn">{{ editingActivity ? '更新' : '创建' }}</button>
            </div>
          </form>
        </div>
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
        this.selectedPrizeId = ""; // 重置奖品选择为空

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
@import '@/assets/styles/button-styles.css';

.page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 50px);
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-header h2 {
  color: #2c3e50;
  font-size: 20px;
  margin: 0;
  font-weight: 600;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  font-size: 14px;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.content-box {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.search-group {
  position: relative;
  max-width: 300px;
}

.search-group i {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #909399;
}

.search-group input {
  width: 100%;
  padding: 8px 12px 8px 35px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
  color: #606266;
}

.search-group input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  outline: none;
}

.table-container {
  overflow-x: auto;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
}

.data-table th {
  background-color: #f5f7fa;
  font-weight: 500;
  color: #2c3e50;
}

.data-table tr:hover {
  background-color: #f5f7fa;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.view-btn {
  composes: btn-view from '@/assets/styles/button-styles.css';
}

.edit-btn {
  composes: btn-edit from '@/assets/styles/button-styles.css';
}

.delete-btn {
  composes: btn-delete from '@/assets/styles/button-styles.css';
}

.view-btn:hover,
.edit-btn:hover,
.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
  padding: 0;
  line-height: 1;
}

.modal-body {
  padding: 20px;
}

.info-group {
  margin-bottom: 15px;
}

.info-group label {
  display: block;
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.info-group span {
  color: #2c3e50;
  font-size: 14px;
}

.prize-list {
  margin-top: 20px;
}

.prize-list h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 500;
}

.prize-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.prize-info {
  flex: 1;
}

.prize-name {
  display: block;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 8px;
  font-size: 14px;
}

.prize-inputs {
  display: flex;
  gap: 15px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-group label {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.input-group span {
  color: #2c3e50;
  font-size: 14px;
  font-weight: 500;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.form-group select,
.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s ease;
}

.form-group select:focus,
.form-group input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  outline: none;
}

.prize-select-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.add-prize-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-prize-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.remove-prize-btn {
  background: none;
  border: none;
  color: #f56c6c;
  cursor: pointer;
  padding: 4px;
  transition: all 0.3s ease;
}

.remove-prize-btn:hover {
  transform: scale(1.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn,
.save-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.cancel-btn {
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.cancel-btn:hover,
.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #ecf5ff;
  color: #409eff;
  border-color: #c6e2ff;
}

.pagination-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination span {
  color: #606266;
  font-size: 14px;
}

/* 图标样式 */
.icon-add:before {
  content: "➕";
}

.icon-search:before {
  content: "🔍";
}

.icon-view:before {
  content: "👁️";
}

.icon-edit:before {
  content: "✏️";
}

.icon-delete:before {
  content: "🗑️";
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
    padding: 10px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .add-btn {
    width: 100%;
    justify-content: center;
  }

  .content-box {
    padding: 10px;
  }

  .search-group {
    max-width: 100%;
  }

  .data-table {
    display: block;
  }

  .data-table thead {
    display: none;
  }

  .data-table tbody tr {
    display: block;
    margin-bottom: 15px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .data-table td {
    display: block;
    text-align: right;
    padding: 10px 15px;
    position: relative;
  }

  .data-table td:before {
    content: attr(data-label);
    position: absolute;
    left: 0;
    width: 50%;
    padding-left: 15px;
    font-weight: 500;
    text-align: left;
    color: #909399;
  }

  .action-buttons {
    justify-content: flex-end;
  }

  .modal-content {
    width: 95%;
    margin: 10px;
  }

  .form-group {
    margin-bottom: 15px;
  }

  .form-group input,
  .form-group select,
  .form-group textarea {
    padding: 10px;
  }

  .form-actions {
    flex-direction: column;
    gap: 10px;
  }

  .cancel-btn,
  .save-btn {
    width: 100%;
  }

  .pagination {
    flex-direction: column;
    gap: 10px;
    padding: 10px;
  }

  .pagination-btn {
    width: 100%;
  }

  .prize-list {
    padding: 10px;
  }

  .prize-item {
    padding: 10px;
  }

  .prize-info {
    flex-direction: column;
    gap: 10px;
  }

  .input-group {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 5px;
  }

  .page-header {
    padding: 8px;
  }

  .content-box {
    padding: 8px;
  }

  .data-table td {
    padding: 8px 12px;
  }

  .modal-content {
    width: 100%;
    margin: 5px;
  }

  .form-group {
    margin-bottom: 12px;
  }

  .form-group input,
  .form-group select,
  .form-group textarea {
    padding: 8px;
  }

  .prize-list {
    padding: 8px;
  }

  .prize-item {
    padding: 8px;
  }
}
</style>