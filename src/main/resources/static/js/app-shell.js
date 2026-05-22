document.addEventListener("DOMContentLoaded", () => {
    const toast = document.getElementById("toast");
    const autoMessage = toast?.dataset.autoMessage;

    window.showUiToast = (message) => {
        if (!toast) return;
        toast.textContent = message;
        toast.classList.add("show");
        window.setTimeout(() => {
            toast.classList.remove("show");
        }, 2000);
    };

    if (autoMessage) {
        window.showUiToast(autoMessage);
    }
});
