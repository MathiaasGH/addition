library(shiny)
library(shinycssloaders)
library(dplyr)
library(bslib)
library(shinyWidgets)
library(shinyBS)


#J'initialise quelques variables et fonction utilises
dossier <- "./simulationFiles"

fichiers <- list.files(dossier, full.names = TRUE)

file.remove(fichiers)

is_valid_uppercase_string <- function(s) {
  grepl("^\\s*[A-Z](\\s*,\\s*[A-Z])*\\s*$", s)
}

is_valid_number_list <- function(s) {
  grepl("^\\s*\\d+(\\s*,\\s*\\d+)*\\s*$", s)
}


#Partie UI
ui <- fluidPage(
  theme = bs_theme(bootswatch = "zephyr"),
  
  #Barre de progression pour les simulations et l'affichage des graphiques
  progressBar(
    id = "progress_bar",
    value = 0,
    total = 100,
    display_pct = FALSE,
    status = "success",
    unit_mark = "%"
  ),
  
  titlePanel("Visualisation de données relatives à des tâches d'addition arithmético-alphabétiques"),
  
  tabsetPanel(
    #La page jeux de donnés
    tabPanel(title="Jeux de données", 
             icon=icon("table"),
             h3("Sélectionnez le jeu de données à visualiser"),
             sidebarLayout(
               sidebarPanel(
                 # Upload de fichier CSV
                 fileInput("csv_file", "Ajouter un fichier CSV :",
                           accept = c(".csv", ".CSV")),
                 
                 # Liste des fichiers disponibles (vide au départ)
                 selectInput("dataset", "Fichiers disponibles :",
                             choices = c("Aucun fichier disponible"=""),
                             selected = ""),
                 # Bouton pour supprimer un fichier
                 conditionalPanel(
                   condition = "input.dataset != ''",
                   actionButton("remove_file", "Supprimer le fichier sélectionné", 
                                class = "btn-danger btn-sm")
                 )
               ),
               
               #La panneau principal de la page d'initialisation des données
               mainPanel(
                 # Statut
                 h4("Statut :"),
                 verbatimTextOutput("status_info") %>% withSpinner(color="#0dc5c1"),
                 
                 # Aperçu des données
                 conditionalPanel(
                   condition = "input.dataset != ''",
                   h4("Aperçu des données :"),
                   tableOutput("data_preview") %>% withSpinner(color="#0dc5c1")
                 )
               )
             )
    ),
    
    #La page des graphiques
    tabPanel(title="Graphiques",
             icon=icon("chart-line"),
             h3("Onglet de visualisation des données"),
             sidebarLayout(
               #Le side-panel contenant la configuration des simulations ou la configuration des graphes
               sidebarPanel(
                 #Si on est sur le menu de configuration des simulations (par défaut oui)
                 conditionalPanel(
                   condition = "output.menuState == 1",
                   h4("Sélectionnez votre fichier de données empiriques, et le modèle simulera un apprentissage en fonction de vos paramètres expérimentaux."),
                   
                   # JavaScript pour gérer le clic sur du texte
                   tags$script(HTML("
                    var clickCount = 0;
                    $(document).on('click', '#clickable_text', function() {
                       clickCount++;
                       Shiny.setInputValue('text_clicked', clickCount % 2 === 1);
                    });
                 ")),
                   
                   conditionalPanel(
                     condition = "!input.text_clicked",
                     div(
                       span("Je refuse : je possède déjà un fichier contenant les simulations"),
                       span("et je veux le tester.", 
                            id = "clickable_text",
                            style = "color: blue; cursor: pointer; text-decoration: underline;")
                     ),
                   ),
                   
                   conditionalPanel(
                     condition = "input.text_clicked",
                     div(
                       span("Je change d'avis :", style = "font-size: 10px;"),
                       span("je veux que le modèle simule l'apprentissage.", 
                            id = "clickable_text",
                            style = "color: blue; cursor: pointer; text-decoration: underline; font-size: 10px;")
                     ),
                   ),
                   
                   verbatimTextOutput("click_result"),
                   
                   conditionalPanel(
                     condition = "input.text_clicked & !input.no_file",
                     selectInput("sim_choice", "Sélectionnez le fichier des SIMULATIONS :",
                                 choices = c("Aucun fichier disponible"=""),
                                 selected = ""),
                   ),
                   
                   conditionalPanel(
                     condition = "!input.no_file",
                     selectInput("expe_choice", "Sélectionnez le fichier des données EXPERIMENTALES :",
                                 choices = c("Aucun fichier disponible"=""),
                                 selected = ""),
                     radioButtons(
                       inputId="problem_order", 
                       label="Ordre de passation",
                       choices = c("Respecté", "Non respecté"),
                       selected = "Respecté"
                     )
                   ),
                   
                   checkboxInput("no_file", "Je n’ai pas de fichiers de données", value = FALSE),
                   
                   
                   
                   tags$div(
                     tags$div(
                       tags$label("Nombre de participants",
                                  `for` = "nb_participant",
                                  style = "font-weight: 500; margin-right: 6px;"),
                       
                       tooltip(
                         actionBttn(inputId = "profils",
                                    icon = icon("info"),
                                    size = "xs",
                                    style = "material-circle",
                                    color = "default",
                                    action = "button"),
                         "Gérer les profils des participants", 
                         id = "profil_tooltip", 
                         placement = "right"
                       )
                     ),
                     
                     h3(""),
                     
                     numericInput(
                       inputId = "nb_participant",
                       label = NULL,
                       value = 1
                     )
                   ),
                   
                   
                   numericInput(
                     inputId = "nb_run",
                     label = "Nombre de run par participant",
                     value = 1
                   ),
                   
                   #Si on a pas de fichier, on peut créer la tâche expérimentale que l'on veut
                   conditionalPanel(
                     condition = "input.no_file",
                     splitLayout(cellWidths = c("auto", "100px"),
                                 "Nombre de sessions :",
                                 numericInput("no_file_nb_sessions",
                                              label=NULL,
                                              value = 0)),
                     uiOutput("sessions_panels"),
                     actionButton("load_no_file", "Charger une configuration", style = "color: white; background-color: grey;"),
                     actionButton("save_no_file", "Sauvegarder une configuration", icon = icon("save"), style = "color: white; background-color: blue;"),
                     actionButton("blocs", "Blocs"),
                     actionButton("generate", "Générer"),
                     
                     
                   ),
                   
                   conditionalPanel(
                     condition = "!input.text_clicked & !input.no_file",
                     selectInput("addends", "Sélectionnez les addends :",
                                 choices = c("Aucun addend disponible"=""),
                                 selected = ""),
                     selectInput("augends", "Sélectionnez les augends :",
                                 choices = c("Aucun augend disponible"=""),
                                 selected = ""),
                     actionButton(
                       inputId = "generate_plot",
                       label="Générer"
                     ),
                   ),
                   actionButton(
                     inputId = "go_to_graph_perso",
                     label="Personnaliser les graphes"
                   )
                 ),
                 
                 #Si on est sur le menu de personnalisation des graphiques on affiche le contenu correspondant
                 conditionalPanel(
                   condition = "output.menuState == 2",                   
                   sliderInput("hauteur", "Hauteur des graphes",
                               min = 400, max = 1200,
                               value = 600, step = 10, ticks = TRUE),
                   
                   sliderInput("largeur", "Largeur des graphes",
                               min = 600, max = 1200,
                               value = 900, step = 10, ticks = TRUE),
                   
                   conditionalPanel(
                     condition = "input.graph_tabs == 'strategies' || input.graph_tabs == 'overlap'",
                     sliderInput("session_strategy_graph", "Affichage de la session",
                                 min = 1, max = 1, value = 1, step = 1, ticks = TRUE)
                   ),
                   
                   conditionalPanel(
                     condition = "input.graph_tabs == 'time'",
                     sliderInput("session_time_slider", "Affichage des sessions",
                                 min = 1, max = 1, value = c(1, 1), step = 1, ticks = TRUE)
                   ),
                   actionButton("reset_menu", "Retour à la configuration des simulations"),
                   
                   
                 )
               ),
               
               # Panel principal : les graphes ou les messages d'erreur
               mainPanel(
                 conditionalPanel(
                   condition = "!input.text_clicked && output.menuState==1",
                   actionButton("open_settings", "Modifier les paramètres du modèle", icon = icon("gear"))
                 ),
                 
                 uiOutput("card_title_display"),
                 
                 tabsetPanel(
                   id = "graph_tabs",
                   tabPanel("Graphe des temps", value = "time", icon = icon("line-chart"),
                            uiOutput("error_message_time"),
                            plotOutput("time_graph") %>% withSpinner(color = "#0dc5c1")),
                   
                   tabPanel("Heatmap", icon = icon("thermometer-1"),
                            uiOutput("error_message_heatmap"),
                            plotOutput("heatmap_graph") %>% withSpinner(color = "#0dc5c1")),
                   
                   tabPanel("Strategies", value = "strategies", icon = icon("cogs"),
                            uiOutput("error_message_strategies"),
                            plotOutput("strategies_graph") %>% withSpinner(color = "#0dc5c1")),
                   
                   tabPanel("Overlap", value = "overlap", icon = icon("recycle"),
                            uiOutput("error_message_overlap"),
                            plotOutput("overlap_graph") %>% withSpinner(color = "#0dc5c1"))
                 )
               )
             )
    )
  )
)

#Le serveur pour gérer les interactions
server <- function(input, output, session) {
  options(shiny.maxRequestSize=30*1024^2)
  
  #Quelques variables utiles
  menu_state <- reactiveVal(1)
  
  
  values <- reactiveValues(
    uploaded_files = list(),
    file_names = NULL,
    current_data = NULL,
    simulation_files = list(),
    experimental_files = list(),
    sim_data = NULL,
    expe_data = NULL,
    sim_file_name = NULL,
    expe_file_name = NULL,
    hauteur = 600,
    largeur = 900,
    no_file = TRUE,
    plot_flag=TRUE,
    order="Respecté",
    data_path=NULL,
    nb_participant=1,
    nb_run=1,
    prop_breakers = 50,
    prop_nonbreakers = 50,
    temp_prop_breakers = 50,
    temp_prop_nonbreakers = 50,
    affiche=FALSE
  )
  
  params <- reactiveValues(
    increasePractice = 0.55,
    t = 3000,
    p=30,
    b=700,
    d=325,
    rationality=0.005,
    counting_reinforcement=0.37,
    retrieving_reinforcement=0.5
  )
  
  
  default_params <- list(
    iincreasePractice = 0.55,
    t = 3000,
    p=30,
    b=700,
    d=325,
    rationality=0.005,
    counting_reinforcement=0.37,
    retrieving_reinforcement=0.5
  )
  
  
  simulations <- reactiveValues(
    simulation = TRUE,
    addends = NULL,
    augends = NULL,
    Ensemble = NULL,
    sessions = 1,
    selected_addend = NULL,
    selected_augend = NULL,
    simulated_data = NULL,
    file_name = NULL,
    sim_message=FALSE
  )
  
  no_file <- reactiveValues(
    print_plot=FALSE,
    
    nb_sess = NULL,
    augendList = NULL,
    addendList = NULL,
    
    problem=NULL,
    problem_type=NULL,
    
    temp_save_incorrect_bloc=NULL,
    incorrect_bloc=NULL,
    type_session=NULL,
    correct_bloc=NULL,
    temp_correct_bloc=NULL,
    temp_type_session=NULL,
    nb_blocs_session=NULL,
    temp_nb_blocs_session=NULL,
    observerIncorrectBloc=NULL
    
  )
  
  
  sessions_observed <- reactiveVal(integer(0))
  
  session_changed <- reactiveVal(0)
  
  already_built <- reactiveVal(list())
  
  #Pour aller à la partie personnalisation des graphes
  observeEvent(input$go_to_graph_perso, {
    req(input$go_to_graph_perso>0)
    menu_state(2)
  })
  
  #Pour revenir à la configuration des simulationsnu
  observeEvent(input$reset_menu, {
    menu_state(1)
  })
  
  output$menuState <- reactive({
    menu_state()
  })
  
  outputOptions(output, "menuState", suspendWhenHidden = FALSE)
  
  observeEvent(input$problem_order, {
    values$order = input$problem_order
    
  })
  
  #Pour générer les simulations à partir d'un fichier de données expérimentales
  observeEvent(input$generate_plot, {
    updatePlot()
    menu_state(2)
  })
  
  #Pour gérer les profils des participants
  observeEvent(input$profils, {  
    showModal(modalDialog(
      h3("Personnalisez les profils"),
      sliderInput(
        inputId = "prop_breakers",
        label = "Proportion de breakers",
        value=values$prop_breakers,
        step=1,
        min=0,
        max=100
      ),
      sliderInput(
        inputId = "prop_nonbreakers",
        label = "Proportion de non-breakers",
        value=values$prop_nonbreakers,
        step=1,
        min=0,
        max=100
      ),
      footer = tagList(
        actionButton(
          inputId = "cancel_profils",
          label="Annuler",
          style = "color: white; background-color: red;"),
        
        actionButton(
          inputId = "save_profils", 
          label = "Sauvegarder",
          icon=icon("save"),
          style = "color: white; background-color: blue;")
      )
    ))
  })
  
  #Pour sauvegarder les profils des participants
  observeEvent(input$save_profils, {
    values$prop_breakers = values$temp_prop_breakers
    values$prop_nonbreakers = values$temp_prop_nonbreakers
    removeModal()
  })
  
  #Pour annuler la modification des profils des participants
  observeEvent(input$cancel_profils, {
    removeModal()
  })
  
  #Pour garantir un pourcentage complet de 100% entre breakers et nonbreakers
  observeEvent(input$prop_breakers, {
    values$temp_prop_breakers = input$prop_breakers
    values$temp_prop_nonbreakers = 100-input$prop_breakers
    updateSliderInput(
      inputId = "prop_nonbreakers",
      label = "Proportion de non-breakers",
      value=values$temp_prop_nonbreakers,
      step=1,
      min=0,
      max=100
    )
  })

  #Pour garantir un pourcentage complet de 100% entre breakers et nonbreakers
  observeEvent(input$prop_nonbreakers, {
    values$temp_prop_nonbreakers = input$prop_nonbreakers
    values$temp_prop_breakers = 100-input$prop_nonbreakers
    updateSliderInput(
      inputId = "prop_breakers",
      label = "Proportion de breakers",
      value=values$temp_prop_breakers,
      step=1,
      min=0,
      max=100
    )
  })
  
  #Pour créer des validateurs pour les paramètres du modèle (limites à certaines valeurs)
  create_validator <- function(input_id, min_val, max_val, param_name) {
    observeEvent(input[[input_id]], {
      if (!is.null(input[[input_id]]) && !is.na(input[[input_id]]) && input[[input_id]] != "") {
        # Convertir en numérique si c'est une chaîne
        val <- as.numeric(input[[input_id]])
        
        # Vérifier si la conversion a réussi
        if (!is.na(val)) {
          if (val > max_val) {
            updateNumericInput(session, input_id, value = max_val)
            showNotification(paste("Valeur de", param_name, "limitée à", max_val), 
                             type = "warning", duration = 2)
          } else if (val < min_val) {
            updateNumericInput(session, input_id, value = min_val)
            showNotification(paste("Valeur de", param_name, "limitée à", min_val), 
                             type = "warning", duration = 2)
          }
        }
      }
    })
  }
  
  #Je crée les validateurs pour tous les paramètres relatifs au modèle
  create_validator("modal_ic", 0, 2, "increase_practice")
  create_validator("modal_rationality", 0, 1, "rationality")
  create_validator("modal_counting_reinforcement", 0, 2, "counting_reinforcement")
  create_validator("modal_retrieving_reinforcement", 0, 2, "retrieving_reinforcement")
  create_validator("modal_t", 0, 5000, "t")
  create_validator("modal_p", 0, 150, "p")
  create_validator("modal_b", 0, 1500, "b")
  create_validator("modal_d", 0, 700, "d")
  
  #J'affiche les paramètres actuels (non utilisé)
  output$current_params_display <- renderUI({
    if(simulations$simulation==TRUE)
      h5(paste("Paramètres actuels: increase_practice =", params$increasePractice, ", rationality =", params$rationality, ", counting_reinforcement =", params$counting_reinforcement, ", retrieving_reinforcement =", params$retrieving_reinforcement, ", t =", params$t, ", p =", params$p, ", b =", params$b, ", d =", params$d))
  })
  
  #Pour modifier la variable booleenne affirmant si l'utilisateur a un fichier de données empiriques ou pas
  observeEvent(input$no_file,{
    values$no_file=!values$no_file
  })
  
  #Pour changer le nombre de participants
  observeEvent(input$nb_participant, {
    values$nb_participant = input$nb_participant
    updateNumericInput(
      inputId = "nb_participant",
      label = NULL,
      value = values$nb_participant
    )
  })
  
  #Pour changer le nombre de run par participant
  observeEvent(input$nb_run, {
    values$nb_run = input$nb_run
  })
  
  #Pour ouvrir le menu de modification des paramètres du modèle
  observeEvent(input$open_settings, {
    showModal(modalDialog(
      title = "Modifier les paramètres",
      
      fluidRow(
        column(8,numericInput("modal_ic", "Valeur increase_practice:", value = params$increasePractice, min = 0, max = 2, step=0.01)),
        column(4, br(), actionButton("reset_ic", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_rationality", "Valeur rationality:", value = params$rationality, min = 0, max = 1, step=0.0001)),
        column(4, br(), actionButton("reset_rationality", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_counting_reinforcement", "Valeur counting_reinforcement:", value = params$counting_reinforcement, min = 0, max = 2, step=0.01)),
        column(4, br(), actionButton("reset_counting_reinforcement", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_retrieving_reinforcement", "Valeur retrieving_reinforcement:", value = params$retrieving_reinforcement, min = 0, max = 2, step=0.01)),
        column(4, br(), actionButton("reset_retrieving_reinforcement", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_t", "Valeur t:", value = params$t, min = 0, max = 5000, step=50)),
        column(4, br(), actionButton("reset_t", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_p", "Valeur p:", value = params$p, min = 0, max = 150, step=1)),
        column(4, br(), actionButton("reset_p", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_b", "Valeur b:", value = params$b, min = 0, max = 1500, step=1)),
        column(4, br(), actionButton("reset_p", "Reset", class = "btn-secondary btn-sm"))
      ),
      fluidRow(
        column(8,numericInput("modal_d", "Valeur d:", value = params$d, min = 0, max = 700, step=1)),
        column(4, br(), actionButton("reset_d", "Reset", class = "btn-secondary btn-sm"))
      ),
      footer = tagList(
        modalButton("Annuler"),
        actionButton("reset_all", "Reset Tout", class = "btn-warning"),
        actionButton("save_params", "Sauvegarder", class = "btn-primary")
      )
    ))
  })
  
  #Pour réinitialiser les paramètres du modèle
  observeEvent(input$reset_ic, {
    updateNumericInput(session, "modal_ic", value = default_params$increasePractice)
  })
  
  observeEvent(input$reset_rationality, {
    updateNumericInput(session, "modal_rationality", value = default_params$rationality)
  })
  
  observeEvent(input$reset_counting_reinforcement, {
    updateNumericInput(session, "modal_counting_reinforcement", value = default_params$counting_reinforcement)
  })
  
  observeEvent(input$reset_retrieving_reinforcement, {
    updateNumericInput(session, "modal_retrieving_reinforcement", value = default_params$retrieving_reinforcement)
  })
  
  observeEvent(input$reset_t, {
    updateNumericInput(session, "modal_t", value = default_params$t)
  })
  
  observeEvent(input$reset_p, {
    updateNumericInput(session, "modal_p", value = default_params$p)
  })
  
  observeEvent(input$reset_b, {
    updateNumericInput(session, "modal_b", value = default_params$b)
  })
  
  observeEvent(input$reset_d, {
    updateNumericInput(session, "modal_d", value = default_params$d)
  })
  
  observeEvent(input$reset_all, {
    updateNumericInput(session, "modal_ic", value = default_params$increasePractice)
    updateNumericInput(session, "modal_rationality", value = default_params$rationality)
    updateNumericInput(session, "modal_counting_reinforcement", value = default_params$counting_reinforcement)
    updateNumericInput(session, "modal_retrieving_reinforcement", value = default_params$retrieving_reinforcement)
    updateNumericInput(session, "modal_t", value = default_params$t)
    updateNumericInput(session, "modal_p", value = default_params$p)
    updateNumericInput(session, "modal_b", value = default_params$b)
    updateNumericInput(session, "modal_d", value = default_params$d)
  })
  
  #Pour sauvegarder les paramètres du modèle
  observeEvent(input$save_params, {
    safe_value <- function(val) {
      if (is.null(val) || is.na(val) || val == "") {
        return(0)
      }
      return(val)
    }
    
    params$increasePractice <- safe_value(input$modal_ic)
    params$rationality <- safe_value(input$modal_rationality)
    params$counting_reinforcement <- safe_value(input$modal_counting_reinforcement)
    params$retrieving_reinforcement <- safe_value(input$modal_retrieving_reinforcement)
    params$t <- safe_value(input$modal_t)
    params$p <- safe_value(input$modal_p)
    params$d <- safe_value(input$modal_d)
    params$b <- safe_value(input$modal_b)
    
    removeModal()
  })
  
  
  # Pour nettoyer le nom du fichier
  clean_filename <- function(filename) {
    clean_name <- tools::file_path_sans_ext(filename)
    clean_name <- gsub("[^a-zA-Z0-9_-]", "_", clean_name)
    return(clean_name)
  }
  
  # Pour obtenir les données de simulation
  get_sim_data <- reactive({
    if(simulations$simulation == FALSE) {
      return(values$sim_data)
    } else {
      return(simulations$simulated_data)
    }
  })
  
  # Pour vérifier si les données sont prêtes pour les graphiques
  data_ready_for_plots <- reactive({
    if(is.null(values$expe_data)) return(FALSE)
    if(simulations$simulation == FALSE) {
      return(!is.null(values$sim_data))
    } else {
      return(!is.null(simulations$simulated_data))
    }
  })
  
  # Pour vérifier les colonnes requises pour les graphiques de temps
  check_columns_time <- reactive({
    if(!data_ready_for_plots()) return(FALSE)
    
    expe_ok <- all(c("session", "Addend", "rt") %in% colnames(values$expe_data))
    
    if(simulations$simulation == FALSE) {
      sim_ok <- all(c("rt", "session", "strategy", "Addend") %in% colnames(values$sim_data))
    } else {
      sim_data <- get_sim_data()
      sim_ok <- !is.null(sim_data) && all(c("rt", "session", "strategy", "Addend") %in% colnames(sim_data))
    }
    return(expe_ok && sim_ok)
  })
  
  # Pour vérifier les colonnes requises pour les graphiques heatmap
  check_columns_heatmap <- reactive({
    if(!data_ready_for_plots()) return(FALSE)
    
    sim_data <- get_sim_data()
    if(is.null(sim_data)) return(FALSE)
    
    return(all(c("Addend", "session", "strategy") %in% colnames(sim_data)))
  })
  
  # Pour vérifier les colonnes requises pour les graphiques de stratégies
  check_columns_strategies <- reactive({
    if(!data_ready_for_plots()) return(FALSE)
    
    sim_data <- get_sim_data()
    if(is.null(sim_data)) return(FALSE)
    
    return(all(c("Addend", "strategy") %in% colnames(sim_data)))
  })
  
  # Pour vérifier les colonnes requises pour les graphiques overlap
  check_columns_overlap <- reactive({
    if(!data_ready_for_plots()) return(FALSE)
    
    sim_data <- get_sim_data()
    if(is.null(sim_data)) return(FALSE)
    
    return(all(c("Addend", "strategy", "Overlap") %in% colnames(sim_data)))
  })
  
  # Gestion du clic sur le texte
  observeEvent(input$text_clicked, {
    if(simulations$simulation == TRUE) {
      simulations$simulation <- FALSE
      updateSelectInput(session, "sim_choice", "Sélectionnez le fichier des SIMULATIONS :",
                        choices = setNames(values$file_names, values$file_names),
                        selected = NULL)
    } else {
      simulations$simulation <- TRUE
      values$sim_file_name <- NULL
      values$sim_data <- NULL
    }
  })
  
  # Chargement d'un nouveau fichier CSV
  observeEvent(input$csv_file, {
    req(input$csv_file)
    
    tryCatch({
      csv_data <- read.csv(input$csv_file$datapath, stringsAsFactors = FALSE)
      csv_path = input$csv_file$datapath
      file_name <- clean_filename(input$csv_file$name)
      
      #Permet d'ajouter un numéro collé au nom du fichier si un fichier du meme nom existe
      if (file_name %in% values$file_names) {
        counter <- 1
        original_name <- file_name
        while (file_name %in% values$file_names) {
          file_name <- paste0(original_name, "_", counter)
          counter <- counter + 1
        }
      }
      
      was_empty <- length(values$file_names) == 0
      
      values$uploaded_files[[file_name]] <- csv_data
      simulations$simulation_files[[file_name]] <- csv_data
      values$experimental_files[[file_name]] <- csv_data
      values$data_path[[file_name]] = csv_path
      values$file_names <- c(values$file_names, file_name)
      
      choices <- setNames(values$file_names, values$file_names)
      
      #J'update les barres de sélection de fichiers en ajoutant le nouveau fichier
      updateSelectInput(session, "dataset",
                        choices = choices,
                        selected = if (was_empty) NULL else file_name)
      
      updateSelectInput(session, "sim_choice",
                        choices = choices,
                        selected = values$sim_file_name)
      
      updateSelectInput(session, "expe_choice",
                        choices = choices,
                        selected = values$expe_file_name)
      
      showNotification(
        paste("Fichier", input$csv_file$name, "ajouté avec succès !"),
        type = "success"
      )
      
    },
    error = function(e) {
    })
  })
  
  # Sélection d'un fichier dans la liste
  observeEvent(input$dataset, {
    if (input$dataset != "" && input$dataset %in% values$file_names) {
      values$current_data <- values$uploaded_files[[input$dataset]]
    } else {
      values$current_data <- NULL
    }
  })
  
  #Pour modifier la hauteur des graphes
  observeEvent(input$hauteur, {
    values$hauteur <- input$hauteur
  })
  
  #Pour modifier la largeur des graphes
  observeEvent(input$largeur, {
    values$largeur <- input$largeur
  })
  
  #Pour sélectionner un fichier de données simulées
  observeEvent(input$sim_choice, {
    if (input$sim_choice != "" && input$sim_choice %in% values$file_names) {
      values$sim_file_name <- input$sim_choice
      values$sim_data <- values$uploaded_files[[input$sim_choice]]
      
      if("session" %in% colnames(values$sim_data)) {
        #Je change le nombre de session et change ou pas la/les session/s selectionnée/s en conséquence (si ca dépasse)
        new_min <- 1
        new_max <- max(unique(values$sim_data$session))
        current_value_strat <- input$session_strategy_graph
        current_value_time <- input$session_time_slider  
        
        new_value_strat <- if (is.null(current_value_strat) || current_value_strat < new_min || current_value_strat > new_max) {
          new_min
        } else {
          current_value_strat
        }
        
        if (is.null(current_value_time) ||
            length(current_value_time) != 2 ||
            any(current_value_time < new_min) ||
            any(current_value_time > new_max)) {
          new_value_time <- c(new_min, new_max)
        } else {
          new_value_time <- current_value_time
        }
        
        #J'update les slider utilisant des choix relatifs aux sessions
        updateSliderInput(session, "session_strategy_graph",
                          min = new_min,
                          max = new_max,
                          value = new_value_strat)
        
        updateSliderInput( 
          session,
          inputId = "session_time_slider", 
          label="Affichage des sessions", 
          min = 1, max = simulations$sessions, 
          value = new_value_time 
        )
      }
      
    } else {
      values$sim_data <- NULL
    }
  })
  
  #Pour sélectionner un fichier de données expérimentales
  observeEvent(input$expe_choice, {
    if (input$expe_choice != "" && input$expe_choice %in% values$file_names) {
      values$expe_file_name <- input$expe_choice
      values$expe_data <- values$uploaded_files[[input$expe_choice]]
      
      if (simulations$simulation == TRUE) {
        simulations$sessions <- max(values$expe_data$session)
        Ensemble_vals <- unique(values$expe_data$Ensemble)
        #Je récupère les groupes expérimentaux et les addends/augends associés
        if("Ensemble" %in% colnames(values$expe_data)) {
          augends <- list()
          addends <- list()
          i <- 1
          for (val in Ensemble_vals) {
            augends[[i]] <- sort(unique(values$expe_data$Augend[values$expe_data$Ensemble == val]))
            addends[[i]] <- sort(unique(values$expe_data$Addend[values$expe_data$Ensemble == val]))
            i <- i + 1
          }
          simulations$addends <- unique(addends)
          simulations$augends <- unique(augends)
          
          addend_choices <- sapply(simulations$addends, function(x) paste(x, collapse = ","))
          augend_choices <- sapply(simulations$augends, function(x) paste(x, collapse = ","))
        } else {
          simulations$addends <- sort(unique(values$expe_data$Addend))
          simulations$augends <- sort(unique(values$expe_data$Augend))
          
          addend_choices <- paste(simulations$addends, collapse = ",")
          augend_choices <- paste(simulations$augends, collapse = ",")
        }
        
        #J'actualise les sliders demandant des données relatives aux addends et aux augends
        updateSelectInput(session, "addends",
                          choices = addend_choices,
                          selected = NULL)
        
        updateSelectInput(session, "augends",
                          choices = augend_choices,
                          selected = NULL)
        
        #J'actualise le nombre de participant en prenant le nombre de participant dans le fichier choisi
        if("Subject" %in% colnames(values$expe_data))
          values$nb_participant = length(unique(values$expe_data$Subject))
        else if("Participant" %in% colnames(values$expe_data))  
          values$nb_participant = length(unique(values$expe_data$Participant))
        else if("subject" %in% colnames(values$expe_data))  
          values$nb_participant = length(unique(values$expe_data$subject))
        else if("participant" %in% colnames(values$expe_data))  
          values$nb_participant = length(unique(values$expe_data$participant))
        else
          values$nb_participant = 1
        updateNumericInput(
          inputId = "nb_participant",
          label = NULL,
          value = values$nb_participant
        )
        if(values$nb_run <= 0 ){
          values$nb_run = 1
          updateNumericInput(
            inputId = "nb_run",
            label = "Nombre de run par participant",
            value = values$nb_run
          )
        }
      }
      
    } else {
      values$expe_data <- NULL
    }
  })
  
  #Pour modifier les addends choisis pour la simulation
  observeEvent(input$addends, {
    simulations$selected_addend <- input$addends
  })

  #Pour modifier les augends choisis pour la simulation
  observeEvent(input$augends, {
    simulations$selected_augend <- input$augends
  })

  #Pour lancer les simualtions à partir d'un fichier de données empiriques et lancer les graphiques
  updatePlot = function(){
    
    current_file = simulations$file_name
    if(!is.null(simulations$selected_addend) && !is.null(simulations$selected_augend)) {
      if(simulations$selected_addend != "" && simulations$selected_augend != "") {
        
        #Je fais une sécurité pour garantir que le nombre de breakers et de non breakers corresponde bien au nombre de participant total
        nb_b = values$prop_breakers
        nb_nb = values$prop_nonbreakers
        nb_run = values$nb_run
        nb_participant_tot = values$nb_participant
        nb_1 <- floor(nb_b / 100 * nb_participant_tot)
        nb_2 <- floor(nb_nb / 100 * nb_participant_tot)
        reste <- nb_participant_tot - (nb_1 + nb_2)
        if (reste > 0) {
          if (nb_b >= nb_nb) {
            nb_1 <- nb_1 + reste
          } else {
            nb_2 <- nb_2 + reste
          }
        }
        nb_participant <- paste0("_br_", nb_1, "_nbr_", nb_2, "_nbrun_", nb_run)
        #Je crée le nom de fichier en y mettant les informations clés (identifiant unique) de la simulation
        simulations$file_name <- paste0("sim_", values$expe_file_name, "_add_", 
                                        simulations$selected_addend, "_aug_", 
                                        simulations$selected_augend,nb_participant, "_param",
                                        paste(
                                          params$increasePractice,
                                          params$t,
                                          params$p,
                                          params$b,
                                          params$d,
                                          params$rationality,
                                          params$counting_reinforcement,
                                          params$retrieving_reinforcement,
                                          gsub(" ", "_", values$order),
                                          sep = ","
                                        ))
        
        if(is.null(current_file) || current_file!=simulations$file_name){
          simulations$simulated_data <- NULL
          values$plot_flag <- FALSE
          
          file <- paste0("./simulationFiles/", simulations$file_name, ".csv")
          
          #Si le fichier existe déjà, je ne simule pas et récupère ses informations
          if(file.exists(file)) {
            simulations$simulated_data <- read.csv(paste0("./simulationFiles/", simulations$file_name, ".csv"))
            values$plot_flag <- TRUE
            updateProgressBar(
              id = "progress_bar",
              value = 100,
              total = 100,
              status = "success",
              title="Récupération des paramètres ...",
              unit_mark = "%"
            )
          } else {
            #Sinon je simule
            if( all(c("session", "Addend", "rt") %in% colnames(values$expe_data))){
              simulations$sim_message <- TRUE
              
              jar_path <- "./simulation_file.jar"
              
              
              if("Subject" %in% colnames(values$expe_data))
                max_participant = length(unique(values$expe_data$Subject))
              else if("Participant" %in% colnames(values$expe_data))  
                max_participant = length(unique(values$expe_data$Participant))
              else if("subject" %in% colnames(values$expe_data))  
                max_participant = length(unique(values$expe_data$subject))
              else if("participant" %in% colnames(values$expe_data))  
                max_participant = length(unique(values$expe_data$participant))
              else
                max_participant = 1
              
              
              if(values$nb_participant > max_participant){
                values$order = "Non respecté"
              }
              
              source("./recupere_problems.R")
              if(values$order == "Non respecté")
                order=FALSE
              else
                order=TRUE
              
              idProgressBar="progress_bar"
              #Je recupère tous les problèmes du fichier pour chaque parricipant si on suit l'odre, pour le premier sinon
              problem_list = recupere_problemes(values$expe_data, values$nb_participant, order, idProgressBar)
              #Je crée une notice de construction pour reconstruire les problèmes du côté java
              notice_construction_problems = notice_construction(problem_list)
              #J'écris cette notice dans un fichier car le fichier peut ne pas passer entre R et le .jar si il est trop gros
              writeLines(notice_construction_problems, "notice_input.txt")
              #Je donne l'information de la distribution des participant
              nb_participant <- paste0("\"", nb_1, ",", nb_2, ",", nb_run, "\"")
              
              #Je réunis tout ca dans les arguments que je vais envoyer à Java
              args <- c("-jar", jar_path, "notice_input.txt" , paste(
                params$increasePractice,
                params$t,
                params$p,
                params$b,
                params$d,
                params$rationality,
                params$counting_reinforcement,
                params$retrieving_reinforcement,
                sep = ","
              ), simulations$file_name, nb_participant)
              
              nb_sim_tot = nb_participant_tot * nb_run
              updateProgressBar(
                id = "progress_bar",
                value = 0,
                total = 100,
                status = "success",
                title="Lancement des simulations ...",
                unit_mark = "%"
              )
              #Je lance le .jar des simulations et j'actualise la barre de progression en lisant la console Java
              output <- system2("java", args = args, stdout = TRUE, stderr = TRUE)
              
              size_fill_map <- NULL
              current_fill_map <- 0
              current_nb_participant <- 0
              for (line in output) {
                
                if (grepl("^size_fill_map:", line)) {
                  size_fill_map <- as.numeric(sub("size_fill_map:", "", line))
                  next
                }
                
                if (grepl("^fill_map:", line) && !is.null(size_fill_map)) {
                  current_fill_map <- as.numeric(sub("fill_map:", "", line))
                  progress_value <- round((current_fill_map / size_fill_map) * 50)  # Jusqu’à 50%
                  
                  updateProgressBar(
                    id = "progress_bar",
                    value = progress_value,
                    total = 100,
                    status = "success",
                    title = "Transfert des informations vers le programme de simulation ...",
                    unit_mark = "%"
                  )
                  next
                }
                
                if (grepl("^nb_participant:", line)) {
                  current_nb_participant <- as.numeric(sub("nb_participant:", "", line))
                  progress_value <- 50 + round((current_nb_participant / values$nb_participant) * 50)  # De 50 à 100%
                  
                  updateProgressBar(
                    id = "progress_bar",
                    value = progress_value,
                    total = 100,
                    status = "success",
                    title = "Simulations en cours...",
                    unit_mark = "%"
                  )
                  next
                }
              }
              
              #Je récupère ce fichier simulé
              simulations$simulated_data <- read.csv(paste0("./simulationFiles/", simulations$file_name, ".csv"))
              showNotification("Fichier sauvegardé à l'emplacement ", (paste0("./simulationFiles/", simulations$file_name, ".csv")))
              #Je peux plot
              values$plot_flag <- TRUE
              simulations$sim_message <- FALSE
            }
            
            else{
            }
            
          }
          
          if("session" %in% colnames(simulations$simulated_data)) {
            new_min <- 1
            new_max <- max(unique(simulations$simulated_data$session))
            current_value_strategy <- input$session_strategy_graph
            current_value_overlap <- input$session_overlap_graph
            current_value_time <- input$session_time_slider
            
            new_value_strategy <- if(is.null(current_value_strategy) || current_value_strategy < new_min || current_value_strategy > new_max) {
              new_min
            } else {
              current_value_strategy
            }
            
            new_value_overlap <- if(is.null(current_value_overlap) || current_value_overlap < new_min || current_value_overlap > new_max) {
              new_min
            } else {
              current_value_overlap
            }
            
            if (is.null(current_value_time) ||
                length(current_value_time) != 2 ||
                any(current_value_time < new_min) ||
                any(current_value_time > new_max)) {
              new_value_time <- c(new_min, new_max)
            } else {
              new_value_time <- current_value_time
            }
            #J'actualise les slider demandant des informations relatives au nombre de session
            updateSliderInput(session, "session_strategy_graph",
                              min = new_min,
                              max = new_max,
                              value = new_value_strategy)
            
            updateSliderInput(session, "session_overlap_graph",
                              min = new_min,
                              max = new_max,
                              value = new_value_overlap)
            updateSliderInput( 
              session,
              inputId = "session_time_slider", 
              label="Affichage des sessions", 
              min = 1, max = simulations$sessions, 
              value = new_value_time 
            )
          }
        }
      }
    }
  }
  
  # Suppression d'un fichier
  observeEvent(input$remove_file, {
    req(input$dataset, input$dataset != "")
    
    values$uploaded_files[[input$dataset]] <- NULL
    values$file_names <- values$file_names[values$file_names != input$dataset]
    values$current_data <- NULL
    
    if (length(values$file_names) > 0) {
      choices <- setNames(values$file_names, values$file_names)
      updateSelectInput(session, "dataset",
                        choices = choices,
                        selected = "")
    } else {
      updateSelectInput(session, "dataset",
                        choices = c("Aucun fichier disponible" = ""),
                        selected = "")
    }
    
    showNotification("Fichier supprimé", type = "warning")
  })
  

  output$status_info <- renderText({
    n_files <- length(values$file_names)
    
    if (n_files == 0) {
      return("Aucun fichier chargé. Utilisez le bouton 'Ajouter un fichier CSV' pour commencer.")
    }
    
    status <- paste("Nombre de fichiers chargés :", n_files)
    
    if (!is.null(values$current_data)) {
      status <- paste(status,
                      "\nFichier sélectionné :", input$dataset,
                      "\nNombre de lignes :", nrow(values$current_data),
                      "\nNombre de colonnes :", ncol(values$current_data))
    }
    
    return(status)
  })
  
  #La preview des données selectionnées
  output$data_preview <- renderTable({
    req(values$current_data)
    head(values$current_data, 10)
  })
  
  # Debug output (non utilisé)
  output$debug_info <- renderText({
    paste(
      "Simulation mode:", simulations$simulation,
      "\nExpe data:", !is.null(values$expe_data),
      "\nSim data:", !is.null(values$sim_data),
      "\nSimulated data:", !is.null(simulations$simulated_data),
      "\nSelected addend:", simulations$selected_addend,
      "\nSelected augend:", simulations$selected_augend,
      "\nData ready:", data_ready_for_plots(),
      "\nColumns time OK:", check_columns_time(),
      "\nColumns heatmap OK:", check_columns_heatmap(),
      "\nColumns strategies OK:", check_columns_strategies()
    )
  })
  
  #Sauvegarder la configuration de la tâche expérimentale
  observeEvent(input$save_no_file, {
    save()
  })
  
  save = function(){
    showModal(modalDialog(
      title = "Sauvegarder la configuration sous le nom de ...",
      easyClose = FALSE,
      fade = TRUE,
      textInput("save_name",
                label = NULL,
                value = input$save_name),
      footer = tagList(
        actionButton("cancel_save", "Annuler",  style = "color: white; background-color: red;"),
        actionButton("confirm_save", "Sauvegarder", icon=icon("save"),  style = "color: white; background-color: blue;")
      )
    ))
  }
  
  #Annuler la sauvegarde de la configuration
  observeEvent(input$cancel_save, {
    removeModal()
  })
  
  #Demander la confirmation de la sauvegarde de la configuration si un fichier avec le même nom existe 
  observeEvent(input$confirm_save, {
    file = paste0("./configSave/",input$save_name,".rds")
    if(file.exists(file)) {
      showModal(modalDialog(
        title = paste0(input$save_name, ".rds", " existe déjà. Voulez-vous l'écraser ? "),
        easyClose = FALSE,
        fade = TRUE,
        footer = tagList(
          actionButton("cancel_save_ecrase", "Non", style = "color: white; background-color: red;"),
          actionButton("confirm_save_ecrase", "Oui", style = "color: white; background-color: blue;")
        )
      ))
    } else {
      showNotification("Sauvegarde en cours...")
      removeModal()
      
      saveRDS(no_file, file)
      showNotification(paste0("Configuration sauvegardée avec succès sous le nom de ", file, ".rds"))
      
    }
  })
  
  #Annuler la sauvegarde avec ce nom
  observeEvent(input$cancel_save_ecrase, {
    removeModal()
    save()
  })
  
  #Confirmer la sauvegarde de la configuration avec le même nom qu'un autre fichier
  observeEvent(input$confirm_save_ecrase, {
    file = paste0("./configSave/",input$save_name)
    removeModal()
    saveRDS(no_file, paste0(file,".rds"))
    showNotification(paste0("Configuration ", file, ".rds écrasée avec succès !"))
  })
  
  #Charge la configuration de la tâche expérimentale choisie
  load = function(){
    showModal(modalDialog(
      title = "Charger la configuration ...",
      easyClose = FALSE,
      fade = TRUE,
      radioButtons("load_name",
                   label = NULL,
                   choices = list.files(path = "./configSave"),
                   selected = input$load_name),
      footer = tagList(
        actionButton("cancel_load", "Annuler",  style = "color: white; background-color: red;"),
        actionButton("confirm_load", "Charger", icon=icon("save"),  style = "color: white; background-color: blue;")
      )
    ))
  }
  
  #Annule le chargement de la tâche expérimentale
  observeEvent(input$cancel_load, {
    removeModal()
  })
  
  #Confirme le chargement de la tâche expérimentale
  observeEvent(input$confirm_load, {
    file = paste0("./configSave/",input$load_name)
    
    showNotification("Chargement du fichier...")
    
    saved_vals <- readRDS(file)
    
    
    for (name in names(saved_vals)) {
      no_file[[name]] <- saved_vals[[name]]
    }
    updateNumericInput(
      session,
      inputId="no_file_nb_sessions",
      label=NULL,
      value = no_file$nb_sess
    )
    removeModal()
    showNotification(paste0("Fichier ", input$load_name, " chargé avec succès !"))
    no_file$print_plot=FALSE
  })
  
  #Charge une configuration de tâche expérimentale
  observeEvent(input$load_no_file, {
    load()
  })
  
  #Genere la simulation d'une tâche expérimentale sans fichier de données expérimentales
  observeEvent(input$generate, {
    generate()
  })
  
  generate = function(){
    showModal(modalDialog(
      title = "Générer la simulation sous le nom ...",
      easyClose = FALSE,
      fade = TRUE,
      textInput("generate_name",
                label = NULL,
                value = input$generate_name),
      footer = tagList(
        actionButton("cancel_generate", "Annuler",  style = "color: white; background-color: red;"),
        actionButton("confirm_generate", "Générer", icon=icon("robot"),  style = "color: white; background-color: blue;")
      )
    ))
  }
  
  #Annule la génération de la simulation de la tâche expérimentale sans fichier de données empiriques
  observeEvent(input$cancel_generate, {
    removeModal()
  })
  
  #Demande confirmation de la génération de la simulation de la tâche expérimentale sans fichier de données empiriques dans le cas où un fichier de même nom existe
  observeEvent(input$confirm_generate, {
    file = paste0("./simulationFiles/",input$generate_name,".csv")
    if(file.exists(file)) {
      showModal(modalDialog(
        title = paste0(input$generate_name, ".csv", " existe déjà. Voulez-vous l'écraser ? "),
        easyClose = FALSE,
        fade = TRUE,
        footer = tagList(
          actionButton("cancel_generate_ecrase", "Non", style = "color: white; background-color: red;"),
          actionButton("confirm_generate_ecrase", "Oui", style = "color: white; background-color: blue;")
        )
      ))
    } else {      
      removeModal()
      generateNoFile(input$generate_name)
      menu_state(2)
    }
  })
  
  #Résoud un problème avec ou sans erreur pour reconstruire les problèmes à partir d'un Addend, un Augend et (ou pas) une Response
  solve <- function(expr, erreur = 0) {
    parts <- strsplit(expr, "\\+")[[1]]
    letter <- tolower(parts[1])             
    offset <- as.numeric(parts[2])
    
    pos <- match(toupper(letter), LETTERS)  
    new_pos <- pos + offset + erreur
    
    if (new_pos > 26) {
      new_pos <- ((new_pos - 1) %% 26) + 1
    }
    
    new_letter <- tolower(LETTERS[new_pos])  
    
    paste0(letter, "+", offset, "=", new_letter)
  }
  
  #Affiche l'énoncé d'un problème en minuscule (Java travaille sur des minuscule)
  minuscule_enonce <- function(expr) {
    parts <- strsplit(expr, "\\+")[[1]]
    letter <- tolower(parts[1])           
    offset <- as.numeric(parts[2])
    
    paste0(letter, "+", offset)           
  }
  
  #Genere une simulation d'une tâche expérimentale sans fichier de données empiriques
  generateNoFile = function(file){
    showNotification("Génération en cours...")
    #Je récupère toutes les informations clés de la simulation : le nombre de sesison, les paramètres, le nombre de blocs ...
    nb_session = no_file$nb_sess
    pb_list_vec = c()
    nb_bloc_string = NULL
    file_name = file
    parameters = paste(
      params$increasePractice,
      params$t,
      params$p,
      params$b,
      params$d,
      params$rationality,
      params$counting_reinforcement,
      params$retrieving_reinforcement,
      sep = ","
    )
    updateProgressBar(
      id = "progress_bar",
      value = 0,
      total = 100,
      status = "success",
      title="Récupération des paramètres ...",
      unit_mark = "%"
    )
    #je stocke toutes les informations clés de la génération pour les passer à Java
    for(sess in 1:nb_session){
      
      nb_bloc_total_sess = no_file$nb_blocs_session[[paste0("session_",sess)]]
      nb_bloc_string=c(nb_bloc_string, nb_bloc_total_sess)
      problem_list = no_file$problem[[paste0("session_", sess)]]
      problem_list_session = NULL
      production=TRUE
      if(is.null(no_file$type_session[[paste0("session_",sess)]]) || no_file$type_session[[paste0("session_",sess)]] == "Production" ){
        production=TRUE
      }
      else
        production=FALSE
      for(nb_correct_bloc in 1:no_file$correct_bloc[[paste0("session_",sess)]]){
        if(production)
          problem_list_session = c(problem_list_session, lapply(problem_list,minuscule_enonce))
        else
          problem_list_session=c(problem_list_session, lapply(problem_list, solve))
      }
      if(production==FALSE){
        for(nb_incorrect_bloc in 1:length(no_file$incorrect_bloc[[paste0("session_", sess)]])){
          bloc = no_file$incorrect_bloc[[paste0("session_", sess)]][[paste0("bloc_", nb_incorrect_bloc)]]
          type = as.numeric(bloc$type)
          nb_bloc = bloc$number
          for(sess_bloc in 1:nb_bloc){
            problem_list_session = c(problem_list_session, lapply(problem_list, solve, erreur = type))
          }
        }
      }
      problem_list_session = paste(problem_list_session, collapse = ",")
      if(sess==1)
        pb_list_vec=c(pb_list_vec,problem_list_session)
      else
        pb_list_vec <- c(pb_list_vec, paste0("new_sess,", problem_list_session))
      
      updateProgressBar(
        id = "progress_bar",
        value = sess*100/nb_session,
        total = 100,
        status = "success",
        title="Récupération des paramètres ...",
        unit_mark = "%"
      )
    }
    #Je crée un texte contenant tous le problèmes des sessions
    pb_list <- paste(pb_list_vec, collapse = "")
    #Et le nombre de blocs par session
    nb_bloc_string = paste(nb_bloc_string, collapse=",")
    
    updateProgressBar(
      id = "progress_bar",
      value = 0,
      total = 100,
      status = "success",
      title="Initialisation du programme de simulations ...",
      unit_mark = "%"
    )
    jar_path <- "./simulation_no_file.jar"
    
    #Je crée une sécurité pour assurer que le nombre de breakers + le nombre de non breakers corresponde au nombre de participant total
    nb_b = values$prop_breakers
    nb_nb = values$prop_nonbreakers
    nb_run = values$nb_run
    nb_participant_tot = values$nb_participant
    nb_1 <- floor(nb_b / 100 * nb_participant_tot)
    nb_2 <- floor(nb_nb / 100 * nb_participant_tot)
    
    reste <- nb_participant_tot - (nb_1 + nb_2)
    
    if (reste > 0) {
      if (nb_b >= nb_nb) {
        nb_1 <- nb_1 + reste
      } else {
        nb_2 <- nb_2 + reste
      }
    }
    
    nb_participant <- paste0("\"", nb_1, ",", nb_2, ",", nb_run, "\"")
    #J'écris les problèmes dans un fichier texte pour ne pas passer tout le texte à Java (pour éviter de procurer une erreur si le texte est trop gros)
    writeLines(pb_list, "notice_input.txt")
    #Je réunis toutes les informations clés
    args <- c("-jar", jar_path, 
              nb_session, "notice_input.txt", nb_bloc_string, file_name, parameters, nb_participant)
    #Et je lance le .jar en lisant la console de Java pour actualiser la barre de progession
    output <- system2("java", args = args, stdout = TRUE, stderr = TRUE)
    
    size_problem_list <- NULL
    current_problem_list <- 0
    current_nb_participant <- 0
    for (line in output) {
      
      if (grepl("^size_problem_list:", line)) {
        size_problem_list <- as.numeric(sub("size_problem_list:", "", line))
        next
      }
      
      if (grepl("^problem_list:", line) && !is.null(size_problem_list)) {
        current_problem_list <- as.numeric(sub("problem_list:", "", line))
        progress_value <- round((current_problem_list / size_problem_list) * 50)  # Jusqu’à 50%
        
        updateProgressBar(
          id = "progress_bar",
          value = progress_value,
          total = 100,
          status = "success",
          title = "Transfert des informations vers le programme de simulation ...",
          unit_mark = "%"
        )
        next
      }
      
      if (grepl("^nb_participant:", line)) {
        current_nb_participant <- as.numeric(sub("nb_participant:", "", line))
        progress_value <- 50 + round((current_nb_participant / values$nb_participant) * 50)  # De 50 à 100%
        
        updateProgressBar(
          id = "progress_bar",
          value = progress_value,
          total = 100,
          status = "success",
          title = "Simulations en cours...",
          unit_mark = "%"
        )
        next
      }
      
    }
    
    #Je récupère le fichier simulé
    simulations$simulated_data <- read.csv(paste0("./simulationFiles/", file, ".csv"))
    
    showNotification("Fichier sauvegardé à l'emplacement ", paste0("./simulationFiles/", file, ".csv"))
    
    #Et j'affiche les graphes
    no_file$print_plot=TRUE
    
    #J'actualise egalement les slider demandes des inforamtions relatives au nombre de sessions
    if("session" %in% colnames(simulations$simulated_data)) {
      new_min <- 1
      new_max <- max(unique(simulations$simulated_data$session))
      current_value_strategy <- input$session_strategy_graph
      current_value_overlap <- input$session_overlap_graph
      current_value_time <- input$session_time_slider
      
      
      new_value_strategy <- if(is.null(current_value_strategy) || current_value_strategy < new_min || current_value_strategy > new_max) {
        new_min
      } else {
        current_value_strategy
      }
      
      new_value_overlap <- if(is.null(current_value_overlap) || current_value_overlap < new_min || current_value_overlap > new_max) {
        new_min
      } else {
        current_value_overlap
      }
      if (is.null(current_value_time) ||
          length(current_value_time) != 2 ||
          any(current_value_time < new_min) ||
          any(current_value_time > new_max)) {
        new_value_time <- c(new_min, new_max)
      } else {
        new_value_time <- current_value_time
      }
      
      updateSliderInput(session, "session_strategy_graph",
                        min = new_min,
                        max = new_max,
                        value = new_value_strategy)
      
      updateSliderInput(session, "session_overlap_graph",
                        min = new_min,
                        max = new_max,
                        value = new_value_overlap)
      updateSliderInput( 
        session,
        inputId = "session_time_slider", 
        label="Affichage des sessions", 
        min = 1, max = no_file$nb_sess, 
        value = new_value_time 
      )
    }
    
    
  }
  
  #Annuler la génération de simulation en cas de nom de fichier deja existant
  observeEvent(input$cancel_generate_ecrase, {
    removeModal()
    generate()
  })
  
  #Confirmer la génération de simulation en cas de nom de fichier deja existant
  observeEvent(input$confirm_generate_ecrase, {
    file = paste0("./simulationFiles/",input$generate_name)
    removeModal()
    generateNoFile(file)
    menu_state(2)
  })
  
  #Calcule le nombre de problème total d'une session (en fonction de tous les blocs) dans le cas d'une tâche expérimentale sans fichier de données empiriques
  nb_problem_session = function(session){
    nb_inco_bloc=0
    list_inco_bloc=no_file$incorrect_bloc[[paste0("session_", session)]]
    for(incorrect_bloc in 1:length(list_inco_bloc)){
      nb_inco_bloc=nb_inco_bloc+list_inco_bloc[[paste0("bloc_", incorrect_bloc)]]$number
    }
    return(length(no_file$problem[[paste0("session_", session)]]) * (no_file$correct_bloc[[paste0("session_",session)]] + nb_inco_bloc) * no_file$nb_blocs_session[[paste0("session_",session)]])
  }
  
  #Affiche toutes les sessions pour les modifier et ainsi créer une tâche expérimentale personnalisée
  output$sessions_panels <- renderUI({
    nb_sessions <- input$no_file_nb_sessions
    if (nb_sessions <= 0) {
      return(NULL)
    }
    #Actualiser l'affichage
    session_changed()
    #Pour ne pas dupliquer les observer je les stocke une fois pour toutes
    current_sessions <- isolate(sessions_observed())
    panels_list <- lapply(1:nb_sessions, function(i) {
      
      if(is.null(no_file$correct_bloc[[paste0("session_",i)]]))
        no_file$correct_bloc[[paste0("session_",i)]]=1
      
      local({
        i_copy <- i 
        if(is.null(no_file$problem_type[i_copy]) || is.na(no_file$problem_type[i_copy])){
          no_file$problem_type[i_copy]="default"
        }
        problem_label <- isolate({
          #Je regarde si les problèmes de la session sont toutes les combinaisons possibles Addend x Augend ou bien des problèmes personnalisés, et j'affiche le texte en conséquence
          if (!is.null(no_file$problem_type[i_copy]) && !is.na(no_file$problem_type[i_copy]) &&
              no_file$problem_type[i_copy] == "perso") {
            
            HTML(paste('<p style="color: grey;"><i>problèmes personalisés (', nb_problem_session(i_copy)/no_file$nb_blocs_session[[paste0("session_",i)]], ' pb. x ', no_file$nb_blocs_session[[paste0("session_",i)]] ,' blocs = ', nb_problem_session(i_copy) ,' pb.)</i></p>'))
          } else {
            HTML(paste('<p style="color: grey;"><i>toutes combinaisons (', nb_problem_session(i_copy)/no_file$nb_blocs_session[[paste0("session_",i)]], ' pb. x ', no_file$nb_blocs_session[[paste0("session_",i)]] ,' blocs = ', nb_problem_session(i_copy) ,' pb.)</i></p>'))
          }
        })
        
        #Je déploie un accordéon pour personnaliser toutes les sessions
        bslib::accordion(
          bslib::accordion_panel(
            title = paste("Session", i_copy),
            wellPanel(
              textInput(paste0("session_", i_copy, "_augends"),
                        label = "Augends :",
                        value = isolate(no_file$augendList[[paste0("augend_session", i_copy)]])),
              textInput(paste0("session_", i_copy, "_addends"),
                        label = "Addends :",
                        value = isolate(no_file$addendList[[paste0("addend_session", i_copy)]])),
              problem_label,
              actionButton(paste0("combinaison_perso_", i_copy), "Combinaisons personnalisées"),
              actionButton(paste0("applies_to_", i_copy), "Appliquer à ..."),
              actionButton(paste0("applies_everywhere_", i_copy), "Appliquer partout")
            )
          )
        )
        
      })
    })
    
    do.call(tagList, panels_list)
  })
  
  
  observe({
    req(input$no_file_nb_sessions)
    
    lapply(1:input$no_file_nb_sessions, function(i) {
      
      output[[paste0("problem_type_text_", i)]] <- renderUI({
        
        if (!is.null(no_file$problem_type[i]) && no_file$problem_type[i] == "perso") {
          HTML('<p style="color: grey;">problèmes personnalisés</p>')
        } else {
          HTML('<p style="color: grey;">toutes combinaisons</p>')
        }
      })
    })
  })
  
  #Pour actualiser les sessions
  updateSession = function(){
    current_nb_sessions <- input$no_file_nb_sessions
    no_file$nb_sess = current_nb_sessions
    if (!is.null(current_nb_sessions) && current_nb_sessions > 0) {
      for (i in 1:current_nb_sessions) {
        if (!is.null(no_file$augendList[[paste0("augend_session", i)]])) {
          updateTextInput(
            session,
            paste0("session_", i, "_augends"),
            value = no_file$augendList[[paste0("augend_session", i)]]
          )
        }
        if (!is.null(no_file$addendList[[paste0("addend_session", i)]])) {
          updateTextInput(
            session,
            paste0("session_", i, "_addends"),
            value = no_file$addendList[[paste0("addend_session", i)]]
          )
        }
      }
    }
  }
  
  #Pour actualiser les sessions
  observeEvent(input$no_file_nb_sessions, {
    # Petite pause pour laisser renderUI se terminer
    invalidateLater(50, session)
    
    isolate({
      updateSession()
    })
  }, priority = -100) # Priorité basse pour s'exécuter après renderUI
  
  
  
  observe({
    req(input$no_file_nb_sessions)
    
    current_nb_sessions <- isolate(input$no_file_nb_sessions)
    current_sessions <- isolate(sessions_observed())
    
    # Met à jour les problèmes selon augends et addends
    update_problems <- function(session_id, augends_str, addends_str) {
      augends_list <- trimws(unlist(strsplit(augends_str, ",")))
      addends_list <- trimws(unlist(strsplit(addends_str, ",")))
      
      no_file$problem[[paste0("session_", session_id)]] <- character(0)
      for (augend in augends_list) {
        for (addend in addends_list) {
          no_file$problem[[paste0("session_", session_id)]] <- c(
            no_file$problem[[paste0("session_", session_id)]],
            paste0(augend, "+", addend)
          )
        }
      }
      
      no_file$problem_type[session_id] <- "default"
      session_changed(session_changed() + 1)
    }
    
    #Je re-affiche toutes les sessions, sauf les sessions déjà affichés (=afficher les nouvelles)
    for (i in 1:current_nb_sessions) {
      if (i %in% current_sessions) next
      
      if(is.null(no_file$nb_blocs_session[[paste0("session_",i)]])){
        no_file$nb_blocs_session[[paste0("session_",i)]]=1
        
        no_file$temp_save_incorrect_bloc[[paste0("session_", i)]] <- list(bloc_1 = list(type = "NULL", number = 0))
        
        no_file$incorrect_bloc=no_file$temp_save_incorrect_bloc
      }
      
      if(is.null(no_file$nb_blocs_session[[paste0("session_",i)]]))
        no_file$nb_blocs_session[[paste0("session_",i)]]=0
      
      local({
        session_id <- i
        
        # Observe les augends et récupère les listes d'augends
        observeEvent(input[[paste0("session_", session_id, "_augends")]], {
          req(input[[paste0("session_", session_id, "_augends")]])
          
          temp_aug_list <- no_file$augendList[[paste0("augend_session", session_id)]]
          new_augends <- toupper(input[[paste0("session_", session_id, "_augends")]])
          
          if (is.null(temp_aug_list) || temp_aug_list != new_augends) {
            no_file$augendList[[paste0("augend_session", session_id)]] <- new_augends
            updateTextInput(session, paste0("session_", session_id, "_augends"),
                            label = "Augends :", value = new_augends)
            
            if (is_valid_uppercase_string(new_augends) &&
                is_valid_number_list(input[[paste0("session_", session_id, "_addends")]])) {
              update_problems(session_id, new_augends, input[[paste0("session_", session_id, "_addends")]])
            }
          }
        }, ignoreInit = TRUE, ignoreNULL = TRUE)
        
        # Observe les addends et récupère les listes d'addends
        observeEvent(input[[paste0("session_", session_id, "_addends")]], {
          req(input[[paste0("session_", session_id, "_addends")]])
          
          temp_add_list <- no_file$addendList[[paste0("addend_session", session_id)]]
          new_addends <- input[[paste0("session_", session_id, "_addends")]]
          
          if (is.null(temp_add_list) || temp_add_list != new_addends) {
            no_file$addendList[[paste0("addend_session", session_id)]] <- new_addends
            
            if (is_valid_uppercase_string(input[[paste0("session_", session_id, "_augends")]]) &&
                is_valid_number_list(new_addends)) {
              update_problems(session_id,
                              input[[paste0("session_", session_id, "_augends")]],
                              new_addends)
            }
          }
        }, ignoreInit = TRUE, ignoreNULL = TRUE)
        
        # Personnaliser les problèmes
        observeEvent(input[[paste0("combinaison_perso_", session_id)]], {
          req(is_valid_uppercase_string(input[[paste0("session_", session_id, "_augends")]]))
          req(is_valid_number_list(input[[paste0("session_", session_id, "_addends")]]))
          
          augends_list <- trimws(unlist(strsplit(input[[paste0("session_", session_id, "_augends")]], ",")))
          addends_list <- trimws(unlist(strsplit(input[[paste0("session_", session_id, "_addends")]], ",")))
          
          if (is.null(no_file$problem[[paste0("session_", session_id)]])) {
            update_problems(session_id,
                            input[[paste0("session_", session_id, "_augends")]],
                            input[[paste0("session_", session_id, "_addends")]])
          }
          
          #J'ouvre une boite de dialogue pour séléctionner les problèmes que l'on veut, et ceux que l'on ne veut pas
          showModal(modalDialog(
            title = paste("Personnaliser les problèmes de la session", session_id),
            bslib::accordion(
              !!!lapply(seq_along(augends_list), function(i) {
                augend <- augends_list[i]
                checkbox_ids <- paste0(augend, "+", addends_list)
                
                bslib::accordion_panel(
                  title = paste("Augend", augend),
                  wellPanel(
                    checkboxGroupInput(
                      inputId = paste0("checkboxes_problems_", session_id, "_", augend),
                      label = "Choisissez les combinaisons :",
                      choices = checkbox_ids,
                      selected = intersect(no_file$problem[[paste0("session_", session_id)]], checkbox_ids)
                    )
                  )
                )
              })
            ),
            footer = tagList(
              modalButton("Annuler"),
              actionButton(paste0("personalize_problems_session_", session_id), "Sauvegarder", class = "btn-primary")
            ),
            size = "l",
            easyClose = TRUE
          ))
        }, ignoreInit = TRUE)
        
        # Sauvegarder la personnalisation des problèmes
        observeEvent(input[[paste0("personalize_problems_session_", session_id)]], {
          augends_list <- trimws(unlist(strsplit(input[[paste0("session_", session_id, "_augends")]], ",")))
          addends_list <- trimws(unlist(strsplit(input[[paste0("session_", session_id, "_addends")]], ",")))
          
          no_file$problem[[paste0("session_", session_id)]] <- character(0)
          
          for (augend in augends_list) {
            selected <- input[[paste0("checkboxes_problems_", session_id, "_", augend)]]
            if (!is.null(selected)) {
              no_file$problem[[paste0("session_", session_id)]] <- c(
                no_file$problem[[paste0("session_", session_id)]],
                selected
              )
            }
          }
          
          showNotification(paste("Les problèmes de la session", session_id, "ont été personnalisés avec succès"))
          
          if (length(no_file$problem[[paste0("session_", session_id)]]) != length(augends_list) * length(addends_list)) {
            no_file$problem_type[session_id] <- "perso"
          } else {
            no_file$problem_type[session_id] <- "default"
          }
          
          removeModal()
        }, ignoreInit = TRUE)
        
        # Appliquer la session choisie à toutes les autres sessions
        observeEvent(input[[paste0("applies_everywhere_", session_id)]], {
          current_sessions_count <- isolate(input$no_file_nb_sessions)
          source_augend <- isolate(no_file$augendList[[paste0("augend_session", session_id)]])
          source_addend <- isolate(no_file$addendList[[paste0("addend_session", session_id)]])
          source_problem_list <- isolate(no_file$problem[[paste0("session_", session_id)]])
          
          #Pour toutes les sessions, je définis les paramètres associés comme étant ceux de la session choisie. Aussi, j'actualise leur affichage
          for (j in 1:current_sessions_count) {
            no_file$augendList[[paste0("augend_session", j)]] <- source_augend
            no_file$addendList[[paste0("addend_session", j)]] <- source_addend
            no_file$problem[[paste0("session_", j)]] <- source_problem_list
            
            updateTextInput(session, paste0("session_", j, "_augends"), label = "Augends :", value = source_augend)
            updateTextInput(session, paste0("session_", j, "_addends"), label = "Addends :", value = source_addend)
            
            no_file$problem_type[j] <- no_file$problem_type[session_id]
          }
          
          showNotification(paste("Paramètres de la session", session_id, "appliqués à toutes les sessions"))
          
          session_changed(session_changed() + 1)
          
        }, ignoreInit = TRUE)
        
        # Choisir les sessions sur lesquels appliquer les paramètres de la session actuelle
        observeEvent(input[[paste0("applies_to_", session_id)]], {
          showModal(modalDialog(
            title = paste("Appliquer les paramètres de la session", session_id, "à d'autres sessions :"),
            tagList(
              lapply(1:input$no_file_nb_sessions, function(i) {
                if (i != session_id) {
                  checkboxInput(
                    inputId = paste0("checkbox_session_", i),
                    label = paste("Session", i)
                  )
                }
              })
            ),
            footer = tagList(
              modalButton("Annuler"),
              actionButton(paste0("applies_to_session_", session_id), "Appliquer", class = "btn-primary")
            )
          ))
        }, ignoreInit = TRUE)
        
        # Appliquer aux sessions sélectionnées
        observeEvent(input[[paste0("applies_to_session_", session_id)]], {
          selected_sessions <- sapply(1:input$no_file_nb_sessions, function(i) {
            if (i != session_id) {
              input[[paste0("checkbox_session_", i)]]
            } else {
              FALSE
            }
          })
          
          selected_indices <- which(selected_sessions)
          
          if (length(selected_indices) > 0) {
            if (length(selected_indices) == 1) {
              showNotification(paste("Paramètres de la session", session_id, "appliqués à la session", selected_indices))
            } else {
              showNotification(paste("Paramètres de la session", session_id, "appliqués aux sessions", paste(selected_indices, collapse = ", ")))
            }
            
            for (j in selected_indices) {
              no_file$augendList[[paste0("augend_session", j)]] <- no_file$augendList[[paste0("augend_session", session_id)]]
              no_file$addendList[[paste0("addend_session", j)]] <- no_file$addendList[[paste0("addend_session", session_id)]]
              no_file$problem[[paste0("session_", j)]] <- no_file$problem[[paste0("session_", session_id)]]
              
              updateTextInput(session, paste0("session_", j, "_augends"), value = no_file$augendList[[paste0("augend_session", j)]])
              updateTextInput(session, paste0("session_", j, "_addends"), value = no_file$addendList[[paste0("addend_session", j)]])
              
              no_file$problem_type[j] <- no_file$problem_type[session_id]
            }
          }
          
          session_changed(session_changed() + 1)
          
          removeModal()
        })
        
      })
    }
    
    sessions_observed(1:current_nb_sessions)
  })
  
  #Afficher les blocs incorrects pour les sessions contenant des problèmes de type "instructed"
  incorrect_bloc_print = function(my_i){
    tagList(
      lapply(1:length(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]]), function(j) {
        if(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]][[paste0("bloc_",j)]]$type!="NULL"){
          splitLayout(
            cellWidths = c("auto", "auto", "70px", "20px", "80px"),  # ajuste ici si besoin
            tags$span("Nombre de présentations par problème faux ("),
            tags$span("TRUE"),
            selectInput(
              paste0("type_temp_save_incorrect_bloc_", j, "_session_", my_i),
              label = NULL,
              choices = c("+1", "+2", "+3", "-1", "-2", "-3"),
              selected = no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]][[paste0("bloc_",j)]][["type"]],
            ),
            tags$span(") :"),
            numericInput(
              paste0("incorrect_bloc_", j, "session_", my_i),
              label = NULL,
              value = no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]][[paste0("bloc_",j)]][["number"]],
              width = "100%"
            ),
            actionButton(
              inputId = paste0("delete_incorrect_bloc_",j,"_session", my_i),
              label="Supprimer",
              icon=icon("trash"),
              style = "color: white; background-color: red;"
            )
          )
        }
      })
    )
  }
  
  #Pour personnaliser les blocs des sessions lors de la création d'une tâche expérimentale
  block_creation = function(){
    showModal(modalDialog(
      title = paste("Personnaliser les blocs"),
      tags$script(HTML("
  $(document).on('click', '.selectize-input', function() {
    var $dropdown = $(this).siblings('.selectize-dropdown');
    if ($dropdown.length) {
      var offset = $(this).offset();
      var height = $(this).outerHeight();
      $dropdown.css({
        'top': offset.top + height,
        'left': offset.left,
        'width': $(this).outerWidth(),
        'position': 'fixed',
        'z-index': 1050
      });
    }
  });
")),
      #Je déploie un accordéon pour personnaliser chaque sessions
      bslib::accordion(
        
        lapply(1:input$no_file_nb_sessions, function(i) {
          
          bslib::accordion_panel(
            title = paste("Session ", i),
            
            wellPanel(
              h4( "Création du bloc"),
              radioButtons(
                inputId=paste0("type_session_",i),
                label="Type de problèmes",
                choices = c("Production", "Jugement"),
                selected = no_file$temp_type_session[[paste0("session_",i)]]
              ),
              splitLayout(cellWidths = c("auto", "100px"),
                          "Nombre de présentations par problème juste :",
                          numericInput(paste0("correct_bloc_session_",i),
                                       label=NULL,
                                       value = no_file$temp_correct_bloc[[paste0("session_",i)]])),
              conditionalPanel(
                condition=paste0("input.type_session_",i,"== 'Jugement'"),
                actionButton(
                  inputId=paste0("add_incorrect_session_",i),
                  label="+ Ajouter des mauvais blocs"
                ),
                update_bloc_ui(i)
              )
            ),
            wellPanel(
              h4( "Constitution de la session"),
              splitLayout(cellWidths = c("auto", "100px"),
                          "Nombre de blocs par session :",
                          numericInput(paste0("nb_bloc_session_", i), label = NULL, value=no_file$temp_nb_blocs_session[[paste0("session_",i)]])),
              actionButton(
                inputId=paste0("block_applies_to_",i),
                label="Appliquer à ..."
              ),
              actionButton(
                inputId=paste0("block_applies_everywhere",i),
                label="Appliquer partout"
              ),
            ),
          )
        })
      ),
      
      footer = tagList(
        actionButton("annuler_blocs","Annuler"),
        actionButton("save_blocs", "Sauvegarder", class = "btn-primary")
      ),
      size = "l", easyClose = TRUE
    ))
  }
  
  #Pour afficher la personnaliser des blocs
  observeEvent(input$blocs, {
    
    nb_sessions <- input$no_file_nb_sessions
    if (nb_sessions <= 0) {
      showNotification("Pas de session créée", type="error")
      return(NULL)
    }
    
    no_file$temp_correct_bloc=no_file$correct_bloc
    no_file$temp_type_session=no_file$type_session
    no_file$temp_nb_blocs_session=no_file$nb_blocs_session
    
    block_creation()
    
    lapply(1:input$no_file_nb_sessions, function(i) {
      local({
        session_id <- paste0("session_", i)
        if (!isTRUE(already_built()[[session_id]])) {
          my_i <- i
          
          observeEvent(input[[paste0("type_session_", my_i)]], {
            no_file$temp_type_session[[paste0("session_",my_i)]]=input[[paste0("type_session_", my_i)]]
          })
          
          observeEvent(input$save_blocs, {
            
            no_file$incorrect_bloc = no_file$temp_save_incorrect_bloc
            for (sess in 1:length(no_file$incorrect_bloc)) {
              no_file$type_session[[paste0("session_",sess)]]=input[[paste0("type_session_", sess)]]
            }
            
            no_file$correct_bloc = no_file$temp_correct_bloc
            no_file$type_session = no_file$temp_type_session
            no_file$nb_blocs_session = no_file$temp_nb_blocs_session
            
            removeModal()
            
            session_changed(session_changed() + 1)
          })
          
          #Je sauvegarde temporairement les informations renseignées pour les blocs corrects
          observeEvent(input[[paste0("correct_bloc_session_", my_i)]], {
            no_file$temp_correct_bloc[[paste0("session_",my_i)]]=input[[paste0("correct_bloc_session_", my_i)]]
          })
          
          #Je sauvegarde temporairement les informations renseignées pour le nombre de blocs
          observeEvent(input[[paste0("nb_bloc_session_", my_i)]], {
            no_file$temp_nb_blocs_session[[paste0("session_",my_i)]]=input[[paste0("nb_bloc_session_", my_i)]]
          })
          
          
          #Pour sélectionner des sessions sur lesquels appliquer les mêmes blocs que ceux de la session actuelle
          observeEvent(input[[paste0("block_applies_to_", my_i)]], {
            
            session_id = my_i 
            
            showModal(modalDialog(
              title = paste("Appliquer les blocks de la session", session_id, "à d'autres sessions :"),
              tagList(
                lapply(1:input$no_file_nb_sessions, function(i) {
                  if (i != session_id) {
                    checkboxInput(
                      inputId = paste0("block_checkbox_session_", i),
                      label = paste("Session", i)
                    )
                  }
                })
              ),
              footer = tagList(
                modalButton("Annuler"),
                actionButton(paste0("block_applies_to_session_", session_id), "Appliquer", class = "btn-primary")
              )
            ))
            
            
            
          }, ignoreInit=TRUE)
          
          #Pour appliquer à toutes les sessions sélectionnées les blocs de la session actuelle
          observeEvent(input[[paste0("block_applies_to_session_", my_i)]], {
            
            session_id=my_i
            selected_sessions <- sapply(1:input$no_file_nb_sessions, function(i) {
              if (i != session_id) {
                isTRUE(input[[paste0("block_checkbox_session_", i)]])
              } else {
                FALSE
              }
            })
            
            selected_indices <- which(selected_sessions)
            
            
            if (length(selected_indices) > 0) {
              if (length(selected_indices) == 1) {
                showNotification(paste("Blocks de la session", session_id, "appliqués à la session", selected_indices))
              } else {
                showNotification(paste("Blocks de la session", session_id, "appliqués aux sessions", paste(selected_indices, collapse = ", ")))
              }
              
              for (j in selected_indices) {
                
                block_applies_to(session_id, sessions=selected_indices)
                
              }
            }
            
            session_changed(session_changed() + 1)
            
            removeModal()
            block_creation()
          })
          
          #Pour appliquer à toutes les autres sessions les blocs de la session actuelle
          observeEvent(input[[paste0("block_applies_everywhere", my_i)]], {
            block_applies_to(my_i)
          }, ignoreInit = TRUE)
          
          
          #Pour appliquer à toutes les autres sessions les blocs de la session actuelle
          block_applies_to = function(my_i, sessions=input$no_file_nb_sessions){
            
            session_id <- my_i
            
            current_sessions_count <- isolate(sessions)
            
            # Récupération des données source de la session de référence
            source_temp_save_incorrect_bloc <- isolate(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]])
            source_temp_type_session <- isolate(no_file$temp_type_session[[paste0("session_", my_i)]])
            source_temp_correct_bloc <- isolate(no_file$temp_correct_bloc[[paste0("session_", my_i)]])
            source_temp_nb_blocs_session <- isolate(no_file$temp_nb_blocs_session[[paste0("session_", my_i)]])
            
            #Je définis les données à chacun et actualise l'interface
            for (j in 1:current_sessions_count) {
              if(j!=my_i){
                no_file$temp_save_incorrect_bloc[[paste0("session_", j)]] <- source_temp_save_incorrect_bloc
                no_file$temp_type_session[[paste0("session_", j)]] <- source_temp_type_session
                no_file$temp_correct_bloc[[paste0("session_", j)]] <- source_temp_correct_bloc
                no_file$temp_nb_blocs_session[[paste0("session_", j)]] <- source_temp_nb_blocs_session
                
                blocs_list <- no_file$temp_save_incorrect_bloc[[paste0("session_", j)]]
                
                if (!is.null(blocs_list) && length(blocs_list) > 0) {
                  for (bloc in seq_along(blocs_list)) {
                    bloc_data <- blocs_list[[paste0("bloc_", bloc)]]
                    
                    if (!is.null(bloc_data)) {
                      updateSelectInput(
                        session,
                        inputId = paste0("type_temp_save_incorrect_bloc_", bloc, "_session_", j),
                        label = NULL,
                        choices = c("+1", "+2", "+3", "-1", "-2", "-3"),
                        selected = bloc_data[["type"]]
                      )
                      
                      updateNumericInput(
                        session,
                        inputId = paste0("incorrect_bloc_", bloc, "session_", j),
                        label = NULL,
                        value = bloc_data[["number"]]
                      )
                    }
                  }
                }
                
                updateRadioButtons(
                  session,
                  inputId = paste0("type_session_", j),
                  label = "Type de problèmes",
                  choices = c("Production", "Jugement"),
                  selected = no_file$temp_type_session[[paste0("session_", j)]]
                )
                
                updateNumericInput(
                  session,
                  inputId = paste0("correct_bloc_session_", j),
                  label = NULL,
                  value = no_file$temp_correct_bloc[[paste0("session_", j)]]
                )
                
                updateNumericInput(
                  session,
                  inputId = paste0("nb_bloc_session_", j),
                  label = NULL,
                  value = no_file$temp_nb_blocs_session[[paste0("session_", j)]]
                )
                
                incorrect_block(j)
              }
            }
            
            showNotification(paste("Blocs de la session", session_id, "appliqués à toutes les sessions"))
            
            session_changed(session_changed() + 1)
          }
          
          
          incorrect_block = function(my_i, click=FALSE){
            if(click){
              if (is.null(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]])) {
              } else if(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]]$bloc_1$type=="NULL" & no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]]$bloc_1$number==0){
                no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]] <- list(bloc_1 = list(type = "+1", number = 1))
                
              }
              else{
                bloc_name <- paste0("bloc_", length(no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]]) + 1)
                no_file$temp_save_incorrect_bloc[[paste0("session_", my_i)]][[bloc_name]] <- list(type = "+1", number = 1)
              }
            }
            
            update_bloc_ui(my_i)
            
            recreate_bloc_observers(my_i)
            
          }
          
          #Ajoute un blocs de problèmes incorrects à la session courante
          observeEvent(input[[paste0("add_incorrect_session_", my_i)]], {
            incorrect_block(my_i, click=TRUE)
            
          },ignoreInit = TRUE)
          flags <- already_built()
          flags[[session_id]] <- TRUE
          already_built(flags)
        }
      })
    })
  })
  
  #Actualise l'affichage blocs incorrects
  update_bloc_ui = function(my_i){
    output[[paste0("temp_save_incorrect_bloc_ui_", my_i)]] <- renderUI({
      incorrect_bloc_print(my_i)
      
    })
  }
  
  #Crée ou recrée des observer pour chaque bloc incorrect en cas de suppression des blocs

  recreate_bloc_observers <- function(my_i) {
    session_key <- paste0("session_", my_i)
    blocs <- no_file$temp_save_incorrect_bloc[[session_key]]
    
    for (j in seq_along(blocs)) {
      local({
        local_j <- j
        observer_key <- paste0("bloc_", local_j, "session_", my_i)
        
        if (isTRUE(no_file$observerIncorrectBloc[[observer_key]])) {
          return()
        }
        
        observeEvent(input[[paste0("type_temp_save_incorrect_bloc_", local_j, "_session_", my_i)]], {
          type_val <- input[[paste0("type_temp_save_incorrect_bloc_", local_j, "_session_", my_i)]]
          no_file$temp_save_incorrect_bloc[[session_key]][[paste0("bloc_", local_j)]]$type <- type_val
        }, ignoreNULL = TRUE, ignoreInit = TRUE)
        
        observeEvent(input[[paste0("incorrect_bloc_", local_j, "session_", my_i)]], {
          number_val <- input[[paste0("incorrect_bloc_", local_j, "session_", my_i)]]
          no_file$temp_save_incorrect_bloc[[session_key]][[paste0("bloc_", local_j)]]$number <- number_val
        }, ignoreNULL = TRUE, ignoreInit = TRUE)
        
        observeEvent(input[[paste0("delete_incorrect_bloc_", local_j, "_session", my_i)]], {
          no_file$temp_save_incorrect_bloc[[session_key]][[paste0("bloc_", local_j)]] <- NULL
          no_file$observerIncorrectBloc[[observer_key]] <- NULL
          reindex_blocs(my_i)
          recreate_bloc_observers(my_i)
          incorrect_bloc_print(my_i)
          showNotification(paste0("Suppression et réindexation du bloc ", local_j, " de la session ", my_i))
        }, ignoreNULL = TRUE, ignoreInit = TRUE)
        
        no_file$observerIncorrectBloc[[observer_key]] <- TRUE
      })
    }
  }
  
  #Modifie les indices des blocs en cas de suppression des blocs
  reindex_blocs <- function(my_i) {
    session_key <- paste0("session_", my_i)
    
    blocs <- no_file$temp_save_incorrect_bloc[[session_key]]
    if (is.null(blocs)) return()
    
    # Créer une nouvelle liste réindexée
    blocs_renamed <- list()
    for (new_j in seq_along(blocs)) {
      old_name <- names(blocs)[new_j]
      blocs_renamed[[paste0("bloc_", new_j)]] <- blocs[[old_name]]
    }
    
    # Remplacer l'ancienne liste par la nouvelle
    no_file$temp_save_incorrect_bloc[[session_key]] <- blocs_renamed
    
    # Nettoyer tous les observateurs associés
    to_remove <- grep(paste0("session_", my_i), names(no_file$observerIncorrectBloc), value = TRUE)
    for (k in to_remove) {
      no_file$observerIncorrectBloc[[k]] <- NULL
    }
  }
  
  #Annuler la modification des blocs
  observeEvent(input$annuler_blocs, {
    showModal(modalDialog(
      title = "Êtes-vous sûr de quitter sans sauvegarder ?",
      easyClose = FALSE,
      fade = TRUE,
      footer = tagList(
        actionButton("no_cancel_bloc", "Non"),
        actionButton("yes_cancel_bloc", "Oui")
      )
    ))
  })
  
  #Annuler l'annulation de la modification des blocs
  observeEvent(input$no_cancel_bloc, {
    removeModal()
    block_creation()
  })
  
  #Confirme l'annulsation de la modification des blocs
  observeEvent(input$yes_cancel_bloc, {
    no_file$temp_save_incorrect_bloc <- no_file$incorrect_bloc
    removeModal()
  })
  
  #Graphique des temps
  output$time_graph <- renderPlot({ 
    if(values$no_file){
      if(!no_file$print_plot){
        return(NULL)
      }
      else{
        source("./Graphes/times.R")
        return(timeSim(simulations$simulated_data, idProgressBar="progress_bar", sess = input$session_time_slider))
      }
    }
    if(check_columns_time() & values$plot_flag==TRUE & !simulations$sim_message) {
      source("./Graphes/times.R")
      sim_data <- get_sim_data()
      timeComparison(sim_data, values$expe_data, idProgressBar="progress_bar", sess=input$session_time_slider)
    } else {
      return(NULL)
    }
  }, width = function() values$largeur, height = function() values$hauteur)
  
  #Message d'erreur des graphes des temps
  output$error_message_time <- renderUI({
    if(simulations$sim_message== TRUE){
      HTML('<p style="color: red;">Données de simulation en cours de génération...</p>')
    }
    else if(values$no_file){
      if(!no_file$print_plot)
        HTML('
        <p>
          <strong>Veuillez vérifier que vous avez bien paramétré la simulation.</strong>
        </p>')
      else
        return(NULL)
    }
    else if(!check_columns_time()) {
      if(simulations$simulation == FALSE) {
        HTML('
        <p style="color: red; font-weight: bold; font-size: 16px;">
          Fichiers incompatibles détectés.
        </p>
        <p>
          <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
        </p>
        <ul>
          <li>
            <strong>Fichier <em>SIMULATION</em> :</strong><br/>
            Doit contenir au minimum les colonnes :
            <code>Addend</code>, 
            <code>rt</code>, 
            <code>session</code>, 
            <code>strategy</code>
          </li>
          <li>
            <strong>Fichier <em>EXPERIMENTAL</em> :</strong><br/>
            Doit contenir au minimum les colonnes :
            <code>Addend</code>, 
            <code>rt</code>,
            <code>session</code>
          </li>
        </ul>
        <p style="color: #555;">
          Merci de corriger vos fichiers avant de poursuivre.
        </p>
        ')
      } else {
        if(is.null(simulations$selected_addend) || is.null(simulations$selected_augend) || 
           simulations$selected_addend == "" || simulations$selected_augend == "" || !all(c("session", "Addend", "rt") %in% colnames(values$expe_data))) {
          HTML('
  <p style="color: red; font-weight: bold; font-size: 16px;">
    Fichiers incompatibles détectés.
  </p>
  <p>
    <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
  </p>
  <ul>
    <li>
      <strong>Fichier <em>EXPERIMENTAL</em> :</strong><br/>
      Doit contenir au minimum les colonnes :
      <code>Addend</code>, 
      <code>rt</code>,
      <code>session</code>.<br/>
      Si plusieurs groupes d’<code>Augend</code> sont présents (par exemple en raison de plusieurs conditions expérimentales), le fichier doit également contenir la colonne 
      <code>Ensemble</code> afin de distinguer ces groupes.
    </li>
  </ul>
  <p style="color: #555;">
    Merci de corriger vos fichiers avant de poursuivre.
  </p>
')
        }
        else{
          HTML('<p>
    <strong>Pour simuler selon les paramètres définis, veuillez cliquer sur le bouton "Générer".</strong>
  </p>')
        }
      }
    } else {
      return(NULL)
    }
  })
  
  #Graphique heatmap
  output$heatmap_graph <- renderPlot({
    if(values$no_file){
      if(!no_file$print_plot){
        return(NULL)
      }
      else{
        source("./Graphes/heatmap.R")
        return(heatmap(simulations$simulated_data, idProgressBar="progress_bar"))
      }
    }
    if(check_columns_heatmap()& values$plot_flag==TRUE & !simulations$sim_message) {
      source("./Graphes/heatmap.R")
      sim_data <- get_sim_data()
      heatmap(sim_data, idProgressBar="progress_bar")
    } else {
      return(NULL)
    }
  }, width = function() values$largeur, height = function() values$hauteur)
  
  #Message d'erreur du graphique heatmap
  output$error_message_heatmap <- renderUI({
    if(simulations$sim_message== TRUE) {
      HTML('<p style="color: red;">Données de simulation en cours de génération...</p>')
    }
    else if(values$no_file){
      if(!no_file$print_plot)
        HTML('
        <p>
          <strong>Veuillez vérifier que vous avez bien paramétré la simulation.</strong>
        </p>')
      else
        return(NULL)
    }
    else if(!check_columns_heatmap()) {
      if(simulations$simulation == FALSE) {
        HTML('
        <p style="color: red; font-weight: bold; font-size: 16px;">
          Fichiers incompatibles détectés.
        </p>
        <p>
          <strong>Veuillez vérifier que votre fichier respecte le format attendu :</strong>
        </p>
        <ul>
          <li>
            <strong>Fichier <em>SIMULATION</em> :</strong><br/>
            Doit contenir au minimum les colonnes :
            <code>Addend</code>, 
            <code>session</code>, 
            <code>strategy</code>
          </li>
        </ul>
        <p style="color: #555;">
          Merci de corriger vos fichiers avant de poursuivre.
        </p>
        ')
      } else {
        if(is.null(simulations$selected_addend) || is.null(simulations$selected_augend) || 
           simulations$selected_addend == "" || simulations$selected_augend == ""|| !all(c("session", "Addend", "rt") %in% colnames(values$expe_data))) {
          HTML('
  <p style="color: red; font-weight: bold; font-size: 16px;">
    Fichiers incompatibles détectés.
  </p>
  <p>
    <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
  </p>
  <ul>
    <li>
      <strong>Fichier <em>EXPERIMENTAL</em> :</strong><br/>
      Doit contenir au minimum les colonnes :
      <code>Addend</code>, 
      <code>rt</code>,
      <code>session</code>.<br/>
      Si plusieurs groupes d’<code>Augend</code> sont présents (par exemple en raison de plusieurs conditions expérimentales), le fichier doit également contenir la colonne 
      <code>Ensemble</code> afin de distinguer ces groupes.
    </li>
  </ul>
  <p style="color: #555;">
    Merci de corriger vos fichiers avant de poursuivre.
  </p>
')
        } 
        else{
          HTML('<p>
    <strong>Pour simuler selon les paramètres définis, veuillez cliquer sur le bouton "Générer".</strong>
  </p>')
        }
      }
    } else {
      return(NULL)
    }
  })
  
  #Graphiques des stratégies
  output$strategies_graph <- renderPlot({
    if(values$no_file){
      if(!no_file$print_plot){
        return(NULL)
      }
      else{
        source("./Graphes/strategies.R")
        file_name=input$generate_name
        return(strategies(simulations$simulated_data, file_name, input$session_strategy_graph, idProgressBar="progress_bar"))
      }
    }
    if(check_columns_strategies()& values$plot_flag==TRUE & !simulations$sim_message) {
      source("./Graphes/strategies.R")
      sim_data <- get_sim_data()
      file_name <- if(simulations$simulation == FALSE) values$sim_file_name else simulations$file_name
      strategies(sim_data, file_name, input$session_strategy_graph, idProgressBar="progress_bar")
    } else {
      return(NULL)
    }
  }, width = function() values$largeur, height = function() values$hauteur)
  
  #Message d'erreur des graphiques de stratégies
  output$error_message_strategies <- renderUI({
    if(simulations$sim_message == TRUE){
      HTML('<p style="color: red;">Données de simulation en cours de génération...</p>')
    }
    else if(values$no_file){
      if(!no_file$print_plot)
        HTML('
        <p>
          <strong>Veuillez vérifier que vous avez bien paramétré la simulation.</strong>
        </p>')
      else
        return(NULL)
    }
    else if(!check_columns_strategies()) {
      if(simulations$simulation == FALSE) {
        HTML('
        <p style="color: red; font-weight: bold; font-size: 16px;">
          Fichiers incompatibles détectés.
        </p>
        <p>
          <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
        </p>
        <ul>
          <li>
            <strong>Fichier <em>SIMULATION</em> :</strong><br/>
            Doit contenir au minimum les colonnes :
            <code>Addend</code>, 
            <code>strategy</code>
          </li>
        </ul>
        <p style="color: #555;">
          Merci de corriger vos fichiers avant de poursuivre.
        </p>
        ')
      } else {
        if(is.null(simulations$selected_addend) || is.null(simulations$selected_augend) || 
           simulations$selected_addend == "" || simulations$selected_augend == ""|| !all(c("session", "Addend", "rt") %in% colnames(values$expe_data))) {
          HTML('
  <p style="color: red; font-weight: bold; font-size: 16px;">
    Fichiers incompatibles détectés.
  </p>
  <p>
    <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
  </p>
  <ul>
    <li>
      <strong>Fichier <em>EXPERIMENTAL</em> :</strong><br/>
      Doit contenir au minimum les colonnes :
      <code>Addend</code>, 
      <code>rt</code>,
      <code>session</code>.<br/>
      Si plusieurs groupes d’<code>Augend</code> sont présents (par exemple en raison de plusieurs conditions expérimentales), le fichier doit également contenir la colonne 
      <code>Ensemble</code> afin de distinguer ces groupes.
    </li>
  </ul>
  <p style="color: #555;">
    Merci de corriger vos fichiers avant de poursuivre.
  </p>
')
        }
        else{
          HTML('<p>
    <strong>Pour simuler selon les paramètres définis, veuillez cliquer sur le bouton "Générer".</strong>
  </p>')
        }
      }
    } else {
      return(NULL)
    }
  })
  
  #Graphiques d'overlap
  output$overlap_graph <- renderPlot({
    if(values$no_file){
      if(!no_file$print_plot){
        return(NULL)
      }
      else{
        source("./Graphes/time_overlap.R")
        return(time_overlap_grouped(simulations$simulated_data, input$session_strategy_graph, idProgressBar="progress_bar"))
      }
    }
    if(is.null(simulations$selected_addend) || is.null(simulations$selected_augend) || 
       simulations$selected_addend == "" || simulations$selected_augend == ""|| !all(c("session", "Addend", "rt", "Overlap") %in% colnames(values$expe_data))){
      source("./Graphes/time_overlap.R")
      sim_data <- get_sim_data()
      return(time_overlap_grouped(sim_data, input$session_strategy_graph, idProgressBar="progress_bar"))
    }
    else if(check_columns_overlap()& values$plot_flag==TRUE & !simulations$sim_message) {
      source("./Graphes/time_overlap.R")
      sim_data <- get_sim_data()
      time_overlap_groupe_compare(sim_data, values$expe_data, input$session_strategy_graph, idProgressBar="progress_bar")
    } else {
      return(NULL)
    }
  }, width = function() values$largeur, height = function() values$hauteur)
  
  #Messages d'erreur des graphiques d'overlap
  output$error_message_overlap <- renderUI({
    if(simulations$sim_message == TRUE){
      HTML('<p style="color: red;">Données de simulation en cours de génération...</p>')
    }
    else if(values$no_file){
      if(!no_file$print_plot)
        HTML('
        <p>
          <strong>Veuillez vérifier que vous avez bien paramétré la simulation.</strong>
        </p>')
      else
        return(NULL)
    }
    else if(!check_columns_overlap()) {
      if(simulations$simulation == FALSE) {
        HTML('
        <p style="color: red; font-weight: bold; font-size: 16px;">
          Fichiers incompatibles détectés.
        </p>
        <p>
          <strong>Veuillez vérifier que vos fichiers respectent les formats attendus :</strong>
        </p>
        <ul>
          <li>
            <strong>Fichier <em>SIMULATION</em> :</strong><br/>
            Doit contenir au minimum les colonnes :
            <code>Overlap</code>,
            <code>Addend</code>, 
            <code>strategy</code>
          </li>
        </ul>
        <p style="color: #555;">
          Merci de corriger vos fichiers avant de poursuivre.
        </p>
        ')
      } else {
        if(is.null(simulations$selected_addend) || is.null(simulations$selected_augend) || 
           simulations$selected_addend == "" || simulations$selected_augend == ""|| !all(c("session", "Addend", "rt", "Overlap") %in% colnames(values$expe_data))) {
          return(NULL)
        }
        else{
          HTML('<p>
    <strong>Pour simuler selon les paramètres définis, veuillez cliquer sur le bouton "Générer".</strong>
  </p>')
        }
      }
    } else {
      return(NULL)
    }
  })
  
}

shinyApp(ui = ui, server = server)


