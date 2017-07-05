LexSemA
========

The lexSemA project is a toolkit for lexico-semantic language resources at several levels: 
    
    - lexsema-io: I/O opérations on corpora and lexical resources
    - lexsema-ontolex: An API to access ontolex lexical resources and more particularly DBNary
    - lexsema-translation: An API to access online machine translation systems
    - lexsema-ml: A set of machine learning tools useful for word sense disambiguation and the implementation of semantic similarity measures
    - lexsema-similarity: A semantic similarity library (both monolongual and multilingual)
    - lexsema-wsd: A word-sense disambiguation library implementing seval state of the art WSD algorithms.
    - lexsema-axalign: A word-sense alignement library and interlingual acception construction framework 
    - lexsema-utils: Various utility classes for the other subjprojects

The project was mainly developed in the GETALP NLP research group in Grenoble, France. 

#Installation

Lexsema is a multi-module maven project. You will need a Java 1.8 JDK as well as apache-maven 3.x installed.

In order to install it, you fist need to clone the repository and enter the lexsema directory. Then you can compile the project and retrieve all the dependencies by typing: 
```
mvn install 
```
 
#License
LexSemA is released under the terms of the GNU General Public License version 3.0

#Contributors

    - Andon Tchechmedjiev
    - Loïc Vial
    - Didier Schwab
    - Gilles Sérasset
