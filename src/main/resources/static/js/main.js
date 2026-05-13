document.addEventListener("DOMContentLoaded", () => {
    const shell = document.querySelector(".app-shell");
    const overlay = document.querySelector(".sidebar-overlay");
    const openBtn = document.querySelector("[data-sidebar-open]");
    const closeSidebar = () => {
        shell?.classList.remove("sidebar-open");
        overlay?.classList.remove("is-open");
    };

    const openSidebar = () => {
        shell?.classList.add("sidebar-open");
        overlay?.classList.add("is-open");
    };

    openBtn?.addEventListener("click", openSidebar);
    overlay?.addEventListener("click", closeSidebar);

    document.querySelectorAll("[data-submenu-toggle]").forEach((button) => {
        button.addEventListener("click", () => {
            const group = button.closest(".nav-group");
            group?.classList.toggle("open");
        });
    });

    document.querySelectorAll("[data-image-input]").forEach((input) => {
        input.addEventListener("change", () => {
            const file = input.files && input.files[0];
            const preview = input.closest(".form-group")?.querySelector("[data-image-preview]");
            if (!preview) {
                return;
            }

            preview.innerHTML = "";
            if (!file) {
                preview.classList.add("image-preview--empty");
                preview.textContent = "Chưa có ảnh xem trước";
                return;
            }

            const img = document.createElement("img");
            img.alt = "Xem trước ảnh món";
            preview.classList.remove("image-preview--empty");
            preview.appendChild(img);

            const reader = new FileReader();
            reader.onload = (event) => {
                img.src = event.target?.result || "";
            };
            reader.readAsDataURL(file);
        });
    });

    const orderTypeInputs = document.querySelectorAll("[data-order-type]");
    const tableSection = document.querySelector("[data-table-section]");
    const toggleTableSection = () => {
        const selected = document.querySelector("[data-order-type]:checked");
        const isDineIn = selected && selected.value === "Dùng tại quán";
        if (!tableSection) {
            return;
        }
        tableSection.style.display = isDineIn ? "block" : "none";
    };

    orderTypeInputs.forEach((input) => input.addEventListener("change", toggleTableSection));
    toggleTableSection();

    const normalizePath = (path) => (path.length > 1 ? path.replace(/\/$/, "") : path);
    const currentPath = normalizePath(window.location.pathname);

    document.querySelectorAll("[data-route]").forEach((link) => {
        const route = normalizePath(link.getAttribute("data-route") || "");
        if (!route) {
            return;
        }

        if (currentPath === route || currentPath.startsWith(`${route}/`)) {
            link.classList.add("active");
            const group = link.closest(".nav-group");
            if (group) {
                group.classList.add("open");
            }
        }
    });
});
