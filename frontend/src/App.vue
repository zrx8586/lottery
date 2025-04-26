<template>
  <div id="app">
    <aside class="sidebar">
      <div class="logo">管理后台</div>
      <ul class="menu">
        <li><router-link to="/activity" active-class="active">活动管理</router-link></li>
        <li><router-link to="/activityPrizeRelationship" active-class="active">活动奖品关系管理</router-link></li>
        <li><router-link to="/prizes" active-class="active">奖品管理</router-link></li>
        <li><router-link to="/cache" active-class="active">缓存管理</router-link></li>
      </ul>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: "App",
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
  height: 100vh;
  width: 100vw;
  font-family: Arial, sans-serif;
  color: #333;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
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