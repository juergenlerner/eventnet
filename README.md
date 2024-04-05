# eventnet
Event network analyzer (eventnet): statistical analysis of networks of relational events.

A **relational event** represents time-stamped interaction ( _who does when what to whom_). Examples include a person sending an message to another person, a Wikipedia user editing a Wikipedia article, a customer buying a certain product, or a country signing an agreement with another country. A **relational hyperevent** represents time-stamped interaction among varying and potentially large numbers of "nodes". Examples include meeting events, team work (e.g., coauthoring of papers), multi-cast or "one-to-many" communication such as email, or papers citing lists of other papers in their references. 

Relational (hyper) event models **R(H)EM** seek to model sequences of such interaction events to uncover or test factors explaining that some nodes interact at a higher rate than others. While the core statistical models are well-established in survival or event-history analysis (e.g., Cox regression) and are implemented in numerous free and commercial statistical software packages, typical explanatory variables of REM and RHEM are novel, network-specific, and sometimes conceptually and computationally demanding to understand and implement. This gap is filled by the eventnet software.

See the tutorials in the [eventnet wiki](https://github.com/juergenlerner/eventnet/wiki) to get started.

## News

**Consider attending the following workshop on RHEM** (one-day / 6 hours) at the INSNA Sunbelt Conference
* [**REM beyond dyads: relational hyperevent modeling with eventnet (beginners and advanced)**](https://sunbelt2024.com/), within the _INSNA Sunbelt Conference 2024_, Edinburgh. 24-30 June 2024. (The RHEM workshop is on Tuesday, June 25.)

**+++ Release of eventnet one +++** 

Eventnet one (Version 1.0 or later) can be downloaded as [eventnet-1.0.jar](https://github.com/juergenlerner/eventnet/raw/master/jars/eventnet-1.0.jar) and comes with three important changes.
* The functionality for dyadic relational event models (REM) and for relational hyperevent models (RHEM) is now provided in a single JAR file (`eventnet-1.0.jar` or later).
* RHEM can now also be specified purely in the graphical user interface (GUI).
* RHEM effects have been completely reorganized. The number of different core types of RHEM statistics could be reduced but a more efficent use of the arguments of statistics actually provides a much larger variation of possible RHEM effects than in prior versions. Note that because of this reorganization, configuration files from versions prior to 1.0 will most likely no longer work with eventnet one. (Note, however, that the JAR files of prior versions are still available at [https://github.com/juergenlerner/eventnet/tree/master/jars/old_versions-0.x](https://github.com/juergenlerner/eventnet/tree/master/jars/old_versions-0.x).)

RHEM effects available in Version 1.0 or later are exhaustively listed and discussed in the [Reference guide on RHEM effects](https://github.com/juergenlerner/eventnet/wiki/RHEM-effects-(reference-guide)). However, users new to eventnet are recommended to first have a look at the more basic tutorials linked from the [eventnet wiki](https://github.com/juergenlerner/eventnet/wiki/).

## Download and use (eventnet for REM and RHEM)
To use eventnet, download the file [eventnet-1.0.jar](https://github.com/juergenlerner/eventnet/raw/master/jars/eventnet-1.0.jar) and start the program by either of:
* double-clicking on the JAR file opens the eventnet graphical user interface (GUI)
* typing the command `java -jar eventnet-x.y.jar` opens the eventnet GUI
* typing the command `java -jar eventnet-x.y.jar <configuration_filename.xml>`, where `<configuration_filename.xml>` is the name of a file containing a fully specified eventnet configuration, executes this configuration without opening the GUI.

**See the [eventnet wiki](https://github.com/juergenlerner/eventnet/wiki) for online tutorials.**

Eventnet is written in java and needs the <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">java runtime environment (JRE)</a>, Java 8 (JRE version 1.8) or higher.

**Source code** (`.java` files) are in [eventnet-1.0-src.jar](https://github.com/juergenlerner/eventnet/raw/master/jars/eventnet-1.0-src.jar).

Eventnet is distributed under the [GNU General Public License v3.0](https://github.com/juergenlerner/eventnet/blob/master/LICENSE).

**Citation:** 
* Lerner and Lomi (2020). [**Reliability of relational event model estimates under sampling: how to fit a relational event model to 360 million dyadic events.**](https://doi.org/10.1017/nws.2019.57) _Network Science_, 8(1):97-135. ([DOI: 10.1017/nws.2019.57](https://doi.org/10.1017/nws.2019.57))
* Lerner and Lomi (2023). [**Relational hyperevent models for polyadic interaction networks**](https://doi.org/10.1093/jrsssa/qnac012). _Journal of the Royal Statistical Society: Series A_. ([https://doi.org/10.1093/jrsssa/qnac012](https://doi.org/10.1093/jrsssa/qnac012))

## Tutorials and training 
**The usual place to get started are the online tutorials in the [eventnet wiki](https://github.com/juergenlerner/eventnet/wiki).**

Moreover, training **workshops or courses** introducing eventnet are offered at the following conferences or summerschools.
* [**REM beyond dyads: relational hyperevent modeling with eventnet (beginners and advanced)**](https://sunbelt2024.com/), within the _INSNA Sunbelt Conference 2024_, Edinburgh. 24-30 June 2024. (The RHEM workshop is on Tuesday, June 25.)

**Past training events** 
* _International "Winter Course," Theory, Methods and Applications of Personal Networks_, UAB Egolab, Barcelona, Spain. February 5-9, 2024.
* _REM beyond dyads: relational hyperevent modeling with eventnet (beginners and advanced)_, within the _European Conference on Social Networks (EUSN 2023)_, Ljubljana, Slovenia. 4-8 September 2023.
* _Relational Event Models (REMs) for the Analysis of Social Networks: A Hands-on Introduction_, part of the _NUSC Summer School in Network and Data Science_, Greenwich, UK. 19th - 23rd June 2023.
* _International "Winter Course," Theory, Methods and Applications of Personal Networks_, Barcelona, Spain. February 6-10, 2023.
* _REM beyond dyads: relational hyperevent modeling with eventnet_, within the _European Conference on Social Networks (EUSN 2022)_, Greenwich, UK. 12-16 September 2022.
* _Relational Event Models (REMs) for the Analysis of Social Networks: A Hands-on Introduction_, part of the _NUSC Summer School in Network and Data Science_, Greenwich, UK. Mon 20th - Sat 25th June 2022.
* _International "Winter Course," Theory, Methods and Applications of Personal Networks_, Barcelona, Spain. February 7-11, 2022.
* _European Conference on Social Networks_, Zurich, Switzerland. September 12, 2019.
* _Sunbelt Social Networks Conference_, Montreal, Canada. June 19, 2019.
* _Relational event models for the analysis of social networks_, University of Exeter, Business School. June 3-4, 2019.

## References
There is a [commented literature list](https://github.com/juergenlerner/eventnet/wiki/References-(list)) of own work on relational event models (REM) or relational hyperevent models (RHEM), using the eventnet software or (published or unpublished) predecessor software in its empirical analysis. The listed papers are expected to complement the eventnet tutorials in providing more formal details, a better embedding into related work, and a more thorough discussion of objectives and contributions. They may also point to further illustrating use cases of REM and RHEM.

## Contact
[J&uuml;rgen Lerner](https://github.com/juergenlerner) [(juergen.lerner@gmail.com)](mailto:juergen.lerner@gmail.com)

## Funding
We acknowledge financial support from [Deutsche Forschungsgemeinschaft (DFG)](http://www.dfg.de/en/) under Project No. **321869138**. From 2017 to 2020: "_Statistical analysis of structural balance in signed social networks_" (LE 2237/2-1) and from 2021 to 2025: "_Statistical analysis of time-stamped multi-actor events in social networks_" (LE 2237/2-2).
