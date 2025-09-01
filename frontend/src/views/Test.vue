<template>
  <div class="test-container">
    <div class="test-wrapper">
      <div class="test-header">
        <h1 class="test-title">API连接测试页面</h1>
        <p class="test-subtitle">验证前端8081端口能否访问后端8080端口API</p>
      </div>

      <div class="test-content">
        <!-- 基础连接测试 -->
        <div class="test-section">
          <h2>基础连接测试</h2>
          <div class="test-item">
            <button class="btn test-btn" @click="testBasicConnection">
              <span class="btn-icon">🔗</span>
              <span class="btn-text">测试基础连接</span>
            </button>
            <div class="test-result" v-if="basicResult">
              <span class="result-label">结果:</span>
              <span :class="['result-value', basicResult.success ? 'success' : 'error']">
                {{ basicResult.success ? '✅ 连接成功' : '❌ 连接失败' }}
              </span>
              <div class="result-details" v-if="basicResult.message">
                {{ basicResult.message }}
              </div>
            </div>
          </div>
        </div>

        <!-- 具体API测试 -->
        <div class="test-section">
          <h2>具体API测试</h2>
          
          <div class="test-item">
            <button class="btn test-btn" @click="testInputAPI">
              <span class="btn-icon">📝</span>
              <span class="btn-text">测试 /test/input API</span>
            </button>
            <div class="test-result" v-if="inputResult">
              <span class="result-label">结果:</span>
              <span :class="['result-value', inputResult.success ? 'success' : 'error']">
                {{ inputResult.success ? '✅ 成功' : '❌ 失败' }}
              </span>
              <div class="result-details" v-if="inputResult.message">
                响应: {{ inputResult.message }}
              </div>
            </div>
          </div>

          <div class="test-item">
            <button class="btn test-btn" @click="testGameAPI">
              <span class="btn-icon">🎮</span>
              <span class="btn-text">测试 /api/game/contract API</span>
            </button>
            <div class="test-result" v-if="gameResult">
              <span class="result-label">结果:</span>
              <span :class="['result-value', gameResult.success ? 'success' : 'error']">
                {{ gameResult.success ? '✅ 成功' : '❌ 失败' }}
              </span>
              <div class="result-details" v-if="gameResult.message">
                响应: {{ gameResult.message }}
              </div>
            </div>
          </div>

          <div class="test-item">
            <button class="btn test-btn" @click="testAuthAPI">
              <span class="btn-icon">🔐</span>
              <span class="btn-text">测试 /api/auth/register API</span>
            </button>
            <div class="test-result" v-if="authResult">
              <span class="result-label">结果:</span>
              <span :class="['result-value', authResult.success ? 'success' : 'error']">
                {{ authResult.success ? '✅ 成功' : '❌ 失败' }}
              </span>
              <div class="result-details" v-if="authResult.message">
                响应: {{ authResult.message }}
              </div>
            </div>
          </div>
        </div>

        <!-- 连接信息 -->
        <div class="test-section">
          <h2>连接信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">前端端口:</span>
              <span class="info-value">8081</span>
            </div>
            <div class="info-item">
              <span class="info-label">后端端口:</span>
              <span class="info-value">8080</span>
            </div>
            <div class="info-item">
              <span class="info-label">后端地址:</span>
              <span class="info-value">http://localhost:8080</span>
            </div>
            <div class="info-item">
              <span class="info-label">测试时间:</span>
              <span class="info-value">{{ currentTime }}</span>
            </div>
          </div>
        </div>

        <!-- 批量测试 -->
        <div class="test-section">
          <h2>批量测试</h2>
          <div class="test-item">
            <button class="btn test-btn primary" @click="runAllTests">
              <span class="btn-icon">🚀</span>
              <span class="btn-text">运行所有测试</span>
            </button>
            <div class="test-result" v-if="allTestsResult">
              <span class="result-label">总体结果:</span>
              <span :class="['result-value', allTestsResult.success ? 'success' : 'error']">
                {{ allTestsResult.success ? '✅ 所有测试通过' : '❌ 部分测试失败' }}
              </span>
              <div class="result-details" v-if="allTestsResult.message">
                {{ allTestsResult.message }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'Test',
  setup() {
    const basicResult = ref(null)
    const inputResult = ref(null)
    const gameResult = ref(null)
    const authResult = ref(null)
    const allTestsResult = ref(null)
    const currentTime = ref('')

    // 更新当前时间
    const updateTime = () => {
      currentTime.value = new Date().toLocaleString('zh-CN')
    }

    // 测试基础连接
    const testBasicConnection = async () => {
      try {
        const response = await axios.get('http://localhost:8080/test/input', {
          timeout: 5000
        })
        basicResult.value = {
          success: true,
          message: `状态码: ${response.status}, 响应: ${response.data}`
        }
      } catch (error) {
        basicResult.value = {
          success: false,
          message: `错误: ${error.message}`
        }
      }
    }

    // 测试 /test/input API
    const testInputAPI = async () => {
      try {
        const response = await axios.get('http://localhost:8080/test/input', {
          timeout: 5000
        })
        inputResult.value = {
          success: true,
          message: response.data
        }
      } catch (error) {
        inputResult.value = {
          success: false,
          message: error.message
        }
      }
    }

    // 测试 /api/game/contract API
    const testGameAPI = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/game/contract', {
          timeout: 5000
        })
        gameResult.value = {
          success: true,
          message: `获取到 ${response.data.length} 条合同数据`
        }
      } catch (error) {
        gameResult.value = {
          success: false,
          message: error.message
        }
      }
    }

    // 测试 /api/auth/register API
    const testAuthAPI = async () => {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/register', {
          username: 'testuser',
          password: 'testpass'
        }, {
          timeout: 5000
        })
        authResult.value = {
          success: true,
          message: `注册API响应: ${JSON.stringify(response.data)}`
        }
      } catch (error) {
        authResult.value = {
          success: false,
          message: error.message
        }
      }
    }

    // 运行所有测试
    const runAllTests = async () => {
      // 清空之前的结果
      basicResult.value = null
      inputResult.value = null
      gameResult.value = null
      authResult.value = null

      // 运行所有测试
      await testBasicConnection()
      await testInputAPI()
      await testGameAPI()
      await testAuthAPI()

      // 统计结果
      const results = [basicResult.value, inputResult.value, gameResult.value, authResult.value]
      const successCount = results.filter(r => r && r.success).length
      const totalCount = results.length

      allTestsResult.value = {
        success: successCount === totalCount,
        message: `测试完成: ${successCount}/${totalCount} 通过`
      }
    }

    onMounted(() => {
      updateTime()
      // 每秒更新时间
      setInterval(updateTime, 1000)
    })

    return {
      basicResult,
      inputResult,
      gameResult,
      authResult,
      allTestsResult,
      currentTime,
      testBasicConnection,
      testInputAPI,
      testGameAPI,
      testAuthAPI,
      runAllTests
    }
  }
}
</script>

<style scoped>
.test-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.test-wrapper {
  width: 100%;
  max-width: 900px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.test-header {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
}

.test-title {
  margin: 0 0 15px 0;
  font-size: 2rem;
  font-weight: 700;
}

.test-subtitle {
  margin: 0;
  font-size: 1rem;
  opacity: 0.9;
}

.test-content {
  padding: 25px;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.test-section h2 {
  margin: 0 0 20px 0;
  color: #182848;
  font-size: 1.5rem;
  border-bottom: 2px solid #4b6cb7;
  padding-bottom: 10px;
}

.test-item {
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
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

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.test-btn {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
  color: white;
}

.test-btn.primary {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: white;
}

.btn-icon {
  font-size: 1.2rem;
}

.test-result {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #6c757d;
}

.result-label {
  font-weight: bold;
  color: #495057;
  margin-right: 10px;
}

.result-value {
  font-weight: bold;
}

.result-value.success {
  color: #28a745;
}

.result-value.error {
  color: #dc3545;
}

.result-details {
  margin-top: 10px;
  padding: 10px;
  background: white;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9rem;
  color: #495057;
  border: 1px solid #e9ecef;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.info-label {
  font-weight: 600;
  color: #495057;
}

.info-value {
  color: #4b6cb7;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-container {
    padding: 10px;
  }

  .test-wrapper {
    border-radius: 12px;
  }

  .test-title {
    font-size: 1.5rem;
  }

  .test-content {
    padding: 15px;
  }

  .test-section {
    padding: 15px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>