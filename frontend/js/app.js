"use strict";

const API_BASE = "http://localhost:8080/api";

const $ = (selector) => document.querySelector(selector);

let products = [];
let sales = JSON.parse(localStorage.getItem("sales") || "[]");

/* -------------------- HELPERS -------------------- */

function money(value) {
    return Number(value || 0).toLocaleString("en-IN", {
        style: "currency",
        currency: "INR"
    });
}

function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, (character) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[character]));
}

function showMessage(selector, message, isError = false) {
    const element = $(selector);
    if (!element) return;

    element.textContent = message;
    element.style.color = isError ? "#dc2626" : "#15803d";
}

function saveSales() {
    localStorage.setItem("sales", JSON.stringify(sales));
}

async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Request failed: ${response.status}`);
    }

    if (response.status === 204) return null;

    const responseText = await response.text();
    return responseText ? JSON.parse(responseText) : null;
}

function normalizeProduct(product) {
    return {
        ...product,
        id: Number(product.id),
        price: Number(product.price),
        quantity: Number(product.quantity),
        minimum: Number(product.minimumStock ?? product.minimum ?? 0)
    };
}

function productPayload(product) {
    return {
        name: product.name,
        category: product.category,
        price: Number(product.price),
        quantity: Number(product.quantity),
        minimumStock: Number(product.minimum)
    };
}

function getStockStatus(product) {
    if (product.quantity <= 0) return "Out of stock";
    if (product.quantity <= product.minimum) return "Low stock";
    return "In stock";
}

function getStatusClass(product) {
    if (product.quantity <= 0) return "out";
    if (product.quantity <= product.minimum) return "low";
    return "in";
}

/* -------------------- PRODUCTS / BACKEND -------------------- */

async function loadProducts() {
    try {
        const data = await apiRequest("/products");
        products = (Array.isArray(data) ? data : []).map(normalizeProduct);
        renderAll();
    } catch (error) {
        console.error("Could not load products:", error);
        alert("Could not load products. Make sure Spring Boot is running on port 8080.");
    }
}

async function createProduct(product) {
    return apiRequest("/products", {
        method: "POST",
        body: JSON.stringify(productPayload(product))
    });
}

async function updateProduct(product) {
    return apiRequest(`/products/${product.id}`, {
        method: "PUT",
        body: JSON.stringify(productPayload(product))
    });
}

async function removeProduct(id) {
    return apiRequest(`/products/${id}`, {
        method: "DELETE"
    });
}

/* -------------------- DASHBOARD -------------------- */

async function renderDashboardSummary() {
    try {
        const summary = await apiRequest("/dashboard/summary");

        if ($("#total-products")) {
            $("#total-products").textContent = summary.totalProducts ?? products.length;
        }

        if ($("#low-stock")) {
            $("#low-stock").textContent = summary.lowStockProducts ?? 0;
        }

        if ($("#total-sales")) {
            $("#total-sales").textContent = money(summary.totalSales ?? 0);
        }
    } catch (error) {
        console.error("Could not load dashboard summary:", error);

        if ($("#total-products")) {
            $("#total-products").textContent = products.length;
        }

        if ($("#low-stock")) {
            $("#low-stock").textContent = products.filter(
                (product) => product.quantity <= product.minimum
            ).length;
        }
    }

    if ($("#total-units")) {
        $("#total-units").textContent = products.reduce(
            (total, product) => total + product.quantity,
            0
        );
    }
}

function renderDashboardTable() {
    const table = $("#dashboard-table");
    if (!table) return;

    if (products.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="5">No products added yet.</td>
            </tr>
        `;
        return;
    }

    table.innerHTML = products.map((product) => `
        <tr>
            <td>${escapeHTML(product.name)}</td>
            <td>${escapeHTML(product.category)}</td>
            <td>${money(product.price)}</td>
            <td>${product.quantity}</td>
            <td>
                <span class="stock-status ${getStatusClass(product)}">
                    ${getStockStatus(product)}
                </span>
            </td>
        </tr>
    `).join("");
}

/* -------------------- STOCK PAGE -------------------- */

function renderStockTable() {
    const table = $("#stock-table");
    if (!table) return;

    const searchText = ($("#search")?.value || "").trim().toLowerCase();

    const filteredProducts = products.filter((product) =>
        `${product.name} ${product.category}`.toLowerCase().includes(searchText)
    );

    if (filteredProducts.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="7">No products found.</td>
            </tr>
        `;
        return;
    }

    table.innerHTML = filteredProducts.map((product) => `
        <tr>
            <td>${escapeHTML(product.name)}</td>
            <td>${escapeHTML(product.category)}</td>
            <td>${money(product.price)}</td>
            <td>${product.quantity}</td>
            <td>${product.minimum}</td>
            <td>
                <span class="stock-status ${getStatusClass(product)}">
                    ${getStockStatus(product)}
                </span>
            </td>
            <td>
                <button type="button" data-action="add" data-id="${product.id}">+1</button>
                <button type="button" data-action="delete" data-id="${product.id}">Delete</button>
            </td>
        </tr>
    `).join("");
}

async function handleProductSubmit(event) {
    event.preventDefault();

    const name = $("#product-name").value.trim();
    const category = $("#product-category").value.trim();
    const price = Number($("#product-price").value);
    const quantity = Number($("#product-quantity").value);
    const minimum = Number($("#product-minimum").value);

    if (!name || !category || price <= 0 || quantity < 1 || minimum < 0) {
        showMessage("#product-message", "Enter valid product details.", true);
        return;
    }

    try {
        const existingProduct = products.find((product) =>
            product.name.toLowerCase() === name.toLowerCase() &&
            product.category.toLowerCase() === category.toLowerCase()
        );

        if (existingProduct) {
            existingProduct.quantity += quantity;
            existingProduct.price = price;
            existingProduct.minimum = minimum;

            await updateProduct(existingProduct);
            showMessage("#product-message", "Product already existed. Stock quantity updated.");
        } else {
            await createProduct({ name, category, price, quantity, minimum });
            showMessage("#product-message", "Product added successfully.");
        }

        $("#product-form").reset();
        $("#product-minimum").value = "5";

        await loadProducts();
    } catch (error) {
        console.error("Could not save product:", error);
        showMessage("#product-message", "Could not save product. Check Spring Boot and the backend.", true);
    }
}

async function handleStockTableClick(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    const id = Number(button.dataset.id);
    const product = products.find((item) => item.id === id);
    if (!product) return;

    try {
        if (button.dataset.action === "add") {
            product.quantity += 1;
            await updateProduct(product);
        }

        if (button.dataset.action === "delete") {
            const confirmed = confirm(`Delete "${product.name}"?`);
            if (!confirmed) return;

            await removeProduct(id);
        }

        await loadProducts();
    } catch (error) {
        console.error("Could not update product:", error);
        alert("Could not update the product. Check Spring Boot and the backend.");
    }
}

/* -------------------- SALES -------------------- */

function renderSaleDropdown() {
    const dropdown = $("#sale-product");
    if (!dropdown) return;

    const previousValue = dropdown.value;

    dropdown.innerHTML = `
        <option value="">Select a product</option>
        ${products.map((product) => `
            <option
                value="${product.id}"
                ${product.quantity <= 0 ? "disabled" : ""}
            >
                ${escapeHTML(product.name)} — ${money(product.price)}
                (${product.quantity} available)
            </option>
        `).join("")}
    `;

    if (products.some((product) => String(product.id) === previousValue)) {
        dropdown.value = previousValue;
    }
}

function updateSaleTotal() {
    const productId = Number($("#sale-product")?.value);
    const quantity = Number($("#sale-quantity")?.value);
    const product = products.find((item) => item.id === productId);

    if ($("#sale-total")) {
        $("#sale-total").textContent =
            product && quantity > 0 ? money(product.price * quantity) : money(0);
    }
}

function renderSalesTable() {
    const table = $("#sales-table");
    if (!table) return;

    if (sales.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="6">No sales recorded yet.</td>
            </tr>
        `;
        return;
    }

    table.innerHTML = [...sales].reverse().map((sale) => `
        <tr>
            <td>${escapeHTML(sale.date)}</td>
            <td>${escapeHTML(sale.productName)}</td>
            <td>${sale.quantity}</td>
            <td>${money(sale.unitPrice)}</td>
            <td>${money(sale.total)}</td>
            <td>${escapeHTML(sale.customer || "Walk-in customer")}</td>
        </tr>
    `).join("");
}

async function handleSaleSubmit(event) {
    event.preventDefault();

    const productId = Number($("#sale-product").value);
    const quantity = Number($("#sale-quantity").value);
    const customer = $("#customer").value.trim() || "Walk-in customer";
    const product = products.find((item) => item.id === productId);

    if (!product || quantity < 1) {
        showMessage("#sale-message", "Select a product and enter a valid quantity.", true);
        return;
    }

    if (quantity > product.quantity) {
        showMessage("#sale-message", "Not enough stock available.", true);
        return;
    }

    try {
        const unitPrice = product.price;
        const total = unitPrice * quantity;

        product.quantity -= quantity;
        await updateProduct(product);

        sales.push({
            date: new Date().toLocaleDateString("en-IN"),
            productId: product.id,
            productName: product.name,
            quantity,
            unitPrice,
            total,
            customer
        });

        saveSales();

        $("#sale-form").reset();
        updateSaleTotal();
        showMessage("#sale-message", "Sale recorded and stock updated.");

        await loadProducts();
    } catch (error) {
        console.error("Could not record sale:", error);
        showMessage("#sale-message", "Could not record sale. Check Spring Boot and the backend.", true);
    }
}

/* -------------------- NAVIGATION -------------------- */

function showPage(pageName) {
    document.querySelectorAll(".page").forEach((page) => {
        const isSelected = page.id === pageName;
        page.classList.toggle("hidden", !isSelected);
        page.classList.toggle("active", isSelected);
    });

    document.querySelectorAll(".nav-btn").forEach((button) => {
        button.classList.toggle("active", button.dataset.page === pageName);
    });
}

/* -------------------- RENDER ALL -------------------- */

function renderAll() {
    renderDashboardTable();
    renderStockTable();
    renderSaleDropdown();
    renderSalesTable();
    renderDashboardSummary();
}

/* -------------------- STARTUP / EVENTS -------------------- */

document.addEventListener("DOMContentLoaded", async () => {
    if ($("#today-label")) {
        $("#today-label").textContent = new Date().toLocaleDateString("en-IN", {
            weekday: "short",
            day: "numeric",
            month: "short",
            year: "numeric"
        });
    }

    $("#product-form")?.addEventListener("submit", handleProductSubmit);
    $("#stock-table")?.addEventListener("click", handleStockTableClick);
    $("#search")?.addEventListener("input", renderStockTable);

    $("#sale-form")?.addEventListener("submit", handleSaleSubmit);
    $("#sale-product")?.addEventListener("change", updateSaleTotal);
    $("#sale-quantity")?.addEventListener("input", updateSaleTotal);

    document.querySelectorAll(".nav-btn").forEach((button) => {
        button.addEventListener("click", () => {
            showPage(button.dataset.page);
        });
    });

    document.querySelectorAll("[data-go-page]").forEach((button) => {
        button.addEventListener("click", () => {
            showPage(button.dataset.goPage);
        });
    });

    $("#dashboard-add-product")?.addEventListener("click", () => {
        showPage("stock");
        $("#product-name")?.focus();
    });

    await loadProducts();
    showPage("dashboard");
});