# install.packages("survival") # uncomment if needed

# attach the library
library(survival)

# set the working directory to the output directory of eventnet; change potentially
setwd(".")

# read explanatory variables from the eventnet output file 
edit.events <- read.csv("WikiEvent.2021_EDIT.csv", sep = ";")

sum(edit.events$IS_OBSERVED) # number of events

summary(edit.events)

# check summary statistics separately for events and non-events
# especially note the big difference in the repetition statistic
summary(edit.events[edit.events$IS_OBSERVED == 1,])
summary(edit.events[edit.events$IS_OBSERVED == 0,])

# specify and estimate a Cox proportional hazard model
edit.model <- coxph(Surv(time = rep(1,nrow(edit.events)), event = edit.events$IS_OBSERVED) ~ repetition 
                    + article_popularity 
                    * user_activity 
                    + four_cycle
                    + strata(EVENT) 
                    , data = edit.events)

# print model parameters
print(summary(edit.model))

