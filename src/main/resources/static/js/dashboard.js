document.addEventListener("DOMContentLoaded", () => {
    const actionButtons = document.querySelectorAll("[data-action]");

    actionButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const targetRoute = button.dataset.route;
            if (targetRoute) {
                window.location.href = targetRoute;
                return;
            }
            window.showUiToast?.(`Flujo "${button.dataset.action}" listo para integrar backend.`);
        });
    });
});
