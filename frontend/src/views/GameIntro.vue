<template>
  <div class="game-intro-container safe-area-bottom">


    <!-- 头部区域 -->
    <div class="header-section">
      <div class="header-content">
        <h1 class="app-title">合同找错游戏</h1>
      </div>
    </div>

    <!-- 用户信息栏 -->
    <div class="user-info-bar">
      <div class="user-info-content">
        <div class="user-avatar">
          <div class="avatar-icon">👤</div>
        </div>
        <div class="user-details">
          <span class="user-name">法律学习者</span>
          <span class="user-status">准备开始挑战</span>
        </div>
        <div class="user-action" @click="startRandomGame">
          <span class="action-text">开始游戏 ></span>
        </div>
      </div>
    </div>

    <!-- 快速开始区域 -->
    <div class="quick-start-section">
      <h2 class="section-title">快速开始</h2>
      <div class="quick-actions">
        <div class="quick-action-card" @click="startGame">
          <div class="action-icon">📄</div>
          <span class="action-text">选择合同</span>
        </div>
        <div class="quick-action-card" @click="showRules">
          <div class="action-icon">📋</div>
          <span class="action-text">游戏规则</span>
        </div>
      </div>
    </div>

    <!-- 合同模板区域 -->
    <div class="contract-templates-section">
      <div class="section-header">
        <h2 class="section-title">合同模板</h2>
        <span class="more-link">更多模板 ></span>
      </div>
      <div class="templates-grid">
        <div class="template-card" v-for="contract in contractTypes" :key="contract.id" @click="startGame">
          <div class="template-preview">
            <div class="template-icon">{{ contract.icon }}</div>
            <div class="template-content">
              <h4 class="template-title">{{ contract.name }}</h4>
              <p class="template-desc">{{ contract.description }}</p>
              <div class="template-meta">
                <span class="difficulty">{{ contract.difficulty }}</span>
                <span class="errors">5个错误点</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>





    <!-- 底部导航 -->
    <div class="bottom-navigation">
      <div class="nav-item active">
        <div class="nav-icon">🏠</div>
        <span class="nav-text">首页</span>
      </div>
      <div class="nav-item">
        <div class="nav-icon">📁</div>
        <span class="nav-text">合同</span>
      </div>
      <div class="nav-item">
        <div class="nav-icon">👤</div>
        <span class="nav-text">个人</span>
      </div>
      <div class="nav-item">
        <div class="nav-icon">⚙️</div>
        <span class="nav-text">设置</span>
      </div>
    </div>

    <!-- 游戏规则弹窗 -->
    <div v-if="showRulesModal" class="modal-overlay" @click="closeRulesModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="modal-icon">📋</span>
            游戏规则
          </h3>
          <button class="modal-close-btn" @click="closeRulesModal">
            <span class="close-icon">×</span>
          </button>
        </div>
        
        <div class="modal-content">
          <div class="rules-section">
            <h4 class="rules-subtitle">🎯 游戏目标</h4>
            <p class="rules-text">仔细阅读合同内容，找出与相关法律法规不符的条款</p>
          </div>
          
          <div class="rules-section">
            <h4 class="rules-subtitle">⏱️ 时间限制</h4>
            <p class="rules-text">每局游戏限时60秒，时间到后自动提交答案</p>
          </div>
          
          <div class="rules-section">
            <h4 class="rules-subtitle">🎯 错误点数量</h4>
            <p class="rules-text">每份合同包含5个错误点，需要全部找出</p>
          </div>
          
          <div class="rules-section">
            <h4 class="rules-subtitle">🏆 计分规则</h4>
            <ul class="rules-list">
              <li class="rules-item">每找到一个错误点：+20分</li>
              <li class="rules-item">剩余时间奖励：每2秒+1分</li>
              <li class="rules-item">最高得分：100分</li>
            </ul>
          </div>
          
          <div class="rules-section">
            <h4 class="rules-subtitle">💡 游戏技巧</h4>
            <ul class="rules-list">
              <li class="rules-item">仔细阅读每一条款</li>
              <li class="rules-item">关注法律术语和表述</li>
              <li class="rules-item">注意权利义务的平衡</li>
              <li class="rules-item">合理分配时间</li>
            </ul>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="modal-btn primary-btn" @click="startRandomGame">
            <span class="btn-icon">🎮</span>
            <span class="btn-text">开始游戏</span>
          </button>
          <button class="modal-btn secondary-btn" @click="closeRulesModal">
            <span class="btn-text">我知道了</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAvailableContracts } from '@/data/contracts.js'

export default {
  name: 'GameIntro',
  setup() {
    const router = useRouter()
    const showRulesModal = ref(false)
    const contractTypes = ref([])

    const getIconById = (id) => ({ 1:'💼',2:'🏠',3:'📦',4:'💻',5:'🔧',6:'🛡️' }[id] || '📄')
    const getDifficultyById = (id) => ({ 1:'⭐',2:'⭐⭐',3:'⭐⭐⭐',4:'⭐⭐⭐⭐',5:'⭐⭐⭐⭐⭐',6:'⭐⭐⭐' }[id] || '⭐')

    // 开始游戏 - 跳转到选择页面
    const startGame = () => {
      router.push('/select')
    }

    // 随机开始游戏 - 随机选择一套合同直接开始
    const startRandomGame = () => {
      if (contractTypes.value.length === 0) {
        console.log('没有可用的合同')
        return
      }
      
      // 随机选择一个合同
      const randomIndex = Math.floor(Math.random() * contractTypes.value.length)
      const randomContract = contractTypes.value[randomIndex]
      
      // 跳转到游戏页面，并传递合同ID
      router.push(`/game?id=${randomContract.id}`)
    }

    // 显示规则
    const showRules = () => {
      showRulesModal.value = true
    }

    // 关闭规则弹窗
    const closeRulesModal = () => {
      showRulesModal.value = false
    }





    onMounted(() => {
      // 设置页面标题
      document.title = '合同找错游戏'
      
      // 获取实际合同数量与类型列表
      try {
        const contracts = getAvailableContracts()
        contractTypes.value = contracts.map(c => ({
          id: c.id,
          icon: getIconById(c.id),
          name: c.title,
          description: c.description || '标准合同模板',
          difficulty: getDifficultyById(c.id)
        }))
      } catch (error) {
        console.log('获取合同数据失败，使用默认值')
        // 使用默认合同数据
        contractTypes.value = [
          { id: 1, icon: '💼', name: '劳动合同', description: '标准劳动合同模板', difficulty: '⭐' },
          { id: 2, icon: '🏠', name: '房屋租赁', description: '房屋租赁合同模板', difficulty: '⭐⭐' },
          { id: 3, icon: '📦', name: '购销合同', description: '商品购销合同模板', difficulty: '⭐⭐⭐' }
        ]
      }
    })

    return {
      showRulesModal,
      contractTypes,
      startGame,
      startRandomGame,
      showRules,
      closeRulesModal
    }
  }
}
</script>

<style>
/* 引入游戏介绍页面样式 */
@import '../assets/styles/game/intro.css';
@import '../assets/styles/game/intro-responsive.css';
</style>