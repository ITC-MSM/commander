package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Drainpipe Vermin
 * {B}
 * Creature — Rat
 * 1/1
 *
 * When this creature dies, you may pay {B}. If you do, target player discards a card.
 */
val DrainpipeVermin = card("Drainpipe Vermin") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat"
    oracleText = "When this creature dies, you may pay {B}. If you do, target player discards a card."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val player = target("target player", Targets.Player)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{B}"),
            effect = Effects.Discard(1, player),
        )
        description = "When this creature dies, you may pay {B}. If you do, target player discards a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Trevor Claxton"
        flavorText = "When times are tough, the poor eat the rats. When times are tougher, the rats eat the poor."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d7251f3-df66-4611-a84c-1897f74431f7.jpg?1783940362"
    }
}
