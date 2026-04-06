package com.auth.lib;

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
}
