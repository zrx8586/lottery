<template>
  <div class="game-intro-container">
    <!-- 动态背景粒子 -->
    <div class="particles-bg">
      <div class="particle" v-for="n in 30" :key="n"></div>
    </div>
    
    <div class="intro-wrapper">
      <!-- 主标题区域 -->
      <div class="intro-header">
        <div class="title-section">
          <h1 class="main-title">
            <span class="title-glow">🎯</span>
            <span class="title-text">合同找错游戏</span>
            <span class="title-sparkle">✨</span>
          </h1>
          <p class="subtitle">提升法律意识，掌握合同要点</p>
          <div class="title-decoration">
            <div class="decoration-line"></div>
            <div class="decoration-star">⭐</div>
            <div class="decoration-line"></div>
          </div>
        </div>
      </div>

      <!-- 游戏特色介绍 -->
      <div class="features-section">
        <h2 class="section-title">游戏特色</h2>
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon-wrapper">
              <div class="feature-icon">{{ feature.icon }}</div>
              <div class="feature-ripple"></div>
            </div>
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
            <div class="feature-particles">
              <span class="particle-dot" v-for="n in 3" :key="n"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 游戏规则说明 -->
      <div class="rules-section">
        <h2 class="section-title">游戏规则</h2>
        <div class="rules-content">
          <div class="rule-item" v-for="(rule, index) in gameRules" :key="index">
            <div class="rule-number">{{ index + 1 }}</div>
            <div class="rule-text">{{ rule }}</div>
          </div>
        </div>
      </div>

      <!-- 合同类型展示 -->
      <div class="contracts-preview">
        <h2 class="section-title">合同类型</h2>
        <div class="contracts-grid">
          <div class="contract-preview" v-for="contract in contractTypes" :key="contract.id">
            <div class="contract-icon">{{ contract.icon }}</div>
            <h4 class="contract-name">{{ contract.name }}</h4>
            <p class="contract-desc">{{ contract.description }}</p>
            <div class="difficulty-badge">
              <span class="difficulty-text">{{ contract.difficulty }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 开始游戏按钮 -->
      <div class="action-section">
        <button class="start-game-btn" @click="startGame">
          <span class="btn-icon">🎮</span>
          <span class="btn-text">开始游戏</span>
          <div class="btn-particles">
            <span class="particle" v-for="n in 8" :key="n"></span>
          </div>
          <div class="btn-glow"></div>
        </button>
        
        <div class="game-stats">
          <div class="stat-item">
            <span class="stat-number">{{ totalContracts }}</span>
            <span class="stat-label">套合同</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">60</span>
            <span class="stat-label">秒时限</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">5</span>
            <span class="stat-label">个错误点</span>
          </div>
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
    const totalContracts = ref(5)

    // 游戏特色
    const features = ref([
      {
        icon: '📚',
        title: '真实合同案例',
        description: '基于真实法律合同，贴近实际工作场景'
      },
      {
        icon: '⚡',
        title: '限时挑战',
        description: '60秒内找出所有错误，考验你的反应速度'
      },
      {
        icon: '🎯',
        title: '精准识别',
        description: '培养敏锐的法律意识，提升合同审查能力'
      },
      {
        icon: '🏆',
        title: '得分系统',
        description: '根据准确率和剩余时间计算得分，挑战高分'
      }
    ])

    // 游戏规则
    const gameRules = ref([
      '仔细阅读合同内容，找出其中的法律错误',
      '点击你认为错误的句子进行选择',
      '每份合同包含5个错误点，需要全部找出',
      '游戏限时60秒，时间到自动提交答案',
      '根据找到的错误数量和剩余时间计算得分',
      '找到所有错误可获得满分100分'
    ])

    // 合同类型
    const contractTypes = ref([
      {
        id: 1,
        icon: '💼',
        name: '劳动合同',
        description: '基础难度，适合初学者',
        difficulty: '⭐'
      },
      {
        id: 2,
        icon: '🏠',
        name: '房屋租赁',
        description: '中等难度，涉及物权法',
        difficulty: '⭐⭐'
      },
      {
        id: 3,
        icon: '📦',
        name: '购销合同',
        description: '较难，涉及合同法',
        difficulty: '⭐⭐⭐'
      },
      {
        id: 4,
        icon: '💻',
        name: '技术开发',
        description: '困难，涉及知识产权',
        difficulty: '⭐⭐⭐⭐'
      },
      {
        id: 5,
        icon: '🔧',
        name: '服务合同',
        description: '专家级，综合法律知识',
        difficulty: '⭐⭐⭐⭐⭐'
      }
    ])

    // 开始游戏
    const startGame = () => {
      router.push('/game')
    }

    onMounted(() => {
      // 设置页面标题
      document.title = '合同找错游戏 - 游戏介绍'
      
      // 获取实际合同数量
      try {
        const contracts = getAvailableContracts()
        totalContracts.value = contracts.length
      } catch (error) {
        console.log('获取合同数据失败，使用默认值')
      }
    })

    return {
      features,
      gameRules,
      contractTypes,
      totalContracts,
      startGame
    }
  }
}
</script>

<style>
/* 引入游戏介绍页面样式 */
@import '../assets/styles/game/intro.css';
@import '../assets/styles/game/intro-responsive.css';
</style>