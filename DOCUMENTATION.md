# UniEat — Documentação Técnica

## Índice

1. [Visão geral](#1-visão-geral)
2. [Arquitetura](#2-arquitetura)
3. [Estrutura de pacotes](#3-estrutura-de-pacotes)
4. [Camada de dados — Firebase](#4-camada-de-dados--firebase)
5. [Modelos](#5-modelos)
6. [DAOs](#6-daos)
7. [Presenters](#7-presenters)
8. [Activities (View)](#8-activities-view)
9. [Fluxos principais](#9-fluxos-principais)
10. [Gerenciamento de sessão](#10-gerenciamento-de-sessão)
11. [Enums](#11-enums)
12. [Utilitários](#12-utilitários)
13. [Configuração de build](#13-configuração-de-build)
14. [CI/CD](#14-cicd)

---

## 1. Visão geral

**UniEat** é um aplicativo Android para pedidos de refeições em cantinas universitárias. O sistema atende dois perfis de usuário:

- **Estudante (ALUNO)**: navega pelo cardápio, monta um carrinho, paga (PIX ou saldo), acompanha o status do pedido e avalia pratos.
- **Cozinheiro (COZINHEIRO)**: recebe pedidos, avança o status de preparo e gerencia o cardápio.

O backend é o **Firebase Realtime Database**, com escuta em tempo real para atualização de status de pedidos.

---

## 2. Arquitetura

O projeto segue o padrão **MVP (Model-View-Presenter)**:

```
View (Activity)
    │  delega ações do usuário
    ▼
Presenter
    │  chama DAOs assincronamente via FirebaseCallback
    ▼
DAO (Firebase)
    │  lê/escreve no Firebase Realtime Database
    ▼
FirebaseHelper (singleton de referências)
```

**Regras do padrão adotado:**

- Activities não contêm lógica de negócio — apenas chamam métodos do presenter e atualizam a UI.
- Presenters recebem `Context` quando necessário (para SessionManager ou navegação).
- DAOs são stateless e retornam resultados via `FirebaseCallback<T>`.
- `SessionManager` guarda o estado do usuário logado em SharedPreferences.

---

## 3. Estrutura de pacotes

```
com.example.unieat/
│
├── adapter/
│   ├── DishCardAdapter.java       # Exibe pratos em cards (estudante)
│   ├── OrderItemAdapter.java      # Itens do carrinho
│   ├── HistoryAdapter.java        # Histórico de pedidos
│   ├── KitchenOrderAdapter.java   # Lista de pedidos para a cozinha
│   └── OrderStatusAdapter.java    # Status visual de um pedido
│
├── dao/
│   ├── FirebaseCallback.java      # Interface de callback genérica
│   ├── UserDAO.java
│   ├── DishDAO.java
│   ├── OrderDAO.java
│   ├── PaymentDAO.java
│   └── RatingDAO.java
│
├── data/
│   ├── FirebaseHelper.java        # Singleton com referências do banco
│   ├── SessionManager.java        # Persistência local da sessão
│   ├── DatabaseHelper.java        # Suporte SQLite (legado, não utilizado)
│   └── DataSeeder.java            # Popula dados iniciais de teste
│
├── enums/
│   ├── UserType.java              # ALUNO | COZINHEIRO
│   ├── OrderStatus.java           # PENDENTE | PREPARANDO | PRONTO | ENTREGUE
│   ├── PaymentMethod.java         # PIX | CASH
│   └── FoodType.java              # 11 categorias de alimento
│
├── model/
│   ├── User.java
│   ├── Dish.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   └── Rating.java
│
├── presenter/
│   ├── LoginPresenter.java
│   ├── RegisterPresenter.java
│   ├── StudentHomePresenter.java
│   ├── OrderPresenter.java
│   ├── PaymentPresenter.java
│   ├── ProfilePresenter.java
│   ├── HistoryPresenter.java
│   ├── OrderStatusPresenter.java
│   ├── student/
│   │   ├── MenuPresenter.java
│   │   └── RatingPresenter.java
│   └── kitchen/
│       ├── KitchenHomePresenter.java
│       └── KitchenMenuPresenter.java
│
├── util/
│   ├── DateUtils.java
│   └── OrderUtils.java
│
└── view/
    ├── BaseActivity.java
    ├── MainActivity.java
    ├── NavigationHelper.java
    ├── LoginActivity.java
    ├── RegisterActivity.java
    ├── ForgotPasswordActivity.java
    ├── ResetPasswordActivity.java
    ├── StudentHomeActivity.java
    ├── MenuActivity.java
    ├── OrderActivity.java
    ├── PaymentActivity.java
    ├── PaymentPixActivity.java
    ├── OrderSuccessActivity.java
    ├── OrderStatusActivity.java
    ├── HistoryActivity.java
    ├── RatingActivity.java
    ├── ProfileActivity.java
    ├── KitchenHomeActivity.java
    ├── KitchenMenuActivity.java
    ├── KitchenAllOrdersActivity.java
    └── EditDishActivity.java
```

---

## 4. Camada de dados — Firebase

### Estrutura do Realtime Database

```
{
  "users": {
    "{userId}": {
      "id": "string",
      "name": "string",
      "username": "string",
      "email": "string",
      "password": "string",
      "balance": 0.0,
      "type": "ALUNO | COZINHEIRO"
    }
  },
  "dishes": {
    "{dishId}": {
      "id": "string",
      "name": "string",
      "description": "string",
      "price": 0.0,
      "type": "FoodType (enum name)",
      "imageName": "string",
      "available": true
    }
  },
  "orders": {
    "{orderId}": {
      "annotation": "string",
      "orderStatus": "PENDENTE | PREPARANDO | PRONTO | ENTREGUE",
      "time": 1234567890000,
      "items": {
        "{itemId}": {
          "dishId": "string",
          "quantity": 1
        }
      }
    }
  },
  "payments": {
    "{paymentId}": {
      "id": "string",
      "orderId": "string",
      "method": "PIX | CASH",
      "amount": 0.0,
      "timeMillis": 1234567890000
    }
  },
  "avaliations": {
    "{ratingId}": {
      "id": "string",
      "dishId": "string",
      "rating": 5,
      "comment": "string"
    }
  }
}
```

> A coleção de avaliações é armazenada com o nome `avaliations` (não `ratings`).

### FirebaseHelper

Singleton que centraliza as referências às coleções:

```java
FirebaseHelper.users()      // → DatabaseReference "users/"
FirebaseHelper.dishes()     // → DatabaseReference "dishes/"
FirebaseHelper.orders()     // → DatabaseReference "orders/"
FirebaseHelper.payments()   // → DatabaseReference "payments/"
FirebaseHelper.ratings()    // → DatabaseReference "avaliations/"
```

A persistência offline do Firebase está habilitada (`setPersistenceEnabled(true)`).

### FirebaseCallback

Interface genérica usada por todos os DAOs para comunicar resultados de forma assíncrona:

```java
public interface FirebaseCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
    static <T> FirebaseCallback<T> ignore() { /* no-op */ }
}
```

---

## 5. Modelos

### User

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID gerado no cadastro |
| `name` | String | Nome completo |
| `username` | String | Nome de usuário único |
| `email` | String | Email único |
| `password` | String | Senha (texto plano) |
| `balance` | double | Saldo disponível em R$ |
| `type` | UserType | ALUNO ou COZINHEIRO |

### Dish

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID |
| `name` | String | Nome do prato |
| `description` | String | Descrição |
| `price` | double | Preço em R$ |
| `type` | FoodType | Categoria do alimento |
| `imageName` | String | Referência à imagem |
| `available` | boolean | Disponível no cardápio |

### Order

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID |
| `items` | List\<OrderItem\> | Itens do pedido |
| `orderStatus` | OrderStatus | Status atual |
| `annotation` | String | Observação do estudante |
| `time` | long | Timestamp em milissegundos |

### OrderItem

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID |
| `dish` | Dish | Prato referenciado |
| `quantity` | int | Quantidade |

### Payment

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID |
| `orderId` | String | ID do pedido relacionado |
| `method` | PaymentMethod | PIX ou CASH |
| `amount` | double | Valor total pago |
| `timeMillis` | long | Timestamp do pagamento |

### Rating

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | String | UUID |
| `dishId` | String | ID do prato avaliado |
| `rating` | int | Nota de 1 a 5 |
| `comment` | String | Comentário opcional |

---

## 6. DAOs

Todos os DAOs seguem o mesmo contrato: métodos assíncronos com `FirebaseCallback<T>`.

### UserDAO

| Método | Retorno | Descrição |
|---|---|---|
| `insert(user, cb)` | void | Insere novo usuário |
| `findAll(cb)` | `List<User>` | Lista todos os usuários |
| `findById(id, cb)` | `User` | Busca por ID |
| `findByEmail(email, cb)` | `User` | Busca por email (usado no login) |
| `findByUsername(username, cb)` | `User` | Busca por username |
| `update(user, cb)` | void | Atualiza todos os campos |
| `updateBalance(id, balance, cb)` | void | Atualiza apenas o saldo |
| `delete(id, cb)` | void | Remove usuário |

### DishDAO

| Método | Retorno | Descrição |
|---|---|---|
| `insert(dish, cb)` | void | Insere novo prato |
| `findAll(cb)` | `List<Dish>` | Lista todos os pratos |
| `getAvailableDishes(cb)` | `List<Dish>` | Apenas pratos disponíveis |
| `getDishesByType(type, cb)` | `List<Dish>` | Filtra por FoodType |
| `findById(id, cb)` | `Dish` | Busca por ID |
| `update(dish, cb)` | void | Atualiza prato |
| `updateAvailability(id, available, cb)` | void | Liga/desliga disponibilidade |
| `delete(id, cb)` | void | Remove prato |
| `searchByName(query, cb)` | `List<Dish>` | Filtra pelo nome (client-side) |

### OrderDAO

| Método | Retorno | Descrição |
|---|---|---|
| `insert(order, cb)` | void | Insere pedido com itens aninhados |
| `findById(id, cb)` | `Order` | Busca pedido com itens |
| `findAll(cb)` | `List<Order>` | Lista todos os pedidos |
| `findByStatus(status, cb)` | `List<Order>` | Filtra por OrderStatus |
| `findRecentOrders(limit, cb)` | `List<Order>` | Últimos N pedidos por timestamp |
| `countByStatus(status, cb)` | `int` | Contagem por status |
| `updateStatus(id, status, cb)` | void | Avança status do pedido |
| `delete(id, cb)` | void | Remove pedido |
| `listenToOrder(id, cb)` | void | Escuta em tempo real um pedido |
| `listenToAllOrders(cb)` | void | Escuta em tempo real todos os pedidos |

### PaymentDAO

| Método | Retorno | Descrição |
|---|---|---|
| `insert(payment, cb)` | void | Registra pagamento |
| `findByOrderId(orderId, cb)` | `Payment` | Pagamento de um pedido |
| `findById(id, cb)` | `Payment` | Busca por ID |
| `findAll(cb)` | `List<Payment>` | Lista todos os pagamentos |

### RatingDAO

| Método | Retorno | Descrição |
|---|---|---|
| `insert(rating, cb)` | void | Registra avaliação |
| `findAll(cb)` | `List<Rating>` | Lista todas as avaliações |
| `findByDishId(dishId, cb)` | `List<Rating>` | Avaliações de um prato |
| `delete(id, cb)` | void | Remove avaliação |

---

## 7. Presenters

### LoginPresenter

- `login(email, password)` — busca usuário por email, compara senha, salva sessão e retorna o tipo de usuário para redirecionar para a tela correta.

### RegisterPresenter

- `register(name, username, email, password)` — valida campos, verifica unicidade de username e email em sequência, cria `User` com UUID e saldo inicial 0.0.

### StudentHomePresenter

- `getFeaturedDishes()` — carrega pratos disponíveis para exibição na home.
- `loadUserBalance()` — atualiza saldo via SessionManager.

### OrderPresenter

- `addItem(dish)` — adiciona ao carrinho ou incrementa quantidade.
- `removeItem(dish)` — decrementa ou remove do carrinho.
- `calculateTotal()` — soma `price * quantity` de todos os itens.
- `placeOrder(annotation)` — cria `Order` com status `PENDENTE`, timestamp atual, e salva os itens como estrutura aninhada no Firebase.
- `clearCart()` — limpa o estado do carrinho.

> O estado do carrinho é mantido na instância do presenter e é perdido se a Activity for destruída.

### PaymentPresenter

- `processPayment(orderId, method, amount)` — cria registro de `Payment` e, para CASH, deduz o valor do saldo do usuário via `UserDAO.updateBalance()` e atualiza o `SessionManager`.
- `calculateServiceFee(subtotal)` — retorna `subtotal * 0.10` (taxa de serviço fixa de 10%).

### ProfilePresenter

- `loadProfile()` — lê dados do `SessionManager` e busca informações atualizadas do usuário no Firebase.

### HistoryPresenter

- `loadHistory()` — busca todos os pedidos e filtra os do usuário logado.

### OrderStatusPresenter

- `loadOrderStatus(orderId)` — usa `listenToOrder()` para receber atualizações em tempo real.

### MenuPresenter (student)

- `loadMenu()` — carrega pratos disponíveis.
- `filterByType(type)` — filtra por `FoodType`.
- `search(query)` — usa `DishDAO.searchByName()`.

### RatingPresenter (student)

- `submitRating(dishId, rating, comment)` — valida nota (1–5), cria `Rating` com UUID e insere.
- `getRatingsByDishId(dishId)` — carrega avaliações e calcula média.

### KitchenHomePresenter

- `loadDashboard()` — conta pedidos por status em paralelo (`PENDENTE`, `PREPARANDO`, `PRONTO`) e carrega os 10 pedidos mais recentes.
- `loadOrdersByStatus(status)` — filtra pedidos por status.
- `advanceOrderStatus(orderId, currentStatus)` — avança linearmente: `PENDENTE → PREPARANDO → PRONTO → ENTREGUE` e recarrega o dashboard.

### KitchenMenuPresenter

- `loadDishes()` — lista todos os pratos.
- `toggleAvailability(dish)` — inverte `available` e atualiza no Firebase.
- `updateDish(dish)` — salva alterações de um prato.

---

## 8. Activities (View)

| Activity | Presenter | Descrição |
|---|---|---|
| `MainActivity` | — | Splash screen, redireciona se já logado |
| `LoginActivity` | `LoginPresenter` | Tela de login |
| `RegisterActivity` | `RegisterPresenter` | Cadastro de novo usuário |
| `ForgotPasswordActivity` | — | Solicita reset de senha |
| `ResetPasswordActivity` | — | Define nova senha |
| `StudentHomeActivity` | `StudentHomePresenter` | Home do estudante com pratos em destaque |
| `MenuActivity` | `MenuPresenter` | Cardápio completo com filtro por categoria |
| `OrderActivity` | `OrderPresenter` | Carrinho de compras |
| `PaymentActivity` | `PaymentPresenter` | Seleção de método de pagamento |
| `PaymentPixActivity` | `PaymentPresenter` | Detalhes do pagamento PIX |
| `OrderSuccessActivity` | — | Confirmação do pedido |
| `OrderStatusActivity` | `OrderStatusPresenter` | Acompanhamento do pedido em tempo real |
| `HistoryActivity` | `HistoryPresenter` | Histórico de pedidos do estudante |
| `RatingActivity` | `RatingPresenter` | Avaliação de prato |
| `ProfileActivity` | `ProfilePresenter` | Perfil e saldo do usuário |
| `KitchenHomeActivity` | `KitchenHomePresenter` | Dashboard da cozinha |
| `KitchenMenuActivity` | `KitchenMenuPresenter` | Gerenciamento de pratos |
| `KitchenAllOrdersActivity` | `KitchenHomePresenter` | Todos os pedidos |
| `EditDishActivity` | `KitchenMenuPresenter` | Edição de prato |

### BaseActivity

Classe abstrata que todos os activities estendem. Configura a status bar com aparência clara (ícones escuros).

### NavigationHelper

Configura o bottom navigation bar:

- `setupBottomNavigation()` — 5 abas: home, menu, pedidos, histórico, perfil (estudante).
- `setupKitchenNavigation()` — 4 abas: home, menu, histórico, perfil (cozinha).

Previne que a mesma Activity seja iniciada duas vezes seguidas.

---

## 9. Fluxos principais

### Login e cadastro

```
Usuário abre o app
    └─► MainActivity verifica SessionManager.isLoggedIn()
            ├─ true  → navega para StudentHomeActivity ou KitchenHomeActivity
            └─ false → LoginActivity

LoginActivity
    └─► LoginPresenter.login(email, password)
            └─► UserDAO.findByEmail()
                    ├─ não encontrado → exibe erro
                    ├─ senha errada   → exibe erro
                    └─ sucesso → SessionManager.saveSession()
                                    └─► navega por UserType
```

```
RegisterActivity
    └─► RegisterPresenter.register(name, username, email, password)
            ├─ valida campos obrigatórios
            ├─ UserDAO.findByUsername() → erro se já existe
            ├─ UserDAO.findByEmail()    → erro se já existe
            └─ UserDAO.insert(User) → volta para LoginActivity
```

### Fluxo de pedido (estudante)

```
StudentHomeActivity ──► MenuActivity ──► OrderActivity
                                              │
                              OrderPresenter.placeOrder()
                              (Order PENDENTE no Firebase)
                                              │
                                        PaymentActivity
                                        ┌────────────┐
                                      PIX            CASH
                                        │              │
                                PaymentPixActivity  desconta saldo
                                        │              │
                                   OrderSuccessActivity
                                              │
                                    OrderStatusActivity
                                    (escuta em tempo real)
```

### Fluxo de status (cozinha)

```
KitchenHomeActivity
    └─► KitchenHomePresenter.loadDashboard()
            └─► conta por status: PENDENTE | PREPARANDO | PRONTO

    └─► clique em pedido → advanceOrderStatus()
            PENDENTE ──► PREPARANDO ──► PRONTO ──► ENTREGUE
```

### Fluxo de pagamento

```
PaymentPresenter.processPayment(orderId, method, amount)
    ├─ cria Payment no Firebase
    ├─ PIX: registra e aguarda confirmação externa
    └─ CASH:
         ├─ UserDAO.updateBalance(newBalance)
         └─ SessionManager.updateBalance(newBalance)
```

### Fluxo de avaliação

```
RatingActivity
    └─► RatingPresenter.submitRating(dishId, rating, comment)
            ├─ valida: 1 ≤ rating ≤ 5
            └─ RatingDAO.insert(Rating com UUID)
```

---

## 10. Gerenciamento de sessão

`SessionManager` usa `SharedPreferences` com o nome `"unieat_session"`.

| Chave | Tipo | Descrição |
|---|---|---|
| `KEY_IS_LOGGED` | boolean | Se há sessão ativa |
| `KEY_ID` | String | ID do usuário |
| `KEY_NAME` | String | Nome completo |
| `KEY_USERNAME` | String | Username |
| `KEY_BALANCE` | float | Saldo atual |
| `KEY_USER_TYPE` | String | Nome do enum `UserType` |
| `KEY_REGISTRATION_DATE` | String | "Mês Ano" em português |

**Comportamentos relevantes:**
- `clearSession()` preserva `KEY_REGISTRATION_DATE`.
- `updateBalance()` atualiza apenas o saldo localmente (sem hit no Firebase).
- `isStudent()` / `isKitchen()` comparam `KEY_USER_TYPE` com os nomes dos enums.

---

## 11. Enums

### UserType
```java
ALUNO       // estudante
COZINHEIRO  // equipe da cozinha
```

### OrderStatus
```java
PENDENTE    // pedido feito, aguardando preparo
PREPARANDO  // em preparo na cozinha
PRONTO      // pronto para retirada
ENTREGUE    // retirado pelo estudante
```

### PaymentMethod
```java
PIX   // pagamento via PIX
CASH  // débito em saldo da conta
```

### FoodType (11 categorias)
```java
SALGADO_ASSADO
SALGADO_FRITO
SANDUICHE_NATURAL
REFEICAO
CUSCUZ
TAPIOCA
DOCE_CAKE
SNACK
BEBIDA_QUENTE
BEBIDA_GELADA
SOBREMESA_GELADA
```

---

## 12. Utilitários

### DateUtils

```java
DateUtils.formatDate(Date date)
// → "dd/MM/yyyy HH:mm"  ex: "24/05/2026 14:30"
```

### OrderUtils

```java
OrderUtils.formatStatus(OrderStatus status)
// → nome em português para exibição na UI
//   PENDENTE   → "Pendente"
//   PREPARANDO → "Preparando"
//   PRONTO     → "Pronto"
//   ENTREGUE   → "Entregue"
```

---

## 13. Configuração de build

**`app/build.gradle.kts`**

| Parâmetro | Valor |
|---|---|
| `namespace` | `com.example.unieat` |
| `compileSdk` | 36 (Android 16) |
| `minSdk` | 28 (Android 9) |
| `targetSdk` | 36 |
| `versionCode` | 1 |
| `versionName` | "1.0" |
| Compatibilidade Java | Java 11 |
| View Binding | habilitado |

**Dependências principais:**

| Biblioteca | Finalidade |
|---|---|
| `firebase-database` (BOM 34.13.0) | Realtime Database |
| `androidx.appcompat` | Suporte a versões anteriores |
| `androidx.constraintlayout` | Layouts responsivos |
| `com.google.android.material` | Material Design 3 |
| `androidx.activity:activity-ktx` | ActivityResult API |
| `junit` | Testes unitários |
| `espresso-core` | Testes de UI |

---

## 14. CI/CD

**Arquivo:** `.github/workflows/android.yml`

**Gatilhos:** push em `main` ou `develop`, pull request para `main`.

**Etapas:**

1. Checkout do repositório
2. Configuração do JDK 17 (distribuição Temurin)
3. Permissão de execução para `gradlew`
4. Injeção do `google-services.json` a partir do secret `GOOGLE_SERVICES_JSON`
5. Build do APK de debug: `./gradlew assembleDebug`
6. Execução dos testes unitários: `./gradlew test`

> O secret `GOOGLE_SERVICES_JSON` deve conter o conteúdo completo do arquivo JSON gerado no Firebase Console.
