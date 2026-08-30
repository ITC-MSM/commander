package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanBlockAdditionalForCreatureGroup
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Selesnya Sagittars
 * {3}{G}{W}
 * Creature — Elf Archer
 * 2/5
 * Reach (This creature can block creatures with flying.)
 * This creature can block an additional creature each combat.
 *
 * [CanBlockAdditionalForCreatureGroup] scoped to [GroupFilter.source] — the same static Brave the
 * Sands hands to a whole team, pointed at just this Elf. Both the blocker-legality check
 * (`BlockPhaseManager`) and the legal-action enumerator read `additionalBlockCount`, so the extra
 * block is offered as well as allowed.
 */
val SelesnyaSagittars = card("Selesnya Sagittars") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Elf Archer"
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "This creature can block an additional creature each combat."
    power = 2
    toughness = 5
    keywords(Keyword.REACH)
    staticAbility {
        ability = CanBlockAdditionalForCreatureGroup(
            count = 1,
            filter = GroupFilter.source(),
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Edward P. Beard, Jr."
        flavorText = "\"What's their strike range, you ask? Let's put it this way: sagittars aim " +
            "their bows using *maps*.\"\n—Otak, Tin Street shopkeep"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a72e933-d977-4be9-985f-8b8cf1267f8c.jpg"
    }
}
