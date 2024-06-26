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
package ai.philterd.entitydb.audit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.audit.AuditAction;
import ai.philterd.entitydb.model.audit.AuditLogger;

public class FileAuditLogger extends AbstractAuditLogger implements AuditLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(AuditLogger.class);

	private File file;
	
	/**
	 * Creates a new file-based audit logger.
	 * @param fileName THe audit log file name.
	 */
	public FileAuditLogger(String systemId, String fileName) {
		
		super(systemId);
		
		file = new File(fileName);
		
	}
	
	/**
	 * Creates a new file-based audit logger that uses a temporary file.
	 * @throws IOException Thrown if a temporary file cannot be created.
	 */
	public FileAuditLogger(String systemId) throws IOException {
		
		super(systemId);
		
		file = File.createTempFile("audit", "log");
		
		LOGGER.info("Logging audit events to temporary file: {}", file.getAbsolutePath());
		
	}
	

	@Override
	public boolean audit(String entityId, long timestamp, String userIdentifier, AuditAction auditAction) {

		final String data = String.format("\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"", entityId, timestamp, userIdentifier, auditAction.toString(), systemId);
		
		try {
		
			FileUtils.writeStringToFile(file, data, true);
			
		} catch (IOException ex) {
			
			LOGGER.error("Unable to audit event.", ex);
			
		}
		
		return true;
		
	}
	

	@Override
	public boolean audit(String query, long timestamp, String userName) {
		
		final String data = String.format("\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"", query, timestamp, userName, AuditAction.QUERY, systemId);
		
		try {
		
			FileUtils.writeStringToFile(file, data, true);
			
		} catch (IOException ex) {
			
			LOGGER.error("Unable to audit event.", ex);
			
		}
		
		return true;
		
	}


	@Override
	public void close() {

		// Nothing to do here.
		
	}

}