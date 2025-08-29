// frontend/src/views/Game.vue
<template>
  <div class="game-container">
    <div class="game-wrapper">
      <div class="game-header">
        <h1 class="game-title">劳动合同找茬小游戏</h1>
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
              :class="{
                selected: selectedSentences.includes(index),
                error: showResults && errorSentences.includes(index),
                correct: showResults && !errorSentences.includes(index) && selectedSentences.includes(index)
              }"
              class="contract-sentence"
            >
              <span class="sentence-number">{{ index + 1 }}.</span>
              <span class="sentence-text">{{ sentence }}</span>
              <span
                v-if="showResults && errorSentences.includes(index)"
                class="error-indicator"
              >
                ❌
              </span>
              <span
                v-else-if="showResults && selectedSentences.includes(index) && !errorSentences.includes(index)"
                class="wrong-indicator"
              >
                ❌
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
            :disabled="selectedSentences.length !== 5"
          >
            <span class="btn-icon">✅</span>
            <span class="btn-text">确认提交</span>
          </button>
        </div>

        <div class="hint-section" v-if="!showResults">
          <div class="hint-box">
            <span class="hint-icon">💡</span>
            <span class="hint-text">提示: 合同中有5处与《中华人民共和国劳动法》不符的描述，请仔细查找</span>
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
              <p v-else class="message poor">需要加强对劳动法的了解哦！</p>
            </div>
            <button class="btn play-again-btn" @click="resetGame">
              <span class="btn-icon">🎮</span>
              <span class="btn-text">再玩一次</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'

export default {
  name: 'Game',
  setup() {
    const contractSentences = ref([])
    const errorSentences = ref([18, 20])
    const selectedSentences = ref([])
    const showResults = ref(false)
    const correctCount = ref(0)
    const score = ref(0)
    const timeLeft = ref(60)
    const gameActive = ref(false)
    let timer = null

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

    const fetchContract = async () => {
      try {
        const response = await fetch('/api/game/contract')
        const data = await response.json()
        contractSentences.value = data
      } catch (error) {
        console.error('获取合同数据失败:', error)
        // 如果后端不可用，使用默认数据
        contractSentences.value = [
          "甲方：______", "乙方：______", "身份证号码：______",
          "根据《中华人民共和国劳动法》，经甲乙双方平等协商同意，自愿签订本合同，共同遵守本合同所列条项。",
          "一、劳动合同期限", "第一条 本合同期限类型为______期限合同。",
          "本合同生效日期：______年______月______日，终止日期：______年______月______日，其中试用期为______。",
          "二、工作内容和义务", "第二条 乙方同意根据甲方工作需要，担任______岗位工作。甲方可依照有关规定，经与乙方协商，对乙方的工作职务和岗位进行调整。",
          "第三条 乙方应按照甲方的要求，按时完成规定的工作数量，达到规定的质量标准，并履行下列义务：",
          "1. 遵守国家宪法、法律、法规；", "2. 遵守甲方的规章制度；", "3. 维护甲方的荣誉和利益；",
          "4. 忠于职守，勤奋工作；", "5. 履行保守甲方商业秘密，不得利用甲方的商业秘密为本人或其他经济组织和个人谋取不正当的经济利益。",
          "三、劳动保护和劳动条件",
          "第四条 甲方安排乙方每日工作时间不超过八小时，平均每周不超过四十小时。甲方由于工作需要，经与工会和乙方协商后可以延长工作时间的，一般每日不得超过一小时，因特殊原因需要延长工作时间的，在保障乙方身体健康条件下延长工作时间，每日不得超过三个小时，每月不得超过三十六小时。",
          "执行综合计算工时制度的，平均日和周工作时间不超过标准工作时间。",
          "执行不定时工时制度的，工作和休息休假乙方自行安排。",
          "甲方安排乙方执行______工时制度。",
          "第五条 甲方延长乙方工作时间，应安排乙方同等时间偶尔或依法支付加班加点工资。"
        ]
      }
    }

    const submitAnswers = async () => {
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
            timeLeft: timeLeft.value
          })
        })

        if (response.ok) {
          const result = await response.json()
          correctCount.value = result.correctCount
          score.value = result.score
        } else {
          // 如果后端不可用，前端计算
          calculateResultLocally()
        }
      } catch (error) {
        console.error('提交答案失败，使用前端计算:', error)
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
      fetchContract()
      resetGame()
    })

    return {
      contractSentences,
      selectedSentences,
      showResults,
      correctCount,
      score,
      timeLeft,
      toggleSelection,
      submitAnswers,
      resetGame,
      errorSentences
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

.game-header {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
}

.game-title {
  margin: 0 0 15px 0;
  font-size: 2rem;
  font-weight: 700;
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

.contract-sentence.error {
  background-color: #f8d7da;
  border-left: 4px solid #dc3545;
}

.contract-sentence.correct {
  background-color: #d1ecf1;
  border-left: 4px solid #17a2b8;
}

.sentence-number {
  font-weight: 600;
  color: #4b6cb7;
  min-width: 24px;
}

.sentence-text {
  flex: 1;
}

.error-indicator,
.wrong-indicator {
  font-size: 1.2rem;
  font-weight: bold;
}

.error-indicator {
  color: #dc3545;
}

.wrong-indicator {
  color: #ffc107;
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
  margin-top: 20px;
  padding: 14px 28px;
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
  max-width: 500px;
  padding: 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.result-title {
  margin: 0 0 20px 0;
  color: #182848;
  font-size: 1.8rem;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .game-container {
    padding: 10px;
  }

  .game-wrapper {
    border-radius: 12px;
  }

  .game-title {
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
}

@media (max-width: 480px) {
  .game-header {
    padding: 20px 15px;
  }

  .game-title {
    font-size: 1.3rem;
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
}
</style>
