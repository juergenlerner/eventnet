package kn.uni.eventnet.hyper.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import kn.uni.eventnet.hyper.EventnetHyper;
import kn.uni.eventnet.hyper.text.Str;

public class ConvertToLibFM4Regression {

	private static final String DENSE_STAT = "DENSE_STAT:";
	private static final String SPARSE_STAT = "SPARSE_STAT:";
	private static final String COLON = ":";
	
	public static void main(String[] args) {
		if(args.length < 13){
			System.out.println("usage: java ConvertToLibFM4Regression "
					+ "<infile_all_events> <infile_constrained_events> <separator> "
					+ "<colNameY> <colNameSrc> "
					+ "<colNameTime> <firstStartValidationTime> <endStartValidationTime> <intervalLength> "
					+ "<outfile_train> <outfile_validation> <outfile_test> "
					+ "<outfile_grouping>"
					+ "[<colEventnetComponentStat1> ...]");
			System.exit(1);
		}

		int argInd = 0;
		String infile_all = args[argInd++];
		String infile_constraint = args[argInd++];
		char sep = args[argInd++].charAt(0);
		String colY = args[argInd++];
		String colNode1 = args[argInd++];
		String colTime = args[argInd++];
		long firstStartValidationTime = Long.parseLong(args[argInd++]);
		long endStartValidationTime = Long.parseLong(args[argInd++]);
		long intervalLength = Long.parseLong(args[argInd++]);
		String outfile_train = args[argInd++];
		String outfile_validation = args[argInd++];
		String outfile_test = args[argInd++];
		String outfile_grouping = args[argInd++];
		int mandatoryArguments = argInd;
		HashSet<String> colEventComponentStats = new HashSet<String>();
		for(int i = mandatoryArguments; i < args.length; ++i){
			colEventComponentStats.add(args[i]);
		}
		
		int counter = 0;
		for(long startValidationTime = firstStartValidationTime; startValidationTime < endStartValidationTime; 
				startValidationTime = startValidationTime + intervalLength) {
			long startTestTime = startValidationTime + intervalLength;
			long endTestTime = startTestTime + intervalLength;
			int missing_digits = 4 - ((int) Math.floor(Math.log10(Math.max(1,counter)))+1);
			String prefix = "";
			for(int i = 0; i < missing_digits; ++i) {
				prefix = prefix + "0";
			}
			prefix = prefix + counter;
			prefix = prefix + "_";
			convertToLibFM(infile_all, infile_constraint, sep, colY, colNode1, colTime, 
					startValidationTime, startTestTime, endTestTime, 
					appendPrefix(outfile_train, prefix), appendPrefix(outfile_validation, prefix), 
					appendPrefix(outfile_test, prefix), appendPrefix(outfile_grouping, prefix), 
					colEventComponentStats);
			++counter;
		}
		System.out.println("DONE!");
	}

	private static String appendPrefix(String outfile_train, String prefix) {
		File file = new File(outfile_train);
		file = new File(file.getParent(), prefix + file.getName());
		outfile_train = file.getAbsolutePath();
		return outfile_train;
	}

	private static void convertToLibFM(String infile_all, String infile_constraint, char sep, String colY,
			String colNode1, String colTime, long startValidationTime, long startTestTime,
			long endTestTime, String outfile_train, String outfile_validation, String outfile_test,
			String outfile_grouping,
			HashSet<String> colEventComponentStats) {
		LinkedHashMap<String,Integer> realStatIndex = new LinkedHashMap<String,Integer>();	
		LinkedHashMap<String,Integer> dimType2GroupIndex = new LinkedHashMap<String,Integer>();	
		LinkedHashMap<Integer,Integer> index2group = new LinkedHashMap<Integer,Integer>();	

		try{
			System.out.println("start valid:" + startValidationTime + 
					"; start test:" + startTestTime + "; end test:" + endTestTime);
			// read file of all events at the training time to get the indices of dimensions 
			CSVParser parser = new CSVParser(new FileReader(infile_all), 
					CSVFormat.DEFAULT.withDelimiter(sep));
			for(CSVRecord record : parser){
				long time = Long.MAX_VALUE;
				for(String str : record) {
					if(str.startsWith(colTime)) {
						time = Long.parseLong(str.substring(colTime.length()));
					}
				}
				if(time < startValidationTime) {
					for(String str : record) {
						if(str.startsWith(colNode1)) {
							if(!dimType2GroupIndex.containsKey(colNode1))
								dimType2GroupIndex.put(colNode1, dimType2GroupIndex.size());
							int lastColonInd = str.lastIndexOf(COLON);
							String statName = str.substring(0,lastColonInd+1);
							if(!realStatIndex.containsKey(statName)) {
								realStatIndex.put(statName,realStatIndex.size());
								index2group.put(realStatIndex.get(statName),dimType2GroupIndex.get(colNode1));
							}
						}
						if(str.startsWith(DENSE_STAT)) {
							int lastColonInd = str.lastIndexOf(COLON);
							String statName = str.substring(0,lastColonInd+1);
							if(!dimType2GroupIndex.containsKey(statName))
								dimType2GroupIndex.put(statName, dimType2GroupIndex.size());
							if(!realStatIndex.containsKey(statName)) {
								realStatIndex.put(statName,realStatIndex.size());
								index2group.put(realStatIndex.get(statName),dimType2GroupIndex.get(statName));
							}
						}
						if(str.startsWith(SPARSE_STAT)) {
							int lastColonInd = str.lastIndexOf(COLON);
							String statName = str.substring(0,lastColonInd+1);
							int secondColonInd = str.indexOf(COLON, str.indexOf(COLON)+1);
							String dimTypeName = str.substring(0, secondColonInd+1);
							if(!dimType2GroupIndex.containsKey(dimTypeName))
								dimType2GroupIndex.put(dimTypeName, dimType2GroupIndex.size());
							if(!realStatIndex.containsKey(statName)) {
								realStatIndex.put(statName,realStatIndex.size());
								index2group.put(realStatIndex.get(statName),dimType2GroupIndex.get(dimTypeName));
							}
						}
						for(String eventComponentStat : colEventComponentStats) {
							if(str.startsWith(eventComponentStat)) {
								int lastColonInd = str.lastIndexOf(COLON);
								String statName = str.substring(0,lastColonInd+1);
								if(!dimType2GroupIndex.containsKey(statName))
									dimType2GroupIndex.put(statName, dimType2GroupIndex.size());
								if(!realStatIndex.containsKey(statName)) {
									realStatIndex.put(statName,realStatIndex.size());
									index2group.put(realStatIndex.get(statName),dimType2GroupIndex.get(statName));
								}
							}							
						}
					}					
				}
			}
			parser.close();
			System.out.println("Found " + realStatIndex.size() + " dimensions");
			System.out.println("Max group index = " + dimType2GroupIndex.size());
			sortAndPrint(outfile_grouping, index2group);
			////////////////////////////////////////////////////////////////////////////////
			// read file of all events to store the training data
			DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			decimalFormat.setMaximumFractionDigits(EventnetProperties.getInstance().getDecimalDigits());
			ArrayList<String> lines = new ArrayList<String>();			
			parser = new CSVParser(new FileReader(infile_all), CSVFormat.DEFAULT.withDelimiter(sep));
			for(CSVRecord record : parser){
				String line = "";
				long time = Long.MAX_VALUE;
				for(String str : record) {
					if(str.startsWith(colTime)) {
						time = Long.parseLong(str.substring(colTime.length()));
					}
				}
				if(time < startValidationTime) {
					double y = Double.NaN;
					boolean hasAnySource = false;
					for(String str : record) {
						if(str.startsWith(colY)) {
							try {
								y = Double.parseDouble(str.substring(colY.length()));
								line = line + decimalFormat.format(y);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if(y != Double.NaN) {
						for(String str : record) {
							if (str.startsWith(colNode1)) {
								int lastColonInd = str.lastIndexOf(COLON);
								String statName = str.substring(0,lastColonInd+1);
								if(realStatIndex.containsKey(statName)) {
									try {
										double x = Double.parseDouble(str.substring(lastColonInd+1));
										if(x != 0.0) {
											hasAnySource = true;
											line = line + Str.SPACE + realStatIndex.get(statName).toString() + 
													Str.COLON + decimalFormat.format(x);									
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								int lastColonInd = str.lastIndexOf(COLON);
								String statName = str.substring(0,lastColonInd+1);
								if(realStatIndex.containsKey(statName)) {
									try {
										double x = Double.parseDouble(str.substring(lastColonInd+1));
										if(x != 0.0) {
											line = line + Str.SPACE + (realStatIndex.get(statName)) + 
													Str.COLON + decimalFormat.format(x);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} 
							}
						}
						if(hasAnySource) {
							lines.add(line);
						}
					} // else: no given target (y)
				}
			}
			parser.close();
			shuffleAndPrint(outfile_train, lines);
			////////////////////////////////////////////////////////////////////////////////
			// read file of all events to store the validation and test data
			ArrayList<String> linesValid = new ArrayList<String>();			
			ArrayList<String> linesTest = new ArrayList<String>();			
			parser = new CSVParser(new FileReader(infile_constraint), CSVFormat.DEFAULT.withDelimiter(sep));
			for(CSVRecord record : parser){
				String line = "";
				long time = Long.MAX_VALUE;
				for(String str : record) {
					if(str.startsWith(colTime)) {
						time = Long.parseLong(str.substring(colTime.length()));
					}
				}
				if(time >= startValidationTime) {
					double y = Double.NaN;
					for(String str : record) {
						if(str.startsWith(colY)) {
							try {
								y = Double.parseDouble(str.substring(colY.length()));
								line = line + decimalFormat.format(y);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if(y != Double.NaN) {
						boolean hasAnySource = false;
						for(String str : record) {
							if (str.startsWith(colNode1)) {
								int lastColonInd = str.lastIndexOf(COLON);
								String statName = str.substring(0,lastColonInd+1);
								if(realStatIndex.containsKey(statName)) {
									try {
										double x = Double.parseDouble(str.substring(lastColonInd+1));
										if(x != 0.0) {
											hasAnySource = true;
											line = line + Str.SPACE + realStatIndex.get(statName).toString() + 
													Str.COLON + decimalFormat.format(x);									
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								int lastColonInd = str.lastIndexOf(COLON);
								String statName = str.substring(0,lastColonInd+1);
								if(realStatIndex.containsKey(statName)) {
									try {
										double x = Double.parseDouble(str.substring(lastColonInd+1));
										if(x != 0.0) {
											line = line + Str.SPACE + (realStatIndex.get(statName)) + 
													Str.COLON + decimalFormat.format(x);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} 
							}
						}
						if(hasAnySource) {
							if(time < startTestTime) {
								linesValid.add(line);
							} else if (time < endTestTime){
								linesTest.add(line);
							}
						}
					} // else: no given target (y)
				}
			}
			parser.close();
			shuffleAndPrint(outfile_validation, linesValid);
			shuffleAndPrint(outfile_test, linesTest);
		}  catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void sortAndPrint(String outfileName, 
			LinkedHashMap<Integer,Integer> index2group) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(outfileName));
		PairIntInt[] pairs = new PairIntInt[index2group.size()];
		int i = 0;
		for(Integer index : index2group.keySet()) {
			pairs[i] = new PairIntInt(index, index2group.get(index));
			++i;
		}
		Arrays.sort(pairs);
		for(int j = 0; j < pairs.length; ++j) {
			writer.println(pairs[j].target().toString());
		}
		writer.flush();
		writer.close();
	}

	private static void shuffleAndPrint(String outfileName, 
			ArrayList<String> lines) throws IOException {
		shuffleLines(lines);
		PrintWriter writer = new PrintWriter(new FileWriter(outfileName));
		for(String line : lines) {
			writer.println(line);
		}
		writer.flush();
		writer.close();
	}

	private static void shuffleLines(ArrayList<String> lines) {
		Random rand = EventnetHyper.getRandom();
		for(int i = lines.size()-1; i > 0; --i) {
			int r = rand.nextInt(i+1);
			swap(lines, i, r);
		}
	}

	private static void swap(ArrayList<String> lines, int i, int j) {
		if(i == j)
			return;
		String tmp = lines.get(j);
		lines.set(j, lines.get(i));
		lines.set(i, tmp);
	}

}
