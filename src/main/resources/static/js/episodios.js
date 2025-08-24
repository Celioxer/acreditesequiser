document.addEventListener('DOMContentLoaded', function() {
    // =================================================================================
    // DADOS DOS EPISÓDIOS
    // Adicione a propriedade 'exclusivo: true/false' e 'descricao' a cada episódio.
    // =================================================================================
    const todosEpisodios = [
        // Exemplo:
          {
                numero: '01',
                titulo: 'O Milagre Do Sol de Fátima',
                descricao: 'Uma análise detalhada dos eventos de 13 de outubro de 1917 em Portugal, testemunhados por milhares.',
                capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/01%20-%20O%20Milagre%20Do%20Sol%20de%20F%C3%A1tima.mp3',
                exclusivo: false
            },
            {
                numero: '02',
                titulo: 'A Sonda do Capão Redondo',
                descricao: 'Investigando o intrigante caso ufológico ocorrido na periferia de São Paulo nos anos 80.',
                capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f35c426c53d67cdbda12d505f',
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/02%20-%20A%20Sonda%20do%20Cap%C3%A3o%20redondo.mp3',
                exclusivo: false
            },
            {
                numero: '03',
                titulo: 'Os Goblins de Hopkinsville',
                descricao: 'O famoso encontro de uma família com pequenas criaturas humanoides em uma fazenda no Kentucky.',
                capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f905809b65134873564de8fb2',
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/03%20-%20Os%20Goblins%20de%20Hopkinsville.mp3',
                exclusivo: false
            },
        // ==================================
            // --- EPISÓDIOS EXCLUSIVOS ---
            // ==================================
            {
                numero: '51',
                titulo: 'A Recuperação de Ovnis pelos EUA',
                descricao: 'Documentos vazados e testemunhos de insiders sobre programas secretos de recuperação de naves.',
                capa: 'https://i.scdn.co/image/ab6765630000ba8a7e0452c938497f394f6f2c6e',
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/51%20-%20A%20Recupera%C3%A7%C3%A3o%20de%20Ovnis%20de%20Origem%20N%C3%A3o%20Humana%20pelos%20EUA.mp3',
                exclusivo: true
            },
            {
                numero: '52',
                titulo: 'Análise dos Documentos Majestic 12',
                descricao: 'Uma investigação profunda sobre a autenticidade e as implicações dos controversos documentos MJ-12.',
                capa: 'https://i.scdn.co/image/ab6765630000ba8a5c4e857416c117d23d8339c7',
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/52%20-%20Analise%20dos%20Documentos%20Majestic%2012.mp3',
                exclusivo: true
            },
            {
                numero: '53',
                titulo: 'O Manuscrito Voynich Decifrado?',
                descricao: 'Novas teorias e análises computacionais que podem finalmente ter quebrado o código do livro mais misterioso do mundo.',
                capa: 'https://i.scdn.co/image/ab6765630000ba8a07156152a1a8rob1s1d2a41d', // Imagem de exemplo
                download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/53%20-%20O%20Manuscrito%20Voynich.mp3',
                exclusivo: true
            },

        // Adicione aqui o restante da sua lista de episódios...
    ];

    // Variáveis de estado e elementos DOM
    let episodiosFiltrados = [...todosEpisodios];
    let paginaAtual = 1;
    const episodiosPorPagina = 12;
    const gridExclusivos = document.getElementById('grid-exclusivos');
    const gridPublicos = document.getElementById('grid-publicos');
    const buscaInput = document.getElementById('busca-episodio');
    const contadorEpisodios = document.getElementById('contador-episodios');
    const btnAnterior = document.getElementById('anterior');
    const btnProximo = document.getElementById('proximo');
    const infoPagina = document.getElementById('info-pagina');

    // Função para renderizar os cards em um grid específico
    function renderizarGrid(lista, elementoGrid) {
        if (!elementoGrid) return;
        elementoGrid.innerHTML = '';

        lista.forEach(ep => {
            const isExclusivo = ep.exclusivo;
            const cardClass = isExclusivo ? 'episodio-card exclusivo' : 'episodio-card';
            const badgeHTML = isExclusivo ? '<div class="card-badge">Exclusivo</div>' : '';

            const episodioCard = document.createElement('div');
            episodioCard.className = cardClass;

            // MOLDE HTML ATUALIZADO AQUI
            episodioCard.innerHTML = `
                ${badgeHTML}
                <img src="${ep.capa}" alt="Capa do Episódio ${ep.numero}" class="card-cover">
                <div class="card-body">
                    <span class="ep-number">#${ep.numero}</span>
                    <h3 class="ep-title">${ep.titulo}</h3>
                    <p class="ep-description">${ep.descricao || 'Descrição não disponível.'}</p>
                    <div class="card-actions">
                        <button class="btn-card btn-play" data-audio-src="${ep.download}">
                            <i class="fas fa-play"></i> Ouvir
                        </button>
                        <a href="${ep.download}" class="btn-card btn-icon btn-download" download title="Fazer Download">
                            <i class="fas fa-download"></i>
                        </a>
                    </div>
                    <div class="card-player-wrapper"></div>
                </div>
            `;
            elementoGrid.appendChild(episodioCard);
        });

        adicionarEventListenersPlayer();
    }

    // Lógica do Player
    function adicionarEventListenersPlayer() {
        document.querySelectorAll('.btn-play').forEach(button => {
            button.addEventListener('click', tocarAudio);
        });
    }

    function tocarAudio(event) {
        const playButton = event.currentTarget;
        const cardBody = playButton.closest('.card-body');
        const actionsWrapper = cardBody.querySelector('.card-actions');
        const playerWrapper = cardBody.querySelector('.card-player-wrapper');
        const audioUrl = playButton.getAttribute('data-audio-src');

        document.querySelectorAll('.card-player-wrapper').forEach(wrapper => {
            if (wrapper !== playerWrapper && wrapper.innerHTML !== '') {
                wrapper.innerHTML = '';
                wrapper.closest('.card-body').querySelector('.card-actions').style.display = 'flex';
            }
        });

        actionsWrapper.style.display = 'none';

        playerWrapper.innerHTML = `
            <audio controls autoplay style="width:100%; height: 40px;">
                <source src="${audioUrl}" type="audio/mpeg">
                Seu navegador não suporta o elemento de áudio.
            </audio>
        `;
    }

    // Lógica principal de atualização da página
    function atualizarPagina() {
        const episodiosExclusivos = episodiosFiltrados.filter(ep => ep.exclusivo);
        const episodiosPublicos = episodiosFiltrados.filter(ep => !ep.exclusivo);
        const totalPaginas = Math.ceil(episodiosPublicos.length / episodiosPorPagina);
        paginaAtual = Math.max(1, Math.min(paginaAtual, totalPaginas || 1));
        const inicio = (paginaAtual - 1) * episodiosPorPagina;
        const fim = inicio + episodiosPorPagina;
        const publicosPaginados = episodiosPublicos.slice(inicio, fim);
        renderizarGrid(episodiosExclusivos, gridExclusivos);
        renderizarGrid(publicosPaginados, gridPublicos);
        contadorEpisodios.textContent = `Mostrando ${episodiosFiltrados.length} de ${todosEpisodios.length} episódios encontrados`;
        infoPagina.textContent = `Página ${paginaAtual} de ${totalPaginas || 1}`;
        btnAnterior.disabled = paginaAtual === 1;
        btnProximo.disabled = paginaAtual === (totalPaginas || 1);
    }

    // Lógica de filtro e paginação
    function filtrarEpisodios() {
        const termo = buscaInput.value.toLowerCase();
        episodiosFiltrados = todosEpisodios.filter(ep =>
            ep.titulo.toLowerCase().includes(termo) || ep.numero.includes(termo)
        );
        paginaAtual = 1;
        atualizarPagina();
    }
    buscaInput.addEventListener('input', filtrarEpisodios);
    btnAnterior.addEventListener('click', () => { if (paginaAtual > 1) { paginaAtual--; atualizarPagina(); } });
    btnProximo.addEventListener('click', () => {
        const totalPaginas = Math.ceil(episodiosFiltrados.filter(ep => !ep.exclusivo).length / episodiosPorPagina);
        if (paginaAtual < totalPaginas) { paginaAtual++; atualizarPagina(); }
    });

    // Inicialização
    atualizarPagina();
});