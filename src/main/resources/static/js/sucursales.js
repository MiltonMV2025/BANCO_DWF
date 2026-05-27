document.addEventListener("DOMContentLoaded", () => {

    // — Abrir modal nueva sucursal
    document.getElementById("new-branch-btn")
        ?.addEventListener("click", () => {
            document.getElementById("branch-modal").classList.add("open");
        });

    // — Cancelar modal nueva sucursal
    document.getElementById("branch-cancel")
        ?.addEventListener("click", () => {
            document.getElementById("branch-modal").classList.remove("open");
        });

    // — Guardar nueva sucursal (submit del form)
    document.getElementById("branch-save")
        ?.addEventListener("click", () => {
            document.getElementById("branch-form").submit();
        });

    // — Botones inactivar
    document.querySelectorAll(".delete-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            const row   = btn.closest("tr");
            const id    = row?.dataset.id;
            const nombre = row?.dataset.nombre ?? "esta sucursal";

            document.getElementById("branch-delete-text").textContent =
                `¿Deseas inactivar la sucursal "${nombre}"?`;

            const form = document.getElementById("branch-delete-form");
            form.action = `/gerencia/general/sucursales/${id}/inactivar`;

            document.getElementById("branch-delete-modal").classList.add("open");
        });
    });

    // — Cancelar inactivar
    document.getElementById("branch-delete-cancel")
        ?.addEventListener("click", () => {
            document.getElementById("branch-delete-modal").classList.remove("open");
        });

    // — Confirmar inactivar
    document.getElementById("branch-delete-confirm")
        ?.addEventListener("click", () => {
            document.getElementById("branch-delete-form").submit();
        });

    // — Cerrar modales con clic fuera
    document.querySelectorAll(".modal-backdrop").forEach((backdrop) => {
        backdrop.addEventListener("click", (e) => {
            if (e.target === backdrop) backdrop.classList.remove("open");
        });
    });
});