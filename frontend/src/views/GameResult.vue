<template>
  <div class="result-section">
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
</style>


