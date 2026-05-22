document.addEventListener("DOMContentLoaded", () => {
    const applyFilter = document.getElementById("apply-filter");
    const pageButtons = document.querySelectorAll(".page-btn");

    if (applyFilter) {
        applyFilter.addEventListener("click", () => {
            window.showUiToast?.("Filtro aplicado (modo visual, sin backend).");
        });
    }

    pageButtons.forEach((button) => {
        button.addEventListener("click", () => {
            if (button.dataset.page === "prev" || button.dataset.page === "next") {
                window.showUiToast?.("Paginación visual habilitada.");
                return;
            }

            pageButtons.forEach((node) => node.classList.remove("active"));
            button.classList.add("active");
            window.showUiToast?.(`Página ${button.dataset.page} seleccionada.`);
        });
    });
});
