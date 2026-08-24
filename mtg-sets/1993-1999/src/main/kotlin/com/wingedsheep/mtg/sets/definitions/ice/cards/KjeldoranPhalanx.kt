package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kjeldoran Phalanx
 * {5}{W}
 * Creature — Human Soldier
 * 2/5
 *
 * First strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * Two printed keywords and nothing else. Banding is engine-live — `CombatDamageManager`
 * and `CombatDamageUtils` both consult it for damage assignment — so declaring it is real behaviour.
 */
val KjeldoranPhalanx = card("Kjeldoran Phalanx") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 5
    oracleText = "First strike; banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.BANDING)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Richard Kane Ferguson"
        flavorText = "\"There's nothing I like better than watching a street full of soldiers kicking down the doors of the guilty and the impure.\"\n—Avram Garrisson, Leader of the Knights of Stromgald"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6e91ba0-b229-4ab1-84f3-2a490dfa5051.jpg"
    }
}
