package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.Play;
import play.mvc.Controller;

/**
 * <p>
 * Class design to generate a full Book from a set of Textile Files (one per
 * chapter).
 * </p>
 * <p>
 * The list of chapters is discribed in the <code>chapters.txt</code> file: One
 * line = one chapter.
 * </p>
 * <p>
 * To insert blank page between too chapters, just insert a simple minus '-'
 * character on one line. some comments can be added into the
 * <code>chapters.txt</code> file, line of comment starting by a sharp '#'
 * character.
 * </p>
 * 
 * @author frederic
 * 
 */
public class Application extends Controller {
	private static String NL = System.getProperty("line.separator");

	/**
	 * Book Generator !
	 */
	public static void index() {
		String error = "";
		StringBuilder text = new StringBuilder();
		StringBuilder fullBook = new StringBuilder();
		String fullPage = "";
		Scanner scan1 = null;

		ArrayList<String> listChapters = new ArrayList<String>();
		ArrayList<String> listChaptersTitle = new ArrayList<String>();

		try {
			scan1 = new Scanner(new FileInputStream(Play.applicationPath
					+ "/pages/chapters/chapters.txt"), "UTF-8");
			while (scan1.hasNextLine()) {
				String chapterTitle = "";
				text = new StringBuilder();
				text.append(scan1.nextLine());
				
				String tswitch = text.toString();
				
				// Comment in file chapters.txt
				if (tswitch.startsWith("#")) {
					
					//DO Nothing !
				
				// Page break in book
				}else if (tswitch.equals("-")) {
					
					listChapters.add("<div style=\"page-break-after\"></div>");
				
				// Table of content
				}else if (tswitch.equals("{toc}")) {
				
					listChapters.add("{toc}");
					
				// Cover
				}else if (tswitch.startsWith("{cover")) {
					// TODO
					listChapters.add("<img src=\"\"/>");
					
				// Back Cover
				}else if (tswitch.startsWith("{backcover")) {
					// TODO
					listChapters.add("<img src=\"\"/>");

				// chapitre
				} else if(!tswitch.equals("")){
					
					String chapter = loadTextileFile(Play.applicationPath + "/pages/chapters/"
							+ text.toString()+ ".textile");
					
					if(chapter !=null && !chapter.equals("")){
						// Add a chapter to the list.
						chapterTitle = getTitle(chapter.toString());
						listChapters.add(convertToHtml(chapter));

						// retrieve Title from Textile.
						listChaptersTitle.add(chapterTitle);

					}
				}
			}

		} catch (Exception e) {
			error = "filenotfound: " + e.getMessage();
			render(error);
		} finally {
			if (scan1 != null)
				scan1.close();
		}

		// On le convertie en HTML
		renderTemplate("Application/index.html", listChapters,
				listChaptersTitle);

		// Rendering ODT template !
		// renderOdt(listChapters, listChaptersTitle);
	}

	/**
	 * Show the page corresponding to <code>chapter</code>.
	 * 
	 * @param chapterFile
	 *            String chapter to display.
	 */
	public static void page(String chapterFile) {
		File item = new File(Play.applicationPath + "/pages/chapters/"
				+ chapterFile.toString() + ".textile");
		StringBuilder chapter = new StringBuilder();

		if (item.exists()) {
			Scanner scan2;
			try {
				scan2 = new Scanner(new FileInputStream(Play.applicationPath
						+ "/pages/chapters/" + chapterFile.toString()
						+ ".textile"), "UTF-8");
				while (scan2.hasNextLine()) {
					chapter.append((scan2.nextLine().toString() + NL));
				}
				// fullBook.append(NL + chapter.toString() + NL + "<hr />" +
				// NL);

				String page = NL + convertToHtml(chapter.toString()) + NL
						+ "<hr />";
				render(page);
			} catch (FileNotFoundException e) {
				String error = "Error during preparation page : "
						+ e.getMessage();
				render(error);
			}

		}

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

	/**
	 * Convert input <code>text</code>formated in Textile markup, to standard
	 * HTML.
	 * 
	 * @param text
	 *            String Text to be converted to HTML.
	 * @return Html text resulting of text Textile markup language convertion.
	 */
	private static String convertToHtml(String text) {

		StringWriter writer = new StringWriter();

		// Création du générateur de document HTML
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);

		// Empêche la génération des balises html et body.
		builder.setEmitAsDocument(false);

		// Création du parser Textile
		MarkupParser parser = new MarkupParser(new TextileLanguage());
		parser.setBuilder(builder);
		parser.parse(text);
		return writer.toString();

	}

	/**
	 * Return String containting Textile markup file <code>filename</code>.
	 * 
	 * @param filename
	 *            String file name of the textile markup document to load.
	 * @return String containing all data from the file.
	 * @throws FileNotFoundException
	 */
	private static String loadTextileFile(String filename){
		StringBuilder chapter = new StringBuilder();
		Scanner scan2 = null;
		File item = new File(filename);

		if (item.exists()) {

			// start scanning all lines.
			try {
				scan2 = new Scanner(new FileInputStream(filename), "UTF-8");
				while (scan2.hasNextLine()) {
					chapter.append((scan2.nextLine().toString() + NL));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (scan2 != null){
					scan2.close();
				}
			}
		}
		return chapter.toString();
	}
}
