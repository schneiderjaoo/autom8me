
## Author

Joao Schneider

# Capa

Título do Projeto: VeriMend - PipeLine inteligente 
Nome do Estudante: João Guilherme Schneider da SIlva
Curso: Engenharia de Software.
Data de Entrega: --

## Resumo
 A proposta é desenvolver uma solução que automatiza a geração de notas de atualização (release notes) para sistemas de software. Atualmente, 
muitas empresas elaboram essas notas manualmente, o que pode ser demorado e propenso a erros. Ao automatizar esse processo, busca-se garantir 
consistência, precisão e eficiência na comunicação das mudanças realizadas no sistema aos clientes e usuários finais.

## Introdução
Com o aumento da complexidade dos sistemas de software a comunicação entre dev e cliente se torna mais difícil, dessa maneira passar tudo que é 
mudado dentro do sistema se torna algo muito complexo que se demanda um tempo valioso e ainda sim é possível cometer erros inconscientemente já 
que pode ser suscetível a erros.
Para solucionar/resolver esse problema surge a ideia de automatização da notas de release(release notes), tendo integração com IA é possível 
transformar os commits em notas claras, organizadas e adaptadas ao cliente, garantindo eficiência e precisão no processo de documentação.
A automatização é um fator que serve para melhorar a produtividade e qualidade dos produtos desenvolvidos, a proposta do projeto seria:

Otimizar processos de documentação, reduzindo o tempo e o esforço manual.
Melhorar a comunicação entre desenvolvedores/gestores e clientes.
Minimizar erros e inconsistências garantindo que todas as atualizações sejam devidamente documentadas sem omissões ou ambiguidades.
Aprimorar a transparência no desenvolvimento de software aumentando a satisfação do usuário através de notas de versão mais informáticas e 
acessíveis.

Além de tudo tudo trazendo uma automatização a empresas que ainda possuem esse processo manual, assim podendo reduzir os custos operacionais 
para deixar a equipe focada em atividades estratégicas.
Sendo o objetivo principal desenvolver uma solução que automatiza a geração de release notes, utilizando pipelines e agentes inteligentes para 
transformar logs técnicos em descrições compressíveis e bem estruturadas. 
Algumas limitações seriam a qualidade dos commits já que a maioria dos desenvolvedores não descreve realmente o que foi feito exemplo commits 
como “ajustes”, nisso já se encaixa o contexto incompleto já que dessa maneira ocasionando em versões imprecisas ou confusas, alto custo 
computacional que vamos fazer a transformação de logs em descrições melhores estruturadas para a geração da nossa release note, dessa maneira 
exige mais recursos computacionais significativos, integrações com diferentes workflows fora de escopo, no caso ferramentas diferentes para 
abrir chamados/tickets que são realizadas as atividades, erro na geraçāo de release notes, como depende de uma IA ela pode cometer erros, como 
uma alteraçāo minima ser interpretada como uma urgente.

### Requisitos Funcionais (RF)
RF01 – O sistema deve coletar automaticamente as mensagens de commit do repositório de código após ter sido realizado um pr(pull request) da 
branch de homologação para de produção..

RF02 – O sistema deve gerar automaticamente um documento de release notes com base nas mensagens de commit coletadas e analisadas.

RF03 – O sistema deve permitir integração com ferramentas de CI/CD, como Jenkins.

RF04 – O sistema deve permitir a exportação das release notes em formatos como Markdown, PDF e HTML.

RF06 – O sistema deve permitir a configuração de idioma das release notes.

RF07 – O sistema deve enviar notificações sobre a geração das release notes para usuários cadastrados, envio de e-mail.

### Requisitos Não-Funcionais (RNF)

RNF01 – O sistema deve garantir a segurança das credenciais ao acessar repositórios privados.

RNF02 – O sistema deve apresentar uma interface amigável e intuitiva para configurações.

RNF03 – O tempo de geração das release notes não deve exceder 1 minuto para repositórios de até 100 commits.

RNF04 – O sistema deve ser compatível com o GitHub.

RNF05 – A IA utilizada para análise dos commits deve ter uma taxa de precisão mínima de 80%.

RNF06 – O sistema deve ser escalável para suportar grandes volumes de commits sem degradação de desempenho.

RNF07 – A solução deve seguir boas práticas de codificação visando legibilidade, padronização e facilidade de manutenção.

RNF08 – O código deve seguir padrões de desenvolvimento seguro, evitando vulnerabilidades e vazamentos de informações.

3.2. Considerações de Design
Discussão sobre as escolhas de design, incluindo alternativas consideradas e justificativas para as decisões tomadas.
Visão Inicial da Arquitetura: Descrição dos componentes principais e suas interconexões.
Padrões de Arquitetura: Indicação de padrões específicos utilizados (ex.: MVC, Microserviços).
Modelos C4: Detalhamento da arquitetura em níveis: Contexto, Contêineres, Componentes, Código.
3.3. Stack Tecnológica
Linguagens de Programação: Justificativa para a escolha de linguagens específicas.
Frameworks e Bibliotecas: Frameworks e bibliotecas a serem utilizados.
Ferramentas de Desenvolvimento e Gestão de Projeto: Ferramentas para desenvolvimento e gestão do projeto. ... qualquer outra informação 
referente a stack tecnológica ...
3.4. Considerações de Segurança
Análise de possíveis questões de segurança e como mitigá-las.

4. Próximos Passos
Descrição dos passos seguintes após a conclusão do documento, com uma visão geral do cronograma para Portfólio I e II.

5. Referências
Listagem de todas as fontes de pesquisa, frameworks, bibliotecas e ferramentas que serão utilizadas.

6. Apêndices (Opcionais)
Informações complementares, dados de suporte ou discussões detalhadas fora do corpo principal.

7. Avaliações de Professores
Adicionar três páginas no final do RFC para que os Professores escolhidos possam fazer suas considerações e assinatura:

Considerações Professor/a: Emanuel
Considerações Professor/a: Vanessa
Considerações Professor/a: Tassiana
