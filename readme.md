# JavaGame Yu-Gi-Oh 🎮

Un projet Java qui implémente un jeu inspiré de l’univers Yu-Gi-Oh.

Ce projet utilise **JavaFX** pour l’interface graphique et un script `game.bat` pour compiler et lancer automatiquement le jeu.

## 🚀 Fonctionnalités

* Interface graphique avec JavaFX

* Compilation et lancement automatisés via `game.bat`

* Gestion des cartes et des règles du jeu

* Documentation incluse (`doc/`, `docs/`)

## 📂 Structure du projet

* `src/` → Code source Java

* `doc/` et `docs/` → Documentation

* `game.bat` → Script de lancement (Windows)

* `readme.md` → Ce fichier

* *(les dossiers `bin/`, `out/`, `report/`, `javafx/` sont ignorés par Git car générés ou trop lourds)*

## ⚙️ Prérequis Logiciels

Pour développer ou lancer ce projet, vous avez besoin des outils suivants :

### 1. Java Development Kit (JDK)

Vous pouvez utiliser une version de **Java 17 ou supérieure**.

* 📥 **Télécharger Java JDK 17 :** [Eclipse Adoptium (Temurin)](https://adoptium.net/temurin/releases/?version=17) ou [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 2. Environnement de Développement (IDE)

Bien que le script `game.bat` permette de lancer le jeu sans IDE, un environnement est recommandé pour lire et modifier le code.

* 📥 **VS Code** (avec Extension Pack for Java) : [Télécharger ici](https://code.visualstudio.com/)

* 📥 **IntelliJ IDEA** (Recommandé pour JavaFX) : [Télécharger ici](https://www.jetbrains.com/idea/download/)

* 📥 **Eclipse** : [Télécharger ici](https://www.eclipse.org/downloads/)


### 3. JavaFX SDK (Graphismes)

⚠️ **Attention :** Même si vous avez une version plus récente de Java (ex: 21), vous **devez** utiliser le **SDK JavaFX version 17** pour que le projet fonctionne correctement avec la configuration actuelle.

## 📥 Installation de JavaFX (Obligatoire)

Le moteur graphique n'est pas inclus dans le dépôt (trop lourd). Vous devez l'installer manuellement.

### Étape 1 : Télécharger le SDK

👉 **Lien direct :** [Télécharger JavaFX SDK 17.0.17 (Windows)](https://gluonhq.com/products/javafx/)
*(Descendez jusqu'à la version 17, Type: SDK, Platform: Windows x64)*

### Étape 2 : Installation dans le projet

1. Ouvrez le fichier ZIP téléchargé.

2. Allez dans le dossier `JavaGame_YuGiOh` (la racine de ce projet).

3. Placez le contenu extrait pour que le chemin soit **exactement** :

   `./javafx/windows/javafx-sdk-17.0.17/`

   *Vérification : Le fichier `javafx.controls.jar` doit se trouver dans `./javafx/windows/javafx-sdk-17.0.17/lib/`.*

## ▶️ Lancer le projet

Sous Windows, une fois JavaFX installé :

1. Double-cliquez sur **`game.bat`**.

2. Le script va automatiquement :

   * Nettoyer les anciens fichiers de compilation.

   * Compiler le code avec Java 17 et les modules JavaFX.

   * Lancer le jeu.

## 📌 Notes importantes

* **Compatibilité :** Assurez-vous que votre variable d'environnement `JAVA_HOME` pointe bien vers votre installation de Java 17+.

* **Git :** Les binaires JavaFX (`.dll`, `.jar`) sont ignorés par Git pour garder le projet léger. Seule la structure des dossiers est conservée.

## 👨‍💻 Auteur

Projet développé par **Nicolas LAW-SHUN** dans le cadre de la formation à Epitech La Réunion.