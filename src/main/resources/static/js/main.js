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
