document.addEventListener("DOMContentLoaded", () => {
    const newEmployeeBtn = document.getElementById("new-employee-btn");
    const employeeModal = document.getElementById("employee-modal");
    const deleteModal = document.getElementById("employee-delete-modal");
    const modalTitle = document.getElementById("employee-modal-title");
    const employeeIdInput = document.getElementById("employee-id");
    const nameInput = document.getElementById("employee-name");
    const roleInput = document.getElementById("employee-role");
    const branchInput = document.getElementById("employee-branch");
    const statusInput = document.getElementById("employee-status");
    const saveButton = document.getElementById("employee-save");
    const cancelButton = document.getElementById("employee-cancel");
    const deleteCancel = document.getElementById("employee-delete-cancel");
    const deleteConfirm = document.getElementById("employee-delete-confirm");
    const deleteText = document.getElementById("employee-delete-text");

    let rowToDelete = null;

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    const resetForm = () => {
        employeeIdInput.value = "";
        nameInput.value = "";
        roleInput.value = "";
        branchInput.value = "";
        statusInput.value = "Activo";
    };

    const fillFormFromRow = (row) => {
        employeeIdInput.value = row.dataset.id || "";
        nameInput.value = row.dataset.nombre || "";
        roleInput.value = row.dataset.rol || "";
        branchInput.value = row.dataset.sucursal || "";
        statusInput.value = row.dataset.estado || "Activo";
    };

    const upsertRow = () => {
        const id = employeeIdInput.value.trim();
        const name = nameInput.value.trim();
        const role = roleInput.value.trim();
        const branch = branchInput.value.trim();
        const status = statusInput.value;

        if (!name || !role || !branch) {
            window.showUiToast?.("Completa los campos requeridos del empleado.");
            return;
        }

        const tbody = document.querySelector(".table-wrap tbody");
        let row = id ? tbody.querySelector(`tr[data-id="${id}"]`) : null;

        if (!row) {
            row = document.createElement("tr");
            row.dataset.id = Date.now().toString();
            row.innerHTML = `
                <td></td>
                <td></td>
                <td></td>
                <td><span class="badge"></span></td>
                <td>
                    <button type="button" class="link-btn view-btn">Ver ficha</button>
                    <span class="action-divider">|</span>
                    <button type="button" class="link-btn edit-btn">Editar</button>
                    <span class="action-divider">|</span>
                    <button type="button" class="link-btn delete-btn">Eliminar</button>
                </td>
            `;
            tbody.prepend(row);
        }

        row.dataset.nombre = name;
        row.dataset.rol = role;
        row.dataset.sucursal = branch;
        row.dataset.estado = status;

        row.cells[0].textContent = name;
        row.cells[1].textContent = role;
        row.cells[2].textContent = branch;

        const badge = row.querySelector(".badge");
        badge.textContent = status;
        badge.className = `badge ${status === "Activo" ? "ok" : "warn"}`;

        bindRowActions(row);
        closeModal(employeeModal);
        window.showUiToast?.("Empleado guardado en modo visual.");
    };

    const bindRowActions = (row) => {
        if (row.dataset.actionsBound === "true") return;
        row.dataset.actionsBound = "true";

        row.querySelector(".view-btn")?.addEventListener("click", () => {
            window.showUiToast?.(`Ficha de ${row.dataset.nombre} en modo visual.`);
        });

        row.querySelector(".edit-btn")?.addEventListener("click", () => {
            modalTitle.textContent = "Editar Empleado";
            fillFormFromRow(row);
            openModal(employeeModal);
        });

        row.querySelector(".delete-btn")?.addEventListener("click", () => {
            rowToDelete = row;
            deleteText.textContent = `¿Eliminar a ${row.dataset.nombre}?`;
            openModal(deleteModal);
        });
    };

    document.querySelectorAll(".table-wrap tbody tr").forEach(bindRowActions);

    newEmployeeBtn?.addEventListener("click", () => {
        modalTitle.textContent = "Nuevo Empleado";
        resetForm();
        openModal(employeeModal);
    });

    saveButton?.addEventListener("click", upsertRow);
    cancelButton?.addEventListener("click", () => closeModal(employeeModal));
    deleteCancel?.addEventListener("click", () => closeModal(deleteModal));

    deleteConfirm?.addEventListener("click", () => {
        if (rowToDelete) {
            rowToDelete.remove();
            rowToDelete = null;
            window.showUiToast?.("Empleado eliminado en modo visual.");
        }
        closeModal(deleteModal);
    });

    [employeeModal, deleteModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
