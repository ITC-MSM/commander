package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Mirari Conjecture
 * {4}{U}
 * Enchantment — Saga
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Return target instant card from your graveyard to your hand.
 * II — Return target sorcery card from your graveyard to your hand.
 * III — Until end of turn, whenever you cast an instant or sorcery spell, copy it.
 *       You may choose new targets for the copy.
 *
 */
val TheMirariConjecture = card("The Mirari Conjecture") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Return target instant card from your graveyard to your hand.\n" +
        "II — Return target sorcery card from your graveyard to your hand.\n" +
        "III — Until end of turn, whenever you cast an instant or sorcery spell, copy it. You may choose new targets for the copy."

    sagaChapter(1) {
        val instant = target(
            "instant card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Instant.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(instant)
    }

    sagaChapter(2) {
        val sorcery = target(
            "sorcery card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Sorcery.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(sorcery)
    }

    sagaChapter(3) {
        effect = Effects.CopyEachSpellCast(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "James Arnold"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c68954c-4bab-4973-9819-ecd084438303.jpg?1562732195"
    }
}
