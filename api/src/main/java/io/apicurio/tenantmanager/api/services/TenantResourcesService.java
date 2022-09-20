/*
 * Copyright 2022 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.tenantmanager.api.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.apicurio.tenantmanager.api.datamodel.TenantResource;

/**
 * @author Fabian Martinez
 */
@ApplicationScoped
public class TenantResourcesService {

    @ConfigProperty(name = "tenant-manager.tenant-resources.types")
    Set<String> supportedResources;

    public boolean isResourceTypeSupported(String type) {
        return supportedResources.contains(type);
    }

    /**
     * Finds duplicates or unsupported resource types
     * @param tenantResources list of tenant resources
     */
    public void validateResources(List<TenantResource> tenantResources) {
        Set<String> items = new HashSet<>();
        for (var resource : tenantResources) {
            if (!isResourceTypeSupported(resource.getType())) {
                throw new BadRequestException(
                        String.format("Invalid configuration, resource type %s is not supported", resource.getType()));
            }
            if (!items.add(resource.getType())) {
                throw new BadRequestException(
                        String.format("Invalid configuration, resource type %s is duplicated", resource.getType()));
            }
        }
    }

}
