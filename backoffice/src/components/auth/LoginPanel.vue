<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  loginForm: { type: Object, required: true },
  loading: { type: Boolean, required: true },
  onLogin: { type: Function, required: true },
});
</script>

<template>
  <section class="panel login-panel">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:login-circle-01" class="ui-icon" />
      Connexion admin
    </h2>
    <form class="grid" @submit.prevent="onLogin">
      <label class="input-label">
        <span
          ><Icon icon="hugeicons:mail-account-01" class="ui-icon" /> Email</span
        >
        <input v-model="loginForm.email" type="email" required />
      </label>
      <label class="input-label">
        <span
          ><Icon icon="hugeicons:ai-lock" class="ui-icon" /> Mot de passe</span
        >
        <input v-model="loginForm.password" type="password" required />
      </label>
      <button class="primary icon-btn" :disabled="loading" type="submit">
        <Icon icon="hugeicons:login-01" class="ui-icon" />
        Se connecter
      </button>
    </form>
  </section>
</template>
