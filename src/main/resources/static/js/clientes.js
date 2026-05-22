document.addEventListener("DOMContentLoaded", () => {
    const newClientBtn = document.getElementById("new-client-btn");
    const clientModal = document.getElementById("client-modal");
    const deleteModal = document.getElementById("client-delete-modal");
    const modalTitle = document.getElementById("client-modal-title");
    const clientForm = document.getElementById("client-form");
    const nameInput = document.getElementById("client-name");
    const duiInput = document.getElementById("client-dui");
    const salaryInput = document.getElementById("client-salary");
    const statusInput = document.getElementById("client-status");
    const saveButton = document.getElementById("client-save");
    const cancelButton = document.getElementById("client-cancel");
    const deleteCancel = document.getElementById("client-delete-cancel");
    const deleteConfirm = document.getElementById("client-delete-confirm");
    const deleteText = document.getElementById("client-delete-text");
    const deleteForm = document.getElementById("client-delete-form");

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    const resetForm = () => {
        clientForm?.setAttribute("action", "/gerencia/clientes");
        nameInput.value = "";
        duiInput.value = "";
        salaryInput.value = "";
        statusInput.value = "ACTIVO";
    };

    const bindRowActions = (row) => {
        row.querySelector(".view-btn")?.addEventListener("click", () => {
            window.showUiToast?.(`Cliente: ${row.dataset.nombre} · DUI ${row.dataset.dui}`);
        });

        row.querySelector(".edit-btn")?.addEventListener("click", () => {
            modalTitle.textContent = "Editar Cliente";
            clientForm?.setAttribute("action", `/gerencia/clientes/${row.dataset.id}`);
            nameInput.value = row.dataset.nombre ?? "";
            duiInput.value = row.dataset.dui ?? "";
            salaryInput.value = row.dataset.salario ?? "";
            statusInput.value = (row.dataset.estado ?? "ACTIVO").toUpperCase();
            openModal(clientModal);
        });

        row.querySelector(".delete-btn")?.addEventListener("click", () => {
            deleteText.textContent = `¿Deseas inactivar a ${row.dataset.nombre}?`;
            deleteForm?.setAttribute("action", `/gerencia/clientes/${row.dataset.id}/inactivar`);
            openModal(deleteModal);
        });
    };

    document.querySelectorAll(".table-wrap tbody tr[data-id]").forEach(bindRowActions);

    newClientBtn?.addEventListener("click", () => {
        modalTitle.textContent = "Nuevo Cliente";
        resetForm();
        openModal(clientModal);
    });

    saveButton?.addEventListener("click", () => {
        if (!clientForm) return;
        if (!nameInput.value.trim() || !duiInput.value.trim() || !salaryInput.value.trim()) {
            window.showUiToast?.("Completa los campos requeridos.");
            return;
        }
        clientForm.submit();
    });

    cancelButton?.addEventListener("click", () => closeModal(clientModal));
    deleteCancel?.addEventListener("click", () => closeModal(deleteModal));
    deleteConfirm?.addEventListener("click", () => deleteForm?.submit());

    [clientModal, deleteModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
