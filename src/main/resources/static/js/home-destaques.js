document.addEventListener('DOMContentLoaded', () => {
    // A variável `todosEpisodios` está disponível globalmente porque incluímos o arquivo 'episodios.js' antes deste.

    // 1. Filtra para pegar apenas os episódios exclusivos.
    const episodiosExclusivos = todosEpisodios.filter(ep => ep.exclusivo);

    // 2. Embaralha a lista de episódios exclusivos para garantir aleatoriedade.
    // (Algoritmo de embaralhamento Fisher-Yates)
    for (let i = episodiosExclusivos.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [episodiosExclusivos[i], episodiosExclusivos[j]] = [episodiosExclusivos[j], episodiosExclusivos[i]];
    }

    // 3. Pega os 3 primeiros episódios da lista embaralhada.
    const episodiosEmDestaque = episodiosExclusivos.slice(0, 3);

    // 4. Encontra o contêiner no HTML onde os cards serão inseridos.
    const gridContainer = document.getElementById('destaques-grid');

    if (gridContainer) {
        // Limpa qualquer conteúdo placeholder que possa existir.
        gridContainer.innerHTML = '';

        // 5. Cria o HTML para cada um dos 3 cards e os insere na página.
        episodiosEmDestaque.forEach(ep => {
            const cardHTML = `
                <div class="episode-card">
                    <div class="card-image-container">
                         <img src="${ep.capa}" alt="Capa do Episódio: ${ep.titulo}" class="card-cover">
                    </div>
                    <div class="card-content">
                        <h3>${ep.titulo}</h3>
                        <p>${ep.descricao}</p>
                        <span class="exclusive-tag">Exclusivo para Assinantes</span>
                    </div>
                </div>
            `;
            // Adiciona o novo card ao grid
            gridContainer.innerHTML += cardHTML;
        });
    }
});