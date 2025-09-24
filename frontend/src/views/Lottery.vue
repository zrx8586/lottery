<template>
  <div class="lottery-page">
    <div class="header-card">
      <h2>幸运抽奖</h2>
      <p>选择活动并点击开始抽奖或管理抽奖次数</p>
    </div>

    <div class="tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'draw' }" @click="activeTab = 'draw'">抽奖</button>
      <button class="tab-btn" :class="{ active: activeTab === 'manage' }" @click="activeTab = 'manage'">次数管理</button>
    </div>

    <div class="controls">
      <div class="control-item">
        <label>活动：</label>
        <select v-model="selectedActivityId" class="select">
          <option value="" disabled>请选择活动</option>
          <option v-for="a in activities" :key="a.id || a.activityId" :value="a.id || a.activityId">
            {{ a.activityName }}
          </option>
        </select>
      </div>
      <div class="control-item" v-if="activeTab === 'draw'">
        <button class="btn primary" :disabled="loading || !selectedActivityId" @click="startDraw">
          {{ loading ? '抽奖中...' : '开始抽奖' }}
        </button>
        <span class="hint">剩余：{{ attemptsText }}</span>
        <button class="btn" :disabled="recordsLoading" @click="loadRecords">
          刷新记录
        </button>
      </div>
      <div class="control-item" v-else>
        <label>用户名：</label>
        <input class="input" v-model.trim="manageUsername" placeholder="输入用户名" />
        <label>次数：</label>
        <input class="input" type="number" min="0" v-model.number="manageAttempts" />
        <button class="btn primary" :disabled="manageLoading || !selectedActivityId || !manageUsername" @click="saveAttempts">保存</button>
        <button class="btn" :disabled="manageLoading || !selectedActivityId || !manageUsername" @click="loadManageState">读取</button>
      </div>
    </div>

    <div class="status-bar" v-if="selectedActivity">
      <div class="status-item">
        <span class="label">活动状态：</span>
        <span class="badge" :class="activityStateClass">{{ activityStateText }}</span>
      </div>
      <div class="status-item">
        <span class="label">活动时间：</span>
        <span class="value">{{ formatTime(selectedActivity.startDate) }} ~ {{ formatTime(selectedActivity.endDate) }}</span>
      </div>
      <div class="status-item">
        <span class="label">剩余次数：</span>
        <span class="value attempts" :class="{ zero: (attemptsTextNum) <= 0 }">{{ attemptsText }}</span>
      </div>
    </div>

    <div v-if="activeTab === 'draw'" class="wheel" :class="{ spinning: loading }">
      <div class="wheel-inner">
        <div class="wheel-content">
          <span v-if="!loading && !result.message">点击“开始抽奖”</span>
          <span v-else-if="loading">抽奖中...</span>
          <span v-else class="result-text" :class="{ success: result.success, failure: !result.success }">
            {{ result.message }}
            <template v-if="result.success && result.prizeName">：{{ result.prizeName }}</template>
          </span>
        </div>
      </div>
    </div>

    <div v-if="activeTab === 'draw' && records.length" class="records">
      <h3>中奖记录</h3>
      <div class="record-list">
        <div class="record" v-for="r in records" :key="r.id || r.recordId || r.wonAt + r.username">
          <span class="time">{{ formatTime(r.wonAt) }}</span>
          <span class="user">{{ r.username }}</span>
          <span class="activity">{{ r.activityName }}</span>
          <span class="prize">{{ r.prizeName }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Lottery',
  data() {
    return {
      activities: [],
      selectedActivityId: '',
      loading: false,
      recordsLoading: false,
      result: { success: false, message: '', prizeName: '' },
      records: [],
      username: '',
      userId: null,
      attempts: null,
      activeTab: 'draw',
      manageUsername: '',
      manageAttempts: 0,
      manageLoading: false,
      manageUserId: null,
      manageActivityUserId: null
    }
  },
  created() {
    this.username = localStorage.getItem('username') || ''
    this.loadUserInfo().then(() => {
      this.loadActivities().then(() => {
        this.refreshDerived()
      })
    })
    this.loadRecords()
  },
  watch: {
    selectedActivityId() {
      this.refreshDerived()
      this.loadRecords()
    }
  },
  computed: {
    selectedActivity() {
      const id = this.selectedActivityId
      return (this.activities || []).find(a => String(a.id || a.activityId) === String(id)) || null
    },
    activityStateText() {
      const a = this.selectedActivity
      if (!a) return '未知'
      const now = Date.now()
      const start = a.startDate ? new Date(a.startDate).getTime() : NaN
      const end = a.endDate ? new Date(a.endDate).getTime() : NaN
      if (!isNaN(start) && now < start) return '未开始'
      if (!isNaN(end) && now > end) return '已结束'
      return '进行中'
    },
    activityStateClass() {
      const t = this.activityStateText
      return {
        pending: t === '未开始',
        finished: t === '已结束',
        running: t === '进行中'
      }
    },
    attemptsText() {
      const n = (this.attempts == null) ? 0 : this.attempts
      return typeof n === 'number' ? String(n) : String(n || 0)
    },
    attemptsTextNum() {
      const n = (this.attempts == null) ? 0 : this.attempts
      return typeof n === 'number' ? n : parseInt(n, 10) || 0
    }
  },
  methods: {
    authHeaders() {
      const token = localStorage.getItem('token')
      return token ? { Authorization: `Bearer ${token}` } : {}
    },
    async loadUserInfo() {
      if (!this.username) return
      try {
        const res = await axios.get(`/api/users/${encodeURIComponent(this.username)}`, { headers: this.authHeaders() })
        const user = res && res.data ? res.data : null
        if (user && (user.userId || user.id)) {
          this.userId = user.userId || user.id
        }
      } catch (e) {
        console.error('加载用户信息失败', e)
      }
    },
    async loadActivities() {
      try {
        const res = await axios.get('/api/activity/all', { headers: this.authHeaders() })
        this.activities = Array.isArray(res.data) ? res.data : ((res.data && res.data.data) ? res.data.data : [])
        if (!this.selectedActivityId && this.activities.length > 0) {
          const a = this.activities[0]
          this.selectedActivityId = a.id || a.activityId
        }
      } catch (e) {
        console.error('加载活动失败', e)
      }
    },
    async refreshDerived() {
      this.attempts = null
      await Promise.all([
        this.loadAttempts(),
        this.loadEligibility()
      ])
    },
    async loadEligibility() {
      try {
        if (!this.username || !this.selectedActivityId) return
        const params = new URLSearchParams()
        params.append('username', this.username)
        params.append('activityId', this.selectedActivityId)
        const res = await axios.get(`/api/lottery/eligibility?${params.toString()}`, { headers: this.authHeaders() })
        const body = res.data
        const data = body && body.data ? body.data : body
        if (data && typeof data.attempts !== 'undefined') {
          this.attempts = data.attempts
        }
      } catch (e) {
        // 忽略错误
      }
    },
    async loadAttempts() {
      try {
        if (!this.userId || !this.selectedActivityId) return
        const res = await axios.get(`/api/activity-users/user/${this.userId}`, { headers: this.authHeaders() })
        const list = res && res.data ? res.data : []
        const target = (list || []).find(u => String(u.activityId) === String(this.selectedActivityId))
        this.attempts = target ? target.lotteryAttempts : 0
      } catch (e) {
        console.error('加载用户活动次数失败', e)
      }
    },
    async loadManageState() {
      try {
        this.manageLoading = true
        // 查询管理用户ID
        const userRes = await axios.get(`/api/users/${encodeURIComponent(this.manageUsername)}`, { headers: this.authHeaders() })
        const user = userRes && userRes.data ? userRes.data : null
        if (!user || !(user.userId || user.id)) {
          alert('未找到该用户')
          return
        }
        this.manageUserId = user.userId || user.id
        // 查询该用户在活动下的记录
        const res = await axios.get(`/api/activity-users/user/${this.manageUserId}`, { headers: this.authHeaders() })
        const list = res && res.data ? res.data : []
        const target = (list || []).find(u => String(u.activityId) === String(this.selectedActivityId))
        if (target) {
          this.manageActivityUserId = target.activityUserId
          this.manageAttempts = target.lotteryAttempts
        } else {
          this.manageActivityUserId = null
          this.manageAttempts = 0
        }
      } catch (e) {
        console.error('读取管理信息失败', e)
      } finally {
        this.manageLoading = false
      }
    },
    async saveAttempts() {
      try {
        this.manageLoading = true
        if (!this.manageUsername) { alert('请输入用户名'); return }
        if (!this.selectedActivityId) { alert('请选择活动'); return }
        if (this.manageUserId == null) {
          const userRes = await axios.get(`/api/users/${encodeURIComponent(this.manageUsername)}`, { headers: this.authHeaders() })
          const user = userRes && userRes.data ? userRes.data : null
          if (!user || !(user.userId || user.id)) { alert('未找到该用户'); return }
          this.manageUserId = user.userId || user.id
        }
        if (this.manageActivityUserId) {
          // 更新
          await axios.put(`/api/activity-users/${this.manageActivityUserId}/attempts?attempts=${this.manageAttempts}`, {}, { headers: this.authHeaders() })
          alert('更新成功')
        } else {
          // 创建
          const payload = { userId: this.manageUserId, activityId: this.selectedActivityId, lotteryAttempts: this.manageAttempts }
          await axios.post('/api/activity-users', payload, { headers: this.authHeaders() })
          alert('创建成功')
        }
        // 刷新当前登录用户显示（若同一人）
        if (this.manageUserId === this.userId) {
          await this.loadAttempts()
        }
      } catch (e) {
        console.error('保存失败', e)
        alert('保存失败，请稍后重试')
      } finally {
        this.manageLoading = false
      }
    },
    async startDraw() {
      if (!this.username) {
        alert('未获取到用户名，请先登录')
        this.$router.push('/login')
        return
      }
      if (!this.selectedActivityId) return
      this.loading = true
      this.result = { success: false, message: '', prizeName: '' }
      try {
        const params = new URLSearchParams()
        params.append('username', this.username)
        params.append('activityId', this.selectedActivityId)
        const res = await axios.post(`/api/lottery/draw?${params.toString()}`, {}, { headers: this.authHeaders() })

        const body = res.data
        const data = body && body.data ? body.data : body
        this.result = {
          success: !!data.success,
          message: data.message || (data.success ? '恭喜中奖！' : '未中奖'),
          prizeName: data.prizeName || ''
        }
        await this.loadRecords()
        await this.loadEligibility()
      } catch (e) {
        const msg = (e && e.response && e.response.data && e.response.data.message)
          ? e.response.data.message
          : '抽奖失败，请稍后重试'
        this.result = { success: false, message: msg, prizeName: '' }
      } finally {
        this.loading = false
      }
    },
    async loadRecords() {
      this.recordsLoading = true
      try {
        const url = this.selectedActivityId ? `/api/lottery/records?activityId=${this.selectedActivityId}` : '/api/lottery/records'
        const res = await axios.get(url, { headers: this.authHeaders() })
        const body = res.data
        const data = body && body.data ? body.data : body
        this.records = Array.isArray(data) ? data : []
      } catch (e) {
        console.error('加载记录失败', e)
      } finally {
        this.recordsLoading = false
      }
    },
    formatTime(ts) {
      try {
        if (!ts) return ''
        const d = new Date(ts)
        const yyyy = d.getFullYear()
        const mm = String(d.getMonth() + 1).padStart(2, '0')
        const dd = String(d.getDate()).padStart(2, '0')
        const hh = String(d.getHours()).padStart(2, '0')
        const mi = String(d.getMinutes()).padStart(2, '0')
        const ss = String(d.getSeconds()).padStart(2, '0')
        return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
      } catch {
        return String(ts)
      }
    }
  }
}
</script>

<style>
.lottery-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 16px 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

.controls {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.control-item { display: flex; align-items: center; gap: 8px; }

.select {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fff;
}

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  background: #f0f2f5;
  color: #333;
}
.btn.primary {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: #fff;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }

.status-bar { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 12px; }
.status-item { display: flex; align-items: center; gap: 8px; }
.status-item .label { color: #909399; }
.status-item .value { color: #333; font-weight: 500; }
.status-item .value.attempts.zero { color: #e74c3c; }
.badge { padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; color: #fff; }
.badge.pending { background: #909399; }
.badge.running { background: #67c23a; }
.badge.finished { background: #f56c6c; }

.wheel {
  margin: 8px 0;
  height: 160px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}
.wheel-inner { width: 96%; height: 70%; border-radius: 10px; background: #f7f9fc; display:flex; align-items:center; justify-content:center; }
.wheel.spinning .wheel-inner { animation: pulse 0.8s infinite ease-in-out; }
.wheel-content { font-size: 16px; color: #606266; }
.result-text { font-weight: 600; }
.result-text.success { color: #2ecc71; }
.result-text.failure { color: #e74c3c; }
@keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(0.98);} 100% { transform: scale(1); } }

.records { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 12px; }
.record-list { display: grid; grid-template-columns: 1fr; gap: 8px; }
.record { display: grid; grid-template-columns: 160px 120px 1fr 1fr; gap: 8px; padding: 8px 10px; background: #f9fafb; border-radius: 6px; }
.record .time { color: #909399; }
.record .user { color: #333; font-weight: 500; }
.record .activity { color: #606266; }
.record .prize { color: #4b6cb7; font-weight: 600; }

@media (max-width: 768px) {
  .record { grid-template-columns: 1fr; }
}
</style>


