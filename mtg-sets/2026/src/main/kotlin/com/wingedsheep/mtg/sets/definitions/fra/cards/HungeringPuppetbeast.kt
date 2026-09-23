package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hungering Puppetbeast — "your choice of" is made as the activated ability resolves, after the
 * counter is placed: a non-spell [ModalEffect] nested in the composite is chosen at resolution.
 */
val HungeringPuppetbeast = card("Hungering Puppetbeast") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Beast Construct"
    power = 5
    toughness = 5
    oracleText = "When this creature enters, create a Heartwood token. (It's a red and green artifact with \"{T}: Add {R} or {G}.\")\n{1}, Sacrifice another artifact: Put a +1/+1 counter on this creature. It gains your choice of trample, hexproof, or haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateHeartwood()
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeAnother(GameObjectFilter.Artifact))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            ModalEffect(
                modes = listOf(
                    Mode.noTarget(
                        Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self, Duration.EndOfTurn),
                        "It gains trample until end of turn"
                    ),
                    Mode.noTarget(
                        Effects.GrantKeyword(Keyword.HEXPROOF, EffectTarget.Self, Duration.EndOfTurn),
                        "It gains hexproof until end of turn"
                    ),
                    Mode.noTarget(
                        Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn),
                        "It gains haste until end of turn"
                    ),
                ),
                chooseCount = 1,
                countsAsModalSpell = false
            ),
        )
        description = "Put a +1/+1 counter on this creature. It gains your choice of trample, hexproof, or haste until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "107"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3db2da7a-8088-4117-916b-f9c905d1b45b.jpg?1789127646"
        inBooster = false
    }
}
