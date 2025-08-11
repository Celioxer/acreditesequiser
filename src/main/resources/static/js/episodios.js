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
        },
               {
                    numero: '51',
                    titulo: 'A Recuperação de Ovnis de Origem Não Humana pelos EUA',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/51%20-%20A%20Recupera%C3%A7%C3%A3o%20de%20Ovnis%20de%20Origem%20N%C3%A3o%20Humana%20pelos%20EUA.mp3'
                },
                {
                    numero: '52',
                    titulo: 'Monstros Aquáticos',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/52%20-%20Monstros%20Aqu%C3%A1ticos.mp3'
                },
                {
                    numero: '53',
                    titulo: 'Os Ovnis Gigantes da FAB',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/53%20-%20Os%20Ovnis%20Gigantes%20da%20FAB.mp3'
                },
                {
                    numero: '54',
                    titulo: 'Histórias Paranormais - Parte 1',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/54%20-%20Hist%C3%B3rias%20Paranormais%20-%20Parte%201.mp3'
                },
                {
                    numero: '55',
                    titulo: 'Brusque Misteriosa',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/55%20-%20Brusque%20Misteriosa.mp3'
                },
                {
                    numero: '56',
                    titulo: 'Histórias Paranormais - Parte 2',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/56%20-%20Hist%C3%B3rias%20Paranormais%20-%20Parte%202.mp3'
                },
                {
                    numero: '57',
                    titulo: 'O Mistério da Antiga Vila Baumer',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/57%20-%20O%20Mist%C3%A9rio%20da%20Antiga%20Vila%20Baumer.mp3'
                },
                {
                    numero: '58',
                    titulo: 'Mitologia Grega - Teseu e o Minotauro',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/58%20-%20Mitologia%20Grega%20-%20Teseu%20e%20o%20Minotauro.mp3'
                },
                {
                    numero: '59',
                    titulo: 'O Ovni da África do Sul',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/59%20-%20O%20Ovni%20da%20%C3%81frica%20do%20Sul.mp3'
                },
                {
                    numero: '60',
                    titulo: 'Ufologia Bizarra',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/60%20-%20Ufologia%20Bizarra.mp3'
                },
                {
                    numero: '61',
                    titulo: 'Caso Cussac',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/61%20-%20Caso%20Cussac.mp3'
                },
                {
                    numero: '62',
                    titulo: 'O Incidente de Tunguska',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/62%20-%20O%20Incidente%20de%20Tunguska.mp3'
                },
                {
                    numero: '63',
                    titulo: 'Missão Ovni - Apollo 11',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/63%20-%20Miss%C3%A3o%20Ovni%20-%20Apollo%2011.mp3'
                },
                {
                    numero: '64',
                    titulo: 'Abdução em Niteroi',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/64%20-%20Abdu%C3%A7%C3%A3o%20em%20Niteroi.mp3'
                },
                {
                    numero: '65',
                    titulo: 'A Audiência sobre Ovnis no Congresso dos EUA',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/65%20-%20A%20Audi%C3%AAncia%20sobre%20Ovnis%20no%20Congresso%20dos%20EUA.mp3'
                },
                {
                    numero: '66',
                    titulo: 'Teorias de Invasão Aliénigena',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/66%20-%20Teorias%20de%20Invas%C3%A3o%20Ali%C3%A9nigena.mp3'
                },
                {
                    numero: '67',
                    titulo: 'Uma Audiência histórica... Mas e o depois',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/67%20-%20Uma%20Audi%C3%AAncia%20hist%C3%B3rica...%20Mas%20e%20o%20depois.mp3'
                },
                {
                    numero: '68',
                    titulo: 'Caso Cláudio',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/68%20-%20Caso%20Cl%C3%A1udio.mp3'
                },
                {
                    numero: '69',
                    titulo: 'Caso Vasp 169',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/69%20-%20Caso%20Vasp%20169.mp3'
                },
                {
                    numero: '70',
                    titulo: 'Missing 411',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/70%20-%20Missing%20411.mp3'
                },
                {
                    numero: '71',
                    titulo: 'Incidente em Varginha - Parte 3',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/71%20-%20Incidente%20em%20Varginha%20-%20Parte%203.mp3'
                },
                {
                    numero: '72',
                    titulo: 'A Casa Assombrada',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/72%20-%20A%20Casa%20Assombrada.mp3'
                },
                {
                    numero: '73',
                    titulo: 'Missão Ovni - Gemini 10 e 12',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/73%20-%20Miss%C3%A3o%20Ovni%20-%20Gemini%2010%20e%2012.mp3'
                },
                {
                    numero: '74',
                    titulo: 'A Saga do Bebê Diabo',
                    capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
                    download: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/51%20-%20100/74%20-%20A%20Saga%20do%20Beb%C3%AA%20Diabo.mp3'
                }
                // Adicione aqui os outros episódios conforme você me enviar...
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