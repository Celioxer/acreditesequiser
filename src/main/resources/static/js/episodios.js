// =================================================================================
// PARTE 1: DEFINIÇÃO DE DADOS E FUNÇÕES (ESCOPO GLOBAL)
// =================================================================================

function getPastaPublica(numeroEpisodio) {
    const num = parseInt(numeroEpisodio, 10);
    if (num >= 1 && num <= 50) return '/1-50/';
    if (num >= 51 && num <= 100) return '/51 - 100/';
    if (num >= 101 && num <= 150) return '/101 - 150/';
    if (num >= 151 && num <= 200) return '/151-200/';
    if (num >= 201) return '/201 - ate o ultimo/';
    return '/'; // Pasta padrão
}

// <<< FUNÇÃO GERADORA SIMPLIFICADA E CORRIGIDA >>>
function criarEpisodio(dados) {
    const baseURL = 'https://arquivos.acreditesequiserpodcast.com.br/Episódios';

    // Regra: Se for somente streaming ou exclusivo, vai para a pasta de assinantes
    const eExclusivoOuStreaming = dados.exclusivo || dados.somenteStreaming;
    const pasta = eExclusivoOuStreaming ? '/Exclusivos para assinantes/' : getPastaPublica(dados.numero);

    const nomeArquivo = encodeURIComponent(dados.titulo);

    // Lógica da Capa: Se tiver capa manual, usa. Senão, gera.
    const capaURL = dados.capa ? dados.capa : `${baseURL}${pasta}${nomeArquivo}.png`;

    // Lógica do Áudio: Se tiver audio manual, usa. Senão, gera.
    // ISSO RESOLVE SEU PROBLEMA: Permite corrigir links quebrados manualmente
    const audioURL = dados.audio ? dados.audio : `${baseURL}${pasta}${nomeArquivo}.mp3`;

    return {
        numero: dados.numero,
        titulo: dados.titulo,
        descricao: dados.descricao,
        exclusivo: dados.exclusivo,
        somenteStreaming: dados.somenteStreaming || false,
        capa: capaURL,
        audio: audioURL, // Link para o player
        // O download só aparece se NÃO for somenteStreaming
        download: dados.somenteStreaming ? null : audioURL
    };
}


const episodiosBrutos = [
    // EPISÓDIOS ESPECIAIS (No topo da lista)
    {
        numero: '13',
        titulo: '13 - Exclusivo - Dossiê Varginha - Parte 1',
        descricao: 'EPISÓDIO EXCLUSIVO PARA ASSINANTES Em 2026 o caso varginha completa 30 anos. E nesse tempo todo, foi criada uma narrativa para o caso que é amplamente conhecida e divulgada na mídia. Em uma recente pesquisa sobre o caso, utilizando material de 1996,descobrimos muitas divergencias,e que serão divulgadas nesse dossie de 3 partes. Entao aperte o play e venha conhecer a descontrução do caso varginha!',
        // Capa manual (já estava assim)
        capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/Exclusivos%20para%20assinantes/13%20-%20Exclusivo%20-%20Dossi%C3%AA%20Varginha%20-%20Parte%201.jpg',
        exclusivo: true,
        somenteStreaming: true,
    },
    {
        numero: '14',
        titulo: '14 - Exclusivo - Dossiê Varginha - Parte 2',
        descricao: 'EPISÓDIO EXCLUSIVO PARA ASSINANTES Nessa segunda parte do dossiê varginha,vamos falar das criaturas e das testemunhas citadas na casuística do caso. Então aperte o play e venha conhecer a desconstrução do caso varginha! RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO. Se você gosta do nosso trabalho, acesse nosso site e participe do nosso grupo exclusivo para assinantes. Acesse o site acreditesequiserpodcast.com.br Assine UFO, a maior, mais conceituada e mais antiga Revista de Ufologia do mundo',
        // Capa manual
        capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/Exclusivos%20para%20assinantes/14%20-%20Exclusivo%20-%20Dossi%C3%AA%20Varginha%20-%20Parte%202.jpg',
        // AQUI: Se o automático não funciona, coloque o link exato do arquivo de áudio aqui:
        audio: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/Exclusivos%20para%20assinantes/14%20-%20Exclusivo%20-%20%20Dossi%C3%AA%20Varginha%20-%20Parte%202.mp3',
        exclusivo: true,
        somenteStreaming: true,
    },
    {
        numero: '15',
                titulo: '15 - Exclusivo - Dossiê Varginha - Parte 3',
                descricao: 'EPISÓDIO EXCLUSIVO PARA ASSINANTES Na terceira parte do dossiê varginha,vamos comentar sobre o caso do policial Marco Eli Chereze, que teve sua morte supostamente atribuida ao contato com o misterioso ser e também a tão alegada participação militar no caso varginha.Então aperte o play e venha conhecer a Fanfic que se transformou no caso varginha! RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO.',
                // Capa manual
                capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/Exclusivos%20para%20assinantes/15%20-%20Exclusivo%20-%20%20Dossi%C3%AA%20Varginha%20-%20Parte%203.jpeg',
                // AQUI: Se o automático não funciona, coloque o link exato do arquivo de áudio aqui:
                audio: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/Exclusivos%20para%20assinantes/15%20-%20Exclusivo%20-%20%20Dossi%C3%AA%20Varginha%20-%20Parte%203.mp3',
                exclusivo: true,
                somenteStreaming: true,
    },
    // --- episodios ---

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
        { numero: '58', titulo: '58 - Mitologia Grega - Teseu e o Minotauro', descricao: 'Contando com o reforço de Fernando Ribas , estamos de volta a grecia antiga para mais uma conversa com  Leonardo Tremeskin, e dessa vez vamos conhecer a história de Teseu e o Minotauro.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc90dd6354235a8a99e9c085b', exclusivo: false },
        { numero: '59', titulo: '59 - O Ovni da África do Sul', descricao: 'Nesse episódio , 2 casos de avistamento de OVNI com vestígios físicos da pouco conhecida casuística africana, ambos ocorridos em 1972.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fdea778f4c3f6336258dc497d', exclusivo: false },
        { numero: '60', titulo: '60 - Ufologia Bizarra', descricao: 'Nesse episódio , o rei das histórias insólitas, vai nos presentear com alguns dos mais estranhos e bizarros casos da ufologia.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f1eb73647b8158d48d3a1207c', exclusivo: false },
        { numero: '61', titulo: '61 - Caso Cussac', descricao: 'Cussac é um pequeno povoado francês e foi nessa região que ocorreu um caso clássico da Ufologia francesa, quando dois irmãos, François e Anne Marie, com 13 e 9 anos respectivamente, avistaram um OVNI esférico, pousado e quatro dos seus tripulantes.Então aperte o play e venha brincar com os nossos novos amigos!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f062bcb6ff592e5f01987ce40', exclusivo: false },
        { numero: '62', titulo: '62 - O Incidente de Tunguska', descricao: 'No dia 30 de junho de 1908 em uma remota região da Sibéria ,alguma coisa provocou uma grande explosão, devastando uma área de milhares de quilômetros quadrados e deixando além da destruição, muitas perguntas e teorias bizarras. E nesse epísódio, PH e Rafael Jacauna discutem sobre todas essas hipóteses, que vão de queda de um ovni até mesmo a um teste do raio da morte de tesla!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ff49299a1d9a14d040fa4c004', exclusivo: false },
        { numero: '63', titulo: '63 - Missão Ovni - Apollo 11', descricao: 'Fotos e documentos oficiais comprovam que nunca estivemos sós na exploração espacial, e nesse episódio o grande UfoJack vai nos contar toda essa história.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f8379c1c3edd57a80e6039d57', exclusivo: false },
        { numero: '64', titulo: '64 - Abdução em Niteroi', descricao: 'Nesse episódio Philipe Kling vai nos contar um caso de abdução ocorrido em niterói na década de 50.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fe176905f068e37f2cf505cc3', exclusivo: false },
        { numero: '65', titulo: '65 - A Audiência sobre Ovnis no Congresso dos EUA', descricao: 'No episódio de hoje PH e Jackson Camargo vão comentar sobre a audiência ufológica que aconteceu no congresso dos EUA no dia 26 de Julho .', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7a3ece493143dd7a6db451d1', exclusivo: false },
        { numero: '66', titulo: '66 - Teorias de Invasão Aliénigena', descricao: 'Como seria uma invasão alienígena? Existem infinitas possibilidades e nesse epísódio, PH e Rafael Jacaúna discutem sobre algumas dessas teorias com o reforço da dupla dinâmica do Podcast Paranormal FM, Leonardo Marques  e Fernando Ribas.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f60427ae43d9be6cda5ca5db4', exclusivo: false },
        { numero: '67', titulo: '67 - Uma Audiência histórica... Mas e o depois', descricao: 'No episódio de hoje PH , Jackson Camargo e Rony Vernet vão discutir o que aconteceu após a audiência ufológica que aconteceu no congresso dos EUA no dia 26 de Julho .', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fbb9ea54b945a285b14153123', exclusivo: false },
        { numero: '68', titulo: '68 - Caso Cláudio', descricao: 'Nesse episódio recebemos a visita do grande  Lauro Miguel, que vai contar sobre o caso Cláudio, que foi um evento ufológico ocorrido na cidade de Cláudio ,MG em 2008, e que também terá um documentário a ser lançado em breve.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f15f1954808dcd5eacac8f70c', exclusivo: false },
        { numero: '69', titulo: '69 - Caso Vasp 169', descricao: 'Na noite de 8 de Fevereiro de 1982 o avião Boeing 727 da Vasp foi acompanhado por um objeto luminoso, que acompanhou o voo durante várias horas e impressionou todos a bordo, e um dos passageiros desse voo nos conta hoje como tudo aconteceu !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fcf376b9dc783601605c5f41c', exclusivo: false },
        { numero: '70', titulo: '70 - Missing 411', descricao: 'Nesse episódio Philipe Kling e PH conversam sobre o documentário Missing 411 e falam também de outros casos semelhantes.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f0ee6f29e006a7cfac9547bbc', exclusivo: false },
        { numero: '71', titulo: '71 - Incidente em Varginha - Parte 3', descricao: 'Quem eram os misteriosos homens que visitaram a mãe das meninas do caso varginha em sua casa, na tentativa de desmentir o caso? O que existe de real sobre os supostos vídeos das criaturas que algumas pessoas afirmam ter? Para tentar responder essas perguntas , O Ufólogo Marco Leal se junta a Jackson Camargo e PH num bate bapo sobre o caso varginha.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f2966267e52b4f0d407b2c79b', exclusivo: false },
        { numero: '72', titulo: '72 - A Casa Assombrada', descricao: 'Nesse episódio vamos conhecer a historia do nosso ouvinte e apoiador João, que morou por um tempo em uma casa onde coisas "estranhas " aconteciam. Então quando estiverem escutando , evitem olhar para a janela aberta! ', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fa3fa228fec6af96bd62f3e15', exclusivo: false },
        { numero: '73', titulo: '73 - Missão Ovni - Gemini 10 e 12', descricao: 'Continuando nossa série Missão Ovni , falaremos hoje das missões Gemini 10 e 12 , e através de fotos e documentos oficiais da nasa , descobrimos que nunca estivemos sós na exploração espacial.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3dc183d075a33e2bc809678c', exclusivo: false },
        { numero: '74', titulo: '74 - A Saga do Bebê Diabo', descricao: 'Nesse episódio Philipe Kling e PH conversam sobre a saga do bebê diabo, uma história bizarra direto dos anos 80!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f2069a2851be3af149676f1e8', exclusivo: false },
        { numero: '75', titulo: '75 - Aliens São Dêmonios', descricao: 'Porquê antigos eventos ufológicos tiveram interpretações tão difentes entre si? Enquanto algumas culturas viam esses eventos como algo milagrosos outras já viam como algo do mal. E nesse episódio UfoJack e PH discutem o porque dessas diferenças.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f47e940f5f670ff197000829d', exclusivo: false },
        { numero: '76', titulo: '76 - Projetos Secretos do Governo', descricao: 'Nesse episódio Rafael Jacaúna e PH vão conversar sobre os mais secretos e bizarros programas, que o governo de alguns países já fizeram, ou quem sabe ainda fazem....', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f569fe1b8388ccf1b91fa4b21', exclusivo: false },
        { numero: '77', titulo: '77 - Caso Sagrada Família', descricao: 'Nesse episódio vamos receber o pesquisador Morgan para falar de um dos mais extraordinários casos ufológicos de MG, que ocorreu em 1963, no Bairro Sagrada Família, em Belo Horizonte , tendo como protagonista 3 garotos que contataram um ser humanóide, ciclope, com aproximadamente 2 metros e meio de altura.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7cfdc0de63231b213bd0f0ad', exclusivo: false },
        { numero: '78', titulo: '78 - I.A. nos Filmes - Parte 1', descricao: 'Várias tecnologias vistas em filmes saíram direto da ficção cientifica para nosso dia a dia, e diante dos recentes avanços da Inteligência Artificial ,fica a pergunta : É ficção ou previsão? Nessa primeira parte PH, Rafael Jacaúna, Fernando Ribas e UfoJAck teorizam sobre nosso futuro diante das máquinas, cada vez mais "inteligentes".', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc21f045b9406c4fa206d840a', exclusivo: false },
        { numero: '79', titulo: '79 - Conferências Ufológicas - México e Nasa', descricao: 'Nesse episódio, PH, Fernando Ribas e UfoJack comentam sobre as audiéncias ufológicas furadas no Congresso Mexicano e da Nasa, ocorridas em setembro de 2023.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ff2ac34166fd21324de3bbe41', exclusivo: false },
        { numero: '80', titulo: '80 - I.A. nos Filmes - Parte 2 - Matrix', descricao: 'Nessa segunda parte PH, Rafael Jacaúna e Fernando Ribas discutem sobre a Matrix, um filme de 1999 que a cada dia que passa , se torna mais próximo da realidade do que da ficção.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1febc6e6104006af4e8558a78b', exclusivo: false },
        { numero: '81', titulo: '81 - O Caso da Escola Ariel', descricao: 'Este caso é considerado um dos melhores casos de contato imediato da história da Ufologia, e ocorreu em 16 de setembro de 1994 e envolveu 62 crianças de várias raças e etnias, que testemunharam um espetáculo extraordinário.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7024b7cb72c5cea15a69dae5', exclusivo: false },
        { numero: '82', titulo: '82 - Projeto Filadélfia', descricao: 'O projeto filadélfia  foi um suposto projeto naval militar realizado por volta de 28 de outubro de 1943, que tinha como objetivo tornar o navio destróier USS Eldridge invisível aos radares inimigos. Mas... parece que aconteceu muito mais do que o esperado, e nesse episódio e vão contar essa história maluca!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1faf1c49abdc79fa6165150d5e', exclusivo: false },
        { numero: '83', titulo: '83 - Revisitando o Caso Magé', descricao: 'Nesse episódio conversamos com o Ufólogo sobre o caso magé, que foi uma suposta queda de Ovni ocorrida na cidade de Magé no RJ em 2020, alguns meses após o inicio da pandemia de Covid. O que teria acontecido ali ? ', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fe9ea487115f5e46011d12760', exclusivo: false },
        { numero: '84', titulo: '84 - Clarividência Hereditária', descricao: 'Nesse episódio vamos conversar com a nossa apoiadora Rita Ferreira, que vai nos contar sobre um dom, que assim como toda a sua família, ela também possui.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f98c0ea52265a6fc1e2f7b69f', exclusivo: false },
        { numero: '85', titulo: '85 - O Caso do Embornal', descricao: 'Nesse episódio vamos falar de um dos casos mais fantásticos de contato imediato da ufologia brasileira, ocorrido na cidade de Baependi, sul de Minas Gerais, em 16 de maio de 1979.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9a821bf6582eb3ad81c44bc9', exclusivo: false },
        { numero: '86', titulo: '86 - A Fuga de Alcatraz', descricao: 'Nesse episódio Rafael Jacaúna e PH vão contar como aconteceu a única fuga bem sucedida da prisao de Alcatraz e qual foi o possível destino desses fugitivos.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f57794b8d5b71c6f665b0107f', exclusivo: false },
        { numero: '87', titulo: '87 - Nossa Senhora de Zeitoun', descricao: 'esse episódio vamos receber o Ufólogo e diretor da Mufon Brasil,  para falar de Nossa Senhora de Zeitoun , que foi um avistamento atribuído à virgem maria tendo como origem um conjunto de aparições marianas que teriam ocorrido em Zeitoun, no distrito do cairo, Egito a partir de 2 de abril de 1968.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fcd2201fc691ba3e54c88025b', exclusivo: false },
        { numero: '88', titulo: '88 - Histórias de Fantasmas - Parte 3', descricao: 'Nesse episódio vamos receber novamente o Ufólogo e diretor da Mufon Brasil,  para nos contar suas histórias pessoais sobre fantasmas e também conversar sobre esse misterioso assunto!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f08c2170763cfa589be7f362e', exclusivo: false },
        { numero: '89', titulo: '89 - Implantes Alienígenas', descricao: 'Nesse episódio vamos receber o Dr. Arthur Gatti, que é cirurgião de pescoço e cabeça , para nos contar suas experiências com corpos estranhos e marcas atípicas encontrados em exames ou cirurgias em seus pacientes. Seriam os famosos implantes alienígenas?', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fc2e7eff728e1f45ba2cb98bf', exclusivo: false },
        { numero: '90', titulo: '90 - Contato com Arcturianos', descricao: 'Nesse episódio ,contando com a ajuda da hipnoterapeuta e pesquisadora , vamos conversar com o Diego, e conhecer sua história que envolve missing time e abdução . Então aperte o play, feche os olhos e se concentrem apenas em nossa conversa.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f78b6f3a444121e8c64627bf9', exclusivo: false },
        { numero: '91', titulo: '91 - Os Casos da Ilha do Carangueijo', descricao: 'Nesse episódio nós vamos dar início a uma série sobre a famosa operação prato, e vamos começar com um caso ufológico envolvendo quatro pescadores maranhenses, envolvendo uma morte , e que tem relação com os incidentes na cidade de colares no Pará.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ff760887dad312fe1119dfa20', exclusivo: false },
        { numero: '92', titulo: '92 - Especial de Halloween - Filmes Alienígenas', descricao: 'Nesse episódio especial de halloween, os nossos apoiadores vão indicar filmes com a temática alienígena para alegrar o seu dias das bruxas. Lembrando que sâo apenas indicações e não damos spoilers de qualquer filme.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fbd4175a9dbf4268e18d20a61', exclusivo: false },
        { numero: '93', titulo: '93 - Novos Avistamentos em Vôos Comerciais', descricao: 'O ano de 2023 mal havia começado e a história dos balões nos EUA e Canadá movimentou a galera da ufologia. Mas desde então vários avistamentos e contatos tem ocorrido por todo o mundo. E nesse episódio vamos falar de 2 casos envolvendo vôos comerciais no brasil e mais alguns casos interessantes.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7ec6c81d4b5bd5d633cbe389', exclusivo: false },
        { numero: '94', titulo: '94 - As Misteriosas Rádios Fantasmas', descricao: 'Nesse episódio vamos receber do Podcast Rádio Sobrenatural, para falar de rádios e transmissões fantasmas.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f531979817722f910e749074a', exclusivo: false },
        { numero: '95', titulo: '95 - Implantes Alienígenas - Parte 2', descricao: 'Nesse episódio recebemos novamente o Dr. Arthur Gatti para a nossa segunda parte sobre implantes alienígenas!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7220b93c12ebea5ea5e6cf94', exclusivo: false },
        { numero: '96', titulo: '96 - Histórias de Fantasmas - Parte 4', descricao: 'Nesse episódio vamos receber novamente do Podcast Rádio Sobrenatural, para falar fantasmas e causos das estradas do nosso brasil.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f663e4b0eb0876fd193025d97', exclusivo: false },
        { numero: '97', titulo: '97 - Acredite se Quiser, Mas Não Eram Balões', descricao: 'Nesse episódio vamos conversar com um oficial da marinha americana , que estava em uma base militar no início do ano durante os incidentes com os balões na divisa dos EUA e canadá. O que realmente teria acontecido?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f37aae1dbedac6d5ca0ac3a89', exclusivo: false },
        { numero: '98', titulo: '98 - Histórias de Lobisomem', descricao: 'Vocês acreditam em lobisomem? Seriam apenas lendas ou seres reais? Nesse episódio vamos conversar sobre essa criatura e conhecer alguns relatos de encontros com esses monstros.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc7f18d0f11959386f66f46b8', exclusivo: false },
        { numero: '99', titulo: '99 - O Vampiro da Moca', descricao: 'Em 1975, ocorreu uma onda de mortes de animais em circunstâncias estranhas na região de Moca, Porto Rico. Hoje, décadas depois, o mistério permanece.Então aperte o play e venha conhecer a provável origem do chupacabras.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3b03e3628a6976df87b6dd68', exclusivo: false },
        { numero: '100', titulo: '100 - Caso Roswell', descricao: 'Chegamos ao episódio 100! E para comemorar essa marca, hoje vamos falar sobre um dos maiores casos da ufologia mundial : O caso Roswell, ocorrido no Novo México em julho de 1947.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f9d00e52fd527e8ee3a73ba65', exclusivo: false },
        { numero: '101', titulo: '101 - ChupaCabras - Parte 1', descricao: 'Esse é um dos maiores enigmas ligados à Ufologia. Um estranho predador, que vitimou milhares de animais de criação, em dezenas de países. O que se sabe sobre a misteriosa criatura? Nesse episódio vamos saber mais sobre esse fenômeno com o maior pesquisador do caso, o grande ufólogo, Carlos Alberto Machado.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f5ac55b4f9f707f0ef758cc32', exclusivo: false },
        { numero: '102', titulo: '102 - De Carona com os Ovnis', descricao: 'Nesse episódio vamos conversar com o apresentador e autor da série De Carona com os Óvnis,  , sobre os bastidores da série e conhecer várias outras histórias que ele ouviu em suas viagens pelo Brasil. Então venha pegar essa carona com a gente e conhecer essas histórias!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f11270fed8b3ace48bf8a35e2', exclusivo: false },
        { numero: '103', titulo: '103 - Caso Antônio Nelso Tasca', descricao: 'Antônio Nelso Tasca passou por uma abdução em 14 de dezembro de 1983, e após a experiência, ele descobre um ferimento indolor em suas costas.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ffbeea906042b5a04c1552ac6', exclusivo: false },
        { numero: '104', titulo: '104 - A Abdução de Kadu', descricao: 'Nesse episódio vamos conhecer a história de Kadu, que teve alguns episódios de missing time, e após fazer a hipnose de regressão , descobriu o que se passou nesse período de tempo perdido.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fca3e281b6c5dc5c7abc8926a', exclusivo: false },
        { numero: '105', titulo: '105 - Caso Feira de Santana', descricao: 'Nesse episódio vamos falar de um intrigante caso de queda de OVNI com resgate de tripulantes, ocorrido na zona rural da cidade de Feira de Santana, Bahia, em 12 de janeiro de 1995.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3cbc3a4d60c32f184aa57bfa', exclusivo: false },
        { numero: '106', titulo: '106 - Conspirações Ufológicas', descricao: 'Nesse episódio convidamos Roberto Munhoz do Canal Projeto 93 para bater um papo sobre conspirações ufológicas, e apresentar uma série de incríveis coincidencias entre dois ex presidentes americanos.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f76f0a59aae66bf52f9dbc9ca', exclusivo: false },
        { numero: '107', titulo: '107 - O Incidente na Floresta de Rendlesham', descricao: 'Nesse episódio especial de natal, vamos falar do caso da Floresta Rendlesham, que envolveu uma série de avistamentos de luzes não identificadas perto da Floresta Rendlesham, em Suffolk, Inglaterra, em 25 de dezembro de 1980 e que foram associados a pousos de OVNIs.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f72fc8a6364af5cac101c153e', exclusivo: false },
        { numero: '108', titulo: '108 - Contato Paranormal', descricao: 'Hoje vamos conhecer as histórias arrepiantes de contatos paranormais da nossa ouvinte Luiza Frez', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f28c0c312ba668480168d8906', exclusivo: false },
        { numero: '109', titulo: '109 - Retrospectiva Ufológica 2023', descricao: 'Nesse episódio convidamos o pesquisador Rony Vernet para relembrarmos o que de mais importante aconteceu no cenário da ufologia em 2023.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fd5d6ae3d3895831ae6117620', exclusivo: false },
        { numero: '110', titulo: '110 - Relatos de Lobisomem', descricao: 'Nesse episódio vamos escutar e comentar relatos de lobisomem e de um certo intruso esporádico agressivo enviados pelos nossos ouvintes e apoiadores.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9e4d0e12cc771e7f2e97792f', exclusivo: false },
        { numero: '111', titulo: '111 - O Monstro de Flatwoods', descricao: 'O monstro Flatwoods é uma criatura que levou pânico à cidade de Flatwoods na West Virginia, Estados Unidos, em 12 de setembro de 1952, após uma luz brilhante cruzar o céu noturno.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f48fa876b027bbe55e8344b14', exclusivo: false },
        { numero: '112', titulo: '112 - O Monstro de LochNess', descricao: 'Localizado nas terras altas da Escócia, rodeado de montanhas escarpadas, florestas e campos, o Lago Ness é um dos grandes lagos da Europa, e talvez seja mesmo o mais misterioso de todos. Suas águas escuras e geladas seriam o lar de uma imensa criatura.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f710f03fb1f26796c60d9506c', exclusivo: false },
        { numero: '113', titulo: '113 - Ufologia Gaúcha', descricao: 'Nesse episódio vamos conversar com o ufólogo Márcio Parussini, sobre a cauística do Rio Grande do Sul e conhecer vários casos interessantes', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3bd6b32ab6d29fcdae528c21', exclusivo: false },
        { numero: '114', titulo: '114 - O Alien de Ronnie Hill', descricao: 'Nesse episódio chamamos o Sr. Philipe Kling  para nos contar a história por trás de uma foto de um suposto ser alienígena.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f86d5ec51b37f8d2a29a77be2', exclusivo: false },
        { numero: '115', titulo: '115 - Incidente em Varginha - Parte 4', descricao: 'Será que após 28 anos ainda podemos esperar alguma novidade sobre o caso varginha? E para tentar reponder essa pergunta, conversamos com o Ufólogo  sobre esse misterioso caso.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f99a79e8d89782befe2ce00b9', exclusivo: false },
        { numero: '116', titulo: '116 - Contatos Inexplicáveis', descricao: 'Hoje vamos conhecer as histórias arrepiantes de contatos inexplicáveis da nossa ouvinte Luana.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ff679b565d5d9aa59e263c44f', exclusivo: false },
        { numero: '117', titulo: '117 - Operação Prato - Parte 1', descricao: 'A partir desse episódio vamos começar a falar da operação prato, que teve seu início em uma onda de casos ufológicos de natureza hostil no estado do Maranhão, levando a população ao pânico e chamando a atenção das autoridades.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fb220375ec452b04d1437cc50', exclusivo: false },
        { numero: '118', titulo: '118 - Abdução de Dormitório', descricao: 'Hoje vamos conversar com nossa ouvinte Athena, que vai nos contar suas histórias de contatos inexplicáveis.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f450b012eb76b2c46f8582548', exclusivo: false },
        { numero: '119', titulo: '119 - Operação Prato - Parte 2', descricao: 'Vamos continuar a falar dos incidentes que levaram ao sugimento da famosa operação prato, e dessa vez já vamos para o Pará, onde o fenômeno começou a chamar a atenção dos militares.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fd1f4bae3c9a07597b3b1569f', exclusivo: false },
        { numero: '120', titulo: '120 - A Possessão', descricao: 'Hoje vamos conhecer a história de um ouvinte que teve problemas com uma entidade se incorporando ao seu irmão.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f28e8ac7fe1de6498a4d93008', exclusivo: false },
        { numero: '121', titulo: '121 - A Casuística Ufológica de Pirassununga', descricao: 'Nesse episódio vamos falar de vários casos ocorridos na cidade de Pirassununga (SP).', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f1f4062e34de671f8fc5a18f3', exclusivo: false },
        { numero: '122', titulo: '122 - O Mestre das Marionetes', descricao: 'Nesse episódio vamos contar a história da nossa ouvinte e apoiadora Jaqueline, que descobriu que além do vício, alguma outra coisa estava controlando seu ex marido.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fcc932c67531e71a95efc8114', exclusivo: false },
        { numero: '123', titulo: '123 - Abdução em Maringá', descricao: 'Nesse episódio um caso de abdução envolvendo o jovem Jocelino de Matos e seu irmão, no bairro Jardim Alvorada, em Maringá (PR), em 13 de abril de 1979.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f427796811fe078272e41ae0f', exclusivo: false },
        { numero: '124', titulo: '124 - A Abdução de Kadu 2', descricao: 'Nesse episódio iremos voltar na história da abdução do Kadu e saber de "coisas" que não foram contadas na primeira conversa.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fde088efbdbe67ac56b2ae86f', exclusivo: false },
        { numero: '125', titulo: '125 - O Incidente do Passo Dyatlov - Parte 1', descricao: 'Incidente do Passo Dyatlov foi um acontecimento que resultou na morte de nove jovens ao norte dos montes urais, na antiga União Soviética , na noite de 2 de fevereiro de 1959 e que nunca foi de fato solucionado, tendo explicações que vão desde um incidente ufológico a um ataque de um Yeti..', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9a70ee0799c99294111b8772', exclusivo: false },
        { numero: '126', titulo: '126 - Mistérios da Mente', descricao: 'Nesse episódio vamos conhecer alguns dos mistérios do mais fascinante orgão do corpo humano: O nosso cerébro!!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fab776a9601467418c732872e', exclusivo: false },
        { numero: '127', titulo: '127 - O Incidente do Passo Dyatlov - Parte 2', descricao: 'Incidente do Passo Dyatlov tem diversas teorias para explicar o que aconteceu. As hipóteses vão desde um encontro ufológico até a supostos testes militares.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fdaed9f1facc723dd4d34ac44', exclusivo: false },
        { numero: '128', titulo: '128 - Experiências de Quase Morte', descricao: 'O que é uma experiência de quase morte? O que os médicos e cientistas dizem sobre essa estranha condição? Hoje nós vamos discutir algumas possibilidades e contar algumas histórias pessoais sobre o assunto.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f6349923794e49bd1be352b10', exclusivo: false },
        { numero: '129', titulo: '129 - Caso Jardinópolis', descricao: 'A cidade de Jardinópolis, no interior paulista foi palco de um acontecimento insólito na noite de 27 de dezembro de 2008, onde um grupo de adolescentes na época teriam presenciado a descida de luzes vermelhas e azuis num terreno baldio próximo de onde estavam e tiveram contato com seres estranhos.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f837ff3dcbe346ad49371c908', exclusivo: false },
        { numero: '130', titulo: '130 - Caso Baleia', descricao: 'Nesse episódio , nosso convidado Morgan, do Projeto Contato, vai nos contar um interessante caso de contato imediato ocorrido nas proximidades do Hospital da Baleia, em Belo Horizonte (MG), em 14 de setembro de 1967.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fa31bd204dd73801563f67df5', exclusivo: false },
        { numero: '131', titulo: '131 - Um Relato da Noite Oficial dos Ovnis', descricao: 'Nesse episódio vamos trazer um relato inédito da famosa noite oficial dos ovnis ,contado pelo nosso convidado o Comandante Roni Piagget, que estava no comando de um voo comercial nessa noite de maio de 1986.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3ed1d3bdce0348d68171ce9c', exclusivo: false },
        { numero: '132', titulo: '132 - Bate Papo Ufólogico com as Apoiadoras', descricao: 'Nesse episódio especial do mês das mulheres, vamos curtir um bate papo ufológico só de garotas, com as apoiadoras do podcast!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f8d5f6273b174b78d7dd99427', exclusivo: false },
        { numero: '133', titulo: '133 - ChupaCabras - Parte 2', descricao: 'Vamos voltar a falar do nosso intruso esporádico agressivo favorito, o chupacabras!!! Venham descobrir as histórias e teorias mais estranhas envolvendo esse ser!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fcc321304865dedf543383314', exclusivo: false },
        { numero: '134', titulo: '134 - Supernatural - Parte 1', descricao: 'Nesse episódio vamos começar a falar sobre uma série que foi uma grande inspiração para o podcast: Supernatural!!!!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fd3bae02bc83d354c28e3f1cb', exclusivo: false },
        { numero: '135', titulo: '135 - Os Casos de Voronezh', descricao: 'Ocorrido na cidade russa de Voronezh, esse caso quatro envolve várias crianças que contatam um alienígena de 3 olhos.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f6bd56275b22771f948e7beff', exclusivo: false },
        { numero: '136', titulo: '136 - Supernatural- Parte 2', descricao: 'Voltamos para caçar coisas, salvar pessoas e continuar o negócio da família na quarta e quinta temporadas de Supernatural .', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f55e2a5d25eef9a3c50403778', exclusivo: false },
        { numero: '137', titulo: '137 - Tipologia Alienígena - Classe Animália', descricao: 'Estamos começando uma série de 5 episódios sobre as diversas raças de seres extraterrestres citados em vários casos ufológicos. Usaremos como base, o livro Guia da Tipologia Extraterrestre, do pesquisador Thiago Ticchetti. E hoje, nosso papo será sobre a classe Animália !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fa9650a5542a6abebef3e3fa5', exclusivo: false },
        { numero: '138', titulo: '138 - Estranha Colheita - Parte 1', descricao: 'Nesse episódio vamos conhecer alguns dos mais bizarros e insólitos casos da ufologia, casos envolvendo mutilação e morte de pessoas !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f43f3a27809331a66a147ce6b', exclusivo: false },
        { numero: '139', titulo: '139 - Tipologia Alienígena - Classe Robótica', descricao: 'Chegamos ao segundo episódio da nossa série sobre os diversos seres citados no livro o Guia Tipologia Extraterrestre, do pesquisador Thiago Ticchetti. E hoje, nosso papo será sobre a classe Robótica !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f2a8d2bfa7034dde8eaf670f6', exclusivo: false },
        { numero: '140', titulo: '140 - O Fenômeno Poltergeist', descricao: 'Nesse episódio vamos falar do fenômeno Poltergeist e de outras coisas que podem (ou não) estar relacionadas.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fb5f1c5dfed6eb42bda73a246', exclusivo: false },
        { numero: '141', titulo: '141 - Tipologia Alienígena - Classe Exótica', descricao: 'Chegamos ao terceiro episódio da nossa série sobre os diversos seres citados no livro o Guia Tipologia Extraterrestre, do pesquisador Thiago Ticchetti, e dessa vez nosso papo será sobre os seres mais estranhos citados em alguns casos: hoje vamos falar da classe exótica!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f96f725b5e04fb1a0d30261dc', exclusivo: false },
        { numero: '142', titulo: '142 - O Mistério dos Sagrados Estigmas', descricao: 'Os chamados sagrados estigmas são feridas que apararecem em algumas pessoas e que evidenciam um contato com alguma entidade religiosa ( ou não !). E nesse episódio contamos com o grande Rafael Jacaúna para essa interessante conversa.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f6cdcca2bcaba23ceb7866041', exclusivo: false },
        { numero: '143', titulo: '143 - Tipologia Alienígena - Classe Humanoide - Parte 1', descricao: 'Chegamos ao quarto episódio da nossa série sobre os diversos seres citados no livro o Guia Tipologia Extraterrestre, do pesquisador Thiago Ticchetti, e dessa vez nosso papo será sobre os seres que se assemelham a nós, a tipologia Humanoide.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fa0352f5562d408596ad6402e', exclusivo: false },
        { numero: '144', titulo: '144 - Saga Star Wars', descricao: 'Aproveitando que o dia mundial de star wars se aproxima, hoje vamos falar saga espacial mais famosa do mundo!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fec77600f115cdd7347f125d0', exclusivo: false },
        { numero: '145', titulo: '145 - Tipologia Alienígena - Classe Humanoide - Parte 2', descricao: 'Chegamos ao último episódio da nossa série sobre os diversos seres citados no livro o Guia Tipologia Extraterrestre, do pesquisador Thiago Ticchetti, e nosso papo será sobre a segunda parte da tipologia Humanoide.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3e8971223ec26829dd76fd75', exclusivo: false },
        { numero: '146', titulo: '146 - Estranha Colheita - Parte 2', descricao: 'Nesse episódio vamos falar de mais alguns casos do livro estranha colheita , que envolvem mutilações e morte de pessoas', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fafc38c78f31c095f5672b4bb', exclusivo: false },
        { numero: '147', titulo: '147 - A Mensagem de Ashtar Sheran', descricao: 'Hoje vamos conversar com nosso ouvinte Ramaira, sobre uma figura muito polêmica na ufologia: Asthar Sheran.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f61bd00c53828603e2612368e', exclusivo: false },
        { numero: '148', titulo: '148 - Estranha Colheita - Parte 3', descricao: 'Nesse episódio encerramos a série sobre o livro estranha colheita com um dos casos mais misteriosos do brasil : O caso das máscaras de chumbo.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fb90438a2fefdb6044ca7581f', exclusivo: false },
        { numero: '149', titulo: '149 - Ufologia e Mistérios do Mato Grosso', descricao: 'Nesse episódio vamos conversar com o pesquisador Ataide Ferreira sobre a casuística ufológica do estado do Mato Grosso, onde foram encontradas possíveis evidencias materiais de uma antiga tecnologia alienígena.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc82348108340e1426f07a5c0', exclusivo: false },
        { numero: '150', titulo: '150 - Caso da Ilha João Donato', descricao: 'Nesse episódio vamos conversar com a pesquisadora Lala Barreto, que vai nos contar uma interessante história sobre a ilha João Donato, e falar sobre o caso Ummo que possuiu uma ligação interessante com o caso de Voronezh', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fef193683dd0622ce70fb5e0a', exclusivo: false },
        { numero: '151', titulo: '151 - Caso Quarouble', descricao: 'Um dos casos clássicos da Ufologia Mundial ocorreu em 10 de setembro de 1954 em Quarouble, na França. Na ocasião um disco voador pousou nas proximidades da residência de um vigia deixando vários vestígios físicos de sua passagem.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f335f39b256508979f6df3e10', exclusivo: false },
        { numero: '152', titulo: '152 - Lugares Míticos', descricao: 'Hoje vamos começar uma série de episódios sobre vários lugares considerados míticos ,e que povoam a imaginação da humanidade a muito tempo!!!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f895b73b16270f7e06333ac1c', exclusivo: false },
        { numero: '153', titulo: '153 - Caso Cash-Landrum', descricao: 'Um importante caso ufológico americano ocorrido em 29 de dezembro de 1980. UM OVNI foi visto sobrevoando a estrada acompanhado por helicópteros sem identificação. A proximidade com o OVNI produziu sequelas físicas nas testemunhas.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9e81db14729a03a0f1161706', exclusivo: false },
        { numero: '154', titulo: '154 - Teorias Sobre Tecnologia Alienígena - Parte1', descricao: 'Nesse episódio vamos teorizar sobre as tecnologias que já ouvimos falar em vários casos ufológicos.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f5ba92d0c89591415ddb59878', exclusivo: false },
        { numero: '155', titulo: '155 - Caso Délio', descricao: 'As experiências de abdução, sob olhar de alguém que nunca foi abduzido, podem soar surreais para uns, loucura para outros, ou ainda algo positivo e atraente para outros. Mas para os abduzidos, na imensa maioria das vezes é um processo difícil, doloroso e perturbador.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f05cd1223fa86db299f1d99fa', exclusivo: false },
        { numero: '156', titulo: '156 - As Múmias de Nazca', descricao: 'Nesse episódio vamos conversar sobre os resultados da investigação realizada sobre as mumias de 3 dedos encontradas no Peru, em 2017.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fcd9a31d2732d38b834746aed', exclusivo: false },
        { numero: '157', titulo: '157 - Osnis', descricao: 'A grande maioria dos relatos de avistamento ufológicos tem alguma relação com a água, ou estando próximos ou mesmo saindo de dentro de mares ou rios, e nesse episódio vamos falar obre alguns casos dos famosos OSNIs!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f05f0bed62aae3250156949fe', exclusivo: false },
        { numero: '158', titulo: '158 - Combustão Humana Espontânea', descricao: 'Incêndios estarrecedores que intrigam bombeiros, investigadores, e peritos, corpos incinerados a mais de 1300° Celsius e reduzidos a uma pilha de cinzas sem sinal de material ígneo. Esses são os fatos da pirocinesia, ou combustão espontânea, um dos fenômenos mais desconcertantes de natureza paranormal, e será o nosso assunto de hoje.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fee3b8f64796e799590f05259', exclusivo: false },
        { numero: '159', titulo: '159 - Caso Mirassol', descricao: 'O Caso Mirassol é um dos mais importantes da Ufologia Brasileira e talvez mundial. O protagonista é, Antônio Carlos Ferreira,  um vigia noturno da cidade de Mirassol, SP, e ele esteve envolvido em vários eventos de contato e abdução que deixaram numerosas evidências físicas de tais experiências.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fed44bbcdf1ddfb0ef0dd0313', exclusivo: false },
        { numero: '160', titulo: '160 - Pactos e Maldições', descricao: 'Nesse episódio vamos falar de pactos e maldições, quais as diferenças entre eles e citar alguns casos famosos.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc4cd20a52e1d5c7ff05765eb', exclusivo: false },
        { numero: '161', titulo: '161 - Anamnese de um Contatado', descricao: 'Implantes alienígenas são objetos físicos, muito pequenos com propriedades intrigantes, que são colocados no corpo de pessoas abduzidas por alienígenas. Sua função e funcionamento permanecem ainda, em grande parte, desconhecidos. Neste episódio você vai conhecer um caso brasileiro, muito interessante, repleto de evidências físicas e fisiológicas e corroboradas pela análise médica-científica de um implante.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3bf0f2ae1774da74b8072004', exclusivo: false },
        { numero: '162', titulo: '162 - A Abdução de Betty e Barney Hill', descricao: 'O Caso Barney e Betty Hill é um divisor de águas para a Ufologia Mundial, pois representou uma mudança radical na característica dos contatos entre humanos e os tripulantes dos misteriosos OVNIs.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ffe1332d921c23f8ae7099014', exclusivo: false },
        { numero: '163', titulo: '163 - Resíduos Extraterrestres', descricao: 'São vários os relatos associados a quedas de meteoros e aparições de OVNIs onde ocorrem o aparecimento de estranhas substâncias, que são encontradas por testemunhas nos locais dos acontecimentos. Os relatos vão desde uma gelatina transparente, de fios finíssimos que se evaporam no ar, misteriosas chuvas de cor vermelha e até mesmo doenças inexplicáveis e incuráveis.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f11e2da349f623c8279992c36', exclusivo: false },
        { numero: '164', titulo: '164 - O Homem Mariposa', descricao: 'Mothman, ou homem mariposa, é uma suposta criatura sobrenatural que teria aparecido em Charleston e Point Pleasant, entre novembro de 1966 e dezembro de 1967. Sua aparição é associada ao acontecimento de futuros desastres. Seria ele um criptóide ou um ser extraterrestre?', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f622f181f68ec15edb4db9391', exclusivo: false },
        { numero: '165', titulo: '165 - Nunca Foram Balões', descricao: 'No início de 2023, todos nós acompanhamos a história dos supostos balões atingidos por aviões nos EUA e Canadá. E segundo os próprios militares, não haviam registros desse objeto "destruido por eles". Mas na tarde do dia 23-09-24 o governo canadense liberou uma foto desse suposto balão.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f4a0e2f6f3900984c2b3b56d5', exclusivo: false },
        { numero: '166', titulo: '166 - O Horror Cósmico de H. P. Lovecraft', descricao: 'O Horror Cósmico é um subgênero literário de terror e ficção estranha que se caracteriza pelo terror do desconhecido e incompreensível. O nome vem do autor norte-americano H.P. Lovecraft , que cunhou o termo para denominar sua teoria estética. Seria tudo apenas ficção ou haveria algo de real nessas histórias?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fd341c389d6ac783ca32955d5', exclusivo: false },
        { numero: '167', titulo: '167 - Eram os Deuses Astronautas', descricao: 'Eram os Deuses Astronautas? é um livro do suíço Erich von Däniken, publicado em 1968, que defende a ideia que alguns achados arqueológicos, monumentos antigos, mapas e marcas em rochas são evidências de que , no passado , alienígenas estiveram no planeta e influenciaram nossa cultura e religião.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc14cd217d60927758c7f66d4', exclusivo: false },
        { numero: '168', titulo: '168 - O Caso do Capitão Abelha', descricao: 'A jornalista Adriana Borges, ou Dryca ,deu um relato que repercutiu nas redes sociais. Segundo a comunicadora, ela teve contato com um extraterrestre no ano de 1991, na cidade de Palmas.Então aperte o play e venha conhecer essa história!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f25879edb9935f055e6201be3', exclusivo: false },
        { numero: '169', titulo: '169 - Voltando a Varginha', descricao: 'Hoje vamos voltar a varginha, e vamos relembrar alguns detalhes desse fantástico caso brasileiro.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f4c936ad0b9dcd0ef78639c3c', exclusivo: false },
        { numero: '170', titulo: '170 - Nada Aconteceu', descricao: 'Nesse episódio que também vai virar uma HQ, vamos conhecer um caso ufológico inédito!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f01e2de6acb11e8d94d9a1d93', exclusivo: false },
        { numero: '171', titulo: '171 - Especial de Halloween 2', descricao: 'No nosso segundo episódio especial de halloween, vamos indicar vários documentários sobre ufologia!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fe60797c8b51feb4a1843ba99', exclusivo: false },
        { numero: '172', titulo: '172 - Chico Xavier e a Viagem a Saturno', descricao: 'Nesse episódio especial da nossa parceria com a editora tábula, nos vamos falar sobre o maior médium do mundo, o grande Chico Xavier.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f0c886fa104ea2135a432482a', exclusivo: false },
        { numero: '173', titulo: '173 - Os Novos Avistamentos em Cláudio', descricao: 'No episódio de hoje vamos bater um papo com o Lauro Miguel, que vai nos contar que os avistamentos na cidade de Cláudio ainda estão acontecendo, tendo inclusive um novo contato com seres luminosos!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f4d483198aaa1c56039efbf5e', exclusivo: false },
        { numero: '174', titulo: '174 - Relatos do Arquivo Fenomenum', descricao: 'Nesse episódio vamos ter alguns relatos ufológicos inéditos aqui no podcast, mas que estarão no nosso livro Relatos Alienígenas que será lançado em breve.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fce67b7f37349577413490f29', exclusivo: false },
        { numero: '175', titulo: '175 - A Nova Audiência pública sobre OVNIs no Congresso dos EUA', descricao: 'Nesse episódio, vamos falar da recente audiência pública sobre OVNIs no Congresso dos EUA, que ocorreu no dia 13 de novembro de 2024.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f00f8870a0fcf3ff13582b5cf', exclusivo: false },
        { numero: '176', titulo: '176 - Caso Onilson Pattero e o Chupa Cabras', descricao: 'Nesse episódio especial da editora tábula, vamos conhecer mais alguns casos que vão virar Hqs ufológicas!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fedebcbc480962c4b8309dec8', exclusivo: false },
        { numero: '177', titulo: '177 - Abdução em Manhattan', descricao: 'Linda Napolitano era uma típica dona de casa que vivia com seu marido na ilha de Manhattan, no coração de Nova York. Mas sua vida mudou completamente naquele 30 de novembro de 1989, quando ela alega ter sido abduzida de dentro do seu quarto em Manhattan. Nesse episódio vamos investigar se foi tudo uma mentira elaborada ou se seu caso é uma prova de vida alienígena.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f50eefd82e8f2ce167701c749', exclusivo: false },
        { numero: '178', titulo: '178 - Relatos Sobrenaturais - Parte 1', descricao: 'Venham conhecer alguns relatos sobrenaturais dos nossos ouvintes!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f423e4507de9d13c4437af89c', exclusivo: false },
        { numero: '179', titulo: '179 - O Gigante de Paty do Alferes', descricao: 'No episódio de hoje um caso estranho que ocorreu em 1977 em Paty do Alferes, interior do Rio de Janeiro. Um encontro insólito, seguido de abdução envolvendo um estranho e gigantesco ser e até mesmo uma realidade paralela! Então aperte o play e venha conhecer essa incrível história!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f445e733244bd50d018c53cc5', exclusivo: false },
        { numero: '180', titulo: '180 - Relatos Sobrenaturais - Parte 2', descricao: 'Venham conhecer mais relatos sobrenaturais enviados pelos nossos ouvintes! Então apertem o play e se preparem para a nota marrom lá em cima!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f57b6c43ad62011f58544a483', exclusivo: false },
        { numero: '181', titulo: '181 - Sorria - Voce está sendo monitorado', descricao: 'Por muitos anos ufólogos do mundo denunciaram a existência de um projeto altamente secreto destinado à escuta clandestina de ligações telefônicas, chamado Echelon. Com base nas poucas informações secretas vinda à público, o sistema Echelon poderia ter acesso à quase todos as ligações telefônicas feitas no mundo, à maioria das transmissões de fax, que era muito utilizado na época e depois, com o surgimento da Internet monitorando também os e-mails de pessoas de interesse da inteligência dos Estados Unidos', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f6bbc01b8e4da11d7f774b266', exclusivo: false },
        { numero: '182', titulo: '182 - Relatos Ufológicos - Parte 1', descricao: 'Nesse episódio vamos conhecer alguns relatos ufológicos enviados pelos nossos ouvintes! Então apertem o play e se preparem para a nota marrom lá em cima!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f3ea3999c018e246784ea5021', exclusivo: false },
        { numero: '183', titulo: '183 - O Ovni do Morenão', descricao: 'Na noite de 6 de março de 1982, durante a partida histórica entre Operário e Vasco, no Estádio Morenão, em Campo Grande (MS), um objeto voador não identificado foi avistado por uma multidão de torcedores ali presentes.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fb4d156e7153ef06d8d87d732', exclusivo: false },
        { numero: '184', titulo: '184 - A Nova Onda Ufológica Mundial', descricao: 'O que está acontecendo pelos céus de todo o mundo? Luzes misteriosas, que a mídia e os militares dizem ser drones, tem aparecido por todo o mundo, em maior frequência nos EUA e Reino Unido. Serão drones? se são drones, quem e porquê estão os enviando? E se não são, o que então é isso?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f8098e1a440a154b00b68836e', exclusivo: false },
        { numero: '185', titulo: '185 - A Crise dos Drovnis', descricao: 'Neste episódio, falaremos das últimas novidades sobre os chamados Drovnis e ver como tudo isso está mudando a discussão global sobre inteligências não humanas.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9e39b8fa76e04ed75585ff69', exclusivo: false },
        { numero: '186', titulo: '186 - Operação Prato - Parte 3', descricao: 'Episódio especial de natal, com informações novas da Operação Prato e sorteio de 7 livros para os ouvintes!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f1972e9bca845bc28bdc9b077', exclusivo: false },
        { numero: '187', titulo: '187 - Retrospectiva Ufológica 2024', descricao: 'Nesse episódio nós vamos comentar os eventos ufológicos mais marcantes desse ano de 2024!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f975b819f3afd19ab1ef9a55a', exclusivo: false },
        { numero: '188', titulo: '188 - Relatos Homem Mariposa', descricao: 'Mothman, ou homem mariposa, é uma suposta criatura sobrenatural que é muito avistada nos Estados Unidos. Mas não é só nos EUA que ele é avistado. Existem muitos relatos de avistamentos dele por todo o Brasil. Seria ele um criptóide ou um ser extraterrestre?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f2f2b073c56989846925b0b00', exclusivo: false },
        { numero: '189', titulo: '189 - Profetas e Profecias', descricao: 'Nostradamus talvez seja um dos mais famosos profetas da história, mas ele não foi o unico. Várias outras pessoas alegam ter o poder de prever eventos futuros. Como elas faziam essas previsões e será que ja acertaram alguma vez?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fc629d6ed54b87c48bf65f9a6', exclusivo: false },
        { numero: '190', titulo: '190 - A Entrevista Alienígena', descricao: 'Em 1996, um suposto ex funcionário do governo americano, apresentou ao mundo uma bizarra filmagem, que teria sido feita em 1991 nas instalações do projeto aquarius, de uma entrevista com um ser tipo Grey. Esse vídeo , considerado falso pela mídia mundial, foi recentemente pesquisado a fundo para um documéntario sobre ele.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f1bd09931d132821b505c9277', exclusivo: false },
        { numero: '191', titulo: '191 - Conspirações da Indústria Farmacêutica', descricao: 'Existem muitos mitos e fake news sobre a indústria farmacêutica, como a existência de uma vacina contra o câncer e de que a vacina da covid estava causando mal as pessoas. O que de fato é real nisso tudo?', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fea7e6589e6a2d00df39fdea9', exclusivo: false },
        { numero: '192', titulo: '192 - Abduções', descricao: 'Na ufologia existem uma série de fatos misteriosos envolvendo o sequestro de seres humanos pelos tripulantes destes ovnis. A Ufologia investiga avidamente esses relatos envolvendo abduções por alienígenas, que continuam acontecendo até os dias atuais.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fee25b97aa1266d1f5447fd86', exclusivo: false },
        { numero: '193', titulo: '193 - O Polémico Aleister Crowley', descricao: 'Venha conhecer o ocultista que invocou demônios, tentou ressuscitar deuses, conversou com espíritos, vivenciou vidas passadas e fundou uma religião.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fca47dc9d802a80ab6a1b409b', exclusivo: false },
        { numero: '194', titulo: '194 - Tiro, Porrada e Aliens', descricao: 'Hoje vamos falar de casos ufológicos que acabaram em agressão fisíca entre humanos e seres extraterrestres!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f0bd86122b8f27c1c79a2c9d1', exclusivo: false },
        { numero: '195', titulo: '195 - O Projeto Blue Bean', descricao: 'Com a grande onda recente de avistamentos ,supostamente ufológicos , muitas pessoas começaram a associar esses eventos ao infame projeto blue beam. E é sobre isso que vamos falar hoje: sobre o que é de fato o blue beam e se ele teria alguma coisa a ver com o que tem acontecido recentemente.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fd3d6dfb06b1ff94464ed3892', exclusivo: false },
        { numero: '196', titulo: '196 - Os Casos da Ilha Reunião', descricao: 'No meio do Oceano Índico existe uma pequena ilha, de colonização francesa que foi palco de extraordinários casos de contato ufológico com tripulantes de um OVNI, onde um desses encontros acabou resultando na cegueira temporária de um jovem, de 21 anos, morador da ilha.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fe3498bc559ddb1d6ac1c4a55', exclusivo: false },
        { numero: '197', titulo: '197 - Os Demônios da Goeta', descricao: 'Goétia nada mais é que um sistema evocatório de espíritos supostamente utilizado pelo rei Salomão e popularizado por MacGregor Mathers e Aleister Crowley, em 1904. São vários os objetivos para o uso da Goétia que vão desde aprendizado sobre línguas, aumento do magnetismo pessoal, conquista amorosa, ganhos financeiros e até sexo e proteção espiritual. As divindades da goetia são representadas por 72 demônios, e hoje vamos falar um pouco sobre eles !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f8334ae4cce27aff448cc1746', exclusivo: false },
        { numero: '198', titulo: '198 - Caso Mauricio', descricao: 'Hoje vamos conversar com nosso apoiador e ouvinte Maurício que procurou ajuda com a Hipnoterapeuta Bruna Bittencourt para tentar acessar memórias da infância quando diversos seres o buscavam para brincar no lado escuro da lua', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f915fb65766d9a79a48ebfdf3', exclusivo: false },
        { numero: '199', titulo: '199 - Vôos Misteriosos - Parte 1', descricao: 'A grande maioria dos acidentes aéreos que que aconteceram pelo mundo tem uma causa oficial, tais como interferencias climáticas, falha mêcanica ou falha humana. Mas e quando não existe uma explicação? Hoje vamos falar justamente desses casos envolvendo desaparecimento de aviões e pilotos que continuam, até hoje sem uma conclusão.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9c31e04f041a72b1c8141af0', exclusivo: false },
        { numero: '200', titulo: '200 - Bases Militares Secretas', descricao: 'Existem várias bases militares espalhadas pelo mundo, que durante décadas foram consideradas como conspirações, tendo sua existência sempre negada pelas autoridades. Mas,com o passar do tempo, suas localizações foram descobertas e deixaram de ser apenas mitos, para serem peças importantes no processo do desacobertamento ufológico !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f9278547bd401a4e37dc2a597', exclusivo: false },
        { numero: '201', titulo: '201 - A Ufologia e Sobrenatural', descricao: 'Nesse episódio vamos discutir sobre uma dúvida que surgiu no grupo dos apoiadores: Será que podemos considerar a ufologia um evento sobrenatural?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f0db8e3d66c3a23a5c3740213', exclusivo: false },
        { numero: '202', titulo: '202 - O Estranho Caso da Chuva de Detritos', descricao: 'Nesse episódio vamos falar de um caso pouco conhecido e que está entre os primeiros casos investigados na pesquisa ufológica !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f19188b63a5942159ad5055f5', exclusivo: false },
        { numero: '203', titulo: '203 - Mitos da Internet', descricao: 'Com o avanço da internet,que eram supostamente"secretas" começaram a ser divulgadas como verídicas, mas na verdade são somente histórias criadas por pessoas que pegaram alguma notícia verdadeira e expandiram um pouco demais seu conceito. E nessa nova séria que estreia nesse episódio vamos começar a conhecer a verdade por tras desses chamados mitos da internet,começando pelos mitos tecnológicos!', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fa8347a37b03e14b7c524a568', exclusivo: false },
        { numero: '204', titulo: '204 - Bruxaria', descricao: 'Para a maioria das pessoas, bruxaria é o uso de poderes sobrenaturais, magia, feitiçaria ou práticas de adivinhação e consulta a espíritos, e também um estilo de vida, uma filosofia, ou uma religião.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7f725126ecab59abef404f40', exclusivo: false },
        { numero: '205', titulo: '205 - O Relatorio Harald Malmgren', descricao: 'Nesse episódio vocês vão conhecer a história do ex-conselheiro presidencial americano Harald Malmgren que afirmou, em uma entrevista que chegou a tocar em alguns fragmentos de um objeto voador não identificado (UAP) recuperados durante um teste nuclear realizado nas Ilhas Marshall em 1962.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f916d8731abbbbdec64a7c969', exclusivo: false },
        { numero: '206', titulo: '206 - Relatos de Lobisomem 2', descricao: 'Nesse episódio vamos ouvir mais relatos de lobisomem enviados pelos nossos ouvintes!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fb3bf56619968ad3bc1902d9a', exclusivo: false },
        { numero: '207', titulo: '207 - Crianças Cósmicas', descricao: 'Nesse episódio vamos conversar com uma ouvinte do podcast , que teve alguns contatos ufológicos que foram extremamente traumatizantes ! ', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1ff066071c015b11ddddce9880', exclusivo: false },
        { numero: '208', titulo: '208 - O Vídeo do Drone', descricao: 'Recentemente, em um canal do Youtube sobre drones, foi publicado um vídeo ,onde acidentalmente foram filmados alguns ovnis. E hoje vamos conversar com o Wanderley Wanzam, que foi o responsável por esse incrível registro.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fdde11fabd56b12502efd5fbb', exclusivo: false },
        { numero: '209', titulo: '209 - Segredos da Maçonaria', descricao: 'Segundo a wikipédia, a maçonaria é uma sociedade secreta voltada ao aperfeiçoamento moral e espiritual de seus membros, promovendo valores como fraternidade, ética e filantropia por meio de rituais simbólicos e vestimentas representativas. Mas será só isso mesmo?', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f77bcf870b1189106d66740bd', exclusivo: false },
        { numero: '210', titulo: '210 - Operação Prato', descricao: 'Localizada no município de São Domingos do Capim, no nordeste do Pará, a Fazenda Jejú foi palco de um caso enigmático que mobilizou autoridades da Força Aérea Brasileira em dezembro de 1977, logo após o "suposto" fim da famosa operação prato.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1ff9669690fd10164471af2f99', exclusivo: false },
        { numero: '211', titulo: '211 - Mortes Bizarras', descricao: 'ATENÇÃO - CONTEÚDO SENSÍVEL - ESSE EPISÓDIO PODE CAUSAR GATILHO EM ALGUMAS PESSOAS. A verdade é que ninguém quer morrer, apesar de sabermos que esse é nosso inexorável fim. Talvez por medo desse desfecho final, muitas pessoas sequer cogitam em pensar na morte, mas alguns casos de morte são tão estranhos que chega a ser difícil não pensar sobre eles.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f7279220285db1473948b45bf', exclusivo: false },
        { numero: '212', titulo: '212 - Dogons', descricao: 'Na constelação do Cão Maior existe uma estrela, chamada Sirius B, e que segundo as antigas histórias contadas pelos dogons, os habitantes dessa estrela teriam aterrissado em nosso planeta e mantido contato com eles!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f78d82931eb0d90234baa4eda', exclusivo: false },
        { numero: '213', titulo: '213 - Vampiros', descricao: 'Vampiros são figuras lendárias encontradas em várias culturas, geralmente descritas como criaturas que se alimentam do sangue de seres vivos, muitas vezes humanos, para sobreviver.. e nesse episódio vamos falar sobre essses seres na cultura pop!', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f559596e876b54fe9f513e176', exclusivo: false },
        { numero: '214', titulo: '214 - Investigação Militar', descricao: 'Nesse episódio vamos conversar com um militar que participou de pesquisas ufológicas chefiadas pelo exército Brasileiro !', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fa554b2091f85d3ea9eae2246', exclusivo: false },
        { numero: '215', titulo: '215 - As Gravações do Cindacta', descricao: 'As Gravações do Cindacta', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1fa80e1bb61da7a10114984c72', exclusivo: false },
        { numero: '216', titulo: '216 - Caso Salyut 6', descricao: 'Um dos mais interessantes casos ufológicos vindos da antiga União Soviética, onde dois cosmonautas russos avistaram uma nave alienígena e seus tripulantes, durante uma missão espacial.', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f0ede8695443701ff04a3b5f7', exclusivo: false },
        { numero: '217', titulo: '217 - Ufoarqueologia', descricao: 'Ufoarqueologia é um termo que combina ufologia com arqueologia, explorando a possibilidade de que seres extraterrestres tenham interagido com civilizações antigas. Esta área busca evidências em registros históricos, artes rupestres e textos antigos que possam sugerir contato com outras formas de vida. ', capa: 'https://image-cdn-ak.spotifycdn.com/image/ab67656300005f1f59079e01ce57ef01e2a0bbc1', exclusivo: false },
        { numero: '218', titulo: '218 - Ooparts', descricao: 'OOPArt é uma sigla em inglês para Out of Place Artifact (ou, artefato fora de lugar). É uma terminologia usada para denominar um objeto de interesse histórico, arqueológico e/ou paleontológico que se encontra em um contexto não usual e aparentemente impossível o qual tende a desafiar a cronologia da história convencional.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1f039b8b0505b4fbdb5516e0bf', exclusivo: false },
        { numero: '219', titulo: '219 - Estranhos Visitantes', descricao: 'Nesse episódio vamos conhecer a história do nosso ouvinte Cláudio, que passou por algumas experiências de contato durante sua infância e procurou ajuda especializada para tentar lembrar o que realmente aconteceu.', capa: 'https://image-cdn-fa.spotifycdn.com/image/ab67656300005f1fe0b4ac7697a5863aec386fc6', exclusivo: false },
        { numero: '220', titulo: '220 - Os Ataques do 11 de Setembro', descricao: 'Em 11 de setembro de 2001 ,19 terroristas sequestraram quatro aviões comerciais americanos carregados de combustível que se dirigiam a vários destinos na Costa Oeste. No total, 2.977 pessoas foram mortas nos ataques terroristas na cidade de Nova York, Washington e nos arredores de Shanksville, Pensilvânia.', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/220%20-%20Os%20Ataques%20do%2011%20de%20Setembro.jpg', exclusivo: false },
        { numero: '221', titulo: '221 - Objetos Interestelares', descricao: 'Objetos interestelares são corpos celestes que não pertencem ao nosso Sistema Solar. Eles se formaram em torno de outras estrelas e vagaram pelo espaço até cruzarem, por acaso, o nosso caminho, e despertarem nossa curiosidade.', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/221%20-%20Objetos%20Interestelares.jpg', exclusivo: false },
        { numero: '222', titulo: '222 - A Biblioteca Macabra de Lovecraft', descricao: 'H. P. Lovecraft é um dos mais importantes autores de fantasia, ficção e terror da história.Conhecido como pai do horror cósmico, sua obra influencia cinema, literatura, cultura pop e quadrinhos,através das dezenas de contos e livros escritos por ele!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/222%20-%20A%20Biblioteca%20Macabra%20de%20Lovecraft.png', exclusivo: false },
        { numero: '223', titulo: '223 - O Fim do Mundo', descricao: 'O fim do mundo poderá acontecer por diversos fatores como um meteoro ou um evento carrington, crise climática, guerras ou até mesmo a inteligência artificial. As vária previsões científicas e as diversas interpretações religiosas apontam para diferentes cenários, que vão desde a extinção da vida humana até a renovação do planeta.', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/223%20-%20O%20Fim%20do%20Mundo.png', exclusivo: false },
        { numero: '224', titulo: '224 -  Notícias Ufológicas - Congresso EUA e Nasa', descricao: 'Nesse episódio vamos falar da recente audiência ufológica no congresso sos EUA , e também e sobre uma "recente" descoberta divulgada pela nasa.Então puxe uma cadeira e venha conversar com a gente !', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/224%20-%20%20Not%C3%ADcias%20Ufol%C3%B3gicas%20-%20Congresso%20EUA%20e%20Nasa.png', exclusivo: false },
        { numero: '225', titulo: '225 - Audiência Ufológica na Câmara dos Deputados', descricao: 'Nesse episódio vamos falar da recente audiência ufológica na câmara dos deputados que foi realizada em brasília, no dia 16 de setembro.Então puxe uma cadeira e venha conversar com a gente !', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/225%20-%20Audi%C3%AAncia%20Ufol%C3%B3gica%20na%20C%C3%A2mara%20dos%20Deputados.jpeg', exclusivo: false },
        { numero: '226', titulo: '226 - Reliquias Sagradas - O Sudário de Turim', descricao: 'O Santo Sudário é um manto de linho que, para muitos, envolveu o corpo de Jesus Cristo após sua crucificação e que está guardado na Catedral de Turim, na Itália. Embora sua autenticidade seja debatida, o Sudário é considerado uma relíquia de grande importância para os católicos', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/226%20-%20Reliquias%20Sagradas%20-%20O%20Sud%C3%A1rio%20de%20Turim.jpg', exclusivo: false },
        { numero: '227', titulo: '227 - Rituais, Invocações e Extraterrestres', descricao: 'No episódio de hoje, vamos conversar com o pesquisador Rony Vernet e conhecer as interessantes e polémicas pesquisas que ele tem feito no campo da ufologia', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/227%20-%20Rituais%2C%20Invoca%C3%A7%C3%B5es%20e%20Extraterrestres.jpeg', exclusivo: false },
        { numero: '228', titulo: '228 - Serfo e as Cirurgias Espaciais', descricao: 'No episódio de hoje, vamos conversar com o fundador da ong Serfo, e saber detahes do trabalho feito por eles, incluindo as chamadas cirurgias espaciais. Peço a todos que divulguem e façam esse episódio chegar até as pessoas que estão passando por algum problema de saúde.Então aperte o play e venha de mente e coração abertos conhecer mais sobre esse fantástico trabalho!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/228%20-%20Serfo%20e%20as%20Cirurgias%20Espaciais.jpg', exclusivo: false },
        { numero: '229', titulo: '229 - A Magia Sexual e os Sonhos Roubados', descricao: 'Nesse episodio vamos conversar com a Bruxa Luiza Lemos que vai nos contar sobre magias envolvendo o sexo e os sonhos, e falar também do seu novo projeto na Editora tábula!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/229%20-%20A%20Magia%20Sexual%20e%20os%20Sonhos%20Roubados.jpg', exclusivo: false },
        { numero: '230', titulo: '230 - Especial de Halloween 3 - Zumbis', descricao: 'No nosso terceiro episódio especial de halloween vamos falar sobre os zumbis, no folclore e na cultura pop. Então aperte o play e venha fugir com a gente da horda de mortos vivos!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/230%20-%20Especial%20de%20Halloween%203%20-%20Zumbis.jpeg', exclusivo: false },
        { numero: '231', titulo: '231 - Os Ovnis Na Mira do Exército', descricao: 'Apesar de sempre negar, o exército brasileiro esteve envolvido em vários incidente ufológicos, e nesse episódio vamos contar alguns que não são conhecidos pela maioria das pessoas! ', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/231%20-%20Os%20Ovnis%20Na%20Mira%20do%20Ex%C3%A9rcito.jpg', exclusivo: false },
        { numero: '232', titulo: '232 - A Lança do Destino', descricao: 'Continuando nossa série sobre as reliquias sagradas , hoje vamos falar da  Lança do Destino ,que teria sido a arma usada pelo centurião romano Longino para perfurar Jesus Cristo durante a crucificação, e ela é cercada por lendas , de que possui poder divino, capaz de influenciar o destino de quem a possui. Será mesmo? ', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/232%20-%20A%20Lan%C3%A7a%20do%20Destino.jpeg', exclusivo: false },
        { numero: '233', titulo: '233 - Caso Santa Isabel', descricao: 'Na década de 1970, em meio à modernização industrial da Argentina, uma série de eventos misteriosos, envolvendo luzes estranhas, aparições e encontros com seres humanoides,desencadeou uma das mais intrigantes investigações ufológicas da América do Sul, marcada por relatos detalhados, efeitos físicos inexplicáveis e coincidências perturbadoras entre as testemunhas. ', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/233%20-%20Caso%20Santa%20Isabel.jpg', exclusivo: false },
        { numero: '234', titulo: '234 - O Polêmico Caso Paciência', descricao: 'Hoje vamos falar de um polêmico caso com diversos efeitos fisiológicos na testemunha, envolvendo criaturas de aspecto bizarro, ocorrido em Paciência, Rio de Janeiro.', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/234%20-%20O%20Pol%C3%AAmico%20Caso%20Paci%C3%AAncia.jpeg', exclusivo: false },
        { numero: '235', titulo: '235 - SuperAlienígenas', descricao: 'Os super-heróis extraterrestres têm nos fascinado por décadas. Vindos de planetas distantes,nos fazem refletir sobre a diversidade cósmica e as possibilidades infinitas de vida inteligente no universo. E hoje vamos falar de alguns dos superalienígenas mais famosos da cultura pop!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/235%20-%20SuperAlien%C3%ADgenas.jpg', exclusivo: false },
        { numero: '236', titulo: '236 - Relatos Ufológicos - Parte 2', descricao: 'No episódio de hoje, vamos trazer mais 8 relatos ufológicos de ouvintes do podcast! Então aperte o play e venham ouvir essas incriveis histórias !', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/236%20-%20Relatos%20Ufol%C3%B3gicos%20-%20Parte%202.jpg', exclusivo: false },
        { numero: '237', titulo: '237 - Notícias Bizarras - Parte 1', descricao: 'ATENÇÃO - CONTEÚDO SENSÍVEL - ESSE EPISÓDIO PODE CAUSAR GATILHO EM ALGUMAS PESSOAS Hoje vamos celebrar os 20 anos do Blog Mundo Gump com uma coleção de notícias bizarras, publicadas pelo mundo afora!', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/237%20-%20Not%C3%ADcias%20Bizarras%20-%20Parte%201.jpg', exclusivo: false },
        { numero: '238', titulo: '238 - Especial de Natal - A Revista UFO', descricao: 'No nosso episódio especial de natal, vamos falar sobre a revista UFO, que foi uma das maiores inspirações para a maioria dos ufologos do brasil! Então aperte o play e venha relembrar essa história ! RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Capas/238%20-%20Especial%20de%20Natal%20-%20A%20Revista%20UFO.jpg', exclusivo: false },
        { numero: '239', titulo: '239 - Retrospectiva Ufológica 2025', descricao: 'Nesse episódio vamos comentar as principais notícias ufológicas que aconteceram em 2025.Então aperte o play e venha relembrar tudo com a gente ! RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO', capa: 'https://pub-9083d14195514a9f89133574d545efc9.r2.dev/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/239%20-%20Retrospectiva%20Ufol%C3%B3gica%202025.jpg', exclusivo: false },
        { numero: '240', titulo: '240 - Relatos Ufológicos - Parte 3', descricao: 'Nesse No episódio de hoje, vamos trazer mais 6 relatos ufológicos de ouvintes do podcast!Então aperte o play e venham ouvir essas incriveis histórias !RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO', capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/240%20-%20Relatos%20Ufol%C3%B3gicos%20-%20Parte%203.jpg', exclusivo: false },
        { numero: '241', titulo: '241 - O Tabuleiro Ouija', descricao: 'O tabuleiro Ouija, criado como um simples jogo de salão durante o auge do espiritismo, tornou-se ao longo do tempo um ícone cercado de mistério, medo e polêmicas — visto por uns como mera curiosidade, por outros como um perigoso portal espiritual , e continua despertando o fascínio e a curiosidade sobre a possibilidade de se comunicar com o além.Então aperte o play e venha tentar falar com os mortos!RECOMENDAMOS ESCUTAR COM FONES DE OUVIDO', capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/241%20-%20O%20Tabuleiro%20Ouija.jpg', exclusivo: false },
        { numero: '242', titulo: '242 - Leonardo Da Vinci', descricao: 'Leonardo da Vinci foi um dos maiores gênios do Renascimento. Artista, inventor e cientista, ele ficou famoso por obras como Mona Lisa e A Última Ceia, além de seus estudos avançados sobre anatomia, engenharia e natureza. Sua curiosidade e criatividade atravessaram séculos, fazendo dele um símbolo da união entre arte e ciência.', capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/00e6df9e-8c07-483f-bc4d-c3323f31490c.jpeg', exclusivo: false },
        { numero: '243', titulo: '243 - TCI - Parte 3', descricao: 'Nesse episódio vamos ter uma nova conversa com a Pesquisadora Sonia Rinaldi que vai nos atualizar sobre as suas recentes descobertas no campo da transcomunicação Instrumental. Apertem o play e se preparem para perder o sono! É RECOMENDADO OUVIR COM FONES DE OUVIDO', capa: 'https://arquivos.acreditesequiserpodcast.com.br/Capas/243%20-%20TCI%20-%20Parte%203.jpg',audio: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/243%20-%20TCI%20-%20Parte%203.mp3.mpeg',exclusivo: false },
        { numero: '244', titulo: '244 - OVNIs e Sua Mania de Grandeza', descricao: 'Nesse episódio, vamos falar sobre alguns casos ufológicos envolvendo naves gigantescas, com algumas chegando a ser maiores que uma pequena cidade.Apertem o play e se preparem para essa grande história !', capa: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/244%20-%20OVNIs%20e%20Sua%20Mania%20de%20Grandeza.jpeg',audio: 'https://arquivos.acreditesequiserpodcast.com.br/Epis%C3%B3dios/201%20-%20ate%20o%20ultimo/244%20-%20OVNIs%20e%20Sua%20Mania%20de%20Grandeza.mp3.mpeg',exclusivo: false },

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
                   },
                   {
                       numero: '12',
                       titulo: '12 - Exclusivo - Os Discos de Dropa',
                       descricao: 'Você já ouviu falar dos Dropas? Não? Então se prepara, porque essa história parece saída direto de um filme de ficção científica. Os misteriosos discos de pedra encontrados na região de Baian-Kara-Ula, na China, nos anos 1930, ainda hoje deixam muita gente de cabelo em pé. Dizem que são de origem extraterrestre,e que ninguém sabe exatamente onde estão agora.',
                       exclusivo: true
                   }
               ];

            const todosEpisodios = episodiosBrutos.map(criarEpisodio);


            // =================================================================================
            // PARTE 2: LÓGICA DA PÁGINA DE EPISÓDIOS
            // =================================================================================
            document.addEventListener('DOMContentLoaded', function() {

                // -----------------------------------------------------------------------------
                // 1. SELEÇÃO DE ELEMENTOS (VARIÁVEIS)
                // -----------------------------------------------------------------------------
                const gridExclusivos = document.getElementById('grid-exclusivos');
                const gridPublicos = document.getElementById('grid-publicos');

                // Se os grids principais não existirem, o script para aqui.
                if (!gridPublicos && !gridExclusivos) {
                    return;
                }

                const buscaInput = document.getElementById('busca-episodio');
                const contadorEpisodios = document.getElementById('contador-episodios');
                const btnAnterior = document.getElementById('anterior');
                const btnProximo = document.getElementById('proximo');
                const infoPagina = document.getElementById('info-pagina');

                // Elementos do novo filtro
                const tipoFiltroRadios = document.querySelectorAll('input[name="tipoEpisodio"]');
                const secaoExclusivos = gridExclusivos.closest('.secao-episodios');
                const secaoPublicos = gridPublicos.closest('.secao-episodios');
                const paginacaoContainer = document.querySelector('.paginacao');

                // Variáveis de estado
                let episodiosFiltrados = [...todosEpisodios];
                let paginaAtual = 1;
                const episodiosPorPagina = 12;

                // -----------------------------------------------------------------------------
                // 2. DEFINIÇÃO DAS FUNÇÕES
                // -----------------------------------------------------------------------------

                function renderizarGrid(lista, elementoGrid) {
                    if (!elementoGrid) return;
                    elementoGrid.innerHTML = '';
                    lista.forEach(ep => {
                        const isExclusivo = ep.exclusivo;
                        // Se for streaming, adicionamos uma classe extra para estilizar se quiser
                        const cardClass = `episodio-card ${isExclusivo ? 'exclusivo' : ''} ${ep.somenteStreaming ? 'streaming-only' : ''}`;

                        // Badge dinâmico: Prioriza "Streaming" se for o caso
                        let badgeHTML = '';
                        if (ep.somenteStreaming) {
                            badgeHTML = '<div class="card-badge streaming">Somente Streaming</div>';
                        } else if (isExclusivo) {
                            badgeHTML = '<div class="card-badge">Exclusivo</div>';
                        }

                        // Lógica do botão de Download: se for somenteStreaming, o link não aparece
                        const downloadBtnHTML = ep.somenteStreaming
                            ? ''
                            : `<a href="${ep.download}" class="btn-card btn-icon btn-download" download title="Fazer Download">
                                    <i class="fas fa-download"></i>
                               </a>`;

                        const episodioCard = document.createElement('div');
                        episodioCard.className = cardClass;
                        episodioCard.innerHTML = `
                            ${badgeHTML}
                            <div class="card-image-container">
                                <img src="${ep.capa}" alt="Capa do Episódio ${ep.numero}" class="card-cover">
                            </div>
                            <div class="card-body">
                                <span class="ep-number">${ep.somenteStreaming ? 'ESPECIAL' : '#' + ep.numero}</span>
                                <h3 class="ep-title">${ep.titulo}</h3>
                                <p class="ep-description">${ep.descricao || 'Descrição não disponível.'}</p>
                                <div class="card-actions">
                                    <button class="btn-card btn-play" data-audio-src="${ep.audio}">
                                        <i class="fas fa-play"></i> Ouvir
                                    </button>
                                    ${downloadBtnHTML}
                                </div>
                                <div class="card-player-wrapper"></div>
                            </div>
                        `;
                        elementoGrid.appendChild(episodioCard);
                    });
                    adicionarEventListenersPlayer();
                }

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

                    // Para qualquer outro player que estiver tocando
                    document.querySelectorAll('.card-player-wrapper').forEach(wrapper => {
                        if (wrapper !== playerWrapper && wrapper.innerHTML !== '') {
                            wrapper.innerHTML = '';
                            wrapper.closest('.card-body').querySelector('.card-actions').style.display = 'flex';
                        }
                    });

                    // Mostra o player no card atual
                    actionsWrapper.style.display = 'none';
                    playerWrapper.innerHTML = `
                        <audio controls autoplay style="width:100%; height: 40px;">
                            <source src="${audioUrl}" type="audio/mpeg">
                            Seu navegador não suporta o elemento de áudio.
                        </audio>
                    `;
                }

                function filtrarEpisodios() {
                    const termo = buscaInput.value.toLowerCase();
                    const tipoSelecionado = document.querySelector('input[name="tipoEpisodio"]:checked').value;
                    let episodiosBase = todosEpisodios;

                    if (tipoSelecionado === 'publicos') {
                        episodiosBase = todosEpisodios.filter(ep => !ep.exclusivo);
                    } else if (tipoSelecionado === 'exclusivos') {
                        episodiosBase = todosEpisodios.filter(ep => ep.exclusivo);
                    }

                    episodiosFiltrados = episodiosBase.filter(ep =>
                        ep.titulo.toLowerCase().includes(termo) || ep.numero.includes(termo)
                    );

                    paginaAtual = 1;
                    atualizarPagina();
                }

                function atualizarPagina() {
                    const tipoSelecionado = document.querySelector('input[name="tipoEpisodio"]:checked').value;

                    // Ajuste da visibilidade das seções
                    secaoExclusivos.style.display = (tipoSelecionado === 'publicos') ? 'none' : 'block';
                    secaoPublicos.style.display = (tipoSelecionado === 'exclusivos') ? 'none' : 'block';

                    // Lógica da paginação (considerando apenas os públicos)
                    const episodiosPublicosBase = episodiosFiltrados.filter(ep => !ep.exclusivo && !ep.somenteStreaming);
                    paginacaoContainer.style.display = (tipoSelecionado === 'exclusivos') || episodiosPublicosBase.length <= episodiosPorPagina ? 'none' : 'flex';

                    // 1. SEPARAR EPISÓDIOS
                    // Exclusivos agora incluem os "Somente Streaming"
                    let episodiosExclusivos = episodiosFiltrados.filter(ep => ep.exclusivo || ep.somenteStreaming);
                    let episodiosPublicos = [...episodiosPublicosBase];

                    // 2. ORDENAÇÃO DOS EXCLUSIVOS (Especiais primeiro)
                    // Primeiro invertemos para manter a ordem cronológica inversa (mais novos primeiro)
                    episodiosExclusivos.reverse();

                    // Depois movemos os "somenteStreaming" para o topo absoluto
                    episodiosExclusivos.sort((a, b) => {
                        if (a.somenteStreaming && !b.somenteStreaming) return -1;
                        if (!a.somenteStreaming && b.somenteStreaming) return 1;
                        return 0;
                    });

                    // 3. ORDENAÇÃO DOS PÚBLICOS
                    episodiosPublicos.reverse();

                    // 4. PAGINAÇÃO (Apenas para os públicos)
                    const totalPaginas = Math.ceil(episodiosPublicos.length / episodiosPorPagina);
                    paginaAtual = Math.max(1, Math.min(paginaAtual, totalPaginas || 1));
                    const inicio = (paginaAtual - 1) * episodiosPorPagina;
                    const fim = inicio + episodiosPorPagina;
                    const publicosPaginados = episodiosPublicos.slice(inicio, fim);

                    // 5. RENDERIZAÇÃO
                    renderizarGrid(episodiosExclusivos, gridExclusivos);
                    renderizarGrid(publicosPaginados, gridPublicos);

                    // 6. ATUALIZAÇÃO DE INTERFACE (CONTADORES)
                    if (contadorEpisodios) {
                        contadorEpisodios.textContent = `Mostrando ${episodiosFiltrados.length} de ${todosEpisodios.length} episódios encontrados`;
                    }
                    if (infoPagina) {
                        infoPagina.textContent = `Página ${paginaAtual} de ${totalPaginas || 1}`;
                    }
                    if (btnAnterior) {
                        btnAnterior.disabled = paginaAtual === 1;
                    }
                    if (btnProximo) {
                        btnProximo.disabled = paginaAtual === (totalPaginas || 1);
                    }
                }

                // -----------------------------------------------------------------------------
                // 3. ADIÇÃO DOS EVENT LISTENERS
                // -----------------------------------------------------------------------------
                if (buscaInput) {
                    buscaInput.addEventListener('input', filtrarEpisodios);
                }

                if (btnAnterior) {
                    btnAnterior.addEventListener('click', () => {
                        if (paginaAtual > 1) {
                            paginaAtual--;
                            atualizarPagina();
                        }
                    });
                }

                if (btnProximo) {
                    btnProximo.addEventListener('click', () => {
                        const totalPaginas = Math.ceil(episodiosFiltrados.filter(ep => !ep.exclusivo).length / episodiosPorPagina);
                        if (paginaAtual < totalPaginas) {
                            paginaAtual++;
                            atualizarPagina();
                        }
                    });
                }

                tipoFiltroRadios.forEach(radio => {
                    radio.addEventListener('change', filtrarEpisodios);
                });

                // -----------------------------------------------------------------------------
                // 4. CHAMADA INICIAL PARA RENDERIZAR A PÁGINA
                // -----------------------------------------------------------------------------
                atualizarPagina();
            });