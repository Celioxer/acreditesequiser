document.addEventListener('DOMContentLoaded', function() {
    // Dados dos episódios (você pode substituir por uma chamada API)
    const todosEpisodios = [
        {
            numero: '01',
            titulo: 'O Milagre Do Sol de Fátima',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
            download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/01%20-%20O%20Milagre%20Do%20Sol%20de%20F%C3%A1tima.mp3'
        },
        {
            numero: '02',
            titulo: 'A Sonda do Capão Redondo',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f35c426c53d67cdbda12d505f',
            download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/02%20-%20A%20Sonda%20do%20Cap%C3%A3o%20redondo.mp3'
        },
        {
            numero: '03',
            titulo: 'Os Goblins de Hopkinsville',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f905809b65134873564de8fb2',
            download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/1-50/03%20-%20Os%20Goblins%20de%20Hopkinsville.mp3'
        }
        // Adicione aqui os outros 214 episódios...
    ];

    // Variáveis de estado
    let episodiosFiltrados = [...todosEpisodios];
    let paginaAtual = 1;
    const episodiosPorPagina = 20;

    // Elementos DOM
    const listaEpisodios = document.getElementById('lista-episodios');
    const buscaInput = document.getElementById('busca-episodio');
    const contadorEpisodios = document.getElementById('contador-episodios');
    const btnAnterior = document.getElementById('anterior');
    const btnProximo = document.getElementById('proximo');
    const infoPagina = document.getElementById('info-pagina');

    // Função para renderizar episódios
    function renderizarEpisodios() {
        // Calcular índices para paginação
        const inicio = (paginaAtual - 1) * episodiosPorPagina;
        const fim = inicio + episodiosPorPagina;
        const episodiosPagina = episodiosFiltrados.slice(inicio, fim);

        // Limpar lista
        listaEpisodios.innerHTML = '';

        // Adicionar episódios
        episodiosPagina.forEach(ep => {
            const episodioCard = document.createElement('div');
            episodioCard.className = 'episodio-card';

            episodioCard.innerHTML = `
                <img src="${ep.capa}" alt="Capa Episódio ${ep.numero}" class="episodio-cover">
                <div class="episodio-info">
                    <h3>Episódio ${ep.numero}: ${ep.titulo}</h3>
                    <a href="${ep.download}" class="episodio-link" download>
                        Download
                    </a>
                </div>
            `;

            listaEpisodios.appendChild(episodioCard);
        });

        // Atualizar contador
        contadorEpisodios.textContent = `Mostrando ${episodiosPagina.length} de ${episodiosFiltrados.length} episódios`;

        // Atualizar paginação
        infoPagina.textContent = `Página ${paginaAtual} de ${Math.ceil(episodiosFiltrados.length / episodiosPorPagina)}`;

        // Habilitar/desabilitar botões
        btnAnterior.disabled = paginaAtual === 1;
        btnProximo.disabled = paginaAtual === Math.ceil(episodiosFiltrados.length / episodiosPorPagina);
    }

    // Função para filtrar episódios
    function filtrarEpisodios() {
        const termo = buscaInput.value.toLowerCase();

        episodiosFiltrados = todosEpisodios.filter(ep => {
            return ep.titulo.toLowerCase().includes(termo) ||
                   ep.numero.includes(termo);
        });

        // Resetar para a primeira página após filtrar
        paginaAtual = 1;
        renderizarEpisodios();
    }

    // Event listeners
    buscaInput.addEventListener('input', filtrarEpisodios);

    btnAnterior.addEventListener('click', () => {
        if (paginaAtual > 1) {
            paginaAtual--;
            renderizarEpisodios();
        }
    });

    btnProximo.addEventListener('click', () => {
        if (paginaAtual < Math.ceil(episodiosFiltrados.length / episodiosPorPagina)) {
            paginaAtual++;
            renderizarEpisodios();
        }
    });

    // Inicializar
    renderizarEpisodios();
});