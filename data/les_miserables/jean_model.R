library(survival)

setwd("<output directory of eventnet>")

chapters <- read.csv("jean_events_EVENTS.csv")

sum(chapters$IS_OBSERVED) # 288
summary(chapters) # summary statistics 
summary(chapters[chapters$IS_OBSERVED == 1,]) # ... over events
summary(chapters[chapters$IS_OBSERVED == 0,]) # ... over non-events

my.model <- coxph(Surv(time = rep(1,nrow(chapters)), event = chapters$IS_OBSERVED) ~
                             sub.rep.1
                           + sub.rep.2
                           + sub.rep.3
                           + assortativity.1
                           + assortativity.2
                           + closure
                           + female
                           + homophily
                           + strata(EVENT_INTERVAL)
                           , data = chapters)
summary(my.model)
