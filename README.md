# Calculadora CLT

App Android (Kotlin + Jetpack Compose, Material 3) com calculadoras trabalhistas: Rescisão, Salário Líquido, Folha de Pagamento, Férias, Horas Extras, Seguro-Desemprego, FGTS, 13º Salário, Tempo entre Datas, Reajustes e Dissídios, e Salário por Hora.

## Build local

Requer JDK 17 e Android SDK (compileSdk 36). Com o `local.properties` apontando `sdk.dir` para o seu SDK:

```
./gradlew test                 # testes unitários (core:domain)
./gradlew :app:assembleDebug   # gera o APK de debug
```

## Estrutura

- `app/` — UI (Compose), ViewModels, navegação, anúncios (AdMob)
- `core/domain/` — módulo Kotlin puro (sem Android) com toda a lógica de cálculo e as tabelas fiscais, testável sem emulador
- `core/designsystem/` — tema Material 3 e componentes Compose reutilizáveis
- `core/common/` — formatação de moeda/data em pt-BR

## Anúncios (AdMob)

A conta AdMob já está configurada. `app/src/main/kotlin/com/calculadoraclt/app/ads/AdIds.kt` e o placeholder `admobAppId` em `app/build.gradle.kts` usam os **IDs de teste oficiais do Google em builds de debug** e os **IDs reais só em builds de release** (`./gradlew :app:assembleRelease` ou `:app:bundleRelease`). Isso evita que testes no dia a dia (emulador, abrir o app várias vezes) gerem tráfego suspeito na conta real — só instale um APK/AAB de release no seu próprio celular se ele estiver registrado como [dispositivo de teste](https://support.google.com/admob/answer/9691433) no AdMob, senão evite clicar nos anúncios.

## Configurar o CI/CD (GitHub Actions)

Há dois workflows em `.github/workflows/`:

- **`ci.yml`** — roda testes e compila um APK de debug em toda branch/PR (exceto `main`). Não precisa de nenhuma configuração adicional.
- **`release.yml`** — a cada push na `main`, roda os testes e, se habilitado, gera um APK e um Android App Bundle assinados, publica na Play Store (faixa **internal testing**) e também cria uma release no GitHub (aba **Releases** do repositório) anexando o APK e o AAB, com a tag `build-<número da execução>`.

O job de publicação fica **desativado por padrão** (só roda testes) até você configurar os itens abaixo — assim um push na `main` sem essa configuração não falha, só pula a publicação.

### 1. Gerar o keystore de assinatura de release

Gere localmente (nunca comitar o arquivo gerado):

```
keytool -genkeypair -v -keystore release.keystore -alias calculadoraclt -keyalg RSA -keysize 2048 -validity 10000
```

Guarde esse arquivo e as senhas em local seguro (ex: gerenciador de senhas) — se você perder o keystore, **não será possível publicar atualizações do app na Play Store nunca mais**, só um app novo.

### 2. Criar o app na Play Console (primeira vez é sempre manual)

A API do Google Play só publica **atualizações** de um app que já existe no Play Console — a primeira versão precisa ser enviada manualmente pelo [Play Console](https://play.google.com/console):

1. Criar o app com `applicationId` = `com.calculadoraclt.app`
2. Preencher a ficha da loja, política de privacidade, formulário de Data Safety e classificação de conteúdo
3. Gerar um AAB assinado localmente (`./gradlew :app:bundleRelease` com o keystore acima) e subir manualmente na faixa de teste interno pelo menos uma vez

### 3. Criar a service account do Google Cloud (para o GitHub Actions publicar sozinho depois)

1. No [Google Cloud Console](https://console.cloud.google.com), criar uma service account e gerar uma chave JSON
2. No Play Console, em **Configurações > Acesso à API**, vincular essa service account com permissão de "Release manager" (ou similar) para o app

### 4. Configurar os segredos no GitHub

Em **Settings > Secrets and variables > Actions** do repositório:

**Secrets:**
- `RELEASE_KEYSTORE_BASE64` — o keystore gerado no passo 1, codificado em base64 (`base64 -w0 release.keystore`)
- `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` — do mesmo keystore
- `PLAY_STORE_SERVICE_ACCOUNT_JSON` — conteúdo completo do JSON da service account do passo 3

**Variables:**
- `PLAY_STORE_PUBLISHING_ENABLED` = `true` — só depois de completar os passos 1 a 4

Depois disso, todo push na `main` publica automaticamente na faixa de teste interno. Promover para produção continua sendo manual pelo Play Console — é o padrão de mercado (evita publicar direto em produção sem revisão).
