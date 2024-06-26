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
package ai.philterd.entitydb.rulesengine.drools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;

import ai.philterd.entitydb.model.entity.Entity;
import ai.philterd.entitydb.model.rulesengine.Rule;
import ai.philterd.entitydb.model.rulesengine.RuleEvaluationResult;
import ai.philterd.entitydb.model.rulesengine.RulesEngineException;
import ai.philterd.entitydb.model.rulesengine.RulesEngine;

/**
 * Implementation of {@link RulesEngine} powered by Drools.
 * 
 * @author Philterd, LLC
 *
 */
public class DroolsRulesEngine implements RulesEngine {
	
	private static final Logger LOGGER = LogManager.getLogger(DroolsRulesEngine.class);
	
	private List<String> rules = new ArrayList<String>();
	
	private KieServices kieServices;
	private KieResources kieResources;
	private KieFileSystem kieFileSystem;
	private KieRepository kieRepository;
	private KieContainer kContainer;
	
	/**
	 * Creates a rules engine powered by Drools. Any error discovered
	 * while loading the rules files will cause a runtime exception.
	 * @param rulesPath The full path to the directory containing
	 * the Drools rule files (*.drl).
	 */
	public DroolsRulesEngine(String rulesPath) throws RulesEngineException {
		
		LOGGER.info("Loading Drools rules from {}.", rulesPath);
		
		kieServices = KieServices.Factory.get();
		kieResources = kieServices.getResources();
		kieFileSystem = kieServices.newKieFileSystem();
		kieRepository = kieServices.getRepository();
		
		File file = new File(rulesPath);
		Collection<File> ruleFiles = FileUtils.listFiles(file, new String[]{"drl"}, true);
		
		for (File ruleFile : ruleFiles) {
			
			//Resource resource = kieResources.newClassPathResource(ruleFile);
			Resource resource = kieResources.newFileSystemResource(ruleFile);

			// path has to start with src/main/resources
			// append it with the package from the rule
			kieFileSystem.write("src/main/resources/" + ruleFile, resource);
			
			rules.add(resource.getSourcePath());
			
		}

		KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);

		kb.buildAll();
		
		if (kb.getResults().hasMessages(Level.ERROR)) {
			throw new RulesEngineException("Erros loading rules: " + kb.getResults().toString());
		}

		kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
		
	}

	@Override
	public RuleEvaluationResult evaluate(Entity entity) {

		TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
		
		KieSession kSession = kContainer.newKieSession();
		kSession.addEventListener(agendaEventListener);
						
		kSession.insert(entity);
		
		kSession.fireAllRules();
		
		List<Match> matches = agendaEventListener.getMatchList();
		
		Collection<? extends Object> results = kSession.getObjects(new ClassObjectFilter(String.class));
		
		String acl = StringUtils.EMPTY;
		
		if(CollectionUtils.isNotEmpty(results)) {
			
			acl = results.iterator().next().toString();
					
		}
		
		kSession.dispose();
		
		return new RuleEvaluationResult(!matches.isEmpty(), acl);
		
	}
	

	@Override
	public List<String> getRules() {
		
		return rules;
		
	}

	/**
	 * {@inheritDoc}
	 * This function does nothing.
	 */
	@Override
	public Rule read(String rule) throws RulesEngineException {
		
		return null;
		
	}

}