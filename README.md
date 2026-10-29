# 📰 Daily News

> O resumo do dia anterior — **as principais notícias por assunto**, sintetizadas por IA em um parágrafo cada.

Aplicação backend que, todo dia de madrugada, coleta as notícias mais relevantes de cada categoria, pede a um modelo de linguagem que escreva um panorama do assunto, e guarda tudo pronto. A página só lê o resultado.

---

## 📋 Sobre o projeto

Acompanhar notícias exige abrir vários sites, filtrar repetição e separar o que importa. O Daily News inverte isso: uma página só, organizada por assunto, com um panorama escrito e as matérias originais logo abaixo.

A escolha de mostrar **o dia anterior** (e não "as últimas notícias") é deliberada: um dia fechado tem começo e fim, não muda enquanto você lê, e dá tempo para os fatos se consolidarem.

**O ciclo que conecta tudo:**

```
notícias de ontem → síntese por IA → persistência → leitura instantânea
```

---

## ✨ Funcionalidades

- ⏰ **Job agendado** — roda sozinho às 4h, com horário configurável por cron
- 📥 **Coleta por categoria** — busca as notícias do dia anterior em 9 assuntos, filtrando por idioma, país e intervalo de datas
- 🤖 **Resumo por IA** — um panorama por categoria, gerado a partir das notícias coletadas
- 🔁 **Idempotência** — rodar o job duas vezes no mesmo dia não duplica nada nem gasta cota à toa
- 🛡️ **Tolerância a falha** — se uma categoria falhar, as outras continuam; sem IA, a página ainda mostra as notícias
- 🌐 **API REST** — resposta com resumo e notícias num envelope só, e `404` para categoria inexistente
- 🌗 **Interface com tema claro/escuro**, navegação por assunto e link para a matéria original

---

## 🏗️ Arquitetura

```
        ┌──────────────┐
  04:00 │  agendamento │  orquestra a escrita
        └──────┬───────┘
               │
       ┌───────┴────────┐
       ▼                ▼
 ┌───────────┐    ┌───────────┐      ┌──────────────┐
 │  noticia  │    │  resumo   │ ───► │  PostgreSQL  │
 │  (GNews)  │    │  (Groq)   │      │   (Docker)   │
 └───────────┘    └───────────┘      └──────┬───────┘
                                            │
        ┌──────────────┐                    │
        │     feed     │ ◄──────────────────┘
        └──────┬───────┘  orquestra a leitura
               │
               ▼
   GET /daily-news/{categoria}
```

- **Pacotes por domínio** (`categoria`, `noticia`, `resumo`), não por camada técnica
- **`noticia` e `resumo` não se conhecem** — quem os conecta são dois orquestradores: `agendamento` (escrita) e `feed` (leitura)
- **Frontend servido pela própria aplicação**, o que elimina CORS e mantém um deploy só

---

## 🧠 Decisões de arquitetura

**Resumo por categoria, não por notícia.** A ideia inicial era resumir cada matéria. Ao inspecionar a resposta real da GNews, ficou claro que o plano gratuito trunca o conteúdo em ~200 caracteres — e a API já devolve uma descrição curta. Resumir um resumo não agregava nada. A solução foi mudar o que a IA faz: sintetizar as notícias de uma categoria em um panorama único.

**Processar no job, não sob demanda.** A página nunca chama API externa. O consumo é fixo em 9 chamadas por API por dia, independente do tráfego — e o carregamento é instantâneo.

**Garantias no banco, não só no código.** Notícias são únicas por URL; resumos, pela combinação categoria + data. As restrições vivem no schema, protegendo contra qualquer caminho de escrita.

**Dependências recebidas, não buscadas.** O `ResumoService` recebe a lista de notícias por parâmetro em vez de consultar o banco — o que o torna testável sem infraestrutura. O mesmo vale para o relógio: o cálculo de datas e o fuso ficam num único componente injetável.

---

## 🛠️ Tecnologias

**Backend**
- Java 23 · Spring Boot 4.1
- Spring Data JPA · PostgreSQL 16
- Spring Scheduling (`@Scheduled`) · RestClient
- Maven · Docker Compose

**Frontend**
- HTML · CSS · JavaScript (sem framework ou build)

**Integrações**
- [GNews API](https://gnews.io) — fonte das notícias
- [Groq](https://console.groq.com) — inferência de LLM (`openai/gpt-oss-120b`)

---

## 💻 Rodando localmente

**Pré-requisitos:** Java 23, Docker, chave da [GNews](https://gnews.io) e da [Groq](https://console.groq.com) (ambas com plano gratuito).

**1. Configure as variáveis de ambiente:**
```bash
cp .env.example .env
```

**2. Suba o banco (PostgreSQL via Docker):**
```bash
docker compose up -d
```

**3. Rode a aplicação:**
```bash
./mvnw spring-boot:run
```

> As variáveis do `.env` precisam estar carregadas no ambiente — via plugin EnvFile na IDE, ou exportadas no shell.

A aplicação sobe em `http://localhost:8080`, cria as tabelas e popula as categorias automaticamente.

**Disparando o job sem esperar as 4h:** ajuste o cron no `application.properties` para alguns minutos à frente e reinicie.
```properties
cron.daily=0 30 14 * * *
```

---

## 📚 Principais endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/daily-news/categorias` | Lista as categorias disponíveis |
| `GET` | `/daily-news/{categoria}` | Resumo do dia + notícias da categoria |

**Exemplo de resposta:**

```json
{
  "categoria": "sports",
  "resumo": "Luka Doncic registrou 52 pontos na vitória do Dallas Mavericks sobre o Utah Jazz...",
  "noticias": [
    {
      "titulo": "NBA: Luka Doncic faz 52 pontos contra o Utah Jazz",
      "data": "2026-10-26T02:43:34Z",
      "url": "https://...",
      "descricao": "O esloveno fez um jogo de almanaque neste domingo...",
      "imagem": "https://...",
      "fonte": "UOL"
    }
  ]
}
```

> Categoria inexistente retorna `404 Not Found`.

---

## 🚧 Limitações conhecidas

- **Sem testes automatizados** — verificação manual durante o desenvolvimento; é a próxima prioridade
- **Schema por `ddl-auto=update`** — funciona em desenvolvimento, mas não remove colunas obsoletas nem altera tipos; o caminho correto é migração versionada (Flyway)
- **Categorias em inglês** — são as nativas da GNews; a tradução para exibição está no frontend
- **Sem deduplicação por conteúdo** — dois veículos publicando a mesma história geram duas entradas, já que as URLs diferem
- **Instância única** — múltiplas cópias disparariam o job em paralelo, multiplicando o consumo de cota
- **Plano gratuito da GNews** — 100 requisições/dia e 12h de atraso nas notícias (irrelevante aqui, já que o conteúdo é sempre do dia anterior)

---

## 👤 Autor

**Leonardo Hideki**
[LinkedIn](https://www.linkedin.com/in/leonardohidekikuriki/) · [GitHub](https://github.com/HidekiLeonardo)
