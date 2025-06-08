
# Salários Consolidado APP

> **Stack:** Java 8 · JSF 2.3 · JPA/Hibernate 5 · CDI/Weld · Tomcat 8.5 · PostgreSQL 15 · Maven 3 · JasperReports 6.20

## 1. Visão Geral

Aplicação web que consolida salários de pessoas de forma performática e assíncrona.  
Principais funcionalidades:

* Cálculo paralelo dos salários, evitando _N + 1_ em consultas JPA  
* Exportação do resultado para PDF via JasperReports  
* Arquitetura limpa: **model → service → controller (Bean) → view (JSF)**

## 2. Como executar em ambiente local

```bash
# 1. clonar o repositório
git clone git@github.com:sergiolinhares/salarios-app.git

# 2. criar banco
executar o comando na raiz do projeto: docker compose up -d 

# 3. configurar servidor
criar um servidor tomcat v8.5 e adicionar o modulo salarios-app nele

# 4. iniciar o servidor e acessar
http://localhost:8080/salarios-app/SalarioConsolidado.xhtml
```

> O schema é criado automaticamente (`drop-and-create`) e o script **`META-INF/sql/dados-iniciais.sql`** carrega dados de exemplo.

### Variáveis configuráveis

| Propriedade                             | Default                  |
|-----------------------------------------|--------------------------|
| `javax.persistence.jdbc.url`            | `jdbc:postgresql://localhost/salario_app` |
| `javax.persistence.jdbc.user`           | `postgres`               |
| `javax.persistence.jdbc.password`       | `postgres`               |
| `concurrent.pool.size` (beans.xml)      | *nº de núcleos da CPU*   |

## 3. Estrutura do Projeto

```
src
├── main
│   ├── java
│   │   └── com.exemplo.salarios
│   │       ├── model
│   │       │   ├── Pessoa.java
│   │       │   ├── Cargo.java
│   │       │   ├── Vencimento.java
│   │       │   ├── CargoVencimento.java
│   │       │   └── PessoaSalarioConsolidado.java
│   │       ├── service
│   │       │   └── SalarioService.java
│   │       ├── controller
│   │       │   └── PessoaSalarioConsolidadoBean.java
│   │       └── util
│   │           ├── EntityManagerProducer.java
│   │           ├── Transacional.java
│   │           └── TransacionalInterceptor.java
│   └── resources
│       ├── META-INF
│       │   └── persistence.xml
│       ├── jasper
│       │   └── salarios_consolidados.jrxml
│       └── salarios
│           └── estilo.css
```

## 4. Diferenciais Técnicos

* **Assíncrono**: `CompletableFuture` + `ExecutorService`
* **Interceptor CDI** para transações declarativas
* **Relatório Jasper** gerado em runtime
* **Clean Code** & SOLID — classes coesas e responsabilidade única

---

Qualquer dúvida estou à disposição.  
**Autor:** _Sérgio Linhares_ – junho/2025
