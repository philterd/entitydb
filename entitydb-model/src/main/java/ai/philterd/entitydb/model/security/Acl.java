/*
 * Copyright 2024 Philterd, LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ai.philterd.entitydb.model.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.domain.User;
import ai.philterd.entitydb.model.exceptions.MalformedAclException;

/**
 * An entity ACL.
 * 
 * @author Philterd, LLC
 *
 */
public class Acl {
	
	private static final Logger LOGGER = LogManager.getLogger(Acl.class);
	
	public static final String WORLD = "::1";

	// Good regex test site: https://regex101.com/
	public static final String ACL_REGEX = "^([A-Aa-z0-9,]+)*[:]([A-Aa-z0-9,]+)*[:][01]$";
	
	private String[] users;
	private String[] groups;
	private int world;		
	
	/**
	 * Creates a new ACL.
	 * @param users The users.
	 * @param groups The groups.
	 * @param world <code>true</code> to allow world read; otherwise <code>false</code>.
	 */
	public Acl(String[] users, String[] groups, int world) {
		
		this.users = users;
		this.groups = groups;
		this.world = world;
		
	}
	
	/**
	 * Creates a new ACL.
	 * @param users The users.
	 * @param groups The groups.
	 * @param world <code>true</code> to allow world read; otherwise <code>false</code>.
	 */
	public Acl(String[] users, String[] groups, boolean world) {
		
		this.users = users;
		this.groups = groups;
		
		if(world) {
			this.world = 1;
		} else {
			this.world = 0;
		}
		
	}
	
	/**
	 * Creates a new ACL.
	 * @param users The users.
	 * @param groups The groups.
	 * @param world <code>true</code> to allow world read; otherwise <code>false</code>.
	 */
	public Acl(Set<String> users, Set<String> groups, boolean world) {
		
		this.users =  users.toArray(new String[users.size()]);
		this.groups = groups.toArray(new String[groups.size()]);
		
		if(world) {
			this.world = 1;
		} else {
			this.world = 0;
		}
		
	}
	
	/**
	 * Creates a new ACL.
	 * @param user The user.
	 * @param groups The groups.
	 * @param world <code>true</code> to allow world read; otherwise <code>false</code>.
	 */
	public Acl(String user, Set<String> groups, boolean world) {
		
		this.users = Arrays.asList(user).toArray(new String[0]);
		this.groups = groups.toArray(new String[groups.size()]);
		
		if(world) {
			this.world = 1;
		} else {
			this.world = 0;
		}
		
	}
	
	/**
	 * Creates a new ACL.
	 * @param acl The ACL.
	 * @throws MalformedAclException Thrown if the ACl is invalid.
	 */
	public Acl(String acl) throws MalformedAclException {
		
		if(!Acl.validate(acl)) {
			throw new MalformedAclException("The ACL [" + acl + "] is malformed.");
		}
		
		List<String> aclSplit = Arrays.asList(acl.split(":"));
		
		users = aclSplit.get(0).split(",");
		groups = aclSplit.get(1).split(",");
		world = Integer.valueOf(aclSplit.get(2));
		
	}
	
	/**
	 * Validates the format of the ACL.
	 * @param acl The ACL.
	 * @return <code>true</code> if the ACL matches the ACL regex pattern; otherwise <code>false</code>.
	 */
	public static boolean validate(String acl) {
		
		if(StringUtils.isNoneEmpty(acl)) {
		
			return Pattern.matches(ACL_REGEX, acl);
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * Determines if the entity having the given ACL is visible to the given {@link User user}.
	 * @param user The {@link User user}.
	 * @return <code>true</code> if the entity having the given ACL is visible to the user;
	 * otherwise <code>false</code>.
	 */
	public boolean isEntityVisibleToUser(User user) {
		
		boolean visible = false;

		// Check the world flag first.
		
		if(world == 1) {
			
			visible = true;
			
		} else {
		
			List<String> aclGroups = Arrays.asList(groups);
			
			Collection<String> intersection = CollectionUtils.intersection(aclGroups, user.getGroups());
			
			// If at least one group intersects then it is visible.
			
			if(CollectionUtils.isNotEmpty(intersection)) {
				
				visible = true;
				
			} else {
				
				// Check the users.
				
				List<String> aclUsers = Arrays.asList(users);

				
				if(aclUsers.contains(String.valueOf(user.getId()))) {
					
					// The user's ID is in the ACL.
					
					visible = true;
					
				} else {
					
					System.out.println("Does not contain.");
				}
				
			}
			
		}
		
		return visible;		
		
	}
	
	public static boolean isEntityVisibleToUser(Acl acl, User user) {
		
		return acl.isEntityVisibleToUser(user);
		
	}
	
	/**
	 * Returns the ACL in the format users:groups:world,
	 * such as <code>user1:user2:1</code>.
	 */
	/*@Override
	public String toString() {
		
		final String usersCsv = String.join(",", users);
		final String groupsCsv = String.join(",", groups);
		
		return String.format("%s:%s:%d", usersCsv, groupsCsv, world);
		
	}*/
	
	/**
	 * Gets the ACL's users.
	 * @return The ACL's users.
	 */
	public String[] getUsers() {
		return users;
	}
	
	/**
	 * Gets the ACL's groups.
	 * @return The ACL's groups.
	 */
	public String[] getGroups() {
		return groups;
	}
	
	/**
	 * Gets the ACL's world visibility flag.
	 * @return <code>1</code> if visible to the world; otherwise <code>0</code>.
	 */
	public int getWorld() {
		return world;
	}

}