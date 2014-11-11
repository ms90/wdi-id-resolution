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

package uma.wdi.ir;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uma.wdi.ir.blocking.BlockingByTitle;
import uma.wdi.ir.blocking.BlockingOperator;
import uma.wdi.ir.blocking.BlockingByYear;
import uma.wdi.ir.blocking.NoBlockingOperator;
import uma.wdi.ir.matching.TitleDateMatcher;
import uma.wdi.ir.matching.DateMatcher;
import uma.wdi.ir.matching.TitleMatcher;
import uma.wdi.ir.matching.AbstractMatcher;

public class IDResolution 
{
	/*
	 * Main class of the Identity Resolution project
	 */
	
	/* Runs evaluation for 2 previously loaded datasets (map1, map2) w.r.t. gold standard (given by file path fnGold),
	 * xpath for unique IDs (idPath), with blocking operator bo and matching strategy ma.
	 * Prints the results into the console if print it true. 
	 * */ 
	private static void runEvaluation(NodeMap map1, NodeMap map2, String fnGold, BlockingOperator bo, AbstractMatcher ma, boolean print)
	{
		// match & evaluate
		Evaluator eval = new Evaluator();
		if (eval.evaluate(map1, map2, fnGold, bo, ma))
		{
			// print results
			if (print) eval.printResults(false);
		}
	}

	/* Loads and runs evaluation for 2 datasets (given by file paths fn1, fn2) w.r.t. gold standard (given by file path fnGold),
	 * xpath for unique IDs (idPath), with blocking operator bo and matching strategy ma.
	 * Prints the results into the console if print it true. 
	 * */ 
	private static void runEvaluation(String fn1, String fn2, String idPath, String fnGold, BlockingOperator bo, AbstractMatcher ma, boolean print)
	{
		NodeMap map1 = new NodeMap();
		NodeMap map2 = new NodeMap();		
		if (map1.loadFromFile(fn1, idPath) && map2.loadFromFile(fn2, idPath))
		{
			runEvaluation(map1, map2, fnGold, bo, ma, print);	
		}
	}
	
	/* Loads and runs evaluation for 2 datasets (given by file paths fn1, fn2) w.r.t. gold standard (given by file path fnGold),
	 * xpath for unique IDs (idPath), with blocking operator bo and matching strategy ma.
	 * Prints the results into fnOutput file. 
	 * */ 
	private static void runOutputResults(String fn1, String fn2, String idPath, String fnOutput, BlockingOperator bo, AbstractMatcher ma)
	{
		NodeMap map1 = new NodeMap();
		NodeMap map2 = new NodeMap();	
		
		Evaluator eval = new Evaluator();
		if (map1.loadFromFile(fn1, idPath) && map2.loadFromFile(fn2, idPath))
		{
			if(eval.calculateScores(map1, map2, bo, ma)) 
			{
				eval.outputResultsToFile(fnOutput);
			}	
		}
	}

	/* Constructs .cvs file for RapidMiner for 2 datasets (given by file paths fn1, fn2), gold standard (given by file path fnGold),
	 * xpath for unique IDs (idPath) and a list of matchers (matchers). 
	 * Prints the results into fnOutput file.  
	 * */ 
	private static void runWriteRegressionFile(String fn1, String fn2, String idPath, String fnGold, String fnOutput, List<AbstractMatcher> matchers) {
		NodeMap map1 = new NodeMap();
		NodeMap map2 = new NodeMap();	
		
		Evaluator eval = new Evaluator();
		if (map1.loadFromFile(fn1, idPath) && map2.loadFromFile(fn2, idPath))
		{
			eval.writeInputFileForLinearRegression(map1, map2, fnGold, fnOutput, matchers);
		}
	}
	

	public static void main(String[] args) 
	{
		System.out.println("-- 2014 version --");
		
		// PROJECT TODO: SPECIFY YOUR INPUT PARAMETERS HERE
		// gold standard file
		String fnGold = "resources/videogames/gold.csv";
		// input files
		String fnDataset1 = "resources/videogames/xml/dbpedia.xml";
		String fnDataset2 = "resources/videogames/xml/giantbomb.xml";
		// path to ID tag 
		String idPath = "/data/videogame/id";
		
		String fnOutput = "resources/videogames/matched.txt";
		String fnRegression = "resources/videogames/regression.csv";
		
		System.out.println("Matching by titles only");
		System.out.println("WITHOUT BLOCKING, run 1:");
		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new TitleMatcher(), true);
		System.out.println();
//		System.out.println("WITHOUT BLOCKING, run 2:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new TitleMatcher(), true);
//		System.out.println();
		System.out.println("WITH BLOCKING:");
		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new TitleMatcher(), true);
		System.out.println("----------------");

//		System.out.println("Matching by dates only");
//		System.out.println("WITHOUT BLOCKING:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new DemoDateMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new DemoBlockingOperator(), new DemoDateMatcher(), true);
//		System.out.println("----------------");
//	
//		System.out.println("Matching by titles and dates");
//		System.out.println("WITHOUT BLOCKING:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new TitleDateMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new DemoBlockingOperator(), new TitleDateMatcher(), true);
//		System.out.println("----------------");
		
		runOutputResults(fnDataset1, fnDataset2, idPath, fnOutput, new BlockingByTitle(), new TitleMatcher());
		
//		List<AbstractMatcher> matchers = Arrays.asList(new AbstractMatcher[]{new DemoTitleMatcher(),new DemoDateMatcher()});
//		runWriteRegressionFile(fnDataset1, fnDataset2, idPath, fnGold, fnRegression, matchers);
		
	}

}
