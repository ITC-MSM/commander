package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.station
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Entropic Battlecruiser
 * {3}{B}
 * Artifact — Spacecraft
 * Station (Tap another creature you control: Put charge counters equal to its power on this
 *   Spacecraft. Station only as a sorcery. It's an artifact creature at 8+.)
 * 1+ | Whenever an opponent discards a card, they lose 3 life.
 * 8+ | Flying, deathtouch
 * Whenever this Spacecraft attacks, each opponent discards a card. Each opponent who can't loses 3 life.
 * 3/10
 */
val EntropicBattlecruiser = card("Entropic Battlecruiser") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Spacecraft"
    power = 3
    toughness = 10
    oracleText = "Station (Tap another creature you control: Put charge counters equal to its " +
        "power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 8+.)\n" +
        "1+ | Whenever an opponent discards a card, they lose 3 life.\n" +
        "8+ | Flying, deathtouch\n" +
        "Whenever this Spacecraft attacks, each opponent discards a card. Each opponent who can't loses 3 life."

    station()

    val atLeast1Charge = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 1)
    val atLeast8Charge = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 8)

    triggeredAbility {
        trigger = Triggers.AnyOpponentDiscards
        triggerCondition = atLeast1Charge
        effect = LoseLifeEffect(3, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = ForEachPlayerEffect(
            players = Player.EachOpponent,
            effects = listOf(
                ConditionalEffect(
                    condition = Exists(Player.You, Zone.HAND),
                    effect = Patterns.Hand.discardCards(1, EffectTarget.Controller),
                    elseEffect = LoseLifeEffect(3, EffectTarget.Controller)
                )
            )
        )
    }

    staticAbility {
        condition = atLeast8Charge
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }
    staticAbility {
        condition = atLeast8Charge
        ability = GrantKeyword(Keyword.FLYING.name, GroupFilter.source())
    }
    staticAbility {
        condition = atLeast8Charge
        ability = GrantKeyword(Keyword.DEATHTOUCH.name, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "99"
        artist = "Josiah \"Jo\" Cameron"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc59796b-9025-44b6-a188-cf6684ebffb9.jpg?1755552755"
    }
}
