PREFIX=$1

java -cp bin/ hr.fer.bioinf.Main \
  --contigs-path=${PREFIX}_contigs.fasta \
  --reads-path=${PREFIX}_reads.fasta \
  --contigs-reads-overlaps-path=${PREFIX}_contigs_overlaps.paf \
  --reads-overlaps-path=${PREFIX}_reads_overlaps.paf \
  ${@:2:99}
