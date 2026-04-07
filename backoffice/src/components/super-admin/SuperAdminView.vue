<script setup>
import { Icon } from "@iconify/vue";
import ChangePasswordForm from "../shared/ChangePasswordForm.vue";

defineProps({
  joinRequests: { type: Array, required: true },
  restaurants: { type: Array, required: true },
  superAdminForm: { type: Object, required: true },
  createRestaurantForm: { type: Object, required: true },
  lastCreatedAdmin: { type: Object, default: null },
  changePasswordForm: { type: Object, required: true },
  onApproveJoinRequest: { type: Function, required: true },
  onCreateRestaurant: { type: Function, required: true },
  onCreateRestaurantAdmin: { type: Function, required: true },
  onChangePassword: { type: Function, required: true },
});
</script>

<template>
  <div class="panel">
    <h2 class="title-with-icon">
      <Icon icon="hugeicons:user-account" class="ui-icon" />
      Espace Super Admin
    </h2>
    <p>Gerez les demandes d'acces et les comptes admins restaurants.</p>
  </div>

  <div class="panel two-col">
    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:agreement-01" class="ui-icon" />
        Demandes de rejoindre Tabila
      </h2>
      <ul class="list">
        <li v-for="request in joinRequests" :key="request.id">
          <div>
            <strong>{{ request.restaurantName }}</strong>
            <p>{{ request.email }} - {{ request.status }}</p>
          </div>
          <button
            class="primary small icon-btn"
            :disabled="request.status !== 'PENDING'"
            @click="onApproveJoinRequest(request.id)"
          >
            <Icon icon="hugeicons:checkmark-badge-01" class="ui-icon" />
            Approuver
          </button>
        </li>
        <li v-if="joinRequests.length === 0">
          <div>
            <strong>Aucune demande</strong>
            <p>Pas de demande en attente pour le moment.</p>
          </div>
        </li>
      </ul>
    </div>

    <div>
      <h2 class="title-with-icon">
        <Icon icon="hugeicons:restaurant-02" class="ui-icon" />
        Creer un restaurant
      </h2>
      <form
        class="grid super-admin-section"
        @submit.prevent="onCreateRestaurant"
      >
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:store-02" class="ui-icon" /> Nom du
            restaurant</span
          >
          <input
            v-model="createRestaurantForm.name"
            type="text"
            placeholder="Restaurant Horizon"
            required
          />
        </label>
        <button class="primary icon-btn" type="submit">
          <Icon icon="hugeicons:add-01" class="ui-icon" />
          Creer le restaurant
        </button>
      </form>

      <h2 class="title-with-icon">
        <Icon icon="hugeicons:add-team" class="ui-icon" />
        Creer un admin restaurant
      </h2>
      <form
        class="grid super-admin-section"
        @submit.prevent="onCreateRestaurantAdmin"
      >
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:mail-account-02" class="ui-icon" /> Email
            admin</span
          >
          <input
            v-model="superAdminForm.email"
            type="email"
            placeholder="admin@restaurant.com"
            required
          />
        </label>
        <label class="input-label">
          <span
            ><Icon icon="hugeicons:restaurant-02" class="ui-icon" />
            Restaurant</span
          >
          <select v-model="superAdminForm.restaurantId" required>
            <option disabled value="">Choisir</option>
            <option
              v-for="restaurant in restaurants"
              :key="restaurant.id"
              :value="restaurant.id"
            >
              {{ restaurant.name }}
            </option>
          </select>
        </label>
        <button class="primary icon-btn" type="submit">
          <Icon icon="hugeicons:add-01" class="ui-icon" />
          Creer le compte
        </button>
      </form>

      <div v-if="lastCreatedAdmin" class="feedback success inline-feedback">
        Compte cree: {{ lastCreatedAdmin.email }} | Mot de passe temporaire
        envoye par email.
      </div>

      <ChangePasswordForm
        :form="changePasswordForm"
        :on-submit="onChangePassword"
        :with-panel="false"
      />
    </div>
  </div>
</template>

<style scoped>
.super-admin-section {
  margin-bottom: 16px;
}
</style>
