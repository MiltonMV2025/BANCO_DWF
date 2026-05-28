document.addEventListener("DOMContentLoaded", () => {

    // Validar que fechaFin no sea menor que fechaInicio
    const fechaInicio = document.querySelector("input[name='fechaInicio']");
    const fechaFin    = document.querySelector("input[name='fechaFin']");

    fechaFin?.addEventListener("change", () => {
        if (fechaInicio.value && fechaFin.value && fechaFin.value < fechaInicio.value) {
            window.showUiToast?.("La fecha fin no puede ser menor que la fecha inicio.");
            fechaFin.value = "";
        }
    });

    fechaInicio?.addEventListener("change", () => {
        if (fechaInicio.value && fechaFin.value && fechaFin.value < fechaInicio.value) {
            fechaFin.value = "";
        }
    });
});