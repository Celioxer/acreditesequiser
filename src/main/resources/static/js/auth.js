document.addEventListener('DOMContentLoaded', function() {
    // Validação básica do formulário de registro
    const registerForm = document.querySelector('form[th\\:object="${usuario}"]');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const senha = this.querySelector('input[type="password"]').value;
            if (senha.length < 6) {
                alert('A senha deve ter pelo menos 6 caracteres');
                e.preventDefault();
            }
        });
    }
});