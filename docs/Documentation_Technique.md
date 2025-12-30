# Documentation Technique - MBDRMod Delivery

## Application de Suivi de Livraison

**Version** : 1.0
**Package** : com.mbdrmod.delivery
**SDK Minimum** : Android 11 (API 30)
**SDK Cible** : Android 15 (API 35)

---

## 1. Architecture de l'Application

### 1.1 Pattern Architectural

L'application suit le pattern **MVVM (Model-View-ViewModel)** avec une architecture en couches :

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                            │
│  (Jetpack Compose, Screens, Components)                  │
├─────────────────────────────────────────────────────────┤
│                   ViewModel Layer                        │
│  (DeliveryViewModel, StateFlow, Business Logic)          │
├─────────────────────────────────────────────────────────┤
│                  Repository Layer                        │
│  (DeliveryRepository - Data abstraction)                 │
├─────────────────────────────────────────────────────────┤
│                    Data Layer                            │
│  (Room Database, Retrofit API, WorkManager)              │
└─────────────────────────────────────────────────────────┘
```

### 1.2 Injection de Dépendances

L'application utilise **Hilt** pour l'injection de dépendances.

**Configuration dans `AppModule.kt`** :
- `DeliveryDatabase` : Base de données Room (Singleton)
- `DeliveryDao` : DAO pour accès aux données
- `WebhookService` : Service Retrofit pour l'API
- `DeliveryRepository` : Repository pour la logique métier
- `ConfigManager` : Gestionnaire de configuration

---

## 2. Structure des Fichiers

```
app/src/main/java/com/mbdrmod/delivery/
├── DeliveryApplication.kt          # Application Hilt
├── MainActivity.kt                 # Activité principale
├── data/
│   ├── local/
│   │   ├── Converters.kt          # Convertisseurs Room (JSON)
│   │   ├── DeliveryDao.kt         # Interface DAO
│   │   └── DeliveryDatabase.kt    # Base de données Room
│   ├── model/
│   │   ├── AppConfig.kt           # Modèle de configuration
│   │   ├── DeliveryData.kt        # Entité principale
│   │   └── WebhookPayload.kt      # Payload pour l'API
│   ├── remote/
│   │   └── WebhookService.kt      # Interface Retrofit
│   └── repository/
│       └── DeliveryRepository.kt  # Repository
├── di/
│   └── AppModule.kt               # Module Hilt
├── ui/
│   ├── components/
│   │   └── FormComponents.kt      # Composants réutilisables
│   ├── navigation/
│   │   └── Navigation.kt          # Navigation Compose
│   ├── screens/
│   │   ├── DeliveryViewModel.kt   # ViewModel principal
│   │   ├── MainScreen.kt          # Écran principal
│   │   ├── home/
│   │   │   └── HomeScreen.kt      # Liste des livraisons
│   │   └── forms/
│   │       ├── FormContainerScreen.kt
│   │       ├── Form1LivraisonScreen.kt
│   │       ├── Form2TemperatureScreen.kt
│   │       ├── Form3SupportsScreen.kt
│   │       ├── Form4CollecteScreen.kt
│   │       ├── Form5AnomaliesScreen.kt
│   │       ├── Form6RemarquesScreen.kt
│   │       └── Form7TerminerScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   └── ConfigManager.kt           # Gestionnaire de config
└── worker/
    └── WebhookRetryWorker.kt      # Worker pour retry
```

---

## 3. Base de Données

### 3.1 Room Database

**Nom** : `delivery_database`

**Entité principale** : `DeliveryData`

| Champ | Type | Description |
|-------|------|-------------|
| id | Long | Clé primaire auto-générée |
| deliveryDate | String | Date de livraison (AAAA/MM/JJ) |
| clientNumber | String | Numéro client |
| tourNumber | Int? | Numéro de tour |
| expectedArrivalTime | String | Heure prévue (HH:mm) |
| actualArrivalTime | String | Heure réelle (HH:mm) |
| deliveryStartTime | String | Heure début (HH:mm) |
| supports | Int? | Nombre de supports |
| weightKg | Double? | Poids en kg |
| packages | Int? | Nombre de colis |
| volumeM3 | Double? | Volume en m³ |
| vehicleFrozenTemp | Double? | Température véhicule surgelé |
| vehicleFreshTemp | Double? | Température véhicule frais |
| productFrozenTemp | Double? | Température produit surgelé |
| productFrozenMethod | String | Méthode contrôle surgelé |
| productFreshTemp | Double? | Température produit frais |
| productFreshMethod | String | Méthode contrôle frais |
| doliesDelivered/Returned | Int? | Dolies livrées/rendues |
| halfPalDelivered/Returned | Int? | 1/2 PAL livrées/rendues |
| europeDelivered/Returned | Int? | Europe livrées/rendues |
| othersDelivered/Returned | Int? | Autres livrées/rendues |
| panieresDelivered/Returned | Int? | Panières livrées/rendues |
| megabibDelivered/Returned | Int? | MegaBib livrées/rendues |
| plasticFilms | Int? | Films plastiques collectés |
| cardboard | Int? | Cartons collectés |
| aluminum | Int? | Aluminium collecté |
| anomalies | List\<Anomaly\> | Liste des anomalies (JSON) |
| remarks | String | Remarques |
| deliveryEndTime | String | Heure fin livraison |
| departureTime | String | Heure départ |
| driverName | String | Nom conducteur |
| managerName | String | Nom manager |
| createdAt | Long | Timestamp création |
| sendStatus | SendStatus | Statut d'envoi |
| lastSendAttempt | Long? | Dernière tentative envoi |
| sentAt | Long? | Timestamp envoi réussi |

### 3.2 Modèle Anomaly

```kotlin
data class Anomaly(
    val id: Long,           // Identifiant unique
    val wrin: String,       // Code WRIN produit
    val description: String,// Description produit
    val quantity: Int?,     // Quantité concernée
    val missing: Boolean,   // Manquant
    val refused: Boolean,   // Refusé
    val excess: Boolean,    // Excédent
    val reason: String      // Motif
)
```

### 3.3 Statuts d'Envoi

```kotlin
enum class SendStatus {
    DRAFT,    // Brouillon (sauvegarde automatique)
    PENDING,  // En attente d'envoi
    SENT,     // Envoyé avec succès
    FAILED    // Échec d'envoi (retry programmé)
}
```

---

## 4. API et Webhook

### 4.1 Configuration

Fichier : `app/src/main/assets/config.json`

```json
{
  "webhook": {
    "url": "https://example.com/webhook",
    "retryIntervalMinutes": 15,
    "dataRetentionDays": 30
  }
}
```

### 4.2 Format du Payload JSON

```json
{
  "id": 123,
  "timestamp": 1703936400000,
  "data": {
    "livraison": {
      "client": {
        "date_livraison": "2024/12/30",
        "numero_client": "CLI001",
        "numero_tour": 5
      },
      "horaire": {
        "heure_prevue_arrivee": "08:00",
        "heure_reelle_arrivee": "08:15",
        "heure_debut_livraison": "08:20"
      },
      "quantite": {
        "supports": 10,
        "poids_kg": 150.5,
        "colis": 25,
        "volume_m3": 2.5
      }
    },
    "temperature": {
      "vehicule": {
        "surgele": -18.5,
        "frais": 4.0
      },
      "produits": {
        "surgele": {
          "temperature": -18.0,
          "methode": "inertia_probe"
        },
        "frais": {
          "temperature": 3.5,
          "methode": "manual_control"
        }
      }
    },
    "supports_livraison": {
      "dolies": { "livrees": 5, "rendues": 3 },
      "demi_pal": { "livrees": 2, "rendues": 1 },
      "europe": { "livrees": 0, "rendues": 0 },
      "autres": { "livrees": 0, "rendues": 0 },
      "panieres": { "livrees": 10, "rendues": 8 },
      "megabib": { "livrees": 0, "rendues": 0 }
    },
    "collecte": {
      "films_plastiques": 5,
      "cartons_collecte": 12,
      "aluminium": 3
    },
    "anomalies": [
      {
        "wrin": "123456",
        "description": "Produit XYZ",
        "quantite": 2,
        "manquant": true,
        "refus": false,
        "excedent": false,
        "motif": "Non livré par fournisseur"
      }
    ],
    "remarques": "RAS",
    "fin_livraison": {
      "heure_fin_livraison": "09:30",
      "heure_depart": "09:45",
      "nom_conducteur": "Jean Dupont",
      "nom_manager": "Marie Martin"
    }
  }
}
```

### 4.3 Gestion des Erreurs et Retry

Le `WebhookRetryWorker` utilise **WorkManager** pour :
- Réessayer automatiquement les envois échoués
- Intervalle configurable (défaut : 15 minutes)
- Contrainte : connexion réseau requise
- Exécution périodique en arrière-plan

---

## 5. Dépendances

### 5.1 Versions Principales

| Bibliothèque | Version |
|--------------|---------|
| Kotlin | 2.0.21 |
| Compose BOM | 2024.12.01 |
| Material 3 | (via BOM) |
| Room | 2.6.1 |
| Hilt | 2.52 |
| Retrofit | 2.11.0 |
| WorkManager | 2.10.0 |
| Navigation Compose | 2.8.5 |
| Coroutines | 1.9.0 |

### 5.2 Plugins Gradle

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
```

---

## 6. Configuration de Build

### 6.1 Versions SDK

- **minSdk** : 30 (Android 11)
- **targetSdk** : 35 (Android 15)
- **compileSdk** : 35

### 6.2 Java/Kotlin

- **Java** : 21
- **JVM Target** : 21

### 6.3 Build Variants

- **debug** : Débogage activé
- **release** : ProGuard désactivé, signature debug

---

## 7. Flux de Données

### 7.1 Sauvegarde Automatique

```
Saisie utilisateur
       ↓
 onValueChange()
       ↓
viewModel.updateDelivery()
       ↓
    saveDraft()
       ↓
 repository.saveDelivery()
       ↓
   Room Database
```

### 7.2 Envoi au Webhook

```
Bouton "Terminer"
       ↓
viewModel.finishAndSend()
       ↓
 Statut → PENDING
       ↓
repository.sendDelivery()
       ↓
  Retrofit POST
       ↓
   Succès? ──→ Statut → SENT
       ↓
   Échec? ──→ Statut → FAILED
              ↓
        WebhookRetryWorker
              ↓
         Retry périodique
```

---

## 8. Composants UI Réutilisables

### 8.1 FormComponents.kt

| Composant | Description |
|-----------|-------------|
| `FormTextField` | Champ texte avec validation |
| `FormNumberField` | Champ numérique entier |
| `FormDecimalField` | Champ numérique décimal |
| `FormDateField` | Champ date (AAAA/MM/JJ) |
| `FormTimeField` | Sélecteur heure (dropdown) |
| `FormTemperatureField` | 3 dropdowns (signe, unités, décimale) |
| `FormRadioGroup` | Groupe de boutons radio |
| `FormCheckboxField` | Case à cocher avec label |
| `FormCard` | Carte conteneur |
| `SectionHeader` | Titre de section |
| `FormNavigationButtons` | Boutons Précédent/Suivant/Terminer |

---

## 9. Sécurité

### 9.1 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 9.2 Recommandations

- Configurer HTTPS pour le webhook en production
- Utiliser un keystore de production pour la signature
- Activer ProGuard/R8 pour l'obfuscation

---

## 10. Tests

### 10.1 Dépendances de Test

- JUnit 4.13.2
- AndroidX Test
- Espresso 3.6.1
- Compose UI Test

### 10.2 Exécution

```bash
# Tests unitaires
./gradlew test

# Tests instrumentés
./gradlew connectedAndroidTest
```

---

## 11. Build et Déploiement

### 11.1 Compilation

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

### 11.2 Localisation APK

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

---

**Document généré le** : Décembre 2024
**Auteur** : MBDRMod Team
