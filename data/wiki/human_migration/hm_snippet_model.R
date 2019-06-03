## install the R package 'survival' if necessary (uncomment the following line)
install.packages("survival")
# attach the library
library(survival)

# set the working directory (adapt)
setwd("<output directory of eventnet>")
# read the events
events <- read.csv("hm_events_snippet_EDIT.csv")
summary(events)

## specify and estimate a Cox proportional hazard model
my.surv <- Surv(time = rep(1,dim(events)[1]), event = events$IS_OBSERVED)
my.model <- coxph(my.surv ~ repetition + X4cycle + activity*popularity
                  + strata(TIME)
                  , data = events)
# print estimated parameters, standard errors, ...
summary(my.model)

####################################################
# BECOME_ACTIVE
events <- read.csv("hm_events_snippet_BECOME_ACTIVE.csv")
summary(events)

## specify and estimate a Cox proportional hazard model
my.surv <- Surv(time = rep(1,dim(events)[1]), event = events$IS_OBSERVED)
my.model <- coxph(my.surv ~ 
                  user_activity 
                  + strata(TIME)
                  , data = events)
# print estimated parameters, standard errors, ...
summary(my.model)

####################################################
# CHOOSE_ARTICLE
events <- read.csv("hm_events_snippet_CHOOSE_ARTICLE.csv")
summary(events)

## specify and estimate a Cox proportional hazard model
my.surv <- Surv(time = rep(1,dim(events)[1]), event = events$IS_OBSERVED)
my.model <- coxph(my.surv ~ repetition 
                  + four_cycle
                  + article_popularity
                  + user_activity:article_popularity
                  + strata(TIME)
                  , data = events)
# print estimated parameters, standard errors, ...
summary(my.model)
