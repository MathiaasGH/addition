# Guide d'utilisation du modèle cognitif computationnel
Pour utiliser le modèle, deux solutions sont disponibles : 
- Cloner le projet Git,
- Exporter le dossier .zip depuis Git

## Cloner le projet
Pour cloner le projet, veuillez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis copier l'URL (ou la clé) présentée. 

Sur votre environnement de travail Java, il suffit maintenant de créer un nouveau projet Git en collant cet URL (ou clé). Sous Eclipse : File > Import... > Git > Project From Git > Clone URI et coller l'URL dans le champ "URI" > Finish.

Le projet s'ajoutera dans la fenêtre "Git Repositories" (pour afficher cette fenêtre, Window > Show View > Other > Git > Git Repositories

## Exporter le dossier
Pour exporter le dossier et ainsi utiliser le modèle développé en Java, vous pouvez cliquer sur le bouton "Code" en vert dans le menu principal du git, puis cliquer sur "Exporter le zip".

Ainsi, vous aurez accès au dossier du [modèle](./Modele_Addition_Arithmetico-Alphabetique). Grâce à ce dernier, vous pouvez l'importer dans votre environnement de travail. Sur Eclipse : File > Import... > General > Existing Project into Workspace puis importez le dossier.

## L'utilisation
Un participant est simulé en instanciant un objet de la classe "Model". 

Deux profils de participants existent :
- Les breakers
- Les non-breakers
  
Par défaut, un participant est breaker. Vous pouvez spécifier son profil explicitement dans les paramètres d'instancation du participant (ex : new Model("nonbreakers"))
