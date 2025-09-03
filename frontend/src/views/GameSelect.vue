<template>
  <div class="contract-selection safe-area-bottom">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
        </button>
        <h1 class="page-title">选择合同</h1>
        <div class="nav-actions">
          <span class="action-icon">🔍</span>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="search-bar">
        <div class="search-icon">🔍</div>
        <input type="text" placeholder="搜索合同类型..." class="search-input" v-model="searchQuery">
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-section">
      <div class="filter-tags">
        <div 
          v-for="filter in filters" 
          :key="filter.id"
          :class="['filter-tag', { active: activeFilter === filter.id }]"
          @click="setActiveFilter(filter.id)"
        >
          {{ filter.name }}
        </div>
      </div>
    </div>

    <!-- 合同列表 -->
    <div class="contracts-section">
      <div class="section-header">
        <h2 class="section-title">合同模板</h2>
        <span class="contract-count">{{ filteredContracts.length }} 个合同</span>
      </div>
      
      <div class="contracts-grid">
        <div 
          v-for="contract in filteredContracts" 
          :key="contract.id"
          class="contract-card"
          @click="startGame(contract.id)"
        >
          <div class="card-header">
            <div class="contract-icon">
              <span class="icon-emoji">{{ getContractIcon(contract.id) }}</span>
            </div>
            <div class="card-actions">
              <div class="difficulty-badge">
                <span class="difficulty-text">{{ getDifficultyLevel(contract.id) }}</span>
              </div>
              <div class="play-indicator">
                <span class="play-icon">▶</span>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <h3 class="contract-name">{{ contract.title }}</h3>
            <p class="contract-desc">{{ contract.description }}</p>
            
            <div class="contract-meta">
              <div class="meta-item">
                <span class="meta-icon">⏱️</span>
                <span class="meta-text">60秒</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">🎯</span>
                <span class="meta-text">5个错误</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">📊</span>
                <span class="meta-text">中等难度</span>
              </div>
            </div>
          </div>
          
          <div class="card-footer">
            <div class="start-button">
              <span class="button-text">开始挑战</span>
              <span class="button-icon">→</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部提示 -->
    <div class="bottom-tip">
      <div class="tip-content">
        <span class="tip-icon">💡</span>
        <span class="tip-text">选择一份合同开始挑战，在60秒内找出所有法律错误</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAvailableContracts, getContractById, getContractTitle } from '@/data/contracts.js'

export default {
  name: 'GameSelect',
  setup(){
    const router = useRouter()
    const availableContracts = ref([])
    const searchQuery = ref('')
    const activeFilter = ref('all')
    
    const filters = ref([
      { id: 'all', name: '全部' },
      { id: 'easy', name: '简单' },
      { id: 'medium', name: '中等' },
      { id: 'hard', name: '困难' }
    ])

    const fetchAvailableContracts = () => {
      try {
        availableContracts.value = getAvailableContracts().map(c => ({
          id: c.id,
          title: c.title,
          description: (getContractById(c.id) && getContractById(c.id).description) || '标准合同模板',
          difficulty: getDifficultyLevel(c.id)
        }))
      } catch (e) {
        // 退化：构造一个基本列表
        availableContracts.value = [1,2,3,4,5].map(id => ({ 
          id, 
          title: getContractTitle(id) || '合同', 
          description: '合同模板',
          difficulty: getDifficultyLevel(id)
        }))
      }
    }

    const startGame = (contractId) => {
      router.push({ name: 'Game', query: { id: String(contractId) } })
    }

    const goBack = () => {
      router.back()
    }

    const setActiveFilter = (filterId) => {
      activeFilter.value = filterId
    }

    const getContractIcon = (contractId) => ({ 1:'💼',2:'🏠',3:'📦',4:'💻',5:'🔧' }[contractId] || '📄')
    const getDifficultyLevel = (contractId) => ({ 1:'⭐',2:'⭐⭐',3:'⭐⭐⭐',4:'⭐⭐⭐⭐',5:'⭐⭐⭐⭐⭐' }[contractId] || '⭐')

    // 计算过滤后的合同列表
    const filteredContracts = computed(() => {
      let contracts = availableContracts.value

      // 搜索过滤
      if (searchQuery.value) {
        contracts = contracts.filter(contract => 
          contract.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          contract.description.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 难度过滤
      if (activeFilter.value !== 'all') {
        contracts = contracts.filter(contract => {
          const difficulty = contract.difficulty
          switch (activeFilter.value) {
            case 'easy': return difficulty === '⭐'
            case 'medium': return difficulty === '⭐⭐' || difficulty === '⭐⭐⭐'
            case 'hard': return difficulty === '⭐⭐⭐⭐' || difficulty === '⭐⭐⭐⭐⭐'
            default: return true
          }
        })
      }

      return contracts
    })

    onMounted(() => {
      fetchAvailableContracts()
      document.title = '选择合同 - 合同纠错游戏'
    })

    return { 
      availableContracts, 
      searchQuery,
      activeFilter,
      filters,
      filteredContracts,
      startGame, 
      goBack,
      setActiveFilter,
      getContractIcon, 
      getDifficultyLevel 
    }
  }
}
</script>

<style scoped>
/* 腾讯电子签风格 - 合同选择页面 */

/* 主容器 */
.contract-selection {
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
  .contract-selection {
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

/* 搜索栏 */
.search-section {
  background: #fff;
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
}

.search-bar {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid #e9ecef;
}

.search-icon {
  font-size: 16px;
  color: #6c757d;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  outline: none;
  font-size: 14px;
  color: #333;
}

.search-input::placeholder {
  color: #6c757d;
}

/* 筛选标签 */
.filter-section {
  background: #fff;
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
}

.filter-tags {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.filter-tag {
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  font-size: 14px;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.filter-tag:hover {
  background: #e9ecef;
}

.filter-tag.active {
  background: #00BFA5;
  border-color: #00BFA5;
  color: #fff;
}

/* 合同列表区域 */
.contracts-section {
  flex: 1;
  padding: 20px 16px;
  background: #fff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.contract-count {
  font-size: 14px;
  color: #6c757d;
}

.contracts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

/* 合同卡片 */
.contract-card {
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.contract-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #00BFA5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.contract-icon {
  width: 40px;
  height: 40px;
  background: #f8f9fa;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-emoji {
  font-size: 20px;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.difficulty-badge {
  background: #00BFA5;
  color: #fff;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.play-indicator {
  width: 24px;
  height: 24px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 12px;
  color: #00BFA5;
}

.card-body {
  margin-bottom: 16px;
}

.contract-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.contract-desc {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.4;
  margin: 0 0 12px 0;
}

.contract-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6c757d;
}

.meta-icon {
  font-size: 12px;
}

.card-footer {
  border-top: 1px solid #f8f9fa;
  padding-top: 12px;
}

.start-button {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #00BFA5;
  color: #fff;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s ease;
}

.start-button:hover {
  background: #00A693;
}

.button-text {
  font-weight: 500;
}

.button-icon {
  font-size: 12px;
}

/* 底部提示 */
.bottom-tip {
  background: #fff;
  padding: 16px;
  border-top: 1px solid #e9ecef;
  margin-top: auto;
}

.tip-content {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #00BFA5;
}

.tip-icon {
  font-size: 16px;
}

.tip-text {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.4;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .contracts-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .contract-card {
    padding: 12px;
  }
  
  .contract-meta {
    gap: 8px;
  }
  
  .meta-item {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .top-nav {
    padding: 8px 12px;
  }
  
  .search-section,
  .filter-section,
  .contracts-section {
    padding: 12px;
  }
  
  .filter-tags {
    gap: 6px;
  }
  
  .filter-tag {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .contract-card {
    padding: 10px;
  }
  
  .contract-name {
    font-size: 15px;
  }
  
  .contract-desc {
    font-size: 13px;
  }
}

/* 滚动条样式 */
.filter-tags::-webkit-scrollbar {
  height: 4px;
}

.filter-tags::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.filter-tags::-webkit-scrollbar-thumb {
  background: #00BFA5;
  border-radius: 2px;
}

.filter-tags::-webkit-scrollbar-thumb:hover {
  background: #00A693;
}
</style>