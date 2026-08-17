package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stoneskin
 * {2}{W}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * Enchanted creature gets +0/+10.
 *
 * A plain Aura: [TargetPermanent] over [GameObjectFilter.Creature] is the "enchant creature" line,
 * and the whole card is one [ModifyStats] whose default filter is already the attached creature —
 * no trigger, no target of its own. [Keyword.FLASH] is the only thing that makes it an instant-speed
 * combat trick.
 */
val Stoneskin = card("Stoneskin") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\n" +
        "Enchant creature\n" +
        "Enchanted creature gets +0/+10."

    keywords(Keyword.FLASH)

    auraTarget = TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature))

    staticAbility {
        ability = ModifyStats(0, 10)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Jake Murray"
        flavorText = "\"Ooh, nice shot! I almost felt that one.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16b9fdaa-9da9-48ff-b271-e6a41aabf073.jpg?1783922802"
    }
}
