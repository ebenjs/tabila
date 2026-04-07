<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  tables: { type: Array, required: true },
  customerBase: { type: String, required: true },
  isLocalCustomerBase: { type: Boolean, required: true },
  menuUrlForTable: { type: Function, required: true },
  qrImageForTable: { type: Function, required: true },
  onCopy: { type: Function, required: true },
});
</script>

<template>
  <div class="panel">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:qr-code" class="ui-icon" />
      QR Code par table
    </h2>
    <p class="qr-subtitle">
      Chaque QR ouvre la page menu client avec le tableId deja renseigne.
    </p>
    <p v-if="isLocalCustomerBase" class="qr-warning">
      URL actuelle: {{ customerBase }}. Cette adresse est locale et n'est pas
      accessible depuis un telephone externe. Definis VITE_CUSTOMER_BASE avec
      l'IP reseau (ex: http://192.168.1.20:5173).
    </p>
    <div class="qr-grid">
      <article v-for="table in tables" :key="table.id" class="qr-card">
        <img
          :src="qrImageForTable(table)"
          :alt="`QR ${table.name}`"
          class="qr-image"
        />
        <div class="qr-meta">
          <h3>{{ table.name }}</h3>
          <p>{{ menuUrlForTable(table) }}</p>
        </div>
        <div class="qr-actions">
          <a
            class="ghost small icon-btn"
            :href="menuUrlForTable(table)"
            target="_blank"
            rel="noopener"
          >
            <Icon icon="hugeicons:link-02" class="ui-icon" />
            Tester
          </a>
          <button
            class="primary small icon-btn"
            @click="onCopy(menuUrlForTable(table))"
          >
            <Icon icon="hugeicons:copy-01" class="ui-icon" />
            Copier le lien
          </button>
        </div>
      </article>
    </div>
  </div>
</template>
