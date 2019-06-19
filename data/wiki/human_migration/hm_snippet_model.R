## install the R package 'survival' if necessary (uncomment the following line)
install.packages("survival")
# attach the library
library(survival)

# set the working directory (adapt)
setwd("<output directory of eventnet>")
# read the events
events <- read.csv("hm_events_snippet_EDIT.csv")
summary(events)

var.names <- names(events)
var.names

# scale (standardize) the explanatory variables (statistics)
# this is not mandatory - but makes effect sizes better comparable
events[,c(9:length(var.names))] <- scale(events[,c(9:length(var.names))])

## specify and estimate a Cox proportional hazard model
my.surv <- Surv(time = rep(1,dim(events)[1]), event = events$IS_OBSERVED)
my.model <- coxph(my.surv ~ repetition + X4cycle + activity*popularity
                  + strata(TIME)
                  , data = events)
# print estimated parameters, standard errors, ...
summary(my.model)
