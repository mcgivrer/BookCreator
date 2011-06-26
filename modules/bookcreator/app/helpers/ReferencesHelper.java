package helpers;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import play.Play;

/**
 * Helpers for the References pages.
 * 
 * @author frederic
 * 
 */
public class ReferencesHelper {

	/**
	 * Default path for the cheat-Sheets.
	 */
	private static final File cheatSheetBaseDir = new File(Play.frameworkPath,
			"book/references");

	/**
	 * Retrieve all Cheats-Sheets from the <code>category</code>.
	 * 
	 * @param category
	 *            String catégroy to parse.
	 * @return
	 */
	public static File[] getSheets(String category) {
		File cheatSheetDir = new File(cheatSheetBaseDir, category);

		if (cheatSheetDir.exists() && cheatSheetDir.isDirectory()) {
			File[] sheetFiles = cheatSheetDir.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.isFile()
							&& pathname.getName().endsWith(".textile");
				}
			});

			// first letters of file name before "-" serves as sort index
			Arrays.sort(sheetFiles, new Comparator<File>() {

				public int compare(File f1, File f2) {

					String o1 = f1.getName();
					String o2 = f2.getName();

					if (o1.contains("-") && o2.contains("-")) {
						return o1.substring(0, o1.indexOf("-")).compareTo(
								o2.substring(0, o1.indexOf("-")));
					} else {
						return o1.compareTo(o2);
					}
				}
			});

			return sheetFiles;
		}

		return null;
	}

	/**
	 * Retrieve Category title from its CamelCasing words format.
	 * 
	 * @param category
	 *            String category to extract from.
	 * @return
	 */
	public static String getCategoryTitle(String category) {
		// split camelCaseWord into separate words
		String[] parts = category.trim().split("(?<!^)(?=[A-Z])");
		StringBuilder title = new StringBuilder();

		// capitalize first char of each word
		for (String part : parts) {
			if (part.length() > 0) {
				title.append(Character.toUpperCase(part.charAt(0)));

				if (part.length() > 1) {
					title.append(part.substring(1));
				}
				title.append(" ");
			}
		}

		return title.toString().trim();
	}

	/**
	 * Retrieve from cheatSHeetBaseDir all of the CheatSheets.
	 * 
	 * @return Map<String, String> containing list of Cheat Cheets.
	 */
	public static Map<String, String> listCategoriesAndTitles() {
		File[] categories = cheatSheetBaseDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		Arrays.sort(categories);

		Map<String, String> categoriesAndTitles = new LinkedHashMap<String, String>();

		for (File category : categories) {
			categoriesAndTitles.put(category.getName(),
					getCategoryTitle(category.getName()));
		}

		return categoriesAndTitles;
	}
}
