document.addEventListener("DOMContentLoaded", () => {
    const operationButtons = document.querySelectorAll(".operation-btn");
    const amountInput = document.getElementById("amount-input");
    const amountLabel = document.getElementById("amount-label");
    const confirmButton = document.getElementById("confirm-action");
    const summaryType = document.getElementById("summary-type");
    const summaryAmount = document.getElementById("summary-amount");
    const summaryTotal = document.getElementById("summary-total");
    const accountSelect = document.getElementById("account-select");
    const summaryAccount = document.getElementById("summary-account");
    const operationInput = document.getElementById("operation-type-input");
    const amountHiddenInput = document.getElementById("operation-amount-input");
    const operationForm = document.getElementById("transfer-operation-form");

    const formatCurrency = (value) => {
        const number = Number(value || 0);
        return `$${number.toFixed(2)}`;
    };

    const syncAmounts = () => {
        const value = amountInput?.value ?? 0;
        if (amountHiddenInput) amountHiddenInput.value = value;
        const formatted = formatCurrency(value);
        if (summaryAmount) summaryAmount.textContent = formatted;
        if (summaryTotal) summaryTotal.textContent = formatted;
    };

    const updateOperationUi = (operationType) => {
        const isDeposito = operationType === "DEPOSITO";
        if (operationInput) operationInput.value = operationType;

        operationButtons.forEach((button) => {
            button.classList.toggle("active", button.dataset.type === operationType);
        });

        if (amountLabel) {
            amountLabel.textContent = isDeposito ? "Monto a depositar" : "Monto a retirar";
        }

        if (confirmButton) {
            confirmButton.innerHTML = `Confirmar <span>${isDeposito ? "Depósito" : "Retiro"}</span>`;
        }

        if (summaryType) {
            summaryType.textContent = isDeposito ? "Depósito" : "Retiro";
        }
    };

    operationButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const operationType = button.dataset.type;
            if (!operationType) return;
            updateOperationUi(operationType);
        });
    });

    amountInput?.addEventListener("input", syncAmounts);

    accountSelect?.addEventListener("change", () => {
        const accountText = accountSelect.options[accountSelect.selectedIndex]?.textContent?.trim();
        if (summaryAccount && accountText) {
            summaryAccount.textContent = accountText;
        }
    });

    operationForm?.addEventListener("submit", (event) => {
        const value = Number(amountInput?.value ?? 0);
        if (value <= 0) {
            event.preventDefault();
            window.showUiToast?.("Ingresa un monto mayor a cero.");
        }
    });

    const initialType = operationInput?.value === "RETIRO" ? "RETIRO" : "DEPOSITO";
    updateOperationUi(initialType);
    syncAmounts();
});
