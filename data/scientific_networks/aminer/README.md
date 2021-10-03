On this page we list several scripts or auxiliary software to convert coauthor-network data from [https://www.aminer.org/citation](https://www.aminer.org/citation) into the input format of eventnet.hyper. We also demonstrate how to compute features (explanatory variables or hyperedge statistics) with eventnet.hyper and how this data is used to train and test models for citation prediction; for an introduction to relational hyperevent models (RHEM) with eventnet.hyper, see the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). 

Note that the actual data is provided by Aminer - here we just list scripts to preprocess and analyze this data with RHEM. A reference for the data is the following (also see the webpage given above).

Jie Tang, Jing Zhang, Limin Yao, Juanzi Li, Li Zhang, and Zhong Su. **ArnetMiner: Extraction and Mining of Academic Social Networks**. In _Proceedings of the Fourteenth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining (SIGKDD'2008)_. pp.990-998.

## Preprocess data for eventnet.hyper

In general, the structure of the input data for eventnet.hyper is explained in the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). The input data might be produced in a number of different ways and possibly starting from different original data. The scripts on this page are provided for convenience and reproducibility. 

Download **DBLP-Citation-network V12** linked from [https://www.aminer.org/citation](https://www.aminer.org/citation). The precise format of the data might change from version to version so that to prepocess a different version of the data, the conversion script [`AminerJSON2CSV.java`](https://github.com/juergenlerner/eventnet/blob/master/data/scientific_networks/aminer/AminerJSON2CSV.java) possibly has to be adapted. Extract the JSON file from the downloaded compressed file. Denote the extracted file by `dblp.v12.json`. Compile `AminerJSON2CSV.java` which needs the libraries [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) and [JSON Simple](https://code.google.com/archive/p/json-simple/). 

Execute the JSON to CSV conversion by calling (possibly adapt filenames and/or classpath)
```
java kn.uni.eventnet.hyper.util.coauthor.io.AminerJSON2CSV dblp.v12.json dblp.v12.csv
```

Execute the script `coauthor_add_norm_citations.R` to compute a normalized citation count and write out a CSV file `dblp.v12.preproc.csv` for analysis with eventnet.hyper.

## Configuration files for eventnet.hyper

The five configuration files `config.citations...xml` listed on the page [https://github.com/juergenlerner/eventnet/tree/master/data/scientific_networks/aminer](https://github.com/juergenlerner/eventnet/tree/master/data/scientific_networks/aminer) can be executed by eventnet.hyper to compute different types of features (explanatory variables) for published scientific papers. Configurations can be processed from the eventnet GUI or from a command line - see the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). This assumes that the input file `dblp.v12.preproc.csv` is in the same directory from which eventnet has been executed (otherwise the input directory has to be updated in the configurations). The different kinds of features, or their combinations, are reflected in the filenames: `hs` for "hyperedge scores", `id` for high-dimensional sparse feature vectors coding the id of the paper's authors as categorical features, and `pc` for high-dimensional sparse feature vectors coding the past collaborators of the paper's authors. Computed features are stored in different output directories: `./output.hs`, `output.id.pc`, etc. 

These configuration files are for computing features explaining or predicting the normalized number of citations of published papers and not for explaining/predicting which groups of scientists are more or less likely to co-author papers. (Configurations and code for the latter task will follow.) 

The configuration `config.citations.hs.xml` is the only one that produces a rectangular (or full) CSV table with named columns as it is usual for eventnet or eventnet.hyper. This table can be analyzed with standard models (e.g., linear regression) for dense input data, see below. The other four configurations produce output files coding high-dimensional sparse feature vectors. The number of different feature dimensions is in the millions - but for each instance a much smaller number of features are non-zero and only the non-zero values are explicitly stored. We use these sparse features to train and test factorization machines (FM) with the [libFM](http://www.libfm.org/) software.

## Citation prediction based on hyperedge scores with linear regression

Predicing citations purely based on the computed hyperedge scores (output produced by the configuration `config.citations.hs.xml`) is straightforward and can for instance be done with linear regression models. The R script `citation_prediction_LM.R` demonstrates how to compute the root mean square error (RMSE) of a linear model that is fitted to the 23 hyperedge scores. 

## Converting the output of eventnet to libfm format

The output of eventnet.hyper produced by the four other configuration files (all but `config.citations.hs.xml`) provides high-dimensional sparse feature vectors to train and test factorization machines (FM) with the [libFM](http://www.libfm.org/). However, libFM requires that feature dimensions are given by consecutive integers (rather than string labels) and it requires a split into training and test data. This conversion could be done in a number of different ways. For convenience and reproducibility we provide the java class [`ConvertToLibFM4Regression.java`](https://github.com/juergenlerner/eventnet/blob/master/data/scientific_networks/aminer/ConvertToLibFM4Regression.java). The conversion can be started, for instance, with the command
```
java -Xmx64g -cp eventnet.hyper-0.2.jar:commons-csv-1.5.jar kn.uni.eventnet.hyper.util.ConvertToLibFMSequence4Regression dblp.v12.preproc_PAPERS.csv dblp.v12.preproc_PAPERS.csv , EVENTNET:WEIGHT: EVENTNET:SOURCE: EVENTNET:TIME: 2014 2015 1 coauth.train.libfm coauth.test.libfm coauth.testp1.libfm grouping.txt
```

## Training factorization machines with libFM

Given the output from `ConvertToLibFM4Regression.java`, factorization machines can be trained and evaluated with [libFM](http://www.libfm.org/), for instance, by the following command
```
libFM -task r -train 0000_coauth.train.libfm -test 0000_coauth.test.libfm -meta 0000_grouping.txt -out 0000_coauth.out -dim '1,1,32' -iter 256 -method mcmc -init_stdev 0.1
```
