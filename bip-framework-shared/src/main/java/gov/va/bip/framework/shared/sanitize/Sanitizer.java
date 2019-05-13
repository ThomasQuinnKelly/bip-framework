package gov.va.bip.framework.shared.sanitize;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Sanitizer {

	/*
	 * private constructor
	 */
	private Sanitizer() {

	}

	/**
	 * Removes invalid characters from a path (optionally with file name at the end of the path).
	 * <p>
	 * Recognized path separator characters are {@code .} (dot), {@code :} (colon), {@code \} (back-slash),
	 * {@code /} (forward-slash).
	 * <p>
	 * The list of invalid characters for all file names is based on Windows filename rules,
	 * as it is the most restrictive.
	 *
	 * @param filePath
	 * @return a potentially modified rendering of the path (or null if path is null)
	 */
	public static String safePath(final String path) {
		if (path == null) {
			return null;
		}

		// characters expressed as integer equivalents
		final int[] recognizedSeparators = { 46, 58, 92, 47 };
		Arrays.sort(recognizedSeparators);

		StringBuilder cleanPath = new StringBuilder();
		try {
			StringBuilder part = new StringBuilder();
			for (int i = 0; i < path.length(); i++) {
				final int c = path.charAt(i);
				if (Arrays.binarySearch(recognizedSeparators, c) < 0) {
					// this char is not a separator, add it to the part
					part.append((char) c);
				} else {
					// this char is a separator, store the cleaned part and the separator
					cleanPath.append(safeFilename(part.toString())).append((char) c);
					// reset the part to start the next segment of the path
					part = new StringBuilder();
				}
			}
			if (part.length() > 0) {
				cleanPath.append(safeFilename(part.toString()));
			}
		} catch (Exception e) {
			throw new SanitizerException("path information", e);
		}

		return cleanPath.toString();
	}

	/**
	 * Removes invalid characters from a file name.
	 * <p>
	 * <b>Note: this method is for file names only.</b> For paths (plus optional file name)
	 * use {@link #safePath(String)}.
	 * <p>
	 * The list of invalid characters for all file names is based on Windows filename rules,
	 * as it is the most restrictive. Disallowed characters include:
	 * <ul>
	 * <li>any ASCII code less than 32 (up to but not including the space character): {@code 0x0 - 0x1F}
	 * <li>greater-than and less-than angle brackets: {@code < >}
	 * <li>pipe (or bar): {@code |}
	 * <li>asterisk (star): {@code *}
	 * <li>colon: {@code :}
	 * <li>question mark: {@code ?}
	 * <li>forward-slash: {@code /}
	 * <li>back-slash: {@code \}
	 * </ul>
	 *
	 * @param filename the file name to clean up
	 * @return String the cleaned file name
	 */
	public static String safeFilename(final String filename) {
		if (filename == null) {
			return null;
		}

		// characters expressed as integer equivalents
		final int[] illegalChars = { 34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47 };
		Arrays.sort(illegalChars);

		StringBuilder cleanFilename = new StringBuilder();
		try {
			for (int i = 0; i < filename.length(); i++) {
				final int c = filename.charAt(i);
				if (Arrays.binarySearch(illegalChars, c) < 0) {
					cleanFilename.append((char) c);
				}
			}
		} catch (Exception e) {
			throw new SanitizerException("filename", e);
		}

		return cleanFilename.toString();
	}

	/**
	 * Removes all the potentially malicious characters from a string,
	 * including:
	 * <ul>
	 * <li>zero value bytes: {@code 0x0}
	 * <li>script tags and attributes: e.g. {@code <script> src eval} etc.
	 * </ul>
	 *
	 * @param string the raw string
	 * @return the sanitized string
	 */
	public static String stripXss(final String string) {
		if (string == null) {
			return null;
		}

		String cleanValue = null;
		try {
			cleanValue = Normalizer.normalize(string, Normalizer.Form.NFD);

			// Avoid null characters
			cleanValue = cleanValue.replaceAll("\0", "");

			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid anything in a src='...' type of expression
			scriptPattern =
					Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
							Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			scriptPattern =
					Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
							Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern =
					Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");
		} catch (Exception e) {
			throw new SanitizerException("input characters", e);
		}

		return cleanValue;
	}
}
