# Project Title

Implementation of a computational pipeline for detection of AMR genes using Succinct de Bruijn Graph based seed and extend Algorithm

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. This project is designed for
linux machines only 

### Prerequisites

We have tested his program has been designed against MegaRes Database. To test against the same. 
MegaRes database is downloaded along with the source code.

The metagenomic database to test against the MegaRes database is available here
https://trace.ncbi.nlm.nih.gov/Traces/sra/?view=search_seq_name&exp=SRX173750&run=&m=search&s=seq

Download the two databases and paste their unzipped files into the main folder and run the script against them. 

JAVA should be installed before running anything.
Link to install JAVA is given here:
https://www.java.com/en/download/help/download_options.xml
To check if JAVA is installed type java -version in command line and see if it returns the version.

GCC Compiler should be installed

### Directory structure

1.Once you have extracted the download folder the structure will follow as follows
  1Extracted Folder
  	1a. BWA Folder
  	1b. Java Jar Files
  	1c. Installation Script
  	1d. Main Program Script
  	1e. Output_files -> This directory is created after 	execution of program
  		1e1. bwa_output -> Output from bwa
  		1e2. bwa_java -> Output from Java Utility 1
  		1e3. bwa_utils_output -> Contains final results
  
  2. BDBA Folder
      This contains the JAVA source code files that need to be compiled. Compilation needs to be done in the root folder. 


### Installing

1.To install the prerequisites go to extracted folder via command line
2. In command line run ./install_all.sh

This will compile the required tools and required libraries to run our program

## Running the Program

Before running the program paste the two input files in the main folder.

The two files are 1. AMR DATABASE 2. METAGENOMIC DATABASE

To run the program run the ./script_final_v1.sh

1. Input the AMR database file name *** Do not give full path, just the file name along with extension(.fastq , .fasta) e.g. amrdatabase.fasta
 
2. Input the Metagenomic database file name *** Do not give full path, just the file name along with extension(.fastq , .fasta) e.g. metagenomic.fasta 

3. The script takes care of everything else. All the output files are in the output_files filder



### Optionsl arguments for JAVA programs
1. MARKER 1 : Argument number 3 :Preferred Partial Overlap length( default = 5)
2. MARKER 2 : Argument number 3 :Kmer size( default = 5 ), Argument number 4 :Extension Length( default =10)


## Authors

* **Pankhuri Mehndiratta**  
* **Parth Rampal** 
* **Sarthak Chauhan** 
* **Suvadeep Chaudhuri** 





## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* Burrows Wheeler Aligner<http://bio-bwa.sourceforge.net>
* etc

