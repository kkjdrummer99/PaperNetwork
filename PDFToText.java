/*********************************************************************************
   Description     : Paper Network - Citation Check Program
   Source File     : PDFToText.java
   Author          : Kyung Jin Kim
                     Dept. of Knowledge Service Engineering
                     Korea Advanced Institute of Science and Technology
   Project Name    : CourseShare
   Team Name       : Network
   Prof.           : Mun Yong Yi / Jae-Gil Lee 
**********************************************************************************/

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.cos.COSDocument;

public class PDFToText {
	String outText;
	
	public static String ConvertPDFToText(String file) throws Exception {
		System.out.println("convertPDF");
		String text = null;
		InputStream in = new FileInputStream(file);
		PDFParser parser = new PDFParser(in);
		parser.parse();

		PDDocument pdd = parser.getPDDocument();
		COSDocument cos = parser.getDocument();
		PDFTextStripper stripper = new PDFTextStripper();
		
		text = stripper.getText(pdd);
		
		System.out.println(text);
		
		StringBuffer sb = new StringBuffer(text);
		for (int i = 0; i < text.length(); i++) {
			if ((int) text.charAt(i) > 127 && (int) text.charAt(i) < 5000)
				sb.setCharAt(i, ' ');
		}
		text = sb.toString();
		//this.outText = text;

		String[] token = text.split("\n");
		for(int i=0; i<token.length;i++)
		{
			//System.out.println(token[i]);
		}
		//System.out.println(text);
		cos.close();
		pdd.close();

		System.out.println("Completed!\n");
		// System.out.println("===================================");
		//return this.outText;
		return text;
	}
	
	public static void main(String[] args) throws Exception {
		FrameEvent fe = new FrameEvent();
					
		FileDialog fileOpen = new FileDialog(fe, "파일열기", FileDialog.LOAD);
		
		fe.setVisible(true);
		fileOpen.setDirectory("C:\\workspace\\PaperNetwork\\src");
		fileOpen.setVisible(true);
		String text = ConvertPDFToText(fileOpen.getDirectory()+"\\"+fileOpen.getFile());
				
		Citation c = new Citation();
		c.numberNameYearCitationSep(text);
		
	}		
}

class FrameEvent extends Frame {	
	public FrameEvent() {
		super("*** Citation ***");
		setSize(800, 100);
				
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		
		super.setLocation(screenSize.width/2 - 150, screenSize.height/2 - 100);
		super.setBackground(Color.cyan);
				
		Label title = new Label("This program is to give you the citation results of the paper that you select.\n\n");
		Font font1 = new Font("Georgia", Font.BOLD, 20);
		title.setFont(font1);
		super.setLayout(new FlowLayout());
		super.add(title);
		super.setVisible(true);
							
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}	
		}
		);
	}
}

class Citation {
	
	final static int MEMORY_SIZE = 1000;
		
	String text;
	String pattern;
	String paperHead = "";
	static Hashtable<String, Double> ht = new Hashtable<String, Double>();
	
	// Constructor
	public Citation() {
		
	}
	
	/****************************************************************************
	 * Method Name : NumberNameYearCitationSep
	 * Purpose     : Separate Number based citation and Name-Year based citation
	 * Parameters  : Text file converted from PDF file using PDF parser
	 * Return      : None
	 ****************************************************************************/ 
	
	public void numberNameYearCitationSep(String text) {
		String numberBasedPattern = "\\[([0-9].{0,4})*[0-9]+[]|?]";
		String nameYearBasedPatternType1 = "[\\(|\\[][A-Z][a-z|A-Z|\\s|.|,|’|&|;]+\\s?[1|2]\\d{3}[a-z]?(,?\\s?[1|2]\\d{3}[a-z]?)*(;?\\s?[A-Z][a-z|A-Z|\\s|.|,|’|&|;]+\\s?[1|2]\\d{3}[a-z]?(,?\\s?[1|2]\\d{3}[a-z]?)*)*[\\)|\\]]";
		String nameYearBasedPatternType2 = "[A-Z][a-z|A-Z|’]+\\s?(\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][a-z|A-Z|’]+\\s?)?\\s?([e][t]\\s?[a][l]\\.?)?\\s?\\([1|2]\\d{3}[a-z]?(\\s?[;|,]\\s?[1|2]\\d{3}[a-z]?)*\\)|[A-Z][a-z|A-Z|’]+\\s?(\\s?&\\s?[A-Z|a-z][a-z|A-Z|’]+\\s?)?\\s?([e][t]\\s?[a][l]\\.?)?\\s?\\([1|2]\\d{3}[a-z]?(\\s?[;|,]\\s?[1|2]\\d{3}[a-z]?)*\\)";
		String shortNameYearBasedPattern = "\\[[A-Z][a-z|A-Z|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?(.\\s*[A-Z][a-z|A-Z]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?)*](,?\\s*\\[[A-Z][a-z|A-Z|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?(.?\\s*[A-Z][a-z|A-Z|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?)*])*";
		String str;
		String temp;
		Pattern numberPattern;
		Matcher mNumberPattern;
		Pattern nameYearPattern1;
		Matcher mNameYearPattern1;
		Pattern nameYearPattern2;
		Matcher mNameYearPattern2;
		Pattern shortNameYearPattern;
		Matcher mShortNameYearPattern;
		int numberBasedCitationCount=0;
		int nameYearBasedCitationCount=0;
		int shortNameYearBasedCitationCount=0;
						
		FileWriter fw = null;
		BufferedWriter fileBw = null;
		FileReader fr = null;
		BufferedReader fileBr = null;
		
		try {
			fw = new FileWriter("sampleIn.txt");
			fileBw = new BufferedWriter(fw);
			
			fileBw.write(text);
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}
		
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("body.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    		    	
	    	OuterLoop:
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S].*") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R].*") || temp.matches(".*S\\s?[u|U]\\s?[g|G]\\s?[g|G]\\s?[e|E]\\s?[s|S]\\s?[t|T]\\s?[e|E]\\s?[t|T]\\s*[R|r]\\s?[e|E]\\s?[a|A]\\s?[d|D]\\s?[i|I]\\s?[n|N]\\s?[g|G].*")) {
		    			break OuterLoop;
		    		}
		    				    			    		   		
		    		fileBw.write(temp);
		    		fileBw.append(' ');
		       	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
		
		// Separate Number based citation and Name-Year based citation
		try {
			fr = new FileReader("body.txt");
		   	fileBr = new BufferedReader(fr);
			    			    	    	
		   	str = "";
		    	
		   	while((str = fileBr.readLine())!=null) {
		   		StringTokenizer buf = new StringTokenizer(str, "\n");
		   		numberPattern = Pattern.compile(numberBasedPattern);
		   		nameYearPattern1 = Pattern.compile(nameYearBasedPatternType1);
		   		nameYearPattern2 = Pattern.compile(nameYearBasedPatternType2);
		   		shortNameYearPattern = Pattern.compile(shortNameYearBasedPattern);
		   		temp="";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		mNumberPattern = numberPattern.matcher(temp);
		    		mNameYearPattern1 = nameYearPattern1.matcher(temp);
		    		mNameYearPattern2 = nameYearPattern2.matcher(temp);
		    		mShortNameYearPattern = shortNameYearPattern.matcher(temp);
		    			    		
		    		while(mNameYearPattern1.find()) {
		    			//nameYearBasedCitationCalculation(text);
		    			//return;
		    			nameYearBasedCitationCount++;
		       		}
		    		
		    		while(mNameYearPattern2.find()) {
		    			//nameYearBasedCitationCalculation(text);
		    			//return;
		    			nameYearBasedCitationCount++;
		    		}
		    		
		    		while(mNumberPattern.find()) {
		    			//numberBasedCitationCalculation(text);
		    			//return;
		    			numberBasedCitationCount++;
		    		}
		    		
		    		while(mShortNameYearPattern.find()) {
		    			//shortNameYearBasedCitationCalculation(text);
		    			//return;
		    			shortNameYearBasedCitationCount++;
		    		}
		    	}
		   	}
			    		    				    	
		   	fileBr.close();
		   	fr.close();
		} catch(Exception e) {
			   System.out.println(e);  
		}		
		
		int max = numberBasedCitationCount;
		if(max<nameYearBasedCitationCount) {
			max = nameYearBasedCitationCount;
		}
		if(max<shortNameYearBasedCitationCount) {
			max = shortNameYearBasedCitationCount;
		}
		
		if(max==numberBasedCitationCount) {
			numberBasedCitationCalculation(text);
			return;
		}
		else if(max==nameYearBasedCitationCount) {
			nameYearBasedCitationCalculation(text);
			return;
		}
		else if(max==shortNameYearBasedCitationCount) {
			shortNameYearBasedCitationCalculation(text);
			return;
		}
	}	
	
	/****************************************************************************
	 * Method Name : numberBasedCitationCalculation
	 * Purpose     : Calculate Number based citation 
	 * Parameters  : text file converted from PDF file using PDF parser
	 * Return      : none
	 ****************************************************************************/ 
	
	public void numberBasedCitationCalculation(String text) {
		String str;	
		String temp;
	
		pattern = "\\[([0-9].{0,4})*[0-9]+[\\]|?]";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		String[] refTempTable = new String[MEMORY_SIZE];
		String[] refTable = new String[MEMORY_SIZE];
		String[][] numRefInfoTable = new String[MEMORY_SIZE][6];
		String[] tempTable = new String[MEMORY_SIZE];
		StringTokenizer st;
		StringTokenizer st1;
		StringBuffer sb;
		
		int tempSize = 0;
		int refTempSize = -1;
		int refSize = 0;
		double weight;
		boolean match;
		
		String nonPCDATAType1 = "&";
		String nonPCDATAType2 = "<";
		String nonPCDATAType3 = ">";
		String nonPCDATAType4 = "\"";
		String nonPCDATAType5 = "'";
		
		FileWriter fw = null;
		BufferedWriter fileBw = null;
		FileReader fr = null;
		BufferedReader fileBr = null;
		
		try {
			fw = new FileWriter("sampleIn.txt");
			fileBw = new BufferedWriter(fw);
			
			fileBw.write(text);
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}
				
		System.out.println("*** Reference Separation Process ***");
		String refPatternType1 = "\\s*\\[[1-9]\\d{0,2}[\\]|?]\\s.+.";
		String refPatternType2 = "[1-9]\\d{0,2}\\s*\\.\\s.+.";
		
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("references.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
	    		OuterLoop:
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S].?\\s*") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R].*") || temp.matches(".*S[u|U][g|G][g|G][e|E][s|S][t|T][e|E][d|D]\\s*[R|r][e|E][a|A][d|D][i|I][n|N][g|G].*") || temp.matches(".*B[I|i][B|b][L|l][I|i][O|o][G|g][R|r][A|a][P|p][H|h][Y|y].?\\s*")) {
		    			while((str = fileBr.readLine())!=null) {
		    				buf = new StringTokenizer(str, "\n");
			    	    	temp = "";
			    	    			    	    			    	    		
			    	    	while(buf.hasMoreTokens()) {
			    	    		temp = buf.nextToken();
			    	    		   						    	    	
			    	    		if(temp.matches(refPatternType1)) {
			    	    			Pattern rpt1 = Pattern.compile(refPatternType1);
				    	    		Matcher mrpt1 = rpt1.matcher(temp);
			    	    			while(mrpt1.find()) {
			    	    				fileBw.newLine();
			    	    				refTempSize++;
			    	    				refTempTable[refTempSize] = temp.substring(mrpt1.start(), mrpt1.end()).trim();
			    	    				System.out.println((refTempSize+1)+"-Th ref.: "+refTempTable[refTempSize]);
			    	    			}
			    	    		}
			    	    		
			    	    		if(temp.matches(refPatternType2)) {
			    	    			Pattern rpt2 = Pattern.compile(refPatternType2);
				    	    		Matcher mrpt2 = rpt2.matcher(temp);
			    	    			while(mrpt2.find()) {
			    	    				fileBw.newLine();
			    	    				refTempSize++;
			    	    				refTempTable[refTempSize] = temp.substring(mrpt2.start(), mrpt2.end()).trim();
			    	    				System.out.println((refTempSize+1)+"-Th ref.: "+refTempTable[refTempSize]);
			    	    			}
			    	    		}
			    	    		
			    	    		
			    	    		if(temp.matches(".*A[p|P][p|P][e|E][n|N][d|D][i|I][x|X].*")) {
					    			break OuterLoop;
					    		}
			    	    		
			    	    		if(temp.matches(".*[W|w][O|o][R|r][D|d].*[F|f][R|r][E|e][Q|q][U|u][E|e][N|n][C|c][Y|y].*")) {
					    			break OuterLoop;
					    		}
			    	    		
			    	    		if(temp.matches(".*A[d|D][d|D][r|R][e|E][s|S][s|S]\\s*[f|F][o|O][r|R]\\s*[C|c][o|O][r|R][r|R][e|E][s|S][p|P][o|O][n|N][d|D][e|E][n|N][c|C][e|E].*")) {
					    			break OuterLoop;
					    		}
			    	    		
			    	    		if(temp.matches(".*Q[u][e][s][t][i][o][n]\\s*[1-9].*")) {
					    			break OuterLoop;
					    		}
			    	    		
			    	    		fileBw.write(temp);
			    	    		//fileBw.newLine();
			    	    	}			    	    	
		    			}		    			
		    		}	    		
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}		
		
		try {
			fr = new FileReader("references.txt");
	    	fileBr = new BufferedReader(fr);
	    			    	    	
	    	str = "";
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken().trim();
		    		if(temp.matches("")) {
		    			break;
		    		}
		    		
		    		if(temp.matches(refPatternType1)) {
    	    			Pattern rpt1 = Pattern.compile(refPatternType1);
	    	    		Matcher mrpt1 = rpt1.matcher(temp);
    	    			while(mrpt1.find()) {
    	    				refTable[refSize++] = temp.substring(mrpt1.start(), mrpt1.end()).trim();
    	    			}
    	    		}
    	    		
    	    		if(temp.matches(refPatternType2)) {
    	    			Pattern rpt2 = Pattern.compile(refPatternType2);
	    	    		Matcher mrpt2 = rpt2.matcher(temp);
    	    			while(mrpt2.find()) {
    	    				refTable[refSize++] = temp.substring(mrpt2.start(), mrpt2.end()).trim();
    	    			}
    	    		}
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();	 
		} catch(Exception e) {
			System.out.println(e);  
		}		
				
		System.out.println("*** Before Reference Separation ***");
		for(int i=0; i<refSize; i++) {
			System.out.println((i+1)+"-th ref.: "+refTable[i]);
		}		
				
		String refPattern1 = "[A-Z][a-z|A-Z|\\-|\\s|.|,|&|’|(|)|;]+\\\\?\\([1|2][0|8|9]\\d{2}[a-z]?(/[1|2][0|8|9]\\d{2}[a-z]?)?\\\\?\\)\\s?\\.?,?:?;?\\s*[“|\"|\\s]?\\s*[A-Z][a-z|A-Z|\\-|\\s|,|?|;|:|(|)|'|‘|’]+[”|\"|.|,|\\s]?";
		Pattern rp1 = Pattern.compile(refPattern1);
		String refPattern2 = ".+”\\s*[A-Z][a-z]+\\.\\s*[A-Z][a-z]+\\.,\\s*[A-Z][a-z]+\\.\\s*\\d+,\\s*[1|2][0|8|9]\\d{2}[a-z]?." +
				"|.+\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3}\\,?\\s?[1|2][0|8|9]\\d{2}[a-z]?\\)?\\.?\\s?“?\\s*([A-Z][a-z|A-Z|\\s|\\-|,|:|?|(|)|'|‘|’|“|”]+[.|,]\\s*)?\\s?(\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3})?" +
				"|.+[1|2][0|8|9]\\d{2}[a-z]?\\)?\\s?\\.?,?\\s*“?\\s*([A-Z][a-z|A-Z|\\s|\\-|,|:|?|(|)|'|‘|’|“|”]+[.|,]\\s*)?(\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3})?[^\\d|A-Z|a-z]*[.|,]?";
		Pattern rp2 = Pattern.compile(refPattern2);
		for(int i=0; i<refSize; i++) {
			if(refTable[i].matches("\\s*\\[?[1-9]\\d*\\s*[]|.]?\\s*[\"|“]?\\s*"+refPattern1+".*")) {
				Matcher mrp1 = rp1.matcher(refTable[i]);
				if(mrp1.find()) {
					numRefInfoTable[i][0] = refTable[i].substring(mrp1.start(), mrp1.end()).trim();
				}
				else {
					numRefInfoTable[i][0] = refTable[i];
				}
			}
			else {
				Matcher mrp2 = rp2.matcher(refTable[i]);
				if(mrp2.find()) {
					//refTable[i] = refTable[i].substring(mrp.start(), mrp.end()).trim();
					numRefInfoTable[i][0] = refTable[i].substring(mrp2.start(), mrp2.end()).trim();
				}	
				else {
					numRefInfoTable[i][0] = refTable[i];
				}
			}	
		}	
		
		System.out.println("*** After Reference Separation ***");
		for(int i=0; i<refSize; i++) {
			System.out.println((i+1)+"-th ref.: "+numRefInfoTable[i][0]);
		}
		System.out.println("****************************");
		
		for(int i=0; i<refSize; i++) {
			if(numRefInfoTable[i][0].matches(".*\\?.*")) {
				match = false;
				sb = new StringBuffer();
					
				Pattern pq = Pattern.compile("\\?");
				Matcher mpq = pq.matcher(numRefInfoTable[i][0]);
							
				while(mpq.find()) {
					mpq.appendReplacement(sb, "\\-");
					match = true;
				}
										
				mpq.appendTail(sb);
							
				if(match) {
					numRefInfoTable[i][0] = sb.toString().trim();
				}			
			}
		}		
		
		// Number extraction from each reference
		String numPatternType1 = "\\s*\\[[1-9]\\d{0,2}[\\]|?]";
		String numPatternType2 = "[1-9]\\d{0,2}\\s*\\.";
		
		for(int i=0; i<refSize; i++) {
			Pattern pnt1 = Pattern.compile(numPatternType1);
			Matcher mpnt1 = pnt1.matcher(refTable[i]);
			Pattern pnt2 = Pattern.compile(numPatternType2);
			Matcher mpnt2 = pnt2.matcher(refTable[i]);
			
			// Number Pattern Type1 : [N]
			if(mpnt1.find()) {
				numRefInfoTable[i][1] = refTable[i].substring(mpnt1.start()+1, mpnt1.end()-1).trim();
				System.out.println((i+1)+"-th ref. number: "+numRefInfoTable[i][1]);
			}
			// Number Pattern Type1 : N.
			else if(mpnt2.find()) {
				numRefInfoTable[i][1] = refTable[i].substring(mpnt2.start(), mpnt2.end()-1).trim();
				System.out.println((i+1)+"-th ref. number: "+numRefInfoTable[i][1]);
			}		
		}
		System.out.println("****************************");
				
		// Citation Count Initialization
		for(int i=0; i<refSize; i++) {
			numRefInfoTable[i][5] = "0.0";
		}		
		
		System.out.println("*** Author, Title, and Year Extraction ***");
		String patternPreTitle = "[^(\\[?[1-9]\\d{0,2}\\]?\\.?\\s?)].+[.|,|”|\"|/|\\s]";
		String patternTitle = ".?\\s?“?”?\\\\?\"?([0-9]+\\s?\\?\\s?)?(\\\\?\\(?[1|2]\\d{3}[a-z]?\\.?\\\\?\\)?\\.?:?)?([1|2]\\d{3}[a-z]?\\s*\\-\\s*[1|2]\\d{3}[a-z]?\\.?)?(\\(U[n][d][a][t][e][d]\\))?(I[n][P][r][o][c][e][e][d][i][n][g][s]\\s*[o][f]\\s*[t][h][e]\\.)?([A-Z|a-z][a-z|A-Z|\\-]+[.|,|:]\\s*)*\\s*“?\\s*[A-Z|a-z|0-9][a-z|A-Z|0-9|\\s|\\-|:|;|(|)|?|@|/|‘|’|*|_|“|”|'|!|&|+|=|\\\\]+[.|,|”|\"](\\s*[A-Z|a-z][a-z|A-Z|\\-]+\\s[A-Z|a-z][a-z|A-Z|0-9|\\s|\\-|,]+[”])?(\\s*([A-Z|a-z][a-z|A-Z|\\-]+,\\s*)+[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|\\s|\\-]+[.|,|”|’|\"])?(\\s*[a-z|\\-|0-9]+[.|'|:|,|/]\\s*[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_|'|.]+[.|,|”|\"|/]?)?(\\s*[A-Z][a-z|\\-]+,\\s*[A-Z|a-z][a-z|A-Z|0-9|\\s|\\-|:]+[.|,|”|’|\"])?(\\s*[a-z]+\\s?[0-9]+\\s*[A-Z|a-z][a-z|A-Z|\\s|\\-|:]+[.|,|”|’|\"])?(\\s*([a-z]+\\s+)?[A-Z][a-z]+['|’]\\s*[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_|']+[.|”|’|\"]?)?(([0-9]+\\.?)+\\s*([A-Z]:)?,?\\s*[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|?|@|/|’|*|_|'|.]+[.|”|’|\"]?)?(\\s*[0-9|\\-]+[:|,]\\s*[A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_|']+[.|”|’|\"|]?)?(\\s*[A-Z][A-Z]+\\s\\d{4}\\.)?(\\s*[i][s]\\s[b][e][i][n][g]\\s[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|?|@|/|’|*|_|']+[.|”|’|\"]?)?";
		
		// Author type :: version1.3
		//String patternAuthor = "((\\-?[A-Z]\\.[,|;]?\\s*)+[A-Z]\\s?[a-z|A-Z|\\-|’]+\\s?,?\\s*)+([a|A][n|N][d|D]\\s*((\\-?[A-Z]\\.[,|;]?\\s*)*[A-Z]\\s?[a-z|A-Z|\\-|’]+\\s?,?\\s*)+)?";
				
		String patternAuthor =  "(\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’|]+\\s*[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’||\\s]+[.|,|:]" +
				"|(\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’|]+\\s?,\\s*(\\-?[A-Z]\\.\\s*)+[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’||\\s]+\\s?,\\s*(\\-?[A-Z]\\.\\s*)+[.|,|:]?" +
				"|((\\-?[A-Z](\\-[A-Z])?\\.\\s*)+[A-Z|a-z][a-z|A-Z|\\-|’|`||\\s]+,?(\\s?\\(?[E|e][d|D][s|S]?[i|I]?[t|T]?[o|O]?[r|R]?\\.?\\)?\\.?\\s*)?,?\\s*)+([J][r]\\.,\\s*)?[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.?,?\\s*)*[A-Z|a-z][a-z|A-Z|\\-|’|?|`]+(\\s+[A-Z|a-z|\\-|?]+\\s*)?\\s*(\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s|:]?" +
				"|(\\-?[A-Z](\\-[A-Z])?\\.\\s*)+[A-Z|a-z][a-z|A-Z|’|`||\\s]+,?\\s*([A-Z][a-z|A-Z|\\-]+\\s*)+[a|A][n|N][d|D]\\s*[A-Z][a-z|A-Z|\\-]+\\s*([A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-]+[.|,|\\s|:]?" + 
				"|(\\-?[A-Z](\\-[A-Z])?\\.\\s*)+[A-Z|a-z][a-z|A-Z|\\-|’|`||\\s]+,?(\\s*\\(?[E|e][d|D][s|S]?[i|I]?[t|T]?[o|O]?[r|R]?\\.?\\)?\\.?\\s*)?,?\\s*((\\-?[A-Z](\\-[A-Z])?\\.?\\s*)+[A-Z][a-z|A-Z|\\-|’|`||\\s]+,?(\\s*\\(?[E|e][d|D][s|S]?[i|I]?[t|T]?[o|O]?[r|R]?\\.?\\)?\\.?\\s*)?,?\\s*)*(\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s|:]?" + 
				"|([A-Z]\\.,?\\s*)+[e|E][t|T].{1,2}[a|A][l|L]." +
				//"|(([A-Z|a-z]\\s?[a-z|A-Z|\\-|’|`|']+\\s*[.|,|\\s])+\\s*(\\-?[A-Z](\\-[A-Z])?[.|,]\\s*[,|;]?\\s*)+([e|E][t|T].{1,2}[a|A][l|L]\\.?\\s*)?(\\s*\\(?[E|e][d|D][s|S]?\\.?\\)?\\.?\\s*)?)+" +
				//"|([A-Z][a-z|A-Z|\\-|`|’]+\\s*,?\\s*)+[a|A][n|N][d|D]\\s*([A-Z][a-z|A-Z|\\-|`|’]+\\s*([a-z]+\\s*)*)+((\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’|`]+)?[,|.]" +
				"|I[n|N][t|T][e|E][l|L]\\s*[C|c][o|O][r|R][p|P][o|O][r|R][a|A][t|T][i|I][o|O][n|N],\\s*[U][S][A][,|.]\\s*" +
				"|S[a|A][m|M][s|S][u|U][n|N][g|G]\\s*[E|e][x|X][p|P][e|E][r|R][i|I][e|E][n|N][c|C][e|E]," +
				//"|[A-Z][a-z|A-Z|\\-|’|`]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\s[a|A][n|N][d|D]\\s[A-Z][a-z|A-Z|\\-|’|`]+\\s[A-Z]\\.\\s*[A-Z][a-z|A-Z|\\-|’]+\\." +
				"|([A-Z][a-z|A-Z|\\-|’|`]+\\s*[A-Z][a-z|A-Z|\\-|’]+,\\s*)+[a|A][n|N][d|D]\\s*[A-Z][a-z|A-Z|\\-|’|`]+\\s*[A-Z]\\.([a-z]+\\s*)?\\s*[A-Z][a-z|A-Z|\\-|’|`]+[.|,]" +
				"|([A-Z]\\s?[a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*\\s*[A-Z]\\s?[a-z|A-Z|\\-|’|`]+,?\\s*)+[a|A][n|N][d|D]\\s*[A-Z][a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*\\s*[A-Z][a-z|A-Z|\\-|’|`]+[.|,]\\s*" +
				"|([A-Z]\\s?[a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*\\s*[A-Z]\\s?[a-z|A-Z|\\-|’|`]+,?\\s*)+&\\s*[A-Z][a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.[,|;]?\\s*)+\\s*[A-Z][a-z|A-Z|\\-|’|`]+,\\s*" +
				"|([A-Z]\\s?[a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.\\s*)+[A-Z]\\s?[a-z|A-Z|\\-|’|`]+\\s*[.|,]\\s*)+(([a|A][n|N][d|D])?\\s*[A-Z][a-z|A-Z|\\-|\\s]+[.|,|:|;])?" +
				"|([A-Z]\\s?[a-z|A-Z|\\-|’|`]+,\\s*[A-Z]\\s?[a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.?\\s*)*;?\\s*)+[a|A][n|N][d|D]\\s*([A-Z][a-z|A-Z|\\-|’|`]+,\\s*[A-Z][a-z|A-Z|\\-|’|`]+\\s*(\\-?[A-Z]\\.\\s*)*;?\\s*)+" +
				"|([A-Z]\\s?[a-z|A-Z|\\-|’|`]+,?\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*)+[a|A][n|N][d|D]\\s*((\\-?[A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-|’|`|\\s]+,?\\s*)+[.|,]?" +
				"|([A-Z][a-z|A-Z|\\-|’|`|\\s]+,?\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*)+&\\s*((\\-?[A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-|’|`]+,?\\s*)+[.|,]?" +
				"|(([A-Z]\\s+)+[A-Z][a-z|A-Z|\\-|’|`]+\\s*)+\\s*[a|A][n|N][d|D]\\s*([A-Z]\\s+)+[A-Z][a-z|A-Z|\\-|’|`]+(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s]" +
				"|([A-Z|a-z]\\s?[a-z|A-Z|\\-|’|`|']+[.|,|\\s]\\s*(\\-?[A-Z|0][a-z]?(\\-[A-Z])?[.|,]\\s*[,|;]?\\s*)+([e|E][t|T].{1,2}[a|A][l|L]\\.?\\s*)?(\\s*\\(?[E|e][d|D][s|S]?\\.?\\)?\\.?\\s*)?\\s*)+[a|A][n|N][d|D]\\s+[A-Z|a-z]\\s?[a-z|A-Z|\\-|\\s|’|`|']+[.|,|\\s]\\s*(\\-?[A-Z][.|,][,|;]?\\s?)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]?" +
				"|([A-Z][a-z|A-Z|\\-|’|`|'|\\s]+[.|,|\\s]\\s*(\\-?[A-Z][a-z]?\\.[,|;]?\\s*)+([e|E][t|T]\\s?[a|A][l|L]\\.?)?(\\s*\\(?[E|e][d|D][s|S]?\\.?\\)?\\.?\\s*)?)+\\s*&\\s*[A-Z][a-z|A-Z|\\-|’|`|\\s]+[.|,|\\s]\\s?(\\-?[A-Z]\\.?\\s*[,|;]?\\s?)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]" +
				"|([A-Z|a-z][a-z|A-Z|\\-|’|`|'|\\s]+[.|,|\\s]\\s*(\\-?[A-Z](\\-[A-Z])?[.|,]\\s*[,|;]?\\s*)+([e|E][t|T].{1,2}[a|A][l|L]\\.?\\s*)?(\\s*\\(?[E|e][d|D][s|S]?\\.?\\)?\\.?\\s*)?)+" +
				"|([A-Z][a-z|A-Z|\\-|’|`]+,?\\s*(\\-?[A-Z]\\.\\s*)*)+[a|A][n|N][d|D]\\s*((\\-?[A-Z]\\.\\s*([a-z]+)?\\s*)*[A-Z][a-z|A-Z|\\-|’|`|\\s]+,?\\s*)+[,|.]" +
				"|([A-Z][a-z|A-Z|\\-|’|`]+,?\\s*)+\\s*[a|A][n|N][d|D]\\s*([A-Z][a-z|A-Z|\\-|’|`]+,?\\s*)+[.|,]?" +
				//"|[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\s[a|A][n|N][d|D]\\s[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\." +
				"|([A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-|`]+\\s+[e|E][t|T].{1,2}[a|A][l|L]." +
				//"|([A-Z|a-z][a-z|A-Z|\\-|`|’]+(\\s+[A-Z|a-z]+\\.?)?[\\s|,|.]?\\s*(\\-?[A-Z][.|,][,|;]?\\s*)+)+([e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\))?[.|,|:|?]?" +
				"|((\\-\\s*)?[A-Z][a-z|A-Z|0-9|\\-|/|’|+|`|\\s]+,\\s*(\\-?[A-Z]\\.\\s*[,|;]?\\s*)*)+([A-Z]:)?(\\s*[A-Z][a-z]+\\.,)?\\.?(\\s*([a|A][n|N][d|D])?\\s*[A-Z][a-z|A-Z|\\-|’|`|\\s]+[.|,]?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|:|?]?" +
				"|[A-Z][a-z|A-Z|\\-|0-9|\\-|/|’|+|`|\\s]+[.|:]?";
	

		String patternYear = "\\(?[1|2][0|8|9]\\d{2}[a-z]?[.|)|,|;]?";
		
		// Extraction :: Title
		for(int i=0; i<refSize; i++) {
			
			if(refTable[i].matches("\\s*\\[?[1-9]\\d*\\s*[]|.]\\s*[\"|“]?\\s*"+refPattern1+".*")) {
				refTable[i] = numRefInfoTable[i][0];
				System.out.println(numRefInfoTable[i][1]+"-th preprocessing title(Type#1): "+refTable[i]);
			}
			else {
				Pattern pa = Pattern.compile(patternPreTitle);
				Matcher mpa = pa.matcher(numRefInfoTable[i][0]);
				if(mpa.find()) {
					refTable[i] = refTable[i].substring(mpa.start(), mpa.end()).trim();
					System.out.println(numRefInfoTable[i][1]+"-th preprocessing title: "+refTable[i]);
				}
				else {
					refTable[i] = "";
					System.out.println(numRefInfoTable[i][1]+"-th preprocessing title: "+refTable[i]);
				}
			}			
		}
		System.out.println("****************************");
		
		// Substitute ( with \\(
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pOpenBrac = Pattern.compile("[(]");
			Matcher mpOpenBrac = pOpenBrac.matcher(refTable[i]);
						
			while(mpOpenBrac.find()) {
				mpOpenBrac.appendReplacement(sb, "\\\\(");
				match = true;
			}
									
			mpOpenBrac.appendTail(sb);
						
			if(match) {
				refTable[i] = sb.toString().trim();
			}		
		}
		
		// Substitute ) with \\)
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
						
			Pattern pClosedBrac = Pattern.compile("[)]");
			Matcher mpClosedBrac = pClosedBrac.matcher(refTable[i]);
								
			while(mpClosedBrac.find()) {
				mpClosedBrac.appendReplacement(sb, "\\\\)");
				match = true;
			}
											
			mpClosedBrac.appendTail(sb);
								
			if(match) {
				refTable[i] = sb.toString().trim();
			}		
		}		
		
		String[] tempRefInfoTable = new String[refSize];
		
		// Extraction :: Author
		for(int i=0; i<refSize; i++) {
			if(refTable[i].matches(refPattern1+".*")) {
				match = false;
				sb = new StringBuffer();
					
				Pattern pp = Pattern.compile("\\([1|2]\\d{3}[a-z]?\\).*");
				Matcher mpp = pp.matcher(numRefInfoTable[i][0]);
							
				while(mpp.find()) {
					mpp.appendReplacement(sb, "");
					match = true;
				}
										
				mpp.appendTail(sb);
							
				if(match) {
					numRefInfoTable[i][3] = sb.toString().trim();
					System.out.println(numRefInfoTable[i][1]+"-th author(Type#1): "+numRefInfoTable[i][3]);
				}	
				else {
					numRefInfoTable[i][3] = "";
				}
			}
			else {
				Pattern pa = Pattern.compile(patternAuthor);
				Matcher mpa = pa.matcher(numRefInfoTable[i][0]);
				if(mpa.find()) {
					numRefInfoTable[i][3] = numRefInfoTable[i][0].substring(mpa.start(), mpa.end()).trim();
					System.out.println(numRefInfoTable[i][1]+"-th author: "+numRefInfoTable[i][3]);
				}
				else {
					numRefInfoTable[i][3] = "";
					System.out.println(numRefInfoTable[i][1]+"-th author: "+numRefInfoTable[i][3]);
				}	
			}	
		}
		System.out.println("****************************");
		
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pp = Pattern.compile("\\+");
			Matcher mpp = pp.matcher(numRefInfoTable[i][3]);
						
			while(mpp.find()) {
				mpp.appendReplacement(sb, "\\\\+");
				match = true;
			}
									
			mpp.appendTail(sb);
						
			if(match) {
				numRefInfoTable[i][3] = sb.toString().trim();
			}		
		}
		
		// Substitute ( with \\(
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pOpenBrac = Pattern.compile("[(]");
			Matcher mpOpenBrac = pOpenBrac.matcher(numRefInfoTable[i][3]);
						
			while(mpOpenBrac.find()) {
				mpOpenBrac.appendReplacement(sb, "\\\\(");
				match = true;
			}
									
			mpOpenBrac.appendTail(sb);
						
			if(match) {
				numRefInfoTable[i][3] = sb.toString().trim();
			}		
		}
		
		// Substitute ) with \\)
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
						
			Pattern pClosedBrac = Pattern.compile("[)]");
			Matcher mpClosedBrac = pClosedBrac.matcher(numRefInfoTable[i][3]);
								
			while(mpClosedBrac.find()) {
				mpClosedBrac.appendReplacement(sb, "\\\\)");
				match = true;
			}
											
			mpClosedBrac.appendTail(sb);
								
			if(match) {
				numRefInfoTable[i][3] = sb.toString().trim();
			}		
		}
		
		// Extraction :: Author + (Year) + Title
		for(int i=0; i<refSize; i++) {
			if(refTable[i].matches(refPattern1+".*")) {
				Pattern pa = Pattern.compile(".*"+numRefInfoTable[i][3]+"\\s*(\\([1|2]\\d{3}[a-z]?\\))?\\s*"+patternTitle);
				Matcher mpa = pa.matcher(numRefInfoTable[i][0]);
				if(mpa.find()) {
					tempRefInfoTable[i] = numRefInfoTable[i][0].substring(mpa.start(), mpa.end()).trim();
					System.out.println(numRefInfoTable[i][1]+"-th author and title(Type#1): "+tempRefInfoTable[i]);
				}
				else {
					tempRefInfoTable[i] = refTable[i];
					System.out.println(numRefInfoTable[i][1]+"-th author and title(Type#1): "+tempRefInfoTable[i]);
				}
			}
			else {
				Pattern pa = Pattern.compile(numRefInfoTable[i][3]+patternTitle);
				Matcher mpa = pa.matcher(numRefInfoTable[i][0]);
				if(mpa.find()) {
					tempRefInfoTable[i] = numRefInfoTable[i][0].substring(mpa.start(), mpa.end()).trim();
					System.out.println(numRefInfoTable[i][1]+"-th author and title: "+tempRefInfoTable[i]);
				}
				else {
					tempRefInfoTable[i] = refTable[i];
					System.out.println(numRefInfoTable[i][1]+"-th author and title: "+tempRefInfoTable[i]);
				}
			}
		}
		System.out.println("****************************");
					
		// Extraction :: Title
		for(int i=0; i<refSize; i++) {
			if(refTable[i].matches(refPattern1+".*")) {
				match = false;
				sb = new StringBuffer();
					
				Pattern pt = Pattern.compile(".*"+numRefInfoTable[i][3]+"\\s*\\([1|2]\\d{3}[a-z]?\\)\\s*[.|,]?");
				Matcher mpt = pt.matcher(tempRefInfoTable[i]);
							
				while(mpt.find()) {
					mpt.appendReplacement(sb, "");
					match = true;
				}
										
				mpt.appendTail(sb);
							
				if(match) {
					numRefInfoTable[i][2] = sb.toString().trim();
					System.out.println(numRefInfoTable[i][1]+"-th title(Type#1): "+numRefInfoTable[i][2]);
				}	
			}
			else {
				match = false;
				sb = new StringBuffer();
					
				Pattern pt = Pattern.compile(numRefInfoTable[i][3]);
				Matcher mpt = pt.matcher(tempRefInfoTable[i]);
							
				if(mpt.find()) {
					mpt.appendReplacement(sb, "");
					match = true;
				}
										
				mpt.appendTail(sb);
							
				if(match) {
					numRefInfoTable[i][2] = sb.toString().trim();
					System.out.println(numRefInfoTable[i][1]+"-th title: "+numRefInfoTable[i][2]);
				}	
			}
		}
		System.out.println("****************************");
				
		// Extraction :: Year
		for(int i=0; i<refSize; i++) {
			Pattern py = Pattern.compile(patternYear);
			Matcher mpy = py.matcher(numRefInfoTable[i][0]);
			if(mpy.find()) {
				numRefInfoTable[i][4] = numRefInfoTable[i][0].substring(mpy.start(), mpy.end()).trim();
				System.out.println(numRefInfoTable[i][1]+"-th year: "+numRefInfoTable[i][4]);
			}
			else {
				numRefInfoTable[i][4] = "";
				System.out.println(numRefInfoTable[i][1]+"-th year: "+numRefInfoTable[i][4]);
			}
		}	
		System.out.println("****************************");
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[p|P][p|P][.|,].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[I][E][E][E].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}	
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("([U][K]:\\s*)?[S|s][p][r][i][n][g][e][r]-[V|v][e][r][l][e][g].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[J|j][o][h][n]\\s*[w|W][i][l][e][y]\\s*&\\s*[s|S][o][n][s].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("C[a][m][b][r][i][d][g][e].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("([I|i][N|n]\\s*)?[P|p][r|R][o|O][c|C]?[s|S][\\s|.|,].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}		
		
		/*
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("([I|i][N|n]\\s*)?[P|p][r|R][o|O][c|C][e|E][e|E][d|D]\\-?[i|I][n|N][g|G][s|S]?.*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}*/
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[e|E][d|D][i|I][t|T][o|O][r|R][s|S]?.?");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		/*
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("\\s[v|V][o][l]\\.");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}*/
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[S][I][G][M][O][D].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[S][I][G][C][H][I].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[1|2]\\d{3}[a-z]?\\s*\\-\\s*[1|2]\\d{3}[a-z]?");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}		
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[U][R][L].*");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}		
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[1|2][0|8|9]\\d{2}[a-z]?");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
					
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[“|”|\"|\\\\|?|\\]|(|)|,|.|:|;|\\-]");
			Matcher mpt = pt.matcher(numRefInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][2] = sb.toString().trim();
			}			
		}
			
		int tempRefSize=refSize;
		for(int i=0; i<tempRefSize; i++) {
			if(numRefInfoTable[i][2].matches("[1|2]\\d{3}[a-z]?")) {
				numRefInfoTable[i][0] = "";
				numRefInfoTable[i][1] = "";
				numRefInfoTable[i][2] = "";
				numRefInfoTable[i][3] = "";
				numRefInfoTable[i][4] = "";
				numRefInfoTable[i][5] = "";
				for(int j=i+1; j<tempRefSize; j++) {
					numRefInfoTable[j-1][0] = numRefInfoTable[j][0];
					numRefInfoTable[j-1][1] = numRefInfoTable[j][1];
					numRefInfoTable[j-1][2] = numRefInfoTable[j][2];
					numRefInfoTable[j-1][3] = numRefInfoTable[j][3];
					numRefInfoTable[j-1][4] = numRefInfoTable[j][4];
					numRefInfoTable[j-1][5] = numRefInfoTable[j][5];
				}
				refSize--;
			}			
		}
		
		tempRefSize=refSize;
		for(int i=0; i<tempRefSize; i++) {
			if(numRefInfoTable[i][2].matches("") || numRefInfoTable[i][2].matches("[1|2]\\d{3}[a-z]?") || numRefInfoTable[i][2].matches("[e|E][t|T]\\s*[a|A][l|L]") || numRefInfoTable[i][2].matches("[A][C][M][P][r][e][s][s]") || numRefInfoTable[i][2].matches("[V][o][l]")) {
				numRefInfoTable[i][2] = numRefInfoTable[i][3];
			}			
		}
			
		// Extraction :: Author 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pa = Pattern.compile("[:|?]");
			Matcher mpa = pa.matcher(numRefInfoTable[i][3]);
				
			while(mpa.find()) {
				mpa.appendReplacement(sb, " ");
				match = true;
			}
						
			mpa.appendTail(sb);
				
			if(match) {
				numRefInfoTable[i][3] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Year 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
					
			Pattern py = Pattern.compile("[.|(|)|,|;]");
			Matcher mpy = py.matcher(numRefInfoTable[i][4]);
						
			while(mpy.find()) {
				mpy.appendReplacement(sb, " ");
				match = true;
			}
								
			mpy.appendTail(sb);
						
			if(match) {
				numRefInfoTable[i][4] = sb.toString().trim();
			}			
		}		
		
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("body.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    		  
	    	OuterLoop:
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			  		    
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S].?\\s*") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R].*") || temp.matches(".*S[u|U][g|G][g|G][e|E][s|S][t|T][e|E][d|D]\\s*[R|r][e|E][a|A][d|D][i|I][n|N][g|G].?\\s*")) {
		    			break OuterLoop;
		    		}
		    		fileBw.write(temp);
		    		fileBw.write(" ");
		    		//fileBw.newLine();
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
		
		// Citation Extraction from Body
		System.out.println("*** Citation Extraction from Body ***");
		try {
			fr = new FileReader("body.txt");
	    	fileBr = new BufferedReader(fr);
	    			    	    	
	    	str = "";
	    	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp="";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		m = p.matcher(temp);
		    		while(m.find()) {
		    			tempTable[tempSize] = temp.substring(m.start()+1, m.end()-1).trim();
		    			System.out.println((tempSize+1)+"-th element => "+tempTable[tempSize]);
		    			tempSize++;
		    		}
		    	}
	    	}
	    				    	
	    	fileBr.close();
	    	fr.close();
	    } catch(Exception e) {
			System.out.println(e);  
		}
		System.out.println("****************************");
		
		System.out.println("*** Citation Analysis ***");
		
		// Case#1. Single Reference Number
		// Format: [N]
		String reg1CaseNum = "[1-9]\\d*";
		// Case#2. Multiple Reference Number - Type1
		// Format: [N1, N2, ..., Nn]
		String reg2CaseNum = "[1-9]\\d*\\s*,\\s*([1-9]\\d*\\,?\\s?)+";
		// Case#3. Multiple Reference Number - Type2
		// Format: [N1], [N2], ..., [Nn] or [N1] [N2] ... [Nn]
		String reg3CaseNum = "[1-9]\\d*\\]\\s*,?\\s*(\\[[1-9]\\d*\\]?\\s*,?\\s*)+";		
		// Case#4. Multiple Reference Number - Type3
		// Format: [Np, Nn-Nm, Nq]
		String reg4CaseNum = "([1-9]\\d*\\s*,\\s*)?[1-9]\\d*\\s*[\\-|\\~]\\s*[1-9]\\d*(\\s*,\\s*[1-9]\\d*)?";
		String reg4CaseNumExcep = "[1-9]\\d{0,2}\\s?[\\-|\\~]\\s?[1-9]\\d{0,2}";
		
		for(int i=0; i<tempSize; i++) {
			// Case#1. Single Reference Number
			// Format: [N]
			if(tempTable[i].matches(reg1CaseNum)) {
				for(int j=0; j<refSize; j++) {
					if(tempTable[i].compareToIgnoreCase(numRefInfoTable[j][1])==0) {
						numRefInfoTable[j][5] = Double.toString(Double.parseDouble(numRefInfoTable[j][5])+1.0);
					}
				}
			}
			
			// Case#2. Multiple Reference Number - Type1
			// Format: [N1, N2, ..., Nn]
			if(tempTable[i].matches(reg2CaseNum)) {
				System.out.println("===> "+tempTable[i]);
				st = new StringTokenizer(tempTable[i], ",");
				
				weight = 0;
				String[] tempCase2Num = new String[st.countTokens()];
				int tempIndex = 0;
				while(st.hasMoreTokens()) {
					tempCase2Num[tempIndex] = st.nextToken().trim();
					System.out.println("===> "+tempCase2Num[tempIndex]);
					weight++;
					tempIndex++;
				}
				
				for(int j=0; j<tempIndex; j++) {
					for(int k=0; k<refSize; k++) {
						if(tempCase2Num[j].compareToIgnoreCase(numRefInfoTable[k][1])==0) {
							numRefInfoTable[k][5] = Double.toString(Double.parseDouble(numRefInfoTable[k][5])+1/(double)weight);
						}
					}
				}
			}
			
			// Case#3. Multiple Reference Number - Type2
			// Format: [N1], [N2], ..., [Nn] or [N1] [N2] ... [Nn]
			if(tempTable[i].matches(reg3CaseNum)) {
				System.out.println("===> "+tempTable[i]);
				st = new StringTokenizer(tempTable[i], "], [");
				
				weight = 0;
				String[] tempCase3Num = new String[st.countTokens()];
				int tempIndex = 0;
				while(st.hasMoreTokens()) {
					tempCase3Num[tempIndex] = st.nextToken().trim();
					System.out.println("===> "+tempCase3Num[tempIndex]);
					weight++;
					tempIndex++;
				}
				
				for(int j=0; j<tempIndex; j++) {
					for(int k=0; k<refSize; k++) {
						if(tempCase3Num[j].compareToIgnoreCase(numRefInfoTable[k][1])==0) {
							numRefInfoTable[k][5] = Double.toString(Double.parseDouble(numRefInfoTable[k][5])+1/(double)weight);
						}
					}
				}
			}
			
			// Case#4. Multiple Reference Number - Type3
			// Format: [Np, Nn-Nm, Nq]
			if(tempTable[i].matches(reg4CaseNum)) {
				System.out.println("===> "+tempTable[i]);
				String[] bufferTable = new String[MEMORY_SIZE];
				int bufferSize = 0;
				
				st = new StringTokenizer(tempTable[i], ",");
				
				String[] tempNum = new String[st.countTokens()];
				int tempIndex = 0;
				while(st.hasMoreTokens()) {
					tempNum[tempIndex] = st.nextToken().trim();
					System.out.println("===> "+tempNum[tempIndex]);
					tempIndex++;
				}
				
				weight = 0;
				for(int j=0; j<tempIndex; j++) {
					
					if(tempNum[j].matches(reg4CaseNumExcep)) {
										
						st1 = new StringTokenizer(tempNum[j], "[-|~]");
						
						String[] number = new String[st1.countTokens()];
						String startNum;
						String endNum;
						int numIndex=0;			
						while(st1.hasMoreTokens()) {
							number[numIndex] = st1.nextToken().trim();
							System.out.println("==> No.: "+number[numIndex]);
							numIndex++;
						}
						
						startNum = number[0];
						endNum = number[1];
																	
						for(int k=Integer.parseInt(startNum); k<=Integer.parseInt(endNum); k++) {
							bufferTable[bufferSize] = Integer.toString(k);
							System.out.println("Number Separation: "+bufferTable[bufferSize]);
							weight++;
							bufferSize++;							
						}							
					}
					else {
						bufferTable[bufferSize] = tempNum[j];
						weight++;
						bufferSize++;						
					}
				}
								
				for(int j=0; j<bufferSize; j++) {
					for(int k=0; k<refSize; k++) {
						if(bufferTable[j].compareToIgnoreCase(numRefInfoTable[k][1])==0) {
							numRefInfoTable[k][5] = Double.toString(Double.parseDouble(numRefInfoTable[k][5])+1/(double)weight);
						}
					}
				}			
			}		
		}
		System.out.println("****************************");
		
		NumberShortNameYearBasedDescSort(numRefInfoTable, refSize);
		
		System.out.println("*** Final Citation Results ***");
		// Floating-point number :: Round up at 3th position
		for(int i=0; i<refSize; i++) {
			double finalCount = Double.parseDouble(String.format("%.3f", Double.parseDouble(numRefInfoTable[i][5])));
			numRefInfoTable[i][5] = Double.toString(finalCount);
			System.out.println(numRefInfoTable[i][1]+"-th ref. paper: "+numRefInfoTable[i][5]);
		}
		
		/*
		// Citation results :: Process to store the Hash table
		for(int i=0; i<refSize; i++) {
			citationResultsStorageProcess(numRefInfoTable[i][1], Double.parseDouble(numRefInfoTable[i][5]));
		}
		// Citation results :: Check for citation results using Hash table
		System.out.println("*** Final Citation Results using Hash table ***");
		citationResultsView();
		*/
		
		// Eliminate Non-PCDATA
		for(int i=0; i<6; i++) {
			
			// Non-PCDATA : &
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType1);
				Matcher mNonPCDATA = nonPCDATA.matcher(numRefInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&amp;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					numRefInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : <
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType2);
				Matcher mNonPCDATA = nonPCDATA.matcher(numRefInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&lt;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					numRefInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : >
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType3);
				Matcher mNonPCDATA = nonPCDATA.matcher(numRefInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&gt;");
					match = true;
				}
						
				mNonPCDATA.appendTail(sb);
					
				if(match) {
					numRefInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : "
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType4);
				Matcher mNonPCDATA = nonPCDATA.matcher(numRefInfoTable[j][i]);
					
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&quot;");
					match = true;
				}
								
				mNonPCDATA.appendTail(sb);
						
				if(match) {
					numRefInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : '
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
					
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType5);
				Matcher mNonPCDATA = nonPCDATA.matcher(numRefInfoTable[j][i]);
							
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&apos;");
					match = true;
				}
										
				mNonPCDATA.appendTail(sb);
							
				if(match) {
					numRefInfoTable[j][i] = sb.toString().trim();
				}			
			}			
		}		
		
		try {
			fw = new FileWriter("citationResults.xml");
			fileBw = new BufferedWriter(fw);
			
			String xmlPrologInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
			String xslStyleSheetInfo = "\n<?xml-stylesheet type=\"text/xsl\" href=\"NumberCitationResultsView.xsl\"?>\n\n";
			fileBw.write(xmlPrologInfo);
			fileBw.write(xslStyleSheetInfo);
			fileBw.write("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"./citationResultsSchema.xsd\">\n");
			for(int i=0; i<refSize; i++) {
				fileBw.write("<refInfo>\n");
				fileBw.write("<reference>"); fileBw.write(numRefInfoTable[i][0]); fileBw.write("</reference>\n");
				fileBw.write("<refNumber>"); fileBw.write(numRefInfoTable[i][1]); fileBw.write("</refNumber>\n");
				fileBw.write("<refTitle>"); fileBw.write(numRefInfoTable[i][2]); fileBw.write("</refTitle>\n");
				fileBw.write("<refAuthor>"); fileBw.write(numRefInfoTable[i][3]); fileBw.write("</refAuthor>\n");
				fileBw.write("<refYear>"); fileBw.write(numRefInfoTable[i][4]); fileBw.write("</refYear>\n");
				fileBw.write("<citationCount>"); fileBw.write(numRefInfoTable[i][5]); fileBw.write("</citationCount>\n");
				fileBw.write("</refInfo>\n\n");
			}
			fileBw.write("</root>");
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}	
	}
	
	public void NumberShortNameYearBasedDescSort(String[][] arr, int n) {
		
		String temp;
		String count;
		
		for(int i=0; i<n-1; i++)
		{
			for(int j=0; j<n-i-1; j++)
			{
				if(Double.parseDouble(arr[j][5]) < Double.parseDouble(arr[j+1][5])) {
					temp = arr[j][0];
					arr[j][0] = arr[j+1][0];
					arr[j+1][0] = temp;
					
					temp = arr[j][1];
					arr[j][1] = arr[j+1][1];
					arr[j+1][1] = temp;
					
					temp = arr[j][2];
					arr[j][2] = arr[j+1][2];
					arr[j+1][2] = temp;
					
					temp = arr[j][3];
					arr[j][3] = arr[j+1][3];
					arr[j+1][3] = temp;
					
					temp = arr[j][4];
					arr[j][4] = arr[j+1][4];
					arr[j+1][4] = temp;
					
					count = arr[j][5];
					arr[j][5] = arr[j+1][5];
					arr[j+1][5] = count;
				}
			}
		}
	}
		
	/****************************************************************************
	 * Method Name : NameYearBasedCitationCalculation
	 * Purpose     : Calculate Name-Year based citation 
	 * Parameters  : Text file converted from PDF file using PDF parser
	 * Return      : None
	 ****************************************************************************/ 
	
	public void nameYearBasedCitationCalculation(String text) {
		String str;	
		String temp;
				
		Pattern p;
		Matcher m;
		String[] tempTable = new String[MEMORY_SIZE];
				
		String patternType1 = "[\\(|\\[][A-Z][a-z|A-Z|\\s|\\-|.|,|’|&|;]+\\s*[1|2]\\d{3}[a-z]?([,|;]?\\s*[1|2]\\d{3}[a-z]?)*([,|;]?\\s*[A-Z][a-z|A-Z|\\-|\\s|.|,|’|&|;]+\\s?[1|2]\\d{3}[a-z]?([;|,]?\\s*[1|2]\\d{3}[a-z]?)*)*\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?[\\)|\\]]";
		String patternType2 = "([A-Z]\\s?[a-z|A-Z|\\-]+,\\s*)*[A-Z]\\s?[a-z|A-Z|’|\\-]+[,|\\s]\\s*(\\s?[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|’]+\\s*)?\\s*([e|E][t|T].{1,2}[a|A][l|L]\\.?,?)?\\s?,?\\s?[\\(|\\[][1|2]\\d{3}[a-z]?(\\s?[;|,]\\s?[1|2]\\d{3}[a-z]?)*\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?\\)|([A-Z]\\s?[a-z|A-Z|\\-]+,\\s?)*[A-Z][a-z|A-Z|’]+,?\\s?(\\s?&\\s?[|A-Z|a-z|\\s][a-z|A-Z|’]+\\s?)?\\s?([e|E][t|T].{1,2}[a|A][l|L]\\.?,?)?\\s?\\([1|2]\\d{3}[a-z]?(\\s?[;|,]\\s?[1|2]\\d{3}[a-z]?)*\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?[\\)|\\]]";
								
		// Pattern Type 1: (Name, Year)		
		// Case1: A Work by One Author
		String reg1Case = "[A-Z][A-Z|a-z|\\-|\\s|’]+[,|\\s]\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?(\\-\\s*[A-Z][a-z]+\\s?)?";
		
		// Case2: Two or More Works by the Same Author in the Same Year / Organization as an Author - Two or More Works by the Same Organization in the Same Year
		String reg2Case = "[A-Z]\\s?[A-Z|a-z|\\-|’]+[,|\\s]\\s*[1|2]\\d{3}[a-z][,|;]\\s*[1|2]\\d{3}[a-z]\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case3: A Work by Two Authors - "&" Type
		String reg3Case = "[A-Z][A-Z|a-z|\\-|\\s|’]+,?\\s*&\\s*[A-Z|a-z][A-Z|a-z|\\s|\\-|’]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case4: A Work by Two Authors - "and" Type
		String reg4Case = "[A-Z]\\s?[A-Z|a-z|\\-|’]+,?\\s[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-|’]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case5: A Work by Three to Five Authors - "&" Type 
		String reg5Case = "([A-Z]\\s?[A-Z|a-z|\\-|\\s|’]+,\\s*){1,5}[A-Z]\\s?[A-Z|a-z|\\-|’]+[,|\\s]\\s*&\\s*[A-Z|a-z][A-Z|a-z|\\s|’]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case6: A Work by Three to Five Authors - "and" Type 
		String reg6Case = "([A-Z]\\s?[A-Z|a-z|\\-|’]+,\\s?){1,3}[A-Z]\\s?[A-Z|a-z|\\-|’]+[,|\\s]\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|’]+,?\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case7: Six or More Authors
		String reg7Case = "([A-Z]\\s?[a-z|A-Z|\\-|’]+,\\s?)*[A-Z|a-z]\\s?[A-Z|a-z|\\-|’]+[,|\\s]\\s?[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case8: Two or More Works by One Author in the Different Year
		String reg8Case = "[A-Z|a-z][A-Z|a-z|\\-|\\s|’]+[,|\\s]\\s?[1|2]\\d{3}[,|;]\\s?([1|2]\\d{3},?\\s?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case9: Organization as an Author 
		String reg9Case = "[A-Z]+;\\s?[A-Z|a-z][a-z|A-Z|\\-|\\s|’]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case10: Two or More Works by Two to Five Authors in the Same Year / Two or More Works by Two Author in the Different Year - "&" Type
		String reg10Case = "([A-Z]\\s?[A-Z|a-z|\\-|’]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case11: Two or More Works by Two to Five Authors in the Same Year / Two or More Works by Two Author in the Different Year - "and" Type
		String reg11Case = "([A-Z]\\s?[A-Z|a-z|\\-|’]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-|’]+\\s*[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|’|\\s]+,?\\s?\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
			
		// Case12: Two or More Works in the Same Parentheses
		//String reg12Case = "([A-Z][A-Z|a-z|\\-|\\s]+[,|\\s]\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+,\\s?[1|2]\\d{3}[a-z],\\s*[1|2]\\d{3}[a-z]\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-|\\s]+&\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z|a-z][A-Z|a-z|\\-|\\s]+,\\s?){1,5}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][A-Z|a-z|\\-]+,\\s?){1,3}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][a-z|A-Z|\\-]+,\\s?)*[A-Z|a-z][A-Z|a-z|\\-]+[,|\\s]\\s?[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z|a-z][A-Z|a-z|\\-|\\s]+,\\s?[1|2]\\d{3},\\s?([1|2]\\d{3},?\\s?)+\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z]+;\\s?[A-Z|a-z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?)\\s*[;|,]\\s*(([A-Z][A-Z|a-z|\\-|\\s]+[,|\\s]\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+,\\s?[1|2]\\d{3}[a-z],\\s*[1|2]\\d{3}[a-z]\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-|\\s]+&\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z|a-z][A-Z|a-z|\\-|\\s]+,\\s?){1,5}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][A-Z|a-z|\\-]+,\\s?){1,3}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][a-z|A-Z|\\-]+,\\s?)*[A-Z|a-z][A-Z|a-z|\\-]+[,|\\s]\\s?[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z|a-z][A-Z|a-z|\\-|\\s]+,\\s?[1|2]\\d{3},\\s?([1|2]\\d{3},?\\s?)+\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z]+;\\s?[A-Z|a-z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[a-z]+\\.?\\s?(\\-?\\d{1,4}\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?)[;|,]?\\s*)+";
		String reg12Case = "([A-Z][A-Z|a-z|\\-|\\s|’]+[,|\\s]\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?(\\-\\s*[A-Z][a-z]+\\s?)?|[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[1|2]\\d{3}[a-z][,|;]\\s*[1|2]\\d{3}[a-z]\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-|\\s]+&\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-|\\s]+,\\s?){1,5}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][A-Z|a-z|\\-]+,\\s?){1,3}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][a-z|A-Z|\\-]+,\\s?)*[A-Z|a-z][A-Z|a-z|\\-]+[,|\\s]\\s?[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z|a-z][A-Z|a-z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[,|;]\\s?([1|2]\\d{3},?\\s?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z]+;\\s?[A-Z|a-z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-]+\\s*[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|’|\\s]+,?\\s?\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?)[;|,]\\s*(([A-Z][A-Z|a-z|\\-|\\s]+[,|\\s]\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?(\\-\\s*[A-Z][a-z]+\\s?)?|[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[1|2]\\d{3}[a-z][,|;]\\s*[1|2]\\d{3}[a-z]\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-|\\s]+&\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z][A-Z|a-z|\\-]+\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-|\\s]+,\\s?){1,5}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][A-Z|a-z|\\-]+,\\s?){1,3}[A-Z][A-Z|a-z|\\-]+[,|\\s]\\s?[a|A][n|N][d|D]\\s?[A-Z|a-z][A-Z|a-z|\\s]+,?\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z][a-z|A-Z|\\-]+,\\s?)*[A-Z|a-z][A-Z|a-z|\\-]+[,|\\s]\\s?[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z|a-z][A-Z|a-z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[,|;]\\s?([1|2]\\d{3},?\\s?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|[A-Z]+;\\s?[A-Z|a-z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-]+[,|\\s]\\s?&\\s?[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+[,|\\s]\\s?[1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?|([A-Z]\\s?[A-Z|a-z|\\-]+,\\s?){0,3}[A-Z][a-z|A-Z|’|\\-]+\\s*[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|’|\\s]+,?\\s?\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?)[;|,]?\\s*)+";
		
		// Pattern Type 2: Name (Year)				
		// Case1: A Work by One Author
		String reg13Case = "([A-Z][a-z|A-Z|’|\\-]+\\s?)+,?\\s*\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case2: Two or More Works by the Same Author in the Same Year / Two or More Works by One Author in the Different Year
		String reg14Case = "[A-Z][a-z|A-Z|’|\\-]+,?\\s*\\([1|2]\\d{3}[a-z]?([;|,]\\s*[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case3: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "and" Type
		String reg15Case = "[A-Z][a-z|A-Z|’|\\-]+,?\\s*[a|A][n|N][d|D],?\\s?[A-Z|a-z][a-z|A-Z|’|\\s]+,?\\s*\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case4: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "&" Type
		String reg16Case = "[A-Z][a-z|A-Z|’|\\-|\\s]+&\\s*[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case5: A Work by Two Authors - "and" Type
		String reg17Case = "[A-Z][a-z|A-Z|’|\\-]+,?\\s*[a|A][n|N][d|D],?\\s*[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+,?\\s?\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case6: A Work by Two Authors - "&" Type
		String reg18Case = "[A-Z][a-z|A-Z|’|\\-|\\s]+,?\\s*&,?\\s*[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+,?\\s*\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case7: A Work by Three to Five Authors - "&" Type 
		String reg19Case = "([A-Z][A-Z|a-z|\\-|’]+,\\s*){1,3}([A-Z|a-z][A-Z|a-z|\\-|\\s]+,?\\s*)&\\s*[A-Z|a-z|\\s][A-Z|a-z|\\s]+,?\\s*\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case8: A Work by Three to Five Authors - "and" Type 
		String reg20Case = "([A-Z][A-Z|a-z|\\-|’]+,?\\s*){2,4}[a|A][n|N][d|D],?\\s*[A-Z|a-z][A-Z|a-z|\\s|\\-]+,?\\s*\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";	
		
		// Case9: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year - "&" Type
		String reg21Case = "([A-Z][A-Z|a-z|\\-|\\s|’]+,?\\s*){2,4}&,?\\s*[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+,?\\s*\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case10: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year - "and" Type
		String reg22Case = "([A-Z][A-Z|a-z|\\-]+,?\\s*){2,4}[a|A][n|N][d|D],?\\s*[A-Z|a-z][a-z|A-Z|’|\\s|\\-]+,?\\s*\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case11: Six or More Authors
		String reg23Case = "[A-Z][a-z|A-Z|’|\\-]+,?\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*\\([1|2]\\d{3}[a-z]?\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		// Case12: Two or More Works by Six or More Authors	
		String reg24Case = "[A-Z][a-z|A-Z|’|\\-]+\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?,?\\s*\\([1|2]\\d{3}[a-z]?([;|,]\\s?[1|2]\\d{3}[a-z]?)+\\s*(,\\s?[A-Z|a-z]+\\.?\\s?(\\-?\\d{1,4}\\.?\\d*\\s?)*([a-z]+)?)?(:\\s*(\\-?\\d{1,4}\\s?)+)?";
		
		String[] tempType1 = new String[MEMORY_SIZE];
		String[] tempType2 = new String[MEMORY_SIZE];
		String[][] nameYear =  new String[MEMORY_SIZE][MEMORY_SIZE];
		String[][] mapTable = new String[MEMORY_SIZE][5];		
		String[][] refNameYear = new String[MEMORY_SIZE][MEMORY_SIZE];
		StringTokenizer st;
		
		int size=0;
		int sizeType1=0;
		int sizeType2=0;
		int sizeMap=0;
		int mapIndex=0;
		int refNameYearIndex=0;
		boolean match;
		StringBuffer sb;
			
		String nonPCDATAType1 = "&";
		String nonPCDATAType2 = "<";
		String nonPCDATAType3 = ">";
		String nonPCDATAType4 = "\"";
		String nonPCDATAType5 = "'";
		
		FileWriter fw = null;
		BufferedWriter fileBw = null;
		FileReader fr = null;
		BufferedReader fileBr = null;
		
		try {
			fw = new FileWriter("sampleIn.txt");
			fileBw = new BufferedWriter(fw);
			
			fileBw.write(text);
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}
						
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("body.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    		    	
	    	OuterLoop:
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken().trim();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S]") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R]?\\s?[e|E]\\s?[s|S]?") || temp.matches("R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]")) {
		    			break OuterLoop;
		    		}
		    			    		
		    		fileBw.write(temp);
		    		fileBw.write(" ");
		       	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
				
		// Pattern Type1
		try {
			fr = new FileReader("body.txt");
		   	fileBr = new BufferedReader(fr);
			    			    	    	
		   	str = "";
		    	
		   	while((str = fileBr.readLine())!=null) {
		   		StringTokenizer buf = new StringTokenizer(str, "\n");
		   		p = Pattern.compile(patternType1);
		   		temp="";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		m = p.matcher(temp);
		    		while(m.find()) {
		    			tempTable[size] = temp.substring(m.start(), m.end()).trim();
		    			System.out.println((size+1)+"-th element => "+tempTable[size]);
		    			size++;
		    		}
		    	}
		   	}
			    		    				    	
		   	fileBr.close();
		   	fr.close();
		} catch(Exception e) {
			   System.out.println(e);  
		}	
						
		// Pattern Type2
		try {
			fr = new FileReader("body.txt");
		   	fileBr = new BufferedReader(fr);
		   			    	    	
		   	str = "";
		   	
		   	while((str = fileBr.readLine())!=null) {
		   		StringTokenizer buf = new StringTokenizer(str, "\n");
		   		p = Pattern.compile(patternType2);
		   		temp="";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		m = p.matcher(temp);
		    		while(m.find()) {
		    			tempTable[size] = temp.substring(m.start(), m.end()).trim();
		    			System.out.println((size+1)+"-th element => "+tempTable[size]);
		    			size++;
		    		}
		    	}
		   	}
			    		    				    	
		   	fileBr.close();
		   	fr.close();
		 } catch(Exception e) {
			System.out.println(e);  
		 }
					
		for(int i=0; i<size; i++) {
			System.out.println((i+1)+"-th element => "+tempTable[i]);
		}
		
		System.out.println("*** References Separation Process ***");		
		
		boolean isRefYear = false;
		String refTemp = "";
		String refYear = "\\([1|2][0|8|9]\\d{2}[a-z]?\\)";
		Pattern pry = Pattern.compile(refYear);
		Matcher mpry;
				
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("references.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    	      	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    		
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S]\\s*\\n?") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R]\\s?[e|E]?\\s?[s|S]?\\s*\\n?") || temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s*\\n?")) {
		    			while((str = fileBr.readLine())!=null) {
		    				buf = new StringTokenizer(str, "\n");
			    	    	
		    				temp = "";		    						    	    				    	    		
			    	    	while(buf.hasMoreTokens()) {
			    	    		temp = buf.nextToken().trim();
			    	    		
			    	    		if(temp.matches("")) {
			    	    			fileBw.newLine();
			    	    			break;
			    	    		}
			    	    		else if(temp.matches("([d|D][e|E]\\s*)?([A-Z][a-z|A-Z|\\-|’]+(\\s*[a-z]+\\s*)?)+,?\\.?\\s?(\\-?[A-Z]\\.,?\\s?)+.*\\(?([A-Z][a-z]+,?\\s*)?([1|2][0|8|9]\\d{2}[a-z]?)?\\)?.*")) {
			    	    			fileBw.newLine();  	    			
			    	    			mpry = pry.matcher(temp);
			    	    			if(mpry.find()) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				isRefYear = true;
			    	    			}
			    	    			else if(temp.matches(".*\\:.*")) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
				    	    		}
			    	    			else {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				break;
			    	    			}		    	    			
			    	    		}			    	    		
			    	    		else if(temp.matches("([a-z]+\\s?)?[A-Z][a-z|A-Z|\\-|’|\\s]+\\.?[,|\\s]?\\s*(\\-?[A-Z][A-Z|a-z]?\\s*[\\.|,]\\s*[,|;]?\\s*)+.*([1|2][0|8|9]\\d{2}[a-z]?)?.*")) {
			    	    			fileBw.newLine();  	    			
			    	    			mpry = pry.matcher(temp);
			    	    			if(mpry.find()) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				isRefYear = true;
			    	    			}
			    	    			else if(temp.matches(".*\\:.*")) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
				    	    		}
			    	    			else {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				break;
			    	    			}		    	    			
			    	    		}
			    	    		else if(temp.matches("(\\-?[A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-|’|\\s]+,\\s*\\(?([A-Z][a-z]+,?\\s*)?([1|2][0|8|9]\\d{2}[a-z]?)?\\)?.*")) {
			    	    			fileBw.newLine();  	    			
			    	    			mpry = pry.matcher(temp);
			    	    			if(mpry.find()) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				isRefYear = true;
			    	    			}
			    	    			else if(temp.matches(".*\\:.*")) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
				    	    		}
			    	    			else {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				break;
			    	    			}		    	    			
			    	    		}	
			    	    		else if(temp.matches("[A-Z][A-Z]+\\.?\\s?\\(?[1|2][0|8|9]\\d{2}[a-z]?\\)?.*")) {
			    	    			fileBw.newLine(); 
			    	    			mpry = pry.matcher(temp);
			    	    			if(mpry.find()) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				isRefYear = true;
			    	    			}
			    	    			else if(temp.matches(".*\\:.*")) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
				    	    		}
			    	    			else {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				break;
			    	    			}		    	    			
			    	    		}			    	    		
			    	    		else {
			    	    			mpry = pry.matcher(temp);
			    	    			if(mpry.find()) {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    				isRefYear = true;
			    	    			}		
			    	    			else {
			    	    				refTemp = refTemp.concat(temp).trim();
			    	    				refTemp = refTemp.concat(" ");
			    	    			}
			    	    		}
			    	    		
			    	    		if(isRefYear) {
			    	    			fileBw.newLine();
			    	    			fileBw.write(refTemp);
			    	    			fileBw.write(" ");
			    	    			refTemp = "";
			    	    			isRefYear = false;
			    	    			//break;
			    	    		}
			    	    		else {			    	    			
			    	    			fileBw.write(refTemp);
			    	    			fileBw.write(" ");
			    	    			refTemp = "";			    	    			
			    	    		}  	    			    	    		
			    	    	}			    	    		
		    			}	    			
		    		}
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
				
		// Ref. Author + Year + Title :: Extraction
		System.out.println("*** References : Author + Year + Title ***");	
		String patternRefSep1 = "([d|D][e|E]\\s*)?[A-Z][a-z|A-Z|\\-|\\s|.|,|&|;|(|)|’|:]+\\s?\\([1|2][0|8|9]\\d{2}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\)\\s*\\.?(\\s?\\)\\.\\s?)?\\s?,?\\s?[\"|“|‘]?[A-Z|a-z|0-9|\\s|:|\\-|?|‘|’|(|)|,|“|”|*|_|+|-|%|'|&|/|#|;]+[,|.|”|\"|’]?([a-z]+\\.([a-z]+\\.)+[a-z|~|/]*\\.)?(\\s*[A-Z][a-z]+\\s\\([A-Z|a-z]+\\.\\),\\s*[A-Z][a-z|A-Z|\\s]+\\.)?";
		String patternRefSep2 = "([d|D][e|E]\\s*)?[A-Z][a-z|A-Z|\\-|\\s|.|,|&|;|(|)|’|:]+\\s?[1|2][0|8|9]\\d{2}[a-z]?\\s*\\.?(\\s?\\)\\.\\s?)?\\s?,?\\s?[\"|“|‘]?[A-Z|a-z|0-9|\\s|:|\\-|?|‘|’|(|)|,|“|”|*|_|+|-|%|'|&|/|#|;]+[,|.|”|\"|’]?";
		
		String patternAuthorType = "((([a-z]+\\s?)?[A-Z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s*(\\-?[A-Z]([A-Z|a-z]\\s?)?[\\.|,|;]\\s*[,|;]?\\s*)+)+((\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*[A-Z][a-z|A-Z|\\-|\\s]+,)?\\s*[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*[A-Z][a-z|A-Z|\\-|\\s]+[\\s|,]\\s*(\\-?[A-Z]\\.(\\s+[A-Z])*[,|;]?\\s*)*)?((\\s*(\\-?[A-Z]\\.[,|;]?\\s*)*[A-Z][a-z|A-Z|\\-|\\s]+,)?\\s*&\\s*[A-Z][a-z|A-Z|\\-|\\s]+[,|\\s]\\s*(\\-?[A-Z]\\.(\\s+[A-Z])*[,|;]?\\s*)*)?|[A-Z][a-z|A-Z|\\-|\\s]+\\.\\s*(\\-?[A-Z]\\.[,|;]?\\s*)+|[A-Z][a-z|A-Z|\\-]+\\s*[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.[,|;]?\\s*([a-z]+\\s+)?)+[A-Z][a-z|A-Z|\\-]+)(\\s*\\(?[E|e][d|D][s|S]?\\.?\\)?\\.?\\s*)?";
		String patternRefSep3 = patternAuthorType+"\\s*([A-Z][0-9].[0-9]:\\s*)?([A-Z][A-Z|a-z|\\-]+:)?\\s*([A|a][n]?\\s+)?[A-Z|a-z][a-z|A-z|\\-]+,?\\s+[A-Z|a-z][a-z|A-Z|\\s|\\-|/|,|:|?|“|”|\"|'|&|;]+[.|,|?]?\\s*([0-9]+[a-z|A-Z]+\\s+)?[A-Z|a-z][a-z|A-Z|0-9|\\s|\\-|;|,|’|:|(|)|.|?|/|“|”|*|_|+|-|%|'|&|#]+[1|2]\\d{3}[a-z]?.?";
			
		Pattern pr1 = Pattern.compile(patternRefSep1);
		Matcher mpr1;
		Pattern pr2 = Pattern.compile(patternRefSep2);
		Matcher mpr2;
		Pattern pr3 = Pattern.compile(patternRefSep3);
		Matcher mpr3;
			
		try {
			fr = new FileReader("references.txt");
	    	fileBr = new BufferedReader(fr);    	
		    	    	
	    	str = "";
	    	      	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken().trim();
		    		
		    		if(temp.matches("")) {
		    			break;
		    		}		    		
		    		
		    		if(temp.matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
		    			mpr1 = pr1.matcher(temp);
			    		if(mpr1.find()) {
			    			mapTable[mapIndex][0] = temp.substring(mpr1.start(), mpr1.end()).trim();
			    			mapTable[mapIndex][4] = "0.0";
			    			System.out.println((mapIndex+1)+"-th ref. paper(type#1): "+mapTable[mapIndex][0]);
			    			mapIndex++;
			    		}	
		    		}
		    		else {
		    			if(temp.matches(".*[1|2]\\d{3}[a-z]?.*")) {
		    				if(temp.matches(patternAuthorType+"\\s*[1|2][0|8|9]\\d{2}[a-z]?.*")) {
			    				mpr2 = pr2.matcher(temp);
					    		if(mpr2.find()) {
					    			mapTable[mapIndex][0] = temp.substring(mpr2.start(), mpr2.end()).trim();
					    			mapTable[mapIndex][4] = "0.0";
					    			System.out.println((mapIndex+1)+"-th ref. paper(type#2): "+mapTable[mapIndex][0]);
					    			mapIndex++;
					    		}	
				    		}	    			
			    			else {
			    				mpr3 = pr3.matcher(temp);
					    		if(mpr3.find()) {
					    			mapTable[mapIndex][0] = temp.substring(mpr3.start(), mpr3.end()).trim();
					    			mapTable[mapIndex][4] = "0.0";
					    			System.out.println((mapIndex+1)+"-th ref. paper(type#3): "+mapTable[mapIndex][0]);
					    			mapIndex++;
					    		}					    		
				    		}	  
		    			}		    			    			
		    		}	    		
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();    	
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
		System.out.println("******************");
		
		// Reference. filtering
		match = false;
		for(int i=0; i<mapIndex; i++) {
			for(int j=i+1; j<mapIndex; j++) {
				if(mapTable[i][0].compareToIgnoreCase(mapTable[j][0])==0) {
					match = true;
					mapTable[j][0] = "";
				}
			}
			
			if(match) {
				mapTable[i][0] = "";
				break;
			}
		}		
	
		for(int i=0; i<mapIndex; i++) {
			if(mapTable[i][0].compareToIgnoreCase("")==0) {
				for(int j=i; j<mapIndex; j++) {
					mapTable[j][0] = mapTable[j+1][0];
				}
				mapIndex--;
			}
		}
		
		for(int i=0; i<mapIndex; i++) {
			System.out.println((i+1)+"-th ref. paper =====> "+mapTable[i][0]);
		}
		
		// Ref. Author + Year Extraction
		System.out.println("*** References : Author + Year ***");
		String[] refNameYearInfo = new String[mapIndex];
		String patternNameYear1 = "([d|D][e|E]\\s*)?[A-Z][a-z|A-Z|\\-|\\s|.|,|&|;|(|)|’|:]+\\s?\\([1|2][0|8|9]\\d{2}[a-z]?\\)\\s?\\.?,?";
		String patternNameYear2 = "([d|D][e|E]\\s*)?[A-Z][a-z|A-Z|\\-|\\s|.|,|&|;|(|)|’|:]+\\s?[1|2][0|8|9]\\d{2}[a-z]?\\s?\\.?,?";
		Pattern prny1 = Pattern.compile(patternNameYear1);
		Matcher mprny1;
		Pattern prny2 = Pattern.compile(patternNameYear2);
		Matcher mprny2;
		
		String patternAuthorTitle = patternAuthorType+"\\s*([A-Z][0-9].[0-9]:\\s*)?([A-Z][A-Z|a-z|\\-]+:\\s*)?([A|a][n]?\\s)?[A-Z|a-z][a-z|A-Z|\\-]+,?\\s+[a-z|A-Z|\\-|/|?|\\s|:|,|?|“|”|\"|'|&|;]+[.|,|?]";
		
		Pattern prnt = Pattern.compile(patternAuthorTitle);
		Matcher mprnt;
		Pattern pa = Pattern.compile(patternAuthorType);
		Matcher mpa;
				
		for(int i=0; i<mapIndex; i++) {
			if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
				mprny1 = prny1.matcher(mapTable[i][0]);
				if(mprny1.find()) {
			    	refNameYearInfo[refNameYearIndex] = mapTable[i][0].substring(mprny1.start(), mprny1.end()).trim();
			    	System.out.println((refNameYearIndex+1)+"-th ref. paper => [author + year]: "+refNameYearInfo[refNameYearIndex]);
			    	refNameYearIndex++;
			    }
				else {
					mprny2 = prny2.matcher(mapTable[i][0]);
    			  	if(mprny2.find()) {
    			   		refNameYearInfo[refNameYearIndex] = mapTable[i][0].substring(mprny2.start(), mprny2.end()).trim();
    			    	System.out.println((refNameYearIndex+1)+"-th ref. paper => [author + year]: "+refNameYearInfo[refNameYearIndex]);
    			    	refNameYearIndex++;
    			  	}
				}
    		}
    		else {
    			if(mapTable[i][0].matches(".*[1|2]\\d{3}[a-z]?.*")) {
    				if(mapTable[i][0].matches(patternAuthorType+"\\s*[1|2][0|8|9]\\d{2}[a-z]?.*")) {
        				mprny2 = prny2.matcher(mapTable[i][0]);
        			  	if(mprny2.find()) {
        			   		refNameYearInfo[refNameYearIndex] = mapTable[i][0].substring(mprny2.start(), mprny2.end()).trim();
        			    	System.out.println((refNameYearIndex+1)+"-th ref. paper => [author + year]: "+refNameYearInfo[refNameYearIndex]);
        			    	refNameYearIndex++;
        			  	}
    	    		}
        			else {
        				mprnt = prnt.matcher(mapTable[i][0]);
        				if(mprnt.find()) {
        			    	refNameYearInfo[refNameYearIndex] = mapTable[i][0].substring(mprnt.start(), mprnt.end()).trim();
        			    	System.out.println((refNameYearIndex+1)+"-th ref. paper => [author + title]: "+refNameYearInfo[refNameYearIndex]);
        			    	refNameYearIndex++;
        			    }	
    	    		}
    			}    			
    		}		
		}	
		System.out.println("******************");
			
		// Ref. Author :: Extraction
		System.out.println("*** References : Author ***");
		for(int i=0; i<refNameYearIndex; i++) {
			if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
				match = false;
				sb = new StringBuffer();
												
				Pattern pAuthor = Pattern.compile("\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\)?\\.?,?\\s?[\"|“|‘]?");
				Matcher mpAuthor = pAuthor.matcher(refNameYearInfo[i]);
														
				while(mpAuthor.find()) {
					mpAuthor.appendReplacement(sb, "");
					match = true;
				}
																			
				mpAuthor.appendTail(sb);
													
				if(match) {
					mapTable[i][2] = sb.toString().trim();
					System.out.println((i+1)+"-th ref. paper's author: "+mapTable[i][2]);
				}	
    		}
			else {
    			if(mapTable[i][0].matches(".*[1|2]\\d{3}[a-z]?.*")) {
    				if(mapTable[i][0].matches(patternAuthorType+"\\s*[1|2][0|8|9]\\d{2}[a-z]?.*")) {
    					match = false;
    					sb = new StringBuffer();
    													
    					Pattern pAuthor = Pattern.compile("[1|2]\\d{3}[a-z]?\\.?,?\\s?[\"|“|‘]?");
    					Matcher mpAuthor = pAuthor.matcher(refNameYearInfo[i]);
    															
    					while(mpAuthor.find()) {
    						mpAuthor.appendReplacement(sb, "");
    						match = true;
    					}
    																				
    					mpAuthor.appendTail(sb);
    														
    					if(match) {
    						mapTable[i][2] = sb.toString().trim();
    						System.out.println((i+1)+"-th ref. paper's author: "+mapTable[i][2]);
    					}	
    	    		}
        			else {
        				mpa = pa.matcher(refNameYearInfo[i]);
        				if(mpa.find()) {
        					mapTable[i][2] = refNameYearInfo[i].substring(mpa.start(), mpa.end()).trim();
        					System.out.println((i+1)+"-th ref. paper's author: "+mapTable[i][2]);
        				}	
    	    		}
    			}    			
    		} 		
		}	
		
		for(int i=0; i<mapIndex; i++) {
			match = false;
			sb = new StringBuffer();
								
			Pattern pTitle = Pattern.compile("\\(");
			Matcher mpTitle = pTitle.matcher(mapTable[i][2]);
					
			while(mpTitle.find()) {
				mpTitle.appendReplacement(sb, "\\\\(");
				match = true;
			}
							
			mpTitle.appendTail(sb);
					
			if(match) {
				mapTable[i][2]= sb.toString().trim();
			}
		}		
		
		for(int i=0; i<mapIndex; i++) {
			match = false;
			sb = new StringBuffer();
								
			Pattern pTitle = Pattern.compile("\\)");
			Matcher mpTitle = pTitle.matcher(mapTable[i][2]);
					
			while(mpTitle.find()) {
				mpTitle.appendReplacement(sb, "\\\\)");
				match = true;
			}
							
			mpTitle.appendTail(sb);
					
			if(match) {
				mapTable[i][2]= sb.toString().trim();
			}
		}		
				
		System.out.println("******************");
		System.out.println("*** References : Year ***");
		String patternRefYear = "[1|2]\\d{3}[a-z]?";
		Pattern py = Pattern.compile(patternRefYear);
		Matcher mpy;
		// Ref. Year :: Extraction
		for(int i=0; i<refNameYearIndex; i++) {
			if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
				mpy = py.matcher(refNameYearInfo[i]);
				if(mpy.find()) {
					mapTable[i][3] = refNameYearInfo[i].substring(mpy.start(), mpy.end()).trim();
					System.out.println((i+1)+"-th ref. paper's year: "+mapTable[i][3]);
				}	
    		}
			else {
    			if(mapTable[i][0].matches(".*[1|2]\\d{3}[a-z]?.*")) {
    				if(mapTable[i][0].matches(patternAuthorType+"\\s*[1|2][0|8|9]\\d{2}[a-z]?.*")) {
    					mpy = py.matcher(refNameYearInfo[i]);
    					if(mpy.find()) {
    						mapTable[i][3] = refNameYearInfo[i].substring(mpy.start(), mpy.end()).trim();
    						System.out.println((i+1)+"-th ref. paper's year: "+mapTable[i][3]);
    					}
    	    		}
        			else {
        				mpy = py.matcher(mapTable[i][0]);
            			if(mpy.find()) {
            				mapTable[i][3] = mapTable[i][0].substring(mpy.start(), mpy.end()).trim();
            				System.out.println((i+1)+"-th ref. paper's year: "+mapTable[i][3]);
            			}	
    	    		}
    			}   			
			}  			
		}	
		System.out.println("******************");
				
		System.out.println("*** Reference Title :: Extraction ***");
		for(int i=0; i<mapIndex; i++) {
			if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
				if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\).*")) {
					match = false;
					sb = new StringBuffer();
											
					Pattern pTitle = Pattern.compile(".*\\("+mapTable[i][3]+"(,\\s+[A-Z][a-z]+/[A-Z][a-z]+)?\\)?\\.?,?\\s?[\"|“|‘]?");
					Matcher mpTitle = pTitle.matcher(mapTable[i][0]);
							
					while(mpTitle.find()) {
						mpTitle.appendReplacement(sb, "");
						match = true;
					}
										
					mpTitle.appendTail(sb);
							
					if(match) {
						mapTable[i][1]= sb.toString().trim();
						System.out.println("==> Title: "+mapTable[i][1]);
					}
				}
				else if(mapTable[i][0].matches(".*\\([1|2]\\d{3}[a-z]?,\\s*[A-Z][a-z|\\-|\\s]+\\s*\\d{0,2}\\).*")) {
					match = false;
					sb = new StringBuffer();
											
					Pattern pTitle = Pattern.compile(".*\\("+mapTable[i][3]+",\\s*[A-Z][a-z|\\-|\\s]+\\s*\\d{0,2}\\)\\.?,?\\s?[\"|“|‘]?");
					Matcher mpTitle = pTitle.matcher(mapTable[i][0]);
							
					while(mpTitle.find()) {
						mpTitle.appendReplacement(sb, "");
						match = true;
					}
										
					mpTitle.appendTail(sb);
							
					if(match) {
						mapTable[i][1]= sb.toString().trim();
						System.out.println("==> Title: "+mapTable[i][1]);
					}
				}				
			}  
			else {
    			if(mapTable[i][0].matches(".*[1|2]\\d{3}[a-z]?.*")) {
    				if(mapTable[i][0].matches(patternAuthorType+"\\s*[1|2][0|8|9]\\d{2}[a-z]?.*")) {
    					match = false;
    					sb = new StringBuffer();
    											
    					Pattern pTitle = Pattern.compile(mapTable[i][2]+".{0,4}"+mapTable[i][3]+"\\.?,?\\s?[\"|“|‘]?");
    					Matcher mpTitle = pTitle.matcher(mapTable[i][0]);
    							
    					while(mpTitle.find()) {
    						mpTitle.appendReplacement(sb, "");
    						match = true;
    					}
    										
    					mpTitle.appendTail(sb);
    							
    					if(match) {
    						mapTable[i][1]= sb.toString().trim();
    						System.out.println((i+1)+"-th title: "+mapTable[i][1]);
    					}
    	    		}
        			else {
        				match = false;
        				sb = new StringBuffer();
        										
        				Pattern pTitle = Pattern.compile(".*"+mapTable[i][2]);
        				Matcher mpTitle = pTitle.matcher(refNameYearInfo[i]);
        						
        				while(mpTitle.find()) {
        					mpTitle.appendReplacement(sb, "");
        					match = true;
        				}
        									
        				mpTitle.appendTail(sb);
        						
        				if(match) {
        					mapTable[i][1]= sb.toString().trim();
        					System.out.println((i+1)+"-th title: "+mapTable[i][1]);
        				}		
    	    		}
    			}   			
			}  		
		}	
		
		// Extraction :: Title 
		for(int i=0; i<mapIndex; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[“|”|\"|\\\\|?|’|.|(|)|/|:]");
			Matcher mpt = pt.matcher(mapTable[i][1]);
						
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
								
			mpt.appendTail(sb);
					
			if(match) {
				mapTable[i][1] = sb.toString().trim();
			}			
		}
				
		for(int i=0; i<mapIndex; i++) {
			// January
			if(mapTable[i][1].matches(".*J[a|A][n|N][u|U][a|A][r|R][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
							
				Pattern p1 = Pattern.compile("J[a|A][n|N][u|U][a|A][r|R][y|Y]");
				Matcher mp1 = p1.matcher(mapTable[i][1]);
									
				while(mp1.find()) {
					mp1.appendReplacement(sb, "");
					match = true;
				}
														
				mp1.appendTail(sb);
										
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// February 
			if(mapTable[i][1].matches(".*F[e|E][b|B][r|R][u|U][a|A][r|R][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
										
				Pattern p2 = Pattern.compile("F[e|E][b|B][r|R][u|U][a|A][r|R][y|Y]");
				Matcher mp2 = p2.matcher(mapTable[i][1]);
												
				while(mp2.find()) {
					mp2.appendReplacement(sb, "");
					match = true;
				}
																	
				mp2.appendTail(sb);
													
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// March 
			if(mapTable[i][1].matches(".*M[a|A][r|R][c|C][h|H].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p3 = Pattern.compile("M[a|A][r|R][c|C][h|H]");
				Matcher mp3 = p3.matcher(mapTable[i][1]);
															
				while(mp3.find()) {
					mp3.appendReplacement(sb, "");
					match = true;
				}
																				
				mp3.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// April
			if(mapTable[i][1].matches(".*A[p|P][r|R][i|I][l|L].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p4 = Pattern.compile("A[p|P][r|R][i|I][l|L]");
				Matcher mp4 = p4.matcher(mapTable[i][1]);
															
				while(mp4.find()) {
					mp4.appendReplacement(sb, "");
					match = true;
				}
																				
				mp4.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// May
			if(mapTable[i][1].matches(".*M[a|A][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p5 = Pattern.compile("M[a|A][y|Y]");
				Matcher mp5 = p5.matcher(mapTable[i][1]);
															
				while(mp5.find()) {
					mp5.appendReplacement(sb, "");
					match = true;
				}
																				
				mp5.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// June
			if(mapTable[i][1].matches(".*J[u|U][n|N][e|E].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p6 = Pattern.compile("J[u|U][n|N][e|E]");
				Matcher mp6 = p6.matcher(mapTable[i][1]);
															
				while(mp6.find()) {
					mp6.appendReplacement(sb, "");
					match = true;
				}
																				
				mp6.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// July
			if(mapTable[i][1].matches(".*J[u|U][l|L][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p7 = Pattern.compile("J[u|U][l|L][y|Y]");
				Matcher mp7 = p7.matcher(mapTable[i][1]);
															
				while(mp7.find()) {
					mp7.appendReplacement(sb, "");
					match = true;
				}
																				
				mp7.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// August
			if(mapTable[i][1].matches(".*A[u|U][g|G][u|U][s|S][t|T].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p8 = Pattern.compile("A[u|U][g|G][u|U][s|S][t|T]");
				Matcher mp8 = p8.matcher(mapTable[i][1]);
															
				while(mp8.find()) {
					mp8.appendReplacement(sb, "");
					match = true;
				}
																				
				mp8.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// September
			if(mapTable[i][1].matches(".*S[e|E][p|P][t|T][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p9 = Pattern.compile("S[e|E][p|P][t|T][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp9 = p9.matcher(mapTable[i][1]);
															
				while(mp9.find()) {
					mp9.appendReplacement(sb, "");
					match = true;
				}
																				
				mp9.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// October
			if(mapTable[i][1].matches(".*O[c|C][t|T][o|O][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p10 = Pattern.compile("O[c|C][t|T][o|O][b|B][e|E][r|R]");
				Matcher mp10 = p10.matcher(mapTable[i][1]);
															
				while(mp10.find()) {
					mp10.appendReplacement(sb, "");
					match = true;
				}
																				
				mp10.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// November
			if(mapTable[i][1].matches(".*N[o|O][v|V][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p11 = Pattern.compile("N[o|O][v|V][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp11 = p11.matcher(mapTable[i][1]);
															
				while(mp11.find()) {
					mp11.appendReplacement(sb, "");
					match = true;
				}
																				
				mp11.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
			
			// December
			if(mapTable[i][1].matches(".*D[e|E][c|C][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p12 = Pattern.compile("D[e|E][c|C][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp12 = p12.matcher(mapTable[i][1]);
															
				while(mp12.find()) {
					mp12.appendReplacement(sb, "");
					match = true;
				}
																				
				mp12.appendTail(sb);
																
				if(match) {
					mapTable[i][1] = sb.toString().trim();
				}	
			}
		}
		
		for(int i=0; i<refNameYearIndex; i++) {
			refNameYearInfo[i] = mapTable[i][2].concat(" ").concat(mapTable[i][3]);
			System.out.println((i+1)+"-th preprocessing mapping table: "+refNameYearInfo[i]);
		}
		
		for(int i=0; i<refNameYearIndex; i++) {
			// January
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*J[a|A][n|N][u|U][a|A][r|R][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
							
				Pattern p1 = Pattern.compile("J[a|A][n|N][u|U][a|A][r|R][y|Y]");
				Matcher mp1 = p1.matcher(refNameYearInfo[i]);
									
				while(mp1.find()) {
					mp1.appendReplacement(sb, "");
					match = true;
				}
														
				mp1.appendTail(sb);
										
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// February 
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*F[e|E][b|B][r|R][u|U][a|A][r|R][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
										
				Pattern p2 = Pattern.compile("F[e|E][b|B][r|R][u|U][a|A][r|R][y|Y]");
				Matcher mp2 = p2.matcher(refNameYearInfo[i]);
												
				while(mp2.find()) {
					mp2.appendReplacement(sb, "");
					match = true;
				}
																	
				mp2.appendTail(sb);
													
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// March 
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*M[a|A][r|R][c|C][h|H].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p3 = Pattern.compile("M[a|A][r|R][c|C][h|H]");
				Matcher mp3 = p3.matcher(refNameYearInfo[i]);
															
				while(mp3.find()) {
					mp3.appendReplacement(sb, "");
					match = true;
				}
																				
				mp3.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// April
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*A[p|P][r|R][i|I][l|L].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p4 = Pattern.compile("A[p|P][r|R][i|I][l|L]");
				Matcher mp4 = p4.matcher(refNameYearInfo[i]);
															
				while(mp4.find()) {
					mp4.appendReplacement(sb, "");
					match = true;
				}
																				
				mp4.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// May
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*M[a|A][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p5 = Pattern.compile("M[a|A][y|Y]");
				Matcher mp5 = p5.matcher(refNameYearInfo[i]);
															
				while(mp5.find()) {
					mp5.appendReplacement(sb, "");
					match = true;
				}
																				
				mp5.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// June
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*J[u|U][n|N][e|E].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p6 = Pattern.compile("J[u|U][n|N][e|E]");
				Matcher mp6 = p6.matcher(refNameYearInfo[i]);
															
				while(mp6.find()) {
					mp6.appendReplacement(sb, "");
					match = true;
				}
																				
				mp6.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// July
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*J[u|U][l|L][y|Y].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p7 = Pattern.compile("J[u|U][l|L][y|Y]");
				Matcher mp7 = p7.matcher(refNameYearInfo[i]);
															
				while(mp7.find()) {
					mp7.appendReplacement(sb, "");
					match = true;
				}
																				
				mp7.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// August
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*A[u|U][g|G][u|U][s|S][t|T].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p8 = Pattern.compile("A[u|U][g|G][u|U][s|S][t|T]");
				Matcher mp8 = p8.matcher(refNameYearInfo[i]);
															
				while(mp8.find()) {
					mp8.appendReplacement(sb, "");
					match = true;
				}
																				
				mp8.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// September
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*S[e|E][p|P][t|T][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p9 = Pattern.compile("S[e|E][p|P][t|T][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp9 = p9.matcher(refNameYearInfo[i]);
															
				while(mp9.find()) {
					mp9.appendReplacement(sb, "");
					match = true;
				}
																				
				mp9.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// October
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*O[c|C][t|T][o|O][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p10 = Pattern.compile("O[c|C][t|T][o|O][b|B][e|E][r|R]");
				Matcher mp10 = p10.matcher(refNameYearInfo[i]);
															
				while(mp10.find()) {
					mp10.appendReplacement(sb, "");
					match = true;
				}
																				
				mp10.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// November
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*N[o|O][v|V][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p11 = Pattern.compile("N[o|O][v|V][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp11 = p11.matcher(refNameYearInfo[i]);
															
				while(mp11.find()) {
					mp11.appendReplacement(sb, "");
					match = true;
				}
																				
				mp11.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			// December
			if(refNameYearInfo[i].matches(".*[1|2]\\d{3}[a-z]?,?\\s*D[e|E][c|C][e|E][m|M][b|B][e|E][r|R].*")) {
				match = false;
				sb = new StringBuffer();
													
				Pattern p12 = Pattern.compile("D[e|E][c|C][e|E][m|M][b|B][e|E][r|R]");
				Matcher mp12 = p12.matcher(refNameYearInfo[i]);
															
				while(mp12.find()) {
					mp12.appendReplacement(sb, "");
					match = true;
				}
																				
				mp12.appendTail(sb);
																
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
		}
		
		System.out.println("*** Mapping Table ***");
		String pName = "[A-Z]\\s?[a-z|A-Z|\\-]+(\\s?[a-z]+)?";
		String pEtAl = "[e|E][t|T]\\.?\\s?[a|A][l|L]";
		String pYear = "[1|2]\\d{3}[a-z]?";
		
		for(int i=0; i<refNameYearIndex; i++) {
			
			if(refNameYearInfo[i].matches(".*\\([E|e][d|D][s|S]\\.?\\).*")) {
				match = false;
				sb = new StringBuffer();
							
				Pattern pEds = Pattern.compile("\\s?\\(?[E|e][d|D][s|S]\\.?\\)?\\s?");
				Matcher mpEds = pEds.matcher(refNameYearInfo[i]);
									
				while(mpEds.find()) {
					mpEds.appendReplacement(sb, "");
					match = true;
				}
														
				mpEds.appendTail(sb);
										
				if(match) {
					refNameYearInfo[i] = sb.toString().trim();
				}	
			}
			
			Pattern rName = Pattern.compile(pName);
			Matcher mrName = rName.matcher(refNameYearInfo[i]);
			int col=0;
			while(mrName.find()) {
				refNameYear[i][col] = refNameYearInfo[i].substring(mrName.start(), mrName.end()).trim();
				col++;
			}
			
			Pattern rEtAl = Pattern.compile(pEtAl);
			Matcher mrEtAl = rEtAl.matcher(refNameYearInfo[i]);
			if(mrEtAl.find()) {
				refNameYear[i][col] = refNameYearInfo[i].substring(mrEtAl.start(), mrEtAl.end()).trim();
				col++;
			}
			
			Pattern rYear = Pattern.compile(pYear);
			Matcher mrYear = rYear.matcher(refNameYearInfo[i]);
			if(mrYear.find()) {
				refNameYear[i][col] = refNameYearInfo[i].substring(mrYear.start(), mrYear.end()).trim();
				col++;
			}
						
			for(int j=0; j<col; j++) {
				
				if(refNameYear[i][j].matches(".*[a|A][n|N][d|D].*")) {
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s?[a|A][n|N][d|D]\\s");
					Matcher mpAnd = pAnd.matcher(refNameYear[i][j]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, "");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						refNameYear[i][j] = sb.toString().trim();
					}	
					
					match = false;
					sb = new StringBuffer();
					
					pAnd = Pattern.compile("\\s[a|A][n|N][d|D]\\s?");
					mpAnd = pAnd.matcher(refNameYear[i][j]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, "");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						refNameYear[i][j] = sb.toString().trim();
					}	
					
					if(refNameYear[i][j].matches(".*[A][N][D].*")) {
						int k=j;
						
						while(true) {
							if(refNameYear[i][k+1].matches(pYear)) {
								refNameYear[i][k] = refNameYear[i][k+1];
								refNameYear[i][k+1] = "";
								break;
							}
							else {
								refNameYear[i][k] = refNameYear[i][k+1];
								k++;
							}						
						}				
					}
				}							
			}		
			
			System.out.print((i+1)+"-th mapping table => ");
			for(int j=0; j<col; j++) {
				System.out.print(refNameYear[i][j]+" ");
			}
			System.out.println();
		}	
		
		System.out.println("\n*** Start Analysis ***");
		
		// Pattern Type#1 :: "(Name, Year)"
		System.out.println("\n*** Pattern Type 1 ***");
		for(int i=0; i<size; i++) {
			Pattern p1 = Pattern.compile(patternType1);
			Matcher m1 = p1.matcher(tempTable[i]);
			if(m1.find()) {
				tempType1[sizeType1] = tempTable[i].substring(m1.start()+1, m1.end()-1).trim();
				System.out.println((sizeType1+1)+"-th element => "+tempType1[sizeType1]);
				sizeType1++;
			}
		}
		// Pattern Type#1 :: "Name (Year)"		
		System.out.println("\n*** Pattern Type 2 ***");
		for(int i=0; i<size; i++) {
			Pattern p2 = Pattern.compile(patternType2);
			Matcher m2 = p2.matcher(tempTable[i]);
			if(m2.find()) {
				tempType2[sizeType2] = tempTable[i].substring(m2.start(), m2.end()-1).trim();
				System.out.println((sizeType2+1)+"-th element => "+tempType2[sizeType2]);
				sizeType2++;
			}
		}
		
		System.out.println("\n*** Citation Data Analysis ***");
		
		// Pattern Type 1
		// Format: "(Name, Year)"
		for(int i=0; i<sizeType1; i++) {
							
			// Case1: A Work by One Author
			// Format: (Name, Year) e.g., (White, 2003) / (Kant 1993)
			if(tempType1[i].matches(reg1Case)) {
				System.out.println("===> "+tempType1[i]);
								
				if(tempType1[i].matches(".*,\\s?[1|2]\\d{3}[a-z]?.*")) {
					st = new StringTokenizer(tempType1[i], ",");
					
					int col=0;
					while(st.hasMoreTokens()) {
						nameYear[sizeMap][col] = st.nextToken().trim();
						System.out.println((col+1)+"-th element: "+nameYear[sizeMap][col]);
						col++;
					}	
						
					if(nameYear[sizeMap][0].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
						match = false;
						sb = new StringBuffer();
												
						Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
						Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
						
						if(mpAnd.find()) {
							mpAnd.appendReplacement(sb, "");
							match = true;
						}
										
						mpAnd.appendTail(sb);
								
						if(match) {
							nameYear[sizeMap][0] = sb.toString().trim();
						}
						
						match = false;
						sb = new StringBuffer();
												
						pAnd = Pattern.compile("[a|A][n|N][d|D]\\s");
						mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
						
						if(mpAnd.find()) {
							mpAnd.appendReplacement(sb, "");
							match = true;
						}
										
						mpAnd.appendTail(sb);
								
						if(match) {
							nameYear[sizeMap][0] = sb.toString().trim();
						}
					}				
					
					int tempIndex = 0;
					Pattern pn = Pattern.compile(pName);
					Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
					while(mpn.find()) {
						tempIndex++;
					}
					
					String[] tempNameYear = new String[++tempIndex];
					tempIndex = 0;
					pn = Pattern.compile(pName);
					mpn = pn.matcher(nameYear[sizeMap][0]);
					while(mpn.find()) {
						tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();						
					}
					
					Pattern pyear = Pattern.compile(pYear);
					Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
					if(mpyear.find()) {
						tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
					}
					else {
						tempNameYear[tempIndex++] = nameYear[sizeMap][1];
					}
										
					match = false;
					for(int j=0; j<refNameYearIndex; j++) {
						for(int k=0; k<tempIndex; k++) {
							if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
								match = true;
								continue;
							}
							else {
								match = false;
								break;
							}
						}
						
						if(match) {
							mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
							System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							break;
						}						
					}					
				}
				else {
					
					int tempIndex = 0;
					Pattern pn = Pattern.compile(pName);
					Matcher mpn = pn.matcher(tempType1[i]);
					while(mpn.find()) {
						tempIndex++;
					}
					
					String[] tempNameYear = new String[++tempIndex];
					tempIndex = 0;
					pn = Pattern.compile(pName);
					mpn = pn.matcher(tempType1[i]);
					while(mpn.find()) {
						tempNameYear[tempIndex] = tempType1[i].substring(mpn.start(), mpn.end()).trim();	
						System.out.println("==============> "+tempNameYear[tempIndex]);
						tempIndex++;
					}
					
					Pattern pyear = Pattern.compile(pYear);
					Matcher mpyear = pyear.matcher(tempType1[i]);
					if(mpyear.find()) {
						tempNameYear[tempIndex] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
						System.out.println("==============> "+tempNameYear[tempIndex]);
						tempIndex++;
					}		
					
					match = false;
					for(int j=0; j<refNameYearIndex; j++) {
						for(int k=0; k<tempIndex; k++) {
							if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
								match = true;
								continue;
							}
							else {
								match = false;
								break;
							}
						}
						
						if(match) {
							mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
							System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							break;
						}						
					}						
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case2: 1. Two or More Works by the Same Author in the Same Year
			// 		     Format: (Name, Yeara, Yearb) e.g., (Leydesdorff, 2004a, 2004b)
			//        2. Organization as an Author - Two or More Works by the Same Organization in the Same Year
			//           Format: (Organization, Yeara, Yearb) e.g., (Thompson ISI, 2001a, 2001b)
			if(tempType1[i].matches(reg2Case)) {
				st = new StringTokenizer(tempType1[i], "[,|;]");
					
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
								
				int tempIndex = 0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
								
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempName[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						for(int t=0; t<col-1; t++) {
							if(nameYear[sizeMap][t+1].compareToIgnoreCase(refNameYear[j][tempIndex])==0) {
								mapTable[j][4] = Double.toString((Double.parseDouble(mapTable[j][4])+1/(double)(col-1)));
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							}
						}
					}
				}				
				
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case3: A Work by Two Authors - "&" Type
			// Format: (Name1 & Name2, Year) e.g., (Perry & Rice, 1998) / (Gibson & Crooks, 1938, p. 456)
			if(tempType1[i].matches(reg3Case)) {
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempIndex++;
				}			
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mpn.start(), mpn.end()).trim();
				}	
								
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
				}				
								
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}					
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
					}
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");				
			}
			
			// Case4: A Work by Two Authors - "and" Type
			// Format: (Name1 and Name2, Year) e.g., (Hill and Brennan, 2000)
			if(tempType1[i].matches(reg4Case)) {
				System.out.println("===> "+tempType1[i]);
								
				if(tempType1[i].matches(".*[a|A][n|N][d|D]\\s?[o|O][t|T][h|H][e|E][r|R].*")) {
					
					if(tempType1[i].matches(".*,.*")) {
						st = new StringTokenizer(tempType1[i], ",");
						
						int col=0;
						while(st.hasMoreTokens()) {
							nameYear[sizeMap][col] = st.nextToken().trim();
							System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
							col++;
						}
						
						String[] tempNameYear = new String[col+1];
						String pCase4Name = "[A-Z]\\s?[a-z|A-Z]+\\s?";
						Pattern pn = Pattern.compile(pCase4Name);
						Matcher mn = pn.matcher(nameYear[sizeMap][0]);
						if(mn.find()) {
							tempNameYear[0] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
						}
						tempNameYear[1] = "et al";
						tempNameYear[2] = nameYear[sizeMap][1];
						
						for(int j=0; j<refNameYearIndex; j++) {
							if(tempNameYear[0].compareToIgnoreCase(refNameYear[j][0])==0 && tempNameYear[1].compareToIgnoreCase(refNameYear[j][1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[j][2])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							}							
						}
					}	
					else {
						String[] tempNameYear = new String[3];
						
						String pCase4Name = "[A-Z]\\s?[a-z|A-Z]+\\s?";
						Pattern pn = Pattern.compile(pCase4Name);
						Matcher mn = pn.matcher(tempType1[i]);
						if(mn.find()) {
							tempNameYear[0] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
						}
						
						tempNameYear[1] = "et al";
						
						Pattern py4 = Pattern.compile(pYear);
						Matcher my = py4.matcher(tempType1[i]);
						if(my.find()) {
							tempNameYear[2] = tempType1[i].substring(my.start(), my.end()).trim();
						}						
											
						for(int j=0; j<refNameYearIndex; j++) {
							if(tempNameYear[0].compareToIgnoreCase(refNameYear[j][0])==0 && tempNameYear[1].compareToIgnoreCase(refNameYear[j][1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[j][2])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}							
						}						
					}				
					
					sizeMap++;
					System.out.println("------------------------------------");		
					continue;
				}				
				else if(tempType1[i].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(tempType1[i]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						tempType1[i] = sb.toString();
					}					
				}				
				
				if(tempType1[i].matches(".*,.*")) {
					st = new StringTokenizer(tempType1[i], ",");
					
					int col=0;
					while(st.hasMoreTokens()) {
						nameYear[sizeMap][col] = st.nextToken().trim();
						System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
						col++;
					}
					
					Pattern pn = Pattern.compile(pName);
					Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
					String[] tempNameYear = new String[col+1];
					int tempIndex=0;
					while(mpn.find()) {
						tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
					}
					
					Pattern pyear = Pattern.compile(pYear);
					Matcher mpyear = pyear.matcher(nameYear[sizeMap][col-1]);
					if(mpyear.find()) {
						tempNameYear[tempIndex++] = nameYear[sizeMap][col-1].substring(mpyear.start(), mpyear.end()).trim();
					}
					else {
						tempNameYear[tempIndex++] = nameYear[sizeMap][col-1];
					}
					
					for(int j=0; j<refNameYearIndex; j++) {
						if(tempNameYear[0].compareToIgnoreCase(refNameYear[j][0])==0 && tempNameYear[1].compareToIgnoreCase(refNameYear[j][1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[j][2])==0) {
							mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
							System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							break;
						}						
					}		
				}
				else {
					st = new StringTokenizer(tempType1[i], " ");
					
					int col=0;
					while(st.hasMoreTokens()) {
						nameYear[sizeMap][col] = st.nextToken().trim();
						System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
						col++;
					}
										
					for(int j=0; j<refNameYearIndex; j++) {
						if(nameYear[sizeMap][0].compareToIgnoreCase(refNameYear[j][0])==0 && nameYear[sizeMap][1].compareToIgnoreCase(refNameYear[j][1])==0 && nameYear[sizeMap][2].compareToIgnoreCase(refNameYear[j][2])==0) {
							mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
							System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
							break;
						}						
					}		
				}
				
				sizeMap++;
				System.out.println("------------------------------------");				
			}
			
			// Case5: A Work by Three to Five Authors - "&" Type
			// Format: (Name1, Name2, Name3, Name4, & Name5, Year) e.g., (Boyack, Wylie, & Davidson, 2002)
			if(tempType1[i].matches(reg5Case)) {
							
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempIndex++;
				}			
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempNameYear[tempIndex] = tempType1[i].substring(mpn.start(), mpn.end()).trim();
					System.out.println("* Name: "+tempNameYear[tempIndex]);
					tempIndex++;
				}	
								
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				if(mpyear.find()) {
					tempNameYear[tempIndex] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
					System.out.println("* Year: "+tempNameYear[tempIndex]);
					tempIndex++;
				}				
								
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}					
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
					}
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case6: A Work by Three to Five Authors - "and" Type
			// Format: (Name1, Name2, Name3, Name4, and Name5, Year) e.g., (Randall, Kant, and Chhabra, 1997) / (Randall, Kant, and Chhabra 1997)
			if(tempType1[i].matches(reg6Case)) {
				System.out.println("===> "+tempType1[i]);
				if(tempType1[i].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(tempType1[i]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						tempType1[i] = sb.toString();
					}					
				}				
					
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(tempType1[i]);
				int tempIndex = 0;
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mpn.start(), mpn.end()).trim();
				}
				
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
				}				
				
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}	
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
						break;
					}
				}
				
				sizeMap++;				
				System.out.println("------------------------------------");
			}
			
			// Case7: Six or More Authors
			// Format: (Name et al., Year) e.g., (Kohonen et al., 2000) / (Kohonen et al. 2000)
			if(tempType1[i].matches(reg7Case)) {
				System.out.println("Case7: Six or More Authors => "+tempType1[i]);
				
				int tempIndex=0;
				String name = "[A-Z]\\s?[a-z|A-Z|\\-]+\\s?";
				Pattern pn = Pattern.compile(name);
				Matcher mn = pn.matcher(tempType1[i]);
				if(mn.find()) {
					tempIndex++;
				}
				
				String[] tempNameYear = new String[tempIndex+2];
				tempIndex=0;
				pn = Pattern.compile(name);
				mn = pn.matcher(tempType1[i]);
				if(mn.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mn.start(), mn.end()).trim();
				}
				
				Pattern pC7EtAl = Pattern.compile(pEtAl);
				Matcher mpC7EtAl = pC7EtAl.matcher(tempType1[i]);
				if(mpC7EtAl.find()) {
					tempNameYear[tempIndex++] = tempType1[i].substring(mpC7EtAl.start(), mpC7EtAl.end()).trim();
				}
				
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				if(mpyear.find()) {
					tempNameYear[tempIndex] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
				}
												
				int nameCount = 0;
				
				for(int j=0; j<refNameYearIndex; j++) {
					if(tempNameYear[0].compareToIgnoreCase(refNameYear[j][0])==0) {
						nameCount++;
						pn = Pattern.compile(pName);
						int k;
						for(k=1; !refNameYear[j][k].matches(pYear); k++) {
							mn = pn.matcher(refNameYear[j][k]);
							if(mn.find()) {
								nameCount++;
							}							
						}
						
						if(nameCount==1) {
							if(tempNameYear[1].compareToIgnoreCase(refNameYear[j][k-1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}
						}
						
						if(nameCount>=2) {
							if(tempNameYear[2].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}
						}
						else {
							continue;
						}
					}					
				}	
				
				sizeMap++;
				System.out.println("------------------------------------");			
			}
			
			// Case8: Two or More Works by One Author in the Different Year
			// Format: (Name, Year1, Year2) e.g., (McCain, 1992, 1998) / (Walsham 1993; 1995)
			if(tempType1[i].matches(reg8Case)) {
				System.out.println("* Pattern type#1, Case8: Two or More Works by One Author in the Different Year => "+tempType1[i]);
				st = new StringTokenizer(tempType1[i], "[,|;]");
					
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}								
				
				if(nameYear[sizeMap][0].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
					match = false;
					sb = new StringBuffer();
											
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
					
					if(mpAnd.find()) {
						mpAnd.appendReplacement(sb, "");
						match = true;
					}
									
					mpAnd.appendTail(sb);
							
					if(match) {
						nameYear[sizeMap][0] = sb.toString().trim();
					}
					
					match = false;
					sb = new StringBuffer();
											
					pAnd = Pattern.compile("[a|A][n|N][d|D]\\s");
					mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
					
					if(mpAnd.find()) {
						mpAnd.appendReplacement(sb, "");
						match = true;
					}
									
					mpAnd.appendTail(sb);
							
					if(match) {
						nameYear[sizeMap][0] = sb.toString().trim();
					}
				}				
				
				int tempIndex = 0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempName = new String[tempIndex];
				tempIndex = 0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();	
				}			
					
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear;
				for(int j=0; j<col; j++) {
					mpyear = pyear.matcher(nameYear[sizeMap][j]);
					while(mpyear.find()) {
						row++;
					}
				}						
				System.out.println("row "+row);		
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				for(int j=0; j<col; j++) {
					mpyear = pyear.matcher(nameYear[sizeMap][j]);
					while(mpyear.find()) {
						tempYear[row++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
					}
				}			
							
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					int k=0;
					for(; k<tempIndex; k++) {
						if(tempName[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
								
					if(match) {
						for(int t=0; t<row; t++) {
							if(tempYear[t].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString((Double.parseDouble(mapTable[j][4])+1/(double)row));
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}						
						}						
					}
				}			
				
				sizeMap++;
				System.out.println("------------------------------------");			
			}
			
			// Case9: Organization as an Author 
			// Format: (Organization, Year) e.g., (SCIE; Thompson ISI, 2001a)
			if(tempType1[i].matches(reg9Case)) {
				st = new StringTokenizer(tempType1[i], ",");
						
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				// Eleminating the Initial Name of Organization e.g., SCIE;
				if(nameYear[sizeMap][0].matches(".*[A-Z][A-Z]+;.*")) {
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("[A-Z][A-Z]+;");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, "");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						nameYear[sizeMap][0] = sb.toString().trim();
					}					
				}
							
				int tempIndex = 0;
				String pCase9Name = "[A-Z][a-z|A-Z]+\\s?";
				Pattern pn = Pattern.compile(pCase9Name);
				Matcher mn = pn.matcher(nameYear[sizeMap][0]);
				while(mn.find()) {					
					tempIndex++;
				}
				
				String[] tempCase9NameYear = new String[++tempIndex];
				tempIndex = 0;
				pn = Pattern.compile(pCase9Name);
				mn = pn.matcher(nameYear[sizeMap][0]);
				while(mn.find()) {
					tempCase9NameYear[tempIndex++] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
				}
				tempCase9NameYear[tempIndex++] = nameYear[sizeMap][1];
							
				match = false;				
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempCase9NameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
						break;
					}
				}
				
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case10: Two or More Works by Two to Five Authors in the Same Year / Two or More Works by Two Author in the Different Year - "&" Type
			// Format: (Name1, Name2, Name3, Name4 & Name5, Year1, Year2, ..., YearN) e.g., (Henderson-Sellers & Barbier, 1999b, 1999c)
			if(tempType1[i].matches(reg10Case)) {
				System.out.println("Pattern type#1, Case#10 => "+tempType1[i]);
							
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempName[tempIndex++] = tempType1[i].substring(mpn.start(), mpn.end()).trim();
				}
								
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				while(mpyear.find()) {
					row++;
				}		
				
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(tempType1[i]);
				while(mpyear.find()) {
					tempYear[row++] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
				}	
				
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					int k=0;
					for(; k<tempIndex; k++) {
						if(tempName[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						for(int t=0; t<row; t++) {
							if(tempYear[t].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1/(double)row);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}						
						}						
					}
				}	
							
				for(int j=0; j<refNameYearIndex; j++) {
					int k=0;
					for(; k<tempIndex; k++) {
						if(tempName[tempIndex-1-k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
					
					if(match) {
						for(int t=0; t<row; t++) {
							if(tempYear[t].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1/(double)row);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}						
						}						
					}
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");					
			}
			
			// Case11: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "and" Type
			// Format: (Name1, Name2, Name3, Name4 and Name5, Year1, Year2, ..., YearN) e.g., (Henderson-Sellers and Barbier, 1999b, 1999c)
			if(tempType1[i].matches(reg11Case)) {
				System.out.println("Pattern type#1, Case#11 => "+tempType1[i]);
														
				if(tempType1[i].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
											
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(tempType1[i]);
													
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
																		
					mpAnd.appendTail(sb);
													
					if(match) {
						tempType1[i] = sb.toString();
					}				
				}	
					
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempIndex++;
				}
							
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(tempType1[i]);
				while(mpn.find()) {
					tempName[tempIndex++] = tempType1[i].substring(mpn.start(), mpn.end()).trim(); 
				}
							
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(tempType1[i]);
				while(mpyear.find()) {
					row++;
				}		
							
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(tempType1[i]);
				while(mpyear.find()) {
					tempYear[row++] = tempType1[i].substring(mpyear.start(), mpyear.end()).trim();
				}	
							
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					int k=0;
					for(; k<tempIndex; k++) {
						if(tempName[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						for(int t=0; t<row; t++) {
							if(tempYear[t].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1/(double)row);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}						
						}						
					}
				}	
							
				for(int j=0; j<refNameYearIndex; j++) {
					int k=0;
					for(; k<tempIndex; k++) {
						if(tempName[tempIndex-1-k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
					
					if(match) {
						for(int t=0; t<row; t++) {
							if(tempYear[t].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1/(double)row);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}						
						}						
					}
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");		
			}
			
			// Case12: Two or More Works in the Same Parentheses
			// Format: (Name1, Year1; Name2, Year2; ...; NameN, YearN) e.g., (Chen, Cribbin, Macredie, & Morar, 2002; Gmur, 2003; Small, 1999; Small, Sweeney, & Greenlee, 1985)
			if(tempType1[i].matches(reg12Case)) {				
				int col;
				if(tempType1[i].matches(".*[1|2]\\d{3}[a-z]?;.*")) {
					st = new StringTokenizer(tempType1[i], ";");
					
					col=0;
					while(st.hasMoreTokens()) {
						nameYear[sizeMap][col] = st.nextToken().trim();
						System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
						col++;
					}
				}
				else {
					col=0;
					
					Pattern pn1 = Pattern.compile(reg1Case);
					Matcher mpn1 = pn1.matcher(tempType1[i]);
					while(mpn1.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn1.start(), mpn1.end()).trim();
					}
					
					Pattern pn2 = Pattern.compile(reg2Case);
					Matcher mpn2 = pn2.matcher(tempType1[i]);
					while(mpn2.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn2.start(), mpn2.end()).trim();
					}
					
					Pattern pn3 = Pattern.compile(reg3Case);
					Matcher mpn3 = pn3.matcher(tempType1[i]);
					while(mpn3.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn3.start(), mpn3.end()).trim();
					}
					
					Pattern pn4 = Pattern.compile(reg4Case);
					Matcher mpn4 = pn4.matcher(tempType1[i]);
					while(mpn4.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn4.start(), mpn4.end()).trim();
					}
					
					Pattern pn5 = Pattern.compile(reg5Case);
					Matcher mpn5 = pn5.matcher(tempType1[i]);
					while(mpn5.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn5.start(), mpn5.end()).trim();
					}
					
					Pattern pn6 = Pattern.compile(reg6Case);
					Matcher mpn6 = pn6.matcher(tempType1[i]);
					while(mpn6.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn6.start(), mpn6.end()).trim();
					}
					
					Pattern pn7 = Pattern.compile(reg7Case);
					Matcher mpn7 = pn7.matcher(tempType1[i]);
					while(mpn7.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn7.start(), mpn7.end()).trim();
					}
					
					Pattern pn8 = Pattern.compile(reg8Case);
					Matcher mpn8 = pn8.matcher(tempType1[i]);
					while(mpn8.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn8.start(), mpn8.end()).trim();
					}
					
					Pattern pn9 = Pattern.compile(reg9Case);
					Matcher mpn9 = pn9.matcher(tempType1[i]);
					while(mpn9.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn9.start(), mpn9.end()).trim();
					}
					
					Pattern pn10 = Pattern.compile(reg10Case);
					Matcher mpn10 = pn10.matcher(tempType1[i]);
					while(mpn10.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn10.start(), mpn10.end()).trim();
					}
					
					Pattern pn11 = Pattern.compile(reg11Case);
					Matcher mpn11 = pn11.matcher(tempType1[i]);
					while(mpn11.find()) {
						nameYear[sizeMap][col++] = tempType1[i].substring(mpn11.start(), mpn11.end()).trim();
					}
				}				
								
				for(int j=0; j<col; j++) {
								
					// Case1: A Work by One Author
					// Format: (Name, Year) e.g., (White, 2003)
					if(nameYear[sizeMap][j].matches(reg1Case)) {								
						System.out.println("Case1:  A Work by One Author => "+nameYear[sizeMap][j]);
						
						if(nameYear[sizeMap][j].matches(".*,\\s?[1|2]\\d{3}[a-z]?.*")) {
							st = new StringTokenizer(nameYear[sizeMap][j], ",");
							
							String[] tempNameYear = new String[2];
							int tempIndex=0;
							while(st.hasMoreTokens()) {
								tempNameYear[tempIndex] = st.nextToken().trim();
								System.out.println("==> "+tempNameYear[tempIndex]);
								tempIndex++;
							}															
							
							tempIndex = 0;
							Pattern pn = Pattern.compile(pName);
							Matcher mpn = pn.matcher(tempNameYear[0]);
							while(mpn.find()) {
								tempIndex++;
							}
							
							String[] tempCase1NameYear = new String[++tempIndex];
							tempIndex = 0;
							pn = Pattern.compile(pName);
							mpn = pn.matcher(tempNameYear[0]);
							while(mpn.find()) {
								tempCase1NameYear[tempIndex++] = tempNameYear[0].substring(mpn.start(), mpn.end()).trim();						
							}
							tempCase1NameYear[tempIndex++] = tempNameYear[1];
							
							match = false;
							for(int k=0; k<refNameYearIndex; k++) {
								for(int t=0; t<tempIndex; t++) {									
									if(tempCase1NameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
										match = true;
										continue;
									}
									else {
										match = false;
										break;
									}
								}
								
								if(match) {
									mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
									System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
									break;
								}						
							}					
						}
						else {
							
							int tempIndex = 0;
							Pattern pn = Pattern.compile(pName);
							Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
							while(mpn.find()) {
								tempIndex++;
							}
							
							String[] tempNameYear = new String[++tempIndex];
							tempIndex = 0;
							pn = Pattern.compile(pName);
							mpn = pn.matcher(nameYear[sizeMap][j]);
							while(mpn.find()) {
								tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim();	
							}
							
							Pattern pyear = Pattern.compile(pYear);
							Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
							if(mpyear.find()) {
								tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
							}					
							
							match = false;
							for(int k=0; k<refNameYearIndex; k++) {
								for(int t=0; t<tempIndex; t++) {																
									if(tempNameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
										match = true;
										continue;
									}
									else {
										match = false;
										break;
									}
								}
								
								if(match) {
									mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
									System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
									break;
								}						
							}						
						}								
					}
					
					// Case2: 1. Two or More Works by the Same Author in the Same Year
					// 		     Format: (Name, Yeara, Yearb) e.g., (Leydesdorff, 2004a, 2004b)
					//        2. Organization as an Author - Two or More Works by the Same Organization in the Same Year
					//           Format: (Organization, Yeara, Yearb) e.g., (Thompson ISI, 2001a, 2001b)
					if(nameYear[sizeMap][j].matches(reg2Case)) {
						st = new StringTokenizer(nameYear[sizeMap][j], ",");
						
						String[] tempNameYear = new String[st.countTokens()];
						int tempCol=0;
						while(st.hasMoreTokens()) {
							tempNameYear[tempCol++] = st.nextToken().trim();							
						}
						
						int tempIndex = 0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(tempNameYear[0]);
						while(mpn.find()) {
							tempIndex++;
						}
						
						String[] tempName = new String[tempIndex];
						tempIndex=0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(tempNameYear[0]);
						while(mpn.find()) {
							tempName[tempIndex++] = tempNameYear[0].substring(mpn.start(), mpn.end()).trim();
						}
										
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							for(int t=0; t<tempIndex; t++) {
								if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								for(int w=0; w<tempCol-1; w++) {
									if(tempNameYear[w+1].compareToIgnoreCase(refNameYear[k][tempIndex])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(2*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
									}
								}
							}
						}								
					}
					
					// Case3: A Work by Two Authors - "&" Type
					// Format: (Name1 & Name2, Year) e.g., (Perry & Rice, 1998) / (Gibson & Crooks, 1938, p. 456)
					if(nameYear[sizeMap][j].matches(reg3Case)) {
						System.out.println("Case3: A Work by Two Authors - \"&\" Type => "+nameYear[sizeMap][j]);
						
						int tempIndex=0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempIndex++;
						}
						
						String[] tempNameYear = new String[tempIndex+1];
						tempIndex=0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim();
						}
						
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
						if(mpyear.find()) {
							tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
						}
						
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							for(int t=0; t<tempIndex; t++) {
								if(tempNameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}
						}	
					}
					
					// Case4: A Work by Two Authors - "and" Type
					// Format: (Name1 and Name2, Year) e.g., (Hill and Brennan, 2000)
					if(nameYear[sizeMap][j].matches(reg4Case)) {
						System.out.println("===> "+nameYear[sizeMap][j]);
						if(nameYear[sizeMap][j].matches(".*[a|A][n|N][d|D]\\s?[o|O][t|T][h|H][e|E][r|R].*")) {
							
							if(nameYear[sizeMap][j].matches(".*,.*")) {
								st = new StringTokenizer(nameYear[sizeMap][j], ",");
								
								String[] tempNameYear = new String[st.countTokens()];
								int tempCol=0;
								while(st.hasMoreTokens()) {
									tempNameYear[tempCol++] = st.nextToken().trim();
								}
								
								String[] tempCase10NameYear = new String[tempCol+1];
								String pCase10Name = "[A-Z][a-z|A-Z]+\\s?";
								Pattern pn = Pattern.compile(pCase10Name);
								Matcher mn = pn.matcher(tempNameYear[0]);
								if(mn.find()) {
									tempCase10NameYear[0] = tempNameYear[0].substring(mn.start(), mn.end()).trim();
								}
								tempCase10NameYear[1] = "et al";
								tempCase10NameYear[2] = tempNameYear[tempCol-1];
								
								for(int k=0; k<refNameYearIndex; k++) {
									if(tempCase10NameYear[0].compareToIgnoreCase(refNameYear[k][0])==0 && tempCase10NameYear[1].compareToIgnoreCase(refNameYear[k][1])==0 && tempCase10NameYear[2].compareToIgnoreCase(refNameYear[k][2])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}							
								}
							}	
							else {
								String[] tempNameYear = new String[3];
								
								String pCase7Name = "[A-Z][a-z|A-Z]+\\s?";
								Pattern pn = Pattern.compile(pCase7Name);
								Matcher mn = pn.matcher(nameYear[sizeMap][j]);
								if(mn.find()) {
									tempNameYear[0] = nameYear[sizeMap][j].substring(mn.start(), mn.end()).trim();
								}
								
								tempNameYear[1] = "et al";
								
								Pattern py4 = Pattern.compile(pYear);
								Matcher my = py4.matcher(nameYear[sizeMap][j]);
								if(my.find()) {
									tempNameYear[2] = nameYear[sizeMap][j].substring(my.start(), my.end()).trim();
								}						
													
								for(int k=0; k<refNameYearIndex; k++) {
									if(tempNameYear[0].compareToIgnoreCase(refNameYear[k][0])==0 && tempNameYear[1].compareToIgnoreCase(refNameYear[k][1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[k][2])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}							
								}						
							}				
							
							sizeMap++;
							System.out.println("------------------------------------");		
							continue;
						}				
						else if(nameYear[sizeMap][j].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
							// Substitute "and" with " "
							match = false;
							sb = new StringBuffer();
									
							Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
							Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][j]);
												
							while(mpAnd.find()) {
								mpAnd.appendReplacement(sb, " ");
								match = true;
							}
																	
							mpAnd.appendTail(sb);
													
							if(match) {
								nameYear[sizeMap][j] = sb.toString();
							}		
							
							match = false;
							sb = new StringBuffer();
									
							pAnd = Pattern.compile("[a|A][n|N][d|D]\\s");
							mpAnd = pAnd.matcher(nameYear[sizeMap][j]);
												
							while(mpAnd.find()) {
								mpAnd.appendReplacement(sb, " ");
								match = true;
							}
																	
							mpAnd.appendTail(sb);
													
							if(match) {
								nameYear[sizeMap][j] = sb.toString();
							}	
						}				
						
						if(nameYear[sizeMap][j].matches(".*,.*")) {
							st = new StringTokenizer(nameYear[sizeMap][j], ",");
							
							String[] tempNameYear = new String[st.countTokens()];
							int tempCol=0;
							while(st.hasMoreTokens()) {
								tempNameYear[tempCol++] = st.nextToken().trim();
							}							
														
							Pattern pn = Pattern.compile(pName);
							Matcher mpn = pn.matcher(tempNameYear[0]);
							String[] tempCase10NameYear = new String[tempCol+1];
							int tempIndex=0;
							while(mpn.find()) {
								tempCase10NameYear[tempIndex++] = tempNameYear[0].substring(mpn.start(), mpn.end()).trim();
							}
							tempCase10NameYear[tempIndex] = tempNameYear[tempCol-1];
							
							for(int k=0; k<refNameYearIndex; k++) {
								if(tempCase10NameYear[0].compareToIgnoreCase(refNameYear[k][0])==0 && tempCase10NameYear[1].compareToIgnoreCase(refNameYear[k][1])==0 && tempCase10NameYear[2].compareToIgnoreCase(refNameYear[k][2])==0) {
									mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
									System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
									break;
								}						
							}		
						}
						else {							
							int tempIndex=0;
							Pattern pn = Pattern.compile(pName);
							Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
							while(mpn.find()) {
								tempIndex++;
							}
							
							String[] tempNameYear = new String[tempIndex+1];
							tempIndex=0;
							pn = Pattern.compile(pName);
							mpn = pn.matcher(nameYear[sizeMap][j]);
							while(mpn.find()) {
								tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim();
							}
							
							
							Pattern pyear = Pattern.compile(pYear);
							Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
							if(mpyear.find()) {
								tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
							}
							else {
								tempNameYear[tempIndex++] = nameYear[sizeMap][j].trim();
							}
														
							match = false;
							for(int k=0; k<refNameYearIndex; k++) {
								for(int t=0; t<tempIndex; t++) {
									if(tempNameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
										match = true;
										continue;
									}
									else {
										match = false;
										break;
									}
								}
								
								if(match) {
									mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
									System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
									break;
								}
							}				
						}		
					}
					
					// Case5: A Work by Three to Five Authors - "&" Type
					// Format: (Name1, Name2, Name3, Name4, & Name5, Year) e.g., (Boyack, Wylie, & Davidson, 2002)
					if(nameYear[sizeMap][j].matches(reg5Case)) {
						System.out.println("Case5: A Work by Three to Five Authors - \"&\" Type => "+nameYear[sizeMap][j]);
						st = new StringTokenizer(nameYear[sizeMap][j], ",");
						
						String[] tempArr = new String[st.countTokens()];
						int tempCol=0;
						while(st.hasMoreTokens()) {
							tempArr[tempCol++] = st.nextToken().trim();	
						}							
						
						int tempIndex=0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn;
						for(int k=0; k<tempCol; k++) {
							mpn  = pn.matcher(tempArr[k]);
							while(mpn.find()) {
								tempIndex++;
							}
						}
						
						String[] tempNameYear = new String[tempIndex+1];
						tempIndex=0;
						pn = Pattern.compile(pName);
						for(int k=0; k<tempCol; k++) {
							mpn  = pn.matcher(tempArr[k]);
							while(mpn.find()) {
								tempNameYear[tempIndex++] = tempArr[k].substring(mpn.start(), mpn.end()).trim();
							}
						}
						
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(tempArr[tempCol-1]);
						if(mpyear.find()) {
							tempNameYear[tempIndex++] = tempArr[tempCol-1].substring(mpyear.start(), mpyear.end()).trim();
						}
						else {
							tempNameYear[tempIndex++] = tempArr[tempIndex].trim();
						}						
											
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							for(int t=0; t<tempIndex; t++) {
								if(tempNameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}					
							}
							
							if(match) {
								mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
							}
						}		
					}
					
					// Case6: A Work by Three to Five Authors - "and" Type
					// Format: (Name1, Name2, Name3, Name4, and Name5, Year) e.g., (Randall, Kant, and Chhabra, 1997) / (Randall, Kant, and Chhabra 1997)
					if(nameYear[sizeMap][j].matches(reg6Case)) {
						System.out.println("Case6: A Work by Three to Five Authors => "+nameYear[sizeMap][j]);
						if(nameYear[sizeMap][j].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
							// Substitute "and" with " "
							match = false;
							sb = new StringBuffer();
										
							Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
							Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][j]);
												
							while(mpAnd.find()) {
								mpAnd.appendReplacement(sb, " ");
								match = true;
							}
																	
							mpAnd.appendTail(sb);
													
							if(match) {
								nameYear[sizeMap][j] = sb.toString();
							}					
						}				
							
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
						int tempIndex = 0;
						while(mpn.find()) {
							tempIndex++;
						}
						
						String[] tempNameYear = new String[tempIndex+1];
						tempIndex=0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim();
						}
						
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
						if(mpyear.find()) {
							tempNameYear[tempIndex++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
						}				
						
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							for(int t=0; t<tempIndex; t++) {
								if(tempNameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}	
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}
						}					
					}

					// Case7: Six or More Authors
					// Format: (Name et al., Year) e.g., (Kohonen et al., 2000) / (Kohonen et al. 2000)
					if(nameYear[sizeMap][j].matches(reg7Case)) {
						System.out.println("Case7: Six or More Authors => "+nameYear[sizeMap][j]);
													
						String[] tempNameYear = new String[3];
						String pCase7Name = "[A-Z]\\s?[a-z|A-Z|\\-]+\\s?";
						Pattern pn = Pattern.compile(pCase7Name);
						Matcher mn = pn.matcher(nameYear[sizeMap][j]);
						if(mn.find()) {
							tempNameYear[0] = nameYear[sizeMap][j].substring(mn.start(), mn.end()).trim();
						}
						
						Pattern pC7EtAl = Pattern.compile(pEtAl);
						Matcher mpC7EtAl = pC7EtAl.matcher(nameYear[sizeMap][j]);
						if(mpC7EtAl.find()) {
							tempNameYear[1] = nameYear[sizeMap][j].substring(mpC7EtAl.start(), mpC7EtAl.end()).trim();
						}
						
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
						if(mpyear.find()) {
							tempNameYear[2] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
						}
														
						int nameCount = 0;
						
						for(int k=0; k<refNameYearIndex; k++) {
							if(tempNameYear[0].compareToIgnoreCase(refNameYear[k][0])==0) {
								nameCount++;
								pn = Pattern.compile(pName);
								int t;
								for(t=1; !refNameYear[k][t].matches(pYear); t++) {
									mn = pn.matcher(refNameYear[k][t]);
									if(mn.find()) {
										nameCount++;
									}							
								}
								
								if(nameCount==1) {
									if(tempNameYear[1].compareToIgnoreCase(refNameYear[k][t-1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}
								}
								
								if(nameCount>=2) {
									if(tempNameYear[2].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}
								}
								else {
									continue;
								}
							}					
						}							
					}
					
					// Case8: Two or More Works by One Author in the Different Year
					// Format: (Name, Year1, Year2) e.g., (McCain, 1992, 1998)
					if(nameYear[sizeMap][j].matches(reg8Case)) {
						System.out.println("Case8:  Two or More Works by One Author in the Different Year => "+nameYear[sizeMap][j]);
												
						st = new StringTokenizer(nameYear[sizeMap][j], "[,|;]");
							
						int tempCol=0;
						String[] tempArr = new String[st.countTokens()];
						while(st.hasMoreTokens()) {
							tempArr[tempCol] = st.nextToken().trim();
							System.out.println((tempCol+1)+"-Th element: "+tempArr[tempCol]);
							tempCol++;
						}								
						
						if(tempArr[0].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
							match = false;
							sb = new StringBuffer();
													
							Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
							Matcher mpAnd = pAnd.matcher(tempArr[0]);
							
							if(mpAnd.find()) {
								mpAnd.appendReplacement(sb, "");
								match = true;
							}
											
							mpAnd.appendTail(sb);
									
							if(match) {
								tempArr[0] = sb.toString().trim();
							}
							
							match = false;
							sb = new StringBuffer();
													
							pAnd = Pattern.compile("[a|A][n|N][d|D]\\s");
							mpAnd = pAnd.matcher(tempArr[0]);
							
							if(mpAnd.find()) {
								mpAnd.appendReplacement(sb, "");
								match = true;
							}
											
							mpAnd.appendTail(sb);
									
							if(match) {
								tempArr[0] = sb.toString().trim();
							}
						}				
						
						int tempIndex = 0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(tempArr[0]);
						while(mpn.find()) {
							tempIndex++;
						}
						
						String[] tempName = new String[tempIndex];
						tempIndex = 0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(tempArr[0]);
						while(mpn.find()) {
							tempName[tempIndex++] = tempArr[0].substring(mpn.start(), mpn.end()).trim();	
						}			
							
						int row=0;
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear;
						for(int k=0; k<tempCol; k++) {
							mpyear = pyear.matcher(tempArr[k]);
							while(mpyear.find()) {
								row++;
							}
						}						
						
						String[] tempYear = new String[row];
						row=0;
						pyear = Pattern.compile(pYear);
						for(int k=0; k<tempCol; k++) {
							mpyear = pyear.matcher(tempArr[k]);
							while(mpyear.find()) {
								tempYear[row++] = tempArr[k].substring(mpyear.start(), mpyear.end()).trim();
							}
						}			
							
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							int t=0;
							for(; t<tempIndex; t++) {
								if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
										
							if(match) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(row*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}						
								}						
							}
						}
					}
					
					// Case9: Organization as an Author 
					// Format: (Organization, Year) e.g., (SCIE; Thompson ISI, 2001a)
					if(nameYear[sizeMap][j].matches(reg9Case)) {
						st = new StringTokenizer(nameYear[sizeMap][j], ",");
								
						int tempCol=0;
						String[] tempNameYear = new String[st.countTokens()];
						while(st.hasMoreTokens()) {
							tempNameYear[tempCol] = st.nextToken().trim();
							System.out.println((tempCol+1)+"-Th element: "+tempNameYear[tempCol]);
							tempCol++;
						}
						
						// Eleminating the Initial Name of Organization e.g., SCIE;
						if(tempNameYear[0].matches(".*[A-Z][A-Z]+;.*")) {
							match = false;
							sb = new StringBuffer();
										
							Pattern pAnd = Pattern.compile("[A-Z][A-Z]+;");
							Matcher mpAnd = pAnd.matcher(tempNameYear[0]);
												
							while(mpAnd.find()) {
								mpAnd.appendReplacement(sb, "");
								match = true;
							}
																	
							mpAnd.appendTail(sb);
													
							if(match) {
								tempNameYear[0] = sb.toString().trim();
							}					
						}
									
						int tempIndex = 0;
						Pattern pn = Pattern.compile(pName);
						Matcher mn = pn.matcher(tempNameYear[0]);
						while(mn.find()) {					
							tempIndex++;
						}
						
						String[] tempCase9NameYear = new String[++tempIndex];
						tempIndex = 0;
						pn = Pattern.compile(pName);
						mn = pn.matcher(tempNameYear[0]);
						while(mn.find()) {
							tempCase9NameYear[tempIndex++] = tempNameYear[0].substring(mn.start(), mn.end()).trim();
						}
						
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(tempNameYear[1]);
						if(mpyear.find()) {
							tempCase9NameYear[tempIndex++] = tempNameYear[1].substring(mpyear.start(), mpyear.end()).trim();
						}
						else {
							tempCase9NameYear[tempIndex++] = nameYear[sizeMap][1];
						}
									
						match = false;				
						for(int k=0; k<refNameYearIndex; k++) {
							for(int t=0; t<tempIndex; t++) {
								if(tempCase9NameYear[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)col);
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}
						}					
					}
					
					// Case10: Two or More Works by Two to Five Authors in the Same Year / Two or More Works by Two Author in the Different Year - "&" Type
					// Format: (Name1, Name2, Name3, Name4 & Name5, Year1, Year2, ..., YearN) e.g., (Henderson-Sellers & Barbier, 1999b, 1999c)
					if(nameYear[sizeMap][j].matches(reg10Case)) {
						System.out.println("Pattern type#1, Case#10 => "+nameYear[sizeMap][j]);
									
						int tempIndex=0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempIndex++;
						}
						
						String[] tempName = new String[tempIndex];
						tempIndex=0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempName[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim();
						}
										
						int row=0;
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
						while(mpyear.find()) {
							row++;
						}		
						
						String[] tempYear = new String[row];
						row=0;
						pyear = Pattern.compile(pYear);
						mpyear = pyear.matcher(nameYear[sizeMap][j]);
						while(mpyear.find()) {
							tempYear[row++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
						}	
						
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							int t=0;
							for(; t<tempIndex; t++) {
								if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(row*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}						
								}						
							}
						}	
									
						for(int k=0; k<refNameYearIndex; k++) {
							int t=0;
							for(; t<tempIndex; t++) {
								if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}						
							}
							
							if(match) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(row*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}						
								}						
							}
						}					
					}
					
					// Case11: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "and" Type
					// Format: (Name1, Name2, Name3, Name4 and Name5, Year1, Year2, ..., YearN) e.g., (Henderson-Sellers and Barbier, 1999b, 1999c)
					if(nameYear[sizeMap][j].matches(reg11Case)) {
						System.out.println("Pattern type#1, Case#11 => "+nameYear[sizeMap][j]);
																
						if(nameYear[sizeMap][j].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
							// Substitute "and" with " "
							match = false;
							sb = new StringBuffer();
													
							Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
							Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][j]);
															
							while(mpAnd.find()) {
								mpAnd.appendReplacement(sb, " ");
								match = true;
							}
																				
							mpAnd.appendTail(sb);
															
							if(match) {
								nameYear[sizeMap][j] = sb.toString();
							}		
							
							match = false;
							sb = new StringBuffer();						
						}	
							
						int tempIndex=0;
						Pattern pn = Pattern.compile(pName);
						Matcher mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempIndex++;
						}
									
						String[] tempName = new String[tempIndex];
						tempIndex=0;
						pn = Pattern.compile(pName);
						mpn = pn.matcher(nameYear[sizeMap][j]);
						while(mpn.find()) {
							tempName[tempIndex++] = nameYear[sizeMap][j].substring(mpn.start(), mpn.end()).trim(); 
						}
									
						int row=0;
						Pattern pyear = Pattern.compile(pYear);
						Matcher mpyear = pyear.matcher(nameYear[sizeMap][j]);
						while(mpyear.find()) {
							row++;
						}		
									
						String[] tempYear = new String[row];
						row=0;
						pyear = Pattern.compile(pYear);
						mpyear = pyear.matcher(nameYear[sizeMap][j]);
						while(mpyear.find()) {
							tempYear[row++] = nameYear[sizeMap][j].substring(mpyear.start(), mpyear.end()).trim();
						}	
									
						match = false;
						for(int k=0; k<refNameYearIndex; k++) {
							int t=0;
							for(; t<tempIndex; t++) {
								if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}
							}
							
							if(match) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(row*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}						
								}						
							}
						}	
									
						for(int k=0; k<refNameYearIndex; k++) {
							int t=0;
							for(; t<tempIndex; t++) {
								if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
									match = true;
									continue;
								}
								else {
									match = false;
									break;
								}						
							}
							
							if(match) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
										mapTable[k][4] = Double.toString(Double.parseDouble(mapTable[k][4])+1/(double)(row*col));
										System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
										break;
									}						
								}						
							}
						}							
					}					
				}
				
				sizeMap++;
				System.out.println("------------------------------------");
			}		
		}
		
		// Pattern Type 2
		for(int i=0; i<sizeType2; i++) {
					
			// Case1: A Work by One Author
			// Format: Name (Year) e.g., Gmur (2003) 
			if(tempType2[i].matches(reg13Case)) {
				
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
					
				int tempIndex = 0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex = 0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
				
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}
				
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {						
						
						if(refNameYear[j][k+1]!=null) {
							if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k+1])==0) {
								match = true;
								continue;
							}
						}	
						
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
						break;
					}
				}				
				
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case2: Two or More Works by the Same Author in the Same Year / Two or More Works by One Author in the Different Year
			// Format: Name (Yeara; Yearb) or Name (Yeara, Yearb) / Name (Year1; Year2) or Name (Year1, Year2)
			// E.g., Leydesdorff (2004a; 2004b) / Leydesdorff (2004a, 2004b) /  McCain (1992; 1998) / McCain (1992, 1998)
			if(tempType2[i].matches(reg14Case)) {
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
								
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}			
				
				String[][] tempNameYear = new String[row][2];
				
				for(int j=0; j<row; j++) {
					Pattern pn = Pattern.compile(pName);
					Matcher mn = pn.matcher(nameYear[sizeMap][0]);
					if(mn.find()) {
						tempNameYear[j][0] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
					}
				}				
				
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				int tempIndex=0;		
				while(mpyear.find()) {
					tempNameYear[tempIndex++][1] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}
				
				for(int j=0; j<row; j++) {
					for(int k=0; k<refNameYearIndex; k++) {
						if(tempNameYear[j][0].compareToIgnoreCase(refNameYear[k][0])==0 && tempNameYear[j][1].compareToIgnoreCase(refNameYear[k][1])==0) {
							mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
							System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
							break;
						}						
					}
				
				}								
							
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case3: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "and" Type
			// Format: Name1 and Name2 (Yeara; Yearb) or Name1 and Name2 (Yeara, Yearb) / Name1 and Name2 (Year1; Year2) or Name1 and Name2 (Year1, Year2)		
			// E.g., Bergman and Feser (1999, 2000)
			if(tempType2[i].matches(reg15Case)) {
				System.out.println("Pattern type#2, Case#3 => "+tempType2[i]);
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
								
				if(nameYear[sizeMap][0].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						nameYear[sizeMap][0] = sb.toString();
					}					
				}	
				
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim(); 
				}
				
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}		
				
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					tempYear[row++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}	
				
				match = false;
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}	
							
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
					
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}		
				
				sizeMap++;
				System.out.println("------------------------------------");			
			}
			
			// Case4: Two or More Works by Two Author in the Same Year / Two or More Works by Two Author in the Different Year - "&" Type
			// Format: Name1 & Name2 (Yeara; Yearb) or Name1 & Name2 (Yeara, Yearb) / Name1 & Name2 (Year1; Year2) or Name1 & Name2 (Year1, Year2)	
			if(tempType2[i].matches(reg16Case)) {
				System.out.println("Pattern type#2, Case#4 => "+tempType2[i]);
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				st = new StringTokenizer(nameYear[sizeMap][0], "&");
				
				String[] tempName = new String[st.countTokens()];
				int tempIndex=0;
				
				while(st.hasMoreTokens()) {
					tempName[tempIndex++] = st.nextToken().trim();
				}
								
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}		
				
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					tempYear[row++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}	
				
				match = false;
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}	
							
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
					
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}				
				
				sizeMap++;
				System.out.println("------------------------------------");					
			}
			
			// Case5: A Work by Two Authors - "and" Type
			// Format: Name1 and Name2 (Year) e.g., Pudovkin and Garfield (2002)
			if(tempType2[i].matches(reg17Case)) {
				System.out.println("Pattern type#2, Case#5: A Work by Two Authors  => "+tempType2[i]);
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				if(nameYear[sizeMap][0].matches(".*\\s[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]\\s?");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						nameYear[sizeMap][0] = sb.toString();
					}	
				}
				
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
				
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}
				else {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1];
				}
				
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
					}	
				}				
				
				sizeMap++;			
				System.out.println("------------------------------------");
			}
			
			// Case6: A Work by Two Authors - "&" Type
			// Format: Name1 & Name2 (Year) e.g., Winograd & Woods (1997)
			if(tempType2[i].matches(reg18Case)) {
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				if(nameYear[sizeMap][0].matches(".*&.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
								
					Pattern pAnd = Pattern.compile("\\s?&\\s?");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
										
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
															
					mpAnd.appendTail(sb);
											
					if(match) {
						nameYear[sizeMap][0] = sb.toString();
					}	
				}
				
				int tempIndex=0;
				String pCase2Name = "\\s?[A-Z]\\s?[a-z|A-Z|\\-]+\\s?";
				Pattern pn = Pattern.compile(pCase2Name);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
				
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pCase2Name);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
				
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}			
				
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
					
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
					}	
				}				
				
				sizeMap++;			
				System.out.println("------------------------------------");
			}
			
			// Case7: A Work by Three to Five Authors - "&" Type
			// Format: Name1, Name2, Name3, Name4, & Name5(Year) e.g., Rosales, Siddiqui, Alon, & Sclaroff (2001)
			if(tempType2[i].matches(reg19Case)) {
				System.out.println("Case7: A Work by Three to Five Authors - \"&\" Type => "+tempType2[i]);
				
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				int tempIndex = 0;
				while(mpn.find()) {
					tempIndex++;
				}
							
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
							
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}
							
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}	
						else {
							match = false;
							break;
						}
					}
								
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
						break;
					}
				}
							
				sizeMap++;				
				System.out.println("------------------------------------");				
			}
						
			// Case8: A Work by Three to Five Authors - "and" Type
			// Format: (Name1, Name2, Name3, Name4, and Name5, Year) e.g., Howe, Leventon and Freeman (1999)
			if(tempType2[i].matches(reg20Case)) {
				System.out.println("Case8: A Work by Three to Five Authors - \"and\" Type => "+tempType2[i]);
				
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				if(nameYear[sizeMap][0].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
											
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
													
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
																		
					mpAnd.appendTail(sb);
														
					if(match) {
						nameYear[sizeMap][0] = sb.toString();
					}					
				}				
								
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				int tempIndex = 0;
				while(mpn.find()) {
					tempIndex++;
				}
							
				String[] tempNameYear = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim();
				}
							
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				if(mpyear.find()) {
					tempNameYear[tempIndex++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}
							
				match = false;
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<tempIndex; k++) {
						if(tempNameYear[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							match = true;
							continue;
						}	
						else {
							match = false;
							break;
						}
					}
								
					if(match) {
						mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
						System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
						break;
					}
				}
							
				sizeMap++;				
				System.out.println("------------------------------------");
			}
			
			// Case9: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year - "&" Type
			// Format: Name1, Name2, Name3, Name4 & Name5 (Yeara; Yearb) or Name1, Name2, Name3, Name4 & Name5 (Year1; Year2) / Name1, Name2, Name3, Name4 & Name5 (Yeara, Yearb) or Name1, Name2, Name3, Name4 & Name5 (Year1, Year2) 	
			// E.g., Haritaoglu, Harwood & Davis (1998, 2000)
			if(tempType2[i].matches(reg21Case)) {
				System.out.println("Case9: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year -\"&\" Type => "+tempType2[i]);
				
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
									
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
							
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim(); 
				}
							
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}		
							
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					tempYear[row++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}	
							
				match = false;
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
								
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}	
										
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
								
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}		
						
				sizeMap++;
				System.out.println("------------------------------------");
			}			
			
			// Case10: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year - "and" Type
			// Format: Name1, Name2, Name3, Name4 and Name5 (Yeara; Yearb) or Name1, Name2, Name3, Name4 and Name5 (Year1; Year2) / Name1, Name2, Name3, Name4 and Name5 (Yeara, Yearb) or Name1, Name2, Name3, Name4 and Name5 (Year1, Year2) 	
			// E.g., Haritaoglu, Harwood and Davis (1998, 2000)
			if(tempType2[i].matches(reg22Case)) {
				System.out.println("Case10: Two or More Works by Three to Five Authors in the Same Year / Two or More Works by Three to Five Authors in the Different Year -\"and\" Type => "+tempType2[i]);
				
				st = new StringTokenizer(tempType2[i], "(");
							
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
											
				if(nameYear[sizeMap][0].matches(".*\\s?[a|A][n|N][d|D]\\s?.*")) {
					// Substitute "and" with " "
					match = false;
					sb = new StringBuffer();
											
					Pattern pAnd = Pattern.compile("\\s[a|A][n|N][d|D]");
					Matcher mpAnd = pAnd.matcher(nameYear[sizeMap][0]);
													
					while(mpAnd.find()) {
						mpAnd.appendReplacement(sb, " ");
						match = true;
					}
																		
					mpAnd.appendTail(sb);
													
					if(match) {
						nameYear[sizeMap][0] = sb.toString();
					}					
				}	
					
				int tempIndex=0;
				Pattern pn = Pattern.compile(pName);
				Matcher mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempIndex++;
				}
							
				String[] tempName = new String[tempIndex];
				tempIndex=0;
				pn = Pattern.compile(pName);
				mpn = pn.matcher(nameYear[sizeMap][0]);
				while(mpn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mpn.start(), mpn.end()).trim(); 
				}
							
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}		
							
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					tempYear[row++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}	
							
				match = false;
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}
					}
								
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}	
										
				for(int k=0; k<refNameYearIndex; k++) {
					int t=0;
					for(; t<tempIndex; t++) {
						if(tempName[tempIndex-1-t].compareToIgnoreCase(refNameYear[k][t])==0) {
							match = true;
							continue;
						}
						else {
							match = false;
							break;
						}						
					}
								
					if(match) {
						for(int w=0; w<row; w++) {
							if(tempYear[w].compareToIgnoreCase(refNameYear[k][t])==0) {
								mapTable[k][4] = Double.toString((Double.parseDouble(mapTable[k][4])+1/(double)row));
								System.out.println("==============> "+mapTable[k][0]+" -> Count: "+mapTable[k][4]);
								break;
							}						
						}						
					}
				}		
						
				sizeMap++;
				System.out.println("------------------------------------");
			}
			
			// Case11: Six or More Authors	
			// Format: Name et al. (Year) e.g., Roepke et al. (1974)
			if(tempType2[i].matches(reg23Case)) {
				System.out.println("Case11: Six or More Authors => "+tempType2[i]);
				st = new StringTokenizer(tempType2[i], "(");
				
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				String[] tempNameYear = new String[col+1];
					
				String pCase4Name = "[A-Z]\\s?[a-z|A-Z]+\\s?";
				Pattern pn = Pattern.compile(pCase4Name);
				Matcher mn = pn.matcher(nameYear[sizeMap][0]);
				if(mn.find()) {
					tempNameYear[0] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
				}
					
				Pattern pC7EtAl = Pattern.compile(pEtAl);
				Matcher mpC7EtAl = pC7EtAl.matcher(nameYear[sizeMap][0]);
				if(mpC7EtAl.find()) {
					tempNameYear[1] = nameYear[sizeMap][0].substring(mpC7EtAl.start(), mpC7EtAl.end()).trim();
				}
				tempNameYear[2] = nameYear[sizeMap][1];
									
				int nameCount = 0;
					
				for(int j=0; j<refNameYearIndex; j++) {
					if(tempNameYear[0].compareToIgnoreCase(refNameYear[j][0])==0) {
						nameCount++;
						pn = Pattern.compile(pName);
						int k;
						for(k=1; !refNameYear[j][k].matches(pYear); k++) {
							mn = pn.matcher(refNameYear[j][k]);
							if(mn.find()) {
								nameCount++;
							}							
						}
							
						if(nameCount==1) {
							if(tempNameYear[1].compareToIgnoreCase(refNameYear[j][k-1])==0 && tempNameYear[2].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}
						}
							
						if(nameCount>=2) {
							if(tempNameYear[2].compareToIgnoreCase(refNameYear[j][k])==0) {
								mapTable[j][4] = Double.toString(Double.parseDouble(mapTable[j][4])+1.0);
								System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
								break;
							}
						}
						else {
							continue;
						}
					}					
				}	
						
				sizeMap++;			
				System.out.println("------------------------------------");
			}
			
			// Case12: Two or More Works by Six or More Authors	
			// Format: Name et al. (Year1; Year2) / Name et al. (Year1, Year2) e.g., Song et al. (2000; 2001)
			if(tempType2[i].matches(reg24Case)) {
				System.out.println("Case12: Two or More Works by Six or More Authors => "+tempType2[i]);
				st = new StringTokenizer(tempType2[i], "(");
							
				int col=0;
				while(st.hasMoreTokens()) {
					nameYear[sizeMap][col] = st.nextToken().trim();
					System.out.println((col+1)+"-Th element: "+nameYear[sizeMap][col]);
					col++;
				}
				
				int tempIndex=0;
				String pCase12Name = "[A-Z]\\s?[a-z|A-Z]+\\s?";
				Pattern pn = Pattern.compile(pCase12Name);
				Matcher mn = pn.matcher(nameYear[sizeMap][0]);
				while(mn.find()) {
					tempIndex++;
				}
											
				String[] tempName = new String[tempIndex+1];
				tempIndex=0;
				pn = Pattern.compile(pCase12Name);
				mn = pn.matcher(nameYear[sizeMap][0]);
				if(mn.find()) {
					tempName[tempIndex++] = nameYear[sizeMap][0].substring(mn.start(), mn.end()).trim();
				}
				
				Pattern pC12EtAl = Pattern.compile(pEtAl);
				Matcher mpC12EtAl = pC12EtAl.matcher(nameYear[sizeMap][0]);
				if(mpC12EtAl.find()) {
					tempName[tempIndex] = nameYear[sizeMap][0].substring(mpC12EtAl.start(), mpC12EtAl.end()).trim();
				}
											
				int row=0;
				Pattern pyear = Pattern.compile(pYear);
				Matcher mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					row++;
				}		
							
				String[] tempYear = new String[row];
				row=0;
				pyear = Pattern.compile(pYear);
				mpyear = pyear.matcher(nameYear[sizeMap][1]);
				while(mpyear.find()) {
					tempYear[row++] = nameYear[sizeMap][1].substring(mpyear.start(), mpyear.end()).trim();
				}	
				
				int nameCount = 0;
				
				for(int j=0; j<refNameYearIndex; j++) {
					for(int k=0; k<=tempIndex; k++) {
						if(tempName[k].compareToIgnoreCase(refNameYear[j][k])==0) {
							nameCount++;
							pn = Pattern.compile(pName);
							int t;
							for(t=k; !refNameYear[j][t].matches(pYear); t++) {
								mn = pn.matcher(refNameYear[j][t]);
								if(mn.find()) {
									nameCount++;
								}							
							}
							
							if(nameCount==1) {
								if(tempName[tempIndex].compareToIgnoreCase(refNameYear[j][t-1])==0) {
									for(int w=0; w<row; w++) {
										if(tempYear[w].compareToIgnoreCase(refNameYear[j][t])==0) {
											mapTable[j][4] = Double.toString((Double.parseDouble(mapTable[j][4])+1/(double)row));
											System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
											break;
										}						
									}
								}
							}
							
							if(nameCount>=2) {
								for(int w=0; w<row; w++) {
									if(tempYear[w].compareToIgnoreCase(refNameYear[j][t])==0) {
										mapTable[j][4] = Double.toString((Double.parseDouble(mapTable[j][4])+1/(double)row));
										System.out.println("==============> "+mapTable[j][0]+" -> Count: "+mapTable[j][4]);
										break;
									}						
								}								
							}
							else {
								continue;
							}
						}
					}
				}				
									
				sizeMap++;			
				System.out.println("------------------------------------");
			}
		}
		
		NameYearBasedDescSort(mapTable, refNameYearIndex);
		
		// Floating-point number :: Round up at 3th position
		for(int i=0; i<refNameYearIndex; i++) {
			double finalCount = Double.parseDouble(String.format("%.3f", Double.parseDouble(mapTable[i][4])));
			mapTable[i][4] = Double.toString(finalCount);
			
		}	
		
		System.out.println("*** Final Citation Results ***\n");
		System.out.println("******************\n");
		for(int i=0; i<refNameYearIndex; i++) {
			System.out.println("=> Ref. Paper: "+mapTable[i][0]);
			System.out.println("   - Title   : "+mapTable[i][1]);
			System.out.println("   - Author  : "+mapTable[i][2]);
			System.out.println("   - Year    : "+mapTable[i][3]);
			System.out.println("   - Count   : "+mapTable[i][4]);
			System.out.println("----------------------");
		}
		
		/*
		// Citation results :: Process to store the Hash table
		for(int i=0; i<refNameYearIndex; i++) {
			citationResultsStorageProcess(mapTable[i][1], Double.parseDouble(mapTable[i][4]));
		}
		// Citation results :: Check for citation results using Hash table
		System.out.println("*** Final Citation Results using Hash table ***");
		citationResultsView();
		*/
		
		// Eliminate Non-PCDATA
		for(int i=0; i<5; i++) {
			
			// Non-PCDATA : &
			for(int j=0; j<mapIndex; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType1);
				Matcher mNonPCDATA = nonPCDATA.matcher(mapTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&amp;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					mapTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : <
			for(int j=0; j<mapIndex; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType2);
				Matcher mNonPCDATA = nonPCDATA.matcher(mapTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&lt;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					mapTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : >
			for(int j=0; j<mapIndex; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType3);
				Matcher mNonPCDATA = nonPCDATA.matcher(mapTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&gt;");
					match = true;
				}
						
				mNonPCDATA.appendTail(sb);
					
				if(match) {
					mapTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : "
			for(int j=0; j<mapIndex; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType4);
				Matcher mNonPCDATA = nonPCDATA.matcher(mapTable[j][i]);
					
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&quot;");
					match = true;
				}
								
				mNonPCDATA.appendTail(sb);
						
				if(match) {
					mapTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : '
			for(int j=0; j<mapIndex; j++) {
				match = false;
				sb = new StringBuffer();
					
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType5);
				Matcher mNonPCDATA = nonPCDATA.matcher(mapTable[j][i]);
							
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&apos;");
					match = true;
				}
										
				mNonPCDATA.appendTail(sb);
							
				if(match) {
					mapTable[j][i] = sb.toString().trim();
				}			
			}			
		}		
				
		try {
			fw = new FileWriter("citationResults.xml");
			fileBw = new BufferedWriter(fw);
			
			String xmlPrologInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
			String xslStyleSheetInfo = "\n<?xml-stylesheet type=\"text/xsl\" href=\"NameYearCitationResultsView.xsl\"?>\n\n";
			fileBw.write(xmlPrologInfo);
			fileBw.write(xslStyleSheetInfo);
			fileBw.write("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"./citationResultsSchema.xsd\">\n");
			for(int i=0; i<refNameYearIndex; i++) {
				fileBw.write("<refInfo>\n");
				fileBw.write("<reference>"); fileBw.write(mapTable[i][0]); fileBw.write("</reference>\n");
				fileBw.write("<refTitle>"); fileBw.write(mapTable[i][1]); fileBw.write("</refTitle>\n");
				fileBw.write("<refAuthor>"); fileBw.write(mapTable[i][2]); fileBw.write("</refAuthor>\n");
				fileBw.write("<refYear>"); fileBw.write(mapTable[i][3]); fileBw.write("</refYear>\n");
				fileBw.write("<citationCount>"); fileBw.write(mapTable[i][4]); fileBw.write("</citationCount>\n");
				fileBw.write("</refInfo>\n\n");
			}
			fileBw.write("</root>");
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}	
	}
	
	public void NameYearBasedDescSort(String[][] arr, int n) {
		
		String temp;
		String count;
		
		for(int i=0; i<n-1; i++)
		{
			for(int j=0; j<n-i-1; j++)
			{
				if(Double.parseDouble(arr[j][4]) < Double.parseDouble(arr[j+1][4])) {
					temp = arr[j][0];
					arr[j][0] = arr[j+1][0];
					arr[j+1][0] = temp;
					
					temp = arr[j][1];
					arr[j][1] = arr[j+1][1];
					arr[j+1][1] = temp;
					
					temp = arr[j][2];
					arr[j][2] = arr[j+1][2];
					arr[j+1][2] = temp;
					
					temp = arr[j][3];
					arr[j][3] = arr[j+1][3];
					arr[j+1][3] = temp;
										
					count = arr[j][4];
					arr[j][4] = arr[j+1][4];
					arr[j+1][4] = count;
				}
			}
		}
	}
	
	/*******************************************************************************
	 * Method Name : ShortNameYearBasedCitationCalculation
	 * Purpose     : Calculate short Name-Year based citation e.g., [Lee00] / [TTT]
	 * Parameters  : Text file converted from PDF file using PDF parser
	 * Return      : None
	 *******************************************************************************/ 
	
	public void shortNameYearBasedCitationCalculation(String text) {
		String str;	
		String temp;
	
		pattern = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?(.\\s*[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?)*](,?\\s*\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?(.?\\s*[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?)*])*";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		String[] refTempTable = new String[MEMORY_SIZE];
		String[] refTable = new String[MEMORY_SIZE];
		String[][] refInfoTable = new String[MEMORY_SIZE][6];
		String[] tempTable = new String[MEMORY_SIZE];
					
		int tempSize = 0;
		int refTempSize = -1;
		int refSize = 0;
		int weight;
		boolean match;
		StringBuffer sb;
		
		FileWriter fw = null;
		BufferedWriter fileBw = null;
		FileReader fr = null;
		BufferedReader fileBr = null;
		
		String nonPCDATAType1 = "&";
		String nonPCDATAType2 = "<";
		String nonPCDATAType3 = ">";
		String nonPCDATAType4 = "\"";
		String nonPCDATAType5 = "'";
		
		try {
			fw = new FileWriter("sampleIn.txt");
			fileBw = new BufferedWriter(fw);
			
			fileBw.write(text);
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}
		
		System.out.println("*** Reference Separation Process ***");
		String refPattern = "\\[[A-Z][a-z|A-Z|\\+]+(\\s*[0-9]{1,2}[a-z]?)?\\s*]\\s*.+.";
				
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("references.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S].*") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R].*")) {
		    			while((str = fileBr.readLine())!=null) {
		    				buf = new StringTokenizer(str, "\n");
			    	    	temp = "";
			    	    			    	    			    	    		
			    	    	while(buf.hasMoreTokens()) {
			    	    		temp = buf.nextToken();
			    	    		   						    	    	
			    	    		if(temp.matches(refPattern)) {
			    	    			Pattern rpt = Pattern.compile(refPattern);
				    	    		Matcher mrpt = rpt.matcher(temp);
			    	    			while(mrpt.find()) {
			    	    				fileBw.newLine();
			    	    				refTempSize++;
			    	    				refTempTable[refTempSize] = temp.substring(mrpt.start(), mrpt.end()).trim();
			    	    				//System.out.println((refTempSize+1)+"-Th ref.: "+refTempTable[refTempSize]);
			    	    			}
			    	    		}			    	    		
			    	    		
			    	    		fileBw.write(temp);
			    	    		//fileBw.newLine();
			    	    	}			    	    	
		    			}		    			
		    		}	    		
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}		
		
		try {
			fr = new FileReader("references.txt");
	    	fileBr = new BufferedReader(fr);
	    			    	    	
	    	str = "";
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			
	    		temp = "";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken().trim();
		    		if(temp.matches("")) {
		    			break;
		    		}
		    		refTable[refSize++] = temp;
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();	 
		} catch(Exception e) {
			System.out.println(e);  
		}
		
		System.out.println("*** Before Reference Separation ***");
		for(int i=0; i<refSize; i++) {
			System.out.println((i+1)+"-Th ref.: "+refTable[i]);
		}		
				
		String refPatternSep = ".+\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3},?\\s?[1|2][0|8|9]\\d{2}[a-z]?\\)?\\.?\\s?(\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3})?|.+[1|2][0|8|9]\\d{2}[a-z]?\\)?\\.?\\s?(\\d{1,3}\\s?[?|\\-]\\s?\\d{1,3})?[^\\d|A-Z|\\s]";
		Pattern rp = Pattern.compile(refPatternSep);
		for(int i=0; i<refSize; i++) {
			Matcher mrp = rp.matcher(refTable[i]);
			if(mrp.find()) {
				refInfoTable[i][0] = refTable[i].substring(mrp.start(), mrp.end()).trim();
			}	
			else {
				refInfoTable[i][0] = refTable[i];
			}
		}	
		
		System.out.println("*** After Reference Separation ***");
		for(int i=0; i<refSize; i++) {
			System.out.println((i+1)+"-th ref.: "+refInfoTable[i][0]);
		}
		System.out.println("****************************");
		
		// Citation symbol extraction from each reference e.g., [KB03] / [Kop99] / [TTT]
		System.out.println("*** Citation symbol extraction from each reference ***");
		String citSymbolPattern = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2})?\\s*]";
				
		for(int i=0; i<refSize; i++) {
			Pattern pcs = Pattern.compile(citSymbolPattern);
			Matcher mpcs = pcs.matcher(refTable[i]);
						
			if(mpcs.find()) {
				refInfoTable[i][1] = refTable[i].substring(mpcs.start()+1, mpcs.end()-1).trim();
				System.out.println((i+1)+"-th ref. symbol: "+refInfoTable[i][1]);
			}				
		}
		System.out.println("****************************");
						
		// Citation Count Initialization
		for(int i=0; i<refSize; i++) {
			refInfoTable[i][5] = "0.0";
		}	
		
		System.out.println("*** Author, Title, and Year Extraction ***");
		
		//String patternAuthorTitle = "(((\\-?[A-Z]\\.\\s?)+[A-Z|a-z][a-z|\\s|A-Z|\\-]+,?\\s?)+\\s?[a|A][n|N][d|D]\\s?(\\-?[A-Z]\\.\\s?)*[A-Z][a-z|A-Z|\\-]+\\s?[.|,|\\s]|(\\-?[A-Z]\\.\\s?)+[A-Z][a-z|A-Z|\\s|\\-]+[.|,|\\s]|([A-Z][a-z|A-Z|\\-]+,\\s?(\\-?[A-Z]\\.,?\\s?)+([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-]+,\\s?(\\-?[A-Z]\\.?,?\\s?)+[.|,|\\s|:|?]|([A-Z][a-z|A-Z|\\-]+\\.?\\s?[A-Z][a-z|A-Z|\\-]+,?\\s?(\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-]+,?)?)+[.|,|\\s|:|?]|([A-Z|a-z][a-z|A-Z|\\s|\\-]+\\s?,?\\s?(\\-?[A-Z]\\.?,?\\s?)+([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+[.|,|\\s|:|?])\\s?“?[A-Z][a-z|A-Z|\\s|\\-|:|,|(|)]+[\\.|”]?(\\s?[0-9]+:\\s?)?([A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)]+\\.?)?";
		//String patternTitle = "\\s?“?\\\\?\"?([0-9]+\\s?\\?\\s?)?[A-Z][a-z|A-Z|0-9|\\s|\\-|:|,|(|)|?|@|/|’|*]+[.|”|\"]?(\\s?[0-9]+:\\s?)?([a-z]+\\s?)?([A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*]+\\.?)?";
		String patternTitle = "\\s?(\\d*[a-z]?\\.?)?“?\\\\?\"?([0-9]+\\s?\\?\\s?)?([A-Z][a-z|A-Z|\\-]+[.|,|:]\\s*)*\\s*[A-Z|a-z][a-z|A-Z|0-9|\\s|\\-|:|(|)|?|@|/|‘|’|*|_|`|“|”]+[.|,|”|\"](\\s*[A-Z][a-z|A-Z|\\s]+”,)?([a-z]+\\s*[A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_]+[.|”|\"]?)?(\\s*[0-9]*:\\s*[A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_]+[.|”|\"]?)?";
		//String patternTitle = ".?\\s?“?\\\\?\"?([0-9]+\\s?\\?\\s?)?(\\([1|2]\\d{3}[a-z]?\\))?([A-Z][a-z|A-Z|\\-]+[.|,|:]\\s*)*\\s*[A-Z|a-z][a-z|A-Z|0-9|\\s|\\-|:|(|)|?|@|/|‘|’|*|_|'|“|”|!|&]+[.|,|”|\"](\\s*[A-Z|a-z][a-z|A-Z|\\-]+,\\s*[a|A][n|N][d|D]\\s*[A-Z|a-z][a-z|A-Z|\\-]+\\s*[A-Z|a-z][a-z|A-Z|\\-]+,\\s*)?(\\s*[a-z]+[:|.]\\s*[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_|'|.]+[.|”|’|\"]?)?(\\s*[0-9|\\-]+[:|,]\\s*[A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|’|*|_|']+[.|”|’|\"|]?)?(([0-9]+\\.?\\s*)+([A-Z]:)?,?\\s*[A-Z|a-z][a-z|A-Z|\\d|\\s|\\-|:|,|.|?|@|/|’|*|_|']+[.|”|’|\"]?)?";
		
		//String patternTitle = "\\s?(\\d*\\.?)?“?\\\\?\"?([0-9]+\\s?\\?\\s?)?\\s?[A-Z][a-z|A-Z|0-9|\\s|\\-|:|,|(|)|?|@|/|‘|’|*]+[.|”|\"]?(\\s?[0-9]+:\\s?)?([a-z]+\\s?)?([A-Z][a-z|A-Z|\\d|\\s|\\-|:|,|(|)|?|@|/|‘|’|*]+\\.?)?";
		
		//String patternAuthor = "((\\-?[A-Z](\\-[A-Z])?\\.\\s?([a-z]+\\s?)?)+[A-Z][a-z|A-Z|\\-]+\\s?([A-Z|a-z]+\\s?)*,?\\s?)+\\s?[a|A][n|N][d|D]\\s?(\\-?[A-Z]\\.\\s?)*[A-Z|a-z][a-z|A-Z|\\-]+\\s?(\\s*[A-Z|a-z|\\-]+)?[.|,|\\s]|((\\-?[A-Z]\\.\\s?)+[A-Z][a-z|A-Z|\\-]+\\s?(\\s*[A-Z|a-z]+)?,?\\s?)+[.|,|\\s]|([A-Z][a-z|A-Z|\\-]+,\\s?(\\-?[A-Z]\\.,?\\s?)+([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-]+,\\s?(\\-?[A-Z]\\.?,?\\s?)+[.|,|\\s|:|?]|(([A-Z][a-z|A-Z|\\-]+\\s?)+(\\s*[a-z]+\\.?)?\\s?,?\\s?(\\-?[A-Z][.|,],?\\s?([a-z]+\\s?\\.?)*)+)+[.|,|:|?]?|([A-Z][a-z|A-Z|\\-]+,?\\s?(\\s?[a-z|0-9]*\\s?)?)+\\.?(\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-]+,?\\s?([A-Z][a-z|\\-]+\\s?)?)?[.|,|\\s|:|?]?";
		
		// Version 1.0
		//String patternAuthor = "((\\-?[A-Z](\\-[A-Z])?\\.\\s?([a-z]+\\s?)*)+[A-Z][a-z|A-Z|\\-]+\\s?([A-Z|a-z]+\\s?)*,?\\s?)+\\s?[a|A][n|N][d|D]\\s?(\\-?[A-Z]\\.\\s?)*[A-Z|a-z][a-z|A-Z|\\-]+\\s?(\\s*[A-Z|a-z|\\-]+)?(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s]|((\\-?[A-Z]\\.\\s?)+[A-Z][a-z|A-Z|\\-]+\\s?(\\s*[A-Z|a-z]+)?,?\\s?)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s]|([A-Z][a-z|A-Z|\\-]+\\s?)+[a|A][n|N][d|D]\\s?([A-Z][a-z|A-Z|\\-]+\\s?)+,|([A-Z][a-z|A-Z|\\-]+,\\s+)+|(([A-Z][a-z|A-Z|\\-]+\\s?)+,\\s*)+[a|A][n|N][d|D]\\s*(([A-Z][a-z|A-Z|\\-]+\\s?)+,\\s*)+|([A-Z][a-z|A-Z|\\-]+\\s?)+,|(([A-Z][a-z|A-Z|\\-]+,?\\s*)+\\s?(\\-?[A-Z]\\.,?\\s?)*([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+\\s?[a|A][n|N][d|D]\\s?([A-Z][a-z|A-Z|\\-]+,?\\s?(\\-?[A-Z]\\.,?\\s?)*)+(\\([A-Z|a-z]+\\.?\\))?[.|,|:|?]|[A-Z][a-z|A-Z|\\-]+,?\\s*(\\-?[A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-]+,|(([A-Z][a-z|A-Z|\\-]+[,|\\s]\\s?)+\\s?(\\-?[A-Z]\\.,?\\s?)*([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+\\s?&\\s?([A-Z][a-z|A-Z|\\-]+,?\\s?(\\-?[A-Z]\\.,?\\s?)*)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]?|(([A-Z][a-z|A-Z|\\-]+\\s?)+(\\s*[a-z]+\\.?)?\\s?,?\\s?(\\-?[A-Z][.|,][,|;]?\\s?([a-z]+\\s?\\.?\\s?)*)+)+(\\s?\\([A-Z|a-z]+\\.?\\))?[.|,|:|?]?|([A-Z][a-z|A-Z|\\-]+(\\s?[a-z]+\\s?)*,?\\s?)+\\.?(\\s*[A-Z]{2,}\\s?[0-9]*\\s*)?(\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-]+,?\\s?([A-Z][a-z|\\-]+\\s?)?)?(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]?";
		
		// Version 1.1
		String patternAuthor = "[A-Z][a-z|A-Z|\\-|\\s|.|,|&|’|`]+[“|\"]|((\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’]+\\s*)+\\s*[a|A][n|N][d|D]\\s*((\\-?[A-Z]\\.\\s*)+[A-Z][a-z|A-Z|\\-|’]+\\s*)+[.|,]?" +
				"|((\\-?[A-Z](\\-[A-Z])?\\.\\s?([a-z]+\\s?)*)+[A-Z]\\s?[a-z|A-Z|\\-|’]+(\\s+[A-Z|a-z]+\\s?)*,?\\s*)+\\s*[a|A][n|N][d|D]\\s*(\\-?[A-Z]\\.\\s?)*[A-Z|a-z][a-z|A-Z|\\-|’]+\\s?(\\s+[A-Z|a-z|\\-]+)?(\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s]" +
				"|((\\-?[A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-|’|\\s]+,?\\s*)+(\\s*[e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s]" +
				"|([A-Z][a-z|A-Z|\\-]+\\s*)+[a|A][n|N][d|D]\\s*([A-Z][a-z|A-Z|\\-]+\\s*)+[,|.]" +
				"|[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\s[a|A][n|N][d|D]\\s[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s*[A-Z][a-z|A-Z|\\-|’]+\\." +
				"|([A-Z][a-z|A-Z|\\-|’]+\\s*(\\-?[A-Z]\\.,?\\s*)+\\s*[A-Z][a-z|A-Z|\\-|’]+,?\\s*)+[a|A][n|N][d|D]\\s*[A-Z][a-z|A-Z|\\-|’]+\\s*(\\-?[A-Z]\\.,?\\s*)+\\s*[A-Z][a-z|A-Z|\\-|’]+,\\s*" +
				"|([A-Z][a-z|A-Z|\\-|’]+,?\\s*(\\-?[A-Z]\\.,?\\s*)+)+[a|A][n|N][d|D]\\s*((\\-?[A-Z]\\.,?\\s?)+[A-Z][a-z|A-Z|\\-|’]+,?\\s*)+[.|,]?" +
				"|([A-Z][a-z|A-Z|\\-|’]+,?\\s*)+[a|A][n|N][d|D]\\s*(([A-Z]\\.\\s*[a-z]+\\s*)?[A-Z][a-z|A-Z|\\-|’]+,?\\s*)+[.|,]?|([A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-]+\\s+[e|E][t|T].{1,2}[a|A][l|L]." +
				"|(([A-Z]\\s+)+[A-Z][a-z|A-Z|\\-|’]+\\s*)+(\\s*[a|A][n|N][d|D]\\s*([A-Z]\\s+)+[A-Z][a-z|A-Z|\\-|’]+)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s]" +
				"|[A-Z][a-z]+\\s([A-Z][a-z]+\\s*)+[.|,|\\s]" +
				"|((\\-?[A-Z]\\.\\s*)+[A-Z|a-z][a-z|A-Z|\\-|’]+(\\s*[A-Z|a-z]+)?(\\s*\\([E|e][d|D][s|S]?\\.?\\)\\.?)?,?\\s?)+(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s]?" +
				"|([A-Z][a-z|A-Z|\\-|’]+,\\s*(\\-?[A-Z]\\.,?\\s*)+([e|E][t|T].{1,2}[a|A][l|L]\\.?)?)+\\s*[a|A][n|N][d|D]\\s*[A-Z][a-z|A-Z|\\-|\\s|’]+,\\s*(\\-?[A-Z]\\.,?\\s?)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]" +
				"|([A-Z][a-z|A-Z|\\-|’]+[,|\\s]\\s?(\\-?[A-Z]\\.,?\\s?)+([e|E][t|T]\\s?[a|A][l|L]\\.?)?)+\\s?&\\s?[A-Z][a-z|A-Z|\\-|’]+,\\s?(\\-?[A-Z]\\.?,?\\s?)+(\\([A-Z|a-z]+\\.?\\))?[.|,|\\s|:|?]" +
				"|([A-Z][a-z|A-Z|\\-|’]+,?\\s?)+\\s*[a|A][n|N][d|D]\\s*([A-Z][a-z|A-Z|\\-|’]+,?\\s*)+[.|,]?" +
				"|[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\s[a|A][n|N][d|D]\\s[A-Z][a-z|A-Z|\\-|’]+\\s[A-Z]\\.\\s[A-Z][a-z|A-Z|\\-|’]+\\." +
				"|([A-Z]\\.,?\\s*)+[A-Z][a-z|A-Z|\\-]+\\s+[e|E][t|T].{1,2}[a|A][l|L].|([A-Z|a-z][a-z|A-Z|\\-|`|’]+(\\s+[A-Z|a-z]+\\.?)?[\\s|,]?\\s*(\\-?[A-Z][.|,][,|;]?\\s?)+)+([e|E][t|T].{1,2}[a|A][l|L]\\.?)?(\\([A-Z|a-z]+\\.?\\))?[.|,|:|?]?" +
				"|([A-Z][a-z|A-Z|0-9|\\-|/|’]+(\\s*[a-z]+)*,?\\s?)+\\.?(\\s*[A-Z]{2,}\\s?[0-9]*\\s*)?(\\s?[a|A][n|N][d|D]\\s?[A-Z][a-z|A-Z|\\-|’]+,?\\s?([A-Z][a-z|\\-]+\\s?)?)?(\\([A-Z|a-z]+\\.?\\)\\.?)?[.|,|\\s|:|?]?";
		
		String patternYear = "[1|2][0|8|9]\\d{2}[a-z]?[.|)|,]";
		
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pp = Pattern.compile("\\+");
			Matcher mpp = pp.matcher(refInfoTable[i][1]);
						
			while(mpp.find()) {
				mpp.appendReplacement(sb, "\\\\+");
				match = true;
			}
									
			mpp.appendTail(sb);
						
			if(match) {
				refInfoTable[i][1] = sb.toString().trim();
			}		
		}
		
		// Extraction :: Reference eliminating citation symbol
		String[] tempRefTable = new String[refSize];
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pt = Pattern.compile("\\["+refInfoTable[i][1]+"\\s*]\\s*");
			Matcher mpt = pt.matcher(refInfoTable[i][0]);
						
			while(mpt.find()) {
				mpt.appendReplacement(sb, "");
				match = true;
			}
									
			mpt.appendTail(sb);
						
			if(match) {
				tempRefTable[i] = sb.toString().trim();
				System.out.println((i+1)+"-th ref.: "+tempRefTable[i]);
			}		
		}
		System.out.println("****************************");
		
		// Extraction :: Author
		for(int i=0; i<refSize; i++) {
			Pattern pa = Pattern.compile(patternAuthor);
			Matcher mpa = pa.matcher(tempRefTable[i]);
			if(mpa.find()) {
				refInfoTable[i][3] = tempRefTable[i].substring(mpa.start(), mpa.end()).trim();
				System.out.println((i+1)+"-th author: "+refInfoTable[i][3]);
			}
			else {
				refInfoTable[i][3] = "";
				System.out.println((i+1)+"-th author: "+refInfoTable[i][3]);
			}
		}
		
		// Substitute ( with \\(
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
										
			Pattern pOpenBrac = Pattern.compile("[(]");
			Matcher mpOpenBrac = pOpenBrac.matcher(refInfoTable[i][3]);
											
			while(mpOpenBrac.find()) {
				mpOpenBrac.appendReplacement(sb, "\\\\(");
				match = true;
			}
															
			mpOpenBrac.appendTail(sb);
											
			if(match) {
				refInfoTable[i][3] = sb.toString().trim();
			}		
		}
								
		// Substitute ) with \\)
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
											
			Pattern pClosedBrac = Pattern.compile("[)]");
			Matcher mpClosedBrac = pClosedBrac.matcher(refInfoTable[i][3]);
														
			while(mpClosedBrac.find()) {
				mpClosedBrac.appendReplacement(sb, "\\\\)");
				match = true;
			}
															
			mpClosedBrac.appendTail(sb);
														
			if(match) {
				refInfoTable[i][3] = sb.toString().trim();
			}		
		}
		System.out.println("****************************");
				
		// Extraction :: Author + Title
		for(int i=0; i<refSize; i++) {
			Pattern pat = Pattern.compile(refInfoTable[i][3]+patternTitle);
			Matcher mpat = pat.matcher(tempRefTable[i]);
			if(mpat.find()) {
				tempRefTable[i] = tempRefTable[i].substring(mpat.start(), mpat.end()).trim();
				System.out.println((i+1)+"-th author and title: "+tempRefTable[i]);
			}
			else {
				System.out.println((i+1)+"-th author and title: "+tempRefTable[i]);				
			}
		}
		System.out.println("****************************");
		
		// Extraction :: Title
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pt = Pattern.compile(refInfoTable[i][3]);
			Matcher mpt = pt.matcher(tempRefTable[i]);
						
			while(mpt.find()) {
				mpt.appendReplacement(sb, "");
				match = true;
			}
									
			mpt.appendTail(sb);
						
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
				System.out.println((i+1)+"-th title: "+refInfoTable[i][2]);
			}		
		}
		System.out.println("****************************");
		
		// Extraction :: Year
		for(int i=0; i<refSize; i++) {
			Pattern py = Pattern.compile(patternYear);
			Matcher mpy = py.matcher(refInfoTable[i][0]);
			if(mpy.find()) {
				refInfoTable[i][4] = refInfoTable[i][0].substring(mpy.start(), mpy.end()).trim();
				System.out.println((i+1)+"-th year: "+refInfoTable[i][4]);
			}
			else {
				refInfoTable[i][4] = "";
				System.out.println((i+1)+"-th year: "+refInfoTable[i][4]);
			}
		}	
		System.out.println("****************************");
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[p|P][p|P][.|,|\\s].*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("([I|i][N|n]\\s*)?[P|p][r|R][o|O][c|C]?[\\s|.|,].*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("([I|i][N|n]\\s*)?[P|p][r|R][o|O][c|C][e|E][e|E][d|D][i|I][n|N][g|G][s|S]?.*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
			
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[e|E][d|D][i|I][t|T][o|O][r|R][s|S]?");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("\\s[v][o][l]\\..*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
	
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[S][I][G][M][O][D].*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[S][I][G][C][H][I].*");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
			
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[1|2][0|8|9]\\d{2}[a-z]?");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}		
		
		// Extraction :: Title 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pt = Pattern.compile("[“|”|\"|\\\\|?|\\]|(|)|:|;|,|.]");
			Matcher mpt = pt.matcher(refInfoTable[i][2]);
				
			while(mpt.find()) {
				mpt.appendReplacement(sb, " ");
				match = true;
			}
						
			mpt.appendTail(sb);
				
			if(match) {
				refInfoTable[i][2] = sb.toString().trim();
			}			
		}
	
		// Extraction :: Author 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
			
			Pattern pa = Pattern.compile("[:|?]");
			Matcher mpa = pa.matcher(refInfoTable[i][3]);
				
			while(mpa.find()) {
				mpa.appendReplacement(sb, " ");
				match = true;
			}
						
			mpa.appendTail(sb);
				
			if(match) {
				refInfoTable[i][3] = sb.toString().trim();
			}			
		}
		
		// Extraction :: Year 
		for(int i=0; i<refSize; i++) {
			match = false;
			sb = new StringBuffer();
					
			Pattern py = Pattern.compile("[.|)|,]");
			Matcher mpy = py.matcher(refInfoTable[i][4]);
						
			while(mpy.find()) {
				mpy.appendReplacement(sb, " ");
				match = true;
			}
								
			mpy.appendTail(sb);
						
			if(match) {
				refInfoTable[i][4] = sb.toString().trim();
			}			
		}		
		
		try {
			fr = new FileReader("sampleIn.txt");
	    	fileBr = new BufferedReader(fr);
	    	fw = new FileWriter("body.txt");
			fileBw = new BufferedWriter(fw);
		    	    	
	    	str = "";
	    		  
	    	OuterLoop:
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
	    			  		    
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		if(temp.matches(".*R\\s?[e|E]\\s?[f|F]\\s?[e|E]\\s?[r|R]\\s?[e|E]\\s?[n|N]\\s?[c|C]\\s?[e|E]\\s?[s|S].*") || temp.matches(".*L\\s?[i|I]\\s?[t|T]\\s?[e|E]\\s?[r|R]\\s?[a|A]\\s?[t|T]\\s?[u|U]\\s?[r|R].*")) {
		    			break OuterLoop;
		    		}
		    		fileBw.write(temp);
		    		fileBw.write(" ");
		    		//fileBw.newLine();
		    	}
	    	}	    	
	    	
	    	fileBr.close();
	    	fr.close();
	    	
	    	fileBw.close();
	    	fw.close();
	 
		} catch(Exception e) {
			System.out.println(e);  
		}
		
		// Citation extraction from body
		System.out.println("*** Citation Extraction from Body ***");
		try {
			fr = new FileReader("body.txt");
	    	fileBr = new BufferedReader(fr);
			    			    	    	
	    	str = "";
			    	
	    	while((str = fileBr.readLine())!=null) {
	    		StringTokenizer buf = new StringTokenizer(str, "\n");
			    			
	    		temp="";
		    	while(buf.hasMoreTokens()) {
		    		temp = buf.nextToken();
		    		m = p.matcher(temp);
		    		while(m.find()) {
		    			tempTable[tempSize] = temp.substring(m.start(), m.end()).trim();
		    			System.out.println((tempSize+1)+"-th element => "+tempTable[tempSize]);
		    			tempSize++;
		    		}
		    	}
	    	}
		    				    	
	    	fileBr.close();
	    	fr.close();
	    } catch(Exception e) {
			System.out.println(e);  
		}
		System.out.println("****************************");
		
		for(int i=0; i<tempSize; i++) {
			match = false;
			sb = new StringBuffer();
				
			Pattern pp = Pattern.compile("\\+");
			Matcher mpp = pp.matcher(tempTable[i]);
						
			while(mpp.find()) {
				mpp.appendReplacement(sb, "\\\\+");
				match = true;
			}
									
			mpp.appendTail(sb);
						
			if(match) {
				tempTable[i] = sb.toString().trim();
				System.out.println((i+1)+"-th tempTable: "+tempTable[i]);
			}		
		}
		
		// Citation Analysis
		System.out.println("*** Citation Analysis ***");
		
		// Case#1. Single Short Name-Year Pattern
		// Format: [NameYear]
		String reg1CaseShortNameYear = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?]";
		// Case#2. Multiple Short Name-Year Pattern - Type1
		// Format: [NameYear1, NameYear2, ..., NameYearn]
		String reg2CaseShortNameYear = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?(.\\s*[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?)+]";
		// Case#3. Multiple Short Name-Year Pattern - Type2
		// Format. [NameYear1], [NameYear2], ..., [NameYearn] or [NameYear1], [NameYear2], ..., [NameYearn]
		String reg3CaseShortNameYear = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?](,?\\s*\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[1-9]{0,2}[a-z]?)?(\\s*,?\\s?[a-z]+\\.\\s?(\\-?\\d+)+)?])+";
		
		
		for(int i=0; i<tempSize; i++) {
			// Case#1. Single Reference Number
			// Format: [N]
			if(tempTable[i].matches(reg1CaseShortNameYear)) {
				System.out.println("=> "+tempTable[i]);
								
				String tempShortNameYear = "";
				String basicShortNameYearPattern = "\\[[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?";
				Pattern pb = Pattern.compile(basicShortNameYearPattern);
				Matcher mpb = pb.matcher(tempTable[i]);
				if(mpb.find()) {
					tempShortNameYear = tempTable[i].substring(mpb.start()+1, mpb.end()).trim();
					System.out.println("=> "+tempShortNameYear);
				}
						
				for(int j=0; j<refSize; j++) {
					if(tempShortNameYear.compareToIgnoreCase(refInfoTable[j][1])==0) {
						refInfoTable[j][5] = Double.toString(Double.parseDouble(refInfoTable[j][5])+1.0);
						System.out.println(refInfoTable[j][1]+"'s count => "+refInfoTable[j][5]);
						System.out.println("----------------------");
					}
				}
			}
			
			// Case#2. Multiple Short Name-Year Pattern - Type1
			// Format: [NameYear1, NameYear2, ..., NameYearn]
			if(tempTable[i].matches(reg2CaseShortNameYear)) {
				System.out.println("=> "+tempTable[i]);
							
				weight=0;
				String basicShortNameYearPattern = "[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?";
				Pattern pb = Pattern.compile(basicShortNameYearPattern);
				Matcher mpb = pb.matcher(tempTable[i]);
				while(mpb.find()) {
					weight++;					
				}
				
				String[] tempShortNameYear = new String[weight];
				int tempIndex = 0;
				pb = Pattern.compile(basicShortNameYearPattern);
				mpb = pb.matcher(tempTable[i]);
				while(mpb.find()) {
					tempShortNameYear[tempIndex++] = tempTable[i].substring(mpb.start(), mpb.end()).trim();	
				}
				
				for(int j=0; j<tempIndex; j++) {
					for(int k=0; k<refSize; k++) {
						if(tempShortNameYear[j].compareToIgnoreCase(refInfoTable[k][1])==0) {
							refInfoTable[k][5] = Double.toString(Double.parseDouble(refInfoTable[k][5])+1/(double)weight);
							System.out.println(refInfoTable[k][1]+"'s count => "+refInfoTable[k][5]);
						}
					}
				}
				System.out.println("----------------------");
			}
			
			// Case#3. Multiple Short Name-Year Pattern - Type2
			// Format. [NameYear1], [NameYear2], ..., [NameYearn] or [NameYear1], [NameYear2], ..., [NameYearn]
			if(tempTable[i].matches(reg3CaseShortNameYear)) {
				System.out.println("=> "+tempTable[i]);
			
				weight=0;
				String basicShortNameYearPattern = "[A-Z][a-z|A-Z|\\\\|\\+]+(\\s*[0-9]{1,2}[a-z]?)?";
				Pattern pb = Pattern.compile(basicShortNameYearPattern);
				Matcher mpb = pb.matcher(tempTable[i]);
				while(mpb.find()) {
					weight++;					
				}
				
				String[] tempShortNameYear = new String[weight];
				int tempIndex = 0;
				pb = Pattern.compile(basicShortNameYearPattern);
				mpb = pb.matcher(tempTable[i]);
				while(mpb.find()) {
					tempShortNameYear[tempIndex++] = tempTable[i].substring(mpb.start(), mpb.end()).trim();					
				}
				
				for(int j=0; j<tempIndex; j++) {
					for(int k=0; k<refSize; k++) {
						if(tempShortNameYear[j].compareToIgnoreCase(refInfoTable[k][1])==0) {
							refInfoTable[k][5] = Double.toString(Double.parseDouble(refInfoTable[k][5])+1/(double)weight);
							System.out.println(refInfoTable[k][1]+"'s count => "+refInfoTable[k][5]);
						}
					}
				}
				System.out.println("----------------------");				
			}
		}		
		System.out.println("****************************");
				
		NumberShortNameYearBasedDescSort(refInfoTable, refSize);
		
		System.out.println("*** Final Citation Results ***");
		System.out.println("****************************");
		// Floating-point number :: Round up at 3th position
		for(int i=0; i<refSize; i++) {
			double finalCount = Double.parseDouble(String.format("%.3f", Double.parseDouble(refInfoTable[i][5])));
			refInfoTable[i][5] = Double.toString(finalCount);
			System.out.println("=> Ref. Paper       : "+refInfoTable[i][0]);
			System.out.println("   - Citation Symbol: "+refInfoTable[i][1]);
			System.out.println("   - Title          : "+refInfoTable[i][2]);
			System.out.println("   - Author         : "+refInfoTable[i][3]);
			System.out.println("   - Year           : "+refInfoTable[i][4]);
			System.out.println("   - Count          : "+refInfoTable[i][5]);
			System.out.println("----------------------");
		}			
		
		/*
		// Citation results :: Process to store the Hash table
		for(int i=0; i<refSize; i++) {
			citationResultsStorageProcess(refInfoTable[i][2], Double.parseDouble(refInfoTable[i][5]));
		}		
		// Citation results :: Check for citation results using Hash table
		System.out.println("*** Final Citation Results using Hash table ***");
		citationResultsView();
		*/
		
		// Eliminate Non-PCDATA
		for(int i=0; i<6; i++) {
			
			// Non-PCDATA : &
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType1);
				Matcher mNonPCDATA = nonPCDATA.matcher(refInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&amp;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					refInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : <
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType2);
				Matcher mNonPCDATA = nonPCDATA.matcher(refInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&lt;");
					match = true;
				}
				
				mNonPCDATA.appendTail(sb);
				
				if(match) {
					refInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : >
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
			
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType3);
				Matcher mNonPCDATA = nonPCDATA.matcher(refInfoTable[j][i]);
				
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&gt;");
					match = true;
				}
						
				mNonPCDATA.appendTail(sb);
					
				if(match) {
					refInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : "
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
				
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType4);
				Matcher mNonPCDATA = nonPCDATA.matcher(refInfoTable[j][i]);
					
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&quot;");
					match = true;
				}
								
				mNonPCDATA.appendTail(sb);
						
				if(match) {
					refInfoTable[j][i] = sb.toString().trim();
				}			
			}
			
			// Non-PCDATA : '
			for(int j=0; j<refSize; j++) {
				match = false;
				sb = new StringBuffer();
					
				Pattern nonPCDATA = Pattern.compile(nonPCDATAType5);
				Matcher mNonPCDATA = nonPCDATA.matcher(refInfoTable[j][i]);
							
				while(mNonPCDATA.find()) {
					mNonPCDATA.appendReplacement(sb, "&apos;");
					match = true;
				}
										
				mNonPCDATA.appendTail(sb);
							
				if(match) {
					refInfoTable[j][i] = sb.toString().trim();
				}			
			}			
		}		
		
		try {
			fw = new FileWriter("citationResults.xml");
			fileBw = new BufferedWriter(fw);
			
			String xmlPrologInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
			String xslStyleSheetInfo = "\n<?xml-stylesheet type=\"text/xsl\" href=\"ShortNameYearCitationResultsView.xsl\"?>\n\n";
			fileBw.write(xmlPrologInfo);
			fileBw.write(xslStyleSheetInfo);
			fileBw.write("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"./citationResultsSchema.xsd\">\n");			
			for(int i=0; i<refSize; i++) {
				fileBw.write("<refInfo>\n");
				fileBw.write("<reference>"); fileBw.write(refInfoTable[i][0]); fileBw.write("</reference>\n");
				fileBw.write("<refSymbol>"); fileBw.write(refInfoTable[i][1]); fileBw.write("</refSymbol>\n");
				fileBw.write("<refTitle>"); fileBw.write(refInfoTable[i][2]); fileBw.write("</refTitle>\n");
				fileBw.write("<refAuthor>"); fileBw.write(refInfoTable[i][3]); fileBw.write("</refAuthor>\n");
				fileBw.write("<refYear>"); fileBw.write(refInfoTable[i][4]); fileBw.write("</refYear>\n");
				fileBw.write("<citationCount>"); fileBw.write(refInfoTable[i][5]); fileBw.write("</citationCount>\n");
				fileBw.write("</refInfo>\n\n");
			}
			fileBw.write("</root>");
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				fileBw.close();
				fw.close();
			} catch(Exception e) {
				
			}
		}		
	}	

	/*******************************************************************************
	 * Method Name : CitationResultsStorageProcess
	 * Purpose     : Store the citation resutls in the Hash table 
	 * Parameters  : Ref. title and its count
	 * Return      : None
	 *******************************************************************************/ 
	
	public void citationResultsStorageProcess(String ref, Double count) {
		ht.put(ref, count);	
	}
	
	/*******************************************************************************
	 * Method Name : CitationResultsView
	 * Purpose     : View the citation resutls stored in the Hash table 
	 * Parameters  : None
	 * Return      : None
	 *******************************************************************************/ 

	public void citationResultsView() {
		int refNum=0;
		
		// Citation results :: Check for citation results using Hash table
		Enumeration<String> Enum = ht.keys();
		while(Enum.hasMoreElements()) {
			Object k = Enum.nextElement();
			Object v = ht.get(k);
			System.out.println((++refNum)+"-th ref: "+k+" => count: "+v);
		}
	}
}