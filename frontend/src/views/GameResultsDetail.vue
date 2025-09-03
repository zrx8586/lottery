<template>
  <div class="results-detail-page safe-area-bottom">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <button class="back-btn" @click="goBack"><span class="back-icon">←</span></button>
        <h1 class="page-title">结果详情</h1>
        <div class="nav-actions"><span class="action-icon">🔎</span></div>
      </div>
    </div>

    <div class="detail-wrapper">
      <div class="header">
        <h2 class="title">合同内容与判定</h2>
      </div>

      <div class="content">
      <div class="summary">
        <div class="item"><span>本次找到</span><strong>{{ userFound }}</strong><span>处</span></div>
        <div class="item"><span>AI可在同样时间找到</span><strong>{{ aiFound }}</strong><span>处</span></div>
      </div>

      <div class="filters">
        <button :class="['filter-btn', { active: filterMode==='all' }]" @click="setFilter('all')">全部</button>
        <button :class="['filter-btn', { active: filterMode==='errors' }]" @click="setFilter('errors')">仅看错误</button>
        <button :class="['filter-btn', { active: filterMode==='missed' }]" @click="setFilter('missed')">仅看未找到</button>
      </div>

      <h3 class="sub-title">📋 合同内容与判定</h3>
      <div class="list">
        <div
          v-for="(idx, pos) in filteredIndices"
          :key="idx"
          class="detail-item"
          :class="{ found: userSelections.includes(idx) && errorIndices.includes(idx) }"
        >
          <div class="index">{{ pos + 1 }}.</div>
          <div class="text">
            <div class="sentence">{{ sentences[idx] }}</div>
            <div class="explain" v-if="errorExplain[idx]"><strong>错误说明：</strong>{{ errorExplain[idx] }}</div>
          </div>
          <template v-if="errorIndices.includes(idx)">
            <div class="tag" v-if="userSelections.includes(idx)">错误点 · 已找到</div>
            <div class="tag missed" v-else>错误点 · 未找到</div>
          </template>
          <template v-else>
            <div class="tag neutral">非错误</div>
          </template>
        </div>
      </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameResultsDetail',
  props: {},
  data() {
    let state = history.state || {}
    if ((!state || !state.sentences) && typeof sessionStorage !== 'undefined') {
      try {
        const cached = JSON.parse(sessionStorage.getItem('resultsDetail') || 'null')
        if (cached) state = cached
      } catch (e) { /* ignore malformed cache */ }
    }
    return {
      sentences: state.sentences || [],
      errorIndices: state.errorIndices || [],
      userSelections: state.userSelections || [],
      errorExplain: state.errorExplanations || {},
      userFound: (state.userFound !== undefined && state.userFound !== null) ? state.userFound : 0,
      aiFound: (state.aiFound !== undefined && state.aiFound !== null)
        ? state.aiFound
        : ((state.userFound !== undefined && state.userFound !== null) ? state.userFound : 0),
      filterMode: 'errors',
      contractId: state.contractId || null,
      headerIndices: []
    }
  },
  created(){
    // 若能拿到 contractId，拉取头部索引以在详情页同样标识
    try {
      if (this.contractId != null) {
        // 动态导入以避免循环依赖
        const mod = require('@/data/contracts.js')
        const getHeaderIndices = mod && mod.getHeaderIndices ? mod.getHeaderIndices : null
        if (getHeaderIndices) {
          this.headerIndices = getHeaderIndices(this.contractId) || []
        }
      }
    } catch (e) { /* ignore */ }
  },
  methods: {
    goBack() {
      this.$router.push({ name: 'GameResult' })
    },
    setFilter(mode){ this.filterMode = mode }
  },
  computed: {
    filteredIndices(){
      const indices = this.sentences.map((_, i) => i)
        .filter(i => !(this.headerIndices || []).includes(i))
      if (this.filterMode === 'all') return indices
      if (this.filterMode === 'errors') return indices.filter(i => this.errorIndices.includes(i))
      if (this.filterMode === 'missed') return indices.filter(i => this.errorIndices.includes(i) && !this.userSelections.includes(i))
      return indices
    }
  }
}
</script>

<style scoped>
/* 腾讯电子签风格 - 结果详情页面 */
/* 顶部导航栏 */
.top-nav { background: #fff; border-bottom: 1px solid #e9ecef; padding: 12px 16px; position: sticky; top: 0; z-index: 100; }
.nav-content { display: flex; justify-content: space-between; align-items: center; }
.back-btn { background: none; border: none; padding: 8px; border-radius: 8px; cursor: pointer; transition: background-color .2s ease; }
.back-btn:hover { background: #f8f9fa; }
.back-icon { font-size: 18px; color: #333; }
.page-title { margin: 0; font-size: 18px; font-weight: 600; color: #333; }
.nav-actions { display: flex; gap: 8px; }
.action-icon { font-size: 16px; color: #6c757d; padding: 8px; border-radius: 8px; }

/* 统一布局样式 - 与其他页面保持一致 */
.results-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.detail-wrapper {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid #e9ecef; }

.title { 
  margin: 0; 
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.summary { 
  display: flex; 
  gap: 20px; 
  margin: 16px 0 20px; 
  color: #6c757d;
  flex-wrap: wrap;
}

.summary .item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  font-size: 14px;
}

.summary .item strong { 
  color: #00BFA5; 
  margin: 0 4px;
  font-weight: 600;
}

.sub-title { 
  margin: 20px 0 12px; 
  color: #333; 
  font-weight: 600;
  font-size: 1.1rem;
}

.filters { 
  display: flex; 
  gap: 8px; 
  margin: 12px 0 16px;
  flex-wrap: wrap;
}

.filter-btn { 
  padding: 6px 12px; 
  border: 1px solid #e9ecef; 
  background: #fff; 
  border-radius: 16px; 
  cursor: pointer; 
  color: #6c757d;
  font-size: 14px;
  transition: all 0.2s ease;
}

.filter-btn.active { background: #00BFA5; border-color: #00BFA5; color: #fff; }

.list { 
  display: flex; 
  flex-direction: column; 
  gap: 12px;
}

.detail-item { 
  display: flex; 
  align-items: flex-start; 
  gap: 12px; 
  padding: 16px; 
  border: 1px solid #e9ecef; 
  border-radius: 12px; 
  background: #fff;
  transition: all 0.2s ease;
}

.detail-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.detail-item.found { 
  border-color: #c6f6d5; 
  background: #f0fff4;
}

.detail-item .index { 
  width: 32px; 
  height: 32px; 
  border-radius: 8px; 
  background: #00BFA5; 
  color: white; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.detail-item .text { 
  flex: 1; 
  line-height: 1.6;
}

.detail-item .text .sentence { 
  margin-bottom: 8px;
  color: #333;
  font-size: 15px;
}

.detail-item .text .explain { 
  color: #6c757d; 
  font-size: 14px;
  line-height: 1.5;
}

.tag { 
  padding: 4px 8px; 
  border-radius: 6px; 
  background: #e6fffa; 
  color: #0f766e; 
  border: 1px solid #99f6e4; 
  white-space: nowrap;
  font-size: 12px;
  font-weight: 500;
}

.tag.missed { 
  background: #fee2e2; 
  color: #991b1b; 
  border-color: #fecaca;
}

.tag.neutral { 
  background: #f3f4f6; 
  color: #6c757d; 
  border-color: #e9ecef;
}

/* 移动端适配 - 全屏显示 */
@media (max-width: 768px) {
  .results-detail-page { padding: 0; margin: 0; border-radius: 0; }
  
  .detail-wrapper {
    padding: 16px;
    margin: 0;
    border-radius: 0;
    min-height: 100vh;
  }
  
  .summary {
    flex-direction: column;
    gap: 12px;
  }
  
  .filters {
    justify-content: center;
  }
  
  .detail-item {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .results-detail-page {
    padding: 0;
    margin: 0;
  }
  
  .detail-wrapper {
    padding: 12px;
    margin: 0;
    border-radius: 0;
  }
  
  .title {
    font-size: 1.3rem;
  }
  
  .detail-item {
    padding: 10px;
    gap: 8px;
  }
  
  .detail-item .index {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
  
  .detail-item .text .sentence {
    font-size: 14px;
  }
  
  .detail-item .text .explain {
    font-size: 13px;
  }
}
</style>

