/**
 * 
 */
package kn.uni.eventnet.hyper.util.coauthor.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Juergen Lerner
 *
 */
public class AminerJSON2CSV {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length < 2) {
			System.out.println("usage: java kn.uni.eventnet.hyper.util.coauthor.io.AminerJSON2CSV <infilename> <outfilename>");
			System.exit(1);
		}
		
		JSONParser parser = new JSONParser();
		
		try {
			JSONArray papers = (JSONArray) parser.parse(new FileReader(args[0]));
			CSVFormat format = CSVFormat.DEFAULT.withHeader("year", "paper.id", "author.id", "n.citation").
					withDelimiter(',');
			CSVPrinter printer = new CSVPrinter(new FileWriter(args[1]), format);
			int count = 0;
			for(Object o : papers) {
				//if(count % 10000 == 0)
				//	System.out.println("processed paper number:" + count);
				JSONObject paper = (JSONObject) o;
				long paper_id = (long) paper.get("id");
				long year = (long) paper.get("year");
				JSONArray authors = (JSONArray) paper.get("authors");
				long n_citations = (long) paper.get("n_citation");
				if(authors != null) {
					for(int i = 0; i < authors.size(); ++i) {
						Object auth_obj = authors.get(i);
						if(auth_obj != null) {
							JSONObject author = (JSONObject) auth_obj;
							long auth_id = (long) author.get("id");
							printer.printRecord(year, paper_id, auth_id, n_citations);
						}
					}
				}
				++count;
			}
			printer.flush();
			printer.close();
			System.out.println("DONE! Processed " + count + " papers.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
