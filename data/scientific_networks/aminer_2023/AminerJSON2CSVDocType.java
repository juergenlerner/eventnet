package kn.uni.eventnet.hyper.util.coauthor.io;

import java.util.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * @author Juergen Lerner
 *
 */
public class AminerJSON2CSVDocType {

	public static final String author_str = "author";
	public static final String paper_ref_paper = "paper.ref.paper";
	public static final String author_ref_paper = "author.ref.paper";
	public static final String paper_ref_author = "paper.ref.author";
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

		try {
			JsonReader reader = new JsonReader(new FileReader(args[0]));
			// maps paper id to the respective event
			HashMap<String,Event> events = new HashMap<String,Event>();
			int count = 0;
			reader.beginArray();
			// iteration over papers
			while(reader.hasNext()) {
				if(count % 10000 == 0)
					System.out.println("processed paper number:" + count);
				addPaper(docType, reader, events);
				++count;
			}
			reader.endArray();
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
			// for "paper.ref.author" events: source is citing paper and target is list of all authors of all references 
			// for "author.ref.author" events: source is list of citing authors and target is list of all authors of all references 
			for(int i = 0; i < ev_array.length; ++i){
				Event e = ev_array[i];
				String year_str = Integer.valueOf(e.year).toString();
				String paper_id = e.id;
				String event_id_auth = e.id + ":" + author_str;
				String event_id_p_ref = e.id + ":" + paper_ref_paper;
				String event_id_a_ref = e.id + ":" + author_ref_paper;
				String event_id_p_a_ref = e.id + ":" + paper_ref_author;
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
					// write paper author reference events
					if(referencedAuthors.size() > 0){
						// for citing paper, write list of referenced authors
						for(String refAuth : referencedAuthors){
							printer.printRecord(year_str, event_id_p_a_ref, paper_id, refAuth, paper_ref_author);
						}
					}
				}
			}
			printer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addPaper(String docType, JsonReader reader, HashMap<String, Event> events) throws IOException {
		reader.beginObject();
		// iteration over info of one paper
		String paper_id = null;
		String paper_doc_type = null;
		int year = 0;
		HashSet<String> authorSet = new HashSet<String>();
		HashSet<String> refSet = new HashSet<String>();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals("id")) {
				paper_id = reader.nextString();
			} else if(name.equals("doc_type")) {
				paper_doc_type = reader.nextString();
			} else if(name.equals("year")) {
				year = (int) reader.nextLong();
			} else if (name.equals("authors") && reader.peek() != JsonToken.NULL) {
				readAuthorsArray(reader, authorSet);
			} else if(name.equals("references") && reader.peek() != JsonToken.NULL) {
				readReferencesArray(reader, refSet);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		if(paper_id.length() > 0 && year > 0 && docType.equals(paper_doc_type))
			events.put(paper_id,new Event(year, paper_id, authorSet, refSet));
	}

	private static void readReferencesArray(JsonReader reader, HashSet<String> refSet) throws IOException {
		reader.beginArray();
		while(reader.hasNext() && reader.peek() != JsonToken.NULL) {
			String ref_id = reader.nextString();
			if(ref_id.length() > 0)
				refSet.add(ref_id);
		}
		reader.endArray();
	}

	private static void readAuthorsArray(JsonReader reader, HashSet<String> authorSet) throws IOException {
		reader.beginArray();
		while(reader.hasNext()) {
			reader.beginObject();
			while(reader.hasNext() && reader.peek() != JsonToken.NULL) {
				String author_info_name = reader.nextName();
				if(author_info_name.equals("id")) {
					String auth_id = reader.nextString();
					if(auth_id.length() > 0)
						authorSet.add(auth_id);
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		reader.endArray();
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
