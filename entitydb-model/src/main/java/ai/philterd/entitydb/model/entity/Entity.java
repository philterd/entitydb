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

package ai.philterd.entitydb.model.entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Entity {

    private String text;
    private String context;
    private String documentId;
    private String languageCode;
    private String uri;
    private String type;
    private double confidence;
    private Map<String, String> metadata;
    private Span span;
    private long extractionDate;

    public Entity() {

    }

    public Entity(final String text) {
        this.text = text;
    }

    public Entity(final String text, final double confidence, final String type, final String span, final String context, final String documentId) {
        this.text = text;
        this.confidence = confidence;
        this.type = type;
        this.span = new Span(span);
        this.context = context;
        this.documentId = documentId;
    }

    public static Entity createRandomPersonEntity() {
        // TODO: Finish this function.
        return new Entity("George Washington");
    }

    public static Collection<Entity> createRandomPersonEntities() {
        // TODO: Finish this function.
        final Collection<Entity> entities = new LinkedList<>();
        entities.add(new Entity("George Washington"));
        return entities;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Span getSpan() {
        return span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }

    public long getExtractionDate() {
        return extractionDate;
    }

    public void setExtractionDate(long extractionDate) {
        this.extractionDate = extractionDate;
    }
}
