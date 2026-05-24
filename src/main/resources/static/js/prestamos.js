document.addEventListener("DOMContentLoaded", () => {

    const detailButtons = document.querySelectorAll(".link-btn:not(.cancel-btn)");
    const cancelButtons = document.querySelectorAll(".cancel-btn");
    const openCaseButton = document.getElementById("open-loan-case");
    const loanAmount = document.getElementById("loan-amount");
    const termInput = document.getElementById("term");
    const monthlyFee = document.getElementById("monthly-fee");
    const newLoanBtn = document.getElementById("new-loan-btn");

    const calculateFee = () => {

        const amount = Number(loanAmount?.value || 0);
        const years = Math.max(Number(termInput?.value || 1), 1);

        const monthly = amount > 0
            ? amount / (years * 12)
            : 0;

        if (monthlyFee) {

            monthlyFee.value = monthly > 0
                ? `$${monthly.toFixed(2)}`
                : "";
        }
    };

    detailButtons.forEach((button) => {

        button.addEventListener("click", () => {

            window.showUiToast?.(
                "Detalle de préstamo pendiente de backend."
            );
        });
    });

    cancelButtons.forEach((button) => {

        button.addEventListener("click", () => {

            window.showUiToast?.(
                "Solicitud marcada para cancelación."
            );
        });
    });

    openCaseButton?.addEventListener("click", async () => {

        const monto = Number(loanAmount.value);
        const anios = Number(termInput.value);

        const plazoMeses = anios * 12;

        try {

            const response = await fetch("/prestamos/solicitar", {

                method: "POST",

                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },

                body: new URLSearchParams({
                    clienteId: 1,
                    monto: monto,
                    plazoMeses: plazoMeses
                })
            });

            const data = await response.text();

            if (response.ok) {

                window.showUiToast?.(
                    "Solicitud enviada correctamente."
                );

                setTimeout(() => {

                    location.reload();

                }, 1500);

            } else {

                window.showUiToast?.(data);
            }

        } catch (error) {

            console.error(error);

            window.showUiToast?.(
                "Error al enviar solicitud."
            );
        }
    });

    newLoanBtn?.addEventListener("click", () => {

        document.querySelector(".open-loan-section")
            ?.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
    });

    loanAmount?.addEventListener("input", calculateFee);
    termInput?.addEventListener("input", calculateFee);

    calculateFee();
});