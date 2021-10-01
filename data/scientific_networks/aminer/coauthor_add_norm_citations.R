## Script to compute "normalized citation counts" of papers and preprocess data for
## analysis with eventnet.hyper (see https://github.com/juergenlerner/eventnet)

# adapt
setwd("<data directory>")

# CSV file produced by kn.uni.eventnet.hyper.util.coauthor.io.AminerJSON2CSV (possibly adapt filename)
coauthor.links <- read.csv("dblp.v12.csv")

# construct paper table
dup.papers <- duplicated(coauthor.links$paper.id)
papers <- coauthor.links[!dup.papers,c("year","paper.id","n.citation")]
papers$year_factor <- as.factor(papers$year)
table(papers$year) 

# start from 1965 (>= 1000 papers from then on) and predict/analyze up to 2014 
coauthor.links <- coauthor.links[coauthor.links$year >= 1965 & coauthor.links$year <= 2014,]
papers <- papers[papers$year >= 1965 & papers$year <= 2014,]

# compute log citation and mean value by year
papers$log.citations <- log1p(papers$n.citation)
cite.by.year <- lm(log.citations ~ year_factor, data = papers)
papers$mean.log.citations <- predict(cite.by.year)
# normalized citations are log citations minus the mean value of that year
papers$norm.citations <- papers$log.citations - papers$mean.log.citations

papers$year_factor <- NULL
papers$mean.log.citations <- NULL
# merge the paper information and the coauthor links
preproc.coauthor <- merge(coauthor.links, papers)

# sort by publication date
preproc.coauthor <- preproc.coauthor[order(preproc.coauthor$year),]
# write to ouput file
write.csv(preproc.coauthor, file = "dblp.v12.preproc.csv", row.names = F)
