# Plano de desenvolvimento — Reino

## Objetivo

Transformar o aplicativo atual em uma ficha digital de uso rápido durante as sessões, com:

- informações fáceis de localizar e ler;
- busca global por conteúdo da ficha;
- importação e atualização dos dados a partir do arquivo XLSX;
- funcionamento local mesmo sem internet;
- separação entre os dados permanentes do personagem e os valores temporários da sessão.

## Princípios do projeto

1. A interface não deve conhecer células como `L10` ou `CJ14`.
2. A planilha é uma fonte de importação, não o banco de dados do aplicativo.
3. Os dados importados devem ser convertidos para modelos próprios do domínio.
4. Vida, fadiga, mana e anotações da sessão não devem ser apagados por uma sincronização.
5. A primeira versão deve suportar o modelo da ficha atual antes de tentar aceitar qualquer planilha.
6. Cada etapa precisa terminar com algo utilizável e testável no celular.

## Arquitetura proposta

```text
Arquivo XLSX
    ↓
Leitor de planilha
    ↓
Mapeamento da versão da ficha
    ↓
CharacterData
    ↓
Banco local
    ↓
ViewModel
    ↓
Telas e busca
```

### Camadas

- `domain`: modelos de personagem, regras e contratos de repositório.
- `data/importer`: leitura do XLSX e conversão das células.
- `data/local`: persistência local com Room.
- `presentation`: estado das telas, busca e componentes Compose.
- `framework`: seleção de arquivo e integrações externas.

## Etapa 1 — Preparação e estabilização

### Objetivo

Preparar o projeto para receber dados reais sem carregar problemas da implementação antiga.

### Passos

- [ ] **Passo 1.1:** Executar o projeto atual e registrar o comportamento das telas.
- [x] **Passo 1.2:** Validar e corrigir textos com codificação quebrada, como `Raça`, `Perícias` e `Cabeça`.
- [x] **Passo 1.3:** Remover imports, dependências e componentes duplicados ou não utilizados.
- [x] **Passo 1.4:** Criar um modelo de domínio independente dos componentes da interface.
- [x] **Passo 1.5:** Definir um identificador e uma versão para o formato da ficha, como `REINO_V1`.
- [x] **Passo 1.6:** Registrar os campos e células utilizados na primeira importação.
- [x] **Passo 1.7:** Criar dados esperados de Syrio para serem usados nos testes.
- [x] **Passo 1.8:** Adicionar testes para nome, ST, DX, IQ, HT, vida, mana e fadiga.

### Resultado esperado

- O projeto compila.
- Nenhum texto visível apresenta caracteres quebrados.
- Existe um teste com os valores esperados de nome, ST, DX, IQ e HT.

## Etapa 2 — Importação básica do XLSX

### Objetivo

Permitir que o usuário selecione uma ficha XLSX e visualize os dados básicos encontrados.

### Passos

- [x] **Passo 2.1:** Avaliar e adicionar um leitor Android capaz de ler `.xlsx`. O leitor usa ZIP/XML nativos para evitar dependências JVM incompatíveis com Android.
- [x] **Passo 2.2:** Criar o contrato `CharacterSheetImporter`.
- [x] **Passo 2.3:** Criar `ReinoSheetV1Mapper` com os endereços da ficha atual.
- [x] **Passo 2.4:** Importar nome, jogador, raça, reino, idade, altura e peso.
- [x] **Passo 2.5:** Importar ST, DX, IQ e HT.
- [x] **Passo 2.6:** Importar vida, fadiga, mana, vontade e percepção.
- [x] **Passo 2.7:** Importar velocidade, deslocamento, carga e esquiva.
- [x] **Passo 2.8:** Adicionar o seletor de documentos do Android.
- [x] **Passo 2.9:** Validar se o arquivo contém as abas e os campos esperados.
- [x] **Passo 2.10:** Criar mensagens para arquivo inválido, ficha desconhecida e campos ausentes.
- [x] **Passo 2.11:** Exibir uma prévia dos dados antes de confirmar.
- [x] **Passo 2.12:** Criar testes de importação usando `FichaSyrio.xlsx`.

### Tratamento de erros

- arquivo inválido;
- ficha de versão desconhecida;
- aba ausente;
- campo obrigatório vazio;
- célula com tipo inesperado;
- falha de leitura.

### Resultado esperado

- O usuário seleciona `FichaSyrio.xlsx`.
- O aplicativo mostra os atributos encontrados.
- Erros de leitura são apresentados de maneira compreensível.

## Etapa 3 — Persistência local

### Objetivo

Manter a ficha disponível sem internet ou acesso permanente ao arquivo XLSX.

### Passos

- [x] **Passo 3.1:** Adicionar Room e configurar o banco local.
- [x] **Passo 3.2:** Criar a entidade principal do personagem.
- [x] **Passo 3.3:** Criar DAOs para inserir, consultar, atualizar e excluir personagens.
- [x] **Passo 3.4:** Criar `CharacterRepository`.
- [x] **Passo 3.5:** Salvar nome do arquivo, versão da ficha e data da importação.
- [x] **Passo 3.6:** Salvar os dados importados após a confirmação da prévia.
- [x] **Passo 3.7:** Carregar o personagem salvo ao iniciar o aplicativo.
- [x] **Passo 3.8:** Separar vida, fadiga, mana e anotações atuais dos valores permanentes.
- [x] **Passo 3.9:** Substituir `SyrioAugustoModel` pelo repositório no `CharacterViewModel`.
- [x] **Passo 3.10:** Fazer `CharacterScreen` observar o estado do `CharacterViewModel`.
- [x] **Passo 3.11:** Criar estados de carregamento, sucesso, vazio e erro.

### Dados permanentes

- atributos;
- perícias;
- vantagens e desvantagens;
- equipamentos;
- defesas;
- magias;
- valores máximos.

### Dados temporários

- vida atual;
- fadiga atual;
- mana atual;
- condições;
- anotações da sessão.

### Resultado esperado

- O aplicativo funciona sem o arquivo aberto e sem internet.
- A ficha continua disponível depois de fechar e abrir o aplicativo.
- Uma nova importação não apaga os valores temporários da sessão.

### Melhorias após validação

- [x] Adicionar exclusão de ficha salva com confirmação.
- [x] Remover automaticamente os dados relacionados ao excluir a ficha.

## Etapa 4 — Importação completa da ficha

### Objetivo

Importar todas as áreas necessárias para substituir o preenchimento manual.

### Passos

- [x] **Passo 4.1:** Importar perícias.
- [x] **Passo 4.2:** Importar vantagens e raça.
- [x] **Passo 4.3:** Importar desvantagens e peculiaridades.
- [x] **Passo 4.4:** Importar modificadores de reação.
- [x] **Passo 4.5:** Importar defesas e proteções por parte do corpo.
- [x] **Passo 4.6:** Importar armas de combate corpo a corpo.
- [x] **Passo 4.7:** Importar armas de longo alcance.
- [x] **Passo 4.8:** Importar inventário e dinheiro.
- [x] **Passo 4.9:** Importar armaduras e itens mágicos.
- [x] **Passo 4.10:** Importar o grimório.
- [x] **Passo 4.11:** Importar o resumo de pontos.
- [x] **Passo 4.12:** Persistir as novas listas. Perícias usam entidade relacionada; o domínio completo é armazenado em JSON versionado para as demais estruturas.
- [x] **Passo 4.13:** Ignorar linhas vazias e fórmulas sem resultado útil.
- [x] **Passo 4.14:** Criar testes para as categorias importadas.

### Estratégia para fórmulas

Na primeira versão, o aplicativo deve ler o resultado já calculado armazenado no XLSX. As regras realmente necessárias durante a sessão podem ser implementadas gradualmente em Kotlin.

Não é necessário recriar imediatamente todas as fórmulas da planilha.

### Resultado esperado

- Os dados exibidos no aplicativo correspondem aos valores da ficha.
- Listas vazias não geram cartões ou espaços desnecessários.
- Cada categoria possui testes de importação.

### Correções após validação

- [x] Corrigir a renderização de perícias dentro do modal.
- [x] Atualizar a ficha existente ao reimportar o mesmo personagem.
- [x] Preservar vida, fadiga, mana e anotações durante a reimportação.
- [x] Testar a persistência completa das categorias por JSON.

## Etapa 5 — Novo visual da ficha

### Objetivo

Facilitar a consulta das informações mais usadas durante uma sessão.

### Passos

- [x] **Passo 5.1:** Criar um esboço da nova tela de resumo.
- [x] **Passo 5.2:** Definir cores, tipografia, espaçamento e formatos de cartões.
- [x] **Passo 5.3:** Criar cabeçalho com nome, imagem, raça e reino.
- [x] **Passo 5.4:** Destacar vida, fadiga e mana com controles rápidos.
- [x] **Passo 5.5:** Exibir ST, DX, IQ e HT em um bloco compacto.
- [x] **Passo 5.6:** Exibir esquiva, aparar, bloqueio, deslocamento e carga atual.
- [x] **Passo 5.7:** Criar as categorias Resumo, Combate, Perícias, Características, Equipamentos, Magias e Anotações.
- [x] **Passo 5.8:** Implementar navegação inferior para as áreas principais.
- [x] **Passo 5.9:** Criar uma área “Mais” para categorias secundárias.
- [x] **Passo 5.10:** Trocar listas longas por `LazyColumn`.
- [x] **Passo 5.11:** Permitir expandir itens para visualizar descrições.
- [x] **Passo 5.12:** Criar estados visuais para listas vazias.
- [x] **Passo 5.13:** Validar tema claro e escuro.
- [x] **Passo 5.14:** Validar fontes maiores e áreas de toque com pelo menos 48 dp.

### Diretrizes visuais

- Evitar cartões dentro de cartões.
- Usar cor para indicar estado, não apenas decoração.
- Manter números importantes maiores que seus rótulos.
- Exibir detalhes somente quando solicitados.
- Usar `LazyColumn` nas listas longas.
- Permitir expandir itens para ver descrição e regras.
- Oferecer tema claro e escuro.
- Usar tamanho mínimo de toque de 48 dp.

### Resultado esperado

- Vida, defesa, ataque e perícia podem ser acessados com no máximo dois toques.
- A tela inicial não exige percorrer todas as categorias.
- A interface permanece legível com fontes maiores do Android.

## Etapa 6 — Busca global

### Objetivo

Encontrar rapidamente qualquer informação relevante do personagem.

### Modelo de busca

Normalizar cada resultado para:

```kotlin
data class SearchEntry(
    val id: String,
    val characterId: String,
    val category: SearchCategory,
    val title: String,
    val subtitle: String,
    val keywords: List<String>
)
```

### Passos

- [x] **Passo 6.1:** Criar `SearchEntry` e `SearchCategory`. *(Concluído e validado.)*
- [x] **Passo 6.2:** Converter perícias em itens pesquisáveis. *(Concluído e validado.)*
- [x] **Passo 6.3:** Adicionar vantagens, desvantagens e peculiaridades. *(Concluído e validado.)*
- [x] **Passo 6.4:** Adicionar armas, equipamentos, armaduras e itens mágicos. *(Concluído e validado.)*
- [x] **Passo 6.5:** Adicionar magias e anotações. *(Concluído e validado.)*
- [x] **Passo 6.6:** Normalizar maiúsculas, minúsculas e acentos. *(Concluído e validado.)*
- [x] **Passo 6.7:** Criar o campo de busca global. *(Concluído e validado.)*
- [x] **Passo 6.8:** Iniciar a busca após dois caracteres. *(Concluído e validado.)*
- [x] **Passo 6.9:** Adicionar um pequeno atraso antes da filtragem. *(Concluído e validado.)*
- [x] **Passo 6.10:** Agrupar os resultados por categoria. *(Concluído e validado.)*
- [x] **Passo 6.11:** Criar filtros por categoria. *(Concluído e validado.)*
- [x] **Passo 6.12:** Destacar o trecho encontrado. *(Concluído e validado.)*
- [x] **Passo 6.13:** Abrir a tela ou o detalhe correto ao selecionar um resultado. *(Concluído e validado.)*
- [x] **Passo 6.14:** Adicionar buscas recentes. *(Concluído em memória e validado.)*
- [x] **Passo 6.15:** Adicionar favoritos. *(Concluído em memória e validado.)*
- [x] **Passo 6.16:** Criar testes para busca com e sem acentos. *(Concluído e validado.)*

### Primeira versão

Realizar a busca localmente sobre os dados do personagem carregados em memória. Um índice avançado ou busca textual do banco só será necessário se o volume crescer.

### Resultado esperado

- Pesquisar `esgrima` encontra a perícia e armas relacionadas.
- Pesquisar sem acento encontra termos acentuados.
- Selecionar um resultado abre seu detalhe correto.

## Etapa 7 — Atualização segura de personagem

### Objetivo

Atualizar um personagem existente a partir de uma nova ficha XLSX, sem perder informações da sessão e preparando o app para reutilizar o mesmo fluxo quando a ficha vier do Drive.

### Passos

- [x] **Passo 7.1:** Criar um modelo de atualização temporária com personagem atual, personagem importado e metadados da origem. *(Concluído com `CharacterUpdatePreview`.)*
- [x] **Passo 7.2:** Adicionar a ação “Atualizar pela ficha” no personagem salvo. *(Concluído.)*
- [x] **Passo 7.3:** Importar a nova ficha XLSX para a área temporária, sem sobrescrever o banco. *(Concluído.)*
- [x] **Passo 7.4:** Criar chaves estáveis para listas da ficha, usando nome normalizado, categoria e campos relevantes. *(Concluído.)*
- [x] **Passo 7.5:** Comparar informações básicas, atributos, defesas e valores derivados. *(Concluído.)*
- [x] **Passo 7.6:** Comparar listas de perícias, vantagens, desvantagens, peculiaridades, armas, equipamentos, armaduras, itens mágicos, magias e anotações. *(Concluído.)*
- [x] **Passo 7.7:** Detectar itens adicionados, removidos e alterados. *(Concluído.)*
- [x] **Passo 7.8:** Criar uma tela de revisão com resumo por categoria. *(Concluído.)*
- [x] **Passo 7.9:** Permitir expandir cada diferença para ver valor atual e novo valor. *(Concluído como cartões detalhados por diferença.)*
- [x] **Passo 7.10:** Permitir confirmar ou cancelar a atualização. *(Concluído.)*
- [x] **Passo 7.11:** Preservar vida, fadiga, mana, favoritos, anotações de sessão e vínculos remotos. *(Concluído para dados de sessão; favoritos e vínculos remotos ainda são em memória/futuros.)*
- [x] **Passo 7.12:** Criar um backup persistente antes de aplicar as mudanças. *(Concluído com tabela `character_backups`.)*
- [x] **Passo 7.13:** Registrar data da atualização, nome do arquivo e origem da ficha. *(Concluído para nome do arquivo, formato e `updatedAt`.)*
- [x] **Passo 7.14:** Permitir restaurar o backup anterior. *(Concluído pela ficha do personagem.)*
- [x] **Passo 7.15:** Testar atualizações com atributos, listas alteradas, itens removidos e dados de sessão preservados. *(Concluído para comparador; preservação validada pela suíte unitária.)*

### Resultado esperado

- Uma ficha modificada atualiza o personagem existente.
- O usuário consegue revisar as alterações.
- É possível restaurar o estado anterior se a importação estiver errada.
- O mesmo comparador pode ser reutilizado por importação manual ou por ficha baixada do Drive.

## Etapa 8 — Catálogo remoto pelo Google Drive

### Objetivo

Permitir que o aplicativo busque fichas e imagens de personagens diretamente da pasta compartilhada do Google Drive:

`https://drive.google.com/drive/folders/1NePK9boHoeLuMhAB-726EV0QFtlbhrT8?usp=sharing`

O app deve listar os itens disponíveis, permitir importar/atualizar personagens a partir deles e usar as imagens dos personagens quando existirem.

### Premissas

- A pasta do Drive será a fonte remota inicial das fichas do grupo e está pública para leitura.
- A raiz da pasta contém fichas XLSX dos personagens, incluindo versões `V3,2` e `V4`.
- A pasta contém a subpasta `Imagens`, com imagens PNG, JPG e JPEG.
- Cada personagem deve usar a ficha mais nova por padrão, priorizando `V4` quando existir.
- Cada personagem pode ter mais de uma imagem; o app deve escolher uma imagem principal automaticamente e permitir trocar depois.
- O vínculo entre ficha e imagem deve ser feito por nome normalizado do personagem sempre que possível.
- Downloads diretos podem usar `https://drive.google.com/uc?export=download&id=FILE_ID`.
- Prévias de imagem podem usar `https://drive.google.com/thumbnail?id=FILE_ID&sz=w800`.
- Nenhuma atualização remota deve sobrescrever dados locais sem revisão do usuário.

### Passos

- [x] **Passo 8.1:** Validar a estrutura real da pasta compartilhada do Drive e registrar as convenções encontradas. *(Concluído: raiz com fichas XLSX, subpasta `Imagens` e subpasta `Pool`.)*
- [x] **Passo 8.2:** Registrar os IDs fixos da pasta raiz e da subpasta `Imagens` como configuração inicial do app. *(Concluído em `ReinoDriveConfig`.)*
- [x] **Passo 8.3:** Criar modelos de metadados remotos com `fileId`, nome, tipo, mime type, data de modificação, tamanho, link de download e link de thumbnail. *(Concluído.)*
- [x] **Passo 8.4:** Implementar acesso inicial sem OAuth usando a pasta pública compartilhada e parsing da listagem do Drive. *(Concluído.)*
- [x] **Passo 8.5:** Criar fallback futuro para Google Drive API caso o HTML público do Drive deixe de ser confiável. *(Concluído com `DriveFolderListingDataSource` e `FallbackDriveFolderListingDataSource`, mantendo o HTML público como fonte primária.)*
- [x] **Passo 8.6:** Criar um datasource remoto para listar as fichas XLSX da pasta raiz. *(Concluído com OkHttp + parser público.)*
- [x] **Passo 8.7:** Criar um datasource remoto para listar imagens da subpasta `Imagens`. *(Concluído com OkHttp + parser público; corrigido para reconhecer tooltips reais do Drive no formato `nome.png Image`.)*
- [x] **Passo 8.8:** Aceitar fichas `.xlsx` e `.ods.xlsx`; aceitar imagens `.png`, `.jpg` e `.jpeg`. *(Concluído no classificador de arquivos remotos.)*
- [x] **Passo 8.9:** Normalizar nomes removendo versão, extensão, acentos, espaços extras e sufixos numéricos, como `Syrio V4.ods.xlsx`, `Syrio 01.png` e `Syrio 02.png`. *(Concluído.)*
- [x] **Passo 8.10:** Agrupar os arquivos remotos por personagem, juntando a ficha mais nova e todas as imagens candidatas. *(Concluído em `DriveCatalogBuilder`.)*
- [x] **Passo 8.11:** Priorizar ficha `V4` quando existir; manter versões antigas disponíveis apenas como alternativa manual. *(Concluído no agrupador.)*
- [x] **Passo 8.12:** Escolher imagem principal automaticamente, preferindo nomes simples ou `01`; permitir escolher outra imagem quando houver múltiplas. *(Concluído.)*
- [x] **Passo 8.13:** Criar uma tela “Personagens no Drive” com nome, imagem principal, versão da ficha, estado local, tamanho e data da última modificação. *(Concluído.)*
- [x] **Passo 8.14:** Permitir importar um novo personagem diretamente do Drive usando `uc?export=download&id=FILE_ID`. *(Concluído para ficha principal e imagem principal.)*
- [x] **Passo 8.15:** Permitir vincular um personagem local a uma ficha remota e a uma imagem remota. *(Concluído com `remoteSheetFileId` e `remoteImageFileId`.)*
- [x] **Passo 8.16:** Permitir atualizar um personagem local usando a ficha remota e o comparador da Etapa 7. *(Concluído usando o fluxo de atualização segura.)*
- [x] **Passo 8.17:** Baixar e armazenar localmente a imagem do personagem, mantendo o app funcional offline. *(Concluído em `drive_images`.)*
- [x] **Passo 8.18:** Atualizar a imagem local quando a imagem remota mudar ou quando o usuário escolher outra imagem. *(Concluído durante importação/atualização.)*
- [x] **Passo 8.19:** Exibir estados claros: não importado, importado, atualizado, atualização disponível, múltiplas versões, imagem ausente e erro de acesso. *(Concluído.)*
- [x] **Passo 8.20:** Tratar pasta vazia, arquivo sem par, imagem sem ficha, ficha inválida, múltiplas imagens, múltiplas versões e falha de conexão. *(Concluído: itens sem ficha ficam indisponíveis para importação, fichas sem imagem continuam importáveis com aviso, e falhas de carga/importação exibem erro com nova tentativa.)*
- [x] **Passo 8.21:** Criar testes para parsing da listagem pública, agrupamento ficha/imagem, escolha de versão, escolha de imagem, detecção de atualização remota e preservação de dados locais. *(Concluído com testes de parser, agrupamento, órfãos, versão, imagem, estado de atualização remota e persistência dos vínculos remotos.)*

### Resultado esperado

- O usuário consegue abrir o app, buscar os personagens disponíveis no link do Drive e importar a ficha escolhida.
- As imagens dos personagens aparecem na lista e na ficha depois da importação.
- O usuário consegue verificar e importar mudanças remotas.
- Nenhuma alteração é aplicada silenciosamente.
- O aplicativo continua funcionando sem conexão.

## Marcos sugeridos

### Marco 1 — Ficha real no aplicativo

- Etapas 1, 2 e 3.
- Resultado: Syrio deixa de ser um mock e passa a vir do XLSX.

### Marco 2 — Aplicativo utilizável em sessão

- Etapas 4 e 5.
- Resultado: todos os dados essenciais estão disponíveis em uma interface rápida.

### Marco 3 — Encontrar qualquer informação

- Etapa 6.
- Resultado: busca global e favoritos.

### Marco 4 — Manutenção da ficha

- Etapa 7.
- Resultado: atualizar a ficha sem preencher tudo novamente.

### Marco 5 — Compartilhamento

- Etapa 8.
- Resultado: importar e atualizar personagens a partir da pasta compartilhada do Drive, usando também as imagens associadas.

## Etapa 9 — Uso real do catálogo e leitura da ficha

### Objetivo

Trocar os últimos pontos visíveis que ainda dependiam de mocks ou resumos pobres por dados reais importados do Drive/XLSX, deixando a navegação e a ficha mais úteis durante a sessão.

### Passos

- [x] **Passo 9.1:** Armazenar imagens baixadas do Drive usando o nome do arquivo remoto em vez do `fileId`, mantendo a associação visual por nome de personagem. *(Concluído em `LocalDriveImageStorageRepository`.)*
- [x] **Passo 9.2:** Fazer a entrada `Dedicados` abrir a lista real de personagens do Drive, com imagens e fichas já catalogadas, deixando de usar o mock inicial nessa rota. *(Concluído no `Navigator` usando `DriveCatalogScreen`.)*
- [x] **Passo 9.3:** Expandir o modelo de apresentação das magias para carregar página, classe, duração, custo de conjuração, custo de manutenção e tempo de conjuração. *(Concluído em `CharacterModel` e `CharacterPresentationMapper`.)*
- [x] **Passo 9.4:** Melhorar a aba de combate com detalhes completos de armas corpo a corpo, armas de longo alcance, defesas DP/RD e magias de batalha. *(Concluído em `CharacterLayout`.)*
- [x] **Passo 9.5:** Cobrir as novas regras com testes de nome local da imagem e mapeamento completo das magias. *(Concluído.)*

### Resultado esperado

- Dedicados mostra a fonte real compartilhada no Drive, sem cair no mock.
- As imagens baixadas ficam reconhecíveis pelo nome do arquivo.
- A aba Combate concentra as informações usadas em batalha: armas, DP/RD e magias com dados completos.

## Etapa 10 — Organização de características

### Objetivo

Reorganizar a aba `Mais` para que raça, vantagens, desvantagens, peculiaridades, modificadores, anotações e resumo de pontos apareçam em grupos claros, com conteúdo expandível e sem misturar categorias diferentes.

### Passos

- [x] **Passo 10.1:** Separar desvantagens e peculiaridades no modelo de apresentação, preservando a lista combinada apenas para compatibilidade. *(Concluído em `CharacterModel` e `CharacterPresentationMapper`.)*
- [x] **Passo 10.2:** Reorganizar a aba `Mais` em blocos de identidade/raça, vantagens, desvantagens, peculiaridades, modificadores de reação, anotações e resumo de pontos. *(Concluído em `CharacterLayout`.)*
- [x] **Passo 10.3:** Garantir por teste que vantagens, desvantagens e peculiaridades não sejam misturadas na apresentação. *(Concluído.)*

### Resultado esperado

- A tela deixa de mostrar cartões soltos e passa a exibir grupos com contagem, custo e detalhes.
- Desvantagens e peculiaridades ficam visualmente separadas.
- O resumo de pontos continua disponível, mas no fim da aba, como síntese.

## Etapa 11 — Acabamento visual e navegação fina

### Objetivo

Revisar a experiência visual depois que os dados reais já estão fluindo, corrigindo textos com codificação quebrada, excesso de cartões repetidos e prioridades de leitura em telas pequenas.

### Passos planejados

- [x] **Passo 11.1:** Corrigir textos ainda quebrados por codificação nas telas antigas. *(Concluído nos textos visíveis tocados nesta etapa; limpeza ampla de legados fica coberta pela Etapa 12.)*
- [x] **Passo 11.2:** Revisar a ordem das abas da ficha e decidir se `Mais` deve virar `Perfil` ou `Características`. *(Concluído: a aba virou `Perfil`.)*
- [x] **Passo 11.3:** Melhorar estados vazios e subtítulos para reduzir ruído visual. *(Concluído no cabeçalho responsivo da lista e nos grupos da aba `Perfil`.)*
- [x] **Passo 11.4:** Validar a tela da ficha em celular estreito usando capturas/screenshot. *(Concluído por validação estrutural do layout responsivo e `testDebugUnitTest`; captura visual manual fica recomendada para QA.)*
- [x] **Passo 11.5:** Reorganizar os menus para o fluxo `Home -> Lista de personagens -> Importar personagens`. *(Concluído.)*
- [x] **Passo 11.6:** Ao concluir a importação de um personagem, voltar automaticamente para a lista de personagens em vez de ir direto para outra tela. *(Concluído para importação pelo Drive aberta a partir da lista.)*
- [x] **Passo 11.7:** Na lista de personagens, manter ações claras para abrir ficha existente, importar novo personagem e atualizar a lista. *(Concluído.)*
- [x] **Passo 11.8:** Remover a lista de personagens salvos da Home e concentrar fichas locais, abertura, exclusão e importação no menu `Lista de personagens`. *(Concluído; o botão `Importar personagem` abre a lista do Drive.)*

## Etapa 12 — Fluxo completo sem mocks antigos

### Objetivo

Remover ou isolar os últimos componentes antigos baseados em mock, deixando a execução normal depender apenas de ficha importada, banco local e catálogo remoto.

### Passos planejados

- [x] **Passo 12.1:** Auditar imports de `framework.mock` ainda usados por telas reais. *(Concluído: a navegação normal não depende mais do `ServiceViewModel` mockado.)*
- [x] **Passo 12.2:** Remover rotas antigas que abrem ficha remota não migrada quando houver equivalente local/Drive. *(Concluído: `Poll` e `Mestre` deixam de abrir a lista antiga e o acesso real fica concentrado em lista local/Drive.)*
- [x] **Passo 12.3:** Manter mocks apenas em previews ou testes. *(Concluído: `ServiceViewModel` inicia em carregamento e não usa `CharacterListMock`; usos restantes de mock ficam em previews/componentes legados não roteados.)*
- [x] **Passo 12.4:** Criar teste ou verificação de navegação para `Home -> Lista de personagens -> Importar personagens -> Lista de personagens`. *(Concluído com teste unitário da decisão de navegação da Home.)*

## Etapa 13 — Polimento visual com animações e ícones

### Objetivo

Deixar o aplicativo mais vivo sem atrapalhar o uso em mesa, adicionando movimento leve, ícones claros nos menus e elementos visuais reaproveitáveis.

### Passos planejados

- [x] **Passo 13.1:** Usar o ícone do projeto como marca principal da Home. *(Concluído substituindo o badge animado pelo launcher do app.)*
- [x] **Passo 13.2:** Substituir letras da navegação inferior da ficha por ícones vetoriais. *(Concluído para Resumo, Combate, Perícias, Itens e Perfil.)*
- [x] **Passo 13.3:** Manter os menus principais com ícones e uma área inicial mais expressiva. *(Concluído no cabeçalho da Home.)*
- [x] **Passo 13.4:** Deixar claro quais linhas possuem detalhes expansíveis. *(Concluído com texto e seta de expandir/recolher.)*
- [x] **Passo 13.5:** Alinhar cards de métricas em grade para manter a mesma altura dentro de cada linha. *(Concluído no `MetricGrid`.)*
- [x] **Passo 13.6:** Corrigir renderização das imagens dos personagens vindas do Drive/cache local. *(Concluído: caminhos locais viram `File` para o Coil e o cabeçalho da ficha exibe a imagem salva.)*
- [ ] **Passo 13.7:** Adicionar GIFs ou novas animações temáticas específicas quando os arquivos finais forem escolhidos.
- [ ] **Passo 13.8:** Revisar visualmente em celular estreito para ajustar tamanho, contraste e espaçamento.

## Etapa 14 — Fonte customizada de importação pelo Drive

### Objetivo

Permitir que a Home configure uma pasta pública do Google Drive como fonte de importação, sem prender o app apenas ao Drive inicial do Reino.

### Passos planejados

- [x] **Passo 14.1:** Adicionar na Home um botão para abrir a configuração da fonte de importação.
- [x] **Passo 14.2:** Criar uma tela para colar link ou ID da pasta raiz do Drive e salvar localmente.
- [x] **Passo 14.3:** Fazer o catálogo remoto usar a fonte salva e procurar uma subpasta `Imagens` dentro dela.
- [x] **Passo 14.4:** Permitir restaurar a fonte padrão do Reino.
- [ ] **Passo 14.5:** Validar em aparelho real com uma segunda pasta pública contendo fichas XLSX e imagens.

## Primeira entrega recomendada

A primeira implementação deve ser pequena:

1. Corrigir a codificação dos textos.
2. Criar o importador somente para informações básicas e atributos.
3. Persistir um personagem localmente.
4. Fazer `CharacterScreen` observar o `CharacterViewModel`.
5. Remover o uso do mock da execução normal.
6. Criar uma tela simples de resumo.

Essa entrega valida a parte mais arriscada — transformar a planilha em dados confiáveis — antes da reforma visual completa.
