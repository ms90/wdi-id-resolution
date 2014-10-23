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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Compares two dates. The similarity is determined by min(date1,date2)/max(date1,date2). 
 * @author Heiko
 *
 */
public class DateSimilarityFunction implements SimilarityFunction {

	@Override
	public double compare(String s1, String s2) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			Calendar c1 = Calendar.getInstance();
			c1.setTime(formatter.parse(s1));
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(formatter.parse(s2));
		
			double time1 = c1.getTimeInMillis();
			double time2 = c2.getTimeInMillis();
			
			return Math.min(time1, time2)/Math.max(time1,time2);
			
		} catch (ParseException e) {
			System.out.println("wrong date format");
			return 0;
		}
	}

}
