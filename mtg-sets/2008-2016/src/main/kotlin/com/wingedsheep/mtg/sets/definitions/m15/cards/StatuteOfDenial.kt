package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Statute of Denial
 * {2}{U}{U}
 * Instant
 * Counter target spell. If you control a blue creature, draw a card, then discard a card.
 *
 * The loot rider is a state check at resolution ([Gate.WhenCondition]), not an intervening-if — the
 * blue creature only has to be there when Statute of Denial resolves.
 */
val StatuteOfDenial = card("Statute of Denial") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. If you control a blue creature, draw a card, then discard a card."

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterSpell()
            .then(
                GatedEffect(
                    gate = Gate.WhenCondition(
                        Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.withColor(Color.BLUE))
                    ),
                    then = Patterns.Hand.loot()
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Zoltan Boros"
        flavorText = "\"Pyrotechnic activity of any sort is strictly prohibited. It is irrelevant that today is a holiday.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af13770d-dddb-4b78-9cd3-4a0dc50472f4.jpg?1783939187"
    }
}
