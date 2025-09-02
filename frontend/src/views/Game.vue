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
<style>
/* 引入拆分后的样式文件 */
@import '../assets/styles/game/base.css';
@import '../assets/styles/game/selection.css';
@import '../assets/styles/game/interface.css';
@import '../assets/styles/game/result.css';
@import '../assets/styles/game/responsive.css';
</style>
