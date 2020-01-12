PREFIX=$1

../../../minimap2/minimap2 -x ava-pb ${PREFIX}_reads.fasta ${PREFIX}_reads.fasta > ${PREFIX}_reads_overlaps.paf
../../../minimap2/minimap2 -x ava-pb ${PREFIX}_reads.fasta ${PREFIX}_contigs.fasta > ${PREFIX}_contigs_overlaps.paf
