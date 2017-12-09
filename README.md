# Project Title

Implementation of a computational pipeline for detection of AMR genes using Succinct de Bruijn Graph based seed and extend Algorithm

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. This project is designed for
linux machines only 

### Prerequisites

GCC Compiler should be installed

JAVA should be installed before running anything.
To check if JAVA is installed type java -version in command line and see if it returns the version. 
If not, link to install JAVA is given here:
https://www.java.com/en/download/help/download_options.xml

We have tested this program against the Human Metagenomic Data SRR532663	from the Human Metagenomic Project.
This dataset can be downloaded at: 
https://trace.ncbi.nlm.nih.gov/Traces/sra/?view=search_seq_name&exp=SRX173750&run=&m=search&s=seq

For the AMR Database, we have provided a copy of the MEGARes database, sourced from the following link:
https://megares.meglab.org/download/index.php

Please ensure that both the datasets are downloaded and their unzipped files are available in the main folder (Defined as "Extracted Folder" below)of the code and run the script against them. 



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
  
  2. Source Code Folder
      This contains the JAVA source code files that need to be compiled. Compilation needs to be done in the root folder. 


### Installing

1.To install the prerequisites go to extracted folder via command line
2. In command line run ./install_all.sh

This will compile the required tools and required libraries to run our program

## Running the Program

Before running the program ensure that the two input files are in the main folder.

The two files are 1. AMR DATABASE 2. METAGENOMIC DATABASE

To run the program run the ./sDBGAMR.sh

1. Input the AMR database file name *** Do not give full path, just the file name along with extension(.fastq , .fasta) e.g. amrdatabase.fasta
 
2. Input the Metagenomic database file name *** Do not give full path, just the file name along with extension(.fastq , .fasta) e.g. metagenomic.fasta 

3. The script takes care of everything else. All the output files are in the output_files folder



### Optionsl arguments for JAVA programs
1. MARKER 1 : Argument number 3 :Preferred Partial Overlap length( default = 5)
2. MARKER 2 : Argument number 3 :Kmer size( default = 5 ), Argument number 4 :Extension Length( default =10)


## Authors 

* **Pankhuri Mehndiratta**  
* **Parth Rampal** 
* **Sarthak Chauhan** 
* **Suvadeep Chaudhuri** 





## Acknowledgments

* Burrows Wheeler Aligner<http://bio-bwa.sourceforge.net>: Li H, Durbin R. Fast and accurate short read alignment with Burrows–Wheeler transform. Bioinformatics. 2009;25(14):1754-1760. doi:10.1093/bioinformatics/btp324. The Burrows Wheeler Aligner Code has been provided as is from their sourcefourge page and no changes have been made to it.
* MEGARes: Lakin S. M., Dean C., Noyes N. R., Dettenwanger A., Ross A. S., Doster E., et al. (2017). MEGARes: an antimicrobial resistance database for high throughput sequencing. Nucleic Acids Res.
