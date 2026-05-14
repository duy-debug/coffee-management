document.addEventListener("DOMContentLoaded", () => {
    const shell = document.querySelector(".app-shell");
    const overlay = document.querySelector(".sidebar-overlay");
    const openBtn = document.querySelector("[data-sidebar-open]");
    const contentHost = document.querySelector("[data-page-content]");
    const topbarTitle = document.querySelector(".page-title");
    const pageSubtitle = document.querySelector(".page-subtitle");
    const sidebar = document.querySelector(".sidebar");

    const normalizePath = (path) => {
        if (!path) {
            return "/";
        }
        return path.length > 1 ? path.replace(/\/$/, "") : path;
    };

    const currentPath = () => normalizePath(window.location.pathname);

    const closeSidebar = () => {
        shell?.classList.remove("sidebar-open");
        overlay?.classList.remove("is-open");
    };

    const openSidebar = () => {
        shell?.classList.add("sidebar-open");
        overlay?.classList.add("is-open");
    };

    const setActiveSidebarLink = (activeLink) => {
        if (!sidebar || !activeLink) {
            return;
        }

        sidebar.querySelectorAll(".nav-link").forEach((link) => {
            link.classList.remove("active");
            link.removeAttribute("aria-current");
        });

        activeLink.classList.add("active");
        activeLink.setAttribute("aria-current", "page");

        const group = activeLink.closest(".nav-group");
        if (group) {
            group.classList.add("open");
        }
    };

    const findBestSidebarLink = (path) => {
        if (!sidebar) {
            return null;
        }

        const normalizedPath = normalizePath(path);
        let bestLink = null;
        let bestLength = -1;

        sidebar.querySelectorAll("[data-route]").forEach((link) => {
            const route = normalizePath(link.getAttribute("data-route") || "");
            if (!route) {
                return;
            }

            const matches = normalizedPath === route || normalizedPath.startsWith(`${route}/`);
            if (!matches) {
                return;
            }

            if (route.length > bestLength) {
                bestLength = route.length;
                bestLink = link;
            }
        });

        return bestLink;
    };

    const syncSidebarState = (path = currentPath()) => {
        if (!sidebar) {
            return;
        }

        const scrollTop = sidebar.scrollTop;

        const activeLink = findBestSidebarLink(path);
        setActiveSidebarLink(activeLink);

        sidebar.scrollTop = scrollTop;
    };

    const bindSubmenuToggles = () => {
        document.querySelectorAll("[data-submenu-toggle]").forEach((button) => {
            if (button.dataset.bound === "1") {
                return;
            }

            button.dataset.bound = "1";
            button.addEventListener("click", () => {
                const group = button.closest(".nav-group");
                group?.classList.toggle("open");
            });
        });
    };

    const bindImageInputs = (root = document) => {
        root.querySelectorAll("[data-image-input]").forEach((input) => {
            if (input.dataset.bound === "1") {
                return;
            }

            input.dataset.bound = "1";
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
    };

    const bindOrderTypeInputs = (root = document) => {
        const orderTypeInputs = root.querySelectorAll("[data-order-type]");
        const tableSection = root.querySelector("[data-table-section]");
        if (!orderTypeInputs.length || !tableSection) {
            return;
        }

        const toggleTableSection = () => {
            const selected = root.querySelector("[data-order-type]:checked");
            const isDineIn = selected && selected.value === "Dùng tại quán";
            tableSection.style.display = isDineIn ? "block" : "none";
        };

        orderTypeInputs.forEach((input) => {
            if (input.dataset.bound === "1") {
                return;
            }
            input.dataset.bound = "1";
            input.addEventListener("change", toggleTableSection);
        });

        toggleTableSection();
    };

    const applyActiveStateFromDocument = (doc) => {
        const nextTitle = doc.querySelector(".page-title")?.textContent?.trim();
        const nextSubtitle = doc.querySelector(".page-subtitle")?.textContent?.trim();
        if (topbarTitle && nextTitle) {
            topbarTitle.textContent = nextTitle;
        }
        if (pageSubtitle && nextSubtitle) {
            pageSubtitle.textContent = nextSubtitle;
        }

        const title = doc.querySelector("title")?.textContent?.trim();
        if (title) {
            document.title = title;
        }
    };

    const replaceMainContent = (doc, path) => {
        if (!contentHost) {
            window.location.assign(window.location.href);
            return;
        }

        const nextContent = doc.querySelector("[data-page-content]");
        const nextInner = nextContent?.innerHTML;
        if (!nextInner) {
            window.location.assign(window.location.href);
            return;
        }

        contentHost.innerHTML = nextInner;
        applyActiveStateFromDocument(doc);
        syncSidebarState(normalizePath(path || window.location.pathname));
        bindImageInputs(contentHost);
        bindOrderTypeInputs(contentHost);
        window.scrollTo(0, 0);
    };

    const loadPage = async (url, pushHistory = true) => {
        const nextUrl = url instanceof URL ? url : new URL(url, window.location.origin);
        const nextPath = nextUrl.pathname + nextUrl.search;

        try {
            const response = await fetch(nextPath, {
                credentials: "same-origin",
                headers: {
                    "X-Requested-With": "fetch"
                }
            });

            if (!response.ok) {
                window.location.assign(nextPath);
                return;
            }

            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");
            replaceMainContent(doc, nextPath);
            if (pushHistory) {
                history.pushState({ url: nextPath }, "", nextPath);
            }
            closeSidebar();
        } catch (error) {
            window.location.assign(nextPath);
        }
    };

    openBtn?.addEventListener("click", openSidebar);
    overlay?.addEventListener("click", closeSidebar);

    document.querySelectorAll("[data-route]").forEach((link) => {
        if (link.dataset.partialBound === "1") {
            return;
        }

        link.dataset.partialBound = "1";
        link.addEventListener("click", (event) => {
            if (event.defaultPrevented) {
                return;
            }

            if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey || event.button !== 0) {
                return;
            }

            const href = link.getAttribute("href");
            if (!href || href.startsWith("http")) {
                return;
            }

            const url = new URL(href, window.location.origin);
            if (url.origin !== window.location.origin) {
                return;
            }

            event.preventDefault();
            setActiveSidebarLink(link);
            loadPage(url.pathname + url.search);
        });
    });

    document.addEventListener("click", (event) => {
        if (!shell?.classList.contains("sidebar-open")) {
            return;
        }

        const target = event.target;
        if (!(target instanceof Element)) {
            return;
        }

        if (!target.closest(".sidebar")) {
            closeSidebar();
        }
    });

    window.addEventListener("popstate", () => {
        loadPage(window.location.pathname + window.location.search, false);
    });

    bindSubmenuToggles();
    bindImageInputs();
    bindOrderTypeInputs();
    syncSidebarState();
});
