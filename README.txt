Group member: Jiayi He, Yufei Zhao

NetID: jhe36, yzhao87

Class: 2021_Spring_CSC_242

ReadMe File for Project3

Files:
1. We write and change the codes in the following classes: Assignment.java ; Bayesian Network.java ; CPT.java ; Distribution.java ; BIF2XMLBIF.java; RandomVariable.java ;XMLBIFParser.java ;XMLBIFPrinter.java; MyBNInferencer.java; MyBNApproxInferencer.java 
2. The rest of the codes we used are provided in the project materials.
3. The algorithms we applied in different classes:
-MyBNInferencer.java  --> exact inference algorithm
-MyBNApproxInferencer.java   --> reject sampling algorithm
-MyBNApproxInferencer.java  --> likelihood sampling algorithm
-MyBNApproxInferencer.java --> Gibbs sampling algorithm
4. Our 242P3 folder contains 3 folder: bin, bn, src. Folder src contains our two main java files MyBNInferencer.java and MyBNApproxInferencer.java. Folder bn contains the provided files. 

How to run: 
Run the program in the terminal. 
First we need to compile using:javac MyBNInferencer.java && javac MyBNApproxInferencer.java
The followings are examples for different algorithms:
1.Exact inference algorithm:
-java MyBNInferencer [filepath]  [query variable] [evidence variables followed by assignment]
Example: java MyBNInferencer aima-alarm.xml B J true M true
2.Reject sampling:
-java MYBNIApproxInferencer[SampleSize] [filepath] [query variable] [evidence variables followed by assignment]
Example: java MyBNIApproxInferencer 10000 aima-alarm.xml B J true M true
3.Likelihood weighting
-java MYBNIApproxInferencer[SampleSize] [filepath] [query variable] [evidence variables followed by assignment]
Example: java MYBNIApproxInferencer 1000 aima-wet-grass.xml R S true
4. Gibbs Sampling Inference:
-java MYBNIApproxInferencer[SampleSize] [filepath] [query variable] [evidence variables followed by assignment]
Example: java java MYBNIApproxInferencer 1000 aima-wet-grass.xml R S true

Here is the example we run in our group member Yufei's terminal (Yufei puts her project folder 242P3 on her Desktop):
Last login: Sat Apr  3 23:09:06 on ttys000
RainydeMacBook-Pro:~ rainyzhao$ cd /Users/rainyzhao/Desktop/242P3/src
RainydeMacBook-Pro:src rainyzhao$ javac MyBNInferencer.java && javac MyBNApproxInferencer.java
RainydeMacBook-Pro:src rainyzhao$ java MyBNInferencer /Users/rainyzhao/Desktop/242P3/bn/examples/aima-alarm.xml B J true M true
{true=0.2841718353643929, false=0.7158281646356071}
RainydeMacBook-Pro:src rainyzhao$ java MyBNApproxInferencer 1000 /Users/rainyzhao/Desktop/242P3/bn/examples/aima-alarm.xml B J true M true
Rejection Sampling: 
{true=0.25, false=0.75}
Likelihood Weighting:
{true=0.7912916056102155, false=0.2087083943897845}
Gibbs Sampling:
{true=0.299, false=0.701}
RainydeMacBook-Pro:src rainyzhao$

Ps (very important, please read carefully):
1. Since all three algorithms (1. Rejection sampling 2. Likelihood weighting 3. Gibbs sampling) are implemented in MYBNIApproxInferencer class (same class), you can just run MYBNIApproxInferencer to get all results.
2. There might be API warning appears. It does not affect the running progress and result at all. 


PLEASE FOLLOW THE INSTRUCTION CAREFULLY

