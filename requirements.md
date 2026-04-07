# 📱 QR Restaurant Ordering System - Requirements Specification

## 🎯 Objective

Build a simple, fast, and scalable web-based system that allows restaurant customers to:

- Scan a QR code at their table
- View the menu
- Place orders
- Pay using mobile money

Restaurants should have a back office to:

- Manage tables
- Manage menu items
- Track orders
- View payments and balances

---

## 🧑‍🍳 User Roles

### 1. Customer (No account required)

- Scan QR code
- View menu
- Add items to cart
- Place order
- Pay via mobile money

### 2. Restaurant Admin

- Login to dashboard
- Manage tables (create/update/delete)
- Manage menu (CRUD categories + items)
- View and manage orders
- View payment history and balance

---

## 📦 Core Features (MVP)

### 1. QR Code System

- Each table has a unique QR code
- QR redirects to: `/menu?tableId=XXX`
- Table is automatically linked to the order

---

### 2. Menu Display (Frontend - Mobile First)

- List categories
- List items with:
  - Name
  - Description
  - Price
  - Image (optional)

- Add to cart
- Modify quantity

---

### 3. Cart & Ordering

- View cart
- Update/remove items
- Submit order
- Order linked to:
  - tableId
  - timestamp
  - status (pending, preparing, served, paid)

---

### 4. Payment (Mobile Money)

- Integrate mobile money API (mock initially)
- Payment flow:
  - Customer clicks "Pay"
  - Enter phone number
  - Trigger payment request
  - Confirm payment status

- Store transaction details:
  - amount
  - status
  - provider

---

### 5. Back Office (Admin Dashboard)

#### Authentication

- Simple login (email + password)

#### Tables Management

- Create table
- Generate QR code
- View list of tables

#### Menu Management

- Create categories
- Create/update/delete items

#### Orders Management

- View incoming orders (real-time or refresh)
- Update order status

#### Payments Dashboard

- View all payments
- Total revenue

---

## ⚙️ Technical Requirements

### Backend

- REST API
- Authentication (JWT or session)
- Entities:
  - Restaurant
  - Table
  - Category
  - MenuItem
  - Order
  - OrderItem
  - Payment

### Frontend

- Mobile-first web app (no native app required)
- Fast loading (<2s)
- Simple UX (max 3 steps to order)

### Database

- Relational (PostgreSQL recommended)

---

## 🔌 API Endpoints (Example)

### Public

- GET /menu?tableId=
- POST /orders
- POST /payments

### Admin

- POST /auth/login
- CRUD /tables
- CRUD /categories
- CRUD /menu-items
- GET /orders
- PATCH /orders/:id/status
- GET /payments

---

## 🚀 Non-Functional Requirements

- Fast (low latency)
- Simple UI/UX
- Works on low-end smartphones
- Minimal clicks
- Scalable to multiple restaurants later

---

## ❌ Out of Scope (for MVP)

- Native mobile apps
- Complex analytics
- Multi-language
- Inventory management
- Notifications (SMS/Push)

---

## 🔥 Future Features (V2)

- Multi-restaurant SaaS
- Advanced analytics (top dishes, peak hours)
- Stock management
- Customer accounts & loyalty
- Waiter mode (manual order input)

---

## 🧠 Development Rules (IMPORTANT)

- Keep code simple and modular
- Avoid over-engineering
- Prioritize speed over perfection
- Build MVP first, optimize later
- Every feature must answer: "Does this help a restaurant today?"

---

## ✅ Definition of Done (MVP)

- Customer can scan QR and order
- Order appears in admin dashboard
- Payment can be simulated successfully
- Restaurant can manage menu and tables

---

## ⚡ Notes for AI Coding Assistant

- Generate clean, readable code
- Prefer simple solutions over complex patterns
- Use REST conventions
- Avoid unnecessary abstractions
- Focus on shipping fast MVP
