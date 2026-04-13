package com.auth.adapter.in.credentials;

/**
 * Réponse JSON minimale pour opérations sans corps métier (ex. suppression réussie).
 */
public record ApiMessageResponse(String message) {
}
