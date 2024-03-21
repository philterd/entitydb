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
package ai.idylnlp.eql.model;

/**
 * Specifies entity ordering for use in an
 * {@link EntityQuery query}.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public enum SortOrder {

	ASC("asc"),
	DESC("desc");
	
	private String property;
	
	private SortOrder(String property) {
		
		this.property = property;
		
	}
	
	@Override
	public String toString() {
		return property;
	}
	
	/**
	 * Gets the name of the property to sort by.
	 * @return The name of the property.
	 */
	public String getSortOrder() {
		return property;
	}
	
}
