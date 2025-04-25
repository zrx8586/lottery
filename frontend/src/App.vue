<template>
  <div id="app">
    <nav>
      <router-link to="/">活动管理</router-link>
      <router-link to="/prizes">奖品管理</router-link>
      <router-link to="/cache">缓存管理</router-link>
    </nav>
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
nav {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
</style>