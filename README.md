# BlackCat — Deploy em Cluster Kubernetes Local (Minikube)

Repositório de implantação da aplicação **BlackCat** (arquitetura em três camadas: Frontend Angular + Nginx, Backend Java Spring Boot e Banco de Dados MySQL) orquestrada 100% localmente utilizando **Kubernetes (Minikube)** e **Docker**.

---

## 📁 Estrutura do Projeto

```text
.
├── backend/                  # Código-fonte do Backend (Spring Boot / Java 17)
│   ├── src/                  # Controllers, Services, Repositories, Entities e Liquibase Changelogs
│   ├── pom.xml               # Gerenciamento de dependências e build Maven
│   └── Dockerfile            # Build Multi-stage (Maven JDK 17 -> Runtime JRE 17)
├── frontend/                 # Código-fonte do Frontend (Angular 18)
│   ├── src/                  # Componentes, Serviços, Guardas de Rota e Environments (/api)
│   ├── public/               # Assets e imagens estáticas
│   ├── angular.json          # Configuração de build do Angular CLI
│   ├── package.json          # Dependências Node/Angular
│   ├── nginx.conf            # Configuração de SPA e Proxy Reverso (/api -> backend-service:8080)
│   └── Dockerfile            # Build Multi-stage (Node 20 Alpine -> Nginx Alpine)
├── mysql/                    # Configurações otimizadas do banco de dados relacional (my.cnf)
├── k8s/                      # Manifestos Kubernetes para o cluster local
│   ├── 01-mysql.yaml         # Secret, ConfigMap, Deployment (1 réplica) e Service ClusterIP (3306)
│   ├── 02-backend.yaml       # ConfigMap, Deployment (2 réplicas - HA) e Service ClusterIP (8080)
│   ├── 03-frontend.yaml      # Deployment (1 réplica) e Service (80 -> 30080)
│   └── 04-ingress.yaml       # Ingress (Traefik) roteando /api -> backend-service e / -> frontend-service
├── evidencias/               # Diretório destinado às capturas de tela da execução prática
├── .gitignore                # Regras de exclusão de versionamento Git
└── README.md                 # Documentação oficial de implantação e reprodução
```

---

## 1. Pré-requisitos

Certifique-se de possuir as seguintes ferramentas instaladas e operacionais na máquina local:

- **[Docker](https://docs.docker.com/get-docker/)** (v20.10 ou superior) — com o serviço/daemon ativo.
- **[Minikube](https://minikube.sigs.k8s.io/docs/start/)** (v1.30 ou superior) — cluster Kubernetes local.
- **[Kubectl](https://kubernetes.io/docs/tasks/tools/)** (v1.28 ou superior) — CLI oficial de gerenciamento do Kubernetes.

Verifique as instalações executando:

```bash
docker --version
minikube version
kubectl version --client
```

---

## 2. Inicialização do Minikube

Inicie o cluster Kubernetes local utilizando o **Docker** como driver de virtualização:

```bash
minikube start --driver=docker
```

Após a inicialização, valide se o nó do cluster encontra-se com status `Ready`:

```bash
kubectl get nodes
```

---

## 3. Construção e Carregamento das Imagens no Minikube

A partir da **raiz do repositório**, execute o build das imagens Docker do **Backend** e do **Frontend** na sua máquina local:

```bash
# 1. Build da imagem do Backend (Spring Boot)
docker build -t blackcat-backend:latest ./backend

# 2. Build da imagem do Frontend (Nginx + Aplicação Web)
docker build -t blackcat-frontend:latest ./frontend
```

Em seguida, carregue as imagens construídas diretamente para dentro do cluster **Minikube** (sem necessidade de enviá-las para um registry externo como Docker Hub ou ECR):

```bash
minikube image load blackcat-backend:latest
minikube image load blackcat-frontend:latest
```

Confirme que as imagens estão disponíveis dentro do ambiente do Minikube:

```bash
minikube image ls
```

---

## 4. Ordem de Execução dos Manifestos Kubernetes (`kubectl apply`)

Aplique os manifestos respeitando a ordem de dependência entre as camadas (Banco de Dados $\rightarrow$ Backend $\rightarrow$ Frontend):

### Passo 4.1 — Camada de Banco de Dados (MySQL)
Cria o `Secret`, `ConfigMap`, `Deployment` (1 réplica) e o `Service` interno (`mysql-service:3306`):

```bash
kubectl apply -f k8s/01-mysql.yaml
```

Aguarde o Pod do MySQL atingir o status `Running` (`1/1`) antes de prosseguir:

```bash
kubectl wait --for=condition=ready pod -l app=mysql --timeout=120s
```

### Passo 4.2 — Camada de Aplicação (Backend Spring Boot)
Cria o `ConfigMap`, `Deployment` com **2 réplicas** (alta disponibilidade) e o `Service` interno (`backend-service:8080`):

```bash
kubectl apply -f k8s/02-backend.yaml
```

### Passo 4.3 — Camada de Apresentação (Frontend Nginx)
Cria o `Deployment` e o `Service` (`frontend-service`):

```bash
kubectl apply -f k8s/03-frontend.yaml
```

### Passo 4.4 — Ingress Controller (Traefik) e Roteamento L7
Instale o **Traefik** via Helm no cluster e aplique o manifesto de Ingress (`04-ingress.yaml`), que encaminha `/api` para o `backend-service:8080` e `/` para o `frontend-service:80`:

```bash
helm repo add traefik https://traefik.github.io/charts
helm repo update
helm upgrade --install traefik traefik/traefik --wait
kubectl apply -f k8s/04-ingress.yaml
```

### Passo 4.5 — Verificação Geral do Deploy
Acompanhe o status de todos os recursos criados no cluster:

```bash
kubectl get pods -o wide
kubectl get deployments
kubectl get services
kubectl get ingress
```

---

## 5. Acesso à Aplicação no Navegador (Via Hostname no Traefik Ingress)

O manifesto `k8s/04-ingress.yaml` configura o roteamento por domínio (*Host-based routing*) para **`blackcat.localhost`** (que já resolve automaticamente para `127.0.0.1` no Windows/Linux/macOS sem precisar editar o arquivo `hosts`) e para **`blackcat.local`**.

### Opção A — Acesso via Hostname na Porta 80 com Traefik (Recomendado)
Encaminhe a porta `80` local para o serviço do **Traefik**:

```bash
kubectl port-forward svc/traefik 80:80
```

Em seguida, acesse diretamente no navegador (sem precisar informar porta):
- **URL:** [http://blackcat.localhost](http://blackcat.localhost)
- *(Ou `http://blackcat.local`, caso adicione `127.0.0.1 blackcat.local` no arquivo `hosts` do sistema).*

### Opção B — Acesso direto via NodePort do Frontend
```bash
minikube service frontend-service
```

> **Por que usar o Traefik (Ingress Controller) em vez de expor serviços via NodePort?**
> 1. **Ponto Único de Entrada (Single Entrypoint):** Em vez de abrir múltiplas portas altas (`30000–32767`) no nó para cada serviço, o Traefik centraliza todo o tráfego externo nas portas padrão HTTP/HTTPS (`80`/`443`).
> 2. **Roteamento Inteligente na Camada 7 (HTTP/HTTPS):** Permite rotear requisições por caminho (*path-based routing*: `/api` vai direto para `backend-service:8080` e `/` vai para `frontend-service:80`) ou por domínio (*host-based routing*) diretamente na borda do cluster.
> 3. **Segurança e Isolamento:** Elimina a necessidade de expor os Services internos como `NodePort`, permitindo que todos os microsserviços operem de forma privada (`ClusterIP`) atrás do Ingress Controller, que também centraliza TLS/SSL, Rate Limiting, CORS e observabilidade.

> **Nota para usuários Windows / Docker Driver:** Mantenha a janela do terminal com o comando `minikube service frontend-service` aberta enquanto utiliza a aplicação no navegador, pois o driver Docker no Windows/macOS cria um túnel local dinâmico até o container do nó Minikube.

### Credenciais de Acesso Inicial (Seed do Banco)
- **Usuário (Login):** `admin`
- **Senha:** `admin`

---

## 6. Evidências Práticas de Execução

Abaixo estão registradas as evidências de funcionamento do cluster Kubernetes e da aplicação rodando no Minikube.

### 6.1. Pods em Execução (`kubectl get pods -o wide`)
Demonstra o Pod do MySQL, as **2 réplicas** do Backend e o Pod do Frontend com status `Running` e seus respectivos endereços IP internos no cluster:

![Evidência - kubectl get pods -o wide](./evidencias/01-get-pods.png)

### 6.2. Services Configurados (`kubectl get services`)
Demonstra os serviços `ClusterIP` (`mysql-service` na porta `3306` e `backend-service` na porta `8080`) e o serviço `NodePort` (`frontend-service` na porta `80:30080/TCP`):

![Evidência - kubectl get services](./evidencias/02-get-services.png)

### 6.3. Deployments Ativos (`kubectl get deployments`)
Demonstra o estado desejado vs. estado atual (`READY`: `1/1` para `mysql-deployment`, `2/2` para `backend-deployment` e `1/1` para `frontend-deployment`):

![Evidência - kubectl get deployments](./evidencias/03-get-deployments.png)

### 6.4. Aplicação Funcionando no Navegador
Demonstra o acesso externo via navegador consumindo o Frontend (Nginx) integrado ao Backend (Spring Boot) e ao Banco de Dados (MySQL):

![Evidência - Aplicação no Navegador](./evidencias/04-aplicacao-navegador.png)
