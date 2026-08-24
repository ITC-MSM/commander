package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fretwork Colony
 * {1}{B}
 * Creature — Insect
 * 1 / 1
 *
 * This creature can't block.
 * At the beginning of your upkeep, put a +1/+1 counter on this creature and you lose 1 life.
 *
 * The upkeep clause is a single trigger doing two things, not two triggers — the counter and the
 * life loss are one composite, so they resolve together and neither can be responded to between
 * them. The life loss names the controller explicitly; [Effects.LoseLife] otherwise defaults to
 * the targeted opponent.
 */
val FretworkColony = card("Fretwork Colony") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    oracleText = "This creature can't block.\n" +
        "At the beginning of your upkeep, put a +1/+1 counter on this creature and you lose 1 life."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            Effects.LoseLife(1, EffectTarget.Controller),
        )
    }

    staticAbility {
        ability = CantBlock()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Christopher Burdett"
        flavorText = "The swarm leaves behind a flawless pattern that only occasionally threatens the structural integrity of the wood."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46bf6ae3-352b-416c-a404-de51cd624198.jpg?1783937205"
    }
}
