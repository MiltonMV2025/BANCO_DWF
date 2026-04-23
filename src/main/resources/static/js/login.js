document.addEventListener("DOMContentLoaded", () => {
    const passwordInput = document.getElementById("password");
    const togglePassword = document.getElementById("togglePassword");
    const modal = document.getElementById("forgotModal");
    const openModalButton = document.getElementById("openForgotModal");
    const closeModalButton = document.getElementById("closeForgotModal");

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener("click", () => {
            passwordInput.type = passwordInput.type === "password" ? "text" : "password";
        });
    }

    if (openModalButton && modal) {
        openModalButton.addEventListener("click", () => {
            modal.classList.add("show");
            modal.setAttribute("aria-hidden", "false");
        });
    }

    if (closeModalButton && modal) {
        closeModalButton.addEventListener("click", () => {
            modal.classList.remove("show");
            modal.setAttribute("aria-hidden", "true");
        });
    }

    if (modal) {
        modal.addEventListener("click", (event) => {
            if (event.target === modal) {
                modal.classList.remove("show");
                modal.setAttribute("aria-hidden", "true");
            }
        });
    }
});
