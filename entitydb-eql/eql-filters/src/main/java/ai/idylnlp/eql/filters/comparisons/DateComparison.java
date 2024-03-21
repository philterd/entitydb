/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/*
 * (C) Copyright 2017 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.idylnlp.eql.filters.comparisons;

import org.apache.commons.lang3.StringUtils;

public enum DateComparison {

	BEFORE("before"), AFTER("after");

	private String dateComparison;

	private DateComparison(String dateComparison) {

		this.dateComparison = dateComparison;

	}

	/**
	 * Gets the enumeration from a string value.
	 * @param dateComparison The value to look up.
	 * @return A {@link DateComparison comparison}.
	 */
	public static DateComparison fromValue(String dateComparison) {

		if(StringUtils.isNotEmpty(dateComparison)) {
			
			for (DateComparison d : DateComparison.values()) {
				
				if (dateComparison.equalsIgnoreCase(d.getDateComparison())) {
					return d;
				}
				
			}
			
		}

		throw new IllegalArgumentException("No date comparison found with value: " + dateComparison);

	}

	@Override
	public String toString() {

		return dateComparison;

	}

	public String getDateComparison() {

		return dateComparison;

	}

}
