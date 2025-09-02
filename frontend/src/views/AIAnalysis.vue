<template>
  <div class="ai-analysis-page">
    <div class="header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h2 class="title">AI 风险分析</h2>
    </div>

    <div class="grid">
      <section class="card highlight">
        <h3>总体评估</h3>
        <p>此处展示模型给出的总体风险分布、分数或等级。</p>
        <div class="placeholder">待接入AI数据</div>
      </section>
      <section class="card">
        <h3>高风险条款</h3>
        <ul class="list">
          <li v-for="i in 4" :key="i">高风险条款占位 {{ i }}</li>
        </ul>
      </section>
      <section class="card">
        <h3>改进建议</h3>
        <ul class="list">
          <li v-for="i in 4" :key="i">建议占位 {{ i }}</li>
        </ul>
      </section>
    </div>

    <div class="brand">
      <div class="qrcode">品牌二维码占位</div>
    </div>
  </div>
</template>

<script>
export default { name: 'AIAnalysis' }
</script>

<style scoped>
.ai-analysis-page { padding: 20px; max-width: 1100px; margin: 0 auto; }
.header { display:flex; align-items:center; gap:12px; margin-bottom:16px }
.back-btn { padding:8px 14px; border-radius:20px; border:1px solid #e5e7eb; background:#fff; cursor:pointer }
.title { margin:0; font-size:20px }
.grid { display:grid; grid-template-columns: 1fr 1fr; gap:16px }
.card { background:#fff; border:1px solid #edf2f7; border-radius:12px; padding:16px; box-shadow: 0 4px 10px rgba(0,0,0,.03) }
.card.highlight { border-color:#c7d2fe; box-shadow: 0 6px 16px rgba(99,102,241,.12) }
.list { margin:0; padding-left:18px }
.placeholder { margin-top:10px; padding:10px; border:1px dashed #c7d2fe; border-radius:8px; color:#4f46e5; background:#eef2ff }
.brand { margin-top:18px; display:flex; justify-content:flex-end }
.qrcode { width:120px; height:120px; border:1px dashed #d1d5db; display:flex; align-items:center; justify-content:center; color:#6b7280; border-radius:8px }
@media (max-width: 768px) { .grid{ grid-template-columns:1fr } .ai-analysis-page{ padding:14px } }
</style>

