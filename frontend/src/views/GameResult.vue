<template>
  <div class="result-section safe-area-bottom">
    <div class="result-wrapper">
      <div class="result-card">
      <div class="result-header">
        <h2 class="result-title">🎉 游戏结果 🎉</h2>
        <div class="result-decoration">
          <div class="decoration-star">⭐</div>
          <div class="decoration-line"></div>
          <div class="decoration-star">⭐</div>
        </div>
      </div>

      <div class="result-stats">
        <div class="stat-item">
          <div class="stat-icon">🎯</div>
          <span class="stat-label">找到错误:</span>
          <span class="stat-value">{{ userFound }} 个</span>
        </div>
        <div class="stat-item">
          <div class="stat-icon">🏆</div>
          <span class="stat-label">得分:</span>
          <span class="stat-value score">{{ score }}/100</span>
        </div>
        <div class="stat-item">
          <div class="stat-icon">⏱️</div>
          <span class="stat-label">用时:</span>
          <span class="stat-value">{{ timeUsed }}s</span>
        </div>
      </div>

      <div class="user-ai-compare">
        <div class="compare-card">
          <div class="compare-title">你的成绩</div>
          <div class="compare-number">{{ userFound }}</div>
          <div class="compare-sub">在 {{ timeUsed }}s 内找到</div>
        </div>
        <div class="compare-card ai">
          <div class="compare-title">AI成绩</div>
          <div class="compare-number">{{ aiFound }}</div>
          <div class="compare-sub">同样用时可找到</div>
        </div>
      </div>

      <div class="action-buttons">
        <button class="btn play-again-btn" @click="playAgain">
          <span class="btn-icon">🎮</span>
          <span class="btn-text">再玩一次</span>
          <div class="btn-particles"></div>
        </button>
        <button class="btn select-other-btn" @click="backToSelect">
          <span class="btn-icon">📄</span>
          <span class="btn-text">选择其他合同</span>
          <div class="btn-glow"></div>
        </button>
        <button class="btn select-other-btn" @click="goToResultsDetail">
          <span class="btn-icon">🔎</span>
          <span class="btn-text">查看详情</span>
          <div class="btn-glow"></div>
        </button>
        <button class="btn select-other-btn" @click="goToAIAnalysis">
          <span class="btn-icon">🤖</span>
          <span class="btn-text">AI分析</span>
          <div class="btn-glow"></div>
        </button>
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
    }
  }
}
</script>

<style>
/* 复用全局游戏样式，保持与项目一致并启用响应式 */
@import '../assets/styles/game/base.css';
@import '../assets/styles/game/result.css';
@import '../assets/styles/game/responsive.css';

/* 统一布局样式 - 与Intro和Select页面保持一致 */
.result-section {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.result-wrapper {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 优化结果卡片样式 */
.result-card {
  width: 100%;
}

/* 优化标题样式 */
.result-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin: 0 0 20px 0;
}

/* 优化统计项样式 */
.result-stats {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.stat-icon {
  font-size: 1.2rem;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.stat-value.score {
  color: #ff6b35;
}

/* 优化对比卡片 */
.user-ai-compare {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  justify-content: center;
}

.compare-card {
  flex: 1;
  max-width: 200px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  border: 1px solid #e9ecef;
}

.compare-card.ai {
  background: #e3f2fd;
  border-color: #bbdefb;
}

.compare-title {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 8px;
  font-weight: 500;
}

.compare-number {
  font-size: 2rem;
  font-weight: 700;
  color: #ff6b35;
  margin-bottom: 4px;
}

.compare-sub {
  font-size: 12px;
  color: #6c757d;
}

/* 优化按钮组 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 160px;
  justify-content: center;
}

.play-again-btn {
  background: #ff6b35;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.2);
}

.play-again-btn:hover {
  background: #e55a2b;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

.select-other-btn {
  background: #007bff;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2);
}

.select-other-btn:hover {
  background: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.btn-icon {
  font-size: 1rem;
}

.btn-text {
  font-size: 14px;
}

/* 移动端适配 - 全屏显示 */
@media (max-width: 768px) {
  .result-section {
    padding: 0;
    margin: 0;
    border-radius: 0;
  }
  
  .result-wrapper {
    padding: 16px;
    margin: 0;
    border-radius: 0;
    min-height: 100vh;
  }
  
  .result-stats {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  
  .user-ai-compare {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  
  .compare-card {
    max-width: 100%;
    width: 100%;
  }
  
  .action-buttons {
    width: 100%;
    gap: 10px;
  }
  
  .btn {
    width: 100%;
    max-width: 100%;
    padding: 14px 20px;
    font-size: 15px;
  }
}

/* 小屏幕适配 */
@media (max-width: 480px) {
  .result-section {
    padding: 0;
    margin: 0;
  }
  
  .result-wrapper {
    padding: 12px;
    margin: 0;
    border-radius: 0;
  }
  
  .result-title {
    font-size: 1.3rem;
    margin-bottom: 16px;
  }
  
  .result-stats {
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .stat-item {
    padding: 10px 12px;
    font-size: 14px;
  }
  
  .user-ai-compare {
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .compare-card {
    padding: 12px;
  }
  
  .compare-number {
    font-size: 1.5rem;
  }
  
  .action-buttons {
    gap: 8px;
    padding-bottom: 20px; /* 确保底部有足够空间 */
  }
  
  .btn {
    padding: 12px 16px;
    font-size: 14px;
    min-height: 44px; /* 确保按钮足够大，便于点击 */
  }
}
</style>


