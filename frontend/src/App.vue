<template>
  <div id="app">
    <header class="header" v-if="isLoggedIn && !isLoginPage && !isGamePage">
      <div class="user-info">
        <img class="avatar" src="https://via.placeholder.com/30" alt="用户头像" />
        <span>当前用户：{{ username }}</span>
        <button @click="confirmLogout">登出</button>
      </div>
    </header>
    <div class="main-container">
      <aside class="sidebar" v-if="isLoggedIn && !isLoginPage && !isGamePage">
        <div class="logo">管理后台</div>
        <ul class="menu">
          <li><router-link to="/activity" active-class="active">活动管理</router-link></li>
          <li><router-link to="/prizes" active-class="active">奖品管理</router-link></li>
          <li><router-link to="/activityPrizeRelationship" active-class="active">活动奖品关系管理</router-link></li>
          <li><router-link to="/cache" active-class="active">缓存管理</router-link></li>
          <li><router-link to="/role" active-class="active">角色管理</router-link></li>
          <li><router-link to="/lottery" active-class="active">抽奖</router-link></li>
        </ul>
      </aside>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      isLoggedIn: false,
      isLoginPage: false
    };
  },
  computed: {
    // 添加计算属性判断是否为游戏页面
    isGamePage() {
      return this.$route.path === '/game';
    }
  },
  watch: {
    $route(to) {
      this.isLoginPage = to.path === '/login';
    }
  },
  async created() {
    const token = localStorage.getItem("token");
    if (token) {
      this.isLoggedIn = true;
      try {
        const response = await axios.get("/api/auth/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.username = response.data.username;
      } catch {
        this.isLoggedIn = false;
      }
    }

    // 初始化登录页面状态
    this.isLoginPage = this.$route.path === '/login';

    // 监听登录成功事件
    this.$eventBus.$on("login-success", this.handleLoginSuccess);
  },
  beforeUnmount() {
    // 移除事件监听
    this.$eventBus.$off("login-success", this.handleLoginSuccess);
  },
  methods: {
    handleLoginSuccess(username) {
      this.isLoggedIn = true;
      this.username = username;
    },
    confirmLogout() {
      if (confirm("确定要登出吗？")) {
        this.logout();
      }
    },
    async logout() {
      try {
        const token = localStorage.getItem("token");
        if (token) {
          await axios.post(
              "/api/auth/logout",
              {},
              {
                headers: { Authorization: `Bearer ${token}` },
              }
          );
        }
      } catch (error) {
        console.error("登出失败", error);
      } finally {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        delete axios.defaults.headers.common["Authorization"];
        this.isLoggedIn = false;
        this.$router.push("/login");
      }
    },
  },
};
</script>

<style>
/* 确保 html 和 body 没有多余的 margin 和 padding */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

/* 确保 #app 使用 flex 布局并填充整个视口 */
#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  font-family: Arial, sans-serif;
  color: #333;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 顶部导航栏 */
.header {
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header .user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header .user-info .avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid white;
  object-fit: cover;
}

.header .user-info span {
  font-size: 14px;
  font-weight: 500;
}

.header .user-info button {
  padding: 6px 12px;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.header .user-info button:hover {
  background-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 主容器 */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px); /* 减去 header 的高度 */
  background-color: #f5f7fa;
}

/* 左侧侧边栏 */
.sidebar {
  width: 240px;
  background: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
}

.sidebar .logo {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-bottom: 10px;
}

.sidebar .menu {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

.sidebar .menu li {
  margin: 0;
}

.sidebar .menu a {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 14px;
  position: relative;
  border-left: 4px solid transparent;
}

.sidebar .menu a:hover {
  background-color: #f5f7fa;
  color: #667eea;
  border-left-color: #667eea;
}

.sidebar .menu a.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  font-weight: 500;
  border-left-color: #667eea;
}

.sidebar .menu a i {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 右侧内容区域 */
.content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
  box-sizing: border-box;
}

/* 图标样式 */
.icon-dashboard:before {
  content: "📊";
}

.icon-activity:before {
  content: "🎯";
}

.icon-prize:before {
  content: "🎁";
}

.icon-cache:before {
  content: "💾";
}

.icon-role:before {
  content: "👥";
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }

  .sidebar .logo {
    font-size: 18px;
    padding: 15px 0;
  }

  .sidebar .menu a {
    padding: 12px 15px;
    font-size: 13px;
  }

  .header .user-info {
    gap: 10px;
  }

  .header .user-info .avatar {
    width: 32px;
    height: 32px;
  }

  .header .user-info span {
    display: none;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 60px;
  }

  .sidebar .logo {
    font-size: 16px;
    padding: 10px 0;
  }

  .sidebar .menu a {
    padding: 10px;
    justify-content: center;
  }

  .sidebar .menu a span {
    display: none;
  }

  .sidebar .menu a i {
    margin: 0;
    font-size: 18px;
  }

  .header .user-info button {
    padding: 4px 8px;
    font-size: 12px;
  }
}

/* 全局样式放在这里 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', sans-serif;
}
body {
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
  padding: 20px;
}
.container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}
.header {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  padding: 20px;
  text-align: center;
}
.timer {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}
.timer.warning {
  color: #ff4757;
}
.content {
  padding: 25px;
}
.contract {
  background: #f9f9f9;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  line-height: 1.8;
}
.contract p {
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
  cursor: pointer;
}
.contract p:hover {
  background-color: #f0f5ff;
}
.contract p.selected {
  background-color: #d4edda;
  border-left: 4px solid #28a745;
}
.contract p.error {
  background-color: #f8d7da;
  border-left: 4px solid #dc3545;
}
.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
button {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
}
.submit-btn {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(75, 108, 183, 0.4);
}
.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.reset-btn {
  background: #6c757d;
  color: white;
}
.reset-btn:hover {
  background: #5a6268;
}
.result {
  margin-top: 20px;
  padding: 20px;
  background: #e9ecef;
  border-radius: 8px;
  text-align: center;
}
.score {
  font-size: 24px;
  font-weight: bold;
  color: #4b6cb7;
  margin: 10px 0;
}
.hint {
  color: #6c757d;
  font-size: 14px;
  margin-top: 10px;
  text-align: center;
}
</style>
