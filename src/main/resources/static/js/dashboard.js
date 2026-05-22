document.addEventListener("DOMContentLoaded", () => {
    const actionButtons = document.querySelectorAll("[data-action]");

    actionButtons.forEach((button) => {
        button.addEventListener("click", () => {
            window.showUiToast?.(`Flujo "${button.dataset.action}" listo para integrar backend.`);
        });
    });
});
