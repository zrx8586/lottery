<template>
  <div class="page-container">
    <div class="page-header">
      <h2>活动管理</h2>
      <button class="add-btn" @click="showAddDialog = true">
        <i class="icon-add"></i> 新增活动
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
              <th>活动描述</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="activity in paginatedActivities" :key="activity.id">
              <td>{{ activity.activityName }}</td>
              <td>{{ activity.activityDesc }}</td>
              <td>{{ formatDate(activity.startDate) }}</td>
              <td>{{ formatDate(activity.endDate) }}</td>
              <td>
                <span :class="['status-badge', (activity.status || 'PENDING').toLowerCase()]">
                  {{ activity.status || '未开始' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn view-btn" @click="viewActivity(activity)">
                    <i class="icon-view"></i>
                  </button>
                  <button class="action-btn edit-btn" @click="editActivity(activity)">
                    <i class="icon-edit"></i>
                  </button>
                  <button class="action-btn delete-btn" @click="confirmDelete(activity)">
                    <i class="icon-delete"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
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

    <!-- 添加/编辑/查看活动对话框 -->
    <div v-if="showAddDialog" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingActivity ? '编辑活动' : '新增活动' }}</h3>
          <button class="close-btn" @click="closeDialog">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveActivity">
            <div class="form-group">
              <label>活动名称</label>
              <input v-model="form.activityName" type="text" required />
            </div>
            <div class="form-group">
              <label>活动描述</label>
              <textarea 
                v-model="form.activityDesc" 
                required 
                class="form-textarea"
                rows="3"
                placeholder="请输入活动描述..."
              ></textarea>
            </div>
            <div class="form-group">
              <label>开始时间</label>
              <input v-model="form.startDate" type="datetime-local" required />
            </div>
            <div class="form-group">
              <label>结束时间</label>
              <input v-model="form.endDate" type="datetime-local" required />
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="form.status" required>
                <option value="ACTIVE">进行中</option>
                <option value="PENDING">未开始</option>
                <option value="ENDED">已结束</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="save-btn">保存</button>
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
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 5,
      showAddDialog: false,
      editingActivity: null,
      viewingActivity: null,
      form: {
        activityName: "",
        activityDesc: "",
        startDate: "",
        endDate: "",
        status: "PENDING"
      }
    };
  },
  computed: {
    filteredActivities() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.activities.filter(activity =>
        activity.activityName.toLowerCase().includes(query)
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
    async fetchActivities() {
      try {
        const response = await axios.get("/api/activity/all");
        this.activities = response.data;
      } catch (error) {
        console.error("获取活动列表失败:", error);
        alert("获取活动列表失败");
      }
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD");
    },
    handleSearch() {
      // 搜索时重置到第一页
      this.currentPage = 1;
    },
    editActivity(activity) {
      this.editingActivity = activity;
      this.form = {
        activityName: activity.activityName,
        activityDesc: activity.activityDesc,
        startDate: moment(activity.startDate).format("YYYY-MM-DDTHH:mm"),
        endDate: moment(activity.endDate).format("YYYY-MM-DDTHH:mm"),
        status: activity.status
      };
      this.showAddDialog = true;
    },
    viewActivity(activity) {
      this.viewingActivity = true;
      this.form = {
        activityName: activity.activityName,
        activityDesc: activity.activityDesc,
        startDate: moment(activity.startDate).format("YYYY-MM-DDTHH:mm"),
        endDate: moment(activity.endDate).format("YYYY-MM-DDTHH:mm"),
        status: activity.status
      };
      this.showAddDialog = true;
    },
    confirmDelete(activity) {
      if (confirm("确定要删除这个活动吗？")) {
        this.deleteActivity(activity);
      }
    },
    async deleteActivity(activity) {
      try {
        await axios.delete(`/api/activity/${activity.id}`);
        this.fetchActivities();
      } catch (error) {
        console.error("删除活动失败:", error);
        alert("删除活动失败");
      }
    },
    closeDialog() {
      this.showAddDialog = false;
      this.editingActivity = null;
      this.viewingActivity = null;
      this.form = {
        activityName: "",
        activityDesc: "",
        startDate: "",
        endDate: "",
        status: "PENDING"
      };
    },
    async saveActivity() {
      try {
        if (this.editingActivity) {
          await axios.put(`/api/activity/${this.editingActivity.id}`, this.form);
        } else {
          await axios.post("/api/activity/create", this.form);
        }
        this.closeDialog();
        this.fetchActivities();
      } catch (error) {
        console.error("保存活动失败:", error);
        alert("保存活动失败");
      }
    }
  },
  mounted() {
    this.fetchActivities();
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

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
}

.status-badge.active {
  background-color: #e6f7e6;
  color: #67c23a;
}

.status-badge.pending {
  background-color: #fff3e6;
  color: #e6a23c;
}

.status-badge.ended {
  background-color: #fef0f0;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 8px;
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
  max-width: 500px;
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  outline: none;
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

.form-textarea {
  min-height: 80px;
  resize: vertical;
  line-height: 1.5;
  font-family: inherit;
}

/* 图标样式 */
.icon-add:before {
  content: "➕";
}

.icon-search:before {
  content: "🔍";
}

.icon-edit:before {
  content: "✏️";
}

.icon-delete:before {
  content: "🗑️";
}

.icon-view:before {
  content: "👁️";
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
</style>