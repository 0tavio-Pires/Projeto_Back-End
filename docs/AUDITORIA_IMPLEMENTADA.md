# Auditoria e implementação

Esta revisão substitui o protótipo de uma linha por um sistema de supervisão e simulação de rede. O escopo implementado é a operação simulada, com interface e persistência; não inclui equipamento ferroviário de campo.

| Achado | Correção |
| --- | --- |
| Estado global mutável compartilhado entre HTTP e Timer | Escritor único no serviço de aplicação, cópia por comando e publicação após commit |
| Timer criado no construtor e vulnerável a exceções | Scheduler gerenciado pelo Spring, passos limitados e suspensão visível em falha |
| Identificação duplicada e entrada arbitrária | IDs/números únicos, DTOs próprios, Bean Validation e invariantes de domínio |
| Duas composições sobrescreviam a ocupação | Reserva exclusiva de recurso e plataforma de destino; validação a cada commit |
| Estações desativadas ignoradas | Disponibilidade de estação, plataforma, linha e trecho verificada durante a circulação |
| Troca de estado de estação alterava trem | Casos de uso e contratos específicos para cada entidade |
| Criação/remoção de estação e trem demonstrativas | Cadastros completos com referências e ocupação protegidas |
| Uma linha fixa, sem topologia da rede | Linhas, estações, plataformas direcionais, trechos e perfis técnicos |
| Integrações confundidas com passagem de trens | Grafos distintos de vias e dependências; transferência exige via verificada |
| Ausência de transferência e busca de caminho | Dijkstra por tempo/custo de ocupação, compatibilidade e bloqueios; execução por trechos |
| Ocorrências sem modelo e efeitos consistentes | Identidade, rótulo, categoria, severidade, alvo, efeito, reconhecimento e resolução |
| Alertas não propagados | Travessia de dependências com ciclos controlados, causa e caminho visíveis |
| Dados desapareciam no reinício | H2 local, suporte PostgreSQL, Flyway, recuperação pausada e exportação/importação |
| Importação genérica e retorno JSON incoerente | Esquema tipado, limite de 5 MB, validação integral e substituição atômica |
| Ausência de autorização e proteção de comandos | Sessão, perfis, BCrypt, CSRF, CSP e cookies restritos |
| Estatísticas fictícias e erro com conjunto vazio | Contagens reais e média/mediana/modas da espera, com caso vazio definido |
| Ausência de interface operacional | React/TypeScript: mapa, frota, ocorrências, cadastros, histórico e cenário |
| Artefatos compilados e cópias antigas no Git | Remoção de `bin/`, regras de ignore e build reproduzível |
| Maven sem checksum e override de repositório incorreto | Maven 3.10.0 fixado com SHA-256; correção do wrapper Windows |
| Testes antigos com expectativas contraditórias | Suítes de domínio, API, segurança, concorrência, persistência e frontend |

## Decisões de arquitetura

O monólito modular evita distribuir uma operação que precisa de uma ordem de comandos definida. A consistência do agregado é prioritária: uma transação grava estado e evento de auditoria. A atualização usa comparação de versão e não admite dois escritores independentes. O snapshot persistido simplifica recuperação; para redes muito grandes, a evolução natural é normalizar entidades, indexar adjacência e introduzir processamento incremental de impactos. A implantação atual é de uma instância por rede.

O roteamento usa o cadastro físico, não nomes de estações nem cores de linha. Compartilhar bitola não basta: energia, sinalização e tecnologia também precisam corresponder. O cenário inicial usa perfis simplificados conservadores e só uma continuidade entre linhas respaldada por serviço histórico documentado. A validação técnica de um novo trecho cabe ao cadastro de engenharia, registrada em `source`; o software verifica a presença da evidência e as regras, não autentica o documento externo.

O algoritmo de circulação é uma simulação discreta com retenção conservadora. Não modela curvas de frenagem ou liberação parcial de cantões. Ocupação é mantida inclusive quando uma ocorrência bloqueia um trem em trecho. A retirada e o cancelamento de transferência exigem plataforma. Um bloqueio não é contornado por teletransporte.

Impactos diretos, dependências operacionais e riscos por integração são diferenciados. Um problema em uma estação pode afetar todas as linhas que a servem. Dependências operacionais propagam efeitos; integrações de passageiros propagam aviso de risco. Resolver um incidente não limpa outros incidentes ativos.

## Validação

A evolução desktop adiciona um inicializador Electron com sandbox e um motor Java separado. O pacote Windows inclui Temurin, fixa seu checksum e mantém os avisos de licença. O aplicativo preserva o mesmo domínio e os mesmos contratos operacionais; o transporte privado acrescenta autenticação temporária local e conserva CSRF. Não há regras ferroviárias duplicadas no inicializador.

Os pontos de integração tratados incluem instância única, porta dinâmica, espera explícita pelo motor, validação de identidade do serviço, dados por conta do Windows, encerramento coordenado, supervisão por pipe para evitar processo órfão, logs, exportação nativa e manual offline. O banco fica fora da pasta de instalação para sobreviver a atualizações. Uma falha de inicialização oferece consulta de logs, nova tentativa ou saída.

O teste com HTTP real identificou que uma recusa de CSRF podia ser redirecionada ao tratamento de erro e aparecer como 401. O handler agora escreve 403 diretamente, preservando o contrato de erro também no desktop.

- Testes Java: cenário histórico, transferência 7 → 10, rejeição de integração sem via, incompatibilidade, via fechada, bloqueios simultâneos, estação desativada, reserva bidirecional, ciclos de alerta, efeitos operacionais, duas horas simuladas sem violar ocupação, números duplicados e dados inválidos.
- Integração: autenticação real, papéis/CSRF, DTO sem sobreposição de estado, cadastros, contagens, histórico, importação atômica e limitada, isolamento de snapshots, reinício pausado, comandos concorrentes e edição de via em uso.
- Falha de persistência: o estado não é publicado quando a gravação falha.
- Frontend: compilação TypeScript e testes de posição, busca e relógio.
- Desktop: seis testes Java de credencial local, origem, endereço remoto, CSRF e encerramento protegido; testes Node de limites de origem e handshake; teste com o Java incluído, banco em caminho com espaços, reinício persistente e perda do pipe do inicializador.
- Aplicativo empacotado: smoke test em base isolada, renderização da janela autenticada, comando com CSRF, exportação JSON, captura da interface e verificação de que o processo Java encerrou.

O instalador ainda não possui assinatura digital nem atualização automática. O pacote atual é Windows x64. Não há sincronização entre instalações desktop, autenticação individual de vários operadores nesse modo ou criptografia adicional do banco local; a proteção dos dados depende da conta do Windows. A implantação web continua disponível para operação centralizada.

Os comandos de reprodução estão no README e são executados no CI. PostgreSQL externo, implantação HTTPS, carga prolongada em produção e operação ferroviária real precisam ser validados no ambiente de destino. O banco exercitado localmente é H2. A suíte não constitui homologação de segurança ferroviária.
