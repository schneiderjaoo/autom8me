## Regra de Negócio – GitDocs

### 1. Fluxo Automático de Release
1. Coletar commits desde a última tag com `GitLogService`.  
2. Classificar commits (`feat`, `fix`, `refactor`, etc.) para definir impacto.  
3. Calcular a nova versão semântica:
   - `refactor`/breaking → incrementa **major**  
   - `feat` → incrementa **minor**  
   - `fix` ou ausência de commits novos → incrementa **patch**              
4. Gerar dois conteúdos:
   - `releaseNotesTemplate` (texto técnico)  
   - `intelligentReleaseNotes` (texto “inteligente” via Gemini)  
5. Persistir release no MongoDB Atlas.  
6. Criar arquivos `vX.Y.Z.md` e `Ge vX.Y.Z.md` no repositório `BookSys`.  
7. Trocar remoto temporariamente, efetuar `git fetch → checkout main → pull --rebase`, `git add/commit/push`, restaurar remoto original.  
8. Atualizar tag `vX.Y.Z` no projeto principal e forçar o push da tag.

### 2. Regras de Conteúdo
- Arquivo `vX.Y.Z.md` deve conter o template técnico completo (Markdown).  
- Arquivo `Ge vX.Y.Z.md` deve conter o texto do Gemini, com prefixo “Ge ” no nome.  
- Idioma padrão: `pt-BR`, mas serviços aceitam outros códigos se configurados.  
- Os arquivos anteriores (`diario-mudancas.md`, `diario-inteligente.md`) não são mais atualizados; cada release cria novos arquivos versionados.

### 3. Integrações
- **MongoDB Atlas:** URL definida em `application.properties`; se falhar, o processo de Git continua, mas o log registra `Failed to store release notes`.  
- **GeminiService:** precisa de chave de API válida (não commitada).  
- **BookSys Repo:** caminho fixo `/Users/user/Documents/GitClones/BookSys`, branch `main`.

### 4. Pré-condições para Execução
- Repositório `autom8me` deve estar limpo (sem alterações locais).  
- Repositório `BookSys` também precisa estar limpo ou a automação fará `git pull --rebase` e abortará se houver arquivos sujos.  
- O usuário que executa precisa ter permissão de push tanto em `autom8me` quanto em `BookSys`.

### 5. Pós-condições
- Tag `vX.Y.Z` atualizada no `autom8me` e enviada ao GitHub.  
- Arquivos `vX.Y.Z.md` e `Ge vX.Y.Z.md` presentes em `BookSys/books/release-notes/Diário de Mudanças`.  
- Commit no `BookSys` com mensagem `chore: update diário de mudanças vX.Y.Z`.  
- Registro no MongoDB (se credenciais corretas).  
- Processo encerra o Spring Boot e faz `System.exit(0)` para uso em scripts/pipelines.

### 6. Fallbacks e Erros Conhecidos
- **Sem commits novos:** gera release de patch mesmo assim para manter diário atualizado.  
- **Tag existente:** uso de `git tag -f` e `git push --force` garante sincronização.  
- **Mongo inválido:** não interrompe o fluxo; apenas loga o erro.  
- **Git com alterações locais:** fluxo aborta e restaura o remoto original. Corrigir estado manualmente e executar de novo.
