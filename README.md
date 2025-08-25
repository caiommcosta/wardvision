# 🧠 WardVision
**Este repositório conta apresenta APENAS parte de um projeto**

Este é um projeto pessoal focado na **análise de partidas profissionais de Dota 2** e na **extração de estatísticas relevantes** a partir de arquivos de replay `.dem` e de **APIs públicas**, como a OpenDota.
---

## 🎯 Visão Geral

O objetivo do projeto é processar e interpretar replays e dados de partidas profissionais, extraindo **informações estratégicas e estatísticas** que podem ser utilizadas para análises de desempenho, criação de conteúdo para redes sociais e futuras integrações em plataformas de análise.
---

## 🧰 Tecnologias Utilizadas

### 🎞️ Parser de Replays (Java)
- Java (JDK 17)
- Maven
- [Clarity (parsing de replays OpenSource)](https://github.com/skadistats/clarity)
- PostgreSQL (banco de dados)
- dotenv-java (gerenciamento de variáveis de ambiente)
- OpenCSV (manipulação de arquivos CSV)
- SLF4J (log)
- JUnit e Mockito (testes)
---

## 🚀 Como Rodar o Projeto

### 1. Pré-requisitos

#### ☕ Java (JDK 17)

Instale o Java Development Kit (JDK) 17:  
👉 [Download do JDK (Oracle)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Após a instalação, configure a variável de ambiente `JAVA_HOME` no seu sistema operacional.

#### 🔧 Maven

Instale o [Maven](https://maven.apache.org/install.html) para gerenciamento das dependências e execução dos builds.

#### 🐘 PostgreSQL

Instale o PostgreSQL para armazenar os dados extraídos dos replays.
---

### 2. Configuração

- Configure o arquivo `.env` na raiz do projeto com as variáveis necessárias, por exemplo:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=wardvision
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
REPLAY_PATH=Seu-caminho/replay-parser/data/tests/raw
PROCESSED_DIR=Seu-caminho/replay-parser/data/tests/processed
ERROR_DIR=Seu-caminho/replay-parser/data/tests/error
```

edite o "Seu-caminho"

### 3. Dependências do Backend (arquivo pom.xml)

```xml
<dependencies>
  <!-- Clarity: parsing de replays Dota 2 -->
  <dependency>
    <groupId>com.skadistats</groupId>
    <artifactId>clarity</artifactId>
    <version>3.1.1</version>
  </dependency>

  <!-- SLF4J: para logs -->
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.13</version>
  </dependency>

  <!-- PostgreSQL -->
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
  </dependency>

  <!-- dotenv -->
  <dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>2.2.4</version>
  </dependency>

  <!-- CSV -->
  <dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
  </dependency>

  <!-- Testes -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>

  <dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.11.0</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

### 4. Executando

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.wardvision.Main"
```

### 5. Estrutura de pastas
```plaintext
F:.
|   estrutura.txt
|   
+---main
|   +---java
|   |   \---com
|   |       \---wardvision
|   |           |   estrutura.txt
|   |           |   Main.java
|   |           |   Run.java
|   |           |   
|   |           +---config
|   |           |       DatabaseConfig.java
|   |           |       
|   |           +---features       
|   |           |   +---smoke_path
|   |           |   |   +---controller
|   |           |   |   |       SmokePathController.java
|   |           |   |   |       
|   |           |   |   +---entities
|   |           |   |   |       SmokeData.java
|   |           |   |   |       SmokePathPoint.java
|   |           |   |   |       
|   |           |   |   +---processor
|   |           |   |   |       SmokePathProcessor.java
|   |           |   |   |       
|   |           |   |   \---repository
|   |           |   |           CsvSmokePathRepository.java
|   |           |   |           DbSmokePathRepository.java
|   |           |   |           ICsvSmokePathRepository.java
|   |           |   |           IDbSmokePathRepository.java
|   |           |   |                 
|   |           +---helpers
|   |           |       CsvFormatter.java
|   |           |       EntityPropertyHelper.java
|   |           |       NameNormalizer.java
|   |           |       ReplayFileHelper.java
|   |           |       
|   |           \---shared
|   |               +---analyzer
|   |               |       BuybackLogProcessor.java
|   |               |       ReplayAnalyzer.java
|   |               |       SimpleRunnerFactory.java
|   |               |       
|   |               +---gametimes
|   |               |   +---entities
|   |               |   |       GameTimes.java
|   |               |   |       
|   |               |   \---processor
|   |               |           GameTimesProcessor.java
|   |               |           IGameTimesProcessor.java
|   |               |           
|   |               +---intercace
|   |               |       IEvent.java
|   |               |       IProcessorWithResult.java
|   |               |       IReplayAnalyzer.java
|   |               |       ISimpleRunnerFactory.java
|   |               |       
|   |               +---match_details
|   |               |   +---entities
|   |               |   |       MatchPlayers.java
|   |               |   |       MatchTeams.java
|   |               |   |       
|   |               |   \---processor
|   |               |           MatchDetailsProcessor.java
|   |               |           
|   |               \---entities
|   |                       MatchContext.java
|   |                       
|   \---resources
|           simplelogger.properties
|           
\---test
    \---java
        \---com
            \---wardvision
                    LoggerLevelTest.java
                    ReplayAnalyzerTest.java
                    RunTest.java
```