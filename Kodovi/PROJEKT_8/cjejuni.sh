java -cp bin/ hr.fer.bioinf.Main \
  --sequence-identity-cutoff=0.7 \
  --contigs-path=../../../tests/cjejuni_contigs.fasta \
  --reads-path=../../../tests/cjejuni_reads.fasta \
  --contigs-reads-overlaps-path=../../../tests/cjejuni_contig_overlaps.paf \
  --reads-overlaps-path=../../../tests/cjejuni_read_overlaps.paf
