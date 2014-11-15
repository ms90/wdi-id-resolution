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
 * Matches two entities, returns a score. 
 * Add here your similarity measures and define how to combine them. 
 * */

package uma.wdi.ir.matching;

import uma.wdi.ir.similarity.LevensteinSimilarityFunction;
import uma.wdi.ir.similarity.TFIDFSimilarityFunction;

public class TFIDFTitleMatcher extends SimpleMatcher
{
	/**
	 * Example of simple matcher: compare titles
	 */

	public TFIDFTitleMatcher() {
		super("title", new TFIDFSimilarityFunction(),0.93);
	}
}
