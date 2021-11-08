The **Enron email data**, is a collection of email messages among employees of Enron Corporation that has been published after the company filed for bankruptcy in 2001. Here we provide the subset of the data that has been cleaned and processed by Zhou et al. (2007) and which has also been used by Perry and Wolfe (2013). We downloaded the data on 13 September 2021 from [https://github.com/patperry/interaction-proc/tree/master/data/enron](https://github.com/patperry/interaction-proc/tree/master/data/enron) and converted it into the format required for modeling with RHEM via eventnet. 

From the GitHub directory referenced above we download the three files `employees.tsv`, `messages.tsv`, and `receipients.tsv`. These files are then converted by the R skript `enron_preprocess.R` which writes the CSV file `enron_events.csv`. For convenience and reproducibility we provide both the R file and the resulting CSV file `enron_events.csv` here. 

The CSV file `enron_events.csv` serves as input for eventnet. Its data format and the analysis with RHEM is explained in the [tutorial on RHEM for multicast interaction networks](https://github.com/juergenlerner/eventnet/wiki/Directed-RHEM-for-multicast-interaction-(tutorial)).

If you want to use the data in a publication please cite the references:
* Zhou, Goldberg, Magdon-Ismail, & Wallace (2007). **Strategies for cleaning organizational emails with an application to enron email dataset**. In _5th Conf. of North American Association for Computational Social and Organizational Science_.
* Perry & Wolfe (2013). **Point process modelling for directed interaction networks**. _Journal of the Royal Statistical Society: Series B_, 75(5), 821-849.
