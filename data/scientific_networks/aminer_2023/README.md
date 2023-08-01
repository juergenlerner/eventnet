On this page we provide auxiliary software to convert scientific networks from [https://www.aminer.org/citation](https://www.aminer.org/citation), version V14, into the input format of eventnet.hyper. Moreover, we provide an eventnet configuration file and an R script to compute hyperedge statistics and to test various effects in interdependent collaboration and citation networks, see the [tutorial on analyzing the coevolution of collaboration and references to prior work](https://github.com/juergenlerner/eventnet/wiki/Coevolution-of-collaboration-and-references-to-prior-work-(tutorial)). 

Note that the actual data is provided by Aminer - here we just list scripts to preprocess and analyze this data with RHEM. A reference for the data is the following (also see the webpage given above).

Jie Tang, Jing Zhang, Limin Yao, Juanzi Li, Li Zhang, and Zhong Su. **ArnetMiner: Extraction and Mining of Academic Social Networks**. In _Proceedings of the Fourteenth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining (SIGKDD'2008)_. pp.990-998.

## Preprocess data for eventnet.hyper

In general, the structure of the input data for eventnet.hyper is explained in the [tutorial on analyzing the coevolution of collaboration and references to prior work](https://github.com/juergenlerner/eventnet/wiki/Coevolution-of-collaboration-and-references-to-prior-work-(tutorial)). The input data might be produced in a number of different ways and possibly starting from different original data. The auxiliary software on this page is provided for convenience, illustration, and reproducibility. Note that this software will (most likely) only work with the Aminer citation network version V14.

Download **DBLP-Citation-network V14** linked from [https://www.aminer.org/citation](https://www.aminer.org/citation). The precise format of the data might change from version to version so that to prepocess a different version of the data, the conversion script [`AminerJSON2CSVDocType.java`](https://github.com/juergenlerner/eventnet/raw/master/data/scientific_networks/aminer_2023/AminerJSON2CSVDocType.java) possibly has to be adapted. Extract the JSON file from the downloaded compressed file. Denote the extracted file by `dblp_v14.json`. Compile `AminerJSON2CSVDocType.java` which needs the libraries [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) and [JSON Simple](https://code.google.com/archive/p/json-simple/). 

To generate the input file for eventnet, considering only journal publications, execute the JSON to CSV conversion by calling (possibly adapt filenames and/or classpath)
```
java -Xmx250g AminerJSON2CSVDocType dblp_v14.json dblp_v14_preproc_Journal.csv Journal
```
The generated CSV file (e.g. `dblp_v14_preproc_Journal.csv`) serves as input file for eventnet.
