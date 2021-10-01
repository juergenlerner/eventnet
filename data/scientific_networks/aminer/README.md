On this page we list several scripts or auxiliary software to convert coauthor-network data from [https://www.aminer.org/citation](https://www.aminer.org/citation) into the input format of eventnet.hyper. We also demonstrate how to compute features (explanatory variables or hyperedge statistics) with eventnet.hyper and how this data is used to train and test models for citation prediction. Note that the actual data is provided by Aminer - here we just list scripts to preprocess and analyze this data with RHEM. A reference for the data is (also see the webpage given above).

Jie Tang, Jing Zhang, Limin Yao, Juanzi Li, Li Zhang, and Zhong Su. **ArnetMiner: Extraction and Mining of Academic Social Networks**. In _Proceedings of the Fourteenth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining (SIGKDD'2008)_. pp.990-998.

## Preprocess data for eventnet.hyper

In general, the structure of the input data for eventnet.hyper is explained in the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). The input data might be produced in a number of different ways and possibly starting from different original data. The scripts on this page are provided for convenience and reproducibility. 

Download **DBLP-Citation-network V12** linked from [https://www.aminer.org/citation](https://www.aminer.org/citation). The precise format of the data might change from version to version. So if you want to prepocess a different version you might have to adapt `AminerJSON2CSV.java`. Extract the JSON file from the downloaded compressed file. Denote the extracted file by `dblp.v12.json`. Compile `AminerJSON2CSV.java` which needs the libraries [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) and [JSON Simple](https://code.google.com/archive/p/json-simple/). 

Execute the JSON to CSV conversion by calling (possibly adapt filenames and/or classpath)
```
java kn.uni.eventnet.hyper.util.coauthor.io.AminerJSON2CSV dblp.v12.json dblp.v12.csv
```

Execute the script `coauthor_add_norm_citations.R` to compute a normalized citation count and write out a CSV file `dblp.v12.preproc.csv` for analysis with eventnet.hyper.

## Configuration files for eventnet.hyper

The five configuration files `config.citations...xml` can be executed by eventnet.hyper to compute different types of features (explanatory variables) for published scientific papers. Configurations can be processed from the eventnet GUI or from a command line - see the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). This assumes that the input file `dblp.v12.preproc.csv` is in the same directory from which eventnet has been executed (otherwise the input directory has to be updated in the configurations). The different kinds of features, or their combinations, are reflected in the filenames: `hs` for "hyperedge scores", `id` for high-dimensional sparse feature vectors coding the id of the paper's authors as categorical features, and `pc` for high-dimensional sparse feature vectors coding the past collaborators of the paper's authors. Computed features are stored in different output directories: `./output.hs`, `output.id.pc`, etc. 

These configuration files are for computing features explaining or predicting the normalized number of citations of published papers and not for explaining/predicting which groups of scientists are more or less likely to co-author papers. (Configurations and code for the latter task will follow.) 

The configuration `config.citations.hs.xml` is the only one that produces a rectangular (or full) CSV table with named columns as it is usual for eventnet or eventnet.hyper. This table can be analyzed with standard models (e.g., linear regression) for dense input data, see below. The other four configurations produce output files coding high-dimensional sparse feature vectors. The number of different feature dimensions is in the millions - but for each instance a much smaller number of features are non-zero and only the non-zero values are explicitly stored. We use these sparse features to train and test factorization machines (FM) with the [libFM](http://www.libfm.org/) software.

## Citation prediction based on hyperedge scores (LM)

Predicing citations purely based on the computed hyperedge scores (output produced by the configuration `config.citations.hs.xml`) is straightforward and can for instance be done with linear regression models. The R script `citation_prediction_LM.R` demonstrates how to compute the root mean square error (RMSE) of a baseline (predicing that each paper receives the mean citations over the training data - which is zero due to the normalization of citation counts) and the error of a linear model that is fitted to the 23 hyperedge scores. 
