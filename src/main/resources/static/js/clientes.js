document.addEventListener("DOMContentLoaded", () => {
    const newClientBtn = document.getElementById("new-client-btn");
    const clientModal = document.getElementById("client-modal");
    const deleteModal = document.getElementById("client-delete-modal");
    const profileModal = document.getElementById("client-profile-modal");
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
    const profileClose = document.getElementById("client-profile-close");
    const profileAvatar = document.getElementById("client-profile-avatar");
    const profileName = document.getElementById("client-profile-name");
    const profileTagline = document.getElementById("client-profile-tagline");
    const profileDui = document.getElementById("client-profile-dui");
    const profileStatus = document.getElementById("client-profile-status");
    const profileSalary = document.getElementById("client-profile-salary");
    const profileAccounts = document.getElementById("client-profile-accounts");
    const profileBalance = document.getElementById("client-profile-balance");
    const profileEmail = document.getElementById("client-profile-email");
    const profilePhone = document.getElementById("client-profile-phone");
    const profileManager = document.getElementById("client-profile-manager");

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    const isValidDui = (value) => {
        const digits = (value ?? "").replace(/\D/g, "");
        return digits.length === 9;
    };

    const resetForm = () => {
        clientForm?.setAttribute("action", "/gerencia/clientes");
        nameInput.value = "";
        duiInput.value = "";
        salaryInput.value = "";
        statusInput.value = "ACTIVO";
    };

    const toCurrency = (value) => {
        const amount = Number(value ?? 0);
        return `$${amount.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    };

    const toInitials = (fullName) => {
        const words = (fullName ?? "").trim().split(/\s+/).filter(Boolean);
        if (!words.length) return "CL";
        return words.slice(0, 2).map((word) => word[0].toUpperCase()).join("");
    };

    const slugifyName = (name) => (name ?? "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9]+/g, ".")
        .replace(/^\.+|\.+$/g, "") || "cliente";

    const buildMockData = (row) => {
        const numericId = Number(row.dataset.id ?? 0);
        const phones = ["+503 7001-1200", "+503 7002-8430", "+503 7010-3290", "+503 7099-4500"];
        const managers = [
            "Gestor asignado: Ejecutivo Comercial",
            "Gestor asignado: Banca Personal",
            "Gestor asignado: Asesor Senior",
            "Gestor asignado: Mesa de Servicio"
        ];
        const idx = Math.abs(numericId) % phones.length;
        const email = `${slugifyName(row.dataset.nombre)}@bancodwf.com`;

        return {
            email,
            phone: phones[idx],
            manager: managers[idx]
        };
    };

    const openClientProfile = (row) => {
        const fullName = row.dataset.nombre ?? "Cliente";
        const mock = buildMockData(row);

        profileAvatar.textContent = toInitials(fullName);
        profileName.textContent = fullName;
        profileTagline.textContent = `Ficha completa · ID ${row.dataset.id ?? "N/A"}`;
        profileDui.textContent = row.dataset.dui ?? "N/D";
        profileStatus.textContent = (row.dataset.estado ?? "ACTIVO").toUpperCase();
        profileSalary.textContent = toCurrency(row.dataset.salario);
        profileAccounts.textContent = row.dataset.cuentas ?? "0";
        profileBalance.textContent = toCurrency(row.dataset.saldo);
        profileEmail.textContent = mock.email;
        profilePhone.textContent = mock.phone;
        profileManager.textContent = mock.manager;
        openModal(profileModal);
    };

    const bindRowActions = (row) => {
        row.querySelector(".view-btn")?.addEventListener("click", () => {
            openClientProfile(row);
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

        if (!isValidDui(duiInput.value)) {
            window.showUiToast?.("Ingresá un DUI válido.");
            return;
        }

        clientForm.submit();
    });

    cancelButton?.addEventListener("click", () => closeModal(clientModal));
    deleteCancel?.addEventListener("click", () => closeModal(deleteModal));
    profileClose?.addEventListener("click", () => closeModal(profileModal));
    deleteConfirm?.addEventListener("click", () => deleteForm?.submit());

    [clientModal, deleteModal, profileModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
