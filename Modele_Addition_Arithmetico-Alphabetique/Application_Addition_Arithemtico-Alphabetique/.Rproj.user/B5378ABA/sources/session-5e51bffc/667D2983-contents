
prodFreq = function(dt, addend, sess){
  return(length(which(dt$strategy=="production" & dt$session==sess & dt$Addend==addend))/length(which(dt$session==sess & dt$Addend==addend)))
}

strategies = function(data, dataname, sess, idProgressBar){
  library(readr)
  library(ggplot2)
  
  updateProgressBar(
    id = idProgressBar,
    value = 0,
    total = 100,
    status = "success",
    title = "Affichage du graphique ...",
    unit_mark = "%"
  )
  
  df = data.frame(
    Strategy = factor(),
    Percentage = numeric(),
    Addend = numeric()
  )   
  
  max_addend = length(sort(unique(data$Addend)))
  current_add=0
  
  for(i in sort(unique(data$Addend))){
    Strat = "Comptage"
    Perc = prodFreq(data, i, sess)*100
    Add=i
    new_row <- data.frame(
      Strategy=Strat,
      Percentage=Perc,
      Addend=Add
    )
    df <- rbind(df, new_row)
    Strat = "Récupération"
    Perc = 100-Perc
    Add=i
    new_row <- data.frame(
      Strategy=Strat,
      Percentage=Perc,
      Addend=Add
    )
    df <- rbind(df, new_row)
    
    current_add=current_add+1
    updateProgressBar(
      id = idProgressBar,
      value = current_add*100/max_addend,
      total = 100,
      status = "success",
      title = "Affichage du graphique ...",
      unit_mark = "%"
    )
  }
  ggplot(df, aes(x = factor(Addend), y = Percentage, fill = Strategy, colour = Strategy)) + 
    scale_fill_manual(values=c("yellow", "purple")) +
    geom_bar(stat = "identity", position = "dodge") +  ylim(0,100) + xlab("Addend") + ggtitle(paste0("Pourcentage par Type de Stratégie durant la session ", sess, "\n pour le fichier '", dataname, "'"))
}