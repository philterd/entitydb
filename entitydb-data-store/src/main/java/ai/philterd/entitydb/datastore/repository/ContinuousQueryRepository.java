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
package ai.philterd.entitydb.datastore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ai.philterd.entitydb.model.datastore.entities.ContinuousQueryEntity;
import ai.philterd.entitydb.model.datastore.entities.UserEntity;

@Repository
public interface ContinuousQueryRepository extends CrudRepository<ContinuousQueryEntity, Long> {
	
	@Query(value = "SELECT * FROM ContinuousQueries t WHERE DATEDIFF(NOW(), t.timestamp) <= t.days OR Days = -1", nativeQuery=true)	 
	public List<ContinuousQueryEntity> getNonExpiredContinuousQueries();
	 
	public List<ContinuousQueryEntity> findByUserOrderByIdDesc(UserEntity userEntity);
		
	@Override
	public <S extends ContinuousQueryEntity> S save(S continuousQueryEntity);
	
	@Override
	public void delete(ContinuousQueryEntity continuousQueryEntity);
	
}