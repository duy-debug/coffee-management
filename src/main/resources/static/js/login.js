document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.querySelector("[data-password-toggle]");
    const passwordInput = document.getElementById("matKhau");

    if (toggle && passwordInput) {
        toggle.addEventListener("click", () => {
            const isPassword = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", isPassword ? "text" : "password");
            toggle.classList.toggle("is-active", isPassword);
            toggle.setAttribute("aria-label", isPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu");
        });
    }
});
