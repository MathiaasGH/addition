# 🇫🇷 Guide d'utilisation de l'application
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

 #### Onglet "Jeux de données"

Dans l'onglet "Jeux de données", vous trouverez une section permettant à l'utilisateur d'importer des fichiers au format .csv. Ces fichiers correspondent à vos données empiriques que vous pourrez visualiser et simuler si elles contiennent les colonnes nécessaires (voir dans l'application).

<img width="400" height="200" alt="image" src="https://github.com/user-attachments/assets/932b6add-d045-4646-bd2e-4793a47610d8" />

  #### Onglet "Graphiques"

 Dans l'onglet "Graphiques", 


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
