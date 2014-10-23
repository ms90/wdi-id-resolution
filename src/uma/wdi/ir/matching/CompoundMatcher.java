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

import java.util.List;
import uma.wdi.ir.ANode;

/**
 * Uses a linear combination of different matchers
 * @author Heiko
 *
 */
public class CompoundMatcher extends AbstractMatcher {

	private List<AbstractMatcher> matchers;
	private List<Double> weights;
	
	private double offset=0.0;
	
	private double sumWeights=1.0;
	
	/**
	 * Initialize with default weights. Normalizes the weights.
	 * @param matchers
	 * @param weights
	 * @param threshold
	 */
	public void setParameters(List<AbstractMatcher> matchers, List<Double> weights, double threshold) {
		this.matchers = matchers;
		this.weights = weights;
		
		sumWeights = 0.0;
		for(double w : weights)
			sumWeights+=w;
		
		offset = 0.0;
		
		setThreshold(threshold);
	}
	
	/**
	 * Initialize as result of linear regression. Does not normalize weights!
	 * @param matchers
	 * @param weights
	 * @param threshold
	 * @param offset
	 */
	public void setParameters(List<AbstractMatcher> matchers, List<Double> weights, double threshold, double offset) {
		this.matchers = matchers;
		this.weights = weights;
		
		sumWeights = 1.0;
		
		this.offset = offset;
		
		setThreshold(threshold);
	}
	
	@Override
	public double doMatch(ANode n1, ANode n2) {
		double score = offset;
		for(int i=0;i<matchers.size();i++) {
			score += weights.get(i)*matchers.get(i).doMatch(n1, n2);
		}
		
		score /= sumWeights;
		
		// normalize to [0,1] interval
		score = Math.min(1.0,Math.max(score,0.0));

		return score;
	}

}
