<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  tabs: { type: Array, required: true },
  activeTab: { type: String, required: true },
});

defineEmits(["select"]);
</script>

<template>
  <nav class="tabs">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      :class="{ active: activeTab === tab.key }"
      @click="$emit('select', tab.key)"
    >
      <Icon :icon="tab.icon" class="ui-icon" />
      {{ tab.label }}
    </button>
  </nav>
</template>
