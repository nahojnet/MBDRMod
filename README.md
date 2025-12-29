# MBDRMod - Application de Livraison

Application Android pour la gestion des formulaires de livraison avec envoi webhook.

## Fonctionnalités

- **7 formulaires successifs** :
  1. **Livraison** : Date, numéro client, numéro tour, horaires, quantités
  2. **Température** : Températures véhicule et produits (surgelé/frais)
  3. **Supports de livraison** : Dolies, 1/2 PAL, Europe, Autres, Panières, MegaBib
  4. **Collecte** : Films plastiques, Cartons, Aluminium
  5. **Anomalies** : Liste dynamique avec ajout/suppression
  6. **Remarques** : Zone de texte libre
  7. **Terminer** : Heure fin, départ, signatures

- **Écran principal** :
  - Bouton nouvelle saisie
  - Liste des livraisons avec statut (envoyé, en attente, échec)
  - Bouton pour renvoyer les données

- **Webhook** :
  - Envoi automatique des données en JSON
  - Retry automatique en cas d'échec
  - Conservation des données pendant une durée configurable

## Configuration

Le fichier `app/src/main/assets/config.json` permet de configurer :

```json
{
  "webhook": {
    "url": "https://votre-url-webhook.com",
    "retryIntervalMinutes": 15,
    "dataRetentionDays": 30
  },
  "requiredFields": {
    // Configuration des champs obligatoires par formulaire
  }
}
```

## Technologies utilisées

- **Kotlin** avec Jetpack Compose
- **Room** pour la persistance locale
- **Retrofit** pour les appels HTTP
- **WorkManager** pour les retry en arrière-plan
- **Hilt** pour l'injection de dépendances
- **Material Design 3**

## Prérequis

- Android SDK 34
- Kotlin 1.9+
- Gradle 8.3+

## Installation

1. Cloner le projet
2. Ouvrir dans Android Studio
3. Configurer l'URL webhook dans `config.json`
4. Build et installer sur appareil Android 11+

## Cible

- Android 11 (API 30) minimum
- Écran 5 pouces, résolution 1080x1920

## Structure du projet

```
app/
├── src/main/
│   ├── assets/
│   │   └── config.json          # Configuration
│   ├── java/com/mbdrmod/delivery/
│   │   ├── data/
│   │   │   ├── local/           # Room DAO, Database
│   │   │   ├── model/           # Entités et modèles
│   │   │   ├── remote/          # Service Retrofit
│   │   │   └── repository/      # Repository
│   │   ├── di/                  # Modules Hilt
│   │   ├── ui/
│   │   │   ├── components/      # Composants réutilisables
│   │   │   ├── screens/         # Écrans (Home, Forms)
│   │   │   └── theme/           # Thème Material
│   │   ├── util/                # Utilitaires
│   │   └── worker/              # WorkManager
│   └── res/                     # Ressources Android
```

## Format JSON Webhook

```json
{
  "id": 1234567890,
  "timestamp": 1704067200000,
  "data": {
    "livraison": {
      "client": {
        "date_livraison": "2024/01/01",
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
        "surgele": -18.0,
        "frais": 4.0
      },
      "produits": {
        "surgele": {
          "temperature": -20.0,
          "methode": "inertia_probe"
        },
        "frais": {
          "temperature": 3.5,
          "methode": "manual_control"
        }
      }
    },
    "supports_livraison": { ... },
    "collecte": { ... },
    "anomalies": [ ... ],
    "remarques": "...",
    "fin_livraison": { ... }
  }
}
```
