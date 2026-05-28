document.addEventListener("DOMContentLoaded", () => {

    document.querySelectorAll(".asignar-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            const row    = btn.closest("tr");
            const id     = row?.dataset.id;
            const nombre = row?.dataset.nombre ?? "este gerente";

            document.getElementById("asignar-text").textContent =
                `¿Deseas asignar a "${nombre}" como gerente de esta sucursal?`;

            // Apunta el form al endpoint correcto con el id de sucursal en la URL
            const sucursalId = window.location.pathname.split("/")[4];
            const form = document.getElementById("asignar-form");
            form.action = `/gerencia/general/sucursales/${sucursalId}/asignar-gerente`;

            document.getElementById("asignar-id-empleado").value = id;

            document.getElementById("asignar-modal").classList.add("open");
        });
    });

    document.getElementById("asignar-cancel")
        ?.addEventListener("click", () => {
            document.getElementById("asignar-modal").classList.remove("open");
        });

    document.getElementById("asignar-confirm")
        ?.addEventListener("click", () => {
            document.getElementById("asignar-form").submit();
        });

    // — Cerrar modal con clic fuera
    document.querySelectorAll(".modal-backdrop").forEach((backdrop) => {
        backdrop.addEventListener("click", (e) => {
            if (e.target === backdrop) backdrop.classList.remove("open");
        });
    });
});