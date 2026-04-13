package com.auth.lib;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Motifs réutilisables (alignés sur les regex TypeScript du projet).
 */
public final class Utils {

	private Utils() {
	}

	/** 7 à 14 chiffres après l’indicatif pays ({@code +} obligatoire, pas de 0 juste après). */
	public static final Pattern PHONE_REGEX = Pattern.compile("^\\+[1-9]\\d{7,14}$");

	/**
	 * Alphabet officiel nanoid, longueur par défaut 21 ; chaîne entière (pas de sous-chaîne).
	 */
	public static final Pattern NANOID_REGEX = Pattern.compile("^[A-Za-z0-9_-]{21}$");

	/** UUID (RFC 4122) ; insensible à la casse comme le flag {@code i} en TS. */
	public static final Pattern UUID_REGEX = Pattern.compile(
			"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Au moins un caractère non espace / non @, puis @, puis au moins un segment, point, extension.
	 */
	public static final Pattern EMAIL_REGEX = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	/**
	 * Au moins une lettre, un chiffre, un caractère non alphanumérique ; longueur minimale 8.
	 */
	public static final Pattern PASSWORD_REGEX = Pattern.compile(
			"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

	/**
	 * Empreinte bcrypt ({@code $2a$}…), pour valider un champ déjà hashé côté domaine.
	 */
	public static final Pattern BCRYPT_HASH_REGEX = Pattern.compile(
			"^\\$2[abxy]?\\$[0-9]{2}\\$[./A-Za-z0-9]{53}$");

	private static final String NANO_ALPHABET =
			"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-";
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	/**
	 * Identifiant public nanoid (21 caractères, alphabet aligné sur {@link #NANOID_REGEX}).
	 */
	public static String newNanoid() {
		StringBuilder sb = new StringBuilder(21);
		for (int i = 0; i < 21; i++) {
			sb.append(NANO_ALPHABET.charAt(SECURE_RANDOM.nextInt(NANO_ALPHABET.length())));
		}
		return sb.toString();
	}

	/**
	 * Jeton opaque (URL-safe Base64), pour access / refresh tokens stockés en base.
	 */
	public static String newOpaqueToken(int numBytes) {
		byte[] buf = new byte[numBytes];
		SECURE_RANDOM.nextBytes(buf);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
	}
}
