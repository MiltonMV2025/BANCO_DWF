document.addEventListener("DOMContentLoaded", () => {
    const fromDate = document.getElementById("from-date");
    const toDate = document.getElementById("to-date");

    if (!fromDate || !toDate) return;

    toDate.addEventListener("change", () => {
        if (fromDate.value && toDate.value && fromDate.value > toDate.value) {
            window.showUiToast?.("La fecha 'Desde' no puede ser mayor que 'Hasta'.");
        }
    });
});
