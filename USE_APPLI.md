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
 
 ***Windows***
Si R n'est pas dans votre variable `PATH` (généralement le cas), précisez le chemin vers l'éxécutable R que vous avez installé pour pouvoir utiliser le langage, puis le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

`& "C:\Program Files\R\R-4.4.2\bin\Rscript.exe" -e "shiny::runApp('C:/Users/Propriétaire/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`

 Si R est dans votre variblae `PATH`, précisez simplement le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

 `& Rscript -e "shiny::runApp('C:/Users/Propriétaire/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`
 
 ***Linux/Mac*** :

Si R n'est pas dans votre variable `PATH`, précisez le chemin vers l'éxécutable R que vous avez installé pour pouvoir utiliser le langage, puis le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 

`/usr/bin/Rscript -e "shiny::runApp('/home/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`

 Si R est dans votre variblae `PATH`, précisez simplement le chemin d'accès vers l'application récupérée depuis ce dépôt Git. Exemple d'éxecution : 
 
 `Rscript -e "shiny::runApp('/home/nom_utilisateur/git/projet_addition/Application_Addition_Arithmetico-Alphabetique')"`
