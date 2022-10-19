/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.cluster.metadata;

import org.elasticsearch.cluster.Diff;
import org.elasticsearch.cluster.SimpleDiffable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.index.Index;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SearchEngine implements SimpleDiffable<SearchEngine>, ToXContentObject {

    public static final ParseField NAME_FIELD = new ParseField("name");
    public static final ParseField INDICES_FIELD = new ParseField("index");
    public static final ParseField HIDDEN_FIELD = new ParseField("hidden");
    public static final ParseField SYSTEM_FIELD = new ParseField("system");
    public static final ParseField RELEVANCE_SETTINGS_ID_FIELD = new ParseField("relevance_settings_id");
    public static final ParseField ANALYTICS_COLLECTION_FIELD = new ParseField("analytics_collection");

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<SearchEngine, Void> PARSER = new ConstructingObjectParser<>(
        "search_engine",
        args -> new SearchEngine(
            (String) args[0],
            (List<Index>) args[1],
            (boolean) args[2],
            (boolean) args[3],
            (String) args[4],
            (String) args[5]
        )
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), NAME_FIELD);
        PARSER.declareObjectArray(ConstructingObjectParser.constructorArg(), (p, c) -> Index.fromXContent(p), INDICES_FIELD);
        PARSER.declareBoolean(ConstructingObjectParser.optionalConstructorArg(), HIDDEN_FIELD);
        PARSER.declareBoolean(ConstructingObjectParser.optionalConstructorArg(), SYSTEM_FIELD);
        PARSER.declareString(ConstructingObjectParser.optionalConstructorArg(), RELEVANCE_SETTINGS_ID_FIELD);
        PARSER.declareString(ConstructingObjectParser.optionalConstructorArg(), ANALYTICS_COLLECTION_FIELD);
    }

    public static SearchEngine fromXContent(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    public static Diff<SearchEngine> readDiffFrom(StreamInput in) throws IOException {
        return SimpleDiffable.readDiffFrom(SearchEngine::new, in);
    }

    private final String name;
    private final List<Index> indices;
    private final boolean isHidden;
    private final boolean isSystem;
    private final String relevanceSettingsId;
    private final String analyticsCollection;

    public SearchEngine(
        String name,
        List<Index> indices,
        boolean isHidden,
        boolean isSystem,
        String relevanceSettingsId,
        String analyticsCollection
    ) {
        this.name = name;
        this.indices = indices;
        this.isHidden = isHidden;
        this.isSystem = isSystem;
        this.relevanceSettingsId = relevanceSettingsId;
        this.analyticsCollection = analyticsCollection;
    }

    public SearchEngine(StreamInput in) throws IOException {
        this(
            in.readString(),
            in.readList(Index::new),
            in.readBoolean(),
            in.readBoolean(),
            in.readOptionalString(),
            in.readOptionalString()
        );
    }

    public String getName() {
        return name;
    }

    public List<Index> getIndices() {
        return indices;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public String getRelevanceSettingsId() {
        return relevanceSettingsId;
    }

    public boolean shouldRecordAnalytics() {
        return Strings.hasText(analyticsCollection);
    }

    public String getAnalyticsCollection() {
        return analyticsCollection;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(NAME_FIELD.getPreferredName(), name);
        builder.xContentList(INDICES_FIELD.getPreferredName(), indices);
        builder.field(HIDDEN_FIELD.getPreferredName(), isHidden);
        builder.field(SYSTEM_FIELD.getPreferredName(), isSystem);
        builder.field(RELEVANCE_SETTINGS_ID_FIELD.getPreferredName(), relevanceSettingsId);
        builder.field(ANALYTICS_COLLECTION_FIELD.getPreferredName(), analyticsCollection);
        builder.endObject();
        return builder;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(name);
        out.writeList(indices);
        out.writeBoolean(isHidden);
        out.writeBoolean(isSystem);
        out.writeOptionalString(relevanceSettingsId);
        out.writeOptionalString(analyticsCollection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchEngine that = (SearchEngine) o;
        return name.equals(that.name)
            && indices.equals(that.indices)
            && Objects.equals(isHidden, that.isHidden)
            && Objects.equals(isSystem, that.isSystem)
            && relevanceSettingsId.equals(that.relevanceSettingsId)
            && analyticsCollection.equals(that.analyticsCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, indices, isHidden, isSystem, relevanceSettingsId, analyticsCollection);
    }
}
