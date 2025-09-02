<template>
  <div class="results-detail-page">
    <div class="header">
      <button class="back-btn" @click="goBack">← 返回结果</button>
      <h2 class="title">详细结果</h2>
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
          v-for="idx in filteredIndices"
          :key="idx"
          class="detail-item"
          :class="{ found: userSelections.includes(idx) && errorIndices.includes(idx) }"
        >
          <div class="index">{{ idx + 1 }}.</div>
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
      filterMode: 'errors'
    }
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
      if (this.filterMode === 'all') return indices
      if (this.filterMode === 'errors') return indices.filter(i => this.errorIndices.includes(i))
      if (this.filterMode === 'missed') return indices.filter(i => this.errorIndices.includes(i) && !this.userSelections.includes(i))
      return indices
    }
  }
}
</script>

<style scoped>
.results-detail-page { padding: 20px; max-width: 960px; margin: 0 auto; }
.header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back-btn { padding: 8px 14px; border-radius: 20px; border: 1px solid #e5e7eb; background:#fff; cursor:pointer }
.title { margin: 0; font-size: 20px; }
.summary { display:flex; gap:16px; margin: 8px 0 16px; color:#374151 }
.sub-title { margin: 10px 0; color:#182848; font-weight:700 }
.filters { display:flex; gap:8px; margin: 8px 0 12px }
.filter-btn { padding:6px 12px; border:1px solid #e5e7eb; background:#fff; border-radius:16px; cursor:pointer; color:#374151 }
.filter-btn.active { background:#eef2ff; border-color:#c7d2fe; color:#4f46e5 }
.summary .item strong { color:#111827; margin: 0 4px }
.list { display:flex; flex-direction:column; gap:10px }
.detail-item { display:flex; align-items:flex-start; gap:12px; padding:12px; border:1px solid #edf2f7; border-radius:10px; background:#fff }
.detail-item.found { border-color:#c6f6d5; background:#f0fff4 }
.detail-item .index { width:32px; height:28px; border-radius:8px; background:#eef2ff; color:#4338ca; display:flex; align-items:center; justify-content:center; font-weight:600 }
.detail-item .text { flex:1; line-height:1.7 }
.detail-item .text .sentence { margin-bottom:6px }
.detail-item .text .explain { color:#6b7280; font-size:.95rem }
.tag { padding:4px 8px; border-radius:6px; background:#e6fffa; color:#0f766e; border:1px solid #99f6e4; white-space:nowrap }
.tag.missed { background:#fee2e2; color:#991b1b; border-color:#fecaca }
.tag.neutral { background:#f3f4f6; color:#374151; border-color:#e5e7eb }
@media (max-width: 480px) { .results-detail-page{ padding:14px } .detail-item{ padding:10px } }
</style>

