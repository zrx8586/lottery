// frontend/src/views/Game.vue
<template>
  <div id="app">
    <div class="container">
      <div class="header">
        <h1>劳动合同找茬小游戏</h1>
        <div class="timer" :class="{ warning: timeLeft < 10 }">
          倒计时: {{ timeLeft }}s
        </div>
        <p>找出合同中的5个错误描述，点击选择你认为错误的句子</p>
      </div>
      
      <div class="content">
        <div class="contract">
          <p v-for="(sentence, index) in contractSentences" 
             :key="index"
             @click="toggleSelection(index)"
             :class="{
               selected: selectedSentences.includes(index),
               error: showResults && errorSentences.includes(index)
             }">
            {{ sentence }}
          </p>
        </div>
        
        <div class="actions">
          <button class="reset-btn" @click="resetGame">重新开始</button>
          <span>已选择: {{ selectedSentences.length }}/5</span>
          <button class="submit-btn" @click="submitAnswers" :disabled="selectedSentences.length !== 5">
            确认提交
          </button>
        </div>
        
        <div class="hint" v-if="!showResults">
          提示: 合同中有5处与《中华人民共和国劳动法》不符的描述，请仔细查找
        </div>
        
        <div class="result" v-if="showResults">
          <h2>游戏结果</h2>
          <p>你找到了 {{ correctCount }} 个错误</p>
          <p class="score">得分: {{ score }}/100</p>
          <p v-if="correctCount === 5">太棒了！你找到了所有错误！</p>
          <p v-else-if="correctCount >= 3">不错，但还有改进空间！</p>
          <p v-else>需要加强对劳动法的了解哦！</p>
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
/* 保持之前的样式不变 */
</style>