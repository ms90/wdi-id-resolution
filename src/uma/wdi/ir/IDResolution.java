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

import java.util.List;

import uma.wdi.ir.blocking.BlockingByTitle;
import uma.wdi.ir.blocking.BlockingOperator;
import uma.wdi.ir.matching.AbstractMatcher;
import uma.wdi.ir.matching.LevensteinTitleMatcher;

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
		String fnGold = "resources/videogames/gold_1.csv";
//		String fnGold = "resources/videogames/gold_2.csv";
		// input files
//		String fnDataset1 = "resources/videogames/xml/dbpedia_cleaned.xml";
//		String fnDataset2 = "resources/videogames/xml/giantbomb_cleaned.xml";
//		String fnDataset1 = "resources/videogames/xml/giantbomb_cleaned.xml";
//		String fnDataset2 = "resources/videogames/xml/thegamesdb_cleaned.xml";
		String fnDataset1 = "resources/videogames/xml/out_dbpedia.xml";
		String fnDataset2 = "resources/videogames/xml/out_giantbomb.xml";
		// path to ID tag 
		String idPath = "/data/videogame/id";
		
		String fnOutput = "resources/videogames/matched-1-2_out.txt";
		String fnRegression = "resources/videogames/regression.csv";
		
		System.out.println("Matching by titles only");
//		System.out.println("-------------------------------");
//		System.out.println("WITHOUT BLOCKING, Levenstein:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new LevensteinTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITHOUT BLOCKING, NeedlemanWunsch:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new NeedlemanWunschTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITHOUT BLOCKING, Jaccard:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new JaccardTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITHOUT BLOCKING, Cosine:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new NoBlockingOperator(), new CosineTitleMatcher(), true);
		System.out.println("-------------------------------");
		System.out.println("WITH BLOCKING, Levenstein:");
		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new LevensteinTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, NeedlemanWunsch:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new NeedlemanWunschTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, Jaccard:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new JaccardTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, Dice:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new DiceTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, JaroWinkler:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new JaroWinklerTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, MongeElkan:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new MongeElkanTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, SmithWatermanGotoh:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new SmithWatermanGotohTitleMatcher(), true);
//		System.out.println();
//		System.out.println("WITH BLOCKING, Cosine:");
//		runEvaluation(fnDataset1, fnDataset2, idPath, fnGold, new BlockingByTitle(), new CosineTitleMatcher(), true);
		System.out.println("-------------------------------");

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
		
		runOutputResults(fnDataset1, fnDataset2, idPath, fnOutput, new BlockingByTitle(), new LevensteinTitleMatcher());
		
//		List<AbstractMatcher> matchers = Arrays.asList(new AbstractMatcher[]{new DemoTitleMatcher(),new DemoDateMatcher()});
//		runWriteRegressionFile(fnDataset1, fnDataset2, idPath, fnGold, fnRegression, matchers);
		
	}

}
