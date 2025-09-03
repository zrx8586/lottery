<template>
  <div class="game-container safe-area-bottom">
    <!-- 合同选择界面 -->
    <div v-if="!selectedContract" class="contract-selection">
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

      <!-- 游戏说明卡片 -->
      <div class="game-info-section">
        <div class="info-card">
          <div class="info-header">
            <div class="info-icon">💡</div>
            <h3 class="info-title">游戏说明</h3>
          </div>
          <p class="info-text">仔细阅读合同内容，找出与相关法律法规不符的条款。每找到一个错误点可得20分，剩余时间作为额外奖励。</p>
        </div>
      </div>

      <!-- 合同列表 -->
      <div class="contracts-section">
        <div class="section-header">
          <h2 class="section-title">合同模板</h2>
          <span class="contract-count">{{ availableContracts.length }} 个合同</span>
        </div>
        
        <div class="contracts-grid">
          <div 
            v-for="contract in availableContracts" 
            :key="contract.id"
            class="contract-card"
            @click="selectContract(contract.id)"
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
    </div>

    <!-- 游戏界面 -->
    <div v-else class="game-interface">
      <!-- 顶部导航栏 -->
      <div class="top-nav">
        <div class="nav-content">
          <button class="back-btn" @click="backToSelection">
            <span class="back-icon">←</span>
          </button>
          <h1 class="page-title">{{ selectedContract.title }}</h1>
          <div class="nav-actions">
            <div class="timer-display" :class="{ warning: timeLeft < 10, danger: timeLeft < 5 }">
              <span class="timer-icon">⏱️</span>
              <span class="timer-text">{{ timeLeft }}s</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 游戏状态栏 -->
      <div class="game-status-bar">
        <div class="status-content">
          <div class="progress-info">
            <span class="progress-text">已选择: {{ selectedSentences.length }}/5</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: (selectedSentences.length / 5) * 100 + '%' }"></div>
            </div>
          </div>
          <div class="game-instruction">
            <span class="instruction-icon">💡</span>
            <span class="instruction-text">找出合同中的5个错误描述，点击选择你认为错误的句子</span>
          </div>
        </div>
      </div>

      <!-- 合同内容区域 -->
      <div class="contract-content-section">
        <!-- 合同头部信息（不可选择） -->
        <div v-if="headerInfoSentences.length" class="contract-header-card">
          <div class="header-card-title">
            <span class="header-icon">📋</span>
            <span class="header-text">合同基本信息</span>
          </div>
          <div class="header-info-list">
            <div v-for="(line, i) in headerInfoSentences" :key="i" class="header-info-item">
              {{ line }}
            </div>
          </div>
        </div>

        <!-- 合同条款列表 -->
        <div class="contract-clauses-section">
          <div class="clauses-header">
            <h3 class="clauses-title">合同条款</h3>
            <span class="clauses-count">{{ displayedIndices.length }} 条</span>
          </div>
          
          <div class="clauses-list">
            <div
              v-for="(idx, pos) in displayedIndices"
              :key="idx"
              @click="toggleSelection(idx)"
              :class="getSentenceClass(idx)"
              class="clause-item"
            >
              <div class="clause-number">{{ pos + 1 }}</div>
              <div class="clause-content">
                <div class="clause-text">{{ contractSentences[idx] }}</div>
                <div v-if="showResults" class="clause-result">
                  <span
                    v-if="errorSentences.includes(idx) && selectedSentences.includes(idx)"
                    class="result-indicator correct"
                  >
                    ✅ 已找到
                  </span>
                  <span
                    v-else-if="errorSentences.includes(idx) && !selectedSentences.includes(idx)"
                    class="result-indicator missed"
                  >
                    ❌ 未发现
                  </span>
                  <span
                    v-else-if="!errorSentences.includes(idx) && selectedSentences.includes(idx)"
                    class="result-indicator wrong"
                  >
                    ❌ 错误选择
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 游戏操作区域 -->
      <div class="game-actions-section">
        <div class="actions-content">
          <div class="action-buttons">
            <button class="action-btn reset-btn" @click="resetGame">
              <span class="btn-icon">🔄</span>
              <span class="btn-text">重新开始</span>
            </button>

            <button
              class="action-btn submit-btn"
              @click="submitAnswers"
              :disabled="selectedSentences.length !== 5 || showResults"
              :class="{ disabled: selectedSentences.length !== 5 || showResults }"
            >
              <span class="btn-icon">✅</span>
              <span class="btn-text">确认提交</span>
            </button>
          </div>

          <div class="hint-section" v-if="!showResults">
            <div class="hint-card">
              <div class="hint-icon">💡</div>
              <div class="hint-content">
                <span class="hint-text">提示: 合同中有5处与相关法律不符的描述，请仔细查找</span>
              </div>
            </div>
          </div>
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
                <div class="stat-item">
                  <div class="stat-icon">⏱️</div>
                  <span class="stat-label">用时:</span>
                  <span class="stat-value">{{ 60 - timeLeft }}s</span>
                </div>
              </div>

              <!-- 用户 vs AI 对比（占位AI值，后续接入真实数据） -->
              <div class="user-ai-compare">
                <div class="compare-card">
                  <div class="compare-title">你的成绩</div>
                  <div class="compare-number">{{ correctCount }}</div>
                  <div class="compare-sub">在 {{ 60 - timeLeft }}s 内找到</div>
                </div>
                <div class="compare-card ai">
                  <div class="compare-title">AI成绩</div>
                  <div class="compare-number">{{ correctCount }}</div>
                  <div class="compare-sub">同样用时可找到</div>
                </div>
              </div>
              
              <div class="result-message">
                <p v-if="correctCount === 5" class="message perfect">�� 太棒了！你找到了所有错误！🎊</p>
                <p v-else-if="correctCount >= 3" class="message good">👍 不错，但还有改进空间！</p>
                <p v-else class="message poor">💪 需要加强对相关法律的了解哦！</p>
              </div>

              <!-- 详细的正确答案已迁移至结果详情页（ResultsDetail.vue） -->

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
                <button class="btn select-other-btn" @click="goToResultsDetail">
                  <span class="btn-icon">🔎</span>
                  <span class="btn-text">查看详情</span>
                  <div class="btn-glow"></div>
                </button>
                <button class="btn select-other-btn" @click="goToAIAnalysis">
                  <span class="btn-icon">🤖</span>
                  <span class="btn-text">AI分析</span>
                  <div class="btn-glow"></div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getContractById, 
  getErrorIndices, 
  getErrorExplanations,
  getHeaderIndices
} from '@/data/contracts.js'

export default {
  name: 'Game',
  setup() {
    const router = useRouter()
    const availableContracts = ref([])
    const selectedContract = ref(null)
    const contractSentences = ref([])
    const errorSentences = ref([])
    const errorExplanations = ref({})
    const selectedSentences = ref([])
    const staticInfoIndices = ref(new Set())
    const headerInfoSentences = ref([])
    const displayedIndices = ref([])
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

    // Note: 合同选择已移至 ContractSelect.vue，这里不再拉取选择列表

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
          computeStaticHeader(contractSentences.value, contractId)
          recomputeDisplayedIndices()
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
          computeStaticHeader(contractSentences.value, contractId)
          recomputeDisplayedIndices()
          errorSentences.value = localContract.errorIndices
          errorExplanations.value = localContract.errorExplanations
          
          // 更新页面标题为具体合同名称
          document.title = `${localContract.title} - 合同纠错游戏`
          
          resetGame()
        }
      }
    }

    // 返回上一页
    const goBack = () => {
      router.back()
    }

    // 返回合同选择界面
    const backToSelection = () => {
      // 若来自结果详情且带有缓存，则恢复结果页而不是回到选择
      try {
        const cached = JSON.parse(sessionStorage.getItem('resultsDetail') || 'null')
        if (cached && cached.sentences && cached.errorIndices) {
          // 恢复到结果视图
          contractSentences.value = cached.sentences
          errorSentences.value = cached.errorIndices
          selectedSentences.value = cached.userSelections || []
          errorExplanations.value = cached.errorExplanations || {}
          showResults.value = true
          correctCount.value = cached.userFound || 0
          score.value = Math.min(100, (cached.userFound || 0) * 20 + Math.floor(timeLeft.value / 2))
          return
        }
      } catch (e) { /* ignore */ }

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

      if (staticInfoIndices.value.has(index)) {
        classes.push('non-selectable')
      }

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
      if (staticInfoIndices.value.has(index)) return

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

      // 跳转到结果页并传递结果（也写入缓存，支持刷新/返回）
      const payload = {
        sentences: contractSentences.value,
        errorIndices: errorSentences.value,
        userSelections: selectedSentences.value,
        errorExplanations: errorExplanations.value,
        userFound: correctCount.value,
        aiFound: correctCount.value, // TODO: 替换为真实AI值
        score: score.value,
        timeUsed: 60 - timeLeft.value,
        contractId: selectedContract.value && selectedContract.value.id
      }
      try { sessionStorage.setItem('gameResult', JSON.stringify(payload)) } catch (e) { /* ignore */ }
      router.push({ name: 'GameResult', state: payload })
    }

    // 跳转到结果详情
    const goToResultsDetail = () => {
      if (!showResults.value) return
      const payload = {
        sentences: contractSentences.value,
        errorIndices: errorSentences.value,
        userSelections: selectedSentences.value,
        errorExplanations: errorExplanations.value,
        userFound: correctCount.value,
        aiFound: correctCount.value // TODO 接入真实AI值
      }
      try {
        sessionStorage.setItem('resultsDetail', JSON.stringify(payload))
      } catch (e) { /* ignore quota or unavailable storage */ }
      router.push({
        name: 'ResultsDetail',
        state: payload
      })
    }

    // 跳转到AI分析
    const goToAIAnalysis = () => {
      if (!showResults.value) return
      router.push({
        name: 'AIAnalysis',
        state: {
          // 可根据需要传入结果概要信息
          userFound: correctCount.value,
          totalErrors: errorSentences.value.length,
          timeUsed: 60 - timeLeft.value
        }
      })
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
      // 1) 如果路由带有 id（从选择页进入），优先开始新游戏并清理旧缓存
      const idStr = (typeof window !== 'undefined' && window.location && new URL(window.location.href).searchParams.get('id')) || null
      if (idStr) {
        try { sessionStorage.removeItem('resultsDetail') } catch (e) { /* ignore */ }
        const id = parseInt(idStr, 10)
        if (!Number.isNaN(id)) {
          selectContract(id)
        }
        document.title = '合同纠错游戏'
        return
      }

      // 2) 否则：若从详情页返回且有缓存，直接恢复结果视图
      try {
        const cached = JSON.parse(sessionStorage.getItem('resultsDetail') || 'null')
        if (cached && cached.sentences && cached.errorIndices) {
          contractSentences.value = cached.sentences
          errorSentences.value = cached.errorIndices
          selectedSentences.value = cached.userSelections || []
          errorExplanations.value = cached.errorExplanations || {}
          showResults.value = true
          correctCount.value = cached.userFound || 0
          score.value = Math.min(100, (cached.userFound || 0) * 20 + Math.floor(timeLeft.value / 2))
          document.title = '合同纠错游戏'
          return
        }
      } catch (e) { /* ignore */ }

      // 3) 两者都没有，则回到选择页
      try { router.replace({ name: 'GameSelect' }) } catch (e) { /* ignore navigation error */ }
      document.title = '合同纠错游戏'
    })

    // 计算合同头部信息：将甲乙方等固定信息标记为不可选择并提取展示
    const computeStaticHeader = (sentences, contractId) => {
      staticInfoIndices.value = new Set()
      headerInfoSentences.value = []
      // 先使用数据源中定义的头部索引
      const dsHeader = (contractId && getHeaderIndices(contractId)) || []
      if (dsHeader.length > 0) {
        dsHeader.forEach(i => {
          if (sentences[i] != null) {
            staticInfoIndices.value.add(i)
            headerInfoSentences.value.push(sentences[i])
          }
        })
        return
      }
      const headerKeywords = [
        '甲方', '乙方', '丙方', '身份证', '联系方式', '联系电话', '联系地址', '房屋地址',
        '签订日期', '签订地点', '合同编号', '供方', '需方', '委托方', '开发方', '服务方', '项目名称'
      ]
      // 判断是否属于头部信息的函数
      const isHeaderLine = (line) => {
        if (!line) return false
        const trimmed = String(line).trim()
        // 硬性规则：明显的章节起始视作正文
        if (/^一、|^二、|^三、|^四、|^五、/.test(trimmed)) return false
        // 前若干行且命中关键词，视作头部
        if (headerKeywords.some(k => trimmed.startsWith(k))) return true
        return false
      }
      // 提取连续的头部段落（通常位于前部）
      for (let i = 0; i < sentences.length; i++) {
        const line = sentences[i]
        if (isHeaderLine(line)) {
          staticInfoIndices.value.add(i)
          headerInfoSentences.value.push(line)
        }
        // 当遇到首个明显章节标题后可以停止进一步收集
        if (/^一、/.test(String(line).trim())) break
      }
    }

    const recomputeDisplayedIndices = () => {
      displayedIndices.value = contractSentences.value
        .map((_, i) => i)
        .filter(i => !staticInfoIndices.value.has(i))
    }

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

    // 获取消息样式类
    const getMessageClass = () => {
      if (correctCount.value === 5) return 'perfect'
      if (correctCount.value >= 3) return 'good'
      return 'poor'
    }

    // 获取消息图标
    const getMessageIcon = () => {
      if (correctCount.value === 5) return '🎉'
      if (correctCount.value >= 3) return '👍'
      return '💪'
    }

    // 获取消息文本
    const getMessageText = () => {
      if (correctCount.value === 5) return '太棒了！你找到了所有错误！'
      if (correctCount.value >= 3) return '不错，但还有改进空间！'
      return '需要加强对相关法律的了解哦！'
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
      goBack,
      backToSelection,
      toggleSelection,
      submitAnswers,
      resetGame,
      getSentenceClass,
      getContractIcon,
      getDifficultyLevel,
      getContractLength,
      getContractFeatures,
      headerInfoSentences,
      displayedIndices,
      goToResultsDetail,
      goToAIAnalysis,
      getMessageClass,
      getMessageIcon,
      getMessageText
    }
  }
}
</script>
<style>
/* 引入腾讯电子签风格的样式文件 */
@import '../assets/styles/game/base.css';
@import '../assets/styles/game/game-tencent.css';
</style>
