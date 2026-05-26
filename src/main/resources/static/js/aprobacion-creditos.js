document.addEventListener("DOMContentLoaded", () => {
    const approveLinks = document.querySelectorAll(".link-btn.approve");
    const rejectButton = document.getElementById("reject-credit");
    const approveButton = document.getElementById("approve-credit");
    const viewLinks = document.querySelectorAll(".view-detail-btn");
    const decisionCard = document.getElementById("decision-card");

    const showDetailLoading = () => {
        decisionCard?.classList.add("loading");
    };

    viewLinks.forEach((link) => {
        link.addEventListener("click", (event) => {
            if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
            if (event.button !== 0) return;

            const href = link.getAttribute("href");
            if (!href) return;

            event.preventDefault();
            showDetailLoading();
            window.showUiToast?.("Cargando detalle de solicitud...");

            requestAnimationFrame(() => {
                window.location.assign(href);
            });
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
