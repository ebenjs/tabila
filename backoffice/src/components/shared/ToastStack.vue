<script setup>
import { Icon } from "@iconify/vue";

const props = defineProps({
  toasts: { type: Array, required: true },
  onDismiss: { type: Function, required: true },
});

function iconFor(type) {
  return type === "error"
    ? "hugeicons:cancel-circle"
    : "hugeicons:checkmark-circle-02";
}
</script>

<template>
  <div class="toast-stack" aria-live="polite" aria-atomic="false">
    <article
      v-for="toast in props.toasts"
      :key="toast.id"
      :class="['toast', `toast-${toast.type}`]"
      role="status"
    >
      <div class="toast-body">
        <Icon :icon="iconFor(toast.type)" class="ui-icon toast-icon" />
        <p>{{ toast.message }}</p>
      </div>
      <button
        class="ghost icon-btn toast-close"
        type="button"
        aria-label="Fermer la notification"
        @click="props.onDismiss(toast.id)"
      >
        <Icon icon="hugeicons:cancel-01" class="ui-icon" />
      </button>
    </article>
  </div>
</template>

<style scoped>
.toast-stack {
  position: fixed;
  top: 18px;
  right: 18px;
  display: grid;
  gap: 10px;
  z-index: 1100;
  width: min(92vw, 360px);
}

.toast {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  border-radius: 14px;
  padding: 12px;
  border: 1px solid;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.16);
  backdrop-filter: blur(6px);
  animation: toastIn 0.24s ease-out;
}

.toast-success {
  background: rgba(220, 252, 231, 0.97);
  border-color: #86efac;
  color: #14532d;
}

.toast-error {
  background: rgba(254, 226, 226, 0.97);
  border-color: #fca5a5;
  color: #7f1d1d;
}

.toast-body {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.toast-body p {
  margin: 0;
  font-size: 13px;
  line-height: 1.4;
  font-weight: 500;
}

.toast-icon {
  margin-top: 1px;
}

.toast-close {
  padding: 6px;
  border-radius: 8px;
  min-width: 30px;
}

@keyframes toastIn {
  from {
    opacity: 0;
    transform: translateY(-8px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 600px) {
  .toast-stack {
    left: 14px;
    right: 14px;
    width: auto;
  }
}
</style>
