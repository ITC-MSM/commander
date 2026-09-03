package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Vedalken Outlander — Conflux #132
 * {W}{U} · Artifact Creature — Vedalken Scout · 2/2
 *
 * Protection from red
 *
 * The Esper member of the "Outlander" cycle — see [GoblinOutlander] — and the only one printed as
 * an artifact creature, which is carried entirely by the type line. The rules text is the printed
 * protection keyword: [KeywordAbility.Protection] over a [ProtectionScope.Color] scope, projected
 * by the engine for targeting, blocking, damage and aura fall-off.
 */
val VedalkenOutlander = card("Vedalken Outlander") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Artifact Creature — Vedalken Scout"
    power = 2
    toughness = 2
    oracleText = "Protection from red"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Izzy"
        flavorText = "The Seekers of Carmot searched across the unknown lands for the mystical red stone that could reforge Esper in ethereal perfection."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d75a7314-39f2-4e32-a5b0-ac1761b6d238.jpg"
    }
}
