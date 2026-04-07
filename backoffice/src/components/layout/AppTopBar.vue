<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  apiBase: { type: String, required: true },
  isAuthenticated: { type: Boolean, required: true },
  onRefresh: { type: Function, required: true },
  onLogout: { type: Function, required: true },
});
</script>

<template>
  <header :class="['topbar', { 'topbar-guest': !isAuthenticated }]">
    <div>
      <p class="eyebrow">Tabila</p>
      <h1 class="title-with-icon">
        <Icon
          :icon="
            isAuthenticated
              ? 'hugeicons:dashboard-circle'
              : 'hugeicons:restaurant-01'
          "
          class="ui-icon"
        />
        {{ isAuthenticated ? "Gestion Restaurant" : "Backoffice" }}
      </h1>
      <p v-if="!isAuthenticated" class="topbar-subtitle">
        Connecte-toi pour gerer ton restaurant.
      </p>
    </div>
    <div v-if="isAuthenticated" class="topbar-actions">
      <span class="api-chip"
        ><Icon icon="hugeicons:link-01" class="ui-icon" /> API:
        {{ apiBase }}</span
      >
      <button class="ghost icon-btn" @click="onRefresh">
        <Icon icon="hugeicons:refresh-03" class="ui-icon" />
        Rafraichir
      </button>
      <button class="danger icon-btn" @click="onLogout">
        <Icon icon="hugeicons:logout-01" class="ui-icon" />
        Deconnexion
      </button>
    </div>
  </header>
</template>
