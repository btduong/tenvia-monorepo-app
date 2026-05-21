package com.tenvia.shop.dto;

import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.user.dto.UserDTO;

public record PowerUpResponseDTO(UserDTO updatedUser, AppliedEffectResult effectResult) {}
