package com.sumit.helpdesk.common;

import java.time.Instant;

public record ApiError(String message, Instant timestamp) {}
