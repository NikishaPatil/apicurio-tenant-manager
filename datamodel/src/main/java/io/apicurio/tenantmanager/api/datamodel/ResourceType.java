
package io.apicurio.tenantmanager.api.datamodel;

import java.util.ArrayList;
import java.util.List;

public class ResourceType {

    public static final String MAX_TOTAL_SCHEMAS_COUNT = "MAX_TOTAL_SCHEMAS_COUNT";
    public static final String MAX_SCHEMA_SIZE_BYTES = "MAX_SCHEMA_SIZE_BYTES";
    public static final String MAX_ARTIFACTS_COUNT = "MAX_ARTIFACTS_COUNT";
    public static final String MAX_VERSIONS_PER_ARTIFACT_COUNT = "MAX_VERSIONS_PER_ARTIFACT_COUNT";
    public static final String MAX_ARTIFACT_PROPERTIES_COUNT = "MAX_ARTIFACT_PROPERTIES_COUNT";
    public static final String MAX_PROPERTY_KEY_SIZE_BYTES = "MAX_PROPERTY_KEY_SIZE_BYTES";
    public static final String MAX_PROPERTY_VALUE_SIZE_BYTES = "MAX_PROPERTY_VALUE_SIZE_BYTES";
    public static final String MAX_ARTIFACT_LABELS_COUNT = "MAX_ARTIFACT_LABELS_COUNT";
    public static final String MAX_LABEL_SIZE_BYTES = "MAX_LABEL_SIZE_BYTES";
    public static final String MAX_ARTIFACT_NAME_LENGTH_CHARS = "MAX_ARTIFACT_NAME_LENGTH_CHARS";
    public static final String MAX_ARTIFACT_DESCRIPTION_LENGTH_CHARS = "MAX_ARTIFACT_DESCRIPTION_LENGTH_CHARS";
    public static final String MAX_REQUESTS_PER_SECOND_COUNT = "MAX_REQUESTS_PER_SECOND_COUNT";

    public static List<String> values() {
        List<String> result = new ArrayList();
        result.add("MAX_TOTAL_SCHEMAS_COUNT");
        result.add("MAX_SCHEMA_SIZE_BYTES");
        result.add("MAX_ARTIFACTS_COUNT");
        result.add("MAX_VERSIONS_PER_ARTIFACT_COUNT");
        result.add("MAX_ARTIFACT_PROPERTIES_COUNT");
        result.add("MAX_PROPERTY_KEY_SIZE_BYTES");
        result.add("MAX_PROPERTY_VALUE_SIZE_BYTES");
        result.add("MAX_ARTIFACT_LABELS_COUNT");
        result.add("MAX_LABEL_SIZE_BYTES");
        result.add("MAX_ARTIFACT_NAME_LENGTH_CHARS");
        result.add("MAX_ARTIFACT_DESCRIPTION_LENGTH_CHARS");
        result.add("MAX_REQUESTS_PER_SECOND_COUNT");
        return result;
    }
}
