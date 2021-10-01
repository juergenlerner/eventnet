
## output directory of eventnet (config.citations.hs.xml)
setwd("<data directory>")

###################################################################
papers <- read.csv("dblp.v12.preproc_PAPERS.csv")

for(test.year in c(2013:2014)){
  train.papers <- papers[papers$TIME < test.year,]
  test.papers <- papers[papers$TIME == test.year,]
  mean.train <- mean(train.papers$WEIGHT)
  rmse.baseline <- sqrt(sum((test.papers$WEIGHT-mean.train)^2)/nrow(test.papers))
  print(paste("test year:",test.year,"RMSE baseline is", rmse.baseline))
  # fit model to training data
  model.citations <- lm(WEIGHT ~ 
                    repetition
                  + cummulative.citations.of.hyperedge
                  + repetition.avg.order.1
                  + repetition.avg.order.2
                  + repetition.avg.order.3
                  + repetition.avg.order.4
                  + repetition.avg.order.5
                  + repetition.avg.order.6
                  + repetition.avg.order.7
                  + repetition.avg.order.8
                  + repetition.avg.order.9
                  + repetition.avg.order.10
                  + sdev.raw.citations.order.1
                  + avg.citations.order.1
                  + avg.citations.order.2
                  + avg.citations.order.3
                  + avg.citations.order.4
                  + avg.citations.order.5
                  + avg.citations.order.6
                  + avg.citations.order.7
                  + avg.citations.order.8
                  + avg.citations.order.9
                  + avg.citations.order.10
                  + number.of.coauthors
                  , data = train.papers
  )
  predicted.citations <- predict(model.citations, newdata = test.papers)
  rmse.model <- sqrt(sum((test.papers$WEIGHT-predicted.citations)^2)/nrow(test.papers))
  print(paste("test year:",test.year,"RMSE LM is", rmse.model))
}


