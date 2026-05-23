const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

function headers() {
    const values = {"Content-Type": "application/x-www-form-urlencoded"};
    if (csrfToken && csrfHeader) values[csrfHeader] = csrfToken;
    return values;
}

function toast(message) {
    let node = document.querySelector(".toast");
    if (!node) {
        node = document.createElement("div");
        node.className = "toast";
        document.body.appendChild(node);
    }
    node.textContent = message;
    node.classList.add("show");
    setTimeout(() => node.classList.remove("show"), 2600);
}

document.addEventListener("click", async (event) => {
    const add = event.target.closest("[data-add-cart]");
    if (add) {
        event.preventDefault();
        const id = add.dataset.addCart;
        const quantity = document.querySelector("[data-quantity]")?.value || "1";
        const response = await fetch(`/api/cart/add/${id}`, {method: "POST", headers: headers(), body: `quantity=${quantity}`});
        if (!response.ok) {
            toast("Connexion requise ou panier indisponible");
            return;
        }
        const cart = await response.json();
        document.querySelectorAll("[data-cart-count]").forEach((node) => node.textContent = cart.itemCount);
        add.textContent = `Ajoute (${cart.itemCount})`;
        add.classList.add("added");
        toast("Produit ajoute au panier");
    }

    const theme = event.target.closest("[data-theme-toggle]");
    if (theme) {
        const next = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
        document.documentElement.dataset.theme = next;
        localStorage.setItem("prime-theme", next);
    }
});

document.addEventListener("change", async (event) => {
    const qty = event.target.closest("[data-cart-qty]");
    if (qty) {
        const response = await fetch(`/api/cart/update/${qty.dataset.cartQty}`, {
            method: "POST",
            headers: headers(),
            body: `quantity=${encodeURIComponent(qty.value)}`
        });
        if (response.ok) location.reload();
    }
});

document.addEventListener("submit", async (event) => {
    const coupon = event.target.closest("[data-coupon-form]");
    if (coupon) {
        event.preventDefault();
        const value = coupon.querySelector("input").value;
        const response = await fetch("/api/cart/coupon", {method: "POST", headers: headers(), body: `coupon=${encodeURIComponent(value)}`});
        if (response.ok) location.reload();
    }
});

document.querySelectorAll("[data-gallery-thumb]").forEach((thumb) => {
    thumb.addEventListener("click", () => {
        document.querySelector("[data-gallery-main]").src = thumb.src;
    });
});

const searchInput = document.querySelector("[data-search]");
if (searchInput) {
    const suggestions = document.querySelector("[data-suggestions]");
    searchInput.addEventListener("input", async () => {
        const q = searchInput.value.trim();
        if (q.length < 2) {
            suggestions.style.display = "none";
            return;
        }
        const response = await fetch(`/api/search?q=${encodeURIComponent(q)}`);
        const data = await response.json();
        suggestions.innerHTML = data.content.map((item) =>
            `<a href="/products/${item.id}"><span>${item.name}</span><strong>${item.price} FCFA</strong></a>`
        ).join("");
        suggestions.style.display = data.content.length ? "block" : "none";
    });
}

document.documentElement.dataset.theme = localStorage.getItem("prime-theme") || "light";
