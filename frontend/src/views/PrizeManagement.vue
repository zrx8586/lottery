<template>
  <div class="prize-management">
    <h1>奖品管理</h1>

    <!-- 搜索和创建按钮 -->
    <div class="action-bar">
      <input v-model="searchQuery" placeholder="搜索奖品名称..." class="search-input"/>
      <button class="btn btn-create" @click="showCreateForm = true">创建新奖品</button>
    </div>

    <!-- 奖品列表 -->
    <div class="prize-list-container">
      <table class="prize-table">
        <thead>
        <tr>
          <th>奖品名称</th>
          <th>概率</th>
          <th>库存</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="prize in paginatedPrizes" :key="prize.id">
          <td>{{ prize.prize.prizeName }}</td>
          <td>{{ prize.probability }}%</td>
          <td>{{ prize.quantity }}</td>
          <td>
            <button class="btn btn-view" @click="viewPrize(prize.prize.prizeId)">查看</button>
            <button class="btn btn-edit" @click="editPrize(prize)">编辑</button>
            <button class="btn btn-delete" @click="deletePrize(prize.id)">删除</button>
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

    <!-- 奖品详情模态框 -->
    <div v-if="selectedPrize" class="modal">
      <div class="modal-content">
        <span class="close" @click="selectedPrize = null">&times;</span>
        <h2>奖品详情</h2>
        <p><strong>奖品名称:</strong> {{ selectedPrize.prize.prizeName }}</p>
        <p><strong>概率:</strong> {{ selectedPrize.probability }}%</p>
        <p><strong>库存:</strong> {{ selectedPrize.quantity }}</p>
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
            <input v-model="formData.prize.prizeName" required/>
          </div>
          <div class="form-group">
            <label>概率:</label>
            <input type="number" v-model="formData.probability" required/>
          </div>
          <div class="form-group">
            <label>库存:</label>
            <input type="number" v-model="formData.quantity" required/>
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
      selectedPrize: null,
      showCreateForm: false,
      editingPrize: null,
      formData: {
        prizeName: "",
        probability: 0,
        quantity: 0
      },
      searchQuery: "",
      currentPage: 1,
      itemsPerPage: 10
    };
  },
  computed: {
    filteredPrizes() {
      console.log(this.prizes);
      // return this.prizes;
      // 修复过滤逻辑，确保根据搜索查询过滤奖品名称
      return this.prizes.filter(item =>
          item.prize.prizeName.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
    paginatedPrizes() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredPrizes.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredPrizes.length / this.itemsPerPage);
    }
  },
  methods: {
    async fetchPrizes() {
      try {
        const response = await axios.get("/api/activity-prizes/1"); // 示例活动ID
        this.prizes = response.data;
      } catch (error) {
        console.error("加载奖品数据失败：", error);
      }
    },
    async viewPrize(prizeId) {
      try {
        const response = await axios.get(`/api/activity-prizes/prize/${prizeId}`);
        this.selectedPrize = response.data;
      } catch (error) {
        console.error("获取奖品详情失败：", error);
      }
    },
    async editPrize(prize) {
      this.editingPrize = prize;
      this.formData = {...prize};
      this.showCreateForm = true;
    },
    async deletePrize(prizeId) {
      if (confirm("确定要删除这个奖品吗？")) {
        try {
          await axios.delete(`/api/activity-prizes/prize/${prizeId}`);
          this.prizes = this.prizes.filter(p => p.id !== prizeId);
        } catch (error) {
          console.error("删除奖品失败：", error);
        }
      }
    },
    async submitForm() {
      try {
        if (this.editingPrize) {
          const response = await axios.put(`/api/activity-prizes/prize/${this.editingPrize.id}`, this.formData);
          const index = this.prizes.findIndex(p => p.id === this.editingPrize.id);
          this.prizes.splice(index, 1, response.data);
        } else {
          const response = await axios.post("/api/activity-prizes/create", this.formData);
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
        name: "",
        probability: 0,
        quantity: 0
      };
    }
  },
  mounted() {
    this.fetchPrizes();
  }
};
</script>

<style scoped>
/* 样式与 ActivityManagement.vue 类似 */
.prize-management {
  font-family: Arial, sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
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