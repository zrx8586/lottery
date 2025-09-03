<template>
  <div class="result-section safe-area-bottom">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
        </button>
        <h1 class="page-title">游戏结果</h1>
        <div class="nav-actions">
          <span class="action-icon">📊</span>
        </div>
      </div>
    </div>

    <!-- 结果卡片 -->
    <div class="result-content">
      <div class="result-card">
        <div class="result-header">
          <div class="result-icon">🎉</div>
          <h2 class="result-title">挑战完成</h2>
          <p class="result-subtitle">恭喜你完成了合同找错挑战</p>
        </div>

        <!-- 成绩统计 -->
        <div class="score-section">
          <div class="score-display">
            <div class="score-number">{{ score }}</div>
            <div class="score-label">总分</div>
          </div>
          <div class="score-details">
            <div class="detail-item">
              <span class="detail-icon">🎯</span>
              <span class="detail-text">找到 {{ userFound }} 个错误</span>
            </div>
            <div class="detail-item">
              <span class="detail-icon">⏱️</span>
              <span class="detail-text">用时 {{ timeUsed }} 秒</span>
            </div>
            <div class="detail-item">
              <span class="detail-icon">📊</span>
              <span class="detail-text">准确率 {{ Math.round((userFound / 5) * 100) }}%</span>
            </div>
          </div>
        </div>

        <!-- 用户 vs AI 对比 -->
        <div class="comparison-section">
          <h3 class="comparison-title">成绩对比</h3>
          <div class="comparison-cards">
            <div class="comparison-card user">
              <div class="card-header">
                <div class="user-avatar">👤</div>
                <div class="user-info">
                  <div class="user-name">你的成绩</div>
                  <div class="user-time">用时 {{ timeUsed }}s</div>
                </div>
              </div>
              <div class="card-score">
                <div class="score-value">{{ userFound }}</div>
                <div class="score-unit">个错误</div>
              </div>
            </div>
            <div class="comparison-card ai">
              <div class="card-header">
                <div class="ai-avatar">🤖</div>
                <div class="ai-info">
                  <div class="ai-name">AI成绩</div>
                  <div class="ai-time">同样用时</div>
                </div>
              </div>
              <div class="card-score">
                <div class="score-value">{{ aiFound }}</div>
                <div class="score-unit">个错误</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 结果评价 -->
        <div class="evaluation-section">
          <div class="evaluation-card" :class="getEvaluationClass()">
            <div class="evaluation-icon">{{ getEvaluationIcon() }}</div>
            <div class="evaluation-content">
              <h4 class="evaluation-title">{{ getEvaluationTitle() }}</h4>
              <p class="evaluation-text">{{ getEvaluationText() }}</p>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <div class="primary-actions">
            <button class="action-btn primary" @click="playAgain">
              <span class="btn-icon">🎮</span>
              <span class="btn-text">再玩一次</span>
            </button>
            <button class="action-btn secondary" @click="backToSelect">
              <span class="btn-icon">📄</span>
              <span class="btn-text">选择其他合同</span>
            </button>
          </div>
          <div class="secondary-actions">
            <button class="action-btn outline" @click="goToResultsDetail">
              <span class="btn-icon">🔎</span>
              <span class="btn-text">查看详情</span>
            </button>
            <button class="action-btn outline" @click="goToAIAnalysis">
              <span class="btn-icon">🤖</span>
              <span class="btn-text">AI分析</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameResult',
  data(){
    let state = history.state || {}
    if ((!state || !state.sentences) && typeof sessionStorage !== 'undefined') {
      try {
        const cached = JSON.parse(sessionStorage.getItem('gameResult') || 'null')
        if (cached) state = cached
      } catch (e) { /* ignore */ }
    }
    return {
      sentences: state.sentences || [],
      errorIndices: state.errorIndices || [],
      userSelections: state.userSelections || [],
      errorExplanations: state.errorExplanations || {},
      userFound: state.userFound || 0,
      aiFound: state.aiFound != null ? state.aiFound : (state.userFound || 0),
      score: state.score || 0,
      timeUsed: state.timeUsed || 0,
      contractId: state.contractId || null
    }
  },
  methods: {
    goBack(){
      this.$router.back()
    },
    playAgain(){
      if (this.contractId != null) {
        this.$router.replace({ name: 'Game', query: { id: String(this.contractId) } })
      } else {
        this.$router.replace({ name: 'GameSelect' })
      }
    },
    backToSelect(){
      this.$router.replace({ name: 'GameSelect' })
    },
    goToResultsDetail(){
      const payload = {
        sentences: this.sentences,
        errorIndices: this.errorIndices,
        userSelections: this.userSelections,
        errorExplanations: this.errorExplanations,
        userFound: this.userFound,
        aiFound: this.aiFound,
        contractId: this.contractId
      }
      try { sessionStorage.setItem('resultsDetail', JSON.stringify(payload)) } catch (e) { /* ignore */ }
      this.$router.push({ name: 'ResultsDetail', state: payload })
    },
    goToAIAnalysis(){
      this.$router.push({ name: 'AIAnalysis', state: { userFound: this.userFound, totalErrors: this.errorIndices.length, timeUsed: this.timeUsed } })
    },
    getEvaluationClass(){
      if (this.userFound === 5) return 'excellent'
      if (this.userFound >= 3) return 'good'
      return 'needs-improvement'
    },
    getEvaluationIcon(){
      if (this.userFound === 5) return '🎉'
      if (this.userFound >= 3) return '👍'
      return '💪'
    },
    getEvaluationTitle(){
      if (this.userFound === 5) return '完美表现！'
      if (this.userFound >= 3) return '表现不错！'
      return '继续努力！'
    },
    getEvaluationText(){
      if (this.userFound === 5) return '你找到了所有错误，法律意识很强！'
      if (this.userFound >= 3) return '找到了大部分错误，还有提升空间。'
      return '建议多学习相关法律知识，提高合同审查能力。'
    }
  }
}
</script>

<style>
/* 腾讯电子签风格 - 游戏结果页面 */

/* 引入基础样式 */
@import '../assets/styles/game/base.css';

/* 主容器 */
.result-section {
  min-height: 100vh;
  background: #f5f7fa;
  position: relative;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 0;
  margin: 0;
}

/* 移动端全屏优化 */
@media (max-width: 768px) {
  .result-section {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
  }
}

/* 顶部导航栏 */
.top-nav {
  background: #fff;
  border-bottom: 1px solid #e9ecef;
  padding: 12px 16px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.back-btn:hover {
  background: #f8f9fa;
}

.back-icon {
  font-size: 18px;
  color: #333;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.nav-actions {
  display: flex;
  gap: 8px;
}

.action-icon {
  font-size: 16px;
  color: #6c757d;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.action-icon:hover {
  background: #f8f9fa;
}

/* 结果内容区域 */
.result-content {
  flex: 1;
  padding: 16px;
  background: #fff;
}

.result-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 结果头部 */
.result-header {
  text-align: center;
  margin-bottom: 32px;
}

.result-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.result-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.result-subtitle {
  font-size: 16px;
  color: #6c757d;
  margin: 0;
}

/* 成绩展示区域 */
.score-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #00BFA5, #00A693);
  border-radius: 16px;
  color: #fff;
}

.score-display {
  text-align: center;
  flex-shrink: 0;
}

.score-number {
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 4px;
}

.score-label {
  font-size: 16px;
  opacity: 0.9;
}

.score-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.detail-icon {
  font-size: 20px;
}

.detail-text {
  font-weight: 500;
}

/* 对比区域 */
.comparison-section {
  margin-bottom: 32px;
}

.comparison-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  text-align: center;
}

.comparison-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.comparison-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.comparison-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comparison-card.user {
  border-color: #00BFA5;
}

.comparison-card.ai {
  background: #e3f2fd;
  border-color: #bbdefb;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar,
.ai-avatar {
  width: 40px;
  height: 40px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info,
.ai-info {
  flex: 1;
}

.user-name,
.ai-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.user-time,
.ai-time {
  font-size: 14px;
  color: #6c757d;
}

.card-score {
  text-align: center;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  color: #00BFA5;
  line-height: 1;
  margin-bottom: 4px;
}

.score-unit {
  font-size: 14px;
  color: #6c757d;
}

/* 评价区域 */
.evaluation-section {
  margin-bottom: 32px;
}

.evaluation-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.evaluation-card.excellent {
  background: #d4edda;
  border-color: #c3e6cb;
  color: #155724;
}

.evaluation-card.good {
  background: #fff3cd;
  border-color: #ffeaa7;
  color: #856404;
}

.evaluation-card.needs-improvement {
  background: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
}

.evaluation-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.evaluation-content {
  flex: 1;
}

.evaluation-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.evaluation-text {
  font-size: 14px;
  margin: 0;
  line-height: 1.4;
}

/* 操作按钮区域 */
.action-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.primary-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.secondary-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.action-btn.primary {
  background: #00BFA5;
  color: #fff;
}

.action-btn.primary:hover {
  background: #00A693;
  transform: translateY(-1px);
}

.action-btn.secondary {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #e9ecef;
}

.action-btn.secondary:hover {
  background: #e9ecef;
}

.action-btn.outline {
  background: #fff;
  color: #00BFA5;
  border: 1px solid #00BFA5;
}

.action-btn.outline:hover {
  background: #f0fffe;
}

.btn-icon {
  font-size: 16px;
}

.btn-text {
  font-weight: 500;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .result-content {
    padding: 12px;
  }
  
  .result-card {
    padding: 16px;
  }
  
  .result-icon {
    font-size: 48px;
  }
  
  .result-title {
    font-size: 20px;
  }
  
  .score-section {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .score-number {
    font-size: 36px;
  }
  
  .score-details {
    gap: 8px;
  }
  
  .detail-item {
    justify-content: center;
  }
  
  .comparison-cards {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .primary-actions,
  .secondary-actions {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .evaluation-card {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .result-content {
    padding: 8px;
  }
  
  .result-card {
    padding: 12px;
  }
  
  .result-icon {
    font-size: 36px;
  }
  
  .result-title {
    font-size: 18px;
  }
  
  .result-subtitle {
    font-size: 14px;
  }
  
  .score-section {
    padding: 16px;
  }
  
  .score-number {
    font-size: 28px;
  }
  
  .score-label {
    font-size: 14px;
  }
  
  .detail-item {
    font-size: 14px;
  }
  
  .comparison-card {
    padding: 16px;
  }
  
  .score-value {
    font-size: 28px;
  }
  
  .evaluation-card {
    padding: 16px;
  }
  
  .evaluation-icon {
    font-size: 24px;
  }
  
  .evaluation-title {
    font-size: 16px;
  }
  
  .evaluation-text {
    font-size: 13px;
  }
  
  .action-btn {
    padding: 12px 16px;
    font-size: 13px;
  }
}
</style>


