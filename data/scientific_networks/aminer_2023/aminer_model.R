library(survival)

setwd("<output directory of eventnet>")

my.events <- read.csv("dblp_v14_preproc_Journal_AUTHOR_REFERENCES_JOINT.csv")

summary(my.events)
summary(my.events[my.events$IS_OBSERVED == 0,]) # recall: variables that are constant zero on the non-events cannot be used in the model
summary(my.events[my.events$IS_OBSERVED == 1,])

names(my.events)
# square root transformation
my.events[,c(13:ncol(my.events))] <- sqrt(my.events[,c(13:ncol(my.events))])

# strata are identified by concatenation of time, number of authors, and number of references
my.events$stratum <- paste(my.events$TIME, my.events$source.size, my.events$target.size, sep = ":")

my.model <- coxph(Surv(time = rep(1,nrow(my.events)), event = my.events$IS_OBSERVED) ~
                      reference.sub.rep.1
                    + reference.sub.rep.2
                    + reference.sub.rep.3
                    #+ reference.sub.rep.4
                    #+ reference.sub.rep.5
                    + auth.ref.paper.sub.rep.1.1
                    #+ auth.ref.paper.sub.rep.1.2
                    #+ auth.ref.paper.sub.rep.2.1
                    #+ auth.ref.paper.sub.rep.2.2
                    #+ auth.ref.paper.sub.rep.3.1
                    + paper.outdegree.popularity 
                    + cite.paper.and.its.references.1
                    #+ cite.paper.and.its.references.2
                    #+ cite.paper.and.its.references.3
                    #+ cite.paper.and.its.references.4
                    + ref.ref.of.coauthor.closure
                    + author.self.citation
                    #+ author.joint.self.citation.2
                    #+ author.joint.self.citation.3
                    + cite.paper.of.coauthor
                    + author.citation.repetition
                    + author.citation.reciprocation
                    + author.sub.rep.1
                    + author.sub.rep.2
                    #+ author.sub.rep.3
                    + diff.author.publication.activity
                    + avg.author.citation.popularity
                    + diff.author.citation.popularity
                    + collaborate.with.citing.author
                    + author.closure.by.coauthor
                    + author.closure.by.coref.to.paper
                    + author.closure.by.coref.to.author
                    + author.closure.by.coref.from.author
                    #+ author.closure.by.transitive.coref.author
                    + strata(stratum)
                    , robust = TRUE
                    , data = my.events)
summary(my.model)

