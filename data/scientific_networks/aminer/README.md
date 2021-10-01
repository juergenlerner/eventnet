On this page we list several scripts or auxiliary software to convert coauthor-network data from [https://www.aminer.org/citation](https://www.aminer.org/citation) into the input format of eventnet.hyper for analysis with RHEM. Note that the actual data is provided by Aminer - here we just list scripts to preprocess and analyze this data with RHEM. A reference for the data is (also see the webpage given above).

Jie Tang, Jing Zhang, Limin Yao, Juanzi Li, Li Zhang, and Zhong Su. **ArnetMiner: Extraction and Mining of Academic Social Networks**. In _Proceedings of the Fourteenth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining (SIGKDD'2008)_. pp.990-998.

## Preprocess data for eventnet.hyper

In general, the structure of the input data for eventnet.hyper is explained in the [RHEM tutorial](https://github.com/juergenlerner/eventnet/wiki/RHEM-first-steps-(tutorial)). The input data might be produced in a number of different ways and possibly starting from different original data. The scripts on this page are provided for convenience and reproducibility. 

Download **DBLP-Citation-network V12** linked from [https://www.aminer.org/citation](https://www.aminer.org/citation). The precise format of the data might change from version to version. So if you want to prepocess a different version you might have to adapt `AminerJSON2CSV.java`. Extract the JSON file from the downloaded compressed file. Denote the extracted file by `dblp.v12.json`. Compile `AminerJSON2CSV.java` which needs the libraries [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) and [JSON Simple](https://code.google.com/archive/p/json-simple/). 

Execute the JSON to CSV conversion by calling (possibly adapt filenames and/or classpath)
```
java kn.uni.eventnet.hyper.util.coauthor.io.AminerJSON2CSV dblp.v12.json dblp.v12.csv
```

Execute the script `coauthor_add_norm_citations.R` to compute a normalized citation count and write out a CSV file `dblp.v12.preproc.csv` for analysis with eventnet.hyper.
