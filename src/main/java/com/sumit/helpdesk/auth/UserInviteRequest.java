package com.sumit.helpdesk.auth;

public record UserInviteRequest(String email, Role role, String password) {}
