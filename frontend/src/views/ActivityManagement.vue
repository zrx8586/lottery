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
              <th class="sortable" @click="toggleSort('startDate')">
                开始时间
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'startDate' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'startDate' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="toggleSort('endDate')">
                结束时间
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'endDate' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'endDate' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="toggleSort('status')">
                状态
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'status' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'status' && sortDirection === 'desc' }"></i>
                </span>
              </th>
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
                <span :class="['status-badge', (activity.status || 'INACTIVE').toLowerCase()]">
                  {{ activity.status === 'ACTIVE' ? '上线' : '下线' }}
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
    <div class="pagination" v-if="totalPages > 1">
      <button 
        @click="changePage(currentPage - 1)" 
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      <span>第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button 
        @click="changePage(currentPage + 1)" 
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
    </div>

    <!-- 添加/编辑/查看活动对话框 -->
    <div v-if="showAddDialog" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ viewingActivity ? '查看活动' : (editingActivity ? '编辑活动' : '新增活动') }}</h3>
          <button class="close-btn" @click="closeDialog">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveActivity">
            <div class="form-group">
              <label>活动名称</label>
              <input 
                v-model="form.activityName" 
                type="text" 
                required 
                :readonly="viewingActivity"
                :class="{ 'readonly': viewingActivity }"
              />
            </div>
            <div class="form-group">
              <label>活动描述</label>
              <textarea 
                v-model="form.activityDesc" 
                required 
                class="form-textarea"
                rows="3"
                placeholder="请输入活动描述..."
                :readonly="viewingActivity"
                :class="{ 'readonly': viewingActivity }"
              ></textarea>
            </div>
            <div class="form-group">
              <label>开始时间</label>
              <div class="datetime-group">
                <input 
                  v-model="form.startDate" 
                  type="date" 
                  required 
                  class="date-input"
                  :readonly="viewingActivity"
                  :class="{ 'readonly': viewingActivity }"
                />
                <input 
                  v-model="form.startTime" 
                  type="time" 
                  required 
                  step="1"
                  class="time-input"
                  :readonly="viewingActivity"
                  :class="{ 'readonly': viewingActivity }"
                />
              </div>
            </div>
            <div class="form-group">
              <label>结束时间</label>
              <div class="datetime-group">
                <input 
                  v-model="form.endDate" 
                  type="date" 
                  required 
                  class="date-input"
                  :readonly="viewingActivity"
                  :class="{ 'readonly': viewingActivity }"
                />
                <input 
                  v-model="form.endTime" 
                  type="time" 
                  required 
                  step="1"
                  class="time-input"
                  :readonly="viewingActivity"
                  :class="{ 'readonly': viewingActivity }"
                />
              </div>
            </div>
            <div class="form-group">
              <label>状态</label>
              <select 
                v-model="form.status" 
                required
                :disabled="viewingActivity"
                :class="{ 'readonly': viewingActivity }"
              >
                <option value="ACTIVE">上线</option>
                <option value="INACTIVE">下线</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeDialog">
                {{ viewingActivity ? '关闭' : '取消' }}
              </button>
              <button 
                v-if="!viewingActivity" 
                type="submit" 
                class="save-btn"
              >
                保存
              </button>
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
      pageSize: 10,
      showAddDialog: false,
      editingActivity: null,
      viewingActivity: null,
      form: {
        activityName: "",
        activityDesc: "",
        startDate: moment().format("YYYY-MM-DD"), // 只设置日期
        startTime: "00:00:00", // 默认时间00:00:00
        endDate: moment().add(1, 'day').format("YYYY-MM-DD"), // 只设置日期
        endTime: "00:00:00", // 默认时间00:00:00
        status: "INACTIVE"
      },
      sortField: 'startDate',
      sortDirection: 'desc'
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
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.sortedActivities.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.sortedActivities.length / this.pageSize);
    },
    sortedActivities() {
      if (!this.filteredActivities) return [];
      
      return [...this.filteredActivities].sort((a, b) => {
        if (this.sortField === 'startDate' || this.sortField === 'endDate') {
          const dateA = new Date(a[this.sortField]);
          const dateB = new Date(b[this.sortField]);
          return this.sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
        } else if (this.sortField === 'status') {
          const statusOrder = { 'ACTIVE': 0, 'INACTIVE': 1 };
          const orderA = statusOrder[a.status];
          const orderB = statusOrder[b.status];
          return this.sortDirection === 'asc' ? orderA - orderB : orderB - orderA;
        }
        return 0;
      });
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
      return moment(date).format("YYYY-MM-DD HH:mm:ss");
    },
    handleSearch() {
      // 搜索时重置到第一页
      this.currentPage = 1;
    },
    editActivity(activity) {
      this.editingActivity = activity;
      const startMoment = moment(activity.startDate);
      const endMoment = moment(activity.endDate);
      this.form = {
        activityName: activity.activityName,
        activityDesc: activity.activityDesc,
        startDate: startMoment.format("YYYY-MM-DD"),
        startTime: startMoment.format("HH:mm:ss"),
        endDate: endMoment.format("YYYY-MM-DD"),
        endTime: endMoment.format("HH:mm:ss"),
        status: activity.status
      };
      this.showAddDialog = true;
    },
    viewActivity(activity) {
      this.viewingActivity = true;
      const startMoment = moment(activity.startDate);
      const endMoment = moment(activity.endDate);
      this.form = {
        activityName: activity.activityName,
        activityDesc: activity.activityDesc,
        startDate: startMoment.format("YYYY-MM-DD"),
        startTime: startMoment.format("HH:mm:ss"),
        endDate: endMoment.format("YYYY-MM-DD"),
        endTime: endMoment.format("HH:mm:ss"),
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
        await axios.delete(`/api/activity/${activity.activityId}`);
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
        startDate: moment().format("YYYY-MM-DD"),
        startTime: "00:00:00",
        endDate: moment().add(1, 'day').format("YYYY-MM-DD"),
        endTime: "00:00:00",
        status: "INACTIVE"
      };
    },
    async saveActivity() {
      // 如果是查看模式，不允许保存
      if (this.viewingActivity) {
        return;
      }
      
      try {
        // 合并日期和时间
        const startDateTime = `${this.form.startDate}T${this.form.startTime}`;
        const endDateTime = `${this.form.endDate}T${this.form.endTime}`;
        
        const activityData = {
          ...this.form,
          startDate: startDateTime,
          endDate: endDateTime
        };
        
        if (this.editingActivity) {
          await axios.put(`/api/activity/${this.editingActivity.activityId}`, activityData);
        } else {
          await axios.post("/api/activity/create", activityData);
        }
        this.closeDialog();
        this.fetchActivities();
      } catch (error) {
        console.error("保存活动失败:", error);
        alert("保存活动失败");
      }
    },
    toggleSort(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortField = field;
        this.sortDirection = 'asc';
      }
      this.currentPage = 1; // 排序后重置到第一页
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
    async toggleActivityStatus(activity) {
      try {
        const newStatus = activity.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        await axios.put(`/api/activity/${activity.activityId}/status`, {
          status: newStatus
        });
        activity.status = newStatus;
        alert("状态更新成功");
      } catch (error) {
        console.error("更新活动状态失败:", error);
        alert("更新活动状态失败");
      }
    }
  },
  mounted() {
    this.fetchActivities();
  }
};
</script>

<style scoped>
@import "../assets/styles/button-styles.css";
@import "../assets/styles/common.css";
@import "../assets/styles/activity-management.css";
@import "../assets/styles/modal.css";
@import "../assets/styles/responsive.css";
</style>