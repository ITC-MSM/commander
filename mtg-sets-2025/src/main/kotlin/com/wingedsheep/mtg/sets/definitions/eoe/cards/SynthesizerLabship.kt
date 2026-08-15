package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.station
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Synthesizer Labship
 * {U}
 * Artifact — Spacecraft
 * Station (Tap another creature you control: Put charge counters equal to its power on this
 * Spacecraft. Station only as a sorcery. It's an artifact creature at 9+.)
 * 2+ | At the beginning of combat on your turn, up to one other target artifact you control
 *       becomes an artifact creature with base power and toughness 2/2 and gains flying
 *       until end of turn.
 * 9+ | Flying, vigilance
 * 4/4
 */
val SynthesizerLabship = card("Synthesizer Labship") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Spacecraft"
    power = 4
    toughness = 4
    oracleText = "Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 9+.)\n" +
        "2+ | At the beginning of combat on your turn, up to one other target artifact you control becomes an artifact creature with base power and toughness 2/2 and gains flying until end of turn.\n" +
        "9+ | Flying, vigilance"

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // Charge-counter threshold predicates ({N+} station symbols, CR 721.2a)
    val charge2 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 2)
    val charge9 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 9)

    // 2+ | At the beginning of combat on your turn, up to one other target artifact you control
    // becomes an artifact creature with base P/T 2/2 and gains flying until end of turn.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerCondition = charge2
        val targetArtifact = target(
            "up to one other target artifact you control",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Artifact.youControl(),
                    excludeSelf = true
                )
            )
        )
        effect = Effects.BecomeCreature(
            target = targetArtifact,
            power = 2,
            toughness = 2,
            keywords = setOf(Keyword.FLYING)
        )
    }

    // 9+ | Station reminder text — it's an artifact creature at 9+
    staticAbility {
        condition = charge9
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    // 9+ | Flying
    staticAbility {
        condition = charge9
        ability = GrantKeyword(Keyword.FLYING.name, GroupFilter.source())
    }

    // 9+ | Vigilance
    staticAbility {
        condition = charge9
        ability = GrantKeyword(Keyword.VIGILANCE.name, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "81"
        artist = "Adrián Rodríguez Pérez"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fba6332c-acba-43f9-877c-ca6c5328aae9.jpg?1755341217"
        ruling("2025-07-25", "If the target of Synthesizer Labship's second ability was already a creature, its base power and toughness will become 2/2 until end of turn. Effects that otherwise modify power and toughness, including +1/+1 counters, will still apply.")
        ruling("2025-07-25", "The resulting artifact creature will be able to attack if it's been under your control continuously since the turn began. It doesn't matter how long it's been a creature, just how long it's been on the battlefield.")
        ruling("2025-07-25", "If Synthesizer Labship's second ability causes a Vehicle to become an artifact creature, it doesn't count as 'crewing' that Vehicle for any ability that would trigger due to a Vehicle becoming crewed.")
        ruling("2025-07-25", "If the ability causes a permanent with station to become an artifact creature, putting enough charge counters on it to meet its station threshold won't overwrite the base 2/2 power and toughness set by this ability.")
    }
}
