package fr.proline.zero.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegExUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(RegExUtil.class);
	
	public static String parseReleaseVersion(final String fastaFileName, final String releaseRegEx) {
		assert (fastaFileName != null) : "parseReleaseVersion() fastaFileName is null";
		assert (releaseRegEx != null) : "parseReleaseVersion() releaseRegEx is null";
				
		Pattern releasePattern = Pattern.compile(releaseRegEx, Pattern.CASE_INSENSITIVE);			

		String result = null;
		if(releasePattern != null) {
    		final Matcher matcher = releasePattern.matcher(fastaFileName);
    
    		if (matcher.find()) {
    
    			if (matcher.groupCount() < 1) {
    				throw new IllegalArgumentException("Invalid Release version Regex");
    			}
    
    			result = matcher.group(1).trim();
    		} else {
    			LOG.warn("Cannot parse release version for fastaFileName [{}] with \"{}\" Regex", fastaFileName,
    				releasePattern.pattern());
    		}
		}
		return result;
	}
	
	/**
	 * Use specified regex to extract searched text from source text 
	 * 
	 * @param sourceText : text to get searched text from
	 * @param releaseRegEx : Regex to be used to get text from sourceText
	 * @return extracted text or null if not found
	 */
	public static String getMatchingString(final String sourceText, final String searchStrRegEx) {
		if(sourceText == null || searchStrRegEx == null)
			return null;
				
		Pattern textPattern = Pattern.compile(searchStrRegEx, Pattern.CASE_INSENSITIVE);			

		String result = null;
		if(textPattern != null) {
    		final Matcher matcher = textPattern.matcher(sourceText);
    
    		if (matcher.find()) {
    
    			if (matcher.groupCount() >= 1) 
    				result = matcher.group(1).trim();
    		} 
		}
		return result;
	}
}
