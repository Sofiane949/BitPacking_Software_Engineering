# Projet SE 2025: Compression d'Entiers (BitPacking)

Sofiane Khourta

Ce projet implémente différentes méthodes de compression d'tableaux d'entiers (BitPacking) en Java, visant à étudier la réduction de la taille des données pour en accélérer la transmission.

Il inclut des modes avec et sans chevauchement de bits (`Overlap` / `NoOverlap`), ainsi qu'une gestion des zones de débordement (`Overflow`) pour un stockage efficace des valeurs exceptionnelles.
## Prérequis

Pour compiler et exécuter ce projet, vous avez besoin de :
* Java (JDK 11 ou plus récent)
* La commande `make` (disponible sur Linux, macOS et Windows via WSL)

## Configuration JUnit 5

Les tests unitaires (`make test`) utilisent JUnit 5. Le `Makefile` est configuré pour utiliser le fichier `.jar` fourni dans le dossier `lib/`.

**Vous n'avez aucune étape d'installation à faire**, le `.jar` est déjà inclus dans ce dépôt.

## Compilation (Build)

Un `Makefile` est fourni pour automatiser toutes les tâches.

### Compiler le projet et créer le .jar

Pour compiler tous les fichiers `.java` (du code source et des tests) et créer un fichier `.jar` exécutable :

```bash
make
```

### Nettoyer le projet

Pour supprimer le dossier build/ et le fichier Compression.jar :
```
Bash
make clean
```

Utilisation du Programme
1. Exécuter le programme principal (Démo)
Le programme principal (Main.java) est conçu pour compresser un fichier de données (un entier par ligne) et vérifier l'intégrité de la décompression.

Le .jar exécutable prend deux arguments :

- Le type de compression.
- Le chemin vers le fichier d'entrée.

Syntaxe :

```
Bash
java -jar Compression.jar <TYPE_COMPRESSION> <FICHIER_ENTREE>
```

Exemple :

Bash

java -jar Compression.jar WITH_OVERLAP inputs/input_overflow.txt

Types de compression valides :

- NO_OVERLAP
- WITH_OVERLAP
- OVERFLOW_NO_OVERLAP
- OVERFLOW_WITH_OVERLAP

Des fichiers inputs sont déjà présents dans inputs/.

### Exécuter les Benchmarks

Pour lancer les benchmarks de performance (BenchmarkRunner.java), utilisez la cible make dédiée :

```
Bash
make benchmark
```

Ceci exécutera une série de tests de performance sur de grands ensembles de données générées aléatoirement. Les temps moyens pour les fonctions compress, decompress et get seront affichés dans la console.

### Exécuter les Tests Unitaires
Pour exécuter la suite de tests unitaires (JUnit 5) afin de vérifier que toutes les classes fonctionnent comme prévu :

```
Bash
make test
```
