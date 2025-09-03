<template>
  <div class="ai-analysis-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <button class="back-btn" @click="$router.back()"><span class="back-icon">←</span></button>
        <h1 class="page-title">AI 风险分析</h1>
        <div class="nav-actions"><span class="action-icon">📈</span></div>
      </div>
    </div>
    <div class="analysis-wrapper">
      <div class="header">
        <h2 class="section-title">总体评估</h2>
      </div>

      <div class="grid">
      <section class="card highlight">
        <h3 class="card-title">总体评估</h3>
        <p class="card-desc">此处展示模型给出的总体风险分布、分数或等级。</p>
        <div class="placeholder">待接入AI数据</div>
      </section>
      <section class="card">
        <h3 class="card-title">高风险条款</h3>
        <ul class="list">
          <li v-for="i in 4" :key="i">高风险条款占位 {{ i }}</li>
        </ul>
      </section>
      <section class="card">
        <h3 class="card-title">改进建议</h3>
        <ul class="list">
          <li v-for="i in 4" :key="i">建议占位 {{ i }}</li>
        </ul>
      </section>
      </div>

      <div class="brand">
        <div class="qrcode">品牌二维码占位</div>
      </div>
    </div>
  </div>
</template>

<script>
export default { name: 'AIAnalysis' }
</script>

<style scoped>
/* 腾讯电子签风格 - AI 分析页面 */
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
.ai-analysis-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.analysis-wrapper {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header { 
  display: flex; 
  align-items: center; 
  gap: 12px; 
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.back-btn { 
  padding: 8px 16px; 
  border-radius: 20px; 
  border: 1px solid #e9ecef; 
  background: #f8f9fa; 
  cursor: pointer;
  color: #6c757d;
  font-size: 14px;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.section-title { margin: 0 0 12px; font-size: 16px; font-weight: 600; color: #333; }
.card-title { margin: 0 0 12px; font-size: 16px; font-weight: 600; color: #333; }
.card-desc { margin: 0 0 12px; color: #6c757d; font-size: 14px; line-height: 1.5; }

.grid { 
  display: grid; 
  grid-template-columns: 1fr 1fr; 
  gap: 20px;
  margin-bottom: 24px;
}

.card { 
  background: #fff; 
  border: 1px solid #e9ecef; 
  border-radius: 12px; 
  padding: 20px; 
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card.highlight { border-color: #e9ecef; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }

.card h3 {
  margin: 0 0 12px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.card p {
  margin: 0 0 12px 0;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.5;
}

.list { 
  margin: 0; 
  padding-left: 18px;
}

.list li {
  margin-bottom: 8px;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.5;
}

.placeholder { margin-top: 12px; padding: 12px; border: 1px dashed #e9ecef; border-radius: 8px; color: #6c757d; background: #f8f9fa; text-align: center; font-size: 14px; font-weight: 500; }

.brand { 
  margin-top: 20px; 
  display: flex; 
  justify-content: flex-end;
}

.qrcode { 
  width: 120px; 
  height: 120px; 
  border: 1px dashed #e9ecef; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: #6c757d; 
  border-radius: 8px;
  background: #f8f9fa;
  font-size: 12px;
  text-align: center;
}

/* 移动端适配 - 全屏显示 */
@media (max-width: 768px) {
  .ai-analysis-page {
    padding: 0;
    margin: 0;
    border-radius: 0;
  }
  
  .analysis-wrapper {
    padding: 16px;
    margin: 0;
    border-radius: 0;
    min-height: 100vh;
  }
  
  .grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .ai-analysis-page {
    padding: 0;
    margin: 0;
  }
  
  .analysis-wrapper {
    padding: 12px;
    margin: 0;
    border-radius: 0;
  }
  
  .title {
    font-size: 1.3rem;
  }
  
  .card {
    padding: 16px;
  }
  
  .card h3 {
    font-size: 1rem;
  }
  
  .qrcode {
    width: 100px;
    height: 100px;
    font-size: 11px;
  }
}
</style>

