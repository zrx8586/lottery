<template>
  <div class="game-container">
    <div class="game-wrapper">
      <!-- 合同选择界面 -->
      <div v-if="!selectedContract" class="contract-selection">
        <div class="selection-header">
          <h1 class="selection-title">选择合同进行游戏</h1>
          <p class="selection-subtitle">请选择一套合同，找出其中的5个错误点</p>
        </div>
        
        <div class="contract-grid">
          <div 
            v-for="contract in availableContracts" 
            :key="contract.id"
            class="contract-card"
            @click="selectContract(contract.id)"
          >
            <div class="contract-icon">📄</div>
            <h3 class="contract-name">{{ contract.title }}</h3>
            <p class="contract-desc">{{ contract.description }}</p>
            <div class="contract-info">
              <span class="info-item">错误点: {{ contract.totalErrors }}个</span>
              <span class="info-item">时间: 60秒</span>
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
            </button>
            <h2 class="contract-title">{{ selectedContract.title }}</h2>
          </div>
          
          <div class="timer-section">
            <div class="timer" :class="{ warning: timeLeft < 10 }">
              <span class="time-label">倒计时:</span>
              <span class="time-value">{{ timeLeft }}s</span>
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
            </button>

            <div class="selection-info">
              <span class="selection-count">已选择: {{ selectedSentences.length }}/5</span>
            </div>

            <button
              class="btn submit-btn"
              @click="submitAnswers"
              :disabled="selectedSentences.length !== 5 || showResults"
            >
              <span class="btn-icon">✅</span>
              <span class="btn-text">确认提交</span>
            </button>
          </div>

          <div class="hint-section" v-if="!showResults">
            <div class="hint-box">
              <span class="hint-icon">💡</span>
              <span class="hint-text">提示: 合同中有5处与相关法律不符的描述，请仔细查找</span>
            </div>
          </div>

          <div class="result-section" v-if="showResults">
            <div class="result-card">
              <h2 class="result-title">游戏结果</h2>
              <div class="result-stats">
                <div class="stat-item">
                  <span class="stat-label">找到错误:</span>
                  <span class="stat-value">{{ correctCount }} 个</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">得分:</span>
                  <span class="stat-value score">{{ score }}/100</span>
                </div>
              </div>
              <div class="result-message">
                <p v-if="correctCount === 5" class="message perfect">太棒了！你找到了所有错误！</p>
                <p v-else-if="correctCount >= 3" class="message good">不错，但还有改进空间！</p>
                <p v-else class="message poor">需要加强对相关法律的了解哦！</p>
              </div>

              <div class="correct-answers-section">
                <h3 class="section-title">正确答案</h3>
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
                </button>
                <button class="btn select-other-btn" @click="backToSelection">
                  <span class="btn-icon">📄</span>
                  <span class="btn-text">选择其他合同</span>
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
          console.log('成功从后端获取合同列表')
        } else {
          throw new Error(`HTTP ${response.status}`)
        }
      } catch (error) {
        // 后端不可用时，静默使用本地数据
        console.log('后端服务不可用，使用本地合同数据')
        availableContracts.value = getAvailableContracts()
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
    })

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
      selectContract,
      backToSelection,
      toggleSelection,
      submitAnswers,
      resetGame,
      getSentenceClass
    }
  }
}
</script>

<style scoped>
.game-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.game-wrapper {
  width: 100%;
  max-width: 900px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.contract-selection {
  padding: 40px 20px;
  text-align: center;
}

.selection-header {
  margin-bottom: 40px;
}

.selection-title {
  font-size: 2.5rem;
  color: #182848;
  margin-bottom: 15px;
  font-weight: 700;
}

.selection-subtitle {
  font-size: 1.2rem;
  color: #6c757d;
  margin: 0;
}

.contract-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
  margin-top: 40px;
}

.contract-card {
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.contract-card:hover {
  transform: translateY(-8px);
  border-color: #4b6cb7;
  box-shadow: 0 8px 25px rgba(75, 108, 183, 0.3);
}

.contract-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.contract-name {
  font-size: 1.4rem;
  color: #182848;
  margin-bottom: 15px;
  font-weight: 600;
}

.contract-desc {
  color: #6c757d;
  margin-bottom: 20px;
  font-size: 1rem;
  line-height: 1.4;
}

.contract-info {
  display: flex;
  justify-content: space-around;
  gap: 15px;
}

.info-item {
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #495057;
  font-weight: 500;
}

.game-interface {
  display: block;
}

.game-header {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
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
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
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
}

.timer.warning {
  background: rgba(255, 71, 87, 0.3);
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
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
}

.contract-sentence:hover {
  background-color: #e9f7ff;
  transform: translateY(-2px);
}

.contract-sentence.selected {
  background-color: #d4edda;
  border-left: 4px solid #28a745;
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
}

.hint-icon {
  font-size: 1.5rem;
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
}

.result-title {
  margin: 0 0 20px 0;
  color: #182848;
  font-size: 1.8rem;
  text-align: center;
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
}

.result-message {
  margin-bottom: 25px;
  text-align: center;
}

.message {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.message.perfect {
  color: #28a745;
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

@media (max-width: 768px) {
  .game-container {
    padding: 10px;
  }

  .game-wrapper {
    border-radius: 12px;
  }

  .selection-title {
    font-size: 2rem;
  }

  .contract-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .contract-card {
    padding: 25px 15px;
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
    font-size: 1.8rem;
  }

  .selection-subtitle {
    font-size: 1rem;
  }

  .contract-selection {
    padding: 30px 15px;
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
</style>
