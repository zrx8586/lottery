<template>
  <div class="lottery-container">
    <div class="lottery-box">
      <div class="lottery-header">
        <h2>幸运抽奖</h2>
        <p>选择一个活动参与抽奖</p>
      </div>

      <div class="activity-selector">
        <select v-model="selectedActivityId" class="activity-select">
          <option value="" disabled>请选择活动</option>
          <option v-for="activity in activeActivities" :key="activity.id" :value="activity.id">
            {{ activity.activityName }}
          </option>
        </select>
      </div>

      <div class="lottery-wheel" :class="{ 'spinning': isSpinning }" @click="startLottery">
        <div class="wheel-inner">
          <div class="wheel-content">
            <span v-if="!isSpinning && !lastResult">点击开始抽奖</span>
            <span v-else-if="isSpinning">抽奖中...</span>
            <span v-else class="result-text">{{ lastResult }}</span>
          </div>
        </div>
      </div>

      <div class="lottery-records" v-if="records.length > 0">
        <h3>抽奖记录</h3>
        <div class="records-list">
          <div v-for="record in records" :key="record.id" class="record-item">
            <span class="record-time">{{ formatDate(record.wonAt) }}</span>
            <span class="record-activity">{{ record.activityName }}</span>
            <span class="record-prize">{{ record.prizeName }}</span>
          </div>
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
      activeActivities: [],
      selectedActivityId: "",
      isSpinning: false,
      lastResult: "",
      records: [],
      username: localStorage.getItem("username") || ""
    };
  },
  methods: {
    async fetchActiveActivities() {
      try {
        const response = await axios.get("/api/activity/all");
        this.activeActivities = response.data.filter(activity => 
          activity.status === "ACTIVE"
        );
      } catch (error) {
        console.error("获取活动列表失败:", error);
        alert("获取活动列表失败");
      }
    },
    async startLottery() {
      if (!this.selectedActivityId) {
        alert("请先选择活动");
        return;
      }

      if (this.isSpinning) return;

      this.isSpinning = true;
      this.lastResult = "";

      try {
        const response = await axios.post("/api/lottery/draw", null, {
          params: {
            username: this.username,
            activityId: this.selectedActivityId
          }
        });

        const result = response.data.data;
        if (result.success) {
          this.lastResult = `恭喜获得：${result.prizeName}`;
        } else {
          this.lastResult = result.message;
        }
      } catch (error) {
        console.error("抽奖失败:", error);
        this.lastResult = "抽奖失败，请稍后重试";
      } finally {
        this.isSpinning = false;
        this.fetchRecords();
      }
    },
    async fetchRecords() {
      try {
        const response = await axios.get("/api/lottery/records", {
          params: {
            activityId: this.selectedActivityId
          }
        });
        this.records = response.data.data;
      } catch (error) {
        console.error("获取抽奖记录失败:", error);
      }
    },
    formatDate(date) {
      return moment(date).format("YYYY-MM-DD HH:mm:ss");
    }
  },
  mounted() {
    this.fetchActiveActivities();
  },
  watch: {
    selectedActivityId() {
      if (this.selectedActivityId) {
        this.fetchRecords();
      }
    }
  }
};
</script>

<style scoped>
.lottery-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.lottery-box {
  background: white;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 600px;
  text-align: center;
}

.lottery-header {
  margin-bottom: 30px;
}

.lottery-header h2 {
  color: #2c3e50;
  font-size: 28px;
  margin-bottom: 10px;
}

.lottery-header p {
  color: #666;
  font-size: 16px;
}

.activity-selector {
  margin-bottom: 30px;
}

.activity-select {
  width: 100%;
  padding: 12px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  color: #333;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.activity-select:focus {
  border-color: #667eea;
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.lottery-wheel {
  width: 200px;
  height: 200px;
  margin: 0 auto 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.lottery-wheel:hover {
  transform: scale(1.05);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.wheel-inner {
  width: 180px;
  height: 180px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wheel-content {
  text-align: center;
  padding: 20px;
}

.wheel-content span {
  font-size: 16px;
  color: #2c3e50;
  font-weight: 500;
}

.result-text {
  color: #67c23a !important;
  font-weight: bold !important;
}

.spinning {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.lottery-records {
  margin-top: 30px;
  text-align: left;
}

.lottery-records h3 {
  color: #2c3e50;
  font-size: 18px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.records-list {
  max-height: 200px;
  overflow-y: auto;
}

.record-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.record-time {
  color: #909399;
  font-size: 14px;
}

.record-activity {
  color: #606266;
  font-weight: 500;
}

.record-prize {
  color: #67c23a;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .lottery-box {
    padding: 30px 20px;
  }

  .lottery-wheel {
    width: 180px;
    height: 180px;
  }

  .wheel-inner {
    width: 160px;
    height: 160px;
  }

  .record-item {
    flex-direction: column;
    gap: 5px;
  }
}

@media (max-width: 480px) {
  .lottery-box {
    padding: 20px 15px;
  }

  .lottery-header h2 {
    font-size: 24px;
  }

  .lottery-wheel {
    width: 160px;
    height: 160px;
  }

  .wheel-inner {
    width: 140px;
    height: 140px;
  }

  .wheel-content span {
    font-size: 14px;
  }
}
</style> 