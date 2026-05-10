package com.tenvia.services;

import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.UserDTO;

public record PowerUpResponse (UserDTO updatedUser, AppliedEffectResult effectResult) {}
