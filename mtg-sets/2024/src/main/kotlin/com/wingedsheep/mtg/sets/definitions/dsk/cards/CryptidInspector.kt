package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cryptid Inspector
 * {2}{G}
 * Creature — Elf Warrior
 * 2/3
 * Vigilance
 * Whenever a face-down permanent you control enters and whenever this creature or another
 * permanent you control is turned face up, put a +1/+1 counter on this creature.
 *
 * The "X and whenever Y" templating is two distinct triggered abilities sharing one payoff
 * (CR has no "or" trigger combiner), so it's modelled as two `triggeredAbility` blocks, both
 * putting a +1/+1 counter on this creature ([EffectTarget.Self]):
 *  - a face-down permanent you control entering ([Triggers.entersBattlefield] filtered to
 *    face-down permanents you control); and
 *  - any permanent you control (including this creature) being turned face up
 *    ([Triggers.CreatureTurnedFaceUp] — in DSK every face-up turn is a manifested creature).
 */
val CryptidInspector = card("Cryptid Inspector") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 2
    toughness = 3
    oracleText = "Vigilance\nWhenever a face-down permanent you control enters and whenever this " +
        "creature or another permanent you control is turned face up, put a +1/+1 counter on " +
        "this creature."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.faceDown().youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    triggeredAbility {
        trigger = Triggers.CreatureTurnedFaceUp()
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Kim Sokol"
        flavorText = "\"Please be dead, please be dead, please be dead.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/820c2932-2e23-4f67-92d0-a630aec6f1b4.jpg?1726286509"
    }
}
