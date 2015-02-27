/*
	Spam detection using a Naive Bayes classifier.
*/

import java.io.*;
import java.util.*;

public class NBSpamDetect
{
	// This a class with two counters (for ham and for spam)
	static class Multiple_Counter
	{
		int counterHam = 0;
		int counterSpam = 0;
	}


	public static void main(String[] args)
	throws IOException
	{
		
		// Location of the directory (the path) taken from the cmd line (first arg)
		File dir_location      = new File( args[0] );
		
		// Listing of the directory (should contain 2 subdirectories: ham/ and spam/)
		File[] dir_listing     = new File[0];

		// Check if the cmd line arg is a directory and list it
		if ( dir_location.isDirectory() )
		{
			dir_listing = dir_location.listFiles();
		}
		else
		{
			System.out.println( "- Error: cmd line arg not a directory.\n" );
		        Runtime.getRuntime().exit(0);
		}
		
		// Listings of the two sub-directories (ham/ and spam/)
		File[] listing_ham = new File[0];
		File[] listing_spam    = new File[0];
		
		// Check that there are 2 sub-directories
		boolean hamFound = false; boolean spamFound = false;
		for (int i=0; i<dir_listing.length; i++) {
			if (dir_listing[i].getName().equals("ham")) {
				listing_ham = dir_listing[i].listFiles(); 
				hamFound = true;
			}
			else if (dir_listing[i].getName().equals("spam")) { 
				listing_spam = dir_listing[i].listFiles(); 
				spamFound = true;
			}
		}
		if (!hamFound || !spamFound) {
			System.out.println( "- Error: specified directory does not contain ham and spam subdirectories.\n" );
		        Runtime.getRuntime().exit(0);
		}

		// Print out the number of messages in ham and in spam
		//System.out.println( "\t number of ham messages is: " + listing_ham.length );
		//System.out.println( "\t number of spam messages is: "    + listing_spam.length );
		
		// Location of the directory (the path) taken from the cmd line (first arg)
		File test_dir_location      = new File( args[1] );
		
		// Listing of the directory (should contain 2 subdirectories: ham/ and spam/)
		File[] test_dir_listing     = new File[0];

		// Check if the cmd line arg is a directory and list it
		if ( test_dir_location.isDirectory() )
		{
			test_dir_listing = test_dir_location.listFiles();
		}
		else
		{
			System.out.println( "- Error: cmd line arg not a directory.\n" );
		        Runtime.getRuntime().exit(0);
		}
		
		// Listings of the two sub-directories (ham/ and spam/)
		File[] test_listing_ham = new File[0];
		File[] test_listing_spam    = new File[0];
		
		// Check that there are 2 sub-directories
		boolean test_hamFound = false; boolean test_spamFound = false;
		for (int i=0; i<test_dir_listing.length; i++) {
			if (test_dir_listing[i].getName().equals("ham")) { 
				test_listing_ham = test_dir_listing[i].listFiles(); 
				test_hamFound = true;
			}
			else if (test_dir_listing[i].getName().equals("spam")) { 
				test_listing_spam = test_dir_listing[i].listFiles(); 
				test_spamFound = true;
			}
		}
		if (!test_hamFound || !test_spamFound) {
			System.out.println( "- Error: specified test directory does not contain ham and spam subdirectories.\n" );
		        Runtime.getRuntime().exit(0);
		}

		// Print out the number of messages in ham and in spam
		//System.out.println( "\t number of test ham messages is: " + test_listing_ham.length );
		//System.out.println( "\t number of test spam messages is: "    + test_listing_spam.length );

		System.out.println("[#]              True Spam \t True Ham");
		System.out.println("Classified Spam  TP            " + "\t " + "FP");
		System.out.println(" Classified Ham  FN             " + " " + "TN");
		System.out.println("");
		
		for(int c = 1; c <= 3; c++) { //for each confusion matrix (total of three)
		
		/*
		 * BEGIN TRAIN
		 */
			
		// Create a hash table for the vocabulary (word searching is very fast in a hash table)
		Hashtable<String,Multiple_Counter> vocab = new Hashtable<String,Multiple_Counter>();
		Multiple_Counter old_cnt   = new Multiple_Counter();

		// Read the e-mail messages
		// The ham mail
		for ( int i = 0; i < listing_ham.length; i ++ ) {
			FileInputStream i_s = new FileInputStream( listing_ham[i] );
			BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
			String line;
			String word = null;
			
        	        while ((line = in.readLine()) != null)					// read a line
			{
				StringTokenizer st = new StringTokenizer(line);			// parse it into words
				
				while (st.hasMoreTokens())
				{
					word = st.nextToken().replaceAll("[^a-zA-Z]","");
					if (c==2) { word = word.toLowerCase(); }
					if (c==3) {
						if(line.startsWith("From:") || line.startsWith("To:") || line.startsWith("Cc:") || line.startsWith("Subject:")) { 
							if(word.equals("From") || word.equals("To") || word.equals("Cc") || word.equals("Subject")) { word = ""; }
						} else {
							word = "";
						}
						
					}
					
          				if ( !word.equals("") ) { // if string isn't empty
						if ( vocab.containsKey(word) )				// check if word exists already in the vocabulary
						{
							old_cnt = vocab.get(word);	// get the counter from the hashtable
							old_cnt.counterHam ++;			// and increment it
					
							vocab.put(word, old_cnt);
						}
						else
						{
							Multiple_Counter fresh_cnt = new Multiple_Counter();
							fresh_cnt.counterHam = 2;
							fresh_cnt.counterSpam    = 1;
						
							vocab.put(word, fresh_cnt);			// put the new word with its new counter into the hashtable
						}
	        			}
				}
			}

                	in.close();
		}
		// The spam mail
		for ( int i = 0; i < listing_spam.length; i ++ ) {
			FileInputStream i_s = new FileInputStream( listing_spam[i] );
			BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
			String line;
			String word = null;
			
        	        while ((line = in.readLine()) != null)					// read a line
			{
				StringTokenizer st = new StringTokenizer(line);			// parse it into words
				while (st.hasMoreTokens())
				{
					word = st.nextToken().replaceAll("[^a-zA-Z]","");
					if (c==2) { word = word.toLowerCase(); }
					if (c==3) {
						if(line.startsWith("From:") || line.startsWith("To:") || line.startsWith("Cc:") || line.startsWith("Subject:")) { 
							if(word.equals("From") || word.equals("To") || word.equals("Cc") || word.equals("Subject")) { word = ""; }
						} else {
							word = "";
						}
						
					}
					
				  if ( ! word.equals("") ) {	
						if ( vocab.containsKey(word) )		// check if word exists already in the vocabulary
						{
							old_cnt = vocab.get(word);	// get the counter from the hashtable
							old_cnt.counterSpam ++;			// and increment it
					
							vocab.put(word, old_cnt);
						}
						else
						{
							Multiple_Counter fresh_cnt = new Multiple_Counter();
							fresh_cnt.counterHam = 1;
							fresh_cnt.counterSpam = 2;
						
							vocab.put(word, fresh_cnt);			// put the new word with its new counter into the hashtable
						}
					}
				}
			}

                	in.close();
		}
		
		// Print out the hash table
		int nWordsHam = 0, nWordsSpam = 0;
		
		for (Enumeration<String> e = vocab.keys(); e.hasMoreElements(); ) {	
			String word;
			
			word = e.nextElement();
			old_cnt  = vocab.get(word);
			/*
			System.out.println( word + " | in ham: " + old_cnt.counterHam + 
			                             " in spam: "    + old_cnt.counterSpam);
			*/
			nWordsHam  += old_cnt.counterHam;
			nWordsSpam += old_cnt.counterSpam;
		}
		
		/*
		 * END TRAIN
		 */
		
		/*
		 * COMPUTE PROBABILITIES
		 */
		
		// Prior probabilities must be computed from the number of ham and spam messages
		int nMessagesHam = listing_ham.length;
		int nMessagesSpam = listing_spam.length;
		int nMessagesTotal = nMessagesHam + nMessagesSpam;
		//double pHam = nMessagesHam/nMessagesTotal;
		double pHam = Math.log(nMessagesHam) - Math.log(nMessagesTotal);
		//double pSpam = (double) nMessagesSpam/nMessagesTotal;
		double pSpam = Math.log(nMessagesSpam) - Math.log(nMessagesTotal);
		// Conditional probabilities computed for every unique word
		// Add-1 smoothing implemented
		// Probabilities stored as log probabilities (log likelihoods).
		Hashtable<String,Double> pWi_Ham = new Hashtable<String,Double>();
		Hashtable<String,Double> pWi_Spam = new Hashtable<String,Double>();
		int nWordsHamSmoothed = nWordsHam + nMessagesHam;
		int nWordsSpamSmoothed = nWordsSpam + nMessagesSpam;
		String word = null;
		for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;) {
			word = e.nextElement();
			old_cnt  = vocab.get(word);
			//pWi_Ham.add((double) addOneSmoothing/nWordsHamSmoothed);
			pWi_Ham.put(word, Math.log(old_cnt.counterHam) - Math.log(nWordsHamSmoothed));
			//pWi_Spam.add((double) addOneSmoothing/nWordsSpamSmoothed);
			pWi_Spam.put(word, Math.log(old_cnt.counterSpam) - Math.log(nWordsSpamSmoothed));
		}
		
		/*
		 * BEGIN TEST
		 */
		
		// Read the e-mail messages
		// The ham mail
		int hamFalsePos = 0, hamTrueNeg = 0;
		for ( int i = 0; i < test_listing_ham.length; i ++ ) 
		{
			FileInputStream i_s = new FileInputStream( test_listing_ham[i] );
			BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
			String line;
			double currHamProb = pHam, currSpamProb = pSpam;
			
        	        while ((line = in.readLine()) != null)					// read a line
			{
				StringTokenizer st = new StringTokenizer(line);			// parse it into words
				while (st.hasMoreTokens())
				{
					word = st.nextToken().replaceAll("[^a-zA-Z]","");
					if (c==2) { word = word.toLowerCase(); }
					if (c==3) {
						if(line.startsWith("From:") || line.startsWith("To:") || line.startsWith("Cc:") || line.startsWith("Subject:")) { 
							if(word.equals("From") || word.equals("To") || word.equals("Cc") || word.equals("Subject")) { word = ""; }
						} else {
							word = "";
						}
					}
							
		        				if ( !word.equals("") ) { // if string isn't empty
								if ( vocab.containsKey(word) )				// check if word exists already in the vocabulary
								{
									currHamProb += pWi_Ham.get(word);
									currSpamProb+= pWi_Spam.get(word);
								}
			        			}
						}
					}

		                	in.close();
		                	
		                	//classify message
		                	if(currSpamProb > currHamProb) { 
		                		hamFalsePos++; 
		                	}
		                	else { 
		                		hamTrueNeg++; 
		                	}
				}
				
				// The spam mail
				int spamTruePos = 0, spamFalseNeg = 0;
				for ( int i = 0; i < test_listing_spam.length; i ++ )
				{
					FileInputStream i_s = new FileInputStream( test_listing_spam[i] );
					BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
					String line;
					double currHamProb = pHam, currSpamProb = pSpam;
					
		        	        while ((line = in.readLine()) != null)					// read a line
					{
						StringTokenizer st = new StringTokenizer(line);			// parse it into words
						while (st.hasMoreTokens())
						{
							word = st.nextToken().replaceAll("[^a-zA-Z]","");
							if (c==2) { word = word.toLowerCase(); }
							if (c==3) {
								if(line.startsWith("From:") || line.startsWith("To:") 
								    || line.startsWith("Cc:") || line.startsWith("Subject:")) { 
									
									if(word.equals("From") || word.equals("To") 
									    || word.equals("Cc") || word.equals("Subject")) { 
									    	
									    	word = ""; 
									}
									
								} else {
									word = "";
								}	
							}
						  if ( ! word.equals("") ) {	
								if ( vocab.containsKey(word) )		// check if word exists already in the vocabulary
								{
									currHamProb += pWi_Ham.get(word);
									currSpamProb+= pWi_Spam.get(word);
								}
							}
						}
					}

		                	in.close();
		                	
		                	//classify message
		                	if(currSpamProb > currHamProb) { 
		                		spamTruePos++;  	
		                	} else { 
		                		spamFalseNeg++; 
		                	}
		                	
				}
		
				System.out.println("["+c+"]"+"\t\t True Spam \t True Ham");
				System.out.println("Classified Spam  " + spamTruePos + "\t\t " + hamFalsePos);
				System.out.println(" Classified Ham  " + spamFalseNeg + "\t\t " + hamTrueNeg);
				if(c !=3 ) { 
					System.out.println(""); 
				}
				
			}
		/*
		 * END TEST
		 */
	}
}
