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
    const searchClient = document.getElementById("search-client");

    let currentOperation = "Deposito";

    const formatCurrency = (value) => {
        const number = Number(value || 0);
        return `$${number.toFixed(2)}`;
    };

    const syncAmounts = () => {
        const formatted = formatCurrency(amountInput?.value);
        if (summaryAmount) summaryAmount.textContent = formatted;
        if (summaryTotal) summaryTotal.textContent = formatted;
    };

    operationButtons.forEach((button) => {
        button.addEventListener("click", () => {
            operationButtons.forEach((node) => node.classList.remove("active"));
            button.classList.add("active");

            currentOperation = button.dataset.type;
            const isDeposito = currentOperation === "Deposito";

            if (amountLabel) {
                amountLabel.textContent = isDeposito ? "Monto a depositar" : "Monto a retirar";
            }

            if (confirmButton) {
                confirmButton.textContent = isDeposito ? "Confirmar Depósito" : "Confirmar Retiro";
            }

            if (summaryType) {
                summaryType.textContent = isDeposito ? "Depósito" : "Retiro";
            }
        });
    });

    amountInput?.addEventListener("input", syncAmounts);

    accountSelect?.addEventListener("change", () => {
        const match = accountSelect.value.match(/(\*{4}\s\*{4}\s\d{4})/);
        if (summaryAccount) {
            summaryAccount.textContent = match ? match[1] : accountSelect.value;
        }
    });

    confirmButton?.addEventListener("click", () => {
        const label = currentOperation === "Deposito" ? "depósito" : "retiro";
        window.showUiToast?.(`Flujo de ${label} listo para integrar backend.`);
    });

    searchClient?.addEventListener("click", () => {
        window.showUiToast?.("Cliente encontrado (modo visual).");
    });

    syncAmounts();
});
