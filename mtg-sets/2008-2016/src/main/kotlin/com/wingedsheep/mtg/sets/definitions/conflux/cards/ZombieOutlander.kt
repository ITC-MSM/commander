package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Zombie Outlander — Conflux #133
 * {U}{B} · Creature — Zombie Scout · 2/2
 *
 * Protection from green
 *
 * The Grixis member of the "Outlander" cycle — see [GoblinOutlander]. Its whole script is the
 * printed protection keyword, spelled [KeywordAbility.Protection] with a [ProtectionScope.Color]
 * scope; the engine projects it and reads it for targeting, blocking, damage and aura fall-off.
 */
val ZombieOutlander = card("Zombie Outlander") {
    manaCost = "{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Zombie Scout"
    power = 2
    toughness = 2
    oracleText = "Protection from green"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.GREEN)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Nils Hamm"
        flavorText = "The ripe smell of life drifted into Grixis. The dead caught the scent and with reckless hunger followed it back into Jund."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f27c2690-5121-452b-b82a-72acb8be6878.jpg"
    }
}
