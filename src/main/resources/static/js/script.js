// Bloco 1: Lógica do Menu Hamburguer e Rolagem Suave
document.addEventListener('DOMContentLoaded', function() {
    
    // Lógica do Menu Hambúrguer
    const menuToggle = document.querySelector('.menu-toggle');
    const mainNav = document.querySelector('.main-nav'); // <<< CORRIGIDO: O alvo é a tag <nav> inteira

    if (menuToggle && mainNav) {
        menuToggle.addEventListener('click', () => {
            // Adiciona/remove a classe 'active' em ambos os elementos
            menuToggle.classList.toggle('active');
            mainNav.classList.toggle('active');
        });

        // Opcional: Fecha o menu ao clicar em um link dentro dele
        mainNav.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                if (mainNav.classList.contains('active')) {
                    menuToggle.classList.remove('active');
                    mainNav.classList.remove('active');
                }
            });
        });
    }

    // Rolagem suave para links internos (corrigido)
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener("click", function(e) {
            e.preventDefault();
            const targetElement = document.querySelector(this.getAttribute("href"));
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: "smooth"
                });
            }
        });
    });
});

// Bloco 2: Lógica da Página de Episódios (coloque seu código aqui)
// Esta verificação garante que o código só tente rodar se estiver na página de episódios
if (document.getElementById('lista-episodios')) {
    
    // Todo o seu código de episódios, player, etc., deve vir aqui dentro.
    // Exemplo:
    const episodios = Array.from({length: 217}, (_, i) => { /* ... */ });
    // ... e assim por diante ...

}