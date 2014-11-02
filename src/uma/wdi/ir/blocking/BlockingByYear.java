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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import uma.wdi.ir.ANode;

/**
 * Blocking key: movies by year
 *  @author Volha
 *  @author Heiko
 *
 */
public class BlockingByYear implements BlockingOperator 
{

	// TODO: "date" should come from here - private XPathExpression blockingKeyXPath;
	
	private Map<String, String> cache = new HashMap<>();
	
	public BlockingByYear() {
	}
	
	@Override
	public String getBlockingKey(ANode node) 
	{
		if(cache.containsKey(node.getID()))
			return cache.get(node.getID());
		
		String blk = null;
		try 
		{
		    for(String date : node.getAttribute("release")) 
            {
	        	if (date != null && !date.isEmpty())
				{
					DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
					Calendar c = Calendar.getInstance();
					c.setTime(formatter.parse(date));
		            int year = c.get(Calendar.YEAR);
		            year = year - year%10;
		            blk = String.valueOf(year);
				}
            }
		} 
		catch (ParseException e) 
		{
			blk = null;
		}
		if (blk == null) System.out.println("ERROR: null blocking key created for node " + node);
		
		cache.put(node.getID(), blk);
		
		return blk;
	}

}
