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

package uma.wdi.ir.blocking;

import java.util.HashMap;
import java.util.Map;

import uma.wdi.ir.ANode;

/**
 * Blocking key: movies by year
 *  @author Maxim
 *
 */
public class BlockingByTitle implements BlockingOperator 
{

	// TODO: "date" should come from here - private XPathExpression blockingKeyXPath;
	
	private Map<String, String> cache = new HashMap<>();
	
	public BlockingByTitle() {
	}
	
	@Override
	public String getBlockingKey(ANode node) 
	{
		if(cache.containsKey(node.getID()))
			return cache.get(node.getID());
		
		String blk = null;
		for(String title : node.getAttribute("title")) 
		{
			if (title != null && !title.isEmpty())
			{
		        blk = String.valueOf(title.charAt(0));
			}
		}
		if (blk == null) System.out.println("ERROR: null blocking key created for node " + node);
		
		cache.put(node.getID(), blk);
		
		return blk;
	}

}
