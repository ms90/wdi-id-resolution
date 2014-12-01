/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * Matches entities in two input maps, calculates scores, uses blocking if specified.
 * Call evaluate() with/without blocking and different min/max scores to compare the results.
 * evaluate() writes results into R, P, F1, runTime and result variables (accessible via getters).
 * To resolve identities within one map, call evaluate() with this map as 1st and 2nd arguments. 
 * Important assumption: ID uniqueness.  
 * */

package uma.wdi.ir;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import uma.wdi.ir.blocking.BlockingOperator;
import uma.wdi.ir.blocking.BlockingByYear;
import uma.wdi.ir.blocking.NoBlockingOperator;
import uma.wdi.ir.matching.LevensteinTitleMatcher;
import uma.wdi.ir.matching.AbstractMatcher;

public class Evaluator 
{
	/*
	 * Class for evaluating the matching strategy: matches two datasets, 
	 * computes and outputs precision, recall, F1 with respect to gold standard,
	 * as well as runtime, number of matching operations.
	 * Prepares .cvs for RapidMiner for rule learning.
	 *  @author Volha
	 *  @author Heiko
	 */
	
	private double runTime = -1;
	private double runTimeMin = -1; 
	private double P = -1;
	private double R = -1;
	private double F1 = -1;
    
	//
	private int correct = -1;
    private int matched = -1;
	
	private long countMatchingOperations = -1;
	
	private String separator = "---";
	
	// id1 separator id2, where id1 precedes id2 (e.g. id1---id2) => double 0..1 matching score
	private Map<String, Double> gold = new HashMap<String, Double>();
	private Set<String> result = new HashSet<String>();

	// for calculating the precision
	private Set<String> gold_ids = new HashSet<String>();

	/* Main function:  load gold standard -> calculate matching scores -> evaluate results */
	public boolean evaluate(NodeMap m1, NodeMap m2, String goldStandard, BlockingOperator bo, AbstractMatcher ma)
	{
		if (loadGold(goldStandard))
		{
			if (calculateScores(m1,m2,bo,ma))
			{
				return evaluateMatching();
			}
		}
		
		return false;
	}
	
	// Main function, with some default values
	public boolean evaluate(NodeMap m1, NodeMap m2, String goldStandard)
	{
		return evaluate(m1, m2, goldStandard, new NoBlockingOperator(), new LevensteinTitleMatcher());
	}	
	
	// Writes a CSV file that can be loaded in RapidMiner for linear regression 
	public boolean writeInputFileForLinearRegression(NodeMap m1, NodeMap m2, String goldStandard, String filenameOutput, List<AbstractMatcher> matchers)
	{
		loadGold(goldStandard);
		try {
			StringBuffer line = new StringBuffer();
			
			FileWriter fw = new FileWriter(filenameOutput);
			// write header
			for(AbstractMatcher matcher : matchers)
				line.append(matcher.getClass().getSimpleName() + ",");
			line.append("score");
			fw.write(line + System.lineSeparator());

			// load gold standard -> calculate matching scores -> evaluate results
			if (loadGold(goldStandard))
			{
				// do the true positives
				for(Map.Entry<String,Double> goldEntry : gold.entrySet()) {
					line = new StringBuffer();
					
					String[] ids = splitIDs(goldEntry.getKey());
					ANode n1 = m1.getNode(ids[0]);
					ANode n2 = m2.getNode(ids[1]);
					if(n1==null) 
					{
						// then it's the other way round
						n1 = m2.getNode(ids[0]);
						n2 = m1.getNode(ids[1]);
						if (n1 == null)
						{
							System.out.println("Cannot build input for linear regression: gold standard does not match the data");
							return false;
						}						
					}
					for(AbstractMatcher matcher : matchers)
						line.append(matcher.doMatch(n1, n2) + ",");
					fw.write(line + "1.0" + System.lineSeparator());						
				}
				
				// do the negative examples - exactly as many as positive
				int positiveExamples = gold.size();
				int negativeExamples = 0;
				List<String> ids1 = new ArrayList<String>(m1.getKeySet());
				List<String> ids2 = new ArrayList<String>(m2.getKeySet());
				Random random = new Random();

				while(negativeExamples<positiveExamples) {
					line = new StringBuffer();
					
					// select two random IDs
					int i1 = random.nextInt(ids1.size());
					int i2 = random.nextInt(ids2.size());
					
					String id1 = ids1.get(i1);
					String id2 = ids2.get(i2);
					
					// check if we accidentally hit a positive example
					if(gold.containsKey(concatAnyIDs(id1, id2)))
						continue;
					
					ANode n1 = m1.getNode(id1);
					ANode n2 = m2.getNode(id2);
					if(n1==null) {
						// then it's the other way round
						n1 = m2.getNode(id1);
						n2 = m1.getNode(id2);
					}
					for(AbstractMatcher matcher : matchers)
						line.append(matcher.doMatch(n1, n2) + ",");
					fw.write(line + "0.0" + System.lineSeparator());

					negativeExamples++;
				}
			}
			fw.close();
		} catch (IOException e) {
			System.out.println("could not write to file " + filenameOutput);
			return false;
		}
		return true;
	}
	
	// Calculate P,R,F1
	private boolean evaluateMatching()
	{
		double tp = 0; // number of true positives
        for (String entry : result) 
        {
    	    if (gold.containsKey(entry)) tp = tp + 1;
        }
	    R = tp/gold.size();
	    int prs = getPartialResultSize();
	    if (prs == 0) P = 0.0;
	    else P = tp/prs; // accounts for partial gold standard (differently  from P = tp/result.size())
	    if (R + P == 0) F1 = 0.0;
	    else F1 = 2*R*P/(R+P);
		
        matched = prs;
        correct = (int) tp;
        
		return true;
	}
	
	// Account for partial gold standard: count only the id pairs in the result at least one of which is in the gold standard  
	private int getPartialResultSize()
	{
		int prs = result.size();
		for (String entry : result)
		{
			String[] ids = splitIDs(entry);
			/* - another option, yielding higher precision, count only the id pairs in the result both of which are in the gold standard:
			* if (!gold_ids.contains(ids[0]) || !gold_ids.contains(ids[1])) prs--;
			*/ 
    	    if (!gold_ids.contains(ids[0]) && !gold_ids.contains(ids[1])) 
    	    {
    	    	// System.out.println("1 - "+ids[0]+", 2 - "+ids[1]);
    	    	prs--;
    	    }
        }
		return prs;
	}
	
	// Print results into console
	public boolean printResults(boolean printResList)
	{
		System.out.println("P = " + P);
		System.out.println("R = " + R);
		System.out.println("F1 = " + F1);
		System.out.println("Matching operations = " + countMatchingOperations);
		System.out.println("Runtime = " + runTime + " ms");   
		System.out.println("Runtime in min. = " + runTimeMin);
		if (printResList)
		{
			for (String id : result) 
	        {
	    	    System.out.println(id);
	        }
		}
		
		return true;
	}
	
	// Print results to file
	public boolean outputResultsToFile(String filename) {
		try 
		{
			FileWriter fw = new FileWriter(filename);
			for(String id : result)
			{
				String[] ids = splitIDs(id);
				fw.write(ids[0] + "," + ids[1] + System.lineSeparator());
			}				
			fw.close();
		} catch (IOException e) {
			System.out.println("could not write to file " + filename);
			return false;
		}
		return true;
	}

	// Load gold standard (matching pairs) from file fn, which contains comma-separated pairs of ids
	private boolean loadGold(String fn)
	{
		try 
		{
			File file = new File(fn);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				if (!line.isEmpty())
				{
					String[] ids = line.split(",");
					if (ids.length != 2)
					{
						System.out.println("ERROR: wrong format of the gold standard file near " + line);
						return false;	
					}
					// store list of ids in the gold standard
					if (!gold_ids.contains(ids[0])) gold_ids.add(ids[0]);
					if (!gold_ids.contains(ids[1])) gold_ids.add(ids[1]);					
					
					gold.put(concatAnyIDs(ids[0],ids[1]), 1.0);
				}
			}
			fileReader.close();
		} 
		catch (IOException e) 
		{
			// e.printStackTrace();
			System.out.println("ERROR: gold standard file " + fn + " not found");
			return false;
		}
		
		return true;
	}

	// Compare all nodes in m1 and m2, calculate match scores
	public boolean calculateScores(NodeMap m1, NodeMap m2, BlockingOperator bo, AbstractMatcher ma)
	{
		ma.resetCounter();
		
		// Start logging time
		Long startTime = new Date().getTime();
        for (String id1 : m1.getKeySet()) 
        {
            for (String id2 : m2.getKeySet()) 
            {
            	String res = concatPrecedingIDs(id1,id2);
            	if (res != null) // id1 precedes id2
            	{
            		// here's the blocking
            		ANode n1 = m1.getNode(id1);
            		ANode n2 = m2.getNode(id2);
            		String key1 = bo.getBlockingKey(n1);
            		String key2 = bo.getBlockingKey(n2);
            		
            		if(key1 != null && key2 != null && key1.equals(key2)) 
            		{
            			if(ma.match(n1, n2))
	            		{
	            			result.add(res);
	            		}
            		}
            	}
            }
        }        
		// End logging time
        Long endTime = new Date().getTime();
        runTime = endTime - startTime;
        runTimeMin = (endTime - startTime)/60000;
        
        countMatchingOperations = ma.getCount();
        
        return true;
	}
	
	/* Three functions that allow comparing (s1,s2) only once, without repeating for (s2,s1) */
	// Returns true if s1 precedes s2
	private boolean ifPrecedes(String s1, String s2)
	{
		return (s1.compareTo(s2) <= 0);
	}
	// Returns concatenated string or null if ifPrecedes is false
	private String concatPrecedingIDs(String id1, String id2)
	{
		if (ifPrecedes(id1,id2)) return concatIDs(id1,id2);
		return null;
	}	
	// Returns concatenated string, s1-s2 or s2-s1, depending on precedence
	private String concatAnyIDs(String id1, String id2)
	{
		if (ifPrecedes(id1,id2)) return concatIDs(id1,id2);
		return concatIDs(id2,id1);
	}
	// Basic method, returns concatenated string, s1-s2
	private String concatIDs(String id1, String id2)
	{
		return id1+separator+id2;
	}	
	
	private String[] splitIDs(String s) {
		return s.split(separator);
	}

	// Getters 
	public double getF1() {
		return F1;
	}

	public double getP() {
		return P;
	}

	public double getR() {
		return R;
	}

	public double getRunTime() {
		return runTime;
	}
	
	public long getCountMatchingOperations() {
		return countMatchingOperations;
	}

	public Collection<String> getResult() {
		return result;
	}
}
