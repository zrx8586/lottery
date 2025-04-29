<template>
  <div class="auth-container">
    <h1 v-if="isLoginMode">登录</h1>
    <h1 v-else>注册</h1>
    <form @submit.prevent="isLoginMode ? login() : register()">
      <div class="form-group">
        <label for="username">用户名</label>
        <input v-model="username" id="username" required />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" v-model="password" id="password" required />
      </div>
      <button type="submit" class="btn" v-if="isLoginMode">登录</button>
      <button type="submit" class="btn" v-else>注册</button>
    </form>
    <p class="toggle-mode">
      <span v-if="isLoginMode">没有账号？</span>
      <span v-else>已有账号？</span>
      <a href="#" @click.prevent="toggleMode">{{ isLoginMode ? '注册' : '登录' }}</a>
    </p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      isLoginMode: true, // 控制登录和注册模式
      username: "",
      password: "",
    };
  },
  methods: {
    toggleMode() {
      this.isLoginMode = !this.isLoginMode; // 切换模式
    },
    async login() {
      try {
        // 清除旧的 Authorization 头，防止旧 Token 干扰
        delete axios.defaults.headers.common["Authorization"];

        // 调用后端登录接口
        const response = await axios.post("/api/auth/login", {
          username: this.username,
          password: this.password,
        });

        // 存储新的 JWT Token
        const token = response.data.token;
        localStorage.setItem("token", token);

        // 设置 Axios 默认请求头
        axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

        // 触发登录成功事件
        this.$eventBus.$emit("login-success", this.username);

        // 跳转到主页面
        this.$router.push("/activity"); // 确保目标路径正确
      } catch (error) {
        // 显示详细的错误信息
        console.error("登录错误详情:", error);
        if (error.response) {
          // 服务器返回了错误响应
          alert(`登录失败：${error.response.data.message || error.response.data || "服务器错误"}`);
        } else if (error.request) {
          // 请求已发出但没有收到响应
          alert("登录失败：无法连接到服务器，请检查网络连接");
        } else {
          // 请求配置出错
          alert(`登录失败：${error.message || "未知错误"}`);
        }
      }
    },
    async register() {
      try {
        // 调用后端注册接口
        const response = await axios.post("/api/auth/register", {
          username: this.username,
          password: this.password,
        });

        // 判断后端返回的状态或数据
        if (response.status === 200) {
          alert(response.data || "注册成功，请登录！");
          this.isLoginMode = true; // 切换回登录模式
        } else {
          alert("注册失败：未知错误");
        }
      } catch (error) {
        // 显示后端返回的错误信息
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
  }
};
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.toggle-mode {
  margin-top: 15px;
  text-align: center;
}

.toggle-mode a {
  color: #007bff;
  cursor: pointer;
  text-decoration: none;
}

.toggle-mode a:hover {
  text-decoration: underline;
}
</style>