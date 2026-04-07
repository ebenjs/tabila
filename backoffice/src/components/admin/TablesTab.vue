<script setup>
import { ref } from "vue";
import { Icon } from "@iconify/vue";
import ConfirmDialog from "../shared/ConfirmDialog.vue";

const props = defineProps({
  form: { type: Object, required: true },
  tables: { type: Array, required: true },
  onCreate: { type: Function, required: true },
  onDelete: { type: Function, required: true },
});

const showDeleteConfirm = ref(false);
const tableToDelete = ref(null);

function askConfirmDelete(table) {
  tableToDelete.value = table;
  showDeleteConfirm.value = true;
}

function confirmDelete() {
  if (tableToDelete.value) {
    props.onDelete(tableToDelete.value.id);
  }
  showDeleteConfirm.value = false;
  tableToDelete.value = null;
}
</script>

<template>
  <ConfirmDialog
    :is-open="showDeleteConfirm"
    title="Supprimer cette table"
    :message="`Êtes-vous sûr de vouloir supprimer '${tableToDelete?.name}' ?`"
    cancel-text="Annuler"
    confirm-text="Supprimer"
    is-danger
    @confirm="confirmDelete"
    @cancel="showDeleteConfirm = false"
  />

  <div class="panel two-col">
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:add-square" class="ui-icon" />
        Nouvelle table
      </h2>
      <form class="grid" @submit.prevent="props.onCreate">
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:dining-table" class="ui-icon" /> Nom</span
          >
          <input
            v-model="props.form.name"
            type="text"
            placeholder="Table Terrasse"
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
        <Icon icon="hugeicons:grid-table" class="ui-icon" />
        Liste des tables
      </h2>
      <ul class="list">
        <li v-for="table in props.tables" :key="table.id">
          <div>
            <strong>{{ table.name }}</strong>
            <p>{{ table.qrCodeUrl }}</p>
          </div>
          <button
            class="danger small icon-btn"
            @click="askConfirmDelete(table)"
          >
            <Icon icon="hugeicons:delete-02" class="ui-icon" />
            Supprimer
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>
