package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Miraculous Recovery
 * {4}{W}
 * Instant
 * Return target creature card from your graveyard to the battlefield. Put a +1/+1 counter on it.
 *
 * The reanimated permanent keeps its entity id across the graveyard→battlefield move, so the
 * follow-up counter lands on the same object: [Effects.Move] then [Effects.AddCounters] on the same
 * target reference. That ordering is also what the rulings describe — the creature enters *without*
 * the counter and receives it afterwards, with no priority window in between.
 */
val MiraculousRecovery = card("Miraculous Recovery") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Return target creature card from your graveyard to the battlefield. Put a +1/+1 counter on it."

    spell {
        val creatureCard = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard
        )
        effect = Effects.Move(creatureCard, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            .then(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creatureCard))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Brian Horton"
        flavorText = "\"You stop breathing for just a few minutes and everyone jumps to conclusions.\" —Zarkuu, necrosavant"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76fecb31-790a-4454-918e-5aeb253021f0.jpg?1783947006"
        ruling("2018-12-07", "The creature returns to the battlefield without a +1/+1 counter on it. Any abilities that trigger when a creature enters the battlefield will trigger or not as appropriate before it receives the +1/+1 counter and resolve after it has received that counter.")
        ruling("2018-12-07", "No player may take actions between the time the creature returns to the battlefield and the time you put a +1/+1 counter on it.")
    }
}
