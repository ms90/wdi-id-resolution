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

package uma.wdi.ir.similarity;

import com.wcohen.ss.Levenstein;

public class LevensteinSimilarityFunction implements SimilarityFunction 
{
	/**
	 * Compares two strings, uses Levenshtein edit distance from the SecodnString library. 
	 * @author Volha
	 *
	 */

	@Override
	public double compare(String s1, String s2) 
	{
		Levenstein dist = new Levenstein();
		dist.score(s1, s2);
		return 1-Math.abs(dist.score(s1,s2)/Math.max(s1.length(), s2.length()));
	}
	
}
