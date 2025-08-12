# 🇫🇷 Guide d'utilisation de l'application

## Table des matières
- [Cloner le projet](#cloner-le-projet)
- [Exporter le dossier](#exporter-le-dossier)
- [Utiliser l'application](#utiliser-lapplication)
    - [Passer par un IDE](#passer-par-un-ide-r)
    - [Passer par un terminal](#passer-par-un-terminal)
- [Contenu du dossier](#contenu-du-dossier)
    - [Onglet "Jeux de données"](#onglet-jeux-de-données)
    - [Onglet "Graphiques"](#onglet-graphiques)
        - [Volet de gauche](#volet-de-gauche)
            - [Sélection des fichiers à visualiser et/ou simuler](#sélection-des-fichiers-à-visualiser-etou-simuler)  


Pour utiliser l'application, deux solutions sont disponibles : 
- Cloner le projet Git,
- Exporter le dossier .zip depuis Git

## Cloner le projet
Pour cloner le projet, veuillez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis copier l'URL (ou la clé) présentée. 

Sur votre environnement de travail R, il suffit maintenant de créer un nouveau projet Git en collant cet URL (ou clé). Sous RStudio : File > New Project > Version Control > Clone URI et coller l'URL dans le champ "URI" > Create Project.

Le projet s'ajoutera dans la fenêtre "Files" en bas à droite de l'interface.

## Exporter le dossier
Pour exporter le dossier et ainsi utiliser l'application, vous pouvez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis cliquer sur "Exporter le zip".

Ainsi, vous aurez accès au dossier de l'[application](./Application_Addition_Arithmetico-Alphabetique). Grâce à ce dernier, vous pouvez importer le fichier [app](./Application_Addition_Arithmetico-Alphabetique/app.R) dans votre IDE R (comme RStudio). Sur RStudio : File > Open File... > puis sélectionnez le fichier `app.R`
 depuis le dossier.

## Utiliser l'application
Pour utiliser l'application, vous pouvez : 
- Passer par un IDE R,
- L'ouvrir depuis le terminal

### Passer par un IDE R
  Une fois le fichier `app.R` importé dans votre IDE, vous pouvez lancer le code en appuyant sur le bouton run spécifique à votre environnement de travail.
  Sur RStudio, le bouton se présente comme suit (Run App). 
<img width="2183" height="87" alt="image" src="https://github.com/user-attachments/assets/2d870f28-b2bb-4810-b0db-d6d9012b7865" />

### Passer par un terminal 
 
 ***Windows***
 
Si R n'est pas dans votre variable `PATH` (généralement le cas), précisez le chemin vers l'éxécutable R que vous avez installé pour pouvoir utiliser le langage, puis le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

`& "C:\Program Files\R\R-4.4.2\bin\Rscript.exe" -e "shiny::runApp('C:/Users/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`

 Si R est dans votre variable `PATH`, précisez simplement le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

 `& Rscript -e "shiny::runApp('C:/Users/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`
 
 ***Linux/Mac*** :

Si R n'est pas dans votre variable `PATH`, précisez le chemin vers l'éxécutable R que vous avez installé pour pouvoir utiliser le langage, puis le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

`/usr/bin/Rscript -e "shiny::runApp('/home/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`

 Si R est dans votre variable `PATH`, précisez simplement le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 
 
 `Rscript -e "shiny::runApp('/home/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`

 ## Contenu du dossier

 Le dossier contient quatre sous-dossiers (sans compter `.Rproj.user`) :
 - [Graphes](./Application_Addition_Arithmetico-Alphabetique/Graphes)
   Ce dossier contient des codes R qui permettent la présentation de graphiques dans l'interface.
 - [configSave](./Application_Addition_Arithmetico-Alphabetique/configSave)
   Ce dossier contient les configurations de création de tâches expérimentales sauvegardées pour les charger ultérieurement.
 - [csv](./Application_Addition_Arithmetico-Alphabetique/csv)
   Ce dossier n'est pas nécessaire. Il sert de dépôt pour vos données expérimentales.
 - [simulationFiles](./Application_Addition_Arithmetico-Alphabetique/simulationFiles)
   Ce dossier contient les simulations créées par le modèle cognitif computationnel. **Attention :** il est vidé à chaque fois que l'application est relancée.

 ## Contenu de l'application

 ### Onglet "Jeux de données"

Dans l'onglet "Jeux de données", vous trouverez une section permettant à l'utilisateur d'importer des fichiers au format .csv. Ces fichiers correspondent à vos données empiriques que vous pourrez visualiser et simuler si elles contiennent les colonnes nécessaires (voir dans l'application).

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/932b6add-d045-4646-bd2e-4793a47610d8" title="Section permettant d'importer des fichiers de données empiriques ou simulées grâce à un autre modèle." />

  ### Onglet "Graphiques"

 Dans l'onglet "Graphiques", vous trouverez deux menus : un volet à gauche, et le menu principal au centre. 

 #### Le volet de gauche

##### Sélection des fichiers à visualiser et/ou simuler
 Ce volet permet à l'utilisateur de renseigner quel fichier de données empiriques (précédemment importé dans l'onglet "Jeux de données") doit être visualisé et simulé. 
 
 <img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/8f7f34ab-cb46-42d1-850b-39654e50571d" title="Section permettant de sélectionner le fichier de données empiriques à regarder." />

Si l'utilisateur possède également un fichier de données simulées grâce à un autre modèle, il est possible de l'ajouter pour visualiser ces données. Pour ce faire, un texte cliquable et présent et offre cette possibilité à l'utilisateur. 

<img width="402" height="95" alt="image" src="https://github.com/user-attachments/assets/7db7099e-18d8-45a4-a070-0d483aa76895" title="Texte permettant à l'utilisateur de lire un fichier de données simulées avant clic." />

puis,

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/94462019-879a-4246-afcd-a10ba6a32345" title="Choix des données simulées." />

##### Ordre de passation des simulations

L'application simule des participants à une tâche selon les données du fichier de données empiriques. Il est possible de : 
- Suivre l'ordre de passation du fichier (le faux-participant 1 suivra les mêmes tâches que le vrai-participant 1 du fichier et dans le même ordre)
- Ne pas suivre l'ordre de passation du fichier (le faux-participant 1 suivra les mêmes tâches que le vrai-participant 1 mais pas dans le même ordre)

<img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/e3692179-b9e4-4a1f-9956-2dbe264fa118" title="Choix de l'ordre de passation des simulations." />


##### Gérer le nombre de participants et leur profil

L'application peut - grâce au modèle Java présent dans le git - simuler des participants à une tâche d'additions arithémtico-alphabétiques. Il peut donc être intéressant de pouvoir choisir le nombre de participants souhaité et le nombre de runs par participant. Pour ce faire, il suffit de spécifier cela dans le champ associé. 

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/b82f694f-249c-4c86-ae9f-e36fcecd4a76" title="Section permettant de spécifier le nombre de participants simulés et combien de fois ils vont être simulés." />

De plus, il est possible de préciser la proportion de participants de profils "breakers" et la proportion de participants de profil "non-breaker" parmi les participants simulés. 
Pour faire cela, il suffit de cliquer sur la bulle d'information associée à la section permettant de spécifier le nombre de participants voulu et de préciser les proportions.

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/e22d521e-51e6-44e9-9db3-68944aa8fbbe" title="Bulle d'information pour préciser les profils des participants." />

puis,

<img width="400" height="250" alt="image" src="https://github.com/user-attachments/assets/30c8ee1f-0c32-471c-9cd3-723ca3482b5f" title="Section pour préciser les profils des participants." />

**⚠️ Remarque : Si le nombre de participant simulé est plus grand que le nombre de participants contenus dans le fichier des données empiriques, toutes les passations simulées seront "non respectées" (=les faux-participants suivront des tâches créés de toutes pièces à partir des tâches contenus dans le fichier)**

##### Simuler sans données empiriques

Si l'utilisateur ne possède pas de données empiriques, l'application permet tout de même de simuler des participants et de visualiser les données. 

<img width="300" height="50" alt="image" src="https://github.com/user-attachments/assets/a0873ad5-ae80-4c25-a6e8-cded0e234917" title="Boîte à séléctionner pour imaginer une tâche sans fichier de données empiriques." />

Suite à cela, il est possible de spécifier le nombre de sessions voulues pour la tâche et les problèmes associées.

<img width="300" height="50" alt="image" src="https://github.com/user-attachments/assets/25945a41-e69d-4f55-8c86-a8cb1407ff3e" title="Section pour spécifier le nombre de sessions." />

puis,

<img width="300" height="200" alt="image" src="https://github.com/user-attachments/assets/ead3fdc7-599e-49d6-a446-4e3113a5ae73" title="Section pour créer des problèmes pour chaque session voulue." />

L'application permet de personnaliser les problèmes souhaités. Vous pouvez découvrir cette fonctionnalité grâce au bouton "Combinaisons personnalisées".

<img width="300" height="50" alt="image" src="https://github.com/user-attachments/assets/47176a1d-46f1-4649-92df-1d506b1b40b9" title="Bouton pour personnaliser les combinaisons de problèmes possibles." />

##### Personnaliser les blocs de présentation de problèmes par sessions

Chaque session présent des problèmes à résoudre. Il est possible de présenter ces problèmes plusieurs fois par sessions. Cela est possible en créant des blocs de problèmes et en présentant X fois ces blocs. Cela permet aussi de préciser la nature des problèmes (production (ex. "A+3=?" vs jugement "A+3=D?")). 
Pour préciser tout cela, il suffit de cliquer sur le bouton "Blocs" puis de remplir les champs associés.

<img width="80" height="80" alt="image" src="https://github.com/user-attachments/assets/4a886570-8d5e-4ebc-84de-83317da99bcf" title="Bouton pour accéder à la personnalisation des blocs de problèmes." />

 puis,

 <img width="400" height="300" alt="image" src="https://github.com/user-attachments/assets/e0bf55c7-81e6-40b7-b289-87a26ddcd05b" title="Section pour personnaliser les blocs de problèmes par sessions." />

##### Sauvegarder et charger des simulations sans données empiriques

Comme vu précédemment, vous pouvez créer des tâches expérimentales et y simuler des participants sans données empiriques. Cependant, cela s'avère long et fastidieux pour des tâches avec beaucoup de sessions. Vous avez donc la possibilité de sauvegarder des configurations puis de les charger plus tard pour éviter de les re-créer. 

<img width="200" height="100" alt="image" src="https://github.com/user-attachments/assets/60885d4a-a062-4def-a171-c11ec38c5c08" title = "Boutons pour charger et sauvegarder des configurations de tâches expérimentales." />

L'application est fournie avec une configuration déjà créee. Il s'agit de la configuration de la tâche exprimentale d'additions sur séquence contigue (CSC) sur problèmes de jugement de Stéphanie Chouteau (Chouteau, 2024).

<img width="400" height="300" alt="image" src="https://github.com/user-attachments/assets/806bd5a1-7ba5-403f-a4dd-239667ccee2d" title = "Chargement de la configuration de Stéphanie Chouteau (Chouteau, 2024)." />




 # 🌎 Application Usage Guide
To use the application, two options are available:  
- Clone the Git project,  
- Export the .zip folder from Git  

## Cloning the Project
To clone the project, please click on the green "Code" button in the main menu of the Git repository, then copy the presented URL (or key).

In your R development environment, simply create a new Git project by pasting this URL (or key). In RStudio: File > New Project > Version Control > Clone URI and paste the URL in the "URI" field > Create Project.

The project will appear in the "Files" pane at the bottom right of the interface.

## Exporting the Folder
To export the folder and use the application, you can click on the green "Code" button in the main Git menu, then click on "Download ZIP".

This will give you access to the [application folder](./Application_Addition_Arithmetico-Alphabetique). Using this folder, you can import the [app file](./Application_Addition_Arithmetico-Alphabetique/app.R) into your R IDE (such as RStudio). In RStudio: File > Open File... > then select the `app.R` file from the folder.

## Using the Application
To use the application, you can:  
- Use an R IDE,  
- Run it from the terminal
  
### Using an R IDE
  Once the `app.R` file is imported into your IDE, you can run the code by pressing the run button specific to your development environment.  
  In RStudio, the button looks like this (Run App).  
<img width="2183" height="87" alt="image" src="https://github.com/user-attachments/assets/2d870f28-b2bb-4810-b0db-d6d9012b7865" />

### Running from a Terminal

 ***Windows***
 
If R is not in your `PATH` variable (usually the case), specify the path to the R executable you installed to use the language, then the path to the application you downloaded from this Git repository. Example execution:  

`& "C:\Program Files\R\R-4.4.2\bin\Rscript.exe" -e "shiny::runApp('C:/Users/username/git/project_addition/Application_Addition_Arithmetico-Alphabetique')"`

If R is in your `PATH` variable, simply specify the path to the application you downloaded from this Git repository. Example execution:

`& Rscript -e "shiny::runApp('C:/Users/username/git/project_addition/Application_Addition_Arithmetico-Alphabetique')"`

 ***Linux/Mac***:

If R is not in your `PATH` variable, specify the path to the R executable you installed to use the language, then the path to the application you downloaded from this Git repository. Example execution:

`/usr/bin/Rscript -e "shiny::runApp('/home/username/git/project_addition/Application_Addition_Arithmetico-Alphabetique')"`

If R is in your `PATH` variable, simply specify the path to the application you downloaded from this Git repository. Example execution:

`Rscript -e "shiny::runApp('/home/username/git/project_addition/Application_Addition_Arithmetico-Alphabetique')"`

## Folder Contents

The folder contains four subfolders (excluding `.Rproj.user`):  
- [Graphes](./Application_Addition_Arithmetico-Alphabetique/Graphes)  
  This folder contains R scripts used to display graphs in the interface.  
- [configSave](./Application_Addition_Arithmetico-Alphabetique/configSave)  
  This folder contains saved experimental task configurations to be loaded later.  
- [csv](./Application_Addition_Arithmetico-Alphabetique/csv)  
  This folder is not necessary. It serves as a repository for your experimental data.  
- [simulationFiles](./Application_Addition_Arithmetico-Alphabetique/simulationFiles)  
  This folder contains simulations created by the computational cognitive model. **Warning:** it is emptied every time the application is restarted.

## Application Contents

Explain how to use it (❌ **to be done**)


 ## References
 -  Download RStudio - Posit. (2024, 12 novembre). Posit. https://posit.co/downloads/
