<script setup>
import { computed, onMounted, ref } from "vue";

const apiBase =
  import.meta.env.VITE_API_BASE ||
  `${window.location.protocol}//${window.location.hostname}:8080`;
const tableId = ref(
  new URLSearchParams(window.location.search).get("tableId") || "",
);
const menu = ref(null);
const loading = ref(false);
const error = ref("");

const categories = computed(() => menu.value?.categories || []);
const hasTableId = computed(() => !!tableId.value);
const bannerStyle = computed(() => ({
  background: menu.value?.backgroundColor || "#ecfeff",
  borderColor: menu.value?.accentColor || "#0f766e",
}));
const titleStyle = computed(() => ({
  color: menu.value?.accentColor || "#0f766e",
}));

async function fetchMenu() {
  if (!hasTableId.value) {
    error.value = "Aucun tableId fourni. Scanne le QR code de ta table.";
    menu.value = null;
    return;
  }

  loading.value = true;
  error.value = "";
  try {
    const response = await fetch(
      `${apiBase}/api/menu?tableId=${encodeURIComponent(tableId.value)}`,
    );
    const bodyText = await response.text();
    const body = bodyText ? JSON.parse(bodyText) : null;
    if (!response.ok) {
      throw new Error(
        body?.message || body?.error || "Impossible de charger le menu",
      );
    }
    menu.value = body;
  } catch (e) {
    error.value = e.message;
    menu.value = null;
  } finally {
    loading.value = false;
  }
}

onMounted(fetchMenu);
</script>

<template>
  <div class="menu-page">
    <div class="halo halo-top"></div>
    <div class="halo halo-bottom"></div>

    <header class="hero">
      <p class="kicker">Restaurant Menu</p>
      <div class="welcome-banner" :style="bannerStyle">
        <h1 :style="titleStyle">
          {{ menu?.restaurantName || "notre restaurant" }}
        </h1>
        <p class="subtitle">
          {{
            menu?.welcomeMessage ||
            "Faites votre choix et profitez de votre repas."
          }}
        </p>
      </div>
    </header>

    <main class="content">
      <section v-if="loading" class="state-card">Chargement du menu...</section>
      <section v-else-if="error" class="state-card error">{{ error }}</section>
      <section v-else-if="!menu" class="state-card">
        QR invalide ou table inexistante. Merci de rescanner le QR de votre
        table.
      </section>

      <template v-else>
        <section class="quick-info">
          <span class="badge">Table {{ menu.tableId }}</span>
          <span class="badge">{{ categories.length }} categories</span>
          <span class="badge">Commande bientot disponible</span>
        </section>

        <section
          v-for="category in categories"
          :key="category.id"
          class="category-block"
        >
          <div class="category-head">
            <h2>{{ category.name }}</h2>
            <span>{{ category.items.length }} item(s)</span>
          </div>

          <ul class="menu-list">
            <li v-for="item in category.items" :key="item.id" class="menu-item">
              <div class="menu-item-main">
                <h3>{{ item.name }}</h3>
                <p>{{ item.description }}</p>
                <strong>{{ item.price }} XAF</strong>
              </div>
              <img
                v-if="item.imageUrl"
                :src="item.imageUrl"
                :alt="item.name"
                class="menu-item-image"
                loading="lazy"
              />
            </li>
          </ul>
        </section>
      </template>
    </main>
  </div>
</template>
