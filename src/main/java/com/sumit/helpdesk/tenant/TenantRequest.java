package com.sumit.helpdesk.tenant;

public record TenantRequest(String slug, String name, String adminEmail, String adminPassword) {}
