## Convert data from https://github.com/patperry/interaction-proc/tree/master/data/enron
## into the format required for modeling with RHEM via eventnet.

## Download the three tables employees.tsv, messages.tsv, and recipients.tsv from the URL above. 
## Then execute the following code to produce the file enron_events.csv (which is also available here).

## empty subject field (two tab characters delimiting an empty cell) 
## seem to cause read.table to skip those lines
## workaround: insert "<no subject>"
messages.file  <- readLines("messages.tsv")
msg.file.modified  <- gsub(pattern = "\t\t", replace = "\t<no subject>\t", x = messages.file)
writeLines(msg.file.modified, con="messages.modified.tsv")

messages.raw <- read.table("messages.modified.tsv", sep = "\t", quote = "")
names(messages.raw) <- c("message.id", "original.file", "time", "subject", "sender.id")

receivers.raw <- read.table("recipients.tsv", sep = "\t")
names(receivers.raw) <- c("message.id", "receiver.number", "receiver.id")

employees.raw <- read.table("employees.tsv", sep = "\t")
names(employees.raw) <- c("emp.id", "emp.name", "dept", "dept(long)", "job.title", "gender", "seniority")

# covariate data is added before the first email
before.min.time <- min(messages.raw$time) - 1

## create table "events" eventually used as input for eventnet

# initialize with "email" events
events <- merge(receivers.raw[,c("message.id","receiver.id")], messages.raw[,c("message.id", "time", "sender.id")])
events$type <- "email"
events$weight <- 1.0
events <- events[,c("message.id","sender.id","receiver.id","time","type","weight")]

# create "add.actor" events
num.actors <- max(events$sender.id)
actors <- c(1:num.actors)
dummy.events <- data.frame(rep(0,num.actors),
                           actors, actors,
                           rep(before.min.time,num.actors),
                           rep("add.actor",num.actors),
                           rep(1.0, num.actors))
names(dummy.events) <- names(events)
events <- rbind(dummy.events, events)

# create "is.female" events
females <- employees.raw$emp.id[employees.raw$gender == "Female"]
num.females <- length(females)
dummy.events <- data.frame(rep(0,num.females),
                           females, females,
                           rep(before.min.time,num.females),
                           rep("is.female",num.females),
                           rep(1.0, num.females))
names(dummy.events) <- names(events)
events <- rbind(dummy.events, events)

# create "is.in.department" events
for(department in c("Legal","Trading")){ # dept = "Other" is the reference/default category
  in.department <- employees.raw$emp.id[employees.raw$dept == department]
  num.in.dept <- length(in.department)
  dummy.events <- data.frame(rep(0,num.in.dept),
                             in.department, in.department,
                             rep(before.min.time,num.in.dept),
                             rep(paste("is.in.",department, sep = ""),num.in.dept),
                             rep(1.0, num.in.dept))
  names(dummy.events) <- names(events)
  events <- rbind(dummy.events, events)
}

# create dummy events setting department as a categorical variable
department <- rep(0, nrow(employees.raw))
department[employees.raw$dept == "Legal"] <- 1 
department[employees.raw$dept == "Trading"] <- 2 
dummy.events <- data.frame(rep(0,num.actors),
                           actors, actors,
                           rep(before.min.time,num.actors),
                           rep("department",num.actors),
                           department)
names(dummy.events) <- names(events)
events <- rbind(dummy.events, events)

# create "is.senior" events
seniors <- employees.raw$emp.id[employees.raw$seniority == "Senior"]
num.seniors <- length(seniors)
dummy.events <- data.frame(rep(0,num.seniors),
                           seniors, seniors,
                           rep(before.min.time,num.seniors),
                           rep("is.senior",num.seniors),
                           rep(1.0, num.seniors))
names(dummy.events) <- names(events)
events <- rbind(dummy.events, events)

# order in time (if not already)
events <- events[order(events$time),]

write.csv(events, file = "enron_events.csv", row.names = F)

