<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  isOpen: { type: Boolean, required: true },
  title: { type: String, required: true },
  message: { type: String, required: true },
  cancelText: { type: String, default: "Annuler" },
  confirmText: { type: String, default: "Confirmer" },
  isDanger: { type: Boolean, default: false },
});

const emit = defineEmits(["confirm", "cancel"]);
</script>

<template>
  <div v-if="isOpen" class="dialog-overlay" @click.self="emit('cancel')">
    <div class="dialog">
      <h3>{{ title }}</h3>
      <p>{{ message }}</p>
      <div class="dialog-actions">
        <button class="ghost" @click="emit('cancel')">
          {{ cancelText }}
        </button>
        <button
          :class="isDanger ? 'danger' : 'primary'"
          @click="emit('confirm')"
        >
          <Icon v-if="isDanger" icon="hugeicons:delete-02" class="ui-icon" />
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog {
  background: white;
  border-radius: 14px;
  padding: 24px;
  max-width: 420px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.dialog h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 700;
}

.dialog p {
  margin: 0 0 24px 0;
  color: var(--muted);
  font-size: 14px;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.dialog-actions button {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
