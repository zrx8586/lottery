<template>
  <div class="contract-selection safe-area-bottom">
    <div class="selection-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="selection-title">
            <span class="title-glow">🎯</span>
            <span class="title-text">选择合同开始游戏</span>
            <span class="title-sparkle">✨</span>
          </h1>
          <p class="selection-subtitle">请选择一份合同，在60秒内找出其中的5个法律错误点</p>
        </div>
      </div>
    </div>

    <div class="contract-grid">
      <div 
        v-for="contract in availableContracts" 
        :key="contract.id"
        class="contract-card"
        @click="startGame(contract.id)"
      >
        <div class="card-header">
          <div class="contract-icon">
            <span class="icon-emoji">{{ getContractIcon(contract.id) }}</span>
          </div>
          <div class="difficulty-badge">
            <span class="difficulty-text">{{ getDifficultyLevel(contract.id) }}</span>
          </div>
        </div>
        <div class="card-body">
          <h3 class="contract-name">{{ contract.title }}</h3>
          <p class="contract-desc">{{ contract.description }}</p>
        </div>
        <div class="card-footer">
          <div class="play-button">
            <span class="play-icon">▶️</span>
            <span class="play-text">开始游戏</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAvailableContracts, getContractById, getContractTitle } from '@/data/contracts.js'

export default {
  name: 'GameSelect',
  setup(){
    const router = useRouter()
    const availableContracts = ref([])

    const fetchAvailableContracts = () => {
      try {
        availableContracts.value = getAvailableContracts().map(c => ({
          id: c.id,
          title: c.title,
          description: (getContractById(c.id) && getContractById(c.id).description) || '标准合同模板'
        }))
      } catch (e) {
        // 退化：构造一个基本列表
        availableContracts.value = [1,2,3,4,5].map(id => ({ id, title: getContractTitle(id) || '合同', description: '合同模板' }))
      }
    }

    const startGame = (contractId) => {
      router.push({ name: 'Game', query: { id: String(contractId) } })
    }

    const getContractIcon = (contractId) => ({ 1:'💼',2:'🏠',3:'📦',4:'💻',5:'🔧' }[contractId] || '📄')
    const getDifficultyLevel = (contractId) => ({ 1:'⭐',2:'⭐⭐',3:'⭐⭐⭐',4:'⭐⭐⭐⭐',5:'⭐⭐⭐⭐⭐' }[contractId] || '⭐')

    onMounted(() => {
      fetchAvailableContracts()
      document.title = '选择合同 - 合同纠错游戏'
    })

    return { availableContracts, startGame, getContractIcon, getDifficultyLevel }
  }
}
</script>

<style scoped>
/* 主容器 - 与Intro页面保持一致的滚动效果 */
.contract-selection { 
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 移动端优化 - 全屏显示 */
@media (max-width: 768px) {
  .contract-selection {
    padding: 0;
    margin: 0;
    border-radius: 0;
  }
  
  .selection-header {
    margin: 0;
    border-radius: 0;
    min-height: auto;
    padding: 16px;
  }
  
  .contract-grid {
    padding: 0 16px 16px;
  }
}

@media (max-width: 480px) {
  .contract-selection {
    padding: 0;
    margin: 0;
  }
  
  .selection-header {
    padding: 12px;
  }
  
  .contract-grid {
    padding: 0 12px 12px;
  }
}

/* 内容包装器 - 类似Intro的wrapper结构 */
.selection-header {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.contract-grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fit, minmax(240px,1fr)); 
  gap: 16px;
  width: 100%;
  max-width: 1200px;
}

.contract-card { 
  background: #fff; 
  border: 1px solid #e9ecef; 
  border-radius: 14px; 
  cursor: pointer; 
  transition: .2s; 
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.contract-card:hover { 
  transform: translateY(-2px); 
  box-shadow: 0 4px 12px rgba(0,0,0,.1);
}

.card-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  padding: 14px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.contract-icon .icon-emoji{ 
  font-size: 28px;
  display: block;
  width: 40px;
  height: 40px;
  background: #e9ecef;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.difficulty-badge{ 
  background: #ff6b35; 
  padding: 4px 10px; 
  border-radius: 14px; 
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.card-body{ 
  padding: 16px 14px;
}

.contract-name{ 
  margin: 0 0 6px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.contract-desc{ 
  margin: 0; 
  color: #6c757d; 
  font-size: 14px;
  line-height: 1.4;
}

.card-footer{ 
  padding: 0 14px 14px;
}

.play-button{ 
  display: inline-flex; 
  align-items: center; 
  gap: 6px; 
  background: #007bff; 
  color: #fff; 
  padding: 8px 12px; 
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.play-button:hover {
  background: #0056b3;
  transform: translateY(-1px);
}

/* 标题样式优化 */
.selection-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.selection-subtitle {
  font-size: 14px;
  color: #6c757d;
  text-align: center;
  margin: 0;
  line-height: 1.4;
}
</style>

