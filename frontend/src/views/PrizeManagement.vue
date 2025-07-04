<template>
  <div class="page-container">
    <div class="page-header">
      <h2>奖品管理</h2>
      <button class="add-btn" @click="showCreateForm = true">
        <i class="icon-add"></i> 新增奖品
      </button>
    </div>

    <div class="content-box">
      <div class="search-bar">
        <div class="search-group">
          <i class="icon-search"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索奖品名称..." 
            @input="handleSearch"
          />
        </div>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>奖品名称</th>
              <th>描述</th>
              <th>图片链接</th>
              <th>类别</th>
              <th class="sortable" @click="toggleSort('stockQuantity')">
                库存
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'stockQuantity' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'stockQuantity' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="toggleSort('prizeValue')">
                价值
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'prizeValue' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'prizeValue' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="toggleSort('isActive')">
                状态
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'isActive' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'isActive' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th class="sortable" @click="toggleSort('datachangeLastTime')">
                更新时间
                <span class="sort-arrow">
                  <i class="arrow up" :class="{ active: sortField === 'datachangeLastTime' && sortDirection === 'asc' }"></i>
                  <i class="arrow down" :class="{ active: sortField === 'datachangeLastTime' && sortDirection === 'desc' }"></i>
                </span>
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="prize in paginatedPrizes" :key="prize.prizeId">
              <td>{{ prize.prizeName }}</td>
              <td>{{ prize.prizeDesc }}</td>
              <td>{{ prize.prizeImageUrl }}</td>
              <td>{{ prize.prizeCategory }}</td>
              <td>{{ prize.stockQuantity }}</td>
              <td>{{ prize.prizeValue }}</td>
              <td>
                <span :class="['status-badge', prize.isActive ? 'active' : 'inactive']">
                  {{ prize.isActive ? '有效' : '无效' }}
                </span>
              </td>
              <td>{{ formatDateTime(prize.datachangeLastTime) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn view-btn" @click="viewPrize(prize)">
                    <i class="icon-view"></i>
                  </button>
                  <button class="action-btn edit-btn" @click="editPrize(prize)">
                    <i class="icon-edit"></i>
                  </button>
                  <button class="action-btn delete-btn" @click="confirmDelete(prize.prizeId)">
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

    <!-- 查看/编辑奖品模态框 -->
    <div v-if="showCreateForm || viewingPrize" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingPrize ? '编辑奖品' : viewingPrize ? '查看奖品' : '新增奖品' }}</h3>
          <button class="close-btn" @click="viewingPrize ? closeView() : closeForm()">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label>奖品名称</label>
              <input 
                v-model="formData.prizeName" 
                type="text" 
                required 
                :readonly="viewingPrize"
                :class="{ 'readonly': viewingPrize }"
              />
            </div>
            <div class="form-group">
              <label>描述</label>
              <textarea 
                v-model="formData.prizeDesc" 
                required 
                :readonly="viewingPrize"
                class="form-textarea"
                rows="3"
                placeholder="请输入奖品描述..."
                :class="{ 'readonly': viewingPrize }"
              ></textarea>
            </div>
            <div class="form-group">
              <label>图片链接</label>
              <input 
                v-model="formData.prizeImageUrl" 
                type="text" 
                :readonly="viewingPrize"
                :class="{ 'readonly': viewingPrize }"
              />
            </div>
            <div class="form-group">
              <label>类别</label>
              <input 
                v-model="formData.prizeCategory" 
                type="text" 
                :readonly="viewingPrize"
                :class="{ 'readonly': viewingPrize }"
              />
            </div>
            <div class="form-group">
              <label>价值</label>
              <input 
                v-model="formData.prizeValue" 
                type="number" 
                :readonly="viewingPrize"
                min="0"
                :class="{ 'readonly': viewingPrize }"
              />
            </div>
            <div class="form-group">
              <label>库存</label>
              <input 
                v-model="formData.stockQuantity" 
                type="number" 
                required 
                :readonly="viewingPrize"
                min="0"
                :class="{ 'readonly': viewingPrize }"
              />
            </div>
            <div class="form-group">
              <label>状态</label>
              <select 
                v-model="formData.isActive" 
                :disabled="viewingPrize"
                :class="{ 'readonly': viewingPrize }"
              >
                <option :value="true">有效</option>
                <option :value="false">无效</option>
              </select>
            </div>
            <div v-if="!viewingPrize" class="form-actions">
              <button type="button" class="cancel-btn" @click="closeForm">取消</button>
              <button type="submit" class="save-btn">{{ editingPrize ? '更新' : '创建' }}</button>
            </div>
            <div v-else class="form-actions">
              <button type="button" class="cancel-btn" @click="closeView">关闭</button>
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
      prizes: [],
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 10,
      showCreateForm: false,
      editingPrize: null,
      viewingPrize: false,
      formData: {
        prizeName: "",
        prizeDesc: "",
        prizeImageUrl: "",
        prizeCategory: "",
        prizeValue: 0,
        stockQuantity: 0,
        isActive: true,
      },
      sortField: 'datachangeLastTime',
      sortDirection: 'desc',
    };
  },
  computed: {
    filteredPrizes() {
      const query = this.searchQuery.trim().toLowerCase();
      return this.prizes.filter(prize =>
          prize.prizeName.toLowerCase().includes(query)
      );
    },
    paginatedPrizes() {
      // 先对全量数据进行排序
      let sortedPrizes = [...this.filteredPrizes];
      
      if (this.sortField) {
        sortedPrizes.sort((a, b) => {
          const aValue = a[this.sortField];
          const bValue = b[this.sortField];
          
          if (this.sortField === 'isActive') {
            return this.sortDirection === 'asc' 
              ? (aValue === bValue ? 0 : aValue ? -1 : 1)
              : (aValue === bValue ? 0 : aValue ? 1 : -1);
          }
          
          if (this.sortField === 'datachangeLastTime') {
            const dateA = new Date(aValue);
            const dateB = new Date(bValue);
            return this.sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
          }
          
          if (typeof aValue === 'number' && typeof bValue === 'number') {
            return this.sortDirection === 'asc' ? aValue - bValue : bValue - aValue;
          }
          return 0;
        });
      }
      
      // 排序后再进行分页
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return sortedPrizes.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredPrizes.length / this.itemsPerPage);
    },
  },
  methods: {
    async fetchPrizes() {
      try {
        const response = await axios.get("/api/prize/all");
        this.prizes = response.data;
      } catch (error) {
        console.error("加载奖品列表失败：", error);
      }
    },
    viewPrize(prize) {
      this.viewingPrize = true;
      this.formData = { ...prize };
    },
    editPrize(prize) {
      this.editingPrize = prize;
      this.formData = { ...prize };
      this.showCreateForm = true;
    },
    async deletePrize(prizeId) {
      if (confirm("确定要删除这个奖品吗？")) {
        try {
          await axios.delete(`/api/prize/${prizeId}`);
          this.prizes = this.prizes.filter(prize => prize.prizeId !== prizeId);
        } catch (error) {
          console.error("删除奖品失败：", error);
        }
      }
    },
    async submitForm() {
      try {
        if (this.editingPrize) {
          const response = await axios.put(`/api/prize/${this.editingPrize.prizeId}`, this.formData);
          const index = this.prizes.findIndex(prize => prize.prizeId === this.editingPrize.prizeId);
          this.prizes.splice(index, 1, response.data);
        } else {
          const response = await axios.post("/api/prize/create", this.formData);
          this.prizes.push(response.data);
        }
        this.closeForm();
      } catch (error) {
        console.error("保存奖品失败：", error);
      }
    },
    closeForm() {
      this.showCreateForm = false;
      this.editingPrize = null;
      this.formData = {
        prizeName: "",
        prizeDesc: "",
        prizeImageUrl: "",
        prizeCategory: "",
        prizeValue: 0,
        stockQuantity: 0,
        isActive: true,
      };
    },
    closeView() {
      this.viewingPrize = false;
    },
    toggleSort(field) {
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
    
    formatDateTime(date) {
      return moment(date).format("YYYY-MM-DD HH:mm:ss");
    },
  },
  mounted() {
    this.fetchPrizes();
  },
};
</script>

<style scoped>
@import "../assets/styles/button-styles.css";
@import "../assets/styles/common.css";
@import "../assets/styles/activity-management.css";
@import "../assets/styles/modal.css";
@import "../assets/styles/responsive.css";
@import "../assets/styles/prize-management.css";
</style>