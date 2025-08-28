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
// Lógica para o Pop-up de Divulgação
document.addEventListener('DOMContentLoaded', () => {

    const popupOverlay = document.getElementById('dev-popup-overlay');
    const closeButton = document.getElementById('close-popup');

    // Função para mostrar o pop-up
    const showPopup = () => {
        if (popupOverlay) {
            popupOverlay.style.display = 'flex'; // Primeiro torna visível para a animação funcionar
            setTimeout(() => {
                popupOverlay.classList.remove('popup-hidden');
            }, 20); // Pequeno delay para garantir que a transição CSS seja aplicada
        }
    };

    // Função para esconder o pop-up
    const hidePopup = () => {
        if (popupOverlay) {
            popupOverlay.classList.add('popup-hidden');
             // Espera a animação terminar para remover o display flex
            setTimeout(() => {
                popupOverlay.style.display = 'none';
            }, 300); // 300ms é a duração da transição no CSS
        }
    };

    // Verifica se o pop-up já foi mostrado nesta sessão
    if (!sessionStorage.getItem('devPopupShown')) {
        // Mostra o pop-up após 4 segundos (4000 ms)
        setTimeout(() => {
            showPopup();
            // Marca que o pop-up foi mostrado nesta sessão
            sessionStorage.setItem('devPopupShown', 'true');
        }, 4000);
    }

    // Event listener para o botão de fechar
    if (closeButton) {
        closeButton.addEventListener('click', hidePopup);
    }

    // Event listener para fechar clicando fora da caixa (no overlay)
    if (popupOverlay) {
        popupOverlay.addEventListener('click', (event) => {
            if (event.target === popupOverlay) {
                hidePopup();
            }
        });
    }
});