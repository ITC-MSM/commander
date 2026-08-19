package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Knights of Thorn
 * {3}{W}
 * Creature — Human Knight
 * 2/2
 *
 * Protection from red; banding
 *
 * Both keywords are handled by the engine — Protection via CR 702.16, Banding via CR 702.22
 * (CombatManager / CombatDamageManager / AttackPhaseManager) — so the card needs no per-card
 * wiring beyond declaring them.
 */
val KnightsOfThorn = card("Knights of Thorn") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Protection from red; banding (Any creatures with banding, and up to one " +
        "without, can attack in a band. Bands are blocked as a group. If any creatures with " +
        "banding you control are blocking or being blocked by a creature, you divide that " +
        "creature's combat damage, not its controller, among any of the creatures it's being " +
        "blocked by or is blocking.)"

    keywords(Keyword.BANDING)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Christopher Rush"
        flavorText = "\"With a great cry, the Goblin host broke and ran as the first wave of Knights penetrated its ranks.\" —Tivadar of Thorn, *History of the Goblin Wars*"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae541c73-9903-49e6-997a-db4701135145.jpg?1783947948"
    }
}
