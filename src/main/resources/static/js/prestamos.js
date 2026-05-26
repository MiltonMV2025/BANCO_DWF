document.addEventListener("DOMContentLoaded", () => {

    const detailButtons = document.querySelectorAll(".link-btn:not(.cancel-btn)");
    const cancelButtons = document.querySelectorAll(".cancel-btn");
    const loanAmount = document.getElementById("loan-amount");
    const monthlyFee = document.getElementById("monthly-fee");
    const newLoanBtn = document.getElementById("new-loan-btn");

    const calculateFee = () => {
        const amount = Number(loanAmount?.value || 0);
        const monthly = amount > 0 ? amount / 12 : 0;

        if (monthlyFee) {
            monthlyFee.value = monthly > 0
                ? `$${monthly.toFixed(2)}`
                : "";
        }
    };

    detailButtons.forEach((button) => {
        button.addEventListener("click", () => {
            window.showUiToast?.("Detalle de préstamo pendiente de backend.");
        });
    });

    cancelButtons.forEach((button) => {
        button.addEventListener("click", () => {
            window.showUiToast?.("Solicitud marcada para cancelación.");
        });
    });

    newLoanBtn?.addEventListener("click", () => {
        document.querySelector(".open-loan-section")
            ?.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
    });

    loanAmount?.addEventListener("input", calculateFee);
    calculateFee();
});
