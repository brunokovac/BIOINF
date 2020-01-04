package hr.fer.bioinf.graph;

public class Edge {
	private String querySequenceName;
	private int queryStart;
	private int queryEnd;
	private char relativeStrand;
	private String targetSequenceName;
	private int targetStart; // on original strand
	private int targetEnd; // on original strand
	private int numberOfResidueMatches;
	private int alignmentBlockLength;

	private int queryOverhang;
	private int targetOverhang;
	private int queryExtension;
	private int targetExtension;

	public Edge(String querySequenceName, int queryStart, int queryEnd, char relativeStrand, String targetSequenceName,
			int targetStart, int targetEnd, int numberOfResidueMatches, int alignmentBlockLength) {
		this.querySequenceName = querySequenceName;
		this.queryStart = queryStart;
		this.queryEnd = queryEnd;
		this.relativeStrand = relativeStrand;
		this.targetSequenceName = targetSequenceName;
		this.targetStart = targetStart;
		this.targetEnd = targetEnd;
		this.numberOfResidueMatches = numberOfResidueMatches;
		this.alignmentBlockLength = alignmentBlockLength;
	}

	public String getQuerySequenceName() {
		return querySequenceName;
	}

	public void setQuerySequenceName(String querySequenceName) {
		this.querySequenceName = querySequenceName;
	}

	public int getQueryStart() {
		return queryStart;
	}

	public void setQueryStart(int queryStart) {
		this.queryStart = queryStart;
	}

	public int getQueryEnd() {
		return queryEnd;
	}

	public void setQueryEnd(int queryEnd) {
		this.queryEnd = queryEnd;
	}

	public char getRelativeStrand() {
		return relativeStrand;
	}

	public void setRelativeStrand(char relativeStrand) {
		this.relativeStrand = relativeStrand;
	}

	public String getTargetSequenceName() {
		return targetSequenceName;
	}

	public void setTargetSequenceName(String targetSequenceName) {
		this.targetSequenceName = targetSequenceName;
	}

	public int getTargetStart() {
		return targetStart;
	}

	public void setTargetStart(int targetStart) {
		this.targetStart = targetStart;
	}

	public int getTargetEnd() {
		return targetEnd;
	}

	public void setTargetEnd(int targetEnd) {
		this.targetEnd = targetEnd;
	}

	public int getNumberOfResidueMatches() {
		return numberOfResidueMatches;
	}

	public void setNumberOfResidueMatches(int numberOfResidueMatches) {
		this.numberOfResidueMatches = numberOfResidueMatches;
	}

	public int getAlignmentBlockLength() {
		return alignmentBlockLength;
	}

	public void setAlignmentBlockLength(int alignmentBlockLength) {
		this.alignmentBlockLength = alignmentBlockLength;
	}

	public int getQueryOverhang() {
		return queryOverhang;
	}

	public void setQueryOverhang(int queryOverhang) {
		this.queryOverhang = queryOverhang;
	}

	public int getTargetOverhang() {
		return targetOverhang;
	}

	public void setTargetOverhang(int targetOverhang) {
		this.targetOverhang = targetOverhang;
	}

	public int getQueryExtension() {
		return queryExtension;
	}

	public void setQueryExtension(int queryExtension) {
		this.queryExtension = queryExtension;
	}

	public int getTargetExtension() {
		return targetExtension;
	}

	public void setTargetExtension(int targetExtension) {
		this.targetExtension = targetExtension;
	}

	private double sequenceIdentity = -1;

	public double getSequenceIdentity() {
		if (sequenceIdentity != -1) {
			return sequenceIdentity;
		}

		int queryOverlap = queryEnd - queryStart;
		int targetOverlap = targetEnd - targetStart;

		this.sequenceIdentity = (double) numberOfResidueMatches / Math.max(queryOverlap, targetOverlap);
		return sequenceIdentity;
	}

	private double overlapScore = -1;

	public double getOverlapScore() {
		if (overlapScore != -1) {
			return overlapScore;
		}

		int queryOverlap = queryEnd - queryStart;
		int targetOverlap = targetEnd - targetStart;

		double sequenceIdentity = getSequenceIdentity();

		overlapScore = (queryOverlap + targetOverlap) * sequenceIdentity / 2;
		return overlapScore;
	}

	private double extensionScore = -1;

	public double getExtensionScore() {
		if (extensionScore != -1) {
			return extensionScore;
		}

		double overlapScore = getOverlapScore();
		extensionScore = overlapScore + targetExtension / 2.0 - (queryOverhang + targetOverhang) / 2.0;
		return extensionScore;
	}

	@Override
	public String toString() {
		return "Edge [querySequenceName=" + querySequenceName + ", queryStart=" + queryStart + ", queryEnd=" + queryEnd
				+ ", relativeStrand=" + relativeStrand + ", targetSequenceName=" + targetSequenceName + ", targetStart="
				+ targetStart + ", targetEnd=" + targetEnd + ", numberOfResidueMatches=" + numberOfResidueMatches
				+ ", alignmentBlockLength=" + alignmentBlockLength + ", queryOverhang=" + queryOverhang
				+ ", targetOverhang=" + targetOverhang + ", queryExtension=" + queryExtension + ", targetExtension="
				+ targetExtension + ", sequenceIdentity=" + sequenceIdentity + ", overlapScore=" + overlapScore
				+ ", extensionScore=" + extensionScore + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alignmentBlockLength;
		result = prime * result + numberOfResidueMatches;
		result = prime * result + queryEnd;
		result = prime * result + ((querySequenceName == null) ? 0 : querySequenceName.hashCode());
		result = prime * result + queryStart;
		result = prime * result + relativeStrand;
		result = prime * result + targetEnd;
		result = prime * result + ((targetSequenceName == null) ? 0 : targetSequenceName.hashCode());
		result = prime * result + targetStart;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (alignmentBlockLength != other.alignmentBlockLength)
			return false;
		if (numberOfResidueMatches != other.numberOfResidueMatches)
			return false;
		if (queryEnd != other.queryEnd)
			return false;
		if (querySequenceName == null) {
			if (other.querySequenceName != null)
				return false;
		} else if (!querySequenceName.equals(other.querySequenceName))
			return false;
		if (queryStart != other.queryStart)
			return false;
		if (relativeStrand != other.relativeStrand)
			return false;
		if (targetEnd != other.targetEnd)
			return false;
		if (targetSequenceName == null) {
			if (other.targetSequenceName != null)
				return false;
		} else if (!targetSequenceName.equals(other.targetSequenceName))
			return false;
		if (targetStart != other.targetStart)
			return false;
		return true;
	}

}
