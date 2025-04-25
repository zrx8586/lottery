<template>
  <div>
    <h2>奖品列表</h2>
    <table>
      <thead>
      <tr>
        <th>奖品ID</th>
        <th>奖品名称</th>
        <th>库存</th>
        <th>概率</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="prize in prizes" :key="prize.activityPrizeId">
        <td>{{ prize.activityPrizeId }}</td>
        <td>{{ prize.prizeName }}</td>
        <td>{{ prize.quantity }}</td>
        <td>{{ prize.probability }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { getPrizesByActivityId } from '../api';

export default {
  data() {
    return {
      prizes: [],
    };
  },
  async mounted() {
    const response = await getPrizesByActivityId(1); // 示例活动ID
    this.prizes = response.data;
  },
};
</script>