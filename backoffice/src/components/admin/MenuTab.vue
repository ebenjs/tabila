<script setup>
import { ref } from "vue";
import { Icon } from "@iconify/vue";
import ConfirmDialog from "../shared/ConfirmDialog.vue";

const props = defineProps({
  form: { type: Object, required: true },
  categories: { type: Array, required: true },
  items: { type: Array, required: true },
  onCreate: { type: Function, required: true },
  onUpdate: { type: Function, required: true },
  onDelete: { type: Function, required: true },
});

const isEditing = ref(false);
const showDeleteConfirm = ref(false);
const itemToDelete = ref(null);

function startEdit(item) {
  isEditing.value = true;
  props.form.id = item.id;
  props.form.name = item.name;
  props.form.description = item.description;
  props.form.price = item.price;
  props.form.categoryId = item.categoryId;
  props.form.imageUrl = item.imageUrl || "";
  props.form.available = item.available;
}

function cancelEdit() {
  isEditing.value = false;
  props.form.id = null;
  props.form.name = "";
  props.form.description = "";
  props.form.price = 0;
  props.form.categoryId = "";
  props.form.imageUrl = "";
  props.form.available = true;
}

function handleSubmit() {
  if (isEditing.value) {
    props.onUpdate();
  } else {
    props.onCreate();
  }
}

function askConfirmDelete(item) {
  itemToDelete.value = item;
  showDeleteConfirm.value = true;
}

function confirmDelete() {
  if (itemToDelete.value) {
    props.onDelete(itemToDelete.value.id);
  }
  showDeleteConfirm.value = false;
  itemToDelete.value = null;
}
</script>

<template>
  <ConfirmDialog
    :is-open="showDeleteConfirm"
    title="Supprimer cet article"
    :message="`Êtes-vous sûr de vouloir supprimer '${itemToDelete?.name}' ?`"
    cancel-text="Annuler"
    confirm-text="Supprimer"
    is-danger
    @confirm="confirmDelete"
    @cancel="showDeleteConfirm = false"
  />

  <div class="panel two-col">
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:menu-restaurant" class="ui-icon" />
        {{ isEditing ? "Modifier l'article" : "Nouvel article" }}
      </h2>
      <form class="grid" @submit.prevent="handleSubmit">
        <label class="input-label">
          <span><Icon icon="hugeicons:note-01" class="ui-icon" /> Nom</span>
          <input v-model="props.form.name" type="text" required />
        </label>
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:comment-01" class="ui-icon" />
            Description</span
          >
          <textarea
            v-model="props.form.description"
            rows="3"
            required
          ></textarea>
        </label>
        <label class="input-label">
          <span><Icon icon="hugeicons:money-02" class="ui-icon" /> Prix</span>
          <input
            v-model.number="props.form.price"
            type="number"
            min="0"
            step="100"
            required
          />
        </label>
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:menu-11" class="ui-icon" /> Categorie</span
          >
          <select v-model="props.form.categoryId" required>
            <option disabled value="">Choisir</option>
            <option
              v-for="category in props.categories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.name }}
            </option>
          </select>
        </label>
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:image-01" class="ui-icon" /> Image URL</span
          >
          <input
            v-model="props.form.imageUrl"
            type="url"
            placeholder="https://..."
          />
        </label>
        <label class="inline">
          <input v-model="props.form.available" type="checkbox" />
          Disponible
        </label>
        <div class="form-actions">
          <button class="primary icon-btn" type="submit">
            <Icon
              :icon="isEditing ? 'hugeicons:edit-02' : 'hugeicons:add-01'"
              class="ui-icon"
            />
            {{ isEditing ? "Mettre à jour" : "Ajouter" }}
          </button>
          <button
            v-if="isEditing"
            class="ghost icon-btn"
            type="button"
            @click="cancelEdit"
          >
            <Icon icon="hugeicons:close-sm" class="ui-icon" />
            Annuler
          </button>
        </div>
      </form>
    </div>
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:menu-01" class="ui-icon" />
        Liste du menu
      </h2>
      <ul class="list">
        <li v-for="item in props.items" :key="item.id">
          <div>
            <strong>{{ item.name }}</strong>
            <p>{{ item.categoryName }} - {{ item.price }} XAF</p>
          </div>
          <div class="item-actions">
            <button class="primary small icon-btn" @click="startEdit(item)">
              <Icon icon="hugeicons:edit-02" class="ui-icon" />
              Éditer
            </button>
            <button
              class="danger small icon-btn"
              @click="askConfirmDelete(item)"
            >
              <Icon icon="hugeicons:delete-02" class="ui-icon" />
              Supprimer
            </button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.form-actions {
  display: flex;
  gap: 12px;
}

.item-actions {
  display: flex;
  gap: 8px;
}
</style>
