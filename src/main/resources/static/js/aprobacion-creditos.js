document.addEventListener("DOMContentLoaded", () => {
    const approveLinks = document.querySelectorAll(".link-btn.approve");
    const rejectButton = document.getElementById("reject-credit");
    const approveButton = document.getElementById("approve-credit");
    const viewLinks = document.querySelectorAll(".link-btn:not(.approve)");

    viewLinks.forEach((button) => {
        button.addEventListener("click", () => {
            window.showUiToast?.("Detalle de solicitud en modo visual.");
        });
    });

    approveLinks.forEach((button) => {
        button.addEventListener("click", () => {
            window.showUiToast?.("Solicitud marcada para aprobación.");
        });
    });

    rejectButton?.addEventListener("click", () => {
        window.showUiToast?.("Solicitud rechazada en modo visual.");
    });

    approveButton?.addEventListener("click", () => {
        window.showUiToast?.("Solicitud aprobada en modo visual.");
    });
});
