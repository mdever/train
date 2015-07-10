package traintracks;

public class Answer {

	private static Boolean verbose = Boolean.FALSE;
	
	public static void setVerbose(Boolean verbose) {
		Answer.verbose = verbose;
	}
	
	public static Boolean getVerbose() {
		return verbose;
	}
	
	private String baseAnswer;
	public String getBaseAnswer() {
		return baseAnswer;
	}

	public void setBaseAnswer(String baseAnswer) {
		this.baseAnswer = baseAnswer;
	}

	public String getVerboseAnswer() {
		return verboseAnswer;
	}

	public void setVerboseAnswer(String verboseAnswer) {
		this.verboseAnswer = verboseAnswer;
	}

	private String verboseAnswer;
	
	public String toString() {
		if (verbose && verboseAnswer != null) {
			return baseAnswer + "\n" + verboseAnswer + "\n----------";
		} else {
			return baseAnswer;
		}
	}
	
}
