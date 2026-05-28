document.addEventListener("DOMContentLoaded", () => {

    // — Aprobar
    document.querySelectorAll(".aprobar-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            const row    = btn.closest("tr");
            const id     = row?.dataset.id;
            const nombre = row?.dataset.nombre ?? "este empleado";

            document.getElementById("aprobar-text").textContent =
                `¿Deseas aprobar a "${nombre}" y activarlo en el sistema?`;

            const form = document.getElementById("aprobar-form");
            form.action = `/gerencia/general/acciones-personal/${id}/aprobar`;

            document.getElementById("aprobar-modal").classList.add("open");
        });
    });

    document.getElementById("aprobar-cancel")
        ?.addEventListener("click", () => {
            document.getElementById("aprobar-modal").classList.remove("open");
        });

    document.getElementById("aprobar-confirm")
        ?.addEventListener("click", () => {
            document.getElementById("aprobar-form").submit();
        });

    // — Rechazar
    document.querySelectorAll(".rechazar-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            const row    = btn.closest("tr");
            const id     = row?.dataset.id;
            const nombre = row?.dataset.nombre ?? "este empleado";

            document.getElementById("rechazar-text").textContent =
                `¿Deseas rechazar la solicitud de "${nombre}"?`;

            const form = document.getElementById("rechazar-form");
            form.action = `/gerencia/general/acciones-personal/${id}/rechazar`;

            document.getElementById("rechazar-modal").classList.add("open");
        });
    });

    document.getElementById("rechazar-cancel")
        ?.addEventListener("click", () => {
            document.getElementById("rechazar-modal").classList.remove("open");
        });

    document.getElementById("rechazar-confirm")
        ?.addEventListener("click", () => {
            document.getElementById("rechazar-form").submit();
        });

    // — Cerrar modales con clic fuera
    document.querySelectorAll(".modal-backdrop").forEach((backdrop) => {
        backdrop.addEventListener("click", (e) => {
            if (e.target === backdrop) backdrop.classList.remove("open");
        });
    });
});