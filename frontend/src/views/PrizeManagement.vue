<template>
  <div class="prize-management">
    <h1>奖品管理</h1>

    <!-- 搜索和创建按钮 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索奖品名称..." class="search-input" />
      <button class="btn btn-create" @click="showCreateForm = true">创建新奖品</button>
    </div>

    <!-- 奖品列表 -->
    <div class="prize-list-container">
      <table class="prize-table">
        <thead>
        <tr>
          <th>奖品名称</th>
          <th>描述</th>
          <th>图片链接</th>
          <th>类别</th>
          <th>价值</th>
          <th>库存</th>
          <th>是否激活</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="prize in paginatedPrizes" :key="prize.prizeId">
          <td>{{ prize.prizeName }}</td>
          <td>{{ prize.prizeDesc }}</td>
          <td>{{ prize.prizeImageUrl }}</td>
          <td>{{ prize.prizeCategory }}</td>
          <td>{{ prize.prizeValue }}</td>
          <td>{{ prize.stockQuantity }}</td>
          <td>{{ prize.isActive ? '是' : '否' }}</td>
          <td>
            <button class="btn btn-view" @click="viewPrize(prize)">查看</button>
            <button class="btn btn-edit" @click="editPrize(prize)">编辑</button>
            <button class="btn btn-delete" @click="deletePrize(prize.prizeId)">删除</button>
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

    <!-- 查看奖品模态框 -->
    <div v-if="viewingPrize" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeView">&times;</span>
        <h2>奖品详情</h2>
        <p><strong>奖品名称:</strong> {{ formData.prizeName }}</p>
        <p><strong>描述:</strong> {{ formData.prizeDesc }}</p>
        <p><strong>图片链接:</strong> {{ formData.prizeImageUrl }}</p>
        <p><strong>类别:</strong> {{ formData.prizeCategory }}</p>
        <p><strong>价值:</strong> {{ formData.prizeValue }}</p>
        <p><strong>库存:</strong> {{ formData.stockQuantity }}</p>
        <p><strong>是否激活:</strong> {{ formData.isActive ? '是' : '否' }}</p>
      </div>
    </div>

    <!-- 创建/编辑奖品表单 -->
    <div v-if="showCreateForm || editingPrize" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeForm">&times;</span>
        <h2>{{ editingPrize ? '编辑奖品' : '创建新奖品' }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>奖品名称:</label>
            <input v-model="formData.prizeName" required />
          </div>
          <div class="form-group">
            <label>描述:</label>
            <textarea v-model="formData.prizeDesc" required></textarea>
          </div>
          <div class="form-group">
            <label>图片链接:</label>
            <input v-model="formData.prizeImageUrl" />
          </div>
          <div class="form-group">
            <label>类别:</label>
            <input v-model="formData.prizeCategory" />
          </div>
          <div class="form-group">
            <label>价值:</label>
            <input type="number" v-model="formData.prizeValue" />
          </div>
          <div class="form-group">
            <label>库存:</label>
            <input type="number" v-model="formData.stockQuantity" required />
          </div>
          <div class="form-group">
            <label>是否激活:</label>
            <select v-model="formData.isActive">
              <option :value="true">是</option>
              <option :value="false">否</option>
            </select>
          </div>
          <button type="submit" class="btn btn-submit">{{ editingPrize ? '更新' : '创建' }}</button>
        </form>
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
      return this.filteredPrizes.slice(start, end);
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
  },
  mounted() {
    this.fetchPrizes();
  },
};
</script>

<style scoped>
/* 样式与 ActivityManagement.vue 保持一致 */
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

.prize-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.prize-table th,
.prize-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid #ddd;
}

.prize-table th {
  background-color: #f4f4f4;
  color: #333;
}

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