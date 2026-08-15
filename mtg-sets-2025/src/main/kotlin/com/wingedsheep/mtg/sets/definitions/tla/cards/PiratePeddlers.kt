package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pirate Peddlers
 * {2}{B}
 * Creature — Human Pirate
 * 2/2
 *
 * Deathtouch
 * Whenever you sacrifice another permanent, put a +1/+1 counter on this creature.
 *
 * The sacrifice ability uses the per-permanent `YouSacrificeAnother` trigger (OTHER binding), so it
 * fires once for EACH other permanent sacrificed — sacrificing three permanents at once adds three
 * counters (CR 603.2c), not one — and the "another" binding excludes this creature sacrificing
 * itself.
 */
val PiratePeddlers = card("Pirate Peddlers") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Pirate"
    power = 2
    toughness = 2
    oracleText = "Deathtouch\n" +
        "Whenever you sacrifice another permanent, put a +1/+1 counter on this creature."

    keywords(Keyword.DEATHTOUCH)

    // Whenever you sacrifice another permanent, put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.YouSacrificeAnother(GameObjectFilter.Permanent)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you sacrifice another permanent, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Eduardo Francisco"
        flavorText = "\"We prefer to think of ourselves as 'high-risk traders.'\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c998994-6c3b-4f34-9b31-babba2b17266.jpg?1764120791"
    }
}
