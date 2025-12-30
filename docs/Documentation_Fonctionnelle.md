# Documentation Fonctionnelle - MBDRMod Delivery

## Application de Suivi de Livraison

**Version** : 1.0
**Plateforme** : Android 11 et supérieur

---

## 1. Présentation Générale

### 1.1 Objectif

L'application MBDRMod Delivery permet aux conducteurs-livreurs de saisir et transmettre les informations relatives à leurs livraisons. Les données sont envoyées automatiquement vers un serveur central via webhook.

### 1.2 Fonctionnalités Principales

- Saisie complète des informations de livraison
- Enregistrement automatique des brouillons
- Envoi des données vers un serveur webhook
- Gestion des échecs d'envoi avec retry automatique
- Historique des livraisons

---

## 2. Écran d'Accueil

### 2.1 Description

L'écran d'accueil affiche la liste des livraisons enregistrées et permet d'en créer de nouvelles.

### 2.2 Éléments Affichés

Pour chaque livraison :
- **Date de livraison**
- **Numéro de client**
- **Icône de statut** :
  - 📝 Crayon gris : Brouillon
  - ⏳ Horloge orange : En attente d'envoi
  - ✅ Coche verte : Envoyé avec succès
  - ❌ Croix rouge : Échec d'envoi

### 2.3 Actions Disponibles

| Bouton | Action |
|--------|--------|
| **+ Nouvelle Livraison** | Crée une nouvelle fiche de livraison |
| **Modifier** (sur une ligne) | Ouvre la fiche en édition |
| **Renvoyer** (si échec) | Retente l'envoi au serveur |
| **Supprimer** | Supprime la livraison (avec confirmation) |

---

## 3. Formulaires de Saisie

L'application comporte **7 formulaires** successifs accessibles via les boutons de navigation.

### Navigation

- **Icône Maison** (en haut à gauche) : Retour à l'accueil avec sauvegarde
- **Bouton Précédent** : Revenir au formulaire précédent
- **Bouton Suivant** : Passer au formulaire suivant
- **Bouton Terminer** : Valider et envoyer (dernier formulaire)

---

## 4. Formulaire 1 : Livraison

### 4.1 Section Client

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Date de Livraison | Date | ✅ | Format AAAA/MM/JJ |
| Numéro de client | Texte | ✅ | Identifiant client |
| Numéro de Tour | Nombre | ✅ | Numéro de la tournée |

### 4.2 Section Horaire

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Heure prévue d'arrivée | Heure | ❌ | Format HH:mm (dropdown) |
| Heure réelle d'arrivée | Heure | ❌ | Format HH:mm (dropdown) |
| Heure de début de livraison | Heure | ❌ | Format HH:mm (dropdown) |

### 4.3 Section Quantité

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Supports | Nombre | ❌ | Nombre de supports |
| Poids en Kg | Décimal | ❌ | Poids total |
| Colis | Nombre | ❌ | Nombre de colis |
| Volume en M3 | Décimal | ❌ | Volume total |

---

## 5. Formulaire 2 : Température

### 5.1 Section Véhicule

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Surgelé | Température | ❌ | Température compartiment surgelé |
| Frais | Température | ❌ | Température compartiment frais |

### 5.2 Section Produits

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Surgelé (température) | Température | ❌ | Température produits surgelés |
| Méthode de contrôle (Surgelé) | Radio | ❌ | Sondes à inertie / Contrôle manuel |
| Frais (température) | Température | ❌ | Température produits frais |
| Méthode de contrôle (Frais) | Radio | ❌ | Sondes à inertie / Contrôle manuel |

### 5.3 Saisie des Températures

Les températures sont saisies via **3 menus déroulants** :
1. **Signe** : + ou -
2. **Unités** : 0 à 25
3. **Décimale** : 0 à 9

Exemple : -18,5°C se saisit : `-` `18` `,` `5`

> **Note** : Pour les températures surgelées, le signe `-` est présélectionné.

---

## 6. Formulaire 3 : Supports de Livraison

Pour chaque type de support, saisir le nombre livré et rendu :

| Type de Support | Livrées | Rendues |
|-----------------|:-------:|:-------:|
| Dolies | Nombre | Nombre |
| 1/2 PAL | Nombre | Nombre |
| Europe | Nombre | Nombre |
| Autres | Nombre | Nombre |
| Panières | Nombre | Nombre |
| MegaBib | Nombre | Nombre |

---

## 7. Formulaire 4 : Collecte

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Films Plastiques | Nombre | ❌ | Quantité collectée |
| Cartons Collecte | Nombre | ❌ | Quantité collectée |
| Aluminium | Nombre | ❌ | Quantité collectée |

---

## 8. Formulaire 5 : Anomalies

### 8.1 Ajout d'Anomalie

Cliquer sur le bouton **+** en haut à droite pour ajouter une nouvelle anomalie.

### 8.2 Champs par Anomalie

| Champ | Type | Description |
|-------|------|-------------|
| WRIN | Texte | Code produit |
| Description | Texte | Description du produit |
| Quantité | Nombre | Quantité concernée |
| Manquant | Case à cocher | Produit manquant |
| Refus | Case à cocher | Produit refusé |
| Excédent | Case à cocher | Produit en excédent |
| Motif | Texte | Raison de l'anomalie |

### 8.3 Suppression d'Anomalie

Cliquer sur l'icône **poubelle** rouge à côté de l'anomalie à supprimer.

---

## 9. Formulaire 6 : Remarques

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Remarques | Texte libre | ❌ | Zone de saisie multi-lignes |

---

## 10. Formulaire 7 : Terminer

### 10.1 Section Fin de Livraison

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Heure de fin de livraison | Heure | ❌ | Format HH:mm |
| Heure de départ | Heure | ❌ | Format HH:mm |

### 10.2 Section Signatures

| Champ | Type | Obligatoire | Description |
|-------|------|:-----------:|-------------|
| Nom du conducteur | Texte | ✅ | Nom complet |
| Nom du manager | Texte | ❌ | Nom du responsable site |

### 10.3 Validation

En cliquant sur **Terminer** :
1. Les champs obligatoires sont vérifiés
2. Les données sont envoyées au serveur
3. Un message confirme le succès ou l'échec
4. Retour à l'écran d'accueil

---

## 11. Sauvegarde Automatique

### 11.1 Fonctionnement

L'application sauvegarde automatiquement les données :
- À chaque modification d'un champ
- Lors du changement de formulaire
- Lors du retour à l'accueil

### 11.2 Brouillons

Les fiches non terminées sont sauvegardées comme **brouillons** et peuvent être reprises ultérieurement depuis l'écran d'accueil.

---

## 12. Gestion des Envois

### 12.1 Statuts

| Statut | Description | Action Possible |
|--------|-------------|-----------------|
| **Brouillon** | Fiche en cours de saisie | Modifier |
| **En attente** | Envoi en cours | Attendre |
| **Envoyé** | Transmission réussie | Consulter |
| **Échec** | Erreur de transmission | Renvoyer |

### 12.2 Retry Automatique

En cas d'échec d'envoi (problème réseau, serveur indisponible) :
- La fiche passe en statut "Échec"
- L'application retente automatiquement l'envoi toutes les 15 minutes
- L'utilisateur peut aussi forcer un renvoi manuel

---

## 13. Validation des Champs

### 13.1 Champs Obligatoires

Les champs obligatoires sont marqués d'un astérisque (**\***).

Si un champ obligatoire n'est pas rempli :
- Le champ s'affiche en rouge
- Un message d'erreur apparaît
- Le passage au formulaire suivant est bloqué

### 13.2 Champs Obligatoires par Défaut

- Date de livraison
- Numéro de client
- Numéro de tour
- Nom du conducteur

> **Note** : Les champs obligatoires sont configurables dans le fichier `config.json`.

---

## 14. Récapitulatif des Icônes

| Icône | Signification |
|:-----:|---------------|
| 🏠 | Retour à l'accueil |
| ➕ | Ajouter une anomalie |
| 🗑️ | Supprimer |
| 📝 | Brouillon |
| ⏳ | En attente d'envoi |
| ✅ | Envoyé avec succès |
| ❌ | Échec d'envoi |
| 🔄 | Renvoyer |

---

## 15. Conseils d'Utilisation

### 15.1 Avant la Livraison

1. Ouvrir l'application
2. Créer une nouvelle livraison
3. Remplir les informations client et horaires prévus

### 15.2 Pendant la Livraison

1. Compléter les relevés de température
2. Noter les supports livrés/rendus
3. Enregistrer les anomalies éventuelles

### 15.3 Après la Livraison

1. Renseigner les heures de fin et départ
2. Saisir les noms (conducteur et manager)
3. Ajouter les remarques si nécessaire
4. Cliquer sur "Terminer" pour envoyer

### 15.4 En Cas de Problème Réseau

- Les données sont sauvegardées localement
- L'envoi sera retentré automatiquement
- Vérifier le statut depuis l'écran d'accueil

---

## 16. Support

Pour toute question ou problème :
- Contacter le service informatique
- Consulter la documentation technique

---

**Document généré le** : Décembre 2024
**Version Application** : 1.0
