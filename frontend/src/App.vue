<template>
  <div id="app">
    <header class="header" v-if="isLoggedIn && !isLoginPage">
      <div class="user-info">
        <img class="avatar" src="https://via.placeholder.com/30" alt="用户头像" />
        <span>当前用户：{{ username }}</span>
        <button @click="confirmLogout">登出</button>
      </div>
    </header>
    <div class="main-container">
      <aside class="sidebar" v-if="isLoggedIn && !isLoginPage">
        <div class="logo">管理后台</div>
        <ul class="menu">
          <li><router-link to="/activity" active-class="active">活动管理</router-link></li>
          <li><router-link to="/activityPrizeRelationship" active-class="active">活动奖品关系管理</router-link></li>
          <li><router-link to="/prizes" active-class="active">奖品管理</router-link></li>
          <li><router-link to="/cache" active-class="active">缓存管理</router-link></li>
          <li><router-link to="/role" active-class="active">角色管理</router-link></li>
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
  height: 50px;
  background-color: #2c3e50;
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
  gap: 10px;
}

.header .user-info .avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 2px solid white;
}

.header .user-info button {
  padding: 5px 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.header .user-info button:hover {
  background-color: #c0392b;
}

/* 主容器 */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 50px); /* 减去 header 的高度 */
}

/* 左侧侧边栏 */
.sidebar {
  width: 240px;
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar .logo {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  background-color: #1a252f;
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
  display: block;
  padding: 15px 20px;
  color: white;
  text-decoration: none;
  transition: background-color 0.3s;
}

.sidebar .menu a:hover {
  background-color: #34495e;
}

.sidebar .menu a.active {
  background-color: #1abc9c;
}

/* 右侧内容区域 */
.content {
  flex: 1;
  padding: 20px;
  background-color: #ecf0f1;
  overflow-y: auto;
  box-sizing: border-box;
}
</style>