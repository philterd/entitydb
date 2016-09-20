/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.queues.messages;

public class InternalQueueUpdateAclMessage {

	private String entityId;
	private String acl;
	private String apiKey;
	
	public InternalQueueUpdateAclMessage(String entityId, String acl, String apiKey) {
		
		this.entityId = entityId;
		this.acl = acl;
		this.apiKey = apiKey;
		
	}

	public String getEntityId() {
		return entityId;
	}

	public String getAcl() {
		return acl;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	
}