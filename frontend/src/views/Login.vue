<template>
  <div class="login-container" :key="componentKey">
    <div class="login-box">
      <div class="login-header">
        <h1>{{ isLoginMode ? '欢迎回来' : '创建账号' }}</h1>
        <p>{{ isLoginMode ? '请登录您的账号' : '注册新账号' }}</p>
      </div>
      
      <form @submit.prevent="isLoginMode ? login() : register()" class="login-form">
        <div class="form-group">
          <div class="input-group">
            <i class="icon-user"></i>
            <input 
              v-model="username" 
              type="text" 
              placeholder="用户名" 
              required 
              autocomplete="username"
            />
          </div>
        </div>
        
        <div class="form-group">
          <div class="input-group">
            <i class="icon-lock"></i>
            <input 
              v-model="password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="密码" 
              required 
              autocomplete="new-password"
              ref="passwordInput"
            />
            <i 
              :class="['icon-eye', showPassword ? 'active' : '']" 
              @click="togglePasswordVisibility"
            ></i>
          </div>
        </div>

        <button type="submit" class="submit-btn">
          {{ isLoginMode ? '登录' : '注册' }}
        </button>
      </form>

      <div class="login-footer">
        <p class="toggle-mode">
          {{ isLoginMode ? '还没有账号？' : '已有账号？' }}
          <a href="#" @click.prevent="toggleMode">
            {{ isLoginMode ? '立即注册' : '立即登录' }}
          </a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      isLoginMode: true,
      username: "",
      password: "",
      showPassword: false,
      componentKey: 0
    };
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.resetForm();
    });
  },
  methods: {
    toggleMode() {
      this.isLoginMode = !this.isLoginMode;
      this.resetForm();
    },
    resetForm() {
      this.username = "";
      this.password = "";
      this.showPassword = false;
      this.componentKey += 1;
      setTimeout(() => {
        if (this.$refs.passwordInput) {
          this.$refs.passwordInput.value = "";
          this.$refs.passwordInput.focus();
        }
      }, 100);
    },
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },
    handlePasswordInput(event) {
      this.password = event.target.value;
    },
    async login() {
      try {
        delete axios.defaults.headers.common["Authorization"];
        const response = await axios.post("/api/auth/login", {
          username: this.username,
          password: this.password,
        });

        if (response.data.success) {
          const token = response.data.data.token;
          localStorage.setItem("token", token);
          localStorage.setItem("username", response.data.data.username);
          axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
          this.$eventBus.$emit("login-success", response.data.data.username);
          this.$router.push("/activity");
        } else {
          alert(response.data.message || "登录失败");
        }
      } catch (error) {
        console.error("登录错误详情:", error);
        if (error.response) {
          alert(`登录失败：${error.response.data.message || error.response.data || "服务器错误"}`);
        } else if (error.request) {
          alert("登录失败：无法连接到服务器，请检查网络连接");
        } else {
          alert(`登录失败：${error.message || "未知错误"}`);
        }
      }
    },
    async register() {
      try {
        const response = await axios.post("/api/auth/register", {
          username: this.username,
          password: this.password,
        });

        if (response.data.success) {
          alert(response.data.message || "注册成功，请登录！");
          this.isLoginMode = true;
        } else {
          alert(response.data.message || "注册失败");
        }
      } catch (error) {
        console.error("注册错误详情:", error);
        if (error.response) {
          alert(`注册失败：${error.response.data.message || error.response.data || "服务器错误"}`);
        } else if (error.request) {
          alert("注册失败：无法连接到服务器，请检查网络连接");
        } else {
          alert(`注册失败：${error.message || "未知错误"}`);
        }
      }
    }
  },
  mounted() {
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 400px;
  transition: all 0.3s ease;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  font-size: 24px;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  cursor: text;
}

.input-group i.icon-user,
.input-group i.icon-lock {
  position: absolute;
  left: 15px;
  color: #999;
  font-size: 16px;
  pointer-events: none;
}

.input-group input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  transition: all 0.3s ease;
  cursor: text;
}

.input-group input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  outline: none;
}

.icon-eye {
  position: absolute;
  right: 15px;
  cursor: pointer;
  color: #999;
  z-index: 1;
}

.icon-eye.active {
  color: #667eea;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.toggle-mode {
  color: #666;
  font-size: 14px;
}

.toggle-mode a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.toggle-mode a:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-header h1 {
    font-size: 20px;
  }
  
  .input-group input {
    padding: 10px 15px 10px 40px;
  }
}

/* 图标样式 */
.icon-user:before {
  content: "👤";
}

.icon-lock:before {
  content: "🔒";
}

.icon-eye:before {
  content: "👁️";
}
</style>