package model;

/** Detailed data for a single journal, used in the scatter chart */
public class JournalScatter {
	private final String title, quartile, subjectArea, sjrIndex, citescore, hIndex;
	private final String totalDocs, totalDocs3y, totalRefs, totalCites3y, citableDocs3y, citesDoc2y, refsDoc;

	public JournalScatter(String title, String quartile, String subjectArea,
		String sjrIndex, String citescore, String hIndex,
		String totalDocs, String totalDocs3y, String totalRefs,
		String totalCites3y, String citableDocs3y, String citesDoc2y, String refsDoc) {
		this.title = title;
		this.quartile = quartile;
		this.subjectArea = subjectArea;
		this.sjrIndex = sjrIndex;
		this.citescore = citescore;
		this.hIndex = hIndex;
		this.totalDocs = totalDocs;
		this.totalDocs3y = totalDocs3y;
		this.totalRefs = totalRefs;
		this.totalCites3y = totalCites3y;
		this.citableDocs3y = citableDocs3y;
		this.citesDoc2y = citesDoc2y;
		this.refsDoc = refsDoc;
	}

	public String getTitle() { return title; }
	public String getQuartile() { return quartile; }
	public String getSubjectArea() { return subjectArea; }
	public String getSjrIndex() { return sjrIndex; }
	public String getCitescore() { return citescore; }
	public String getHIndex() { return hIndex; }
	public String getTotalDocs() { return totalDocs; }
	public String getTotalDocs3y() { return totalDocs3y; }
	public String getTotalRefs() { return totalRefs; }
	public String getTotalCites3y() { return totalCites3y; }
	public String getCitableDocs3y() { return citableDocs3y; }
	public String getCitesDoc2y() { return citesDoc2y; }
	public String getRefsDoc() { return refsDoc; }
}
