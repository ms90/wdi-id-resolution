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

package uma.wdi.ir.matching;

import java.util.LinkedList;
import java.util.List;


public class TitleDateMatcher extends CompoundMatcher 
{
	/**
	 * Example of compound matcher: combines DemoTitleMatcher and DemoDateMatcher
	 */

	public TitleDateMatcher() 
	{
		AbstractMatcher m1 = new TitleMatcher();
		double w1 = 9;
		
		AbstractMatcher m2 = new DateMatcher();
		double w2 = 1;
		
		List<AbstractMatcher> matchers = new LinkedList<AbstractMatcher>();
		matchers.add(m1);
		matchers.add(m2);
		
		List<Double> weights = new LinkedList<Double>();
		weights.add(w1);
		weights.add(w2);
		
		setParameters(matchers,weights,0.5);
	}

}
