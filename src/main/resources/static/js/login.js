document.addEventListener("DOMContentLoaded", () => {
    const passwordInput = document.getElementById("password");
    const togglePassword = document.getElementById("togglePassword");

    const forgotModal = document.getElementById("forgotModal");
    const openForgotModalButton = document.getElementById("openForgotModal");
    const closeForgotModalButton = document.getElementById("closeForgotModal");

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    togglePassword?.addEventListener("click", () => {
        if (!passwordInput) return;
        passwordInput.type = passwordInput.type === "password" ? "text" : "password";
    });

    openForgotModalButton?.addEventListener("click", () => openModal(forgotModal));
    closeForgotModalButton?.addEventListener("click", () => closeModal(forgotModal));

    forgotModal?.addEventListener("click", (event) => {
        if (event.target === forgotModal) {
            closeModal(forgotModal);
        }
    });
});
