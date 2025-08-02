# Guide d'utilisation de l'application
Pour utiliser l'application, deux solutions sont disponibles : 
- Cloner le projet Git,
- Exporter le dossier .zip depuis Git

## Cloner le projet
Pour cloner le projet, veuillez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis copier l'URL (ou la clé) présentée. 

Sur votre environnement de travail R, il suffit maintenant de créer un nouveau projet Git en collant cet URL (ou clé). Sous RStudio : File > New Project > Version Control > Clone URI et coller l'URL dans le champ "URI" > Create Project.

Le projet s'ajoutera dans la fenêtre "Files" en bas à droite de l'interface.

## Exporter le dossier
Pour exporter le dossier et ainsi utiliser l'application, vous pouvez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis cliquer sur "Exporter le zip".

Ainsi, vous aurez accès au dossier de l'[application](./Application_Addition_Arithmetico-Alphabetique). Grâce à ce dernier, vous pouvez importer le fichier [app](./Application_Addition_Arithmetico/app.R) dans votre IDE R (comme RStudio). Sur RStudio : File > Open File... > puis sélectionnez le fichier `app.R`
 depuis le dossier.

## Utiliser l'application
Pour utiliser l'application, vous pouvez : 
- Passer par un IDE R,
- L'ouvrir depuis le terminal, à condition que R soit installé dans le dossier contenant l'application (par défaut non installé)

  ### Passer par un IDE R
  Une fois le fichier `app.R` importé dans votre IDE, vous pouvez lancer le code en appuyant sur le bouton run spécifique à votre environnement de travail.
  Sur RStudio, le bouton se présente comme suit (Run App). 
<img width="2183" height="87" alt="image" src="https://github.com/user-attachments/assets/2d870f28-b2bb-4810-b0db-d6d9012b7865" />

 ### Passer par un terminal
 Si vous souhaitez lancer l'application depuis le terminal, assurez-vous d'avoir R installé dans votre dossier. Une fois chose faite, il vous suffit d'ouvrir un terminal de commande et de taper : 
 
 ***Windows***
 
 `shiny::runApp("chemin/vers/ton/app")`
 
 ***Linux/Mac*** :
 
 `R -e "shiny::runApp('chemin/vers/le/dossier/app')"`
