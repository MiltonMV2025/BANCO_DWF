document.addEventListener("DOMContentLoaded", () => {
    const newEmployeeBtn = document.getElementById("new-employee-btn");
    const employeeModal = document.getElementById("employee-modal");
    const deleteModal = document.getElementById("employee-delete-modal");
    const profileModal = document.getElementById("employee-profile-modal");
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
    const profileClose = document.getElementById("employee-profile-close");
    const profileAvatar = document.getElementById("employee-profile-avatar");
    const profileName = document.getElementById("employee-profile-name");
    const profileTagline = document.getElementById("employee-profile-tagline");
    const profileRole = document.getElementById("employee-profile-role");
    const profileStatus = document.getElementById("employee-profile-status");
    const profileLoans = document.getElementById("employee-profile-loans");
    const profileShift = document.getElementById("employee-profile-shift");
    const profileBranch = document.getElementById("employee-profile-branch");
    const profileEmail = document.getElementById("employee-profile-email");
    const profilePhone = document.getElementById("employee-profile-phone");
    const profileJoined = document.getElementById("employee-profile-joined");

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

    const toInitials = (fullName) => {
        const words = (fullName ?? "").trim().split(/\s+/).filter(Boolean);
        if (!words.length) return "EM";
        return words.slice(0, 2).map((word) => word[0].toUpperCase()).join("");
    };

    const slugifyName = (name) => (name ?? "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9]+/g, ".")
        .replace(/^\.+|\.+$/g, "") || "empleado";

    const buildMockData = (row) => {
        const numericId = Number(row.dataset.id ?? 0);
        const shifts = ["Matutino", "Vespertino", "Rotativo", "Nocturno"];
        const branches = ["Sucursal Centro", "Sucursal Escalón", "Sucursal Santa Tecla", "Sucursal Merliot"];
        const phones = ["+503 7110-2200", "+503 7123-9400", "+503 7135-8812", "+503 7144-1001"];
        const joinedDates = ["Ingreso: 2021-03-10", "Ingreso: 2022-01-15", "Ingreso: 2020-09-28", "Ingreso: 2019-06-04"];
        const idx = Math.abs(numericId) % shifts.length;
        const email = `${slugifyName(row.dataset.nombre)}@bancodwf.com`;

        return {
            shift: shifts[idx],
            branch: branches[idx],
            phone: phones[idx],
            joined: joinedDates[idx],
            email
        };
    };

    const openEmployeeProfile = (row) => {
        const fullName = row.dataset.nombre ?? "Empleado";
        const mock = buildMockData(row);

        profileAvatar.textContent = toInitials(fullName);
        profileName.textContent = fullName;
        profileTagline.textContent = `Ficha operativa · ID ${row.dataset.id ?? "N/A"}`;
        profileRole.textContent = row.dataset.rol ?? "Sin rol";
        profileStatus.textContent = (row.dataset.estado ?? "ACTIVO").toUpperCase();
        profileLoans.textContent = row.dataset.prestamos ?? "0";
        profileShift.textContent = mock.shift;
        profileBranch.textContent = mock.branch;
        profileEmail.textContent = mock.email;
        profilePhone.textContent = mock.phone;
        profileJoined.textContent = mock.joined;
        openModal(profileModal);
    };

    const bindRowActions = (row) => {
        row.querySelector(".view-btn")?.addEventListener("click", () => {
            openEmployeeProfile(row);
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
    profileClose?.addEventListener("click", () => closeModal(profileModal));
    deleteConfirm?.addEventListener("click", () => deleteForm?.submit());

    [employeeModal, deleteModal, profileModal].forEach((modal) => {
        modal?.addEventListener("click", (event) => {
            if (event.target === modal) closeModal(modal);
        });
    });
});
