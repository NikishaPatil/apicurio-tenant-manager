
package io.apicurio.multitenant.api.datamodel;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Type for TenantResource
 * <p>
 * Configuration of the limits for a specific resource type
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "limit"
})
@Generated("jsonschema2pojo")
public class TenantResource {

    /**
     * The type of the resource to limit
     * (Required)
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("The type of the resource to limit")
    private String type;
    /**
     * The quantity to limit this resource
     * (Required)
     * 
     */
    @JsonProperty("limit")
    @JsonPropertyDescription("The quantity to limit this resource")
    private Long limit;

    /**
     * The type of the resource to limit
     * (Required)
     * 
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * The type of the resource to limit
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * The quantity to limit this resource
     * (Required)
     * 
     */
    @JsonProperty("limit")
    public Long getLimit() {
        return limit;
    }

    /**
     * The quantity to limit this resource
     * (Required)
     * 
     */
    @JsonProperty("limit")
    public void setLimit(Long limit) {
        this.limit = limit;
    }

}
