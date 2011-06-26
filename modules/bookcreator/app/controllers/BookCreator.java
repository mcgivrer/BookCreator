package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.vfs.*;

import helpers.ReferencesHelper;

import java.io.*;
import java.util.*;

/**
 * Display the Book pages according to url parsed from /@book
 * 
 * @author frederic
 * 
 */
public class BookCreator extends Controller {

	/**
	 * retrieve the default first page of the book.
	 * 
	 * @throws Exception
	 */
	public static void index() throws Exception {
		page("home", null);
	}

	/**
	 * Load the <code>id</code> page from the book location (
	 * <code>pages/chapters</code>).
	 * 
	 * @param id
	 *            String identifier for the page and title of the textile file.
	 * @param module
	 *            external module
	 * @throws Exception
	 */
	public static void page(String id, String module) throws Exception {
		File page = new File(Play.applicationPath, "pages/chapters/" + id
				+ ".textile");
		// if (module != null) {
		// page = new File(Play.modules.get(module).getRealFile(),
		// "pages/chapters/" + id + ".textile");
		// }
		if (!page.exists()) {
			notFound("Manual page for " + id + " not found");
		}
		String textile = IO.readContentAsString(page);
		String html = toHTML(textile);
		String title = getTitle(textile);

		List<String> modules = new ArrayList();
		List<String> apis = new ArrayList();
		if (id.equals("home") && module == null) {
			for (String key : Play.modules.keySet()) {
				VirtualFile mr = Play.modules.get(key);
				VirtualFile home = mr.child("pages/chapters/home.textile");
				if (home.exists()) {
					modules.add(key);
				}
				if (mr.child("pages/references/index.html").exists()) {
					apis.add(key);
				}
			}
		}

		render(id, html, title, modules, apis, module);
	}

	/**
	 * Construct references from <code>category</code>.
	 * @param category
	 */
	public static void reference(String category) {
		File[] sheetFiles = ReferencesHelper.getSheets(category);
		if (sheetFiles != null) {
			List<String> sheets = new ArrayList<String>();

			for (File file : sheetFiles) {
				sheets.add(toHTML(IO.readContentAsString(file)));
			}

			String title = ReferencesHelper.getCategoryTitle(category);
			Map<String, String> otherCategories = ReferencesHelper
					.listCategoriesAndTitles();

			render(title, otherCategories, sheets);
		}
		notFound("Cheat sheet directory not found");
	}

	/**
	 * Display the asked image <code>name</code> from <code>module</code>.
	 * 
	 * @param name
	 * @param module
	 */
	public static void image(String name, String module) {
		File image = new File(Play.frameworkPath, "pages/images/" + name
				+ ".png");
		// if (module != null) {
		// image = new File(Play.modules.get(module).getRealFile(),
		// "pages/images/" + name + ".png");
		// }
		if (!image.exists()) {
			notFound();
		}
		renderBinary(image);
	}

	/**
	 * Serve the <code>name</code> file from the <code>module</code>.
	 * 
	 * @param name
	 *            String name of the file to serve.
	 * @param module
	 *            String Module name from served file.
	 */
	public static void file(String name, String module) {
		File file = new File(Play.frameworkPath, "pages/files/" + name);
		if (module != null) {
			file = new File(Play.modules.get(module).getRealFile(),
					"pages/files/" + name);
		}
		if (!file.exists()) {
			notFound();
		}
		renderBinary(file);
	}

	/**
	 * Convert to HTML <code>textile</code> formated input string.
	 * 
	 * @param textile
	 *            String Textile markup formated text to convert to HTML.
	 * @return String HTML converted text.
	 */
	static String toHTML(String textile) {
		String html = new jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser(
				new jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage())
				.parseToHtml(textile);
		html = html.substring(html.indexOf("<body>") + 6,
				html.lastIndexOf("</body>"));
		return html;
	}

	/**
	 * extract title from "h1." first line of the textile file.
	 * 
	 * @param textile
	 *            String textile content for this chapter.
	 * @return String Title of the chapter.
	 */
	static String getTitle(String textile) {
		if (textile.length() == 0) {
			return "";
		}
		return textile.split("\n")[0].substring(3).trim();
	}

}
