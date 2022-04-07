/*
 * Copyright 2021 Red Hat
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
package io.apicurio.tenantmanager.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.apicurio.tenantmanager.api.datamodel.NewApicurioTenantRequest;
import io.apicurio.tenantmanager.api.datamodel.ApicurioTenant;
import io.apicurio.tenantmanager.api.datamodel.ApicurioTenantList;
import io.apicurio.tenantmanager.api.datamodel.SortBy;
import io.apicurio.tenantmanager.api.datamodel.SortOrder;
import io.apicurio.tenantmanager.api.datamodel.TenantStatusValue;
import io.apicurio.tenantmanager.api.datamodel.UpdateApicurioTenantRequest;
import io.apicurio.tenantmanager.api.dto.DtoMappers;
import io.apicurio.tenantmanager.api.services.TenantResourcesService;
import io.apicurio.tenantmanager.api.services.TenantStatusService;
import io.apicurio.tenantmanager.logging.audit.Audited;
import io.apicurio.tenantmanager.metrics.UsageMetrics;
import io.apicurio.tenantmanager.storage.ApicurioTenantStorage;
import io.apicurio.tenantmanager.storage.TenantNotFoundException;
import io.apicurio.tenantmanager.storage.dto.ApicurioTenantDto;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

/**
 * @author Fabian Martinez
 */
@ApplicationScoped
public class TenantsResourceImpl implements TenantsResource {

    @Inject
    ApicurioTenantStorage tenantsRepository;

    @Inject
    TenantStatusService tenantStatusService;

    @Inject
    TenantResourcesService resourcesService;

    @Inject
    UsageMetrics usageMetrics;

    @Override
    public ApicurioTenantList getTenants(@QueryParam("status") String status,
            @QueryParam("offset") @Min(0) Integer offset, @QueryParam("limit") @Min(1) @Max(500) Integer limit,
            @QueryParam("order") SortOrder order, @QueryParam("orderby") SortBy orderby) {

        offset = (offset != null) ? offset : 0;
        limit = (limit != null) ? limit : 20;
        order = (order != null) ? order : SortOrder.asc;
        orderby = (orderby != null) ? orderby : SortBy.tenantId;

        Sort sort = Sort.by(orderby.value()).direction(order == SortOrder.asc ? Direction.Ascending : Direction.Descending);

        String query = "";
        Parameters parameters = new Parameters();
        if (status != null) {
            query += "status = :status";
            parameters = parameters.and("status", status);
        }

        List<ApicurioTenantDto> items = tenantsRepository.queryTenants(query, sort, parameters, offset, limit);
        Long total = tenantsRepository.count(query, parameters);

        ApicurioTenantList list = new ApicurioTenantList();
        list.setItems(items.stream().map(ApicurioTenantDto::toDatamodel).collect(Collectors.toList()));
        list.setCount(total.intValue());
        return list;
    }

    @Override
    @Transactional
    @Audited
    public Response createTenant(NewApicurioTenantRequest tenantRequest) {

        required(tenantRequest.getTenantId(), "TenantId is mandatory");
        required(tenantRequest.getOrganizationId(), "OrganizationId is mandatory");

        ApicurioTenantDto tenant = new ApicurioTenantDto();

        tenant.setTenantId(tenantRequest.getTenantId());
        tenant.setOrganizationId(tenantRequest.getOrganizationId());
        tenant.setName(tenantRequest.getName());
        tenant.setDescription(tenantRequest.getDescription());
        tenant.setCreatedOn(new Date());
        tenant.setCreatedBy(tenantRequest.getCreatedBy());
        tenant.setStatus(TenantStatusValue.READY.value());

        if (tenantRequest.getResources() != null) {
            //find duplicates, invalid config
            resourcesService.validateResources(tenantRequest.getResources());

            tenantRequest.getResources()
                .stream()
                .map(DtoMappers::toStorageDto)
                .forEach(dto -> tenant.addResource(dto));
        }

        tenantsRepository.save(tenant);

        return Response.status(Status.CREATED).entity(tenant.toDatamodel()).build();
    }

    @Override
    public ApicurioTenant getTenant(@PathParam("tenantId") String tenantId) {
        return tenantsRepository.findByTenantId(tenantId)
                .map(ApicurioTenantDto::toDatamodel)
                .orElseThrow(() -> TenantNotFoundException.create(tenantId));
    }

    /**
     * @see io.apicurio.tenantmanager.api.TenantsResource#updateTenant(java.lang.String, io.apicurio.tenantmanager.api.datamodel.UpdateApicurioTenantRequest)
     */
    @Override
    @Transactional
    @Audited
    public void updateTenant(String tenantId, UpdateApicurioTenantRequest tenantRequest) {
        boolean updated = false;
        ApicurioTenantDto tenant = tenantsRepository.findByTenantId(tenantId).orElseThrow(() -> TenantNotFoundException.create(tenantId));
        if (tenantRequest.getName() != null) {
            tenant.setName(tenantRequest.getName());
            updated = true;
        }
        if (tenantRequest.getDescription() != null) {
            tenant.setDescription(tenantRequest.getDescription());
            updated = true;
        }

        if (tenantRequest.getResources() != null) {
            //find duplicates, invalid config
            resourcesService.validateResources(tenantRequest.getResources());

            // First remove all the old resources
            if (tenant.getResources() != null) {
                new ArrayList<>(tenant.getResources()).forEach(r -> tenant.removeResource(r));
            }

            // Now add in the new ones
            tenantRequest.getResources()
                .stream()
                .map(DtoMappers::toStorageDto)
                .forEach(dto -> tenant.addResource(dto));
            updated = true;
        }

        if (tenantRequest.getStatus() != null) {
            if (!tenantStatusService.verifyTenantStatusChange(tenant, tenantRequest.getStatus())) {
                throw new BadRequestException(
                        String.format("Invalid new tenant status, status change from %s to %s is not allowed", tenant.getStatus(), tenantRequest.getStatus().value()));
            }
            tenant.setStatus(tenantRequest.getStatus().value());
            updated = true;
            //very important to call this before modifiying the previous modifiedOn date
            usageMetrics.tenantStatusChanged(tenant);
        }

        if (updated) {
            tenant.setModifiedOn(new Date());
            tenantsRepository.save(tenant);
        }

    }

    @Override
    @Transactional
    @Audited
    public void deleteTenant(@PathParam("tenantId") String tenantId) {
        ApicurioTenantDto tenant = tenantsRepository.findByTenantId(tenantId).orElseThrow(() -> TenantNotFoundException.create(tenantId));
        if (!tenantStatusService.verifyTenantStatusChange(tenant, TenantStatusValue.TO_BE_DELETED)) {
            throw new BadRequestException(
                    String.format("Unable to mark tenant to be deleted, status change from %s to %s is not allowed", tenant.getStatus(), TenantStatusValue.TO_BE_DELETED.value()));
        }
        tenant.setModifiedOn(new Date());
        tenant.setStatus(TenantStatusValue.TO_BE_DELETED.value());
        tenantsRepository.save(tenant);
    }

    private void required(String parameter, String message) {
        if (parameter == null || parameter.isEmpty()) {
            throw new BadRequestException(message);
        }
    }

}
