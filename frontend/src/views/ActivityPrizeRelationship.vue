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
              <th class="sortable" @click="handleSortChange('startDate')">
                开始时间
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'startDate' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'startDate' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="handleSortChange('endDate')">
                结束时间
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'endDate' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'endDate' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="activity in paginatedActivities" :key="activity.activityId">
              <td>{{ activity.activityName }}</td>
              <td>{{ activity.activityDesc }}</td>
              <td>{{ formatDateTime(activity.startDate) }}</td>
              <td>{{ formatDateTime(activity.endDate) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn view-btn" @click="viewActivity(activity)">
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
      <div class="pagination" v-if="totalPages > 1">
        <button 
          @click="changePage(currentPage - 1)" 
          :disabled="currentPage === 1" 
          class="pagination-btn"
        >
          上一页
        </button>
        <span>第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
        <button 
          @click="changePage(currentPage + 1)" 
          :disabled="currentPage === totalPages" 
          class="pagination-btn"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 活动详情模态框 -->
    <div v-if="viewingActivity" class="modal-overlay">
      <div class="modal-content modal-content-scrollable">
        <div class="modal-header">
          <h3>活动奖品关系</h3>
          <button class="close-btn" @click="closeView">&times;</button>
        </div>
        <div class="modal-body modal-body-scrollable">
          <form>
            <div class="form-group">
            <label>活动名称:</label>
              <input type="text" v-model="viewForm.activityName" class="readonly">
          </div>
            <div class="form-group">
            <label>描述:</label>
              <textarea v-model="viewForm.activityDesc" class="readonly"></textarea>
          </div>
            <div class="form-group">
            <label>开始日期:</label>
              <input type="text" v-model="viewForm.startDate" class="readonly">
            </div>
            <div class="form-group">
              <label>开始时间:</label>
              <input type="text" v-model="viewForm.startTime" class="readonly">
          </div>
            <div class="form-group">
            <label>结束日期:</label>
              <input type="text" v-model="viewForm.endDate" class="readonly">
            </div>
            <div class="form-group">
              <label>结束时间:</label>
              <input type="text" v-model="viewForm.endTime" class="readonly">
          </div>
            <div class="prize-list-container">
            <h4>奖品列表</h4>
              <div v-for="(prize, index) in viewForm.prizes" :key="index" class="prize-item-view">
              <div class="prize-info">
                <span class="prize-name">{{ prize.prizeName }}</span>
                <div class="prize-inputs">
                  <div class="input-group">
                      <label>概率 (%):</label>
                      <span>{{ (prize.probability * 100).toFixed(2) }}%</span>
                  </div>
                  <div class="input-group">
                    <label>库存:</label>
                    <span>{{ prize.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeView">关闭</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 创建/编辑活动表单 -->
    <div v-if="showCreateForm" class="modal-overlay">
      <div class="modal-content modal-content-scrollable">
        <div class="modal-header">
          <h3>{{ editingActivity ? '编辑活动奖品关系' : '创建活动奖品关系' }}</h3>
          <button class="close-btn" @click="closeForm">&times;</button>
        </div>
        <div class="modal-body modal-body-scrollable">
          <!-- 错误提示 -->
          <div v-if="showError" class="error-message">
            <i class="icon-error"></i>
            <span>{{ errorMessage }}</span>
            <button class="close-error-btn" @click="hideError">&times;</button>
          </div>
          
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
              <div class="prize-list-container">
              <div v-for="(prize, index) in formData.prizes" :key="index" class="prize-item">
                <div class="prize-info">
                  <span class="prize-name">{{ prize.prizeName }}</span>
                  <div class="prize-inputs">
                    <div class="input-group">
                        <label>概率 (%):</label>
                      <input
                        type="number"
                        v-model="prize.probability"
                          placeholder="概率 (0-100)"
                        min="0"
                          max="100"
                          step="0.01"
                          @input="validateProbability(prize)"
                        required
                      />
                    </div>
                    <div class="input-group">
                      <label>库存:</label>
                      <input
                        type="number"
                        v-model="prize.quantity"
                        placeholder="库存"
                          min="1"
                          @input="validateQuantity(prize)"
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
              
              <!-- 概率总和显示 -->
              <div v-if="formData.prizes.length > 0" class="probability-summary">
                <span class="probability-total" :class="{ 'error': totalProbability !== 100 }">
                  概率总和: {{ totalProbability.toFixed(2) }}%
                  <span v-if="totalProbability !== 100" class="error-hint">
                    (必须为100%)
                  </span>
                </span>
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

    <!-- 错误提示组件 -->
    <div v-if="showError" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import axios from "axios";
import moment from "moment";
import '@/assets/styles/common.css';
import '@/assets/styles/modal.css';
import '@/assets/styles/responsive.css';

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
      itemsPerPage: 10,
      sortField: 'startDate', // 默认按开始时间排序
      sortDirection: 'desc', // 默认倒序
      errorMessage: '', // 添加错误信息
      showError: false, // 控制错误提示显示
      viewingActivity: false,
      viewForm: {
        activityName: "",
        activityDesc: "",
        startDate: "",
        startTime: "",
        endDate: "",
        endTime: "",
        prizes: []
      },
    };
  },
  computed: {
    filteredActivities() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.activities.filter(activity => {
        return activity.activityName && activity.activityName.toLowerCase().includes(query);
      });
    },
    sortedActivities() {
      let activitiesToSort = this.filteredActivities; // 对过滤后的全量数据进行排序
      
      if (!this.sortField) return activitiesToSort;
      
      return [...activitiesToSort].sort((a, b) => {
        const aValue = new Date(a[this.sortField]);
        const bValue = new Date(b[this.sortField]);
        
        if (this.sortDirection === 'asc') {
          return aValue - bValue;
        } else {
          return bValue - aValue;
        }
      });
    },
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.sortedActivities.slice(start, end); // 对排序后的数据进行分页
    },
    totalPages() {
      return Math.ceil(this.sortedActivities.length / this.itemsPerPage); // 使用排序后的数据计算总页数
    },
    
    totalProbability() {
      return this.formData.prizes.reduce((sum, prize) => sum + parseFloat(prize.probability || 0), 0);
    },
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
      // 重置表单状态
      this.selectedActivityId = null;
      this.selectedPrizeId = null;
      this.formData.prizes = [];
      this.editingActivity = null;
      this.showCreateForm = true; // 显示奖品选择表单
    },
    addPrize() {
      const selectedPrize = this.availablePrizes.find(prize => prize.prizeId === this.selectedPrizeId);
      if (selectedPrize) {
        this.formData.prizes.push({
          prizeId: selectedPrize.prizeId,
          prizeName: selectedPrize.prizeName,
          probability: 1, // 改为1，表示1%
          quantity: 1 // 改为1，表示默认库存为1
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
    async viewActivity(activity) {
      try {
        const response = await axios.get(`/api/activity-prize-relationship/${activity.activityId}`);
        const act = response.data;
        // 拆分时间
        const start = moment(act.startDate);
        const end = moment(act.endDate);
        this.viewForm = {
          activityName: act.activityName,
          activityDesc: act.activityDesc,
          startDate: start.format("YYYY-MM-DD"),
          startTime: start.format("HH:mm:ss"),
          endDate: end.format("YYYY-MM-DD"),
          endTime: end.format("HH:mm:ss"),
          prizes: act.prizes || []
        };
        this.viewingActivity = true;
      } catch (error) {
        console.error("获取活动详情失败：", error);
      }
    },
    async editActivity(activity) {
      try {
        // 先重置表单状态，确保没有残留数据
        this.resetFormData();
        
        const response = await axios.get(`/api/activity-prize-relationship/${activity.activityId}/details`);
        const activityDetails = response.data;

        this.editingActivity = activity;
        this.selectedActivityId = activity.activityId;
        
        // 将概率从小数转换为百分比
        this.formData.prizes = (activityDetails.prizes || []).map(prize => ({
          ...prize,
          probability: prize.probability * 100 // 转换为百分比
        }));
        
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
        // 隐藏之前的错误信息
        this.hideError();
        
        // 校验是否有奖品
        if (this.formData.prizes.length === 0) {
          this.errorMessage = '请至少添加一个奖品';
          this.showError = true;
          return;
        }
        
        // 校验概率总和是否为100%
        const totalProbability = this.formData.prizes.reduce((sum, prize) => sum + parseFloat(prize.probability || 0), 0);
        if (Math.abs(totalProbability - 100) > 0.01) { // 允许0.01%的误差
          this.errorMessage = `概率总和必须为100%，当前总和为${totalProbability.toFixed(2)}%`;
          this.showError = true;
          return;
        }
        
        // 校验库存是否都大于0
        const invalidPrizes = this.formData.prizes.filter(prize => !prize.quantity || prize.quantity <= 0);
        if (invalidPrizes.length > 0) {
          const prizeNames = invalidPrizes.map(prize => prize.prizeName).join('、');
          this.errorMessage = `以下奖品的库存必须大于0：${prizeNames}`;
          this.showError = true;
          return;
        }
        
        const payload = {
          activityId: this.selectedActivityId,
          prizes: this.formData.prizes.map(item => ({
            prizeId: item.prizeId,
            probability: item.probability / 100, // 将百分比转换为小数
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
        
        // 显示错误信息
        if (error.response && error.response.data) {
          // 使用 BaseResponse 格式的错误信息
          const responseData = error.response.data;
          this.errorMessage = responseData.message || '操作失败，请重试';
        } else if (error.message) {
          // 网络错误或其他错误
          this.errorMessage = error.message;
        } else {
          this.errorMessage = '操作失败，请重试';
        }
        this.showError = true;
      }
    },
    closeForm() {
      this.showCreateForm = false;
      this.resetFormData(); // 使用统一的重置方法
      this.hideError(); // 清除错误信息
    },
    closeView() {
      this.viewingActivity = false;
      this.viewForm = {
        activityName: "",
        activityDesc: "",
        startDate: "",
        startTime: "",
        endDate: "",
        endTime: "",
        prizes: []
      };
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD");
    },
    formatDateTime(date) {
      return moment(date).format("YYYY-MM-DD HH:mm:ss");
    },
    handleSortChange(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortField = field;
        this.sortDirection = 'asc';
      }
    },
    handleSearch() {
      // 搜索时重置到第一页
      this.currentPage = 1;
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
    validateProbability(prize) {
      // 确保概率在0-100之间
      if (prize.probability < 0) {
        prize.probability = 0;
      } else if (prize.probability > 100) {
        prize.probability = 100;
      }
    },
    validateQuantity(prize) {
      // 确保库存大于等于1
      if (prize.quantity < 1) {
        prize.quantity = 1;
      }
    },
    hideError() {
      this.showError = false;
      this.errorMessage = '';
    },
    // 新增统一的重置表单数据方法
    resetFormData() {
      this.editingActivity = null;
      this.formData.prizes = [];
      this.selectedActivityId = null;
      this.selectedPrizeId = null;
    },
  },
  mounted() {
    this.fetchActivities();
    this.fetchAvailableActivities();
    this.fetchAvailablePrizes();
  }
};
</script>


<style scoped>
@import "../assets/styles/button-styles.css";
@import "../assets/styles/common.css";
@import "../assets/styles/modal.css";
@import "../assets/styles/responsive.css";
@import "../assets/styles/activity-prize-relationship.css";
</style>