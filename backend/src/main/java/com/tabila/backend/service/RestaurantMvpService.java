package com.tabila.backend.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.tabila.backend.api.dto.CategoryRequest;
import com.tabila.backend.api.dto.CategoryResponse;
import com.tabila.backend.api.dto.CreateOrderRequest;
import com.tabila.backend.api.dto.CreatePaymentRequest;
import com.tabila.backend.api.dto.MenuCategoryResponse;
import com.tabila.backend.api.dto.MenuItemRequest;
import com.tabila.backend.api.dto.MenuItemResponse;
import com.tabila.backend.api.dto.MenuResponse;
import com.tabila.backend.api.dto.OrderItemResponse;
import com.tabila.backend.api.dto.OrderResponse;
import com.tabila.backend.api.dto.PaymentResponse;
import com.tabila.backend.api.dto.PaymentsDashboardResponse;
import com.tabila.backend.api.dto.RestaurantConfigRequest;
import com.tabila.backend.api.dto.RestaurantConfigResponse;
import com.tabila.backend.api.dto.TableRequest;
import com.tabila.backend.api.dto.TableResponse;
import com.tabila.backend.domain.Category;
import com.tabila.backend.domain.CustomerOrder;
import com.tabila.backend.domain.MenuItem;
import com.tabila.backend.domain.OrderItem;
import com.tabila.backend.domain.Payment;
import com.tabila.backend.domain.Restaurant;
import com.tabila.backend.domain.RestaurantTable;
import com.tabila.backend.domain.enums.OrderStatus;
import com.tabila.backend.domain.enums.PaymentStatus;
import com.tabila.backend.repository.CategoryRepository;
import com.tabila.backend.repository.CustomerOrderRepository;
import com.tabila.backend.repository.MenuItemRepository;
import com.tabila.backend.repository.PaymentRepository;
import com.tabila.backend.repository.RestaurantRepository;
import com.tabila.backend.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class RestaurantMvpService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public RestaurantMvpService(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository,
            CustomerOrderRepository orderRepository,
            PaymentRepository paymentRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public MenuResponse getMenu(Long tableId) {
        Long safeTableId = Objects.requireNonNull(tableId, "tableId est requis");
        RestaurantTable table = tableRepository
                .findById(safeTableId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Table introuvable"));
        if (!table.isActive()) {
            throw new ResponseStatusException(BAD_REQUEST, "Cette table est inactive");
        }

        Long restaurantId = table.getRestaurant().getId();
        List<Category> categories = categoryRepository.findAllByRestaurantIdOrderByDisplayOrderAscIdAsc(restaurantId);
        List<MenuItem> items = menuItemRepository.findAllByRestaurantIdAndAvailableTrueOrderByIdAsc(restaurantId);

        Map<Long, List<MenuItemResponse>> itemsByCategory = items
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getId(),
                        Collectors.mapping(this::toMenuItemResponse, Collectors.toList())));

        List<MenuCategoryResponse> payload = categories
                .stream()
                .map(category -> new MenuCategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDisplayOrder(),
                        itemsByCategory.getOrDefault(category.getId(), List.of())))
                .toList();

        Restaurant restaurant = table.getRestaurant();
        return new MenuResponse(
                table.getId(),
                table.getName(),
                restaurant.getName(),
                restaurant.getWelcomeMessage(),
                restaurant.getBackgroundColor(),
                restaurant.getAccentColor(),
                payload);
    }

    public RestaurantConfigResponse getRestaurantConfig(Long restaurantId) {
        Restaurant restaurant = findRestaurant(restaurantId);
        return toRestaurantConfigResponse(restaurant);
    }

    public RestaurantConfigResponse updateRestaurantConfig(Long restaurantId, RestaurantConfigRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        restaurant.setName(request.name().trim());
        restaurant.setWelcomeMessage(request.welcomeMessage().trim());
        restaurant.setBackgroundColor(request.backgroundColor().trim());
        restaurant.setAccentColor(request.accentColor().trim());
        return toRestaurantConfigResponse(restaurantRepository.save(restaurant));
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Long safeTableId = Objects.requireNonNull(request.tableId(), "tableId est requis");
        RestaurantTable table = tableRepository
                .findById(safeTableId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Table introuvable"));
        if (!table.isActive()) {
            throw new ResponseStatusException(BAD_REQUEST, "Cette table est inactive");
        }

        CustomerOrder order = new CustomerOrder();
        order.setTable(table);
        order.setRestaurant(table.getRestaurant());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var requestedItem : request.items()) {
            Long safeMenuItemId = Objects.requireNonNull(requestedItem.menuItemId(), "menuItemId est requis");
            MenuItem menuItem = menuItemRepository
                    .findById(safeMenuItemId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Article menu introuvable"));

            if (!menuItem.isAvailable()) {
                throw new ResponseStatusException(BAD_REQUEST, "Un article n'est pas disponible");
            }

            if (!menuItem.getRestaurant().getId().equals(table.getRestaurant().getId())) {
                throw new ResponseStatusException(BAD_REQUEST, "Article non valide pour cette table");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(requestedItem.quantity());
            orderItem.setUnitPrice(menuItem.getPrice());
            BigDecimal lineTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(requestedItem.quantity()));
            orderItem.setLineTotal(lineTotal);
            total = total.add(lineTotal);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        CustomerOrder saved = orderRepository.save(order);
        return toOrderResponse(saved);
    }

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Long safeOrderId = Objects.requireNonNull(request.orderId(), "orderId est requis");
        CustomerOrder order = orderRepository
                .findById(safeOrderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Commande introuvable"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setProvider(request.provider());
        payment.setPhoneNumber(request.phoneNumber());
        payment.setTransactionRef("TX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));

        boolean success = request.phoneNumber().replaceAll("\\D", "").length() >= 8;
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        if (success) {
            order.setStatus(OrderStatus.PAID);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
        }

        Payment saved = paymentRepository.save(payment);
        return toPaymentResponse(saved);
    }

    public List<TableResponse> listTables(Long restaurantId) {
        return tableRepository
                .findAllByRestaurantIdOrderByIdAsc(restaurantId)
                .stream()
                .map(this::toTableResponse)
                .toList();
    }

    public TableResponse createTable(Long restaurantId, TableRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        RestaurantTable table = new RestaurantTable();
        table.setName(request.name().trim());
        table.setActive(request.active() == null || request.active());
        table.setRestaurant(restaurant);
        table.setQrCodeUrl("pending");
        table = tableRepository.save(table);
        table.setQrCodeUrl("/menu?tableId=" + table.getId());
        table = tableRepository.save(table);
        return toTableResponse(table);
    }

    public TableResponse updateTable(Long restaurantId, Long tableId, TableRequest request) {
        RestaurantTable table = findTableOfRestaurant(restaurantId, tableId);
        table.setName(request.name().trim());
        if (request.active() != null) {
            table.setActive(request.active());
        }
        return toTableResponse(tableRepository.save(table));
    }

    public void deleteTable(Long restaurantId, Long tableId) {
        RestaurantTable table = findTableOfRestaurant(restaurantId, tableId);
        tableRepository.delete(Objects.requireNonNull(table));
    }

    public List<CategoryResponse> listCategories(Long restaurantId) {
        return categoryRepository
                .findAllByRestaurantIdOrderByDisplayOrderAscIdAsc(restaurantId)
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    public CategoryResponse createCategory(Long restaurantId, CategoryRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        Category category = new Category();
        category.setName(request.name().trim());
        category.setDisplayOrder(request.displayOrder());
        category.setRestaurant(restaurant);
        return toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long restaurantId, Long categoryId, CategoryRequest request) {
        Category category = findCategoryOfRestaurant(restaurantId, categoryId);
        category.setName(request.name().trim());
        category.setDisplayOrder(request.displayOrder());
        return toCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long restaurantId, Long categoryId) {
        Category category = findCategoryOfRestaurant(restaurantId, categoryId);
        categoryRepository.delete(Objects.requireNonNull(category));
    }

    public List<MenuItemResponse> listMenuItems(Long restaurantId) {
        return menuItemRepository
                .findAllByRestaurantIdOrderByIdAsc(restaurantId)
                .stream()
                .map(this::toMenuItemResponse)
                .toList();
    }

    public MenuItemResponse createMenuItem(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        Category category = findCategoryOfRestaurant(restaurantId, request.categoryId());

        MenuItem item = new MenuItem();
        item.setName(request.name().trim());
        item.setDescription(request.description().trim());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        item.setAvailable(request.available() == null || request.available());
        item.setCategory(category);
        item.setRestaurant(restaurant);

        return toMenuItemResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse updateMenuItem(Long restaurantId, Long itemId, MenuItemRequest request) {
        MenuItem item = findMenuItemOfRestaurant(restaurantId, itemId);
        Category category = findCategoryOfRestaurant(restaurantId, request.categoryId());

        item.setName(request.name().trim());
        item.setDescription(request.description().trim());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        if (request.available() != null) {
            item.setAvailable(request.available());
        }
        item.setCategory(category);

        return toMenuItemResponse(menuItemRepository.save(item));
    }

    public void deleteMenuItem(Long restaurantId, Long itemId) {
        MenuItem item = findMenuItemOfRestaurant(restaurantId, itemId);
        menuItemRepository.delete(Objects.requireNonNull(item));
    }

    public List<OrderResponse> listOrders(Long restaurantId) {
        return orderRepository
                .findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status) {
        CustomerOrder order = findOrderOfRestaurant(restaurantId, orderId);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        return toOrderResponse(orderRepository.save(order));
    }

    public PaymentsDashboardResponse getPaymentsDashboard(Long restaurantId) {
        List<PaymentResponse> payments = paymentRepository
                .findAllByOrderRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .map(this::toPaymentResponse)
                .toList();

        BigDecimal revenue = paymentRepository.sumAmountByRestaurantIdAndStatus(restaurantId, PaymentStatus.SUCCESS);
        return new PaymentsDashboardResponse(revenue, payments);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        Long safeRestaurantId = Objects.requireNonNull(restaurantId, "restaurantId est requis");
        return restaurantRepository
                .findById(safeRestaurantId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Restaurant introuvable"));
    }

    private RestaurantTable findTableOfRestaurant(Long restaurantId, Long tableId) {
        Long safeTableId = Objects.requireNonNull(tableId, "tableId est requis");
        RestaurantTable table = tableRepository
                .findById(safeTableId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Table introuvable"));
        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Table hors restaurant");
        }
        return table;
    }

    private Category findCategoryOfRestaurant(Long restaurantId, Long categoryId) {
        Long safeCategoryId = Objects.requireNonNull(categoryId, "categoryId est requis");
        Category category = categoryRepository
                .findById(safeCategoryId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categorie introuvable"));
        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Categorie hors restaurant");
        }
        return category;
    }

    private MenuItem findMenuItemOfRestaurant(Long restaurantId, Long menuItemId) {
        Long safeMenuItemId = Objects.requireNonNull(menuItemId, "menuItemId est requis");
        MenuItem item = menuItemRepository
                .findById(safeMenuItemId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Menu item introuvable"));
        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Menu item hors restaurant");
        }
        return item;
    }

    private CustomerOrder findOrderOfRestaurant(Long restaurantId, Long orderId) {
        Long safeOrderId = Objects.requireNonNull(orderId, "orderId est requis");
        CustomerOrder order = orderRepository
                .findById(safeOrderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Commande introuvable"));
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Commande hors restaurant");
        }
        return order;
    }

    private TableResponse toTableResponse(RestaurantTable table) {
        return new TableResponse(table.getId(), table.getName(), table.getQrCodeUrl(), table.isActive());
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDisplayOrder());
    }

    private MenuItemResponse toMenuItemResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.isAvailable(),
                item.getCategory().getId(),
                item.getCategory().getName());
    }

    private OrderResponse toOrderResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order
                .getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTable().getId(),
                order.getTable().getName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getPhoneNumber(),
                payment.getTransactionRef(),
                payment.getCreatedAt());
    }

    private RestaurantConfigResponse toRestaurantConfigResponse(Restaurant restaurant) {
        return new RestaurantConfigResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getWelcomeMessage(),
                restaurant.getBackgroundColor(),
                restaurant.getAccentColor(),
                restaurant.getCurrency());
    }
}
