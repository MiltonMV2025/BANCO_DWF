document.addEventListener("DOMContentLoaded", () => {
    const duiInputs = document.querySelectorAll('input[type="text"][name="dui"], input[type="search"][name="dui"]');

    const formatDui = (value) => {
        const digits = (value ?? "").replace(/\D/g, "").slice(0, 9);
        if (digits.length <= 8) return digits;
        return `${digits.slice(0, 8)}-${digits.slice(8)}`;
    };

    const setupDuiInput = (input) => {
        input.required = true;
        input.maxLength = 10;
        input.minLength = 10;
        input.pattern = "\\d{8}-\\d";
        input.inputMode = "numeric";
        input.autocomplete = "off";
        input.title = "Formato requerido: 12345678-9";

        input.value = formatDui(input.value);

        input.addEventListener("input", () => {
            input.value = formatDui(input.value);
        });

        input.addEventListener("blur", () => {
            input.value = formatDui(input.value);
        });
    };

    duiInputs.forEach(setupDuiInput);
});
