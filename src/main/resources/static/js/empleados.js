document.addEventListener("DOMContentLoaded", () => {
    const newEmployeeBtn = document.getElementById("new-employee-btn");
    const employeeModal = document.getElementById("employee-modal");
    const deleteModal = document.getElementById("employee-delete-modal");
    const modalTitle = document.getElementById("employee-modal-title");
    const employeeForm = document.getElementById("employee-form");
    const nameInput = document.getElementById("employee-name");
    const roleInput = document.getElementById("employee-role");
    const statusInput = document.getElementById("employee-status");
    const saveButton = document.getElementById("employee-save");
    const cancelButton = document.getElementById("employee-cancel");
    const deleteCancel = document.getElementById("employee-delete-cancel");
    const deleteConfirm = document.getElementById("employee-delete-confirm");
    const deleteText = document.getElementById("employee-delete-text");
    const deleteForm = document.getElementById("employee-delete-form");

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    const resetForm = () => {
        employeeForm?.setAttribute("action", "/gerencia/empleados");
        nameInput.value = "";
        roleInput.value = "";
        statusInput.value = "ACTIVO";
    };

    const bindRowActions = (row) => {
        row.querySelector(".view-btn")?.addEventListener("click", () => {
            window.showUiToast?.(`Empleado: ${row.dataset.nombre} · Rol ${row.dataset.rol}`);
        });

        row.querySelector(".edit-btn")?.addEventListener("click", () => {
            modalTitle.textContent = "Editar Empleado";
            employeeForm?.setAttribute("action", `/gerencia/empleados/${row.dataset.id}`);
            nameInput.value = row.dataset.nombre ?? "";
            roleInput.value = row.dataset.rol ?? "";
            statusInput.value = (row.dataset.estado ?? "ACTIVO").toUpperCase();
            openModal(employeeModal);
        });

        row.querySelector(".delete-btn")?.addEventListener("click", () => {
            deleteText.textContent = `¿Deseas inactivar a ${row.dataset.nombre}?`;
            deleteForm?.setAttribute("action", `/gerencia/empleados/${row.dataset.id}/inactivar`);
            openModal(deleteModal);
        });
    };

    document.querySelectorAll(".table-wrap tbody tr[data-id]").forEach(bindRowActions);

    newEmployeeBtn?.addEventListener("click", () => {
        modalTitle.textContent = "Nuevo Empleado";
        resetForm();
        openModal(employeeModal);
    });

    saveButton?.addEventListener("click", () => {
        if (!employeeForm) return;
        if (!nameInput.value.trim() || !roleInput.value.trim()) {
            window.showUiToast?.("Completa los campos requeridos.");
            return;
        }
        employeeForm.submit();
    });

    cancelButton?.addEventListener("click", () => closeModal(employeeModal));
    deleteCancel?.addEventListener("click", () => closeModal(deleteModal));
    deleteConfirm?.addEventListener("click", () => deleteForm?.submit());

    [employeeModal, deleteModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
