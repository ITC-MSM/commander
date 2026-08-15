package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Illvoi Operative
 * {1}{U}
 * Creature — Jellyfish Rogue
 * Whenever you cast your second spell each turn, put a +1/+1 counter on this creature.
 * 2/1
 */
val IllvoiOperative = card("Illvoi Operative") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Jellyfish Rogue"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast your second spell each turn, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you cast your second spell each turn, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Quintin Gleim"
        flavorText = "The Uthros Combine's first question for any scientific breakthrough is always: \"How can this be used to keep our next breakthrough secret?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0ae9fc7-1802-4806-9996-1f1f458ff6a7.jpg?1752946793"
    }
}
