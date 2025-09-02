<template>
  <div class="contract-selection">
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
.contract-selection { padding: 20px }
.contract-grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(240px,1fr)); gap:16px }
.contract-card { background:#fff; border:1px solid #e9ecef; border-radius:14px; cursor:pointer; transition:.2s; overflow:hidden }
.contract-card:hover { transform: translateY(-2px); box-shadow:0 6px 18px rgba(0,0,0,.06) }
.card-header { display:flex; justify-content:space-between; align-items:center; padding:14px }
.contract-icon .icon-emoji{ font-size:28px }
.difficulty-badge{ background:#eef2ff; border:1px solid #c7d2fe; padding:4px 10px; border-radius:14px; color:#4f46e5 }
.card-body{ padding:0 14px 14px }
.contract-name{ margin:0 0 6px 0 }
.contract-desc{ margin:0; color:#6b7280; font-size:.95rem }
.card-footer{ padding:0 14px 14px }
.play-button{ display:inline-flex; align-items:center; gap:6px; background:#4b6cb7; color:#fff; padding:8px 12px; border-radius:20px }
</style>

