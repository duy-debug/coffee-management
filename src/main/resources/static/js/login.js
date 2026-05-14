document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.querySelector("[data-password-toggle]");
    const passwordInput = document.getElementById("matKhau");

    const bindAutoDismissAlerts = () => {
        document.querySelectorAll(".alert").forEach((alert) => {
            if (alert.dataset.autoDismissBound === "1") {
                return;
            }

            alert.dataset.autoDismissBound = "1";

            window.setTimeout(() => {
                if (!alert.isConnected) {
                    return;
                }

                alert.classList.add("alert--dismissed");
                window.setTimeout(() => {
                    if (alert.isConnected) {
                        alert.remove();
                    }
                }, 280);
            }, 3000);
        });
    };

    if (toggle && passwordInput) {
        toggle.addEventListener("click", () => {
            const isPassword = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", isPassword ? "text" : "password");
            toggle.classList.toggle("is-active", isPassword);
            toggle.setAttribute("aria-label", isPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
        });
    }

    bindAutoDismissAlerts();
});
