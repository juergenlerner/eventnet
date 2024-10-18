import java.util.*;

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
public class AminerJSON2CSVDocType {

    public static final String author_str = "author";
    public static final String paper_ref_paper = "paper.ref.paper";
    public static final String author_ref_paper = "author.ref.paper";
    public static final String author_ref_author = "author.ref.author";
    
    /**
     * @param args
     */
    public static void main(String[] args) {

	if(args.length < 3) {
	    System.out.println("Preprocessing of the Aminer citation network dataset V14 (https://www.aminer.org/citation) \n into a CSV file that can be processed with eventnet, filtering the type of the publications: 'Journal' or 'Conference'.");
	    System.out.println("Note that this proprocessing will most likely not work with any other version than V14, without adapting to different file formats and/or field names.");
	    System.out.println("usage: java AminerJSON2CSVDocType <infilename> <outfilename> {Journal|Conference}");
	    System.exit(1);
	}

	String docType = args[2];
		
	JSONParser parser = new JSONParser();
		
	try {
	    // maps paper id to the respective event
	    HashMap<String,Event> events = new HashMap<String,Event>();
	    JSONArray papers = (JSONArray) parser.parse(new FileReader(args[0]));
	    int count = 0;
	    for(Object o : papers) {
		if(count % 10000 == 0)
			System.out.println("processed paper number:" + count);
		JSONObject paper = (JSONObject) o;
		String paper_id = (String) paper.get("id");
		String paper_doc_type = (String) paper.get("doc_type");
		int year = (int) ((long) paper.get("year"));
		JSONArray authors = (JSONArray) paper.get("authors");
		HashSet<String> authorSet = new HashSet<String>();
		if(authors != null) {
		    for(int i = 0; i < authors.size(); ++i) {
			Object auth_obj = authors.get(i);
			if(auth_obj != null) {
			    JSONObject author = (JSONObject) auth_obj;
			    String auth_id = (String) author.get("id");
			    if(auth_id.length() > 0)
				authorSet.add(auth_id);
			}
		    }
		}
		JSONArray references = (JSONArray) paper.get("references");
		HashSet<String> refSet = new HashSet<String>();
		if(references != null) {
		    for(int i = 0; i < references.size(); ++i) {
			Object ref_obj = references.get(i);
			if(ref_obj != null) {
			    String ref_id = (String) ref_obj;
			    if(ref_id.length() > 0)
				refSet.add(ref_id);
			}
		    }
		}
		if(paper_id.length() > 0 && year > 0 && docType.equals(paper_doc_type))
		    events.put(paper_id,new Event(year, paper_id, authorSet, refSet));
		++count;
	    }
	    System.out.println("DONE! Processed " + count + " papers.");
	    System.out.println("Found " + events.size() + " events.");

	    //sort
	    Event[] ev_array = events.values().toArray(new Event[0]);
	    Arrays.sort(ev_array);

	    // write
	    CSVFormat format = CSVFormat.DEFAULT.withHeader("Year","EventID","Source","Target","Type").
		withDelimiter(',');
	    CSVPrinter printer = new CSVPrinter(new FileWriter(args[1]), format);
	    // four event types: "author", "paper.ref.paper", "author.ref.paper", "author.ref.author."
	    // EventID is event type concatenated with publication id
	    // for "author" events: source is author, target is paper id
	    // for "paper.ref.paper" events: source is citing paper and target is citation (item in reference list)
	    // for "author.ref.paper" events: source is list of citing authors and target is list of references (items in reference list)
	    // for "author.ref.author" events: source is list of citing authors and target is list of all authors of all references 
	    for(int i = 0; i < ev_array.length; ++i){
		Event e = ev_array[i];
		String year_str = Integer.valueOf(e.year).toString();
		String paper_id = e.id;
		String event_id_auth = e.id + ":" + author_str;
		String event_id_p_ref = e.id + ":" + paper_ref_paper;
		String event_id_a_ref = e.id + ":" + author_ref_paper;
		String event_id_a_a_ref = e.id + ":" + author_ref_author;
		// write author events
		for(String author : e.authors){
		    printer.printRecord(year_str, event_id_auth, author, paper_id, author_str);
		}
		// keep only references that are among the included papers (considering their type)
		HashSet<String> tmp_refs = new HashSet<String>();
		for(String ref: e.references){
		    if(events.containsKey(ref)){
			tmp_refs.add(ref);
		    }
		}
		// write paper paper reference events
		for(String ref : tmp_refs){
		    printer.printRecord(year_str, event_id_p_ref, paper_id, ref, paper_ref_paper);
		}
		// write author paper reference events
		if(e.authors.size() > 0 && tmp_refs.size() > 0){
		    HashSet<String> referencedAuthors = new HashSet<String>(); // stores all referenced authors once
		    // for first author, write list of references
		    String author = e.authors.iterator().next();
		    for(String ref : tmp_refs){
			printer.printRecord(year_str, event_id_a_ref, author, ref, author_ref_paper);
			if(events.containsKey(ref)){
			    for(String refAuth : events.get(ref).authors){
				referencedAuthors.add(refAuth);
			    }
			}
		    }
		    // for first reference, write out all authors
		    String ref = tmp_refs.iterator().next();
		    for(String auth : e.authors){
			printer.printRecord(year_str, event_id_a_ref, auth, ref, author_ref_paper);
		    }
		    // write author author reference events
		    if(referencedAuthors.size() > 0){
			// for first author, write list of referenced authors
			String firstAuth = e.authors.iterator().next();
			for(String refAuth : referencedAuthors){
			    printer.printRecord(year_str, event_id_a_a_ref, firstAuth, refAuth, author_ref_author);
			}
			// for first referenced author, write out all authors
			String refAuth = referencedAuthors.iterator().next();
			for(String auth : e.authors){
			    printer.printRecord(year_str, event_id_a_a_ref, auth, refAuth, author_ref_author);
			}			
		    }
		}
	    }
	    printer.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParseException e) {
	    e.printStackTrace();
	}
    }

}

class Event implements Comparable<Event>{
    int year;
    String id;
    HashSet<String> authors;
    HashSet<String> references;

    public Event(int year, String id, HashSet<String> authors, HashSet<String> references){
	this.id = id;
	this.year = year;
	this.authors = authors;
	this.references = references;
    }

    public int compareTo(Event e){
	if(year < e.year)
	    return -1;
	if(year > e.year)
	    return 1;
	return 0;
    }
}
