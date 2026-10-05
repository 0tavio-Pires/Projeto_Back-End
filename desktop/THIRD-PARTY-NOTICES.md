# Componentes redistribuídos

O instalador inclui o runtime Eclipse Temurin 21 (OpenJDK), sob GPL versão 2 com Classpath Exception. A versão, URL de origem e checksum estão em `runtime-lock.json`; os avisos completos acompanham `runtime/legal`. O código-fonte correspondente está disponível na página de release indicada no arquivo de versões, no projeto oficial Adoptium/Temurin.

Electron usa licença MIT e distribui componentes Chromium e Node.js sob suas respectivas licenças. O pacote mantém os arquivos `LICENSE.electron.txt` e `LICENSES.chromium.html` produzidos pelo empacotador. As dependências Java ficam no JAR com seus metadados de licença. Consulte também os arquivos de lock npm e o `pom.xml` do projeto para o inventário de dependências.

Não há serviço de atualização automática, telemetria externa ou coleta de credenciais no inicializador desktop.
