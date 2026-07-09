library(survival)
setwd("<eventnet output directory>")

##########################################################
events <- read.csv("events_ALTER_NOM.csv")

events$ego.alter.aatie <- events$ego.alter.aatie.forward+events$ego.alter.aatie.backward
events$ego.alter.link.tracing <- events$ego.alter.link.tracing.forward+events$ego.alter.link.tracing.backward

events[,c(13:ncol(events))] <- scale(events[,c(13:ncol(events))])

events$stratum <- paste(events$TIME, events$num.alters, sep = ":")

my.model <- coxph(Surv(time = rep(1,nrow(events)), event = events$IS_OBSERVED) ~
                    female + ego.alter.diff.female + alter.alter.diff.female
                  + age + ego.alter.diff.age + alter.alter.diff.age
                  + bmi + ego.alter.diff.bmi + alter.alter.diff.bmi
                  + procfood + ego.alter.diff.procfood + alter.alter.diff.procfood
                  + edu + ego.alter.diff.edu + alter.alter.diff.edu
                  + smoking + ego.alter.diff.smoking + alter.alter.diff.smoking
                  + nominate.egos
                  + times.nominated + times.pairs.conominated + times.triples.conominated
                  + nomination.repetition 
                  + ego.alter.aatie + alter.alter.aatie 
                  + ego.alter.link.tracing + alter.alter.link.tracing
                  + ego.alter.household + alter.alter.household
                  + ego.alter.family + alter.alter.family
                  + ego.alter.event.coparticipation + alter.alter.event.coparticipation
                  + strata(stratum)
                  , data = events
                  , control = coxph.control(iter.max = 200)
)
summary(my.model)

events.w2 <- events[events$TIME > 100,]
events.w1 <- events[events$TIME <= 100,]

my.model.w2 <- coxph(Surv(time = rep(1,nrow(events.w2)), event = events.w2$IS_OBSERVED) ~
                    female + ego.alter.diff.female + alter.alter.diff.female
                  + age + ego.alter.diff.age + alter.alter.diff.age
                  + bmi + ego.alter.diff.bmi + alter.alter.diff.bmi
                  + procfood + ego.alter.diff.procfood + alter.alter.diff.procfood
                  + edu + ego.alter.diff.edu + alter.alter.diff.edu
                  + smoking + ego.alter.diff.smoking + alter.alter.diff.smoking
                  + nominate.egos
                  + times.nominated + times.pairs.conominated + times.triples.conominated
                  + nomination.repetition 
                  + ego.alter.aatie + alter.alter.aatie 
                  + ego.alter.link.tracing + alter.alter.link.tracing
                  + ego.alter.household + alter.alter.household
                  + ego.alter.family + alter.alter.family
                  + ego.alter.event.coparticipation + alter.alter.event.coparticipation
                  + strata(stratum)
                  , data = events.w2
                  , control = coxph.control(iter.max = 200)
)
summary(my.model.w2)

my.model.w1 <- coxph(Surv(time = rep(1,nrow(events.w1)), event = events.w1$IS_OBSERVED) ~
                       female + ego.alter.diff.female + alter.alter.diff.female
                     + age + ego.alter.diff.age + alter.alter.diff.age
                     + bmi + ego.alter.diff.bmi + alter.alter.diff.bmi
                     + procfood + ego.alter.diff.procfood + alter.alter.diff.procfood
                     + edu + ego.alter.diff.edu + alter.alter.diff.edu
                     + smoking + ego.alter.diff.smoking + alter.alter.diff.smoking
                     + nominate.egos
                     + ego.alter.link.tracing + alter.alter.link.tracing
                     + ego.alter.household + alter.alter.household
                     + ego.alter.family + alter.alter.family
                     + strata(stratum)
                     , data = events.w1
                     , control = coxph.control(iter.max = 200)
)
summary(my.model.w1)

###############################################################
## dyadic

my.model.dyadic <- coxph(Surv(time = rep(1,nrow(events)), event = events$IS_OBSERVED) ~
                    female + ego.alter.diff.female #+ alter.alter.diff.female
                  + age + ego.alter.diff.age #+ alter.alter.diff.age
                  + bmi + ego.alter.diff.bmi #+ alter.alter.diff.bmi
                  + procfood + ego.alter.diff.procfood #+ alter.alter.diff.procfood
                  + edu + ego.alter.diff.edu #+ alter.alter.diff.edu
                  + smoking + ego.alter.diff.smoking #+ alter.alter.diff.smoking
                  + nominate.egos
                  + times.nominated #+ times.pairs.conominated #+ times.triples.conominated
                  + nomination.repetition 
                  + ego.alter.aatie #+ alter.alter.aatie 
                  + ego.alter.link.tracing
                  + ego.alter.household #+ alter.alter.household
                  + ego.alter.family #+ alter.alter.family
                  + ego.alter.event.coparticipation #+ alter.alter.event.coparticipation
                  + strata(stratum)
                  , data = events
                  , control = coxph.control(iter.max = 200)
)
summary(my.model.dyadic)

my.model.w2.dyadic <- coxph(Surv(time = rep(1,nrow(events.w2)), event = events.w2$IS_OBSERVED) ~
                       female + ego.alter.diff.female #+ alter.alter.diff.female
                     + age + ego.alter.diff.age #+ alter.alter.diff.age
                     + bmi + ego.alter.diff.bmi #+ alter.alter.diff.bmi
                     + procfood + ego.alter.diff.procfood #+ alter.alter.diff.procfood
                     + edu + ego.alter.diff.edu #+ alter.alter.diff.edu
                     + smoking + ego.alter.diff.smoking #+ alter.alter.diff.smoking
                     + nominate.egos
                     + times.nominated #+ times.pairs.conominated #+ times.triples.conominated
                     + nomination.repetition
                     + ego.alter.aatie #+ alter.alter.aatie 
                     + ego.alter.link.tracing
                     + ego.alter.household #+ alter.alter.household
                     + ego.alter.family #+ alter.alter.family
                     + ego.alter.event.coparticipation #+ alter.alter.event.coparticipation
                     + strata(stratum)
                     , data = events.w2
                     , control = coxph.control(iter.max = 200)
)
summary(my.model.w2.dyadic)

my.model.w1.dyadic <- coxph(Surv(time = rep(1,nrow(events.w1)), event = events.w1$IS_OBSERVED) ~
                       female + ego.alter.diff.female #+ alter.alter.diff.female
                     + age + ego.alter.diff.age #+ alter.alter.diff.age
                     + bmi + ego.alter.diff.bmi #+ alter.alter.diff.bmi
                     + procfood + ego.alter.diff.procfood #+ alter.alter.diff.procfood
                     + edu + ego.alter.diff.edu #+ alter.alter.diff.edu
                     + smoking + ego.alter.diff.smoking #+ alter.alter.diff.smoking
                     + nominate.egos
                     + ego.alter.link.tracing
                     + ego.alter.household #+ alter.alter.household
                     + ego.alter.family #+ alter.alter.family
                     + ego.alter.event.coparticipation #+ alter.alter.event.coparticipation
                     + strata(stratum)
                     , data = events.w1
                     , control = coxph.control(iter.max = 200)
)
summary(my.model.w1.dyadic)

