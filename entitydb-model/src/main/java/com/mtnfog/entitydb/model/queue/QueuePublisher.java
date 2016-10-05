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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.model.queue;

import java.util.Collection;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.exceptions.EntityPublisherException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;

/**
 * Interface for queue publishers. The implementing classes publish
 * entities and their associated information to a queue for processing.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface QueuePublisher {

	/**
	 * Publish entities to the ingest queue.
	 * @param entities A collection of {@link Entity entities}.
	 * @param acl The entity's ACL.
	 * @param apiKey The API key of the requester.
	 * @throws MalformedAclException Thrown if the ACL does not meet the ACL regular expression.
	 * @throws EntityPublisherException Thrown if the request cannot be queued.
	 */
	public void queueIngest(Collection<Entity> entities, String acl, String apiKey) throws MalformedAclException, EntityPublisherException;
	
	/**
	 * Publish requests for entity ACL updates to the queue.
	 * @param entityId The entity's ID.
	 * @param acl The new ACL.
	 * @param apiKey The API key of the requester.
	 * @throws MalformedAclException Thrown if the ACL does not meet the ACL regular expression.
	 * @throws EntityPublisherException Thrown if the request cannot be queued.
	 */
	public void queueUpdateAcl(String entityId, String acl, String apiKey) throws MalformedAclException, EntityPublisherException;
	
}