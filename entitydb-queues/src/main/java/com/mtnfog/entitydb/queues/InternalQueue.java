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
package com.mtnfog.entitydb.queues;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.mtnfog.entitydb.queues.messages.InternalQueueIngestMessage;
import com.mtnfog.entitydb.queues.messages.InternalQueueUpdateAclMessage;
public class InternalQueue {

	private static ConcurrentLinkedQueue<InternalQueueIngestMessage> ingestQueue;
	private static ConcurrentLinkedQueue<InternalQueueUpdateAclMessage> updateAclQueue;
	
	public static ConcurrentLinkedQueue<InternalQueueIngestMessage> getIngestQueue() {
		
		if(ingestQueue == null) {
			
			ingestQueue = new ConcurrentLinkedQueue<InternalQueueIngestMessage>();
			
		}
		
		return ingestQueue;

	}
	
	public static ConcurrentLinkedQueue<InternalQueueUpdateAclMessage> getUpdateAclQueue() {
		
		if(updateAclQueue == null) {
			
			updateAclQueue = new ConcurrentLinkedQueue<InternalQueueUpdateAclMessage>();
			
		}
		
		return updateAclQueue;

	}
	
}