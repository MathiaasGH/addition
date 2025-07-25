# time_overlap = function(data, sess, nbGroups = 4) {
#   op <- par(no.readonly = TRUE)
#   n_row_col <- ceiling(sqrt(nbGroups))
#   par(mfrow = c(n_row_col, n_row_col))
#   par(mar = c(7, 4, 4, 2))
#   
#   quantileStep = 100 / nbGroups
#   addends = sort(unique(data$Addend))
#   
#   strategy_to_color <- function(p_answer) {
#     rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
#     rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
#   }
#   
#   # je determine les limites de l'axe y
#   rt_valid_means <- c()
#   quantileInf = -1
#   quantileSup = quantileStep
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     group_means = sapply(addends, function(a) mean(dt$rt[dt$Addend == a], na.rm = TRUE))
#     rt_valid_means = c(rt_valid_means, group_means[!is.na(group_means)])
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   if (length(rt_valid_means) > 0) {
#     global_min_rt = min(rt_valid_means)
#     global_max_rt = max(rt_valid_means)
#     ylim_min = max(0, global_min_rt - 0.1 * global_min_rt)
#     ylim_max = global_max_rt + 0.1 * global_max_rt
#   } else {
#     ylim_min = 0
#     ylim_max = 1
#   }
#   
#   # je reboucle pour afficher les graphes
#   quantileInf = -1
#   quantileSup = quantileStep
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     
#     plot(NULL, xlim = c(addends[1], addends[length(addends)]), ylim = c(ylim_min, ylim_max),
#          xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
#          main = paste0(max(0, round(quantileInf, 1)), " < Overlap <= ", round(quantileSup, 1), "\nSession ", sess))
#     axis(1, at = addends, labels = addends)
#     
#     if (nrow(dt) == 0) {
#       text(mean(addends), (ylim_min + ylim_max)/2, "aucune donnée", col = "gray40", cex = 1.2)
#       quantileInf = max(0, quantileInf) + quantileStep
#       quantileSup = quantileSup + quantileStep
#       next
#     }
#     
#     meanTimeAddend = rep(NA, length(addends))
#     colors = rep("#999999", length(addends))
#     
#     for (i in seq_along(addends)) {
#       a = addends[i]
#       subset_dt = dt[dt$Addend == a, ]
#       
#       if (nrow(subset_dt) > 0) {
#         meanTimeAddend[i] = mean(subset_dt$rt, na.rm = TRUE)
#         n_answer = sum(subset_dt$strategy == "answer", na.rm = TRUE)
#         p_answer = n_answer / nrow(subset_dt)
#         colors[i] = strategy_to_color(p_answer)
#       }
#     }
#     
#     if (any(is.na(meanTimeAddend))) {
#       text(mean(addends), ylim_max - 0.025 * ylim_max, "données manquantes", col = adjustcolor("gray70", alpha.f = 0.6), cex = 1)
#     }
#     
#     valid_indices = which(!is.na(meanTimeAddend))
#     points(addends[valid_indices], meanTimeAddend[valid_indices], col = colors[valid_indices], pch = 16, cex = 1)
#     
#     # === Segments colorés entre points valides ===
#     n_seg <- 100
#     for (i in 1:(length(addends) - 1)) {
#       if (!is.na(meanTimeAddend[i]) && !is.na(meanTimeAddend[i+1])) {
#         x_seq <- seq(addends[i], addends[i+1], length.out = n_seg)
#         y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i+1], length.out = n_seg)
#         col_seq <- interpolate_color(colors[i], colors[i+1], n_seg)
#         for (j in 1:(n_seg - 1)) {
#           segments(x_seq[j], y_seq[j], x_seq[j+1], y_seq[j+1], col = col_seq[j], lwd = 2)
#         }
#       }
#     }
#     
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   par(op)
# }

# time_overlap_grouped = function(data, sess, nbGroups = 4) {
#   op <- par(no.readonly = TRUE)
#   par(mar = c(7, 4, 4, 2))
#   
#   quantileStep = 100 / nbGroups
#   addends = sort(unique(data$Addend))
#   
#   strategy_to_color <- function(p_answer) {
#     rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
#     rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
#   }
#   
#   # je determine les limites de l'axe y
#   rt_valid_means <- c()
#   quantileInf = -1
#   quantileSup = quantileStep
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     group_means = sapply(addends, function(a) mean(dt$rt[dt$Addend == a], na.rm = TRUE))
#     rt_valid_means = c(rt_valid_means, group_means[!is.na(group_means)])
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   if (length(rt_valid_means) > 0) {
#     global_min_rt = min(rt_valid_means)
#     global_max_rt = max(rt_valid_means)
#     ylim_min = max(0, global_min_rt - 0.1 * global_min_rt)
#     ylim_max = global_max_rt + 0.1 * global_max_rt
#   } else {
#     ylim_min = 0
#     ylim_max = 1
#   }
#   
#   # je reboucle pour afficher les graphes
#   quantileInf = -1
#   quantileSup = quantileStep
#   plot(NULL, xlim = c(addends[1], addends[length(addends)]), ylim = c(ylim_min, ylim_max),
#        xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
#        main = paste0(max(0, round(quantileInf, 1)), " < Overlap <= ", round(quantileSup, 1), "\nSession ", sess))
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     
#     
#     axis(1, at = addends, labels = addends)
#     
#     if (nrow(dt) == 0) {
#       text(mean(addends), (ylim_min + ylim_max)/2, "aucune donnée", col = "gray40", cex = 1.2)
#       quantileInf = max(0, quantileInf) + quantileStep
#       quantileSup = quantileSup + quantileStep
#       next
#     }
#     
#     meanTimeAddend = rep(NA, length(addends))
#     colors = rep("#999999", length(addends))
#     
#     for (i in seq_along(addends)) {
#       a = addends[i]
#       subset_dt = dt[dt$Addend == a, ]
#       
#       if (nrow(subset_dt) > 0) {
#         meanTimeAddend[i] = mean(subset_dt$rt, na.rm = TRUE)
#         n_answer = sum(subset_dt$strategy == "answer", na.rm = TRUE)
#         p_answer = n_answer / nrow(subset_dt)
#         colors[i] = strategy_to_color(p_answer)
#       }
#     }
#     
#     if (any(is.na(meanTimeAddend))) {
#       text(mean(addends), ylim_max - 0.025 * ylim_max, "données manquantes", col = adjustcolor("gray70", alpha.f = 0.6), cex = 1)
#     }
#     
#     valid_indices = which(!is.na(meanTimeAddend))
#     points(addends[valid_indices], meanTimeAddend[valid_indices], col = colors[valid_indices], pch = 16, cex = 1)
#     
#     # === Segments colorés entre points valides ===
#     n_seg <- 100
#     for (i in 1:(length(addends) - 1)) {
#       if (!is.na(meanTimeAddend[i]) && !is.na(meanTimeAddend[i+1])) {
#         x_seq <- seq(addends[i], addends[i+1], length.out = n_seg)
#         y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i+1], length.out = n_seg)
#         col_seq <- interpolate_color(colors[i], colors[i+1], n_seg)
#         for (j in 1:(n_seg - 1)) {
#           segments(x_seq[j], y_seq[j], x_seq[j+1], y_seq[j+1], col = col_seq[j], lwd = 2)
#         }
#       }
#     }
#     
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   par(op)
# }





interpolate_color <- function(col1, col2, n) {
  rgb1 <- col2rgb(col1)/255
  rgb2 <- col2rgb(col2)/255
  r <- seq(rgb1[1], rgb2[1], length.out = n)
  g <- seq(rgb1[2], rgb2[2], length.out = n)
  b <- seq(rgb1[3], rgb2[3], length.out = n)
  rgb(r, g, b)
}








# time_overlap_grouped = function(data, sess, nbGroups = 4) {
#   op <- par(no.readonly = TRUE)
#   par(mar = c(7, 4, 4, 2))
#   
#   quantileStep = 100 / nbGroups
#   addends = sort(unique(data$Addend))
#   
#   strategy_to_color <- function(p_answer) {
#     rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
#     rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
#   }
#   
#   # Définition des styles pour chaque groupe
#   group_styles <- list(
#     list(pch = 15, lwd = 2),  # carré plein
#     list(pch = 16, lwd = 2),  # cercle plein
#     list(pch = 17, lwd = 2),  # triangle plein
#     list(pch = 18, lwd = 2),  # losange plein
#     list(pch = 3, lwd = 2),   # croix fine
#     list(pch = 4, lwd = 2),   # X
#     list(pch = 8, lwd = 2)    # étoile
#   )
#   
#   
#   
#   # === Étape 1 : calcul des bornes globales pour ylim ===
#   rt_valid_means <- c()
#   quantileInf = -1
#   quantileSup = quantileStep
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     group_means = sapply(addends, function(a) mean(dt$rt[dt$Addend == a], na.rm = TRUE))
#     rt_valid_means = c(rt_valid_means, group_means[!is.na(group_means)])
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   if (length(rt_valid_means) > 0) {
#     global_min_rt = min(rt_valid_means)
#     global_max_rt = max(rt_valid_means)
#     ylim_min = max(0, global_min_rt - 0.1 * global_min_rt)
#     ylim_max = global_max_rt + 0.1 * global_max_rt
#   } else {
#     ylim_min = 0
#     ylim_max = 1
#   }
#   
#   # === Étape 2 : initialisation graphique ===
#   quantileInf = -1
#   quantileSup = quantileStep
#   if(seul)
#     title = paste0("Temps moyens par Addend — Session ", sess, " pour le fichier de simulations")
#   else
#     title = paste0("Temps moyens par Addend — Session ", sess)
#   plot(NULL, xlim = c(addends[1], addends[length(addends)]), ylim = c(ylim_min, ylim_max),
#        xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
#        main = paste0("Temps moyens par Addend — Session ", sess))
#   
#   axis(1, at = addends, labels = addends)
#   
#   legend_labels <- character(nbGroups)
#   legend_styles <- list()
#   
#   for (group in 1:nbGroups) {
#     dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
#     
#     if (nrow(dt) > 0) {
#       meanTimeAddend = rep(NA, length(addends))
#       colors = rep("#999999", length(addends))
#       p_vals = c()
#       
#       for (i in seq_along(addends)) {
#         a = addends[i]
#         subset_dt = dt[dt$Addend == a, ]
#         
#         if (nrow(subset_dt) > 0) {
#           meanTimeAddend[i] = mean(subset_dt$rt, na.rm = TRUE)
#           n_answer = sum(subset_dt$strategy == "answer", na.rm = TRUE)
#           p_answer = n_answer / nrow(subset_dt)
#           colors[i] = strategy_to_color(p_answer)
#           p_vals = c(p_vals, p_answer)
#         }
#       }
#       
#       valid_indices = which(!is.na(meanTimeAddend))
#       
#       current_style = group_styles[[group]]
#       
#       points(addends[valid_indices], meanTimeAddend[valid_indices], 
#              col = "black", pch = current_style$pch, cex = 2.5)
#       points(addends[valid_indices], meanTimeAddend[valid_indices], 
#              col = colors[valid_indices], pch = current_style$pch, cex = 2)
#       
#       n_seg <- 100
#       for (i in 1:(length(addends) - 1)) {
#         if (!is.na(meanTimeAddend[i]) && !is.na(meanTimeAddend[i+1])) {
#           x_seq <- seq(addends[i], addends[i+1], length.out = n_seg)
#           y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i+1], length.out = n_seg)
#           col_seq <- interpolate_color(colors[i], colors[i+1], n_seg)
#           for (j in 1:(n_seg - 1)) {
#             segments(x_seq[j], y_seq[j], x_seq[j+1], y_seq[j+1], 
#                      col = col_seq[j], lwd = current_style$lwd, lty = current_style$lty)
#           }
#         }
#       }
#     }
#     
#     legend_labels[group] = paste0(round(max(0, quantileInf), 1), "–", round(quantileSup, 1))
#     legend_styles[[group]] = group_styles[[group]]
#     
#     quantileInf = max(0, quantileInf) + quantileStep
#     quantileSup = quantileSup + quantileStep
#   }
#   
#   legend("bottom", inset = 0.01,
#          legend = legend_labels,
#          pch = sapply(legend_styles, function(s) s$pch),
#          lwd = sapply(legend_styles, function(s) s$lwd),
#          col = "black",
#          title = "Overlap (%)",
#          horiz = FALSE,
#          ncol = 4,
#          bty = "n", pt.cex = 1.5, cex = 0.9)
#   
#   
#   par(op)
# }
# 
time_overlap_groupe_compare = function(data1, data2, sess, nbGroups=4, idProgressBar){
  op <- par(no.readonly = TRUE)
  par(mfrow = c(1, 2))
  par(mar = c(7, 4, 4, 2))

  time_overlap_grouped(data1, sess, seul=FALSE, idProgressBar)
  time_overlap_grouped(data2, sess, seul=FALSE, idProgressBar)

  par(op)

}

time_overlap_grouped = function(data, sess, seul=TRUE, nbGroups = 4, idProgressBar) {
  updateProgressBar(
    id = idProgressBar,
    value = 0,
    total = 100,
    status = "success",
    title = "Affichage du graphique ...",
    unit_mark = "%"
  )
  if(seul){
    op <- par(no.readonly = TRUE)
    par(mar = c(7, 4, 4, 2))
  }
  quantileStep = 100 / nbGroups
  addends = sort(unique(data$Addend))
  
  strategy_to_color <- function(p_answer) {
    rgb_val <- colorRamp(c("yellow", "purple"))(p_answer)
    rgb(rgb_val[1]/255, rgb_val[2]/255, rgb_val[3]/255)
  }
  
  # Définition des styles pour chaque groupe
  group_styles <- list(
    list(pch = 15, lwd = 2),  # carré plein
    list(pch = 16, lwd = 2),  # cercle plein
    list(pch = 17, lwd = 2),  # triangle plein
    list(pch = 18, lwd = 2),  # losange plein
    list(pch = 3, lwd = 2),   # croix fine
    list(pch = 4, lwd = 2),   # X
    list(pch = 8, lwd = 2)    # étoile
  )
  
  
  
  # === Étape 1 : calcul des bornes globales pour ylim ===
  rt_valid_means <- c()
  quantileInf = -1
  quantileSup = quantileStep
  for (group in 1:nbGroups) {
    dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
    group_means = sapply(addends, function(a) mean(dt$rt[dt$Addend == a], na.rm = TRUE))
    rt_valid_means = c(rt_valid_means, group_means[!is.na(group_means)])
    quantileInf = max(0, quantileInf) + quantileStep
    quantileSup = quantileSup + quantileStep
  }
  
  if (length(rt_valid_means) > 0) {
    global_min_rt = min(rt_valid_means)
    global_max_rt = max(rt_valid_means)
    ylim_min = max(0, global_min_rt - 0.1 * global_min_rt)
    ylim_max = global_max_rt + 0.1 * global_max_rt
  } else {
    ylim_min = 0
    ylim_max = 1
  }
  
  # === Étape 2 : initialisation graphique ===
  quantileInf = -1
  quantileSup = quantileStep
  if(seul)
    title = paste0("Temps moyens par Addend — Session ", sess, " pour le fichier de simulations")
  else
    title = paste0("Temps moyens par Addend — Session ", sess)
  plot(NULL, xlim = c(addends[1], addends[length(addends)]), ylim = c(ylim_min, ylim_max),
       xaxt = "n", xlab = "addend", ylab = "temps moyen (ms)",
       main =title)
  
  axis(1, at = addends, labels = addends)
  
  legend_labels <- character(nbGroups)
  legend_styles <- list()
  

  for (group in 1:nbGroups) {
    dt = data[data$Overlap > quantileInf & data$Overlap <= quantileSup & data$session == sess, ]
    
    if (nrow(dt) > 0) {
      meanTimeAddend = rep(NA, length(addends))
      colors = rep("#999999", length(addends))
      p_vals = c()
      
      for (i in seq_along(addends)) {
        a = addends[i]
        subset_dt = dt[dt$Addend == a, ]
        
        if (nrow(subset_dt) > 0) {
          meanTimeAddend[i] = mean(subset_dt$rt, na.rm = TRUE)
          n_answer = sum(subset_dt$strategy == "answer", na.rm = TRUE)
          p_answer = n_answer / nrow(subset_dt)
          colors[i] = strategy_to_color(p_answer)
          p_vals = c(p_vals, p_answer)
        }
      }
      
      valid_indices = which(!is.na(meanTimeAddend))
      
      current_style = group_styles[[group]]
      
      points(addends[valid_indices], meanTimeAddend[valid_indices], 
             col = "black", pch = current_style$pch, cex = 2.5)
      points(addends[valid_indices], meanTimeAddend[valid_indices], 
             col = colors[valid_indices], pch = current_style$pch, cex = 2)
      
      n_seg <- 100
      for (i in 1:(length(addends) - 1)) {
        if (!is.na(meanTimeAddend[i]) && !is.na(meanTimeAddend[i+1])) {
          x_seq <- seq(addends[i], addends[i+1], length.out = n_seg)
          y_seq <- seq(meanTimeAddend[i], meanTimeAddend[i+1], length.out = n_seg)
          col_seq <- interpolate_color(colors[i], colors[i+1], n_seg)
          for (j in 1:(n_seg - 1)) {
            segments(x_seq[j], y_seq[j], x_seq[j+1], y_seq[j+1], 
                     col = col_seq[j], lwd = current_style$lwd, lty = current_style$lty)
          }
        }
      }
    }
    
    legend_labels[group] = paste0(round(max(0, quantileInf), 1), "–", round(quantileSup, 1))
    legend_styles[[group]] = group_styles[[group]]
    
    quantileInf = max(0, quantileInf) + quantileStep
    quantileSup = quantileSup + quantileStep
    
    updateProgressBar(
      id = idProgressBar,
      value = group*100/nbGroups,
      total = 100,
      status = "success",
      title = "Affichage du graphique ...",
      unit_mark = "%"
    )
    
  }
  
  legend("bottom", inset = 0.01,
         legend = legend_labels,
         pch = sapply(legend_styles, function(s) s$pch),
         lwd = sapply(legend_styles, function(s) s$lwd),
         col = "black",
         title = "Overlap (%)",
         horiz = FALSE,
         ncol = 4,
         bty = "n", pt.cex = 1.5, cex = 0.9)
  
  if(seul)
    par(op)
}