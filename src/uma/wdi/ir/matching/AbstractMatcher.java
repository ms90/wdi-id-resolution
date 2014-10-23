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

import uma.wdi.ir.ANode;

public abstract class AbstractMatcher 
{
	/*
	 * An abstract matcher all matchers should extend
	 */
	
	private long count = 0;

	// if a matching score >= threshold, we consider two entities to be duplicates
	private double threshold = 0.0;
	
	public void setThreshold(double t) {
		threshold = t;
	}
	
	/**
	 * Determines whether two nodes match or not
	 * @param n1 the first node
	 * @param n2 the second node
	 * @return true if nodes match
	 */
	public boolean match(ANode n1, ANode n2) {
		count++;
		return doMatch(n1, n2)>=threshold;
	}
	
	/**
	 * Get the number of matching operations performed
	 * @return
	 */
	public long getCount() {
		return count;
	}
	
	/**
	 * Resets the counter
	 */
	public void resetCounter() {
		count = 0;
	}
	
	public abstract double doMatch(ANode n1, ANode n2);
}
