Data on actor co-appearances in chapters of Victor Hugo's _Les Misérables_, derived from the file `jean.dat` compiled by Donald Knuth. See the Webpage for the [**Standford GraphBase**](https://www-cs-faculty.stanford.edu/~knuth/sgb.html) for a link to the original data.
The file [`jean_events.csv`](https://github.com/juergenlerner/eventnet/raw/master/data/les_miserables/jean_events.csv) is a CSV file with three columns, `event.id`, `participant`, and `type`. The cell delimiter is the comma (`,`). A row `1.1.1:MY,NP;MY,MB` from Knuth's file `jean.dat` yields the following three rows (in general: as many as the number of different actors in the chapter) in the `jean_events.csv` file:
```
"1.1.1","MY","chapter"
"1.1.1","NP","chapter"
"1.1.1","MB","chapter"
```
The events of type `chapter`, linking the chapter index (e.g., `1.1.1`) to the actors appearing in the chapter (e.g., `MY`), are the actual hyperevents that are to be modeled. These hyperevents are preceeded in the file by 80 dummy events of type `add.actor`, such as the following:
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

If you use this data in a publication, please cite the original data source: Knuth, D. E. (1993). The Stanford GraphBase: a platform for combinatorial computing (Vol. 1). New York: AcM Press.
