#!/bin/bash



#this reads the input file fow bwa to match with megares database
read -p	'enter full path to file for AMR database: ' amrdatabase
read -p 'enter full path to file for metagenomic database:' metadatabase

mkdir output_files
#Going into BWA Folder

cp $amrdatabase bwa-0.7.17/
cp $metadatabase bwa-0.7.17/
cd bwa-0.7.17/


#making the index for bwa
./bwa index $amrdatabase

#Running bwa mem
./bwa mem -a $amrdatabase $metadatabase > out2.sam

#Going to outer folder and copies the output sam to output files folder
cd ..
mkdir output_files/bwa_output
cp bwa-0.7.17/out2.sam output_files/bwa_output


# running the java utils to read the out.sam file

# ************ MARKER 1 *******************
java -jar AMRUtilsTest_new.jar  $amrdatabase "output_files/bwa_output/out2.sam"


mkdir output_files/bwa_java

# Copy into the output folder
pwd
cp "alignedReads.fasta" output_files/bwa_java
cp "partiallyOverlapAligned.fasta" output_files/bwa_java



#***************** MARKER 2 *****************
java -cp AMRUtilsTest_new.jar deBruijnGraph.dbg "output_files/bwa_java/partiallyOverlapAligned.fasta" "output_files/bwa_java/alignedReads.fasta"


mkdir output_files/java_utils_output

cp output_files/bwa_java/extendedReads.fasta output_files/java_utils_output/


java -cp AMRUtilsTest_new.jar succinctDeBruijnGraph.sdbg "output_files/bwa_java/partiallyOverlapAligned.fasta" "output_files/bwa_java/alignedReads.fasta"

cp output_files/bwa_java/succinctDBGDataStruct.txt output_files/java_utils_output/

