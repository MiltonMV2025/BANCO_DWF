document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("register-form");
    const passwordInput = document.getElementById("password");
    const confirmInput = document.getElementById("confirmarPassword");
    const togglePassword = document.getElementById("togglePassword");
    const toggleConfirmPassword = document.getElementById("toggleConfirmPassword");

    const toggleVisibility = (input) => {
        if (!input) return;
        input.type = input.type === "password" ? "text" : "password";
    };

    togglePassword?.addEventListener("click", () => toggleVisibility(passwordInput));
    toggleConfirmPassword?.addEventListener("click", () => toggleVisibility(confirmInput));

    registerForm?.addEventListener("submit", (event) => {
        if (!passwordInput || !confirmInput) return;
        if (passwordInput.value.trim() !== confirmInput.value.trim()) {
            event.preventDefault();
            window.alert("La confirmación de contraseña no coincide.");
        }
    });
});
