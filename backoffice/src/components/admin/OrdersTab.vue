<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  orders: { type: Array, required: true },
  onUpdateStatus: { type: Function, required: true },
});
</script>

<template>
  <div class="panel">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:delivery-box-01" class="ui-icon" />
      Suivi des commandes
    </h2>
    <ul class="list">
      <li v-for="order in orders" :key="order.id">
        <div>
          <strong>Commande #{{ order.id }} - {{ order.tableName }}</strong>
          <p>
            {{ order.items.length }} article(s) - {{ order.totalAmount }} XAF -
            {{ order.status }}
          </p>
        </div>
        <div class="status-actions">
          <button
            class="ghost small icon-btn"
            @click="onUpdateStatus(order.id, 'PENDING')"
          >
            <Icon icon="hugeicons:clock-01" class="ui-icon" />
            Pending
          </button>
          <button
            class="ghost small icon-btn"
            @click="onUpdateStatus(order.id, 'PREPARING')"
          >
            <Icon icon="hugeicons:chef-hat" class="ui-icon" />
            Preparing
          </button>
          <button
            class="ghost small icon-btn"
            @click="onUpdateStatus(order.id, 'SERVED')"
          >
            <Icon icon="hugeicons:serving-food" class="ui-icon" />
            Served
          </button>
          <button
            class="primary small icon-btn"
            @click="onUpdateStatus(order.id, 'PAID')"
          >
            <Icon icon="hugeicons:wallet-02" class="ui-icon" />
            Paid
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>
