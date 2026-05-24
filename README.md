# UniEat

Aplicativo Android para pedidos de refeições em cantinas universitárias. Estudantes navegam pelo cardápio, fazem pedidos e acompanham o status em tempo real. A equipe da cozinha gerencia pratos, recebe e avança os pedidos pelo fluxo de preparo.

---

## Funcionalidades

**Estudante**
- Login e cadastro de conta
- Visualização de pratos em destaque e cardápio completo por categoria
- Carrinho de compras com anotações por pedido
- Pagamento via PIX ou saldo em conta (com taxa de serviço de 10%)
- Acompanhamento do status do pedido em tempo real
- Histórico de pedidos
- Avaliação de pratos (1–5 estrelas + comentário)
- Perfil com saldo e data de cadastro

**Cozinha**
- Dashboard com contagem de pedidos por status
- Avanço do status do pedido: `Pendente → Preparando → Pronto → Entregue`
- Gerenciamento de pratos (nome, preço, tipo, disponibilidade)
- Visualização de todos os pedidos

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 11 |
| Plataforma | Android (minSdk 28 / targetSdk 36) |
| Arquitetura | MVP (Model-View-Presenter) |
| Backend | Firebase Realtime Database |
| UI | View Binding, Material Design 3 |
| CI/CD | GitHub Actions |

---

## Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17
- Conta no Firebase com Realtime Database habilitado
- Arquivo `google-services.json` configurado (veja abaixo)

---

## Como rodar

**1. Clone o repositório**
```bash
git clone https://github.com/felipe-damasceno19/UniEat.git
cd UniEat
```

**2. Configure o Firebase**

- Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
- Adicione um app Android com o pacote `com.example.unieat`
- Baixe o `google-services.json` gerado e coloque em `app/google-services.json`
- No Firebase Console, habilite o **Realtime Database** em modo de teste

**3. Abra no Android Studio**

Abra a pasta raiz do projeto. O Gradle sincroniza automaticamente as dependências.

**4. Execute**

Conecte um dispositivo físico ou emulador (API 28+) e pressione **Run**.

> O app popula automaticamente dados de teste no primeiro login (usuários e pratos de exemplo).

---

## Contas de teste

| Tipo | Email | Senha |
|---|---|---|
| Estudante | `felipe@teste.com` | `123456` |
| Cozinha | `cozinha@teste.com` | `123456` |

> Esses dados são inseridos pelo `DataSeeder` na primeira execução.

---

## Estrutura do projeto

```
app/src/main/java/com/example/unieat/
├── adapter/      # Adapters de RecyclerView
├── dao/          # Acesso ao Firebase (UserDAO, DishDAO, OrderDAO, ...)
├── data/         # FirebaseHelper, SessionManager, DataSeeder
├── enums/        # UserType, OrderStatus, PaymentMethod, FoodType
├── model/        # User, Dish, Order, OrderItem, Payment, Rating
├── presenter/    # Lógica de negócio (MVP)
│   ├── kitchen/  # KitchenHomePresenter, KitchenMenuPresenter
│   └── student/  # MenuPresenter, RatingPresenter
├── util/         # DateUtils, OrderUtils
└── view/         # Activities (UI)
```

---

## CI/CD

O pipeline do GitHub Actions executa a cada push em `main` ou `develop`:

1. Compila o APK de debug (`./gradlew assembleDebug`)
2. Roda os testes unitários (`./gradlew test`)
3. Injeta o `google-services.json` via secret `GOOGLE_SERVICES_JSON`

---

## Licença

Projeto acadêmico. Todos os direitos reservados aos autores.
