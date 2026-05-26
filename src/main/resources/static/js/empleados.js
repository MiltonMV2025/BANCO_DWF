document.addEventListener("DOMContentLoaded", () => {
    const newEmployeeBtn = document.getElementById("new-employee-btn");
    const employeeModal = document.getElementById("employee-modal");
    const deleteModal = document.getElementById("employee-delete-modal");
    const profileModal = document.getElementById("employee-profile-modal");
    const modalTitle = document.getElementById("employee-modal-title");
    const employeeForm = document.getElementById("employee-form");
    const nameInput = document.getElementById("employee-name");
    const duiInput = document.getElementById("employee-dui");
    const roleInput = document.getElementById("employee-role");
    const passwordInput = document.getElementById("employee-password");
    const passwordConfirmInput = document.getElementById("employee-password-confirm");
    const statusInput = document.getElementById("employee-status");
    const usernameNote = document.getElementById("employee-username-note");
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
    const profileUsername = document.getElementById("employee-profile-username");
    const profileRole = document.getElementById("employee-profile-role");
    const profileStatus = document.getElementById("employee-profile-status");
    const profileLoans = document.getElementById("employee-profile-loans");
    const profileShift = document.getElementById("employee-profile-shift");
    const profileBranch = document.getElementById("employee-profile-branch");
    const profileEmail = document.getElementById("employee-profile-email");
    const profilePhone = document.getElementById("employee-profile-phone");
    const profileJoined = document.getElementById("employee-profile-joined");

    const roleNameByCode = new Map(
        Array.from(roleInput?.options ?? [])
            .map((option) => [option.value, option.textContent?.trim() ?? option.value])
    );

    const isValidDui = (value) => {
        const digits = (value ?? "").replace(/\D/g, "");
        return digits.length === 9;
    };

    const openModal = (modal) => {
        modal?.classList.add("show");
        modal?.setAttribute("aria-hidden", "false");
    };

    const closeModal = (modal) => {
        modal?.classList.remove("show");
        modal?.setAttribute("aria-hidden", "true");
    };

    const normalizeRoleCode = (value) => {
        const role = (value ?? "").trim();
        if (!role) return "";
        if (roleNameByCode.has(role)) return role;
        for (const [code, name] of roleNameByCode.entries()) {
            if (name.toLowerCase() === role.toLowerCase()) {
                return code;
            }
        }
        return role;
    };

    const resolveRoleLabel = (value) => {
        const roleCode = normalizeRoleCode(value);
        return roleNameByCode.get(roleCode) ?? value ?? "Sin rol";
    };

    const resetForm = () => {
        employeeForm?.setAttribute("action", "/gerencia/empleados");
        nameInput.value = "";
        duiInput.value = "";
        roleInput.value = "";
        passwordInput.value = "";
        passwordConfirmInput.value = "";
        passwordInput.required = true;
        passwordConfirmInput.required = true;
        passwordInput.placeholder = "Mínimo 6 caracteres";
        passwordConfirmInput.placeholder = "Repite la contraseña";
        usernameNote.textContent = "Usuario de acceso: se utilizará este DUI para iniciar sesión.";
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
        profileUsername.textContent = row.dataset.dui ?? "N/A";
        profileRole.textContent = resolveRoleLabel(row.dataset.rol);
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
            duiInput.value = row.dataset.dui ?? "";
            roleInput.value = normalizeRoleCode(row.dataset.rol);
            statusInput.value = (row.dataset.estado ?? "ACTIVO").toUpperCase();
            passwordInput.value = "";
            passwordConfirmInput.value = "";
            passwordInput.required = false;
            passwordConfirmInput.required = false;
            passwordInput.placeholder = "Dejá vacío para mantener la contraseña";
            passwordConfirmInput.placeholder = "Repite solo si cambiás la contraseña";
            usernameNote.textContent = `Usuario de acceso (DUI): ${row.dataset.dui ?? "No disponible"}`;
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
        if (!nameInput.value.trim() || !duiInput.value.trim() || !roleInput.value.trim()) {
            window.showUiToast?.("Completa los campos requeridos.");
            return;
        }

        if (!isValidDui(duiInput.value)) {
            window.showUiToast?.("Ingresá un DUI válido.");
            return;
        }

        if (passwordInput.required && !passwordInput.value.trim()) {
            window.showUiToast?.("Debes definir una contraseña.");
            return;
        }

        if (passwordInput.value.trim() !== passwordConfirmInput.value.trim()) {
            window.showUiToast?.("La confirmación de contraseña no coincide.");
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
