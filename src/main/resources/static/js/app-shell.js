document.addEventListener("DOMContentLoaded", () => {
    const toast = document.getElementById("toast");

    window.showUiToast = (message) => {
        if (!toast) return;
        toast.textContent = message;
        toast.classList.add("show");
        window.setTimeout(() => {
            toast.classList.remove("show");
        }, 2000);
    };
});
