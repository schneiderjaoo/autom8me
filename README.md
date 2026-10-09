# **GitDocs: Da Máquina ao Cliente - Automatizando Release Notes com IA**

**Autor:** João Guilherme Schneider da Silva  
**Curso:** Engenharia de Software  
**Data da Última Revisão:** Novembro de 2025

## **Resumo**

Este trabalho propõe o desenvolvimento do GitDocs, uma ferramenta inteligente para automação da geração de release notes em sistemas de software. Atualmente, muitas empresas elaboram essas documentações manualmente, resultando em processos demorados e suscetíveis a erros. A solução visa garantir consistência, precisão e eficiência na comunicação das alterações realizadas nos sistemas aos clientes e usuários finais, através da implementação de uma aplicação Java standalone integrada com inteligência artificial, versionamento automático e suporte multilíngue nativo.

## **Abstract**

This work proposes the development of GitDocs, an intelligent tool for automating the generation of release notes in software systems. Currently, many companies create these documents manually, resulting in time-consuming and error-prone processes. The solution aims to ensure consistency, accuracy, and efficiency in communicating system changes to clients and end users through the implementation of a Java standalone application integrated with artificial intelligence, automatic versioning, and native multilingual support.

## **1. Introdução**

### **1.1 Contextualização**

Com o aumento da complexidade dos sistemas de software contemporâneos, a comunicação entre desenvolvedores e clientes torna-se mais desafiadora. Informar com precisão todas as alterações realizadas em um sistema é uma tarefa que consome tempo considerável e frequentemente resulta em omissões ou erros involuntários. A crescente demanda por transparência e agilidade no desenvolvimento de software impulsiona a necessidade de soluções automatizadas que possam transformar informações técnicas em conteúdo acessível e estruturado para o usuário final.

### **1.2 Problema Identificado**

A elaboração manual de release notes apresenta limitações críticas que impactam a eficiência organizacional:

* **Propensão a Erros e Omissões:** A documentação manual é suscetível a falhas humanas, comprometendo a qualidade da informação transmitida aos clientes.  
* **Barreira de Comunicação:** Existe dificuldade recorrente na comunicação entre equipes técnicas e clientes devido ao uso de linguagem técnica pouco acessível ao público não técnico.  
* **Impacto na Produtividade:** O tempo gasto em tarefas repetitivas de documentação desvia o foco das equipes de atividades estratégicas que impulsionam a inovação e o crescimento.  
* **Inconsistências de Processo:** Ausência de padronização na estrutura, formato e distribuição das informações de release.

### **1.3 Solução Proposta**

O GitDocs é uma ferramenta inteligente desenvolvida em Java 21 com Spring Boot que processa commits Git com recursos de inteligência artificial para automatizar completamente a geração de release notes. A solução utiliza o modelo Gemini 2.5 Flash do Google para transformar automaticamente commits técnicos em descrições claras, organizadas e adaptadas ao público final, implementando simultaneamente um sistema de versionamento semântico automático e suporte nativo a múltiplos idiomas (português, inglês e espanhol). Os arquivos gerados são automaticamente commitados em um repositório GitBook (BookSys) para publicação.

### **1.4 Objetivos**

**Objetivo Geral:** Desenvolver uma ferramenta JAR executável que automatize integralmente a geração de release notes, utilizando processamento de commits Git e agentes de IA para transformar logs técnicos em descrições claras e bem estruturadas, com versionamento automático baseado no tipo de alteração e distribuição automática em repositório GitBook.

**Objetivos Específicos:**

* Otimizar processos de documentação, reduzindo tempo e esforço manual  
* Implementar sistema de versionamento semântico automático (Major.Minor.Patch)  
* Aprimorar a comunicação entre desenvolvedores, gestores e clientes  
* Minimizar erros e inconsistências na documentação através de processos automatizados  
* Aumentar a transparência no desenvolvimento de software  
* Oferecer suporte trilíngue nativo para atender mercados internacionais  
* Reduzir custos operacionais, liberando equipes para atividades estratégicas
* Integrar com pipelines CI/CD via GitHub Actions para automação completa

### **1.5 Limitações e Desafios**

* **Dependência da Qualidade dos Commits:** A eficácia da ferramenta está diretamente relacionada à qualidade das mensagens de commit. Mensagens genéricas como "ajustes" ou "correções" podem resultar em release notes imprecisas, exigindo padronização nas práticas de desenvolvimento.  
* **Interpretações da IA:** O uso de inteligência artificial pode ocasionar interpretações equivocadas, como classificar alterações mínimas como críticas ou vice-versa, necessitando de mecanismos de validação e fallback.  
* **Recursos Computacionais:** O processamento de grandes volumes de dados via IA e a transformação semântica requer recursos computacionais significativos, podendo impactar a escalabilidade em cenários de alta demanda.  
* **Dependência de APIs Externas:** A solução depende da disponibilidade e estabilidade da API do Google Gemini, podendo ser afetada por limitações de rate limiting ou indisponibilidade temporária.

## **2. Descrição do Projeto**

### **2.1 Tema**

O GitDocs é uma ferramenta inteligente para automação completa da geração de release notes que processa commits Git com recursos avançados de inteligência artificial. A solução transforma automaticamente logs técnicos em descrições estruturadas e acessíveis, implementa versionamento semântico automático baseado no tipo de alteração identificado pela IA, e oferece suporte multilíngue nativo para facilitar a comunicação com diferentes públicos. Os arquivos gerados são automaticamente commitados em um repositório GitBook para publicação imediata.

### **2.2 Problemas a Resolver**

* **Documentação Manual Ineficiente:** Eliminação completa da necessidade de elaboração manual das release notes, reduzindo drasticamente erros e omissões que comprometem a qualidade da documentação entregue aos clientes.  
* **Comunicação Técnica Inacessível:** Transformação automática de linguagem técnica em descrições claras e acessíveis para usuários finais, melhorando significativamente a comunicação entre equipes de desenvolvimento e stakeholders não técnicos.  
* **Gestão de Versões Manual:** Automação do controle de versões baseado no tipo e impacto das alterações realizadas, eliminando inconsistências e erros no versionamento semântico.  
* **Processos Repetitivos:** Liberação das equipes de tarefas repetitivas de documentação, permitindo foco integral em atividades estratégicas de desenvolvimento e inovação.
* **Distribuição Automática:** Integração com repositórios GitBook para publicação automática das release notes geradas.

### **2.3 Arquitetura da Solução**

A arquitetura do GitDocs é fundamentada em processamento Git orientado a comandos, garantindo simplicidade e facilidade de deploy:

```
Git Repository → Git Log Service → Commit Analyzer → AI Processor (Gemini) 
→ Version Manager → Release Generator → File Export → BookSys Repository (GitBook)
```

**Componentes Principais:**

* **Git Log Service:** Coleta commits do repositório Git local ou remoto  
* **Commit Analyzer:** Pré-processa mensagens de commit com validação de padrões e classificação  
* **AI Processor:** Integração com Google Gemini 2.5 Flash para análise semântica e geração de conteúdo inteligente  
* **Version Manager:** Controle de versionamento semântico automático baseado em tipos de commit  
* **Release Generator:** Compilação e formatação das release notes em Markdown  
* **File Export:** Geração de arquivos Markdown versionados (`vX.Y.Z.md` e `Ge vX.Y.Z.md`)  
* **Git Integration:** Commit e push automático dos arquivos gerados no repositório BookSys (GitBook)

## **3. Especificação Técnica**

### **3.1 Requisitos de Software**

#### **3.1.1 Requisitos Funcionais (RF)**

* **RF01:** O sistema deve coletar automaticamente as mensagens de commit do repositório Git desde a última tag gerada  
* **RF02:** O sistema deve gerar automaticamente documentos de release notes estruturados com base na análise semântica das mensagens de commit coletadas  
* **RF03:** O sistema deve permitir integração com ferramentas de CI/CD via execução de JAR em GitHub Actions  
* **RF04:** O sistema deve possibilitar a exportação das release notes no formato Markdown com templates customizáveis  
* **RF05:** O sistema deve implementar versionamento semântico automático (Major.Minor.Patch) baseado no tipo de alteração identificado pela classificação de commits  
* **RF06:** O sistema deve permitir a configuração do idioma das release notes com suporte nativo a português, inglês e espanhol  
* **RF07:** O sistema deve categorizar automaticamente as alterações em: features (feat), melhorias (refactor), correções (fix) e breaking changes  
* **RF08:** O sistema deve permitir o controle completo de histórico e versionamento das release notes, possibilitando consulta e auditoria de versões anteriores através de tags Git  
* **RF09:** O sistema deve implementar filtragem inteligente de commits por tipo, relevância e impacto antes da geração das release notes  
* **RF10:** O sistema deve permitir geração automática via GitHub Actions ou manual sob demanda das release notes  
* **RF11:** O sistema deve implementar sistema robusto de fallback para buscar commits quando não houver tag anterior ou commits desde a última tag  
* **RF12:** O sistema deve criar e fazer push de tags Git automaticamente após gerar as release notes  
* **RF13:** O sistema deve fazer commit e push automático dos arquivos gerados no repositório BookSys (GitBook)  
* **RF14:** O sistema deve persistir as release notes geradas no MongoDB Atlas para histórico e auditoria

#### **3.1.2 Requisitos Não-Funcionais (RNF)**

* **RNF01:** O sistema deve garantir a segurança das credenciais utilizadas para acessar APIs externas através de variáveis de ambiente e gestão segura de secrets no GitHub Actions  
* **RNF02:** O tempo de geração das release notes não deve exceder 2 minutos para repositórios com até 100 commits, mantendo performance otimizada  
* **RNF03:** O sistema deve ser compatível com repositórios Git locais e remotos, com suporte a branches main e master  
* **RNF04:** A inteligência artificial utilizada deve apresentar acurácia mínima de 80% na categorização e análise semântica dos commits  
* **RNF05:** O sistema deve ser altamente escalável, suportando grandes volumes de commits sem degradação de performance  
* **RNF06:** O código deve seguir rigorosamente boas práticas de desenvolvimento Java 21+, garantindo legibilidade, padronização e facilidade de manutenção  
* **RNF07:** O sistema deve adotar padrões avançados de segurança incluindo sanitização de inputs e proteção contra injection attacks  
* **RNF08:** O sistema deve ser resiliente a falhas, continuando a execução mesmo em caso de erro na persistência MongoDB ou na geração de conteúdo via IA

### **3.2 Casos de Uso**

#### **3.2.1 Atores do Sistema**

* **Desenvolvedor:** Responsável por executar o JAR localmente e configurar parâmetros  
* **GitHub Actions:** Pipeline CI/CD que executa o JAR automaticamente após commits no repositório  
* **GitDocs (JAR):** Ferramenta executável responsável por processar commits e gerar release notes  
* **Usuário Final (Gestor/Cliente):** Destinatário das release notes geradas e publicadas no GitBook (BookSys)

#### **3.2.2 Fluxo Principal de Uso**

1. Desenvolvedor faz push de commits para o repositório principal  
2. GitHub Actions detecta o push e executa o workflow de geração de release notes  
3. GitDocs coleta commits do repositório desde a última tag  
4. Commit Analyzer classifica commits por tipo (feat, fix, refactor, etc.)  
5. Version Manager calcula nova versão semântica baseada nos tipos de commit  
6. AI Processor analisa e transforma commits técnicos em descrições claras usando Google Gemini  
7. Release Generator compila informações e gera dois arquivos Markdown:
   - `vX.Y.Z.md` (versão técnica)
   - `Ge vX.Y.Z.md` (versão inteligente gerada pela IA)
8. Sistema persiste release notes no MongoDB Atlas  
9. Sistema cria tag Git `vX.Y.Z` e faz push para o repositório principal  
10. Sistema faz commit e push dos arquivos gerados no repositório BookSys (GitBook)  
11. Release notes ficam disponíveis automaticamente no GitBook para consulta dos clientes

## **4. Stack Tecnológica**

### **4.1 Linguagens de Programação**

**Java 21:** Escolhida pela robustez, performance e ecosystem maduro para aplicações enterprise. A linguagem oferece excelente suporte a operações assíncronas, integração nativa com ferramentas de CI/CD, e compatibilidade com frameworks como Spring Boot para desenvolvimento rápido e escalável.

### **4.2 Frameworks e Bibliotecas**

#### **4.2.1 Backend Core**

* **Java 21:** Linguagem de programação principal  
* **Spring Boot 3.5.7:** Framework principal para aplicação standalone  
* **Spring Boot Starter Web:** Para integração HTTP e REST  
* **Spring Data MongoDB:** Para persistência de release notes no MongoDB Atlas  
* **Lombok:** Para redução de boilerplate code  
* **Jackson:** Para processamento de JSON nas respostas da API Gemini

#### **4.2.2 Inteligência Artificial**

* **Google Gemini API 2.5 Flash:** Modelo de IA state-of-the-art para análise semântica avançada de commits, geração de descrições em linguagem natural, e suporte nativo a múltiplos idiomas com alta precisão  
* **Prompt Engineering Avançado:** Templates estruturados e otimizados para diferentes tipos de contexto específico por idioma, e validação de qualidade da resposta

#### **4.2.3 Banco de Dados e Persistência**

* **MongoDB Atlas:** Solução em nuvem escolhida pela flexibilidade com dados semi-estruturados (commits, releases, configurações), facilidade de escalabilidade horizontal, e recursos avançados de indexação e busca
  * **Collection:** `release_notes` - Armazena histórico completo de todas as release notes geradas
  * **Estrutura de Dados:** Cada documento contém tag, versão, idioma, conteúdo técnico e conteúdo inteligente gerado pela IA
  * **Persistência Opcional:** O sistema continua funcionando mesmo em caso de falha na conexão com MongoDB, garantindo resiliência
  * **Métricas e Estatísticas:** A coleta de métricas de performance, volume de dados e estatísticas de uso ainda está em desenvolvimento

### **4.3 Ferramentas de Desenvolvimento e DevOps**

#### **4.3.1 Integração Contínua**

* **GitHub Actions:** Plataforma principal de CI/CD escolhida pela integração nativa com repositórios GitHub, facilidade de configuração via YAML, e capacidade de execução em ambientes isolados  
* **Git:** Plataforma de versionamento que serve como fonte dos commits para processamento

#### **4.3.2 Gerenciamento de Build e Deploy**

* **Maven:** Ferramenta de build e gerenciamento de dependências  
* **JAR Executável:** Deploy principal como arquivo JAR standalone (`git-0.0.1-SNAPSHOT.jar`)  
* **GitBook (BookSys):** Repositório Git utilizado para publicação automática das release notes geradas

### **4.4 Controle de Versão e Documentação**

* **Git:** Sistema de controle de versão distribuído  
* **GitHub:** Plataforma de hospedagem de repositórios Git  
* **GitBook (BookSys):** Repositório Git especializado para documentação, onde as release notes são publicadas automaticamente

## **5. Modelo de Dados**

### **5.1 Estrutura de Dados**

**Configurações:** Arquivo `application.properties` para configuração do JAR  
**Commits:** Processamento em memória durante execução via `GitCommitDTO`  
**Release Notes:** Geração de arquivos de saída (Markdown) e persistência no MongoDB

### **5.2 Entidade GitCommitDTO**

```java
public class GitCommitDTO {
    private String sha;
    private String message;
    private String date;
    private CommitType type;
}
```

### **5.3 Entidade ReleaseNotes (MongoDB)**

```java
@Document(collection = "release_notes")
public class ReleaseNotes {
    private String id;
    private String tag;
    private String version;
    private String language;
    private String technicalContent;  // vX.Y.Z.md
    private String intelligentContent; // Ge vX.Y.Z.md
}
```

**MongoDB Atlas - Detalhes de Implementação:**
* **Collection:** `release_notes` - Armazena histórico completo de todas as release notes geradas
* **Database:** Configurável via `spring.data.mongodb.database` (padrão: `Cluster0`)
* **Persistência:** Cada release gerada é automaticamente salva no MongoDB Atlas
* **Resiliência:** O sistema continua funcionando mesmo em caso de falha na conexão com MongoDB (erro é logado mas não interrompe o processo)
* **Estrutura de Dados:** Cada documento contém tag, versão, idioma, conteúdo técnico e conteúdo inteligente gerado pela IA
* **Métricas e Estatísticas:** A coleta de métricas detalhadas (volume de dados, performance de queries, estatísticas de uso) ainda está em desenvolvimento e será implementada em versões futuras

### **5.4 Entidade Version**

```java
public class Version {
    private int major;
    private int minor;
    private int patch;
    
    // Métodos para incrementar versão baseado em tipo de commit
}
```

### **5.5 Enum CommitType**

```java
public enum CommitType {
    FEAT, FIX, REFACTOR, DOCS, STYLE, TEST, CHORE, PERF, CI, BUILD, REVERT
}
```

## **6. Implementação Atual**

### **6.1 Status da Implementação**

#### **Funcionalidades Implementadas**

* Processamento de commits via `git log` desde a última tag  
* Classificação automática de commits (feat, fix, refactor, docs, etc.)  
* Geração de release notes estruturadas em Markdown  
* Integração com Google Gemini 2.5 Flash para melhorar descrições  
* Persistência de release notes no MongoDB Atlas (com tratamento de erro - sistema continua mesmo se MongoDB falhar)  
* Sistema de versionamento semântico automático (Major.Minor.Patch)  
* Criação e push automático de tags Git  
* Commit e push automático de arquivos no repositório BookSys (GitBook)  
* Integração completa com GitHub Actions  
* Suporte a fallback quando não há tag anterior ou commits novos  
* Geração de dois arquivos por release: `vX.Y.Z.md` (técnico) e `Ge vX.Y.Z.md` (inteligente)  
* Configuração via `application.properties` e variáveis de ambiente  
* Tratamento robusto de erros com continuidade de execução mesmo em falhas parciais

#### **Em Desenvolvimento**

* Exportação em PDF/HTML  
* Sistema de notificações por email  
* Interface web responsiva para consulta de release notes  
* Coleta de métricas e estatísticas do MongoDB Atlas (volume de dados, performance, uso) - **métricas ainda em desenvolvimento**

#### **Próximas Etapas**

* Otimizações de performance para grandes volumes de commits  
* Sistema de retry automático para chamadas à API Gemini
* Integração com outros sistemas de documentação

### **6.2 Arquitetura Spring Boot Atual**

```java
@SpringBootApplication
public class GitApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GitApplication.class, args);
        ReleaseNotesRepository releaseNotesRepository = context.getBean(ReleaseNotesRepository.class);
        Environment env = context.getEnvironment();

        // Services
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();
        
        // Pipeline de processamento
        String lastTag = gitLogService.getLastTag();
        Version version = calcularVersaoAtual(lastTag);
        List<GitCommitDTO> commits = gitLogService.getGitLogsSince(lastTag, daysFallback);
        commits = commitService.classifyCommits(commits);
        calcularNovaVersao(commits, version);
        
        String tag = "v" + version.toString();
        criarTagLocal(tag);
        enviarTagParaRemoto(tag);
        
        String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, versionArray, tag, language);
        String intelligentReleaseNotes = geminiService.generateResponse(releaseNotesTemplate, language);
        
        // Persistência e commit no BookSys
        releaseNotesRepository.save(releaseNotesDocument);
        criarArquivosReleaseNotes(repoPath, relativeDir, tag, releaseNotesTemplate, intelligentReleaseNotes);
        fazerCommitNoBookSys(repoPath, relativeDir, tag, releaseFile, branchName);
    }
}
```

### **6.3 Fluxo de Execução**

1. **Coleta de Commits:** `GitLogService` busca commits desde a última tag ou usando fallback de dias
2. **Classificação:** `CommitService` classifica commits por tipo usando `ClassifierService`
3. **Cálculo de Versão:** `Version` calcula nova versão semântica baseada nos tipos de commit:
   - `refactor` → incrementa MAJOR
   - `feat` → incrementa MINOR
   - `fix` ou sem commits → incrementa PATCH
4. **Geração de Conteúdo:** `ReleaseNotesService` gera template técnico e `GeminiService` gera versão inteligente
5. **Persistência:** Salva no MongoDB Atlas (com tratamento de erro)
6. **Criação de Tag:** Cria tag Git `vX.Y.Z` e faz push forçado
7. **Commit no BookSys:** Cria arquivos `vX.Y.Z.md` e `Ge vX.Y.Z.md` no repositório BookSys e faz commit/push

## **7. Considerações de Segurança**

### **7.1 Proteção de Dados e Credenciais**

* **Variáveis de Ambiente:** Todas as credenciais sensíveis (API keys, tokens) são gerenciadas via variáveis de ambiente ou GitHub Secrets  
* **Input Sanitization:** Validação e sanitização rigorosa de todas as entradas (commits, configurações) para prevenir ataques de injection  
* **HTTPS Enforced:** Todas as comunicações exclusivamente via protocolo HTTPS  
* **Secrets Management:** Uso de GitHub Secrets para armazenamento seguro de tokens e chaves de API

### **7.2 Segurança da Inteligência Artificial**

* **Prompt Injection Prevention:** Sanitização avançada de mensagens de commit para evitar manipulação da IA  
* **Timeout Protection:** Configuração de timeouts rigorosos nas requisições para IA  
* **Content Validation:** Validação de conteúdo gerado pela IA antes da persistência  
* **API Key Rotation:** Suporte a rotação de chaves de API via variáveis de ambiente

### **7.3 Segurança de Repositórios**

* **Token Management:** Uso de Personal Access Tokens (PAT) para acesso a repositórios privados  
* **Remote URL Management:** Troca temporária e restauração segura de URLs remotas durante operações Git  
* **Branch Protection:** Validação de branches antes de operações de push

## **8. Configuração e Uso**

### **8.1 Executar com Docker**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue?logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)

O projeto já está configurado com `application.properties` commitado. Basta executar com Docker:

#### **8.1.1 Pré-requisitos**

- **Docker** instalado e rodando
- **Repositório BookSys** clonado localmente (o caminho está configurado no `application.properties`)

#### **8.1.2 Passo a Passo**

1. **Clonar o repositório autom8me:**
   ```bash
   git clone https://github.com/schneiderjaoo/autom8me.git
   cd autom8me
   ```

2. **Clonar o repositório BookSys (se ainda não tiver):**
   ```bash
   # Ajuste o caminho conforme seu sistema
   git clone https://github.com/schneiderjaoo/bookSys.git /Users/user/Documents/GitClones/BookSys
   ```
   
   **Nota:** O caminho padrão no `application.properties` é `/Users/user/Documents/GitClones/BookSys`. Se seu BookSys estiver em outro local, você pode:
   - Ajustar o `application.properties` antes de rodar, ou
   - Montar o BookSys em um volume Docker (veja opção abaixo)

3. **Build da imagem Docker:**
   ```bash
   docker build -t gitdocs:latest .
   ```

4. **Executar o container:**
   
   **Opção 1: BookSys no caminho padrão (Mac/Linux)**
   ```bash
   # O application.properties tem: app.repo.booksys.path=/Users/user/Documents/GitClones/BookSys
   # Monte o BookSys no mesmo caminho dentro do container
   docker run --rm \
     -v $(pwd):/app \
     -v /Users/user/Documents/GitClones/BookSys:/Users/user/Documents/GitClones/BookSys \
     -w /app \
     gitdocs:latest
   ```
   
   **Opção 2: BookSys montado em /app/BookSys (mais portável)**
   ```bash
   # Se preferir montar o BookSys em /app/BookSys, ajuste o caminho via variável de ambiente
   docker run --rm \
     -v $(pwd):/app \
     -v /Users/user/Documents/GitClones/BookSys:/app/BookSys \
     -w /app \
     -e APP_REPO_BOOKSYS_PATH=/app/BookSys \
     gitdocs:latest
   ```
   
   **Opção 3: Script simplificado (recomendado)**
   
   Crie um arquivo `run-docker.sh`:
   ```bash
   #!/bin/bash
   docker run --rm \
     -v $(pwd):/app \
     -v /Users/user/Documents/GitClones/BookSys:/Users/user/Documents/GitClones/BookSys \
     -w /app \
     gitdocs:latest
   ```
   
   Torne executável e rode:
   ```bash
   chmod +x run-docker.sh
   ./run-docker.sh
   ```

   **Explicação dos parâmetros:**
   - `--rm`: Remove o container automaticamente após execução
   - `-v $(pwd):/app`: Monta o diretório atual (autom8me) no container
   - `-v /Users/user/Documents/GitClones/BookSys:/app/BookSys`: Monta o repositório BookSys no container
   - `-w /app`: Define o diretório de trabalho
   - `-e APP_REPO_BOOKSYS_PATH`: (Opcional) Sobrescreve o caminho do BookSys se necessário

   **Nota:** Todas as configurações (incluindo a chave do Gemini) já estão no `application.properties` commitado. Não é necessário passar variáveis de ambiente.

### **8.2 Executar Localmente (Sem Docker)**

Se preferir executar sem Docker:

1. **Pré-requisitos:**
   - Java 21 (JDK)
   - Maven 3.6+
   - Git

2. **Clonar e executar:**
   ```bash
   git clone https://github.com/schneiderjaoo/autom8me.git
   cd autom8me
   mvn spring-boot:run
   ```

   Ou compile e execute o JAR:
   ```bash
   mvn clean package
   java -jar target/git-0.0.1-SNAPSHOT.jar
   ```

### **8.3 Configuração GitHub Actions**

1. **Configurar Secrets no GitHub:**
   - `GEMINI_API_KEY`: Chave da API do Google Gemini (obrigatório)
   - `BOOKSYS_TOKEN`: Personal Access Token com acesso ao repositório BookSys (ou usar `GITHUB_TOKEN`)
   - `MONGODB_URI`: URI de conexão do MongoDB Atlas (opcional - se não configurado, o sistema continua funcionando mas não persiste no banco)

2. **Workflow automático:**
   O workflow `.github/workflows/generate-release-notes.yml` é executado automaticamente após push na branch `main` ou `master`.

### **8.4 Estrutura de Arquivos Gerados**

Os arquivos são gerados no repositório BookSys na pasta configurada:

```
BookSys/
└── books/
    └── release-notes/
        └── Diário de Mudanças/
            ├── v0.1.0.md          # Versão técnica
            ├── Ge v0.1.0.md       # Versão inteligente (Gemini)
            ├── v0.2.0.md
            ├── Ge v0.2.0.md
            └── ...
```

## **9. Próximos Passos e Cronograma**

### **9.1 Melhorias Imediatas**

* Sistema de versionamento semântico automático - **Implementado**  
* Integração com GitHub Actions - **Implementado**  
* Commit automático no BookSys - **Implementado**
* Tratamento avançado de erros da API Gemini

### **9.2 Funcionalidades Futuras**

* Exportação em PDF/HTML  
* Sistema de notificações por email
* Dashboard de métricas de releases com dados do MongoDB Atlas  
* Integração com outros sistemas de documentação  
* Suporte a múltiplos repositórios  
* Análise de tendências e estatísticas baseadas em dados históricos do MongoDB Atlas (coleta de números e métricas em desenvolvimento)

## **10. Conclusão**

O GitDocs representa uma solução viável e focada para automação de release notes utilizando tecnologias modernas Java 21, Spring Boot 3.5.7 e inteligência artificial. O escopo atual permite validar a hipótese central do projeto enquanto estabelece fundações sólidas para expansões futuras. A arquitetura baseada em Spring Boot garante escalabilidade e manutenibilidade, enquanto a integração com Google Gemini demonstra o potencial transformador da IA na automação de processos de desenvolvimento.

A integração completa com GitHub Actions e o repositório GitBook (BookSys) permite uma automação end-to-end, desde a detecção de commits até a publicação automática das release notes, eliminando completamente a necessidade de intervenção manual no processo.

O projeto contribui para o campo da Engenharia de Software ao demonstrar como tecnologias emergentes podem resolver problemas práticos enfrentados diariamente por equipes de desenvolvimento, reduzindo overhead operacional e melhorando a comunicação com stakeholders através de documentação clara e acessível gerada automaticamente.

## **11. Referências e Documentação**

* **Spring Boot:** https://spring.io/projects/spring-boot  
* **Google Gemini API:** https://ai.google.dev/  
* **MongoDB Atlas:** https://www.mongodb.com/cloud/atlas  
* **GitHub Actions:** https://docs.github.com/en/actions  
* **GitBook:** https://www.gitbook.com/  
* **Semantic Versioning:** https://semver.org/

---

O projeto contribui para o campo da Engenharia de Software ao demonstrar como tecnologias emergentes podem resolver problemas práticos enfrentados diariamente por equipes de desenvolvimento, reduzindo overhead operacional e melhorando a comunicação com stakeholders.

# Banner

![GitDocs Banner](banner_git_docs_pdf.pdf)