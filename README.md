# Daily News

Uma landing page que resume, em um parágrafo por assunto, o que aconteceu no dia anterior.

Todo dia de madrugada a aplicação busca as principais notícias por categoria, pede a uma IA que sintetize cada assunto em um texto curto, e guarda tudo no banco. A página lê o resultado pronto.

---

## O problema

Acompanhar notícias exige abrir vários sites, filtrar repetição e separar o que importa. A proposta aqui é inverter isso: uma página só, organizada por assunto, com um panorama escrito e as matérias originais logo abaixo.

A escolha de mostrar **o dia anterior** (e não "as últimas notícias") é deliberada: um dia fechado tem começo e fim, não muda enquanto você lê, e dá tempo para os fatos se consolidarem.

---

## Como funciona

```
        04:00 (job agendado)
             │
             ├─► GNews API ──► notícias do dia anterior, por categoria
             │                        │
             │                        └─► PostgreSQL
             │
             └─► Groq (LLM) ──► um resumo por categoria
                                      │
                                      └─► PostgreSQL

        Requisição do usuário
             │
             └─► GET /daily-news/{categoria} ──► { categoria, resumo, notícias }
```

O job roda sozinho uma vez por dia. A página nunca chama a GNews nem a IA — ela só lê o que já foi processado, o que mantém o carregamento rápido e o consumo das APIs externas previsível (9 chamadas por dia para cada uma).

---

## Stack

| Tecnologia | Por quê |
|---|---|
| **Java 23 + Spring Boot 4.1** | Framework dominante no mercado Java; ecossistema maduro para agendamento, acesso a dados e API REST |
| **PostgreSQL 16** | Dados estruturados e relacionais (notícia pertence a categoria); mais próximo de ambiente real que um banco embarcado |
| **Docker Compose** | Sobe o banco com um comando, com volume para persistir dados entre reinicializações |
| **Maven** | Build e dependências |
| **GNews API** | Fonte das notícias; aceita filtro por categoria, idioma, país e intervalo de datas |
| **Groq** (`openai/gpt-oss-120b`) | Inferência de LLM com modelos open-source e plano gratuito |
| **HTML/CSS/JS puros** | A página é simples demais para justificar um framework e um passo de build |

---

## Decisões de arquitetura

### Pacotes por domínio, não por camada

Cada domínio (`categoria`, `noticia`, `resumo`) carrega suas próprias entidades, repositórios e serviços. O que muda junto fica junto.

### Os domínios não se conhecem

`noticia` e `resumo` não têm referência um ao outro. Quem os conecta são dois orquestradores:

- **`agendamento`** coordena a escrita: busca notícias, entrega ao resumo, persiste
- **`feed`** coordena a leitura: junta notícias e resumo em uma resposta só

O `ResumoService` **recebe** a lista de notícias como parâmetro em vez de buscá-la. Isso o torna testável sem banco e reutilizável para resumir qualquer conjunto de notícias.

### Resumo por categoria, não por notícia

A ideia inicial era resumir cada matéria individualmente. Ao inspecionar a resposta real da GNews, ficou claro que o plano gratuito **trunca o conteúdo** em ~200 caracteres — e a própria API já devolve uma descrição curta. Resumir um resumo não agregava nada.

A solução foi mudar o que a IA faz: sintetizar **as notícias de uma categoria em um panorama único**. Mais fiel à proposta original e viável com o material disponível.

### Operações idempotentes

Rodar o job duas vezes no mesmo dia não duplica nada:

- notícias são identificadas pela URL (`unique`)
- resumos, pela combinação categoria + data (`unique` composta)
- categorias, pelo nome

As garantias estão no **banco**, não apenas no código — o que protege contra qualquer caminho de escrita.

### Falha em uma categoria não derruba o job

O processamento de cada categoria é isolado. Se a IA estiver indisponível, as notícias ainda são salvas e a página funciona sem os resumos — degradação graciosa em vez de página vazia.

### Datas em um lugar só

"Qual é o dia de ontem" depende de fuso horário e era calculado em três classes diferentes. A regra foi extraída para um componente `Calendario`, que concentra a única declaração do fuso do projeto.

---

## Como rodar

### Pré-requisitos

- Java 23
- Docker e Docker Compose
- Chave da [GNews API](https://gnews.io) (plano gratuito)
- Chave da [Groq](https://console.groq.com) (plano gratuito)

### 1. Variáveis de ambiente

Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp .env.example .env
```

### 2. Suba o banco

```bash
docker compose up -d
```

O PostgreSQL fica disponível em `localhost:5433`.

### 3. Rode a aplicação

```bash
./mvnw spring-boot:run
```

> As variáveis do `.env` precisam estar carregadas no ambiente. Na IDE, use um plugin como o EnvFile; no terminal, exporte-as antes de rodar.

Na primeira execução, as tabelas e as categorias são criadas automaticamente.

### 4. Acesse

```
http://localhost:8080
```

### Disparando o job manualmente

O job roda às 4h por padrão. Para testar sem esperar, ajuste o cron no `application.properties` para alguns minutos à frente e reinicie:

```properties
cron.daily=0 30 14 * * *
```

---

## Endpoints

### `GET /daily-news/categorias`

```json
[{ "nome": "sports" }, { "nome": "health" }]
```

### `GET /daily-news/{categoria}`

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

Categoria inexistente retorna **404**.

---

## Limitações conhecidas

- **Sem testes automatizados.** A verificação foi manual durante o desenvolvimento. É a próxima prioridade.
- **Schema gerenciado por `ddl-auto=update`.** Funciona em desenvolvimento, mas não remove colunas obsoletas nem altera tipos existentes. O caminho correto é migração versionada (Flyway).
- **Categorias em inglês.** São as categorias nativas da GNews. A tradução para exibição está no frontend; o ideal seria um rótulo no próprio modelo.
- **Sem deduplicação por conteúdo.** Dois veículos publicando a mesma história geram duas entradas, já que as URLs diferem.
- **Instância única.** Rodar várias cópias faria o job disparar em paralelo e consumir a cota multiplicada. Resolver isso exigiria um lock distribuído.
- **Limites do plano gratuito.** A GNews permite 100 requisições/dia e entrega notícias com 12h de atraso — irrelevante aqui, já que o conteúdo exibido é sempre do dia anterior.
- **Tamanho do resumo não é garantido.** O limite é pedido no prompt, e modelos de linguagem tratam isso como orientação aproximada.
