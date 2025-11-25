package loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Card.Card;
import Card.MonsterCard;
import Card.SpellCard;
import Card.TrapCard;
import Player.Player;
import Card.Effect;

/**
 * Utility class for loading Yu-Gi-Oh! cards from the YGOPRODeck API.
 * <p>
 * This class provides methods to fetch card data from the official YGOPRODeck database
 * and convert it into usable {@link Card} objects for the game. It supports loading
 * individual cards by name, loading entire sets, and loading random selections from sets.
 * </p>
 * <p>
 * The loader uses the YGOPRODeck API v7 at {@code https://db.ygoprodeck.com/api/v7/}
 * to retrieve card information including stats, descriptions, images, and metadata.
 * All network requests are handled synchronously and may throw exceptions if the API
 * is unavailable or if card data is malformed.
 * </p>
 * <p>
 * <strong>Key Features:</strong>
 * <ul>
 * <li>Load individual cards by exact name</li>
 * <li>Load complete card sets (e.g., Starter Decks)</li>
 * <li>Load random card selections from sets</li>
 * <li>Automatic card type detection (Monster, Spell, Trap)</li>
 * <li>Safe JSON parsing with fallback values</li>
 * <li>Image URL extraction with default fallback</li>
 * </ul>
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class CardLoader {

    /**
     * Loads a single card by its exact name from the YGOPRODeck API.
     * <p>
     * This method queries the API for a card with the specified name and constructs
     * the appropriate Card object (MonsterCard, SpellCard, or TrapCard) based on
     * the card's type. The card is assigned to the specified owner.
     * </p>
     * <p>
     * The card name must match exactly (case-insensitive) with the official card name
     * in the database. If multiple cards match, only the first result is returned.
     * </p>
     * <p>
     * <strong>Example usage:</strong>
     * <pre>
     * Card blueEyes = CardLoader.loadCard("Blue-Eyes White Dragon", player);
     * </pre>
     * </p>
     *
     * @param cardName the exact name of the card to load
     * @param owner the Player who will own this card
     * @return a Card object (MonsterCard, SpellCard, or TrapCard) with data from the API
     * @throws Exception if the network request fails, the card is not found, or JSON parsing fails
     */
    public static Card loadCard(String cardName, Player owner) throws Exception {
        String apiUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name="
                        + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        URL url = new URL(apiUrl);

        try (InputStreamReader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject cardObj = root.getAsJsonArray("data").get(0).getAsJsonObject();

            return buildCardFromJson(cardObj, owner);
        }
    }

    /**
     * Loads a random selection of cards from a specific card set.
     * <p>
     * This method fetches all cards from the specified set URL, shuffles them randomly,
     * and returns the requested number of cards. This is useful for creating randomized
     * decks or booster pack simulations from official card sets.
     * </p>
     * <p>
     * <strong>Example usage:</strong>
     * <pre>
     * String yugiStarterUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=Starter+Deck:+Yugi";
     * List&lt;Card&gt; randomCards = CardLoader.loadRandomCardsFromSet(yugiStarterUrl, player, 5);
     * </pre>
     * </p>
     * <p>
     * If the requested count exceeds the number of available cards in the set,
     * all cards from the set are returned.
     * </p>
     *
     * @param setUrl the API URL for the card set to load from
     * @param owner the Player who will own these cards
     * @param count the number of random cards to select from the set
     * @return a List of randomly selected Card objects from the set
     * @throws Exception if the network request fails or JSON parsing fails
     */
    public static List<Card> loadRandomCardsFromSet(String setUrl, Player owner, int count) throws Exception {
        URL url = new URL(setUrl);

        try (InputStreamReader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            List<Card> allCards = new ArrayList<>();

            for (var element : root.getAsJsonArray("data")) {
                JsonObject cardObj = element.getAsJsonObject();
                Card card = buildCardFromJson(cardObj, owner);
                if (card != null) {
                    allCards.add(card);
                }
            }

            Collections.shuffle(allCards);
            return allCards.subList(0, Math.min(count, allCards.size()));
        }
    }

    /**
     * Loads all cards from a specific card set without shuffling or limiting.
     * <p>
     * This method retrieves every card from the specified set and returns them
     * in their original API order. This is ideal for loading complete Starter Decks,
     * Structure Decks, or any set where all cards are needed.
     * </p>
     * <p>
     * <strong>Example usage:</strong>
     * <pre>
     * String kaibaStarterUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=Starter+Deck:+Kaiba";
     * List&lt;Card&gt; kaibaCards = CardLoader.loadCardsFromSet(kaibaStarterUrl, player);
     * </pre>
     * </p>
     *
     * @param setUrl the API URL for the card set to load
     * @param owner the Player who will own these cards
     * @return a List containing all Card objects from the set
     * @throws Exception if the network request fails or JSON parsing fails
     */
    public static List<Card> loadCardsFromSet(String setUrl, Player owner) throws Exception {
        URL url = new URL(setUrl);

        try (InputStreamReader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            List<Card> allCards = new ArrayList<>();

            for (var element : root.getAsJsonArray("data")) {
                JsonObject cardObj = element.getAsJsonObject();
                Card card = buildCardFromJson(cardObj, owner);
                if (card != null) {
                    allCards.add(card);
                }
            }

            return allCards; // no shuffle or limit
        }
    }

    /**
     * Constructs a Card object from a JSON object retrieved from the API.
     * <p>
     * This method analyzes the card's type field to determine whether it's a
     * Monster, Spell, or Trap card, then creates the appropriate Card subclass
     * with all relevant data extracted from the JSON.
     * </p>
     * <p>
     * The method uses safe extraction methods to handle missing or null fields
     * gracefully, ensuring that cards with incomplete data can still be loaded
     * with default values where necessary.
     * </p>
     * <p>
     * <strong>Card Type Detection:</strong>
     * <ul>
     * <li>If type contains "Monster" → creates MonsterCard</li>
     * <li>If type equals "Spell Card" → creates SpellCard</li>
     * <li>If type equals "Trap Card" → creates TrapCard</li>
     * <li>Otherwise → returns null</li>
     * </ul>
     * </p>
     *
     * @param cardObj the JsonObject containing card data from the API
     * @param owner the Player who will own this card
     * @return a Card object (MonsterCard, SpellCard, or TrapCard), or null if the type is unrecognized
     */
    private static Card buildCardFromJson(JsonObject cardObj, Player owner) {
        String type = safeString(cardObj, "type");
        String imageUrl = safeImageUrl(cardObj);

        if (type.contains("Monster")) {
            return new MonsterCard(
                safeString(cardObj, "name"),
                safeString(cardObj, "desc"),
                safeInt(cardObj, "id"),
                owner,
                safeInt(cardObj, "atk"),
                safeInt(cardObj, "def"),
                safeInt(cardObj, "level"),
                safeString(cardObj, "attribute"),
                safeString(cardObj, "race"),
                "Normal",
                type,
                imageUrl
            );
        } else if (type.equals("Spell Card")) {
            return new SpellCard(
                safeString(cardObj, "name"),
                safeString(cardObj, "desc"),
                safeInt(cardObj, "id"),
                owner,
                safeString(cardObj, "race"),
                null,
                type.contains("Continuous"),
                imageUrl
            );
        } else if (type.equals("Trap Card")) {
            return new TrapCard(
                safeString(cardObj, "name"),
                safeString(cardObj, "desc"),
                safeInt(cardObj, "id"),
                owner,
                safeString(cardObj, "race"),
                null,
                type.contains("Continuous"),
                imageUrl
            );
        }

        return null;
    }

    /**
     * Safely extracts an integer value from a JSON object.
     * <p>
     * If the specified key doesn't exist or contains a null value, returns 0
     * as a default. This prevents NullPointerExceptions when processing card
     * data that may have missing fields.
     * </p>
     *
     * @param obj the JsonObject to extract from
     * @param key the field name to retrieve
     * @return the integer value, or 0 if the field is missing or null
     */
    private static int safeInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

    /**
     * Safely extracts a string value from a JSON object.
     * <p>
     * If the specified key doesn't exist or contains a null value, returns an
     * empty string as a default. This prevents NullPointerExceptions and ensures
     * all string fields have non-null values.
     * </p>
     *
     * @param obj the JsonObject to extract from
     * @param key the field name to retrieve
     * @return the string value, or an empty string if the field is missing or null
     */
    private static String safeString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    /**
     * Safely extracts the image URL from a card's JSON data.
     * <p>
     * The API provides multiple image variations in a "card_images" array.
     * This method retrieves the URL from the first image entry, validates that
     * it's a proper HTTP/HTTPS URL, and returns a default placeholder if the
     * image data is missing or invalid.
     * </p>
     * <p>
     * <strong>Fallback behavior:</strong>
     * If the image URL is missing, null, empty, or doesn't start with "http",
     * returns {@code "file:resources/ui/default.png"} as a fallback image.
     * </p>
     *
     * @param cardObj the JsonObject containing card data from the API
     * @return the image URL as a String, or a default placeholder path if unavailable
     */
    private static String safeImageUrl(JsonObject cardObj) {
        try {
            JsonObject firstImg = cardObj.getAsJsonArray("card_images")
                                         .get(0).getAsJsonObject();
            String url = firstImg.get("image_url").getAsString();
            if (url != null && !url.isEmpty() && url.startsWith("http")) {
                return url;
            }
        } catch (Exception ignored) { }
        return "file:resources/ui/default.png";
    }
}