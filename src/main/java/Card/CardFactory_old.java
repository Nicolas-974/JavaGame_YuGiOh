// package Card;
// import Player.Player;
// import Card.MonsterCard;
// import Card.SpellCard;
// import Card.TrapCard;
// import Card.Effect;

// public class CardFactory {


//     // Exemple de monstre emblématique
//     public static MonsterCard createDarkMagician(Player owner) {
//         MonsterCard card = new MonsterCard(
//             "Dark Magician",
//             "The ultimate wizard in terms of attack and defense.",
//             46986414,
//             owner,
//             2500, 2100, 7,
//             "DARK", "Spellcaster", "Normal"
//         );

//         return card;
//     }

//     public static MonsterCard createBlueEyes(Player owner) {
//         MonsterCard card = new MonsterCard(
//             "Blue-Eyes White Dragon",
//             "This legendary dragon is a powerful engine of destruction. Virtually invincible, very few have faced this awesome creature and lived to tell the tale.",
//             89631139,
//             owner,
//             3000, 2500, 8,
//             "LIGHT", "Dragon", "Normal"
//         );

//         return card;
//     }

//     public static MonsterCard createWallOfIlluion(Player owner) {

//         Effect returnHand = new Effect("Returns from your opponent's field to your opponent's hand", "Trigger");


//         MonsterCard card = new MonsterCard(
//             "Wall of Illusion",
//             "If this card is attacked by a monster, after damage calculation: Return that monster to the hand.",
//             13945283,
//             owner,
//             1000, 1850, 4,
//             "DARK", "Fiend", "Effect",
//             returnHand,
//             "Trigger"
//         );

//         return card;
//     }

//     public static MonsterCard monster2(Player owner) {

//         Effect reveal = new Effect("Reveals face-down cards", "Trigger");


//         MonsterCard card = new MonsterCard(
//             "The Stern Mystic",
//             "FLIP: Reveal all face-down cards on the field (Flip Effects are not activated), then return them to their original positions.",
//             87557188,
//             owner,
//             1500, 1200, 4,
//             "LIGHT", "Spellcaster", "Flip", "Effect",
//             reveal,
//             "Flip"
//         );

//         return card;
//     }

//     public static SpellCard spell1(Player owner) {

//         Effect specialSummon = new Effect("Special Summons from your Graveyar/opponent's Graveyard to your field", "Trigger");


//         SpellCard card = new SpellCard(
//             "Monster Reborn",
//             "Target 1 monster in either GY; Special Summon it.",
//             83764719,
//             owner,
//             "Normal",
//             specialSummon,
//             false
//         );

//         return card;
//     }

//     public static SpellCard spell2(Player owner) {

//         Effect equip = new Effect("Equipped gains ATK / Equipped gains DEF", "Trigger");


//         SpellCard card = new SpellCard(
//             "Book of Secret Arts",
//             "A Spellcaster-Type monster equipped with this card increases its ATK and DEF by 300 points.",
//             91595718,
//             owner,
//             "Equip",
//             equip,
//             true
//         );

//         return card;
//     }

//     public static SpellCard spell3(Player owner) {

//         Effect typeBooster = new Effect("All monsters gain ATK / All monsters gain DEF / All monsters lose ATK / All monsters lose DEF", "Trigger");


//         SpellCard card = new SpellCard(
//             "Yami",
//             "All Fiend and Spellcaster monsters on the field gain 200 ATK/DEF, also all Fairy monsters on the field lose 200 ATK/DEF.",
//             59197169,
//             owner,
//             "Field",
//             typeBooster,
//             true
//         );

//         return card;
//     }

//     public static TrapCard trap1(Player owner) {

//         Effect destroy = new Effect("Destroys your opponent's Attack Position Monster Cards", "Trigger");


//         TrapCard card = new TrapCard(
//             "Mirror Force",
//             "When an opponent's monster declares an attack: Destroy all your opponent's Attack Position monsters.",
//             44095762,
//             owner,
//             "Normal",
//             destroy,
//             false
//         );

//         return card;
//     }

//     public static TrapCard trap2(Player owner) {

//         Effect extraSummon = new Effect("Allows multiple Normal Summons / Performs a Normal Summon / Performs a Normal Set", "Trigger");


//         TrapCard card = new TrapCard(
//             "Ultimate Offering",
//             "During your Main Phase or your opponent's Battle Phase: You can pay 500 Life Points; immediately after this effect resolves, Normal Summon/Set 1 monster.",
//             44095762,
//             owner,
//             "Continuous",
//             extraSummon,
//             true
//         );

//         return card;
//     }

//     public static MonsterCard createFusionMonster(Player owner) {
//     return new MonsterCard(
//         "Gaia the Dragon Champion",
//         "Gaia the Fierce Knight + Curse of Dragon",
//         66889139,          // ID officiel de la carte
//         owner,
//         2600,              // ATK
//         2100,              // DEF
//         7,                 // Niveau
//         "WIND",            // Attribut
//         "Dragon",          // Type
//         "Fusion"           // Sous-type
//     );
// }

    

// }