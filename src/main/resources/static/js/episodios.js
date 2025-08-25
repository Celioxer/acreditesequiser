document.addEventListener('DOMContentLoaded', function() {
    // =================================================================================
    // FUNÇÃO AUXILIAR PARA ESCOLHER A PASTA PÚBLICA
    // =================================================================================
    function getPastaPublica(numeroEpisodio) {
        const num = parseInt(numeroEpisodio, 10);

        if (num >= 1 && num <= 50) {
            return '/1-50/';
        } else if (num >= 51 && num <= 100) {
            return '/51 - 100/';
        } else if (num >= 101 && num <= 150) {
            return '/101 - 150/';
        } else if (num >= 151 && num <= 200) {
            return '/151-200/';
        } else if (num >= 201) {
            return '/201 - ate o ultimo/';
        } else {
            return '/'; // Pasta padrão caso algo dê errado
        }
    }

    // =================================================================================
    // FUNÇÃO GERADORA HÍBRIDA ATUALIZADA
    // =================================================================================
    function criarEpisodio(dados) {
        const baseURL = 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Episódios';

        // A mágica acontece aqui: a pasta é escolhida com base na lógica
        const pasta = dados.exclusivo
            ? '/Exclusivos para assinantes/'
            : getPastaPublica(dados.numero);

        const nomeArquivo = encodeURIComponent(dados.titulo);

        // Lógica Híbrida para a Capa
        const capaURL = dados.capa ? dados.capa : `${baseURL}${pasta}${nomeArquivo}.png`;

        // A URL de download é sempre gerada com a pasta correta
        const downloadURL = `${baseURL}${pasta}${nomeArquivo}.mp3`;

        return {
            numero: dados.numero,
            titulo: dados.titulo,
            descricao: dados.descricao,
            exclusivo: dados.exclusivo,
            capa: capaURL,
            download: downloadURL
        };
    }

    // =================================================================================
    // DADOS BRUTOS DOS EPISÓDIOS - LISTA COMPLETA E UNIFICADA
    // =================================================================================
    const episodiosBrutos = [
        // --- EPISÓDIOS PÚBLICOS ---
        {
            numero: '01',
            titulo: '01 - O Milagre Do Sol de Fátima',
            descricao: 'Uma análise detalhada dos eventos de 13 de outubro de 1917 em Portugal.',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde0dfa412257cf625e628c5a',
            exclusivo: false
        },
        {
            numero: '02',
            titulo: '02 - A Sonda do Capão redondo',
            descricao: 'Investigando o intrigante caso ufológico ocorrido na periferia de São Paulo.',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f35c426c53d67cdbda12d505f',
            exclusivo: false
        },
        {
            numero: '03',
            titulo: '03 - Os Goblins de Hopkinsville',
            descricao: 'O famoso encontro de uma família com pequenas criaturas humanoides em Kentucky.',
            capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f905809b65134873564de8fb2',
            exclusivo: false
        },
        { numero: '04', titulo: '04 - Mitos natalinos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '05', titulo: '05 - Everyday Chemistry', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '06', titulo: '06 - Onda Ufologica', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '07', titulo: '07 - O Caso Duas Pontes', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '08', titulo: '08 - Real vs Fake - Registros Ufologicos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '09', titulo: '09 - O Menino de Tordesilhas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '10', titulo: '10 - Fogo no Ceu - A Abdução de Travis Walton', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '11', titulo: '11 - Caso Algeciras', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '12', titulo: '12 - Phill Schneider e a Revolta dos Subniveis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '13', titulo: '13 - Casos Bacacheri e Bauru', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '14', titulo: '14 - Militares e os Ovnis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '15', titulo: '15 - Caso Kathie Davies', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '16', titulo: '16 - O Efeito Mandela e Historias do Multiverso', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '17', titulo: '17 - Caso Pedro Luro', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '18', titulo: '18 - Bate Papo Ufológico - Antônio Faleiro', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '19', titulo: '19 - Ovnis ou Balões', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '20', titulo: '20 - Mitologia Grega - Medusa', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '21', titulo: '21 - O Encontro de Rosa Daineli', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '22', titulo: '22 - Real vs Fake 2 - Registros Ufologicos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '23', titulo: '23 - A Noite Oficial Dos Ovnis - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '24', titulo: '24 - Histórias de Fantasmas 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '25', titulo: '25 - A Noite Oficial Dos Ovnis - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '26', titulo: '26 - Histórias de Fantasmas 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '27', titulo: '27 - Caso Colombo', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '28', titulo: '28 - Ovnis no Folclore Brasileiro', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '29', titulo: '29 - O Clássico Caso Higgins', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '30', titulo: '30 - Edward Mordrake', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '31', titulo: '31 - Casos Maria Cintra e Turibio Pereira', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '32', titulo: '32 - Os 12 Trabalhos de Hércules', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '33', titulo: '33 - O Fantástico Caso Delphos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '34', titulo: '34 - BloodBorne', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '35', titulo: '35 - A Batalha De Los Angeles', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '36', titulo: '36 - Os Ovnis na Bíblia', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '37', titulo: '37 - Caso Stephen Michalak', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '38', titulo: '38 - Caso Lonnie Zamora', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '39', titulo: '39 - O Mistério de Vorstad', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '40', titulo: '40 - TCI - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '41', titulo: '41 - Os Alienígenas Pescadores', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '42', titulo: '42 - TCI - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '43', titulo: '43 - O Caso da Barragem do Funil', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '44', titulo: '44 - O Livro de Enoque', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '45', titulo: '45 - Antonina – Terra de Mistérios e UFOs', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '46', titulo: '46 - Relatos de um MIB', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '47', titulo: '47 - Incidente em Varginha - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '48', titulo: '48 - Hipnoterapia em Abduções', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '49', titulo: '49 - Incidente em Varginha - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '50', titulo: '50 - Trilogia A Volta dos Mortos Vivos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '51', titulo: '51 - A Recuperação de Ovnis de Origem Não Humana pelos EUA', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '52', titulo: '52 - Monstros Aquáticos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '53', titulo: '53 - Os Ovnis Gigantes da FAB', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '54', titulo: '54 - Histórias Paranormais - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '55', titulo: '55 - Brusque Misteriosa', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '56', titulo: '56 - Histórias Paranormais - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '57', titulo: '57 - O Mistério da Antiga Vila Baumer', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '58', titulo: '58 - Mitologia Grega - Teseu e o Minotauro', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '59', titulo: '59 - O Ovni da África do Sul', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '60', titulo: '60 - Ufologia Bizarra', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '61', titulo: '61 - Caso Cussac', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '62', titulo: '62 - O Incidente de Tunguska', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '63', titulo: '63 - Missão Ovni - Apollo 11', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '64', titulo: '64 - Abdução em Niteroi', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '65', titulo: '65 - A Audiência sobre Ovnis no Congresso dos EUA', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '66', titulo: '66 - Teorias de Invasão Aliénigena', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '67', titulo: '67 - Uma Audiência histórica... Mas e o depois', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '68', titulo: '68 - Caso Cláudio', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '69', titulo: '69 - Caso Vasp 169', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '70', titulo: '70 - Missing 411', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '71', titulo: '71 - Incidente em Varginha - Parte 3', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '72', titulo: '72 - A Casa Assombrada', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '73', titulo: '73 - Missão Ovni - Gemini 10 e 12', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '74', titulo: '74 - A Saga do Bebê Diabo', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '75', titulo: '75 - Aliens São Dêmonios', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '76', titulo: '76 - Projetos Secretos do Governo', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '77', titulo: '77 - Caso Sagrada Família', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '78', titulo: '78 - I.A. nos Filmes - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '79', titulo: '79 - Conferências Ufológicas - México e Nasa', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '80', titulo: '80 - I.A. nos Filmes - Parte 2 - Matrix', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '81', titulo: '81 - O Caso da Escola Ariel', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '82', titulo: '82 - Projeto Filadélfia', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '83', titulo: '83 - Revisitando o Caso Magé', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '84', titulo: '84 - Clarividência Hereditária', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '85', titulo: '85 - O Caso do Embornal', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '86', titulo: '86 - A Fuga de Alcatraz', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '87', titulo: '87 - Nossa Senhora de Zeitoun', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '88', titulo: '88 - Histórias de Fantasmas - Parte 3', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '89', titulo: '89 - Implantes Alienígenas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '90', titulo: '90 - Contato com Arcturianos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '91', titulo: '91 - Os Casos da Ilha do Carangueijo', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '92', titulo: '92 - Especial de Halloween - Filmes Alienígenas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '93', titulo: '93 - Novos Avistamentos em Vôos Comerciais', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '94', titulo: '94 - As Misteriosas Rádios Fantasmas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '95', titulo: '95 - Implantes Alienígenas - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '96', titulo: '96 - Histórias de Fantasmas - Parte 4', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '97', titulo: '97 - Acredite se Quiser, Mas Não Eram Balões', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '98', titulo: '98 - Histórias de Lobisomem', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '99', titulo: '99 - O Vampiro da Moca', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '100', titulo: '100 - Caso Roswell', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '101', titulo: '101 - ChupaCabras - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '102', titulo: '102 - De Carona com os Ovnis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '103', titulo: '103 - Caso Antônio Nelso Tasca', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '104', titulo: '104 - A Abdução de Kadu', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '105', titulo: '105 - Caso Feira de Santana', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '106', titulo: '106 - Conspirações Ufológicas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '107', titulo: '107 - O Incidente na Floresta de Rendlesham', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '108', titulo: '108 - Contato Paranormal', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '109', titulo: '109 - Retrospectiva Ufológica 2023', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '110', titulo: '110 - Relatos de Lobisomem', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '111', titulo: '111 - O Monstro de Flatwoods', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '112', titulo: '112 - O Monstro de LochNess', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '113', titulo: '113 - Ufologia Gaúcha', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '114', titulo: '114 - O Alien de Ronnie Hill', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '115', titulo: '115 - Incidente em Varginha - Parte 4', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '116', titulo: '116 - Contatos Inexplicáveis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '117', titulo: '117 - Operação Prato - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '118', titulo: '118 - Abdução de Dormitório', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '119', titulo: '119 - Operação Prato - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '120', titulo: '120 - A Possessão', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '121', titulo: '121 - A Casuística Ufológica de Pirassununga', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '122', titulo: '122 - O Mestre das Marionetes', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '123', titulo: '123 - Abdução em Maringá', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '124', titulo: '124 - A Abdução de Kadu 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '125', titulo: '125 - O Incidente do Passo Dyatlov - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '126', titulo: '126 - Mistérios da Mente', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '127', titulo: '127 - O Incidente do Passo Dyatlov - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '128', titulo: '128 - Experiências de Quase Morte', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '129', titulo: '129 - Caso Jardinópolis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '130', titulo: '130 - Caso Baleia', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '131', titulo: '131 - Um Relato da Noite Oficial dos Ovnis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '132', titulo: '132 - Bate Papo Ufólogico com as Apoiadoras', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '133', titulo: '133 - ChupaCabras - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '134', titulo: '134 - Supernatural - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '135', titulo: '135 - Os Casos de Voronezh', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '136', titulo: '136 - Supernatural- Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '137', titulo: '137 - Tipologia Alienígena - Classe Animália', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '138', titulo: '138 - Estranha Colheita - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '139', titulo: '139 - Tipologia Alienígena - Classe Robótica', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '140', titulo: '140 - O Fenômeno Poltergeist', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '141', titulo: '141 - Tipologia Alienígena - Classe Exótica', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '142', titulo: '142 - O Mistério dos Sagrados Estigmas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '143', titulo: '143 - Tipologia Alienígena - Classe Humanoide - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '144', titulo: '144 - Saga Star Wars', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '145', titulo: '145 - Tipologia Alienígena - Classe Humanoide - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '146', titulo: '146 - Estranha Colheita - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '147', titulo: '147 - A Mensagem de Ashtar Sheran', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '148', titulo: '148 - Estranha Colheita - Parte 3', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '149', titulo: '149 - Ufologia e Mistérios do Mato Grosso', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '150', titulo: '150 - Caso da Ilha João Donato', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '151', titulo: '151 - Caso Quarouble', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '152', titulo: '152 - Lugares Míticos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '153', titulo: '153 - Caso Cash-Landrum', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '154', titulo: '154 - Teorias Sobre Tecnologia Alienígena - Parte1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '155', titulo: '155 - Caso Délio', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '156', titulo: '156 - As Múmias de Nazca', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '157', titulo: '157 - Osnis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '158', titulo: '158 - Combustão Humana Espontânea', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '159', titulo: '159 - Caso Mirassol', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '160', titulo: '160 - Pactos e Maldições', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '161', titulo: '161 - Anamnese de um Contatado', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '162', titulo: '162 - A Abdução de Betty e Barney Hill', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '163', titulo: '163 - Resíduos Extraterrestres', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '164', titulo: '164 - O Homem Mariposa', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '165', titulo: '165 - Nunca Foram Balões', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '166', titulo: '166 - O Horror Cósmico de H. P. Lovecraft', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '167', titulo: '167 - Eram os Deuses Astronautas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '168', titulo: '168 - O Caso do Capitão Abelha', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '169', titulo: '169 - Voltando a Varginha', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '170', titulo: '170 - Nada Aconteceu', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '171', titulo: '171 - Especial de Halloween 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '172', titulo: '172 - Chico Xavier e a Viagem a Saturno', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '173', titulo: '173 - Os Novos Avistamentos em Cláudio', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '174', titulo: '174 - Relatos do Arquivo Fenomenum', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '175', titulo: '175 - A Nova Audiência pública sobre OVNIs no Congresso dos EUA', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '176', titulo: '176 - Caso Onilson Pattero e o Chupa Cabras', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '177', titulo: '177 - Abdução em Manhattan', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '178', titulo: '178 - Relatos Sobrenaturais - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '179', titulo: '179 - O Gigante de Paty do Alferes', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '180', titulo: '180 - Relatos Sobrenaturais - Parte 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '181', titulo: '181 - Sorria - Voce está sendo monitorado', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '182', titulo: '182 - Relatos Ufológicos - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '183', titulo: '183 - O Ovni do Morenão', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '184', titulo: '184 - A Nova Onda Ufológica Mundial', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '185', titulo: '185 - A Crise dos Drovnis', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '186', titulo: '186 - Operação Prato - Parte 3', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '187', titulo: '187 - Retrospectiva Ufológica 2024', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '188', titulo: '188 - Relatos Homem Mariposa', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '189', titulo: '189 - Profetas e Profecias', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '190', titulo: '190 - A Entrevista Alienígena', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '191', titulo: '191 - Conspirações da Indústria Farmacêutica', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '192', titulo: '192 - Abduções', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '193', titulo: '193 - O Polémico Aleister Crowley', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '194', titulo: '194 - Tiro, Porrada e Aliens', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '195', titulo: '195 - O Projeto Blue Bean', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '196', titulo: '196 - Os Casos da Ilha Reunião', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '197', titulo: '197 - Os Demônios da Goeta', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '198', titulo: '198 - Caso Mauricio', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '199', titulo: '199 - Vôos Misteriosos - Parte 1', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '200', titulo: '200 - Bases Militares Secretas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '201', titulo: '201 - A Ufologia e Sobrenatural', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '202', titulo: '202 - O Estranho Caso da Chuva de Detritos', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '203', titulo: '203 - Mitos da Internet', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '204', titulo: '204 - Bruxaria', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '205', titulo: '205 - O Relatorio Harald Malmgren', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '206', titulo: '206 - Relatos de Lobisomem 2', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '207', titulo: '207 - Crianças Cósmicas', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '208', titulo: '208 - O Vídeo do Drone', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '209', titulo: '209 - Segredos da Maçonaria', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '210', titulo: '210 - Operação Prato', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '211', titulo: '211 - Mortes Bizarras', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '212', titulo: '212 - Dogons', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '213', titulo: '213 - Vampiros', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '214', titulo: '214 - Investigação Militar', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '215', titulo: '215 - As Gravações do Cindacta', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '216', titulo: '216 - Caso Salyut 6', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '217', titulo: '217 - Ufoarqueologia', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '218', titulo: '218 - Ooparts', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
        { numero: '219', titulo: '219 - Estranhos Visitantes', descricao: 'Descrição pendente...', capa: 'URL_DA_CAPA_SPOTIFY_AQUI', exclusivo: false },
            // --- EPISÓDIOS EXCLUSIVOS ---
                   // Para estes, não precisamos especificar a 'capa', pois ela será gerada automaticamente.
                   {
                       numero: '01',
                       titulo: '01 - Exclusivo - Bate papo com Jacauna - Caso Magé',
                       descricao: 'Uma conversa aprofundada com o ufólogo Jacauna sobre os detalhes do famoso Caso Magé.',
                       exclusivo: true
                   },
                   {
                       numero: '02',
                       titulo: '02 - Exclusivo - Caso Benedito Bogea',
                       descricao: 'Análise do caso de abdução de Benedito Bogea, um dos mais intrigantes da ufologia brasileira.',
                       exclusivo: true
                   },
                   {
                       numero: '03',
                       titulo: '03 - Exclusivo - A Verdade Sobre Lobisomens',
                       descricao: 'Uma investigação sobre relatos históricos e modernos da existência de lobisomens.',
                       exclusivo: true
                   },
                   {
                       numero: '04',
                       titulo: '04 - Exclusivo - A Busca Pela Fonte da Juventude',
                       descricao: 'Relatos e mitos sobre a lendária fonte que promete a vida eterna.',
                       exclusivo: true
                   },
                   {
                       numero: '05',
                       titulo: '05 - Exclusivo  - O Triângulo de Dyfeld',
                       descricao: 'Investigando a misteriosa área no País de Gales conhecida por avistamentos de OVNIs.',
                       exclusivo: true
                   },
                   {
                       numero: '06',
                       titulo: '06 - Exclusivo - Revisitando o Caso Kelly Hopkinsville',
                       descricao: 'Uma nova análise sobre o encontro de uma família com pequenas criaturas em Kentucky.',
                       exclusivo: true
                   },
                   {
                       numero: '07',
                       titulo: '07 - Exclusivo - O Caso Paranormal de Doris Bither',
                       descricao: 'A história real que inspirou o filme "The Entity", sobre uma mulher assombrada por uma força invisível.',
                       exclusivo: true
                   },
                   {
                       numero: '08',
                       titulo: '08- Exclusivo - O dia em que Discos Voadores Assustaram Curitiba',
                       descricao: 'Relembrando a noite em que luzes misteriosas sobrevoaram a capital paranaense.',
                       exclusivo: true
                   },
                   {
                       numero: '09',
                       titulo: '09 - Exclusivo - A Inevitável 3 Guerra Mundial',
                       descricao: 'Uma análise de profecias e tensões geopolíticas que apontam para um conflito global.',
                       exclusivo: true
                   },
                   {
                       numero: '10',
                       titulo: '10 - Exclusivo- A Comoda Assassina',
                       descricao: 'A história assustadora por trás de um objeto amaldiçoado e os eventos trágicos associados a ele.',
                       exclusivo: true
                   },
                   {
                       numero: '11',
                       titulo: '11 -  Exclusivo- Incidente em Varginha - Parte 5',
                       descricao: 'A continuação da saga sobre o mais famoso caso da ufologia brasileira.',
                       exclusivo: true
                   }
               ];

               // Transforma os dados brutos na lista completa de episódios
               const todosEpisodios = episodiosBrutos.map(criarEpisodio);

               // =================================================================================
               // O RESTANTE DO SEU CÓDIGO (LÓGICA DO PLAYER, FILTRO, ETC.)
               // =================================================================================

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
                   if (contadorEpisodios) contadorEpisodios.textContent = `Mostrando ${episodiosFiltrados.length} de ${todosEpisodios.length} episódios encontrados`;
                   if (infoPagina) infoPagina.textContent = `Página ${paginaAtual} de ${totalPaginas || 1}`;
                   if (btnAnterior) btnAnterior.disabled = paginaAtual === 1;
                   if (btnProximo) btnProximo.disabled = paginaAtual === (totalPaginas || 1);
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

               if (buscaInput) buscaInput.addEventListener('input', filtrarEpisodios);
               if (btnAnterior) btnAnterior.addEventListener('click', () => { if (paginaAtual > 1) { paginaAtual--; atualizarPagina(); } });
               if (btnProximo) btnProximo.addEventListener('click', () => {
                   const totalPaginas = Math.ceil(episodiosFiltrados.filter(ep => !ep.exclusivo).length / episodiosPorPagina);
                   if (paginaAtual < totalPaginas) { paginaAtual++; atualizarPagina(); }
               });

               // Inicialização
               atualizarPagina();
           });