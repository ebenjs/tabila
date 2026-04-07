<script setup>
import { ref } from "vue";
import { Icon } from "@iconify/vue";
import ConfirmDialog from "../shared/ConfirmDialog.vue";

const props = defineProps({
  form: { type: Object, required: true },
  categories: { type: Array, required: true },
  onCreate: { type: Function, required: true },
  onDelete: { type: Function, required: true },
});

const showDeleteConfirm = ref(false);
const categoryToDelete = ref(null);

function askConfirmDelete(category) {
  categoryToDelete.value = category;
  showDeleteConfirm.value = true;
}

function confirmDelete() {
  if (categoryToDelete.value) {
    props.onDelete(categoryToDelete.value.id);
  }
  showDeleteConfirm.value = false;
  categoryToDelete.value = null;
}
</script>

<template>
  <ConfirmDialog
    :is-open="showDeleteConfirm"
    title="Supprimer cette categorie"
    :message="`Êtes-vous sûr de vouloir supprimer '${categoryToDelete?.name}' ?`"
    cancel-text="Annuler"
    confirm-text="Supprimer"
    is-danger
    @confirm="confirmDelete"
    @cancel="showDeleteConfirm = false"
  />

  <div class="panel two-col">
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:add-to-list" class="ui-icon" />
        Nouvelle categorie
      </h2>
      <form class="grid" @submit.prevent="props.onCreate">
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:book-open-01" class="ui-icon" /> Nom</span
          >
          <input
            v-model="props.form.name"
            type="text"
            placeholder="Desserts"
            required
          />
        </label>
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:sorting-01" class="ui-icon" /> Ordre</span
          >
          <input
            v-model.number="props.form.displayOrder"
            type="number"
            min="1"
            required
          />
        </label>
        <button class="primary icon-btn" type="submit">
          <Icon icon="hugeicons:add-01" class="ui-icon" />
          Ajouter
        </button>
      </form>
    </div>
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:menu-11" class="ui-icon" />
        Liste des categories
      </h2>
      <ul class="list">
        <li v-for="category in props.categories" :key="category.id">
          <div>
            <strong>{{ category.name }}</strong>
            <p>Ordre: {{ category.displayOrder }}</p>
          </div>
          <button
            class="danger small icon-btn"
            @click="askConfirmDelete(category)"
          >
            <Icon icon="hugeicons:delete-02" class="ui-icon" />
            Supprimer
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>
