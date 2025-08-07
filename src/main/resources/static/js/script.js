document.addEventListener('DOMContentLoaded', function() {
    // Menu hamburguer
    const menuToggle = document.querySelector('.menu-toggle');
    const nav = document.querySelector('nav ul');

    if(menuToggle && nav) {
        menuToggle.addEventListener('click', () => {
            menuToggle.classList.toggle('active');
            nav.classList.toggle('active');
        });

        // Fecha o menu ao clicar em um item
        document.querySelectorAll('nav a').forEach(item => {
            item.addEventListener('click', () => {
                if (window.innerWidth <= 768) {
                    menuToggle.classList.remove('active');
                    nav.classList.remove('active');
                }
            });
        });
    }

    // Rolagem suave para links internos
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener("click", function(e) {
            e.preventDefault();
            document.querySelector(this.getAttribute("href")).scrollIntoView({
                behavior: "smooth"

                document.addEventListener('DOMContentLoaded', function() {
                    // Dados dos episódios (pode ser substituído por uma API real)
                    const episodios = Array.from({length: 217}, (_, i) => {
                        const numero = i + 1;
                        const ano = 2015 + Math.floor((numero - 1) / 12); // Simula anos de 2015 em diante
                        const mes = (numero % 12) || 12;
                        return {
                            numero,
                            titulo: `Episódio ${numero}: Título do Episódio`,
                            data: `${mes.toString().padStart(2, '0')}/${ano}`,
                            spotifyId: '6X8ee9z0Y7lGswyThQCEZ8', // ID de exemplo do Spotify
                            cloudflareUrl: `https://example.cloudflare.com/episodio-${numero}.mp3`
                        };
                    });

                    const listaEpisodios = document.getElementById('lista-episodios');
                    const buscaInput = document.getElementById('busca-episodio');
                    const filtroAno = document.getElementById('filtro-ano');

                    // Função para renderizar episódios
                    function renderizarEpisodios(episodiosParaRenderizar) {
                        listaEpisodios.innerHTML = '';

                        episodiosParaRenderizar.forEach(ep => {
                            const episodioCard = document.createElement('div');
                            episodioCard.className = 'episodio-card';

                            episodioCard.innerHTML = `
                                <img src="https://i.scdn.co/image/${ep.spotifyId}" alt="Capa do Episódio ${ep.numero}" class="episodio-cover">
                                <div class="episodio-info">
                                    <h3 class="episodio-titulo">${ep.titulo}</h3>
                                    <div class="episodio-numero">#${ep.numero.toString().padStart(3, '0')}</div>
                                    <div class="episodio-data">${ep.data}</div>
                                    <div class="episodio-links">
                                        <a href="https://open.spotify.com/episode/${ep.spotifyId}" target="_blank" class="episodio-link">Ouvir no Spotify</a>
                                        <a href="${ep.cloudflareUrl}" class="episodio-link" download>Download</a>
                                        <button class="episodio-link player-btn" data-audio="${ep.cloudflareUrl}">Ouvir Aqui</button>
                                    </div>
                                </div>
                            `;

                            listaEpisodios.appendChild(episodioCard);
                        });

                        // Adiciona eventos aos botões de player
                        document.querySelectorAll('.player-btn').forEach(btn => {
                            btn.addEventListener('click', function() {
                                const audioUrl = this.getAttribute('data-audio');
                                abrirPlayer(audioUrl);
                            });
                        });
                    }

                    // Função para filtrar episódios
                    function filtrarEpisodios() {
                        const termoBusca = buscaInput.value.toLowerCase();
                        const anoSelecionado = filtroAno.value;

                        const episodiosFiltrados = episodios.filter(ep => {
                            const matchesBusca = ep.titulo.toLowerCase().includes(termoBusca) ||
                                                ep.numero.toString().includes(termoBusca);
                            const matchesAno = anoSelecionado === 'todos' ||
                                             ep.data.endsWith(anoSelecionado);

                            return matchesBusca && matchesAno;
                        });

                        renderizarEpisodios(episodiosFiltrados);
                    }

                    // Função para abrir player de áudio
                    function abrirPlayer(audioUrl) {
                        // Fecha player existente
                        const playerExistente = document.getElementById('audio-player');
                        if (playerExistente) playerExistente.remove();

                        // Cria novo player
                        const player = document.createElement('div');
                        player.id = 'audio-player';
                        player.style.position = 'fixed';
                        player.style.bottom = '0';
                        player.style.left = '0';
                        player.style.right = '0';
                        player.style.backgroundColor = '#0A1517';
                        player.style.padding = '1rem';
                        player.style.zIndex = '1000';
                        player.style.boxShadow = '0 -2px 10px rgba(0,0,0,0.5)';

                        player.innerHTML = `
                            <audio controls autoplay style="width: 100%; max-width: 500px; margin: 0 auto; display: block;">
                                <source src="${audioUrl}" type="audio/mpeg">
                                Seu navegador não suporta o elemento de áudio.
                            </audio>
                            <button id="fechar-player" style="position: absolute; right: 1rem; top: 1rem; background: none; border: none; color: #D6C178; cursor: pointer;">X</button>
                        `;

                        document.body.appendChild(player);

                        // Adiciona evento para fechar player
                        document.getElementById('fechar-player').addEventListener('click', function() {
                            player.remove();
                        });
                    }

                    // Event listeners para filtros
                    buscaInput.addEventListener('input', filtrarEpisodios);
                    filtroAno.addEventListener('change', filtrarEpisodios);

                    // Renderiza todos os episódios inicialmente
                    renderizarEpisodios(episodios);
                });
            });
        });
    });
});