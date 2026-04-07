<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from "vue";
import AppTopBar from "./components/layout/AppTopBar.vue";
import LoginPanel from "./components/auth/LoginPanel.vue";
import ChangePasswordForm from "./components/shared/ChangePasswordForm.vue";
import SuperAdminView from "./components/super-admin/SuperAdminView.vue";
import AdminTabs from "./components/admin/AdminTabs.vue";
import RestaurantTab from "./components/admin/RestaurantTab.vue";
import TablesTab from "./components/admin/TablesTab.vue";
import CategoriesTab from "./components/admin/CategoriesTab.vue";
import MenuTab from "./components/admin/MenuTab.vue";
import OrdersTab from "./components/admin/OrdersTab.vue";
import PaymentsTab from "./components/admin/PaymentsTab.vue";
import QrCodeTab from "./components/admin/QrCodeTab.vue";
import ToastStack from "./components/shared/ToastStack.vue";

const apiBase = import.meta.env.VITE_API_BASE || "http://localhost:8080";
const customerBase = normalizeBaseUrl(
  import.meta.env.VITE_CUSTOMER_BASE ||
    window.location.origin.replace(":5174", ":5173"),
);
const token = ref(localStorage.getItem("backoffice_token") || "");
const userRole = ref(localStorage.getItem("backoffice_role") || "");
const activeTab = ref("tables");
const loading = ref(false);
const toasts = ref([]);
const toastTimers = new Map();

const loginForm = reactive({
  email: "admin@tabila.local",
  password: "admin123",
});
const tableForm = reactive({ name: "" });
const categoryForm = reactive({ name: "", displayOrder: 1 });
const menuItemForm = reactive({
  id: null,
  name: "",
  description: "",
  price: 0,
  imageUrl: "",
  categoryId: "",
  available: true,
});
const restaurantConfigForm = reactive({
  name: "",
  welcomeMessage: "",
  backgroundColor: "#ecfeff",
  accentColor: "#0f766e",
});
const changePasswordForm = reactive({
  currentPassword: "",
  newPassword: "",
  confirmNewPassword: "",
});

const tables = ref([]);
const categories = ref([]);
const menuItems = ref([]);
const orders = ref([]);
const paymentsDashboard = ref({ totalRevenue: 0, payments: [] });
const joinRequests = ref([]);
const restaurants = ref([]);
const lastCreatedAdmin = ref(null);
const superAdminForm = reactive({ email: "", restaurantId: "" });
const createRestaurantForm = reactive({ name: "" });

const isAuthenticated = computed(() => !!token.value);
const isSuperAdmin = computed(() => userRole.value === "SUPER_ADMIN");
const isLocalCustomerBase = computed(() =>
  /localhost|127\.0\.0\.1/i.test(customerBase),
);

const adminTabs = [
  { key: "tables", label: "Tables", icon: "hugeicons:dining-table" },
  { key: "categories", label: "Categories", icon: "hugeicons:menu-restaurant" },
  { key: "menu", label: "Menu", icon: "hugeicons:menu-01" },
  { key: "orders", label: "Commandes", icon: "hugeicons:ticket-01" },
  { key: "payments", label: "Paiements", icon: "hugeicons:wallet-02" },
  { key: "qrcode", label: "QR Code", icon: "hugeicons:qr-code" },
  { key: "restaurant", label: "Restaurant", icon: "hugeicons:store-02" },
  { key: "account", label: "Compte", icon: "hugeicons:account-setting-01" },
];

function normalizeBaseUrl(value) {
  let next = (value || "").trim();
  if (!next) {
    return "http://localhost:5173";
  }
  if (!/^https?:\/\//i.test(next)) {
    next = `http://${next}`;
  }
  return next.replace(/\/+$/, "");
}

function setSuccess(msg) {
  pushToast("success", msg);
}

function setError(msg) {
  pushToast("error", msg);
}

function pushToast(type, msg) {
  const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
  toasts.value = [...toasts.value, { id, type, message: msg }];

  const timer = setTimeout(() => {
    dismissToast(id);
  }, 5000);
  toastTimers.set(id, timer);
}

function dismissToast(id) {
  toasts.value = toasts.value.filter((toast) => toast.id !== id);
  const timer = toastTimers.get(id);
  if (timer) {
    clearTimeout(timer);
    toastTimers.delete(id);
  }
}

function clearToasts() {
  toasts.value = [];
  for (const timer of toastTimers.values()) {
    clearTimeout(timer);
  }
  toastTimers.clear();
}

async function request(path, options = {}, requiresAuth = false) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };
  if (requiresAuth) {
    headers.Authorization = `Bearer ${token.value}`;
  }

  const response = await fetch(`${apiBase}${path}`, { ...options, headers });
  const bodyText = await response.text();
  let body;
  try {
    body = bodyText ? JSON.parse(bodyText) : null;
  } catch {
    body = null;
  }

  if (!response.ok) {
    const serverMessage =
      body?.message || body?.error || `Erreur ${response.status}`;
    throw new Error(serverMessage);
  }

  return body;
}

async function login() {
  loading.value = true;
  clearToasts();
  try {
    const result = await request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(loginForm),
    });
    token.value = result.token;
    userRole.value = result.role || "ADMIN";
    localStorage.setItem("backoffice_token", token.value);
    localStorage.setItem("backoffice_role", userRole.value);
    setSuccess("Connexion reussie.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  } finally {
    loading.value = false;
  }
}

function logout() {
  clearToasts();
  token.value = "";
  userRole.value = "";
  localStorage.removeItem("backoffice_token");
  localStorage.removeItem("backoffice_role");
  setSuccess("Session fermee.");
}

async function loadAll() {
  if (!token.value) {
    return;
  }
  if (isSuperAdmin.value) {
    await loadSuperAdminData();
    return;
  }

  await loadAdminData();
}

async function loadAdminData() {
  loading.value = true;
  try {
    const [
      nextTables,
      nextCategories,
      nextMenuItems,
      nextOrders,
      nextPayments,
      nextRestaurantConfig,
    ] = await Promise.all([
      request("/api/admin/tables", {}, true),
      request("/api/admin/categories", {}, true),
      request("/api/admin/menu-items", {}, true),
      request("/api/admin/orders", {}, true),
      request("/api/admin/payments", {}, true),
      request("/api/admin/restaurant-config", {}, true),
    ]);

    tables.value = nextTables;
    categories.value = nextCategories;
    menuItems.value = nextMenuItems;
    orders.value = nextOrders;
    paymentsDashboard.value = nextPayments;
    restaurantConfigForm.name = nextRestaurantConfig.name || "";
    restaurantConfigForm.welcomeMessage =
      nextRestaurantConfig.welcomeMessage || "";
    restaurantConfigForm.backgroundColor =
      nextRestaurantConfig.backgroundColor || "#ecfeff";
    restaurantConfigForm.accentColor =
      nextRestaurantConfig.accentColor || "#0f766e";
    if (!menuItemForm.categoryId && categories.value.length > 0) {
      menuItemForm.categoryId = categories.value[0].id;
    }
  } catch (e) {
    setError(e.message);
  } finally {
    loading.value = false;
  }
}

async function loadSuperAdminData() {
  loading.value = true;
  try {
    const [nextJoinRequests, nextRestaurants] = await Promise.all([
      request("/api/super-admin/join-requests", {}, true),
      request("/api/super-admin/restaurants", {}, true),
    ]);
    joinRequests.value = nextJoinRequests;
    restaurants.value = nextRestaurants;
    if (!superAdminForm.restaurantId && restaurants.value.length > 0) {
      superAdminForm.restaurantId = restaurants.value[0].id;
    }
  } catch (e) {
    setError(e.message);
  } finally {
    loading.value = false;
  }
}

async function createRestaurantAdminUser() {
  if (!superAdminForm.email.trim() || !superAdminForm.restaurantId) {
    setError("Email et restaurant requis.");
    return;
  }

  try {
    const created = await request(
      "/api/super-admin/users",
      {
        method: "POST",
        body: JSON.stringify({
          email: superAdminForm.email.trim(),
          restaurantId: Number(superAdminForm.restaurantId),
        }),
      },
      true,
    );
    lastCreatedAdmin.value = created;
    superAdminForm.email = "";
    setSuccess(
      "Admin restaurant cree. Le mot de passe temporaire a ete envoye par email.",
    );
  } catch (e) {
    setError(e.message);
  }
}

async function createRestaurant() {
  if (!createRestaurantForm.name.trim()) {
    setError("Le nom du restaurant est requis.");
    return;
  }

  try {
    const created = await request(
      "/api/super-admin/restaurants",
      {
        method: "POST",
        body: JSON.stringify({ name: createRestaurantForm.name.trim() }),
      },
      true,
    );

    createRestaurantForm.name = "";
    setSuccess(`Restaurant cree: ${created.name}.`);
    await loadSuperAdminData();
    if (!superAdminForm.restaurantId) {
      superAdminForm.restaurantId = created.id;
    }
  } catch (e) {
    setError(e.message);
  }
}

async function changeMyPassword() {
  if (!changePasswordForm.currentPassword || !changePasswordForm.newPassword) {
    setError("Mot de passe actuel et nouveau mot de passe requis.");
    return;
  }
  if (changePasswordForm.newPassword.length < 8) {
    setError("Le nouveau mot de passe doit contenir au moins 8 caracteres.");
    return;
  }
  if (
    changePasswordForm.newPassword !== changePasswordForm.confirmNewPassword
  ) {
    setError("La confirmation du nouveau mot de passe ne correspond pas.");
    return;
  }

  try {
    await request(
      "/api/admin/account/change-password",
      {
        method: "PATCH",
        body: JSON.stringify({
          currentPassword: changePasswordForm.currentPassword,
          newPassword: changePasswordForm.newPassword,
        }),
      },
      true,
    );

    changePasswordForm.currentPassword = "";
    changePasswordForm.newPassword = "";
    changePasswordForm.confirmNewPassword = "";
    setSuccess("Mot de passe modifie avec succes.");
  } catch (e) {
    setError(e.message);
  }
}

async function approveJoinRequest(id) {
  try {
    await request(
      `/api/super-admin/join-requests/${id}/approve`,
      { method: "PATCH" },
      true,
    );
    setSuccess("Demande approuvee.");
    await loadSuperAdminData();
  } catch (e) {
    setError(e.message);
  }
}

async function createTable() {
  if (!tableForm.name.trim()) return;
  try {
    await request(
      "/api/admin/tables",
      {
        method: "POST",
        body: JSON.stringify({ name: tableForm.name, active: true }),
      },
      true,
    );
    tableForm.name = "";
    setSuccess("Table creee.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function deleteTable(id) {
  try {
    await request(`/api/admin/tables/${id}`, { method: "DELETE" }, true);
    setSuccess("Table supprimee.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function createCategory() {
  if (!categoryForm.name.trim()) return;
  try {
    await request(
      "/api/admin/categories",
      {
        method: "POST",
        body: JSON.stringify({
          name: categoryForm.name,
          displayOrder: Number(categoryForm.displayOrder),
        }),
      },
      true,
    );
    categoryForm.name = "";
    categoryForm.displayOrder = categories.value.length + 1;
    setSuccess("Categorie creee.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function deleteCategory(id) {
  try {
    await request(`/api/admin/categories/${id}`, { method: "DELETE" }, true);
    setSuccess("Categorie supprimee.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function createMenuItem() {
  if (
    !menuItemForm.name.trim() ||
    !menuItemForm.description.trim() ||
    !menuItemForm.categoryId
  )
    return;
  try {
    await request(
      "/api/admin/menu-items",
      {
        method: "POST",
        body: JSON.stringify({
          name: menuItemForm.name,
          description: menuItemForm.description,
          price: Number(menuItemForm.price),
          imageUrl: menuItemForm.imageUrl || null,
          categoryId: Number(menuItemForm.categoryId),
          available: menuItemForm.available,
        }),
      },
      true,
    );
    menuItemForm.name = "";
    menuItemForm.description = "";
    menuItemForm.price = 0;
    menuItemForm.imageUrl = "";
    menuItemForm.available = true;
    setSuccess("Article cree.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function updateMenuItem() {
  if (
    !menuItemForm.name.trim() ||
    !menuItemForm.description.trim() ||
    !menuItemForm.categoryId ||
    !menuItemForm.id
  )
    return;
  try {
    await request(
      `/api/admin/menu-items/${menuItemForm.id}`,
      {
        method: "PUT",
        body: JSON.stringify({
          name: menuItemForm.name,
          description: menuItemForm.description,
          price: Number(menuItemForm.price),
          imageUrl: menuItemForm.imageUrl || null,
          categoryId: Number(menuItemForm.categoryId),
          available: menuItemForm.available,
        }),
      },
      true,
    );
    menuItemForm.id = null;
    menuItemForm.name = "";
    menuItemForm.description = "";
    menuItemForm.price = 0;
    menuItemForm.imageUrl = "";
    menuItemForm.available = true;
    setSuccess("Article mis a jour.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function deleteMenuItem(id) {
  try {
    await request(`/api/admin/menu-items/${id}`, { method: "DELETE" }, true);
    setSuccess("Article supprime.");
    if (menuItemForm.id === id) {
      menuItemForm.id = null;
      menuItemForm.name = "";
      menuItemForm.description = "";
      menuItemForm.price = 0;
      menuItemForm.imageUrl = "";
      menuItemForm.available = true;
    }
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

async function updateOrderStatus(orderId, status) {
  try {
    await request(
      `/api/admin/orders/${orderId}/status`,
      {
        method: "PATCH",
        body: JSON.stringify({ status }),
      },
      true,
    );
    setSuccess("Statut commande mis a jour.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

function menuUrlForTable(table) {
  return `${customerBase}/?tableId=${table.id}`;
}

function qrImageForTable(table) {
  return `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(menuUrlForTable(table))}`;
}

async function copyToClipboard(value) {
  try {
    await navigator.clipboard.writeText(value);
    setSuccess("Lien copie dans le presse-papiers.");
  } catch {
    setError("Copie impossible sur ce navigateur.");
  }
}

async function saveRestaurantConfig() {
  if (
    !restaurantConfigForm.name.trim() ||
    !restaurantConfigForm.welcomeMessage.trim()
  ) {
    setError("Nom du restaurant et message de bienvenue requis.");
    return;
  }

  try {
    await request(
      "/api/admin/restaurant-config",
      {
        method: "PUT",
        body: JSON.stringify({
          name: restaurantConfigForm.name.trim(),
          welcomeMessage: restaurantConfigForm.welcomeMessage.trim(),
          backgroundColor: restaurantConfigForm.backgroundColor,
          accentColor: restaurantConfigForm.accentColor,
        }),
      },
      true,
    );
    setSuccess("Configuration restaurant mise a jour.");
    await loadAll();
  } catch (e) {
    setError(e.message);
  }
}

onMounted(async () => {
  if (token.value) {
    await loadAll();
  }
});

onUnmounted(() => {
  clearToasts();
});
</script>

<template>
  <div class="page">
    <div class="orb orb-left"></div>
    <div class="orb orb-right"></div>

    <ToastStack :toasts="toasts" :on-dismiss="dismissToast" />

    <AppTopBar
      :api-base="apiBase"
      :is-authenticated="isAuthenticated"
      :on-refresh="loadAll"
      :on-logout="logout"
    />

    <LoginPanel
      v-if="!isAuthenticated"
      :login-form="loginForm"
      :loading="loading"
      :on-login="login"
    />

    <section v-else class="workspace">
      <SuperAdminView
        v-if="isSuperAdmin"
        :join-requests="joinRequests"
        :restaurants="restaurants"
        :super-admin-form="superAdminForm"
        :create-restaurant-form="createRestaurantForm"
        :last-created-admin="lastCreatedAdmin"
        :change-password-form="changePasswordForm"
        :on-approve-join-request="approveJoinRequest"
        :on-create-restaurant="createRestaurant"
        :on-create-restaurant-admin="createRestaurantAdminUser"
        :on-change-password="changeMyPassword"
      />

      <template v-else>
        <AdminTabs
          :tabs="adminTabs"
          :active-tab="activeTab"
          @select="(tabKey) => (activeTab = tabKey)"
          class="admin-tabs"
        />

        <ChangePasswordForm
          v-if="activeTab === 'account'"
          :form="changePasswordForm"
          :on-submit="changeMyPassword"
        />

        <RestaurantTab
          v-if="activeTab === 'restaurant'"
          :form="restaurantConfigForm"
          :on-save="saveRestaurantConfig"
        />

        <TablesTab
          v-if="activeTab === 'tables'"
          :form="tableForm"
          :tables="tables"
          :on-create="createTable"
          :on-delete="deleteTable"
        />

        <CategoriesTab
          v-if="activeTab === 'categories'"
          :form="categoryForm"
          :categories="categories"
          :on-create="createCategory"
          :on-delete="deleteCategory"
        />

        <MenuTab
          v-if="activeTab === 'menu'"
          :form="menuItemForm"
          :categories="categories"
          :items="menuItems"
          :on-create="createMenuItem"
          :on-update="updateMenuItem"
          :on-delete="deleteMenuItem"
        />

        <OrdersTab
          v-if="activeTab === 'orders'"
          :orders="orders"
          :on-update-status="updateOrderStatus"
        />

        <PaymentsTab
          v-if="activeTab === 'payments'"
          :payments-dashboard="paymentsDashboard"
        />

        <QrCodeTab
          v-if="activeTab === 'qrcode'"
          :tables="tables"
          :customer-base="customerBase"
          :is-local-customer-base="isLocalCustomerBase"
          :menu-url-for-table="menuUrlForTable"
          :qr-image-for-table="qrImageForTable"
          :on-copy="copyToClipboard"
        />
      </template>
    </section>
  </div>
</template>
