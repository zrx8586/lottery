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
              <th>状态</th>
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
                  {{ prize.isActive ? '已激活' : '未激活' }}
                </span>
              </td>
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
              <input v-model="formData.prizeName" type="text" required :readonly="viewingPrize" />
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
              ></textarea>
            </div>
            <div class="form-group">
              <label>图片链接</label>
              <input v-model="formData.prizeImageUrl" type="text" :readonly="viewingPrize" />
            </div>
            <div class="form-group">
              <label>类别</label>
              <input v-model="formData.prizeCategory" type="text" :readonly="viewingPrize" />
            </div>
            <div class="form-group">
              <label>价值</label>
              <input 
                v-model="formData.prizeValue" 
                type="number" 
                :readonly="viewingPrize"
                min="0"
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
              />
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="formData.isActive" :disabled="viewingPrize">
                <option :value="true">已激活</option>
                <option :value="false">未激活</option>
              </select>
            </div>
            <div v-if="!viewingPrize" class="form-actions">
              <button type="button" class="cancel-btn" @click="closeForm">取消</button>
              <button type="submit" class="save-btn">{{ editingPrize ? '更新' : '创建' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

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
      sortField: "",
      sortDirection: "asc",
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
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      
      let sortedPrizes = [...this.filteredPrizes];
      
      if (this.sortField) {
        sortedPrizes.sort((a, b) => {
          const aValue = a[this.sortField];
          const bValue = b[this.sortField];
          
          if (typeof aValue === 'number' && typeof bValue === 'number') {
            return this.sortDirection === 'asc' ? aValue - bValue : bValue - aValue;
          }
          return 0;
        });
      }
      
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
  },
  mounted() {
    this.fetchPrizes();
  },
};
</script>

<style scoped>
@import "../assets/styles/button-styles.css";
@import "../assets/styles/common.css";

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

.status-badge.inactive {
  background-color: #fef0f0;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 8px;
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
</style>