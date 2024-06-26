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
package ai.philterd.entitydb.testing;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.entitystore.AbstractStoredEntity;
import ai.philterd.entitydb.model.entitystore.EntityStore;
import ai.philterd.entitydb.model.entitystore.MetadataSanitizer;
import ai.philterd.entitydb.model.entitystore.QueryResult;
import ai.philterd.entitydb.model.eql.ConfidenceRange;
import ai.philterd.entitydb.model.eql.EntityMetadataFilter;
import ai.philterd.entitydb.model.eql.EntityOrder;
import ai.philterd.entitydb.model.eql.EntityQuery;
import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.NonexistantEntityException;
import ai.philterd.entitydb.model.search.IndexedEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractEntityStoreTest<T extends AbstractStoredEntity> {
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractEntityStoreTest.class);

	protected EntityStore<T> entityStore;
	
	public abstract EntityStore<T> getEntityStore() throws EntityStoreException;
	
	@Before
	public void before() throws EntityStoreException {
		entityStore = getEntityStore();
	}
	
	@After
	public void after() throws EntityStoreException {
		entityStore.close();
	}
	
	@Test
	public void getNonIndexedEntities() throws EntityStoreException {

		final Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");

		final Entity entity2 = new Entity();
		entity2.setText("George Washington");
		entity2.setConfidence(90.0);
		entity2.setType("person");
		entity2.setContext("context2");
		
		entityStore.storeEntity(entity, "::1");
		entityStore.storeEntity(entity2, "::1");
	
		assertEquals(2, entityStore.getEntityCount());

		final List<T> nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(2, nonIndexedEntities.size());
				
	}	
	
	@Test
	public void getVisibleNonIndexedEntities() throws EntityStoreException, NonexistantEntityException {

		final Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");

		final Entity entity2 = new Entity();
		entity2.setText("George Washington");
		entity2.setConfidence(90.0);
		entity2.setType("person");
		entity2.setContext("context2");
		
		final String entityId1 = entityStore.storeEntity(entity, "::1");
		final String entityId2 = entityStore.storeEntity(entity2, "::1");
	
		assertEquals(2, entityStore.getEntityCount());	
		
		final String entityId3 = entityStore.updateAcl(entityId2, "::0");

		final List<T> nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(2, nonIndexedEntities.size());
		
		for(T e : nonIndexedEntities) {
			
			// entityId2 should NOT be returned as it is no longer visible due to the ACL update.
			
			if(StringUtils.equals(entityId1, e.getId()) || StringUtils.equals(entityId3, e.getId())) {
				// Good.
			} else {
				Assert.fail("An invalid entity was returned.");
			}
			
		}
				
	}	
	
	@Test
	public void markAsIndexed() throws EntityStoreException {

		final Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");

		final String entityId1 = entityStore.storeEntity(entity, "::1");
		
		entity.setContext("context2");
		final String entityId2 = entityStore.storeEntity(entity, "::1");
		
		assertEquals(2, entityStore.getEntityCount());

		List<T> nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(2, nonIndexedEntities.size());
		
		for(T rdbmsStoredEntity : nonIndexedEntities) {
			
			entityStore.markEntityAsIndexed(rdbmsStoredEntity.getId());
			
		}
		
		nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(0, nonIndexedEntities.size());
		
		// Verify that the entities we just marked really did get marked.

		final T e1 = entityStore.getEntityById(entityId1);
		assertTrue(e1.getIndexed() != 0);

		final T e2 = entityStore.getEntityById(entityId2);
		assertTrue(e2.getIndexed() != 0);
		
	}
	
	@Test
	public void markEntitiesAsIndexed() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		String entityId1 = entityStore.storeEntity(entity, "::1");
		
		entity.setContext("context2");
		String entityId2 = entityStore.storeEntity(entity, "::1");
		
		assertEquals(2, entityStore.getEntityCount());
		
		List<T> nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(2, nonIndexedEntities.size());
		
		List<String> entitiesToMark = new ArrayList<String>();
		entitiesToMark.add(entityId1);
		entitiesToMark.add(entityId2);
		
		long updated = entityStore.markEntitiesAsIndexed(entitiesToMark);
		
		assertEquals(2, updated);
		
		nonIndexedEntities = entityStore.getNonIndexedEntities(25);
		
		assertEquals(0, nonIndexedEntities.size());
		
		// Verify that the entities we just marked really did get marked.
		
		T e1 = entityStore.getEntityById(entityId1);
		assertTrue(e1.getIndexed() != 0);
		
		T e2 = entityStore.getEntityById(entityId2);
		assertTrue(e2.getIndexed() != 0);
		
	}
	
	@Test
	public void deleteEntity() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
	
		String entityId1 = entityStore.storeEntity(entity, "::1");
		
		entity.setContext("context2");
		String entityId2 = entityStore.storeEntity(entity, "::1");
		
		assertEquals(2, entityStore.getEntityCount());
		
		entityStore.deleteEntity(entityId1);
		
		assertEquals(1, entityStore.getEntityCount());
		
	}
	
	@Test(expected = NonexistantEntityException.class)
	public void updateAclNonexistantEntityTest() throws EntityStoreException, NonexistantEntityException {
		
		String entityId1 = "entitydoesnotexist";
		entityStore.updateAcl(entityId1, "user2:group2:1");
		
	}
	
//	@Test
//	@Ignore
//	public void updateAclTest() throws EntityStoreException, NonexistantEntityException {
//
//		final Entity entity = Entity.createRandomPersonEntity();
//		entity.setContext("context");
//
//		String entityId1 = entityStore.storeEntity(entity, "user:group:1");
//		String entityId2 = entityStore.updateAcl(entityId1, "user2:group2:1");
//
//		LOGGER.info("Original entity ID: {}", entityId1);
//		LOGGER.info("New entity ID: {}", entityId2);
//
//		T originalEntity = entityStore.getEntityById(entityId1);
//		assertEquals(0, originalEntity.getVisible());
//		assertEquals("user:group:1", originalEntity.getAcl());
//
//		T newEntity = entityStore.getEntityById(entityId2);
//		assertEquals(1, newEntity.getVisible());
//		assertEquals("user2:group2:1", newEntity.getAcl());
//
//		// Make sure all other properties of the entities are the same.
//		assertEquals(originalEntity.getText(), newEntity.getText());
//		assertEquals(originalEntity.getConfidence(), newEntity.getConfidence(), 0);
//		assertEquals(originalEntity.getContext(), newEntity.getContext());
//		assertEquals(originalEntity.getDocumentId(), newEntity.getDocumentId());
//		assertEquals(originalEntity.getExtractionDate(), newEntity.getExtractionDate());
//		assertEquals(originalEntity.getLanguage(), newEntity.getLanguage());
//		assertEquals(originalEntity.getUri(), newEntity.getUri());
//
//	}
	
	@Test
	public void storeEntity() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		entityStore.storeEntity(entity, "::1");
		
	}
	
	@Test
	public void storeEntities() throws EntityStoreException {

		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
	
	}
	
	@Test
	@Ignore
	public void storeEntities2() throws EntityStoreException {

		Set<Entity> entities = new HashSet<Entity>();
		entities.add(Entity.createRandomPersonEntity());
		entities.add(Entity.createRandomPersonEntity());

		entityStore.storeEntities(entities, "::1");

		EntityQuery entityQuery = new EntityQuery();
		QueryResult queryResult = entityStore.query(entityQuery);

		for(IndexedEntity rdbmsStoredEntity : queryResult.getEntities()) {

			LOGGER.info(rdbmsStoredEntity.toString());
			LOGGER.info("\tMetadata = " + rdbmsStoredEntity.getMetadata().size());

			for(String s : rdbmsStoredEntity.getMetadata().keySet()) {

				LOGGER.info("\t" + s + " = " + rdbmsStoredEntity.getMetadata().get(s));

			}

		}

	}
	
	@Test
	public void storeQueryEnrichedEntity() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setDocumentId("doc1");
		entity.setContext("context");
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("name", "value1");		
		metadata.put("spouse", "Martha Washington");
		entity.setMetadata(metadata);
		
		// ------------------
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(54.0);
		entity2.setType("person");
		entity2.setDocumentId("doc2");
		entity2.setContext("context");
		
		Map<String, String> metadata2 = new HashMap<String, String>();
		metadata2.put("name", "value2");		
		entity2.setMetadata(metadata2);
		
		// ------------------
		
		Entity entity3 = new Entity();
		entity3.setText("Abraham Lincoln");
		entity3.setConfidence(54.0);
		entity3.setType("person");
		entity3.setDocumentId("doc3");
		entity3.setContext("context");
		
		Map<String, String> metadata3 = new HashMap<String, String>();
		metadata3.put("Birth Date", "1970-07-04");		
		entity3.setMetadata(metadata3);
		
		// ------------------

		entityStore.storeEntity(entity, "::1");
		entityStore.storeEntity(entity2, "::1");
		entityStore.storeEntity(entity3, "::1");
		
		String sanitizedKey = MetadataSanitizer.sanitizeKey("Birth Date");
		
		EntityQuery entityQuery = new EntityQuery();
		// TODO: Something is causing the date filter to fail on Cassandra.
		//entityQuery.setEntityMetadataFilters(Arrays.asList(new EntityMetadataFilter(sanitizedKey, "1970-07-04")));
		entityQuery.setEntityMetadataFilters(Arrays.asList(new EntityMetadataFilter("spouse", "Martha Washington")));
		
		QueryResult queryResult = entityStore.query(entityQuery);
		
		assertEquals(1, queryResult.getEntities().size());
		
	}
	
	@Test
	public void getEntityById() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");		
		entity.setContext("context");
		
		String entityId = entityStore.storeEntity(entity, "::1");
		
		T rdbmsStoredEntity = entityStore.getEntityById(entityId);
		
		assertEquals(entityId, rdbmsStoredEntity.getId());
		assertEquals(entity.getText(), rdbmsStoredEntity.getText());
		
	}
	
	@Test
	public void getEntitiesById() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		Map<Entity, String> entityIds = entityStore.storeEntities(entities, "::1");
		
		List<String> ids = new ArrayList<String>();
		
		for(Entity e : entityIds.keySet()) {
			ids.add(entityIds.get(e));
		}
		
		List<T> rdbmsStoredEntities = entityStore.getEntitiesByIds(ids, false);
		
		assertEquals(ids.size(), rdbmsStoredEntities.size());		
		
		for(T rdbmsStoredEntity : rdbmsStoredEntities) {
			
			assertTrue(ids.contains(rdbmsStoredEntity.getId()));
		}
		
	}
	
	@Test
	public void querySingleEntity() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setText("George Washington");
		
		QueryResult queryResult = entityStore.query(entityQuery);
		
		assertEquals(1, queryResult.getEntities().size());
		
		/*T storedEntity = queryResult.getEntities().get(0);
		
		assertEquals(entity.getText(), storedEntity.getText());
		assertEquals(entity.getConfidence(), storedEntity.getConfidence(), 0);
		assertEquals(entity.getType(), storedEntity.getType());*/
		
	}
	
	@Test
	public void queryAllEntities() throws EntityStoreException {

		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(2, queryResult.getEntities().size());
		
	}
	
	@Test
	public void queryByLanguage() throws EntityStoreException {

		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setLanguageCode("en");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setLanguageCode("es");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setLanguageCode("en");
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(1, queryResult.getEntities().size());
		
	}
	
	@Test
	public void queryConfidenceEntities() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setConfidenceRange(new ConfidenceRange(83, 87));
		
		QueryResult queryResult = entityStore.query(entityQuery);
		
		assertEquals(1, queryResult.getEntities().size());
		
		/*T storedEntity = queryResult.getEntities().get(0);
		
		assertEquals(entity2.getText(), storedEntity.getText());
		assertEquals(entity2.getConfidence(), storedEntity.getConfidence(), 0);
		assertEquals(entity2.getEntityClass().getType(), storedEntity.getType());*/
		
	}
	
	@Test
	public void querySortOrderEntities() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setEntityOrder(EntityOrder.TEXT);
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(2, queryResult.getEntities().size());
		
		/*T sortedEntity1 = queryResult.getEntities().get(0);
		T sortedEntity2 = queryResult.getEntities().get(1);
		
		assertEquals(entity2.getText(), sortedEntity1.getText());
		assertEquals(entity.getText(), sortedEntity2.getText());*/
	
	}
	
	@Test
	public void queryTypesEntities() throws EntityStoreException {
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("United States");
		entity2.setConfidence(85);
		entity2.setType("place");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setEntityOrder(EntityOrder.TEXT);
		entityQuery.setType("place");
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(1, queryResult.getEntities().size());
		
		/*T sortedEntity = queryResult.getEntities().get(0);
		
		assertEquals(entity2.getText(), sortedEntity.getText());*/
		
	}
	
	@Test
	public void queryEmptyContextsEntities() throws EntityStoreException {
				
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("United States");
		entity2.setConfidence(85);
		entity2.setType("place");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
				
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setEntityOrder(EntityOrder.TEXT);
		entityQuery.setType("place");
		entityQuery.setContext("empty");
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(0, queryResult.getEntities().size());
		
	}
	
	@Test
	public void queryContextsEntities() throws EntityStoreException {
		
		final String context = "context";
		
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");		
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("United States");
		entity2.setConfidence(85);
		entity2.setType("place");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);		
		
		entityStore.storeEntities(entities, "::1");
		
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setEntityOrder(EntityOrder.TEXT);
		entityQuery.setContext(context);
		
		QueryResult queryResult = entityStore.query(entityQuery);
	
		assertEquals(2, queryResult.getEntities().size());
		
	}
	
	@Test
	public void storeEntitiesCount() throws EntityStoreException {
				
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context");
		
		Entity entity2 = new Entity();
		entity2.setText("Abraham Lincoln");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		entities.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		
		long count = entityStore.getEntityCount();
		
		assertEquals(2, count);
		
	}
	
	@Test
	public void getContexts() throws EntityStoreException {
				
		Entity entity = new Entity();
		entity.setText("George Washington");
		entity.setConfidence(90.0);
		entity.setType("person");
		entity.setContext("context1");
		
		Entity entity2 = new Entity();
		entity2.setText("George Washington");
		entity2.setConfidence(85);
		entity2.setType("person");
		entity2.setContext("context2");
		
		Set<Entity> entities = new HashSet<Entity>();
		entities.add(entity);
		
		Set<Entity> entities2 = new HashSet<Entity>();
		entities2.add(entity2);
		
		entityStore.storeEntities(entities, "::1");
		entityStore.storeEntities(entities2, "::1");
		
		List<String> contexts = entityStore.getContexts();
		
		assertEquals(2, contexts.size());
		assertTrue(contexts.contains("context1"));
		assertTrue(contexts.contains("context2"));
		
	}

}