document.addEventListener("DOMContentLoaded", () => {
    const newClientBtn = document.getElementById("new-client-btn");
    const applyFilter = document.getElementById("apply-filter");
    const clientModal = document.getElementById("client-modal");
    const deleteModal = document.getElementById("client-delete-modal");
    const modalTitle = document.getElementById("client-modal-title");
    const clientIdInput = document.getElementById("client-id");
    const nameInput = document.getElementById("client-name");
    const duiInput = document.getElementById("client-dui");
    const salaryInput = document.getElementById("client-salary");
    const statusInput = document.getElementById("client-status");
    const saveButton = document.getElementById("client-save");
    const cancelButton = document.getElementById("client-cancel");
    const deleteCancel = document.getElementById("client-delete-cancel");
    const deleteConfirm = document.getElementById("client-delete-confirm");
    const deleteText = document.getElementById("client-delete-text");

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
        clientIdInput.value = "";
        nameInput.value = "";
        duiInput.value = "";
        salaryInput.value = "";
        statusInput.value = "Activo";
    };

    const fillFormFromRow = (row) => {
        clientIdInput.value = row.dataset.id || "";
        nameInput.value = row.dataset.nombre || "";
        duiInput.value = row.dataset.dui || "";
        salaryInput.value = row.dataset.salario || "";
        statusInput.value = row.dataset.estado || "Activo";
    };

    const formatSalary = (value) => {
        const number = Number(value || 0);
        return `$${number.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    };

    const upsertRow = () => {
        const id = clientIdInput.value.trim();
        const name = nameInput.value.trim();
        const dui = duiInput.value.trim();
        const salary = salaryInput.value.trim();
        const status = statusInput.value;

        if (!name || !dui || !salary) {
            window.showUiToast?.("Completa los campos requeridos del cliente.");
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
                <td>0</td>
                <td></td>
                <td><span class="badge"></span></td>
                <td>
                    <button type="button" class="link-btn view-btn">Ver perfil</button>
                    <span class="action-divider">|</span>
                    <button type="button" class="link-btn edit-btn">Editar</button>
                    <span class="action-divider">|</span>
                    <button type="button" class="link-btn delete-btn">Eliminar</button>
                </td>
            `;
            tbody.prepend(row);
        }

        row.dataset.nombre = name;
        row.dataset.dui = dui;
        row.dataset.salario = salary;
        row.dataset.estado = status;

        row.cells[0].textContent = name;
        row.cells[1].textContent = dui;
        row.cells[3].textContent = formatSalary(salary);

        const badge = row.querySelector(".badge");
        badge.textContent = status;
        badge.className = `badge ${status === "Activo" ? "ok" : "warn"}`;

        bindRowActions(row);
        closeModal(clientModal);
        window.showUiToast?.("Cliente guardado en modo visual.");
    };

    const bindRowActions = (row) => {
        if (row.dataset.actionsBound === "true") return;
        row.dataset.actionsBound = "true";

        row.querySelector(".view-btn")?.addEventListener("click", () => {
            window.showUiToast?.(`Perfil de ${row.dataset.nombre} en modo visual.`);
        });

        row.querySelector(".edit-btn")?.addEventListener("click", () => {
            modalTitle.textContent = "Editar Cliente";
            fillFormFromRow(row);
            openModal(clientModal);
        });

        row.querySelector(".delete-btn")?.addEventListener("click", () => {
            rowToDelete = row;
            deleteText.textContent = `¿Eliminar a ${row.dataset.nombre}?`;
            openModal(deleteModal);
        });
    };

    document.querySelectorAll(".table-wrap tbody tr").forEach(bindRowActions);

    newClientBtn?.addEventListener("click", () => {
        modalTitle.textContent = "Nuevo Cliente";
        resetForm();
        openModal(clientModal);
    });

    applyFilter?.addEventListener("click", () => {
        window.showUiToast?.("Filtro aplicado en modo visual.");
    });

    saveButton?.addEventListener("click", upsertRow);
    cancelButton?.addEventListener("click", () => closeModal(clientModal));
    deleteCancel?.addEventListener("click", () => closeModal(deleteModal));

    deleteConfirm?.addEventListener("click", () => {
        if (rowToDelete) {
            rowToDelete.remove();
            rowToDelete = null;
            window.showUiToast?.("Cliente eliminado en modo visual.");
        }
        closeModal(deleteModal);
    });

    [clientModal, deleteModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
