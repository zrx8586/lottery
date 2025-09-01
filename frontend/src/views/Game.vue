<template>
  <div class="game-container">
    <!-- 动态背景粒子 -->
    <div class="particles-bg">
      <div class="particle" v-for="n in 20" :key="n"></div>
    </div>
    
    <div class="game-wrapper">
      <!-- 合同选择界面 -->
      <div v-if="!selectedContract" class="contract-selection">
        <div class="selection-header">
          <div class="header-content">
            <div class="title-section">
              <h1 class="selection-title">
                <span class="title-glow">🎯</span>
                <span class="title-text">合同找错游戏</span>
                <span class="title-sparkle">✨</span>
              </h1>
              <p class="selection-subtitle">选择一份合同，在60秒内找出其中的5个法律错误点</p>
              <div class="title-decoration">
                <div class="decoration-line"></div>
                <div class="decoration-star">⭐</div>
                <div class="decoration-line"></div>
              </div>
            </div>
            
            <div class="stats-section">
              <div class="stat-card" v-for="(stat, index) in stats" :key="index">
                <div class="stat-icon-wrapper">
                  <div class="stat-icon">{{ stat.icon }}</div>
                  <div class="stat-ripple"></div>
                </div>
                <div class="stat-info">
                  <span class="stat-number">{{ stat.value }}</span>
                  <span class="stat-label">{{ stat.label }}</span>
                </div>
                <div class="stat-particles">
                  <span class="particle-dot" v-for="n in 3" :key="n"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="contract-grid">
          <div 
            v-for="contract in availableContracts" 
            :key="contract.id"
            class="contract-card"
            @click="selectContract(contract.id)"
          >
            <div class="card-glow"></div>
            <div class="card-header">
              <div class="contract-icon">
                <span class="icon-emoji">{{ getContractIcon(contract.id) }}</span>
                <div class="icon-ring"></div>
              </div>
              <div class="difficulty-badge">
                <span class="difficulty-text">{{ getDifficultyLevel(contract.id) }}</span>
                <div class="badge-glow"></div>
              </div>
            </div>
            
            <div class="card-body">
              <h3 class="contract-name">{{ contract.title }}</h3>
              <p class="contract-desc">{{ contract.description }}</p>
              
              <div class="contract-features">
                <div class="feature-item" v-for="(feature, index) in getContractFeatures(contract)" :key="index">
                  <span class="feature-icon">{{ feature.icon }}</span>
                  <span class="feature-text">{{ feature.text }}</span>
                  <div class="feature-line"></div>
                </div>
              </div>
            </div>
            
            <div class="card-footer">
              <div class="play-button">
                <span class="play-icon">▶️</span>
                <span class="play-text">开始游戏</span>
                <div class="button-particles">
                  <span class="particle" v-for="n in 5" :key="n"></span>
                </div>
              </div>
            </div>
            
            <div class="card-hover-effect"></div>
          </div>
        </div>
        
        <div class="game-info">
          <div class="info-card">
            <div class="info-icon">💡</div>
            <div class="info-content">
              <h4>游戏说明</h4>
              <p>仔细阅读合同内容，找出与相关法律法规不符的条款。每找到一个错误点可得20分，剩余时间作为额外奖励。</p>
            </div>
            <div class="info-decoration">
              <div class="decoration-circle"></div>
              <div class="decoration-dot"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 游戏界面 -->
      <div v-else class="game-interface">
        <div class="game-header">
          <div class="contract-info-header">
            <button class="back-btn" @click="backToSelection">
              <span class="btn-icon">←</span>
              <span class="btn-text">返回选择</span>
              <div class="btn-glow"></div>
            </button>
            <h2 class="contract-title">{{ selectedContract.title }}</h2>
          </div>
          
          <div class="timer-section">
            <div class="timer" :class="{ warning: timeLeft < 10, danger: timeLeft < 5 }">
              <div class="timer-glow"></div>
              <span class="time-label">倒计时:</span>
              <span class="time-value">{{ timeLeft }}s</span>
              <div class="timer-particles">
                <span class="particle" v-for="n in 3" :key="n"></span>
              </div>
            </div>
          </div>
          
          <p class="game-instruction">找出合同中的5个错误描述，点击选择你认为错误的句子</p>
        </div>

        <div class="game-content">
          <div class="contract-container">
            <div class="contract-content">
              <p
                v-for="(sentence, index) in contractSentences"
                :key="index"
                @click="toggleSelection(index)"
                :class="getSentenceClass(index)"
                class="contract-sentence"
              >
                <span class="sentence-number">{{ index + 1 }}.</span>
                <span class="sentence-text">{{ sentence }}</span>
                <span v-if="showResults">
                  <span
                    v-if="errorSentences.includes(index) && selectedSentences.includes(index)"
                    class="indicator correct-found"
                  >
                    ✅ 已找到
                  </span>
                  <span
                    v-else-if="errorSentences.includes(index) && !selectedSentences.includes(index)"
                    class="indicator correct-missed"
                  >
                    ❌ 未发现
                  </span>
                  <span
                    v-else-if="!errorSentences.includes(index) && selectedSentences.includes(index)"
                    class="indicator wrong-selected"
                  >
                    ❌ 错误选择
                  </span>
                </span>
              </p>
            </div>
          </div>

          <div class="game-actions">
            <button class="btn reset-btn" @click="resetGame">
              <span class="btn-icon">🔄</span>
              <span class="btn-text">重新开始</span>
              <div class="btn-particles"></div>
            </button>

            <div class="selection-info">
              <div class="selection-counter">
                <span class="counter-label">已选择:</span>
                <span class="counter-value">{{ selectedSentences.length }}/5</span>
                <div class="counter-progress">
                  <div class="progress-bar" :style="{ width: (selectedSentences.length / 5) * 100 + '%' }"></div>
                </div>
              </div>
            </div>

            <button
              class="btn submit-btn"
              @click="submitAnswers"
              :disabled="selectedSentences.length !== 5 || showResults"
            >
              <span class="btn-icon">✅</span>
              <span class="btn-text">确认提交</span>
              <div class="btn-glow"></div>
            </button>
          </div>

          <div class="hint-section" v-if="!showResults">
            <div class="hint-box">
              <span class="hint-icon">💡</span>
              <span class="hint-text">提示: 合同中有5处与相关法律不符的描述，请仔细查找</span>
              <div class="hint-sparkle"></div>
            </div>
          </div>

          <div class="result-section" v-if="showResults">
            <div class="result-card">
              <div class="result-header">
                <h2 class="result-title">🎉 游戏结果 🎉</h2>
                <div class="result-decoration">
                  <div class="decoration-star">⭐</div>
                  <div class="decoration-line"></div>
                  <div class="decoration-star">⭐</div>
                </div>
              </div>
              
              <div class="result-stats">
                <div class="stat-item">
                  <div class="stat-icon">🎯</div>
                  <span class="stat-label">找到错误:</span>
                  <span class="stat-value">{{ correctCount }} 个</span>
                </div>
                <div class="stat-item">
                  <div class="stat-icon">🏆</div>
                  <span class="stat-label">得分:</span>
                  <span class="stat-value score">{{ score }}/100</span>
                </div>
              </div>
              
              <div class="result-message">
                <p v-if="correctCount === 5" class="message perfect">�� 太棒了！你找到了所有错误！🎊</p>
                <p v-else-if="correctCount >= 3" class="message good">👍 不错，但还有改进空间！</p>
                <p v-else class="message poor">💪 需要加强对相关法律的了解哦！</p>
              </div>

              <div class="correct-answers-section">
                <h3 class="section-title">📋 正确答案</h3>
                <div class="correct-answers-list">
                  <div
                    v-for="(index) in errorSentences"
                    :key="index"
                    class="correct-answer-item"
                  >
                    <div class="answer-header">
                      <span class="answer-number">{{ index + 1 }}.</span>
                      <span class="answer-status">
                        <span v-if="selectedSentences.includes(index)" class="found">✓ 已找到</span>
                        <span v-else class="missed">✗ 未找到</span>
                      </span>
                    </div>
                    <p class="answer-text">{{ contractSentences[index] }}</p>
                    <div class="error-explanation" v-if="errorExplanations[index]">
                      <span class="explanation-label">错误说明:</span>
                      <span class="explanation-text">{{ errorExplanations[index] }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="action-buttons">
                <button class="btn play-again-btn" @click="resetGame">
                  <span class="btn-icon">🎮</span>
                  <span class="btn-text">再玩一次</span>
                  <div class="btn-particles"></div>
                </button>
                <button class="btn select-other-btn" @click="backToSelection">
                  <span class="btn-icon">📄</span>
                  <span class="btn-text">选择其他合同</span>
                  <div class="btn-glow"></div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { 
  getAvailableContracts, 
  getContractById, 
  getContractTitle, 
  getErrorIndices, 
  getErrorExplanations 
} from '@/data/contracts.js'

export default {
  name: 'Game',
  setup() {
    const availableContracts = ref([])
    const selectedContract = ref(null)
    const contractSentences = ref([])
    const errorSentences = ref([])
    const errorExplanations = ref({})
    const selectedSentences = ref([])
    const showResults = ref(false)
    const correctCount = ref(0)
    const score = ref(0)
    const timeLeft = ref(60)
    const gameActive = ref(false)
    let timer = null

    // 统计数据
    const stats = ref([
      { icon: '📚', value: 0, label: '套合同' },
      { icon: '⏱️', value: 60, label: '秒时限' },
      { icon: '🎯', value: 5, label: '个错误点' }
    ])

    // 获取可选择的合同列表
    const fetchAvailableContracts = async () => {
      try {
        const response = await fetch('/api/game/contracts')
        
        if (response.ok) {
          const data = await response.json()
          availableContracts.value = Array.from(data.contracts).map(id => ({
            id: id,
            title: getContractTitle(id),
            description: (getContractById(id) && getContractById(id).description) || '标准合同模板',
            totalErrors: (getContractById(id) && getContractById(id).totalErrors) || 5
          }))
          stats.value[0].value = availableContracts.value.length
          console.log('成功从后端获取合同列表')
        } else {
          throw new Error(`HTTP ${response.status}`)
        }
      } catch (error) {
        // 后端不可用时，静默使用本地数据
        console.log('后端服务不可用，使用本地合同数据')
        availableContracts.value = getAvailableContracts()
        stats.value[0].value = availableContracts.value.length
      }
    }

    // 选择合同
    const selectContract = async (contractId) => {
      try {
        const response = await fetch(`/api/game/contract/${contractId}`)
        
        if (response.ok) {
          const data = await response.json()
          
          selectedContract.value = {
            id: data.id,
            title: data.title
          }
          contractSentences.value = data.content
          errorSentences.value = getErrorIndices(contractId)
          errorExplanations.value = getErrorExplanations(contractId)
          
          // 更新页面标题为具体合同名称
          document.title = `${data.title} - 合同纠错游戏`
          
          resetGame()
          console.log('成功从后端获取合同内容')
        } else {
          throw new Error(`HTTP ${response.status}`)
        }
      } catch (error) {
        // 后端不可用时，使用本地数据
        console.log('后端服务不可用，使用本地合同数据')
        const localContract = getContractById(contractId)
        if (localContract) {
          selectedContract.value = { 
            id: localContract.id, 
            title: localContract.title 
          }
          contractSentences.value = localContract.content
          errorSentences.value = localContract.errorIndices
          errorExplanations.value = localContract.errorExplanations
          
          // 更新页面标题为具体合同名称
          document.title = `${localContract.title} - 合同纠错游戏`
          
          resetGame()
        }
      }
    }

    // 返回合同选择界面
    const backToSelection = () => {
      selectedContract.value = null
      contractSentences.value = []
      errorSentences.value = []
      errorExplanations.value = {}
      selectedSentences.value = []
      showResults.value = false
      correctCount.value = 0
      score.value = 0
      timeLeft.value = 60
      gameActive.value = false
      if (timer) clearInterval(timer)
      // 更新页面标题
      document.title = '合同纠错游戏'
    }

    // 计算句子的CSS类
    const getSentenceClass = (index) => {
      const classes = []

      if (showResults.value) {
        if (errorSentences.value.includes(index)) {
          classes.push('correct-answer')
        } else if (selectedSentences.value.includes(index)) {
          classes.push('wrong-selected')
        }
      } else if (selectedSentences.value.includes(index)) {
        classes.push('selected')
      }

      return classes
    }

    const toggleSelection = (index) => {
      if (showResults.value) return

      const position = selectedSentences.value.indexOf(index)
      if (position === -1) {
        if (selectedSentences.value.length < 5) {
          selectedSentences.value.push(index)
        }
      } else {
        selectedSentences.value.splice(position, 1)
      }
    }

    const submitAnswers = async () => {
      if (showResults.value) return;

      clearInterval(timer)
      gameActive.value = false

      try {
        const response = await fetch('/api/game/submit', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            selectedIndices: selectedSentences.value,
            timeLeft: timeLeft.value,
            contractId: selectedContract.value.id
          })
        })

        if (response.ok) {
          const result = await response.json()
          correctCount.value = result.correctCount
          score.value = result.score
          console.log('成功提交答案到后端')
        } else {
          // 如果后端返回错误，使用前端计算
          console.log('后端返回错误，使用前端计算')
          calculateResultLocally()
        }
      } catch (error) {
        // 后端不可用时，使用前端计算
        console.log('后端服务不可用，使用前端计算')
        calculateResultLocally()
      }

      showResults.value = true
    }

    const calculateResultLocally = () => {
      correctCount.value = selectedSentences.value.filter(
        index => errorSentences.value.includes(index)
      ).length

      const timeBonus = Math.floor(timeLeft.value / 2)
      score.value = Math.min(100, correctCount.value * 20 + timeBonus)
    }

    const resetGame = () => {
      selectedSentences.value = []
      showResults.value = false
      correctCount.value = 0
      score.value = 0
      timeLeft.value = 60
      gameActive.value = true

      if (timer) clearInterval(timer)
      timer = setInterval(() => {
        if (timeLeft.value > 0) {
          timeLeft.value--
        } else {
          clearInterval(timer)
          submitAnswers()
        }
      }, 1000)
    }

    watch(timeLeft, (newValue) => {
      if (newValue === 0 && gameActive.value) {
        submitAnswers()
      }
    })

    onMounted(() => {
      fetchAvailableContracts()
      // 设置页面标题
      document.title = '合同纠错游戏'
    })

    // 获取合同图标
    const getContractIcon = (contractId) => {
      const icons = {
        1: '💼', // 劳动合同
        2: '🏠', // 房屋租赁
        3: '📦', // 购销合同
        4: '💻', // 技术开发
        5: '🔧'  // 服务合同
      }
      return icons[contractId] || '📄'
    }

    // 获取难度等级
    const getDifficultyLevel = (contractId) => {
      const difficulties = {
        1: '⭐',    // 劳动合同 - 基础
        2: '⭐⭐',  // 房屋租赁 - 中等
        3: '⭐⭐⭐', // 购销合同 - 较难
        4: '⭐⭐⭐⭐', // 技术开发 - 困难
        5: '⭐⭐⭐⭐⭐' // 服务合同 - 专家
      }
      return difficulties[contractId] || '⭐'
    }

    // 获取合同长度
    const getContractLength = (contractId) => {
      const lengths = {
        1: 25, // 劳动合同
        2: 25, // 房屋租赁
        3: 22, // 购销合同
        4: 26, // 技术开发
        5: 26  // 服务合同
      }
      return lengths[contractId] || 20
    }

    // 获取合同特性
    const getContractFeatures = (contract) => {
      return [
        { icon: '🔍', text: `${contract.totalErrors}个错误点` },
        { icon: '⏰', text: '60秒时限' },
        { icon: '📝', text: `${getContractLength(contract.id)}条条款` }
      ]
    }

    return {
      availableContracts,
      selectedContract,
      contractSentences,
      selectedSentences,
      showResults,
      correctCount,
      score,
      timeLeft,
      errorSentences,
      errorExplanations,
      stats,
      selectContract,
      backToSelection,
      toggleSelection,
      submitAnswers,
      resetGame,
      getSentenceClass,
      getContractIcon,
      getDifficultyLevel,
      getContractLength,
      getContractFeatures
    }
  }
}
</script>

<style scoped>
/* 游戏化样式 - 方案C */

.game-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow-x: hidden;
}

.game-wrapper {
  width: 100%;
  max-width: 1000px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 合同选择界面 */
.contract-selection {
  padding: 40px 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: white;
  position: relative;
  overflow: hidden;
}

.selection-header {
  margin-bottom: 50px;
  position: relative;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.title-section {
  margin-bottom: 40px;
}

.selection-title {
  font-size: 3.5rem;
  color: white;
  margin-bottom: 20px;
  font-weight: 800;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  background: linear-gradient(45deg, #fff, #f0f8ff, #fff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  animation: titleGlow 3s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  0% { text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #fff; }
  100% { text-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff; }
}

.selection-subtitle {
  font-size: 1.4rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 300;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.stats-section {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  border-radius: 24px;
  padding: 25px 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  min-width: 120px;
  position: relative;
  overflow: hidden;
  animation: statCardFloat 3s ease-in-out infinite;
}

.stat-card:nth-child(1) { animation-delay: 0s; }
.stat-card:nth-child(2) { animation-delay: 0.5s; }
.stat-card:nth-child(3) { animation-delay: 1s; }

@keyframes statCardFloat {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.05);
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
  display: block;
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.stat-info {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 800;
  color: #ffd700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: numberPulse 2s ease-in-out infinite;
}

@keyframes numberPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.stat-label {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.contract-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  margin-top: 50px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding-bottom: 20px;
}

.contract-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 28px;
  padding: 0;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  overflow: hidden;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(15px);
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.contract-card:hover {
  transform: translateY(-15px) scale(1.03);
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.4);
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px 25px 20px;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.contract-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  backdrop-filter: blur(10px);
  position: relative;
  animation: iconFloat 3s ease-in-out infinite;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-5px) rotate(5deg); }
}

@keyframes iconRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.icon-emoji {
  font-size: 2.2rem;
  position: relative;
  z-index: 2;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.icon-ring {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  animation: ringRotate 4s linear infinite;
}

@keyframes ringRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 优化难度徽章样式 */
.difficulty-badge {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  color: #8b6914;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
  position: relative;
  animation: badgeFloat 2s ease-in-out infinite;
  border: 1px solid rgba(255, 215, 0, 0.5);
}

@keyframes badgeFloat {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-3px); }
}

.difficulty-text {
  position: relative;
  z-index: 2;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}


.badge-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  border-radius: 20px;
  animation: badgeGlow 2s ease-in-out infinite;
}

@keyframes badgeGlow {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}

@keyframes badgeShine {
  0%, 100% { box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
  50% { box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3); }
}

.card-body {
  padding: 25px;
}

.contract-name {
  font-size: 1.6rem;
  color: #182848;
  margin-bottom: 15px;
  font-weight: 700;
  line-height: 1.3;
}

.contract-desc {
  color: #6c757d;
  margin-bottom: 25px;
  font-size: 1rem;
  line-height: 1.5;
  min-height: 3em;
}

.contract-features {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-item:hover {
  background: #e9ecef;
  transform: translateX(5px);
}

.feature-icon {
  font-size: 1.2rem;
  width: 20px;
  text-align: center;
  animation: featureIconPulse 2s ease-in-out infinite;
}

@keyframes featureIconPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.feature-text {
  font-size: 0.9rem;
  color: #495057;
  font-weight: 500;
}

.card-footer {
  padding: 0 25px 25px;
}

.play-button {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  padding: 15px 25px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
  position: relative;
  overflow: hidden;
}

.play-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(40, 167, 69, 0.4);
}

.play-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.play-button:hover::before {
  left: 100%;
}

.play-icon {
  font-size: 1.2rem;
}

.play-text {
  font-size: 1rem;
}

.card-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.5s ease;
  pointer-events: none;
  border-radius: 28px;
}

.contract-card:hover .card-hover-effect {
  opacity: 1;
}

.game-info {
  margin-top: 50px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.info-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  border-radius: 24px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: flex-start;
  gap: 20px;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

.info-card:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-5px);
}

.info-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
  animation: iconBounce 2s ease-in-out infinite;
}

.info-content h4 {
  color: white;
  font-size: 1.3rem;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.info-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  line-height: 1.6;
  margin: 0;
}

/* 游戏界面样式 */
.game-interface {
  display: block;
}

.game-header {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.contract-info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.back-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.back-btn:hover::before {
  left: 100%;
}

.btn-icon {
  font-size: 1.2rem;
}

.btn-text {
  font-weight: 600;
  font-size: 1rem;
}

.contract-title {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  flex: 1;
  text-align: center;
}

.timer-section {
  margin-bottom: 15px;
}

.timer {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 1.5rem;
  font-weight: 700;
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 30px;
  backdrop-filter: blur(10px);
  animation: timerPulse 2s ease-in-out infinite;
}

@keyframes timerPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.timer.warning {
  background: rgba(255, 71, 87, 0.3);
  animation: timerWarning 1s infinite;
}

@keyframes timerWarning {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.game-instruction {
  margin: 0;
  font-size: 1rem;
  opacity: 0.9;
}

.game-content {
  padding: 25px;
}

.contract-container {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
  max-height: 50vh;
  overflow-y: auto;
}

.contract-content {
  line-height: 1.8;
}

.contract-sentence {
  margin-bottom: 15px;
  padding: 15px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: flex-start;
  gap: 10px;
  position: relative;
  border-left: 4px solid transparent;
  animation: sentenceFadeIn 0.5s ease-in-out;
}

@keyframes sentenceFadeIn {
  0% { opacity: 0; transform: translateY(10px); }
  100% { opacity: 1; transform: translateY(0); }
}

.contract-sentence:hover {
  background-color: #e9f7ff;
  transform: translateY(-2px);
}

.contract-sentence.selected {
  background-color: #d4edda;
  border-left: 4px solid #28a745;
  animation: sentenceSelect 0.3s ease-in-out;
}

@keyframes sentenceSelect {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

.contract-sentence.correct-answer {
  background-color: #d1ecf1;
  border-left: 4px solid #17a2b8;
}

.contract-sentence.wrong-selected {
  background-color: #f8d7da;
  border-left: 4px solid #dc3545;
}

.sentence-number {
  font-weight: 600;
  color: #4b6cb7;
  min-width: 24px;
}

.sentence-text {
  flex: 1;
}

.indicator {
  font-size: 0.9rem;
  font-weight: bold;
  padding: 3px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.correct-found {
  color: #0c5460;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
}

.correct-missed {
  color: #0c5460;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
}

.wrong-selected {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}

.game-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 25px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
}

.btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.btn:hover::before {
  left: 100%;
}

.reset-btn {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
  color: white;
}

.submit-btn {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}

.play-again-btn {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: white;
}

.select-other-btn {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
  color: white;
}

.selection-info {
  display: flex;
  align-items: center;
}

.selection-count {
  font-size: 1.1rem;
  font-weight: 600;
  color: #495057;
  padding: 8px 16px;
  background: rgba(73, 80, 87, 0.1);
  border-radius: 20px;
  animation: countPulse 1s ease-in-out infinite;
}

@keyframes countPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.hint-section {
  margin-bottom: 25px;
}

.hint-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  color: #856404;
  position: relative;
  overflow: hidden;
  animation: hintGlow 2s ease-in-out infinite alternate;
}

@keyframes hintGlow {
  0% { box-shadow: 0 0 5px rgba(255, 193, 7, 0.3); }
  100% { box-shadow: 0 0 15px rgba(255, 193, 7, 0.5); }
}

.hint-icon {
  font-size: 1.5rem;
  animation: hintIconBounce 2s ease-in-out infinite;
}

@keyframes hintIconBounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-3px); }
}

.hint-text {
  flex: 1;
  font-size: 0.95rem;
}

.result-section {
  display: flex;
  justify-content: center;
}

.result-card {
  width: 100%;
  padding: 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  animation: resultCardSlideIn 0.5s ease-in-out;
}

@keyframes resultCardSlideIn {
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
}

.result-title {
  margin: 0 0 20px 0;
  color: #182848;
  font-size: 1.8rem;
  text-align: center;
  animation: resultTitleGlow 2s ease-in-out infinite alternate;
}

@keyframes resultTitleGlow {
  0% { text-shadow: 0 0 5px rgba(24, 40, 72, 0.3); }
  100% { text-shadow: 0 0 15px rgba(24, 40, 72, 0.5); }
}

.result-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 25px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  animation: statItemFadeIn 0.5s ease-in-out;
}

.stat-item:nth-child(1) { animation-delay: 0.1s; }
.stat-item:nth-child(2) { animation-delay: 0.2s; }

@keyframes statItemFadeIn {
  0% { opacity: 0; transform: scale(0.8); }
  100% { opacity: 1; transform: scale(1); }
}

.stat-label {
  font-size: 1rem;
  color: #6c757d;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4b6cb7;
}

.stat-value.score {
  color: #28a745;
  font-size: 2rem;
  animation: scoreValueGlow 2s ease-in-out infinite alternate;
}

@keyframes scoreValueGlow {
  0% { text-shadow: 0 0 5px rgba(40, 167, 69, 0.3); }
  100% { text-shadow: 0 0 15px rgba(40, 167, 69, 0.5); }
}

.result-message {
  margin-bottom: 25px;
  text-align: center;
}

.message {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
  animation: messageSlideIn 0.5s ease-in-out;
}

@keyframes messageSlideIn {
  0% { opacity: 0; transform: translateX(-20px); }
  100% { opacity: 1; transform: translateX(0); }
}

.message.perfect {
  color: #28a745;
  animation: perfectMessageGlow 2s ease-in-out infinite alternate;
}

@keyframes perfectMessageGlow {
  0% { text-shadow: 0 0 5px rgba(40, 167, 69, 0.3); }
  100% { text-shadow: 0 0 15px rgba(40, 167, 69, 0.5); }
}

.message.good {
  color: #ffc107;
}

.message.poor {
  color: #dc3545;
}

.correct-answers-section {
  margin: 30px 0;
}

.section-title {
  text-align: center;
  color: #182848;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.correct-answers-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.correct-answer-item {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  animation: answerItemSlideIn 0.5s ease-in-out;
}

.correct-answer-item:nth-child(1) { animation-delay: 0.1s; }
.correct-answer-item:nth-child(2) { animation-delay: 0.2s; }
.correct-answer-item:nth-child(3) { animation-delay: 0.3s; }
.correct-answer-item:nth-child(4) { animation-delay: 0.4s; }
.correct-answer-item:nth-child(5) { animation-delay: 0.5s; }

@keyframes answerItemSlideIn {
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e9ecef;
}

.answer-number {
  font-weight: bold;
  color: #4b6cb7;
}

.answer-status .found {
  color: #28a745;
  font-weight: bold;
}

.answer-status .missed {
  color: #dc3545;
  font-weight: bold;
}

.answer-text {
  font-style: italic;
  margin: 0 0 15px 0;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 5px;
  border-left: 3px solid #17a2b8;
}

.error-explanation {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 12px;
  margin-top: 10px;
}

.explanation-label {
  font-weight: 600;
  color: #495057;
  margin-right: 8px;
}

.explanation-text {
  color: #6c757d;
  font-style: italic;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-container {
    padding: 10px;
  }

  .game-wrapper {
    border-radius: 12px;
  }

  .selection-title {
    font-size: 2.5rem;
  }

  .selection-subtitle {
    font-size: 1.2rem;
  }

  .stats-section {
    gap: 20px;
  }

  .stat-card {
    padding: 20px 25px;
    min-width: 100px;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .contract-grid {
    grid-template-columns: 1fr;
    gap: 25px;
    margin-top: 30px;
    margin-bottom: 30px;
  }

  .contract-card {
    margin: 0 10px;
    max-width: 100%;
  }

  .card-header {
    padding: 20px 20px 15px;
  }

  .card-body {
    padding: 20px;
  }

  .card-footer {
    padding: 0 20px 20px;
  }

  .stats-section {
    flex-direction: row;
    justify-content: space-around;
    gap: 15px;
  }

  .stat-card {
    flex: 1;
    min-width: auto;
    padding: 15px 20px;
  }

  .stat-icon {
    font-size: 2rem;
    margin-bottom: 10px;
  }

  .stat-number {
    font-size: 1.3rem;
  }

  .stat-label {
    font-size: 0.8rem;
  }

  .contract-info-header {
    flex-direction: column;
    gap: 15px;
  }

  .back-btn {
    align-self: flex-start;
  }

  .contract-title {
    font-size: 1.5rem;
  }

  .timer {
    font-size: 1.2rem;
    padding: 6px 15px;
  }

  .game-content {
    padding: 15px;
  }

  .contract-container {
    padding: 15px 10px;
  }

  .contract-sentence {
    padding: 12px;
    margin-bottom: 12px;
  }

  .sentence-number {
    min-width: 20px;
  }

  .game-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .btn {
    justify-content: center;
    width: 100%;
  }

  .selection-info {
    order: -1;
  }

  .result-card {
    padding: 20px;
  }

  .result-stats {
    flex-direction: column;
    gap: 15px;
  }

  .stat-value {
    font-size: 1.3rem;
  }

  .stat-value.score {
    font-size: 1.5rem;
  }

  .answer-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .game-header {
    padding: 20px 15px;
  }

  .selection-title {
    font-size: 2rem;
  }

  .selection-subtitle {
    font-size: 1rem;
  }

  .contract-selection {
    padding: 20px 15px;
    padding-bottom: 80px;
  }

  .stats-section {
    flex-direction: row;
    justify-content: space-between;
    gap: 10px;
    margin: 0 10px;
  }

  .stat-card {
    flex: 1;
    max-width: none;
    padding: 12px 15px;
  }

  .stat-icon {
    font-size: 1.5rem;
    margin-bottom: 8px;
  }

  .stat-number {
    font-size: 1.1rem;
  }

  .stat-label {
    font-size: 0.7rem;
  }

  .contract-grid {
    margin-top: 20px;
    gap: 20px;
    margin-bottom: 20px;
  }

  .card-header {
    padding: 15px 15px 10px;
  }

  .card-body {
    padding: 15px;
  }

  .card-footer {
    padding: 0 15px 15px;
  }

  .info-card {
    padding: 20px;
    flex-direction: column;
    text-align: center;
  }

  .title-section {
    margin-bottom: 25px;
  }

  .selection-title {
    margin-bottom: 15px;
  }

  .game-instruction {
    font-size: 0.9rem;
  }

  .hint-box {
    padding: 12px 15px;
    gap: 8px;
  }

  .hint-text {
    font-size: 0.85rem;
  }

  .result-title {
    font-size: 1.5rem;
  }

  .message {
    font-size: 1rem;
  }

  .correct-answer-item {
    padding: 15px;
  }
}

/* 苹果设备特殊优化 */
@supports (-webkit-touch-callout: none) {
  .contract-selection {
    padding-bottom: env(safe-area-inset-bottom, 100px);
  }
  
  .contract-grid {
    margin-bottom: env(safe-area-inset-bottom, 30px);
  }
  
  .game-info {
    margin-bottom: env(safe-area-inset-bottom, 20px);
  }
}

/* iPhone 14 等设备优化 */
@media (max-width: 390px) and (max-height: 844px) {
  .contract-selection {
    padding: 15px 15px 80px;
  }
  
  .selection-header {
    margin-bottom: 25px;
  }
  
  .title-section {
    margin-bottom: 15px;
  }
  
  .selection-title {
    font-size: 2rem;
    margin-bottom: 10px;
  }
  
  .selection-subtitle {
    font-size: 1rem;
    line-height: 1.4;
  }
  
  .stats-section {
    gap: 8px;
    margin-bottom: 20px;
    flex-direction: column;
    align-items: center;
  }
  
  .stat-card {
    padding: 12px 15px;
    width: 100%;
    max-width: 200px;
    margin-bottom: 8px;
  }
  
  .stat-icon {
    font-size: 1.8rem;
    margin-bottom: 8px;
  }
  
  .stat-number {
    font-size: 1.2rem;
  }
  
  .stat-label {
    font-size: 0.75rem;
  }
  
  .contract-grid {
    gap: 15px;
    margin-top: 20px;
    margin-bottom: 20px;
  }
  
  .contract-card {
    margin: 0 5px;
  }
  
  .card-header {
    padding: 15px 15px 10px;
  }
  
  .card-body {
    padding: 15px;
  }
  
  .card-footer {
    padding: 0 15px 15px;
  }
  
  .game-info {
    margin-top: 30px;
  }
  
  .info-card {
    padding: 20px;
    flex-direction: column;
    text-align: center;
  }
}

/* 超小屏幕优化 */
@media (max-width: 375px) {
  .contract-selection {
    padding: 15px 15px 70px;
  }
  
  .selection-title {
    font-size: 1.8rem;
    margin-bottom: 8px;
  }
  
  .selection-subtitle {
    font-size: 0.9rem;
    line-height: 1.3;
  }
  
  .stats-section {
    gap: 6px;
    margin-bottom: 15px;
  }
  
  .stat-card {
    padding: 10px 12px;
    margin-bottom: 6px;
  }
  
  .stat-icon {
    font-size: 1.5rem;
    margin-bottom: 6px;
  }
  
  .stat-number {
    font-size: 1rem;
  }
  
  .stat-label {
    font-size: 0.65rem;
  }
  
  .contract-grid {
    gap: 12px;
    margin-top: 15px;
    margin-bottom: 15px;
  }
  
  .card-header {
    padding: 12px 12px 8px;
  }
  
  .card-body {
    padding: 12px;
  }
  
  .card-footer {
    padding: 0 12px 12px;
  }
  
  .info-card {
    padding: 15px;
  }
}

/* 针对iPhone 14等设备的特殊优化 */
@media (max-width: 390px) and (max-height: 844px) {
  .stats-section {
    flex-direction: row;
    justify-content: space-between;
    gap: 6px;
    margin: 0 10px 15px;
  }
  
  .stat-card {
    flex: 1;
    max-width: none;
    margin-bottom: 0;
    padding: 10px 8px;
  }
  
  .stat-icon {
    font-size: 1.5rem;
    margin-bottom: 6px;
  }
  
  .stat-number {
    font-size: 1rem;
  }
  
  .stat-label {
    font-size: 0.7rem;
  }
  
  .contract-grid {
    gap: 12px;
    margin-top: 15px;
  }
  
  .contract-card {
    margin: 0 3px;
  }
  
  .card-header {
    padding: 12px 15px 8px;
  }
  
  .card-body {
    padding: 12px 15px;
  }
  
  .card-footer {
    padding: 0 15px 12px;
  }
  
  .play-button {
    padding: 12px 20px;
    font-size: 0.9rem;
  }
  
  .play-icon {
    font-size: 1rem;
  }
  
  .play-text {
    font-size: 0.9rem;
  }
}
</style>