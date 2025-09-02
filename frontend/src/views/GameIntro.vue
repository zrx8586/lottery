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

<style scoped>
/* 游戏介绍页面样式 */
.game-intro-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 动态背景粒子 */
.particles-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particleFloat 6s ease-in-out infinite;
}

.particle:nth-child(odd) {
  animation-delay: -2s;
  animation-duration: 8s;
}

.particle:nth-child(3n) {
  animation-delay: -4s;
  animation-duration: 10s;
}

@keyframes particleFloat {
  0%, 100% {
    transform: translateY(0px) translateX(0px);
    opacity: 0.6;
  }
  25% {
    transform: translateY(-20px) translateX(10px);
    opacity: 1;
  }
  50% {
    transform: translateY(-40px) translateX(-5px);
    opacity: 0.8;
  }
  75% {
    transform: translateY(-20px) translateX(-10px);
    opacity: 1;
  }
}

/* 主容器 */
.intro-wrapper {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  max-width: 1200px;
  width: 100%;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
  animation: introFadeIn 1s ease-out;
}

@keyframes introFadeIn {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 标题区域 */
.intro-header {
  text-align: center;
  margin-bottom: 50px;
}

.main-title {
  font-size: 3.5rem;
  font-weight: 800;
  color: #2d3748;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  animation: titleGlow 2s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  0% {
    text-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
  }
  100% {
    text-shadow: 0 0 30px rgba(102, 126, 234, 0.8);
  }
}

.title-glow, .title-sparkle {
  font-size: 2.5rem;
  animation: sparkle 1.5s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.2) rotate(180deg);
  }
}

.subtitle {
  font-size: 1.3rem;
  color: #718096;
  margin: 0 0 30px 0;
  font-weight: 500;
}

.title-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.decoration-line {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

.decoration-star {
  font-size: 1.5rem;
  animation: starTwinkle 2s ease-in-out infinite;
}

@keyframes starTwinkle {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.3);
  }
}

/* 特色区域 */
.features-section {
  margin-bottom: 50px;
}

.section-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2d3748;
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

.feature-icon-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.feature-icon {
  font-size: 3rem;
  display: inline-block;
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.feature-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 50%;
  animation: ripple 2s ease-in-out infinite;
}

@keyframes ripple {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0;
  }
}

.feature-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 15px;
}

.feature-description {
  color: #718096;
  line-height: 1.6;
  font-size: 1rem;
}

.feature-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.particle-dot {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(102, 126, 234, 0.6);
  border-radius: 50%;
  animation: particleFloat 3s ease-in-out infinite;
}

.particle-dot:nth-child(1) {
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.particle-dot:nth-child(2) {
  top: 60%;
  right: 20%;
  animation-delay: 1s;
}

.particle-dot:nth-child(3) {
  bottom: 20%;
  left: 60%;
  animation-delay: 2s;
}

/* 规则区域 */
.rules-section {
  margin-bottom: 50px;
}

.rules-content {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 16px;
  padding: 30px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.rule-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.rule-item:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateX(5px);
}

.rule-item:last-child {
  margin-bottom: 0;
}

.rule-number {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.rule-text {
  color: #4a5568;
  line-height: 1.6;
  font-size: 1rem;
}

/* 合同预览区域 */
.contracts-preview {
  margin-bottom: 50px;
}

.contracts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.contract-preview {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.contract-preview:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.15);
}

.contract-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
}

.contract-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 10px;
}

.contract-desc {
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 15px;
  line-height: 1.4;
}

.difficulty-badge {
  display: inline-block;
  background: rgba(102, 126, 234, 0.1);
  padding: 5px 12px;
  border-radius: 20px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.difficulty-text {
  color: #667eea;
  font-size: 0.9rem;
  font-weight: 500;
}

/* 操作区域 */
.action-section {
  text-align: center;
}

.start-game-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 20px 50px;
  border-radius: 50px;
  font-size: 1.3rem;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.start-game-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
}

.start-game-btn:active {
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 1.5rem;
  animation: btnIconBounce 1.5s ease-in-out infinite;
}

@keyframes btnIconBounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.btn-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.btn-particles .particle {
  position: absolute;
  width: 2px;
  height: 2px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: btnParticleFloat 2s ease-in-out infinite;
}

.btn-particles .particle:nth-child(1) { top: 20%; left: 20%; animation-delay: 0s; }
.btn-particles .particle:nth-child(2) { top: 30%; right: 20%; animation-delay: 0.2s; }
.btn-particles .particle:nth-child(3) { bottom: 20%; left: 30%; animation-delay: 0.4s; }
.btn-particles .particle:nth-child(4) { bottom: 30%; right: 30%; animation-delay: 0.6s; }
.btn-particles .particle:nth-child(5) { top: 50%; left: 10%; animation-delay: 0.8s; }
.btn-particles .particle:nth-child(6) { top: 50%; right: 10%; animation-delay: 1s; }
.btn-particles .particle:nth-child(7) { top: 70%; left: 50%; animation-delay: 1.2s; }
.btn-particles .particle:nth-child(8) { top: 10%; left: 50%; animation-delay: 1.4s; }

@keyframes btnParticleFloat {
  0%, 100% {
    transform: translateY(0px) scale(1);
    opacity: 0.8;
  }
  50% {
    transform: translateY(-10px) scale(1.2);
    opacity: 1;
  }
}

.btn-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  border-radius: inherit;
  animation: btnGlow 2s ease-in-out infinite;
}

@keyframes btnGlow {
  0%, 100% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
}

/* 游戏统计 */
.game-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  color: #718096;
  font-size: 1rem;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .intro-wrapper {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .main-title {
    font-size: 2.5rem;
    flex-direction: column;
    gap: 10px;
  }
  
  .title-glow, .title-sparkle {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .contracts-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 15px;
  }
  
  .game-stats {
    gap: 20px;
  }
  
  .stat-number {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .intro-wrapper {
    padding: 20px 15px;
  }
  
  .main-title {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 1.8rem;
  }
  
  .feature-card {
    padding: 20px;
  }
  
  .start-game-btn {
    padding: 15px 30px;
    font-size: 1.1rem;
  }
  
  .game-stats {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
