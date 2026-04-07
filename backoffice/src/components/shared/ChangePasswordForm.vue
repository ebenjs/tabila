<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  form: { type: Object, required: true },
  onSubmit: { type: Function, required: true },
  withPanel: { type: Boolean, default: true },
});
</script>

<template>
  <div :class="withPanel ? 'panel' : ''">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:account-setting-01" class="ui-icon" />
      Changer mon mot de passe
    </h2>
    <form class="grid" @submit.prevent="onSubmit">
      <label class="input-label">
        <span
          ><Icon icon="hugeicons:ai-lock" class="ui-icon" /> Mot de passe
          actuel</span
        >
        <input v-model="form.currentPassword" type="password" required />
      </label>
      <label class="input-label">
        <span
          ><Icon icon="hugeicons:lock-password" class="ui-icon" /> Nouveau mot
          de passe</span
        >
        <input
          v-model="form.newPassword"
          type="password"
          minlength="8"
          required
        />
      </label>
      <label class="input-label">
        <span
          ><Icon icon="hugeicons:checkmark-circle-02" class="ui-icon" />
          Confirmer le nouveau mot de passe</span
        >
        <input
          v-model="form.confirmNewPassword"
          type="password"
          minlength="8"
          required
        />
      </label>
      <button class="primary icon-btn" type="submit">
        <Icon icon="hugeicons:checkmark-circle-02" class="ui-icon" />
        Mettre a jour
      </button>
    </form>
  </div>
</template>
