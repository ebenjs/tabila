<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  paymentsDashboard: { type: Object, required: true },
});
</script>

<template>
  <div class="panel">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:payment-01" class="ui-icon" />
      Paiements
    </h2>
    <p class="revenue">
      Total revenu: {{ paymentsDashboard.totalRevenue }} XAF
    </p>
    <ul class="list">
      <li v-for="payment in paymentsDashboard.payments" :key="payment.id">
        <div>
          <strong>Transaction {{ payment.transactionRef }}</strong>
          <p>
            Commande #{{ payment.orderId }} - {{ payment.provider }} -
            {{ payment.amount }} XAF
          </p>
        </div>
        <span class="pill" :class="payment.status.toLowerCase()">{{
          payment.status
        }}</span>
      </li>
    </ul>
  </div>
</template>
