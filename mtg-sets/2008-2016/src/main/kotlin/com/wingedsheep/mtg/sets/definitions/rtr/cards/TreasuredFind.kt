package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Treasured Find
 * {B}{G}
 * Sorcery
 *
 * Return target card from your graveyard to your hand. Exile Treasured Find.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The self-exile rider is a second move in the composite, on [EffectTarget.Self]. Because it is
 * part of the resolution rather than a replacement, a Treasured Find countered on the stack goes
 * to the graveyard normally.
 */
val TreasuredFind = card("Treasured Find") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Return target card from your graveyard to your hand. Exile Treasured Find."

    spell {
        val c = target(
            "target card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Any.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.Composite(
            Effects.ReturnToHand(c),
            Effects.Exile(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "Jason Chan"
        flavorText = "Gorgons crave beautiful things: gems, exquisite amulets, the alabaster corpses of the petrified dead . . ."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2c0e00b-2290-493f-a3fc-3b9bff2830cc.jpg?1783940330"
    }
}
