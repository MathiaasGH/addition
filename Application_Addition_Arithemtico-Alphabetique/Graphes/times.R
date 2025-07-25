interpolate_color <- function(col1, col2, n) {
  rgb1 <- col2rgb(col1)/255
  rgb2 <- col2rgb(col2)/255
  r <- seq(rgb1[1], rgb2[1], length.out = n)
  g <- seq(rgb1[2], rgb2[2], length.out = n)
  b <- seq(rgb1[3], rgb2[3], length.out = n)
  rgb(r, g, b)
}

timeComparison = function(sim, exp, idProgressBar, sess) {
  library(tidyverse)
  library(scales) # pour rescale()
  library(Metrics)
  
  updateProgressBar(
    id = idProgressBar,
    value = 0,
    total = 100,
    status = "success",
    title = "Affichage du graphique ...",
    unit_mark = "%"
  )
  
  addends = sort(unique(sim$Addend))
  sessions = intersect(sort(unique(sim$session)), sess[1]:sess[2])
  
  # === Calcul du max global des meanTimeAddend ===
  get_max_meanTimeAddend = function(data) {
    max(sapply(sessions, function(session) {
      sapply(addends, function(addend) {
        mean(data$rt[data$session == session & data$Addend == addend], na.rm = TRUE)
      })
    }), na.rm = TRUE)
  }
  
  # === Calcul du min global des meanTimeAddend ===
  get_min_meanTimeAddend = function(data) {
    min(sapply(sessions, function(session) {
      sapply(addends, function(addend) {
        mean(data$rt[data$session == session & data$Addend == addend], na.rm = TRUE)
      })
    }), na.rm = TRUE)
  }
  
  global_max_rt = max(get_max_meanTimeAddend(sim), get_max_meanTimeAddend(exp), na.rm = TRUE)
  global_min_rt = min(get_min_meanTimeAddend(sim), get_min_meanTimeAddend(exp), na.rm = TRUE)
  
  dt = sim
  par(mfrow = c(1, 2))
  par(mar = c(7, 4, 4, 2)) 
  
  # Fonction pour convertir proportion de answer en couleur
  strategy_to_color <- function(p_answer) {
    rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
    rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
  }
  
  plot(NULL, xlim = c(addends[1], addends[length(addends)]),
       xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
       main = "Temps moyen\nde résolution\ndes problèmes (simulation)",
       ylim = c(max(global_min_rt-10/100*global_min_rt,0), global_max_rt+10/100*global_max_rt))
  
  axis(1, at = addends, labels = addends)
  
  max_sess_first_part = length(sessions)
  nb_sess=0
  for(session in sessions){
    meanTimeAddend = c()
    colors = c()
    
    for(addend in addends){
      subset_dt = dt[which(dt$Addend == addend & dt$session == session), ]
      meanTimeAddend = c(meanTimeAddend, mean(subset_dt$rt, na.rm = TRUE))
      
      n_total <- nrow(subset_dt)
      n_answer <- sum(subset_dt$strategy == "answer")
      p_answer <- ifelse(n_total == 0, 0.5, n_answer / n_total)
      colors = c(colors, strategy_to_color(p_answer))
    }
    
    points(addends, meanTimeAddend, col = colors, pch = 16, cex = 1)
    
    n_seg <- 100
    n_addends <- length(meanTimeAddend) - 1
    
    for (i in 1:n_addends) {
      x_seq <- seq(addends[i], addends[i + 1], length.out = n_seg)
      y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i + 1], length.out = n_seg)
      col_seq <- interpolate_color(colors[i], colors[i + 1], n_seg)
      
      for (j in 1:(n_seg - 1)) {
        segments(x_seq[j], y_seq[j], x_seq[j + 1], y_seq[j + 1], col = col_seq[j], lwd = 2)
      }
    }
    nb_sess=nb_sess+1
    progress_value=nb_sess*50/max_sess_first_part
    updateProgressBar(
      id = idProgressBar,
      value = progress_value,
      total = 100,
      status = "success",
      title = "Affichage du graphique ...",
      unit_mark = "%"
    )
  }
  
  ############### PLOT EXPERIMENTAL
  
  dt = exp
  par(mar = c(7, 4, 4, 2)) 
  
  nb_sess=0
  max_sess_second_part=length(sessions)
  for(session in sessions){
    meanTimeAddend = c()
    
    for(addend in addends){
      meanTimeAddend = c(meanTimeAddend, mean(dt$rt[which(dt$Addend == addend & dt$session == session)], na.rm = TRUE))
    }
    
    if(session == sessions[1]){
      plot(x = addends, y = meanTimeAddend, type = "o", xaxt = "n", xlab = "addend", 
           ylab = "temps moyen (ms)", main = "Temps moyen\nde résolution\ndes problèmes (empirique)", 
           col = "black",   ylim = c(max(global_min_rt-10/100*global_min_rt,0), global_max_rt+10/100*global_max_rt))
    }
    else{
      points(addends, meanTimeAddend, type = "o", col = "black")  
    }
    
    axis(1, at = addends, labels = addends)
    nb_sess=nb_sess+1
    progress_value=nb_sess*50/max_sess_second_part + 50
    updateProgressBar(
      id = idProgressBar,
      value = progress_value,
      total = 100,
      status = "success",
      title = "Affichage du graphique ...",
      unit_mark = "%"
    )
  }
  
  mtext(bquote(N[simulation] == .(length(unique(sim$participant)))), 
        side = 1, outer = TRUE, line = -2.7, cex = 1)
  mtext(bquote(RMSE[total] == .(RMSE_production(sim, exp, sessions))), side = 1, outer = TRUE, line = -1.5, cex = 1)
}



RMSE_production <- function(sim, target, sessions) {
  simVect <- c()
  targetVect <- c()
  
  # Extraction automatique des addends communs aux deux jeux de données
  addends = sort(unique(sim$Addend))
  
  for (sess in sessions) {
    simTime <- sapply(addends, function(a) {
      mean(sim$rt[sim$session == sess & sim$Addend == a], na.rm = TRUE)
    })
    
    targetTime <- sapply(addends, function(a) {
      mean(target$rt[target$session == sess & target$Addend == a], na.rm = TRUE)
    })
    
    simVect <- c(simVect, simTime)
    targetVect <- c(targetVect, targetTime)
  }
  
  # Normalisation conjointe
  norm <- normalize_pair(simVect, targetVect)
  
  # Calcul du RMSE
  return(rmse(norm$x, norm$y))
}


normalize_pair <- function(x, y) {
  min_val = min(c(x, y))
  max_val = max(c(x, y))
  list(
    x = (x - min_val) / (max_val - min_val),
    y = (y - min_val) / (max_val - min_val)
  )
}

timeSim = function(sim, idProgressBar, sess) {
  library(tidyverse)
  library(scales) # pour rescale()
  library(Metrics)
  
  addends = sort(unique(sim$Addend))
  sessions = intersect(sort(unique(sim$session)), sess[1]:sess[2])
  
  
  # === Calcul du max global des meanTimeAddend ===
  get_max_meanTimeAddend = function(data) {
    max(sapply(sessions, function(session) {
      sapply(addends, function(addend) {
        mean(data$rt[data$session == session & data$Addend == addend], na.rm = TRUE)
      })
    }), na.rm = TRUE)
  }
  
  # === Calcul du min global des meanTimeAddend ===
  get_min_meanTimeAddend = function(data) {
    min(sapply(sessions, function(session) {
      sapply(addends, function(addend) {
        mean(data$rt[data$session == session & data$Addend == addend], na.rm = TRUE)
      })
    }), na.rm = TRUE)
  }
  
  global_max_rt = get_max_meanTimeAddend(sim)
  global_min_rt = get_min_meanTimeAddend(sim)
  
  dt = sim
  par(mar = c(7, 4, 4, 2)) 
  
  # Fonction pour convertir proportion de answer en couleur
  strategy_to_color <- function(p_answer) {
    rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
    rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
  }
  
  plot(NULL, xlim = c(addends[1], addends[length(addends)]),
       xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
       main = "Temps moyen\nde résolution\ndes problèmes (simulation)",
       ylim = c(max(global_min_rt-10/100*global_min_rt,0), global_max_rt+10/100*global_max_rt))
  
  axis(1, at = addends, labels = addends)
  
  max_session = length(sessions)
  current_sess=0
  
  for(session in sessions){
    meanTimeAddend = c()
    colors = c()
    
    for(addend in addends){
      subset_dt = dt[which(dt$Addend == addend & dt$session == session), ]
      meanTimeAddend = c(meanTimeAddend, mean(subset_dt$rt, na.rm = TRUE))
      
      n_total <- nrow(subset_dt)
      n_answer <- sum(subset_dt$strategy == "answer")
      p_answer <- ifelse(n_total == 0, 0.5, n_answer / n_total)
      colors = c(colors, strategy_to_color(p_answer))
    }
    
    points(addends, meanTimeAddend, col = colors, pch = 16, cex = 1)
    
    n_seg <- 100
    n_addends <- length(meanTimeAddend) - 1
    
    for (i in 1:n_addends) {
      x_seq <- seq(addends[i], addends[i + 1], length.out = n_seg)
      y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i + 1], length.out = n_seg)
      col_seq <- interpolate_color(colors[i], colors[i + 1], n_seg)
      
      for (j in 1:(n_seg - 1)) {
        segments(x_seq[j], y_seq[j], x_seq[j + 1], y_seq[j + 1], col = col_seq[j], lwd = 2)
      }
    }
    current_sess = current_sess+1
    progress_value = current_sess*100/max_session
    updateProgressBar(
      id = idProgressBar,
      value = progress_value,
      total = 100,
      status = "success",
      title = "Affichage du graphique ...",
      unit_mark = "%"
    )
  }
}



