Data on actor co-appearances in chapters of Victor Hugo's _Les Misérables_, derived from the file [`jean.dat`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean.dat) compiled by Donald Knuth. See the Webpage for the [**Standford GraphBase**](https://www-cs-faculty.stanford.edu/~knuth/sgb.html) for a link to the original data.  
The file [`jean_events.csv`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean_events.csv) is a CSV file with three columns, `event.id`, `participant`, and `type`. The cell delimiter is the comma (`,`). A row `1.1.1:MY,NP;MY,MB` from Knuth's file `jean.dat` yields the following three rows (in general: as many rows as the number of different actors in the chapter) in the `jean_events.csv` file:
```
"1.1.1","MY","chapter"
"1.1.1","NP","chapter"
"1.1.1","MB","chapter"
```
The events of type `chapter`, linking the chapter index (e.g., `1.1.1`) to the actors appearing in the chapter (e.g., `MY`), are the actual hyperevents that are to be modeled. These hyperevents are preceded in the file by 80 dummy events of type `add.actor`, such as the following:
```
"dummy","BS","add.actor"
"dummy","HL","add.actor"
...
```
defining the set of actors that could appear in chapters ("risk set") and 28 dummy events of type `is.female`, such as the following:
```
"dummy","AZ","is.female"
"dummy","BT","is.female"
```
defining which of the actors are female. (This information is based on the actor names and short descriptions given in `jean.dat`, as well as on our own reading of the novel.)

For convenience, we also provide a copy of the unchanged file [`jean.dat`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean.dat) since it contains a short description of the actors encoded by the two-letter acronyms, such as
```
MY Monsieur Charles Fran\c{c}ois Bienvenu Myriel, Bishop of D--
```

If you use this data in a publication, please also cite the original data source: Knuth, D. E. (1993). The Stanford GraphBase: a platform for combinatorial computing (Vol. 1). New York: ACM Press.

The file [`jean_config.txt`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean_config.txt) is an eventnet configuration file to compute hyperedge statistics based on the given `csv` table. The configuration file can be executed either by loading it in the eventnet GUI, and execute from there, or via the command
```
java -Xmx4g -jar eventnet-1.3.jar jean_config.txt
```
(possibly adapt version numbers and/or memory size) assuming that the `.jar` file, the `.csv` file, and the configuration file are all in the current directory.

The file [`jean_model.R`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean_model.R) is a short R code file demonstrating how to estimate RHEM parameters with the `coxph` function in the `survival` package.
