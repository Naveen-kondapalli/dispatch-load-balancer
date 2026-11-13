package com.freightfox.dispatch.dto;

import java.util.List;

public record ErrorResponse(String status, String message, List<String> details) {}
