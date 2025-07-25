heatmap = function(data, idProgressBar){
  library(dplyr)
  library(ggplot2)
  
  # updateProgressBar(
  #   id = idProgressBar,
  #   value = 0,
  #   total = 100,
  #   status = "success",
  #   title = "Affichage du graphique ...",
  #   unit_mark = "%"
  # )
  heatmap_data <- data %>%
    group_by(Addend, session) %>%
    summarise(proportion_answer = mean(strategy == "answer"), .groups = "drop")
  
  ggplot(heatmap_data, aes(x = factor(session), y = factor(Addend), fill = proportion_answer)) +
    geom_tile(color = "white") +
    scale_fill_gradient(low = "yellow", high = "purple", name = "Prop. 'récup.'", limits = c(0, 1))+
    labs(x = "Session", y = "Addend", title = "Stratégie 'récupération' par Addend et Session") +
    theme_minimal()
  
  # updateProgressBar(
  #   id = idProgressBar,
  #   value = 100,
  #   total = 100,
  #   status = "success",
  #   title = "Affichage du graphique ...",
  #   unit_mark = "%"
  # )
  
}
