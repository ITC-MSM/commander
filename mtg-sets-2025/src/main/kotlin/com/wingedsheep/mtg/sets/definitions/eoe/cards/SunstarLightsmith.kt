package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sunstar Lightsmith
 * {3}{W}
 * Creature — Human Artificer
 * 3/3
 *
 * Whenever you cast your second spell each turn, put a +1/+1 counter on this creature and draw a card.
 */
val SunstarLightsmith = card("Sunstar Lightsmith") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Artificer"
    oracleText = "Whenever you cast your second spell each turn, put a +1/+1 counter on this creature and draw a card."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self).then(
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Jarel Threat"
        flavorText = "She forges blades of incandescence and quenches them in pitch darkness."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f60b09d-9814-4a36-a57d-59b0e04c1c2f.jpg?1752946716"
    }
}
