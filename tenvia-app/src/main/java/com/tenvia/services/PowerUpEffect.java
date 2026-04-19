package com.tenvia.services;

import java.util.List;

public sealed interface PowerUpEffect permits FiftyFiftyEffect, HammerEffect {
    List<Integer> hiddenSelectionsIds();
}

// The name 'hiddenSelectionsIds' needs to be persistent for both record so that the client dont have to check.
record FiftyFiftyEffect(List<Integer> hiddenSelectionsIds) implements PowerUpEffect {}
record HammerEffect(List<Integer> hiddenSelectionsIds) implements PowerUpEffect {}
