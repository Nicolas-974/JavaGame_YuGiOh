# JavaGame Yu-Gi-Oh 🎮

Un projet Java qui implémente un jeu inspiré de l’univers Yu-Gi-Oh.  
Ce projet utilise **JavaFX** pour l’interface graphique et un script `game.bat` pour compiler et lancer automatiquement le jeu.

---

## 🚀 Fonctionnalités
- Interface graphique avec JavaFX
- Compilation et lancement automatisés via `game.bat`
- Gestion des cartes et des règles du jeu
- Documentation incluse (`doc/`, `docs/`)

---

## 📂 Structure du projet
- `src/` → Code source Java
- `doc/` et `docs/` → Documentation
- `game.bat` → Script de lancement (Windows)
- `readme.md` → Ce fichier
- *(les dossiers `bin/`, `out/`, `report/`, `javafx/` sont ignorés par Git car générés ou trop lourds)*

---

## ⚙️ Prérequis
- **Java 17 ou supérieur** (JDK installé sur votre machine)
- **JavaFX SDK 17** (non inclus dans le dépôt, à télécharger séparément)

---

## 📥 Installation de JavaFX
### 1. Télécharger le SDK
👉 [Télécharger JavaFX SDK](https://gluonhq.com/products/javafx/)

### 2. Placer le SDK
Décompressez l’archive dans le dossier suivant de votre projet : JavaGame_YuGiOh/javafx/windows/javafx-sdk-17.0.17/

Le chemin attendu par `game.bat` est : JavaGame_YuGiOh/javafx/windows/javafx-sdk-17.0.17/lib


⚠️ Si le SDK n’est pas présent à cet endroit, le script affichera une erreur.

---

## ▶️ Lancer le projet
Sous Windows, il suffit de **double-cliquer sur `game.bat`**.  
Le script va :  
1. Nettoyer le dossier `out/`  
2. Compiler toutes les sources Java  
3. Copier les ressources  
4. Lancer le jeu via JavaFX  

---

## 📌 Notes
- Les binaires JavaFX (`.dll`, `.so`, `.dylib`) ne sont **pas inclus** dans ce dépôt pour éviter les problèmes de taille.  
- Assurez-vous de télécharger et placer JavaFX dans le bon dossier avant de lancer le jeu.  
- Le projet est conçu pour être léger et facilement reproductible.

---

## 👨‍💻 Auteur
Projet développé par **Nicolas LAW-SHUN** dans le cadre de la formation à Epitech La Réunion.