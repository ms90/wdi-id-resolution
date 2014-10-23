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

import java.util.Set;

import uma.wdi.ir.ANode;
import uma.wdi.ir.similarity.SimilarityFunction;

/**
 * A simple matcher that takes an XPath for a tag, and compares it 
 * with a similarity function
 * 
 * @author Heiko
 *
 */
public class SimpleMatcher extends AbstractMatcher {
	
    private String             attribute;
    private SimilarityFunction function;
    
    public SimpleMatcher(String attribute, SimilarityFunction function, double threshold)
    {
        this.attribute = attribute;
        this.function = function;
        this.setThreshold(threshold);
    }
	
	
	@Override
	public double doMatch(ANode n1, ANode n2) 
	{
		Set<String> ss1 = n1.getAttribute(attribute);
	    Set<String> ss2 = n2.getAttribute(attribute);
	    
	    double sim = 0;
        for(String s1 : ss1) 
        {
            for(String s2 : ss2) 
            {
            	// TODO: implement here your logic to compare lists/multi-values
            	// For instance, "max" is an option for a multi-valued attribute, e.g. "Canada" and "Toronto" as a birthPlace in dataset1 
            	// and only "Canada" in dataset 2 will result in matching these two attributes (similarity = 1).
            	// For lists (e.g. actors of a movie) you might implement some set intersection-based aggregation.
            	sim = Math.max(sim, function.compare(s1, s2));            	
            }
        }
        return sim;
	}
	
	public String getAttribute()
    {
        return attribute;
    }	
}
