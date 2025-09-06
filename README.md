# **GitDocs: Da Máquina ao Cliente — Automatizando Release Notes com IA**

**Autor:** João Guilherme Schneider da Silva  
 **Curso:** Engenharia de Software  
 **Data da Última Revisão:** 5 de setembro de 2025

## **Resumo**

Este trabalho propõe o desenvolvimento do GitDocs, uma ferramenta inteligente para automação da geração de release notes em sistemas de software. Atualmente, muitas empresas elaboram essas documentações manualmente, resultando em processos demorados e suscetíveis a erros. A solução visa garantir consistência, precisão e eficiência na comunicação das alterações realizadas nos sistemas aos clientes e usuários finais, através da implementação de uma arquitetura baseada em microserviços Java integrados com inteligência artificial e versionamento automático.

## **Abstract**

This work proposes the development of GitDocs, an intelligent tool for automating the generation of release notes in software systems. Currently, many companies create these documents manually, resulting in time-consuming and error-prone processes. The solution aims to ensure consistency, accuracy, and efficiency in communicating system changes to clients and end users through the implementation of a Java-based microservices architecture integrated with artificial intelligence and automatic versioning.

## **1\. Introdução**

### **1.1 Contextualização**

Com o aumento da complexidade dos sistemas de software contemporâneos, a comunicação entre desenvolvedores e clientes torna-se mais desafiadora. Informar com precisão todas as alterações realizadas em um sistema é uma tarefa que consome tempo considerável e frequentemente resulta em omissões ou erros involuntários. A crescente demanda por transparência e agilidade no desenvolvimento de software impulsiona a necessidade de soluções automatizadas que possam transformar informações técnicas em conteúdo acessível e estruturado para o usuário final.

### **1.2 Problema Identificado**

A elaboração manual de release notes apresenta limitações críticas que impactam a eficiência organizacional:

* **Propensão a Erros e Omissões:** A documentação manual é suscetível a falhas humanas, comprometendo a qualidade da informação transmitida aos clientes.  
* **Barreira de Comunicação:** Existe dificuldade recorrente na comunicação entre equipes técnicas e clientes devido ao uso de linguagem técnica pouco acessível ao público não técnico.  
* **Impacto na Produtividade:** O tempo gasto em tarefas repetitivas de documentação desvia o foco das equipes de atividades estratégicas que impulsionam a inovação e o crescimento.  
* **Inconsistências de Processo:** Ausência de padronização na estrutura, formato e distribuição das informações de release.

### **1.3 Solução Proposta**

O GitDocs é uma ferramenta inteligente desenvolvida em Java que integra webhooks do GitHub com recursos de inteligência artificial para automatizar completamente a geração de release notes. A solução utiliza o modelo Gemini do Google para transformar automaticamente commits técnicos em descrições claras, organizadas e adaptadas ao público final, implementando simultaneamente um sistema de versionamento semântico automático.

### **1.4 Objetivos**

**Objetivo Geral:** Desenvolver uma ferramenta MVP que automatize a geração de release notes, utilizando webhooks inteligentes e agentes de IA para transformar logs técnicos em descrições claras e bem estruturadas, com versionamento automático baseado no tipo de alteração.

**Objetivos Específicos:**

* Otimizar processos de documentação, reduzindo tempo e esforço manual  
* Implementar sistema de versionamento semântico automático (Major.Minor.Patch)  
* Aprimorar a comunicação entre desenvolvedores e clientes  
* Minimizar erros e inconsistências na documentação através de processos automatizados  
* Aumentar a transparência no desenvolvimento de software  
* Demonstrar viabilidade técnica da automação com IA

### **1.5 Escopo e Limitações do MVP**

**Escopo Incluído:**

* Integração direta via webhooks GitHub (sem Jenkins)  
* Processamento com IA Google Gemini  
* Versionamento semântico automático  
* API REST para consulta de releases  
* Suporte inicial apenas ao idioma português  
* Exportação em formato Markdown

**Limitações Definidas:**

* **Dependência da Qualidade dos Commits:** A eficácia da ferramenta está diretamente relacionada à qualidade das mensagens de commit  
* **Processamento Síncrono:** Para simplificação do MVP, processamento será síncrono  
* **Escopo Linguístico:** Foco inicial em português, com arquitetura preparada para expansão  
* **Integração Única:** GitHub como única plataforma de versionamento no MVP

## **2\. Descrição do Projeto**

### **2.1 Arquitetura da Solução MVP**

A arquitetura do GitDocs MVP é baseada em microserviços Java com processamento simplificado:

GitHub Webhook → Spring Boot Controller → Commit Service →   
AI Processor (Gemini) → Version Service → Release Service →   
MongoDB Atlas → API REST

**Componentes Principais:**

* **Webhook Controller:** Recebe eventos do GitHub via webhook  
* **Commit Service:** Coleta e processa mensagens de commit  
* **AI Processor:** Integração com Google Gemini para análise semântica  
* **Version Service:** Controle de versionamento semântico automático  
* **Release Service:** Compilação e formatação das release notes  
* **Storage Service:** Persistência via MongoDB Atlas

### **2.2 Problemas Focais do MVP**

* **Documentação Manual Ineficiente:** Eliminação da necessidade de elaboração manual das release notes básicas  
* **Comunicação Técnica Inacessível:** Transformação automática de linguagem técnica em descrições acessíveis  
* **Gestão de Versões Manual:** Automação do controle de versões baseado no tipo de alteração  
* **Processos Repetitivos:** Liberação das equipes de tarefas básicas de documentação

## **3\. Especificação Técnica**

### **3.1 Requisitos de Software**

#### **3.1.1 Requisitos Funcionais MVP (RF)**

* **RF01:** O sistema deve receber automaticamente webhooks do GitHub quando há push para branch main/master  
* **RF02:** O sistema deve coletar commits desde a última release automaticamente  
* **RF03:** O sistema deve gerar documentos de release notes estruturados com base na análise semântica das mensagens de commit  
* **RF04:** O sistema deve implementar versionamento semântico automático (Major.Minor.Patch) baseado na categorização por IA  
* **RF05:** O sistema deve categorizar automaticamente as alterações em: features, melhorias, correções e breaking changes  
* **RF06:** O sistema deve exportar release notes em formato Markdown  
* **RF07:** O sistema deve fornecer API REST para consulta de releases geradas  
* **RF08:** O sistema deve implementar fallback automático em caso de falha da IA

#### **3.1.2 Requisitos Não-Funcionais MVP (RNF)**

* **RNF01:** O sistema deve validar assinaturas HMAC-SHA256 dos webhooks GitHub  
* **RNF02:** O tempo de geração das release notes não deve exceder 30 segundos para até 50 commits  
* **RNF03:** A IA deve apresentar acurácia mínima de 70% na categorização dos commits  
* **RNF04:** O código deve seguir boas práticas de desenvolvimento Java 17+ e Spring Boot  
* **RNF05:** O sistema deve ser totalmente containerizado via Docker

### **3.2 Casos de Uso MVP**

#### **3.2.1 Atores do Sistema**

* **Desenvolvedor:** Responsável por realizar commits estruturados e push para main  
* **GitHub:** Sistema que dispara webhooks automaticamente  
* **GitDocs:** Ferramenta responsável por processar commits e gerar release notes  
* **Usuário Final:** Consulta releases via API REST

#### **3.2.2 Fluxo Principal Simplificado**

1. Desenvolvedor realiza push para branch main do repositório  
2. GitHub dispara webhook automaticamente para GitDocs  
3. GitDocs coleta commits desde última release  
4. AI Processor analisa e categoriza commits usando Gemini  
5. Version Service calcula nova versão semântica  
6. Release Service gera release notes em Markdown  
7. Sistema armazena no MongoDB e disponibiliza via API

## **4\. Stack Tecnológica**

### **4.1 Backend**

* **Linguagem:** Java 19+  
* **Framework:** Spring Boot 3.x  
* **Persistência:** Spring Data MongoDB  
* **HTTP Client:** Spring Web (RestTemplate/WebClient)

### **4.2 Inteligência Artificial**

* **Modelo:** Google Gemini API  
* **Processamento:** Prompt engineering otimizado para português  
* **Fallback:** Análise baseada em expressões regulares

### **4.3 Banco de Dados**

* **Solução:** MongoDB Atlas (Cloud gratuito)  
* **Collections:** projects, releases  
* **Indexação:** Otimizada para consultas por projeto e versão

### **4.4 Infraestrutura**

* **Containerização:** Docker  
* **Deploy:** Docker Compose para desenvolvimento  
* **Monitoramento:** Logs estruturados com SLF4J

## **5\. Modelo de Dados**

### **5.1 Entidade Project**
```java
@Document(collection \= "projects")  
public class Project {  
    @Id  
    private String id;  
      
    @Indexed  
    private String repositoryUrl;  
      
    private String webhookSecret;  
    private String lastVersion;  
    private ProjectStatus status;  
    private LocalDateTime createdAt;  
}
```
### **5.2 Entidade Release**
```java
@Document(collection \= "releases")  
public class Release {  
    @Id  
    private String id;  
      
    @Indexed  
    private String projectId;  
      
    @Indexed  
    private String version;  
      
    private ReleaseStatus status;  
    private List\<CommitAnalysis\> commits;  
    private String releaseNotesMarkdown;  
    private LocalDateTime createdAt;  
}
```
## **6\. Implementação**

### **6.1 Arquitetura Spring Boot**
```java
@RestController  
@RequestMapping("/api/webhook")  
public class WebhookController {  
      
    @PostMapping("/github")  
    public ResponseEntity\<String\> handleGitHubWebhook(  
            @RequestBody GitHubPushPayload payload,  
            @RequestHeader("X-Hub-Signature-256") String signature) {  
          
        if (webhookService.validateSignature(payload, signature)) {  
            releaseService.processRelease(payload);  
            return ResponseEntity.ok("Processing...");  
        }  
          
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)  
            .body("Invalid signature");  
    }  
}
```
### **6.2 Processamento com IA**
```java
@Service  
public class GeminiService {  
      
    public ReleaseAnalysis analyzeCommits(List\<String\> commits) {  
        try {  
            String prompt \= buildAnalysisPrompt(commits);  
            return callGeminiAPI(prompt);  
        } catch (Exception e) {  
            return fallbackAnalysis(commits);  
        }  
    }  
}
```
## **7\. Cronograma de Desenvolvimento**

### **7.1 Portfolio II \- Semanas 5-8 (MVP)**

**Semanas 5-6: Core Implementation**

* Setup Spring Boot \+ MongoDB Atlas  
* WebhookController implementado  
* CommitService para coleta via GitHub API  
* GeminiService integração básica

**Semanas 7-8: Features Essenciais**

* VersionService para cálculo semântico  
* ReleaseService para geração de markdown  
* API REST para consultas  
* Testes básicos (cobertura mínima 60%)

### **7.2 Critérios de Sucesso**

* Webhook funcional recebendo eventos GitHub  
* IA categorizando commits com 70%+ acurácia  
* Versionamento semântico automático operacional  
* API REST retornando releases válidas  
* Documentação técnica completa

## **8\. Considerações de Segurança**

### **8.1 Webhook Security**

* Validação HMAC-SHA256 de todas as requisições GitHub  
* Sanitização de inputs para prevenir injection attacks  
* Rate limiting básico por IP

### **8.2 Proteção da IA**

* Sanitização de mensagens de commit antes do processamento  
* Timeout configurado para chamadas Gemini API  
* Validação de responses da IA antes da persistência

## **9\. Conclusão**

O GitDocs representa uma solução viável e focada para automatização de release notes utilizando tecnologias modernas Java e inteligência artificial. O escopo MVP permite validar a hipótese central do projeto enquanto estabelece fundações sólidas para expansões futuras. A arquitetura baseada em Spring Boot garante escalabilidade e manutenibilidade, enquanto a integração com Google Gemini demonstra o potencial transformador da IA na automação de processos de desenvolvimento.

O projeto contribui para o campo da Engenharia de Software ao demonstrar como tecnologias emergentes podem resolver problemas práticos enfrentados diariamente por equipes de desenvolvimento, reduzindo overhead operacional e melhorando a comunicação com stakeholders.

