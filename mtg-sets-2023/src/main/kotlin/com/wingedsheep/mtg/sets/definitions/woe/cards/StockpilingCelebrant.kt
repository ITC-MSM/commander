package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stockpiling Celebrant
 * {2}{W}
 * Creature — Dwarf Knight
 * 3/2
 *
 * When this creature enters, you may return another target nonland permanent you control to its
 * owner's hand. If you do, scry 2.
 *
 * `optional = true` models the "you may" — declining skips the whole effect (no bounce, no scry).
 * The target is chosen when the trigger goes on the stack; if it becomes illegal before
 * resolution the ability has no legal target and is removed, so the "if you do" scry never fires.
 * When the player accepts, [Effects.Move] returns the permanent to its owner's hand and then
 * [Effects.Scry] follows.
 */
val StockpilingCelebrant = card("Stockpiling Celebrant") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Knight"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, you may return another target nonland permanent you " +
        "control to its owner's hand. If you do, scry 2."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target(
            "target",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.NonlandPermanent.youControl(), excludeSelf = true)
            )
        )
        effect = Effects.Move(t, Zone.HAND) then Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Raluca Marinescu"
        flavorText = "\"What? I'm saving it for later.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0214ebc6-c59f-4170-8dd2-ce07caa6e6ad.jpg?1783915126"
    }
}
