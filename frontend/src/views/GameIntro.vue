<template>
  <div class="game-intro-container safe-area-bottom">
    <!-- 顶部状态栏 -->
    <div class="status-bar">
      <div class="status-left">
        <span class="time">{{ currentTime }}</span>
      </div>
      <div class="status-center">
        <div class="network-indicator">
          <div class="signal-bars">
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
          <span class="network-text">4G</span>
        </div>
      </div>
      <div class="status-right">
        <span class="battery">76%</span>
        <div class="battery-icon"></div>
      </div>
    </div>

    <!-- 头部区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="header-left">
          <div class="version-selector">
            <span class="version-text">企业版</span>
            <span class="dropdown-arrow">▼</span>
          </div>
        </div>
        <div class="header-center">
          <h1 class="app-title">合同找错游戏</h1>
        </div>
        <div class="header-right">
          <div class="header-actions">
            <span class="action-icon">⋯</span>
            <span class="action-icon">🎯</span>
          </div>
        </div>
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
        <div class="user-action">
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
        <div class="quick-action-card" @click="showStats">
          <div class="action-icon">📊</div>
          <span class="action-text">历史记录</span>
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
        <div class="template-card" v-for="contract in contractTypes.slice(0, 3)" :key="contract.id">
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

    <!-- 游戏管理区域 -->
    <div class="game-management-section">
      <h2 class="section-title">游戏管理</h2>
      <div class="management-grid">
        <div class="management-item">
          <div class="management-icon">📚</div>
          <span class="management-text">合同库</span>
        </div>
        <div class="management-item">
          <div class="management-icon">🎯</div>
          <span class="management-text">难度设置</span>
        </div>
        <div class="management-item">
          <div class="management-icon">👥</div>
          <span class="management-text">排行榜</span>
        </div>
        <div class="management-item">
          <div class="management-icon">⚙️</div>
          <span class="management-text">设置</span>
        </div>
        <div class="management-item">
          <div class="management-icon">📈</div>
          <span class="management-text">统计</span>
        </div>
        <div class="management-item">
          <div class="management-icon">💡</div>
          <span class="management-text">帮助</span>
        </div>
      </div>
    </div>

    <!-- 游戏工具区域 -->
    <div class="game-tools-section">
      <h2 class="section-title">游戏工具</h2>
      <div class="tools-list">
        <div class="tool-item">
          <div class="tool-icon">🔍</div>
          <span class="tool-text">合同分析</span>
        </div>
        <div class="tool-item">
          <div class="tool-icon">📝</div>
          <span class="tool-text">错误记录</span>
        </div>
        <div class="tool-item">
          <div class="tool-icon">🤖</div>
          <span class="tool-text">AI助手</span>
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
    const currentTime = ref('10:47')
    const contractTypes = ref([])

    const getIconById = (id) => ({ 1:'💼',2:'🏠',3:'📦',4:'💻',5:'🔧',6:'🛡️' }[id] || '📄')
    const getDifficultyById = (id) => ({ 1:'⭐',2:'⭐⭐',3:'⭐⭐⭐',4:'⭐⭐⭐⭐',5:'⭐⭐⭐⭐⭐',6:'⭐⭐⭐' }[id] || '⭐')

    // 开始游戏
    const startGame = () => {
      router.push('/select')
    }

    // 显示规则
    const showRules = () => {
      // 可以添加规则弹窗或跳转到规则页面
      alert('游戏规则：\n1. 仔细阅读合同内容\n2. 找出其中的法律错误\n3. 60秒内完成挑战\n4. 根据准确率计算得分')
    }

    // 显示统计
    const showStats = () => {
      // 可以添加统计页面
      alert('历史记录功能开发中...')
    }

    // 更新当前时间
    const updateTime = () => {
      const now = new Date()
      const hours = now.getHours().toString().padStart(2, '0')
      const minutes = now.getMinutes().toString().padStart(2, '0')
      currentTime.value = `${hours}:${minutes}`
    }

    onMounted(() => {
      // 设置页面标题
      document.title = '合同找错游戏'
      
      // 更新当前时间
      updateTime()
      setInterval(updateTime, 60000) // 每分钟更新一次
      
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
      currentTime,
      contractTypes,
      startGame,
      showRules,
      showStats
    }
  }
}
</script>

<style>
/* 引入游戏介绍页面样式 */
@import '../assets/styles/game/intro.css';
@import '../assets/styles/game/intro-responsive.css';
</style>