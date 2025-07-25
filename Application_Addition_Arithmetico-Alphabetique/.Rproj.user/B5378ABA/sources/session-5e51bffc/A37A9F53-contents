recupere_problemes <- function(data, nbParticipants = NULL, order = TRUE, idProgressBar, updateProgress=TRUE) {
  
  result_list <- list()
  
  if (order) {
    progress=0
    maxProgress=length(sort(unique(data$Subject)))
    for (participant in sort(unique(data$Subject))) {
      if(updateProgress)
      updateProgressBar(
        id = idProgressBar,
        value = round(progress*100/maxProgress),
        total = 100,
        status = "success",
        title="Récupération des paramètres ...",
        unit_mark = "%"
      )
      for (sess in sort(unique(data$session))) {
        dt <- data[data$Subject == participant & data$session == sess, ]
        
        problems_vec <- with(dt, {
          augends <- tolower(as.character(Augend))
          addends <- tolower(as.character(Addend))
          responses <- if ("Response" %in% names(dt)) tolower(as.character(Response)) else NULL
          
          if (is.null(responses)) {
            paste0(augends, "+", addends)
          } else {
            paste0(augends, "+", addends, "=", responses)
          }
        })
        
        problems_str <- paste(problems_vec, collapse = ",")
        result_list[[length(result_list) + 1]] <- data.frame(
          Participant = participant,
          session = sess,
          problems = problems_str,
          stringsAsFactors = FALSE
        )
      }
      progress=progress+1
    }
    
  } else {
    first_participant <- sort(unique(data$Subject))[1]
    sessions <- sort(unique(data$session))
    
    #le jeu de base c'est le jeu entier (chaque session) du participant 1
    base_sets <- lapply(sessions, function(sess) {
      dt <- data[data$Subject == first_participant & data$session == sess, ]
      
      with(dt, {
        augends <- tolower(as.character(Augend))
        addends <- tolower(as.character(Addend))
        responses <- if ("Response" %in% names(dt)) tolower(as.character(Response)) else NULL
        
        if (is.null(responses)) {
          paste0(augends, "+", addends)
        } else {
          paste0(augends, "+", addends, "=", responses)
        }
      })
    })
    names(base_sets) <- sessions
    

    for (i in 1:nbParticipants) {
      for (sess in sessions) {
        problems_str <- paste(sample(base_sets[[as.character(sess)]]), collapse = ",")
        
        result_list[[length(result_list) + 1]] <- data.frame(
          Participant = i,
          session = sess,
          problems = problems_str,
          stringsAsFactors = FALSE
        )
      }
      if(updateProgress)
      updateProgressBar(
        id = idProgressBar,
        value = round(i*100/nbParticipants),
        total = 100,
        status = "success",
        title="Récupération des paramètres ...",
        unit_mark = "%"
      )
    }
  }
  
  return(do.call(rbind, result_list))
}



notice_construction = function(data){
  notice=NULL
  for(participant in unique(sort(data$Participant))){
    for(sess in unique(sort(data$session))){
      problems = data$problems[data$Participant == participant & data$session == sess]
      notice = paste0(notice, print_line_notice(participant, sess, problems))
    }
  }
  return(notice)
}

print_line_notice = function(participant, session, problems){
  return(paste0("new_participant", participant, "new_session", session, "new_problems", problems))
}
