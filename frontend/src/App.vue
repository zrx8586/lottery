<template>
  <div id="app">
    <header class="header">
      <div class="user-info">
        <span>当前用户：{{ username }}</span>
        <button @click="logout">登出</button>
      </div>
    </header>
    <div class="main-container">
      <aside class="sidebar">
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
      username: "", // 当前登录用户
    };
  },
  async created() {
    try {
      const token = localStorage.getItem("token");
      if (token) {
        const response = await axios.get("/api/auth/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.username = response.data.username;
      }
    } catch (error) {
      console.error("获取用户信息失败", error);
    }
  },
  methods: {
    logout() {
      localStorage.removeItem("token"); // 清除本地存储的Token
      this.$router.push("/login"); // 跳转到登录页面
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