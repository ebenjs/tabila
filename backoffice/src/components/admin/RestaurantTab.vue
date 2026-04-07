<script setup>
import { Icon } from "@iconify/vue";

defineProps({
  form: { type: Object, required: true },
  onSave: { type: Function, required: true },
});
</script>

<template>
  <div class="panel two-col">
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:settings-02" class="ui-icon" />
        Configuration du restaurant
      </h2>
      <form class="grid" @submit.prevent="onSave">
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:store-02" class="ui-icon" /> Nom du
            restaurant</span
          >
          <input v-model="form.name" type="text" required />
        </label>

        <label class="input-label">
          <span
            ><Icon icon="hugeicons:message-01" class="ui-icon" /> Message de
            bienvenue</span
          >
          <textarea v-model="form.welcomeMessage" rows="3" required></textarea>
        </label>

        <label class="input-label">
          <span
            ><Icon icon="hugeicons:color-picker" class="ui-icon" /> Couleur de
            fond</span
          >
          <input v-model="form.backgroundColor" type="color" />
        </label>
        <input v-model="form.backgroundColor" type="text" />

        <label class="input-label">
          <span
            ><Icon icon="hugeicons:paint-brush-01" class="ui-icon" /> Couleur
            d'accent</span
          >
          <input v-model="form.accentColor" type="color" />
        </label>
        <input v-model="form.accentColor" type="text" />

        <button class="primary icon-btn" type="submit">
          <Icon icon="hugeicons:save-energy-01" class="ui-icon" />
          Enregistrer
        </button>
      </form>
    </div>
    <div class="panel preview-card">
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:eye" class="ui-icon" />
        Apercu client
      </h2>
      <div
        class="restaurant-preview"
        :style="{
          background: form.backgroundColor || '#ecfeff',
          borderColor: form.accentColor || '#0f766e',
        }"
      >
        <h3
          :style="{
            color: form.accentColor || '#0f766e',
          }"
        >
          Bienvenue dans
          {{ form.name || "votre restaurant" }}
        </h3>
        <p>
          {{
            form.welcomeMessage || "Votre message de bienvenue apparaitra ici."
          }}
        </p>
      </div>
    </div>
  </div>
</template>
