package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Everything Pizza
 * {2}
 * Artifact — Food
 *
 * When this artifact enters, search your library for a basic land card, reveal it,
 * put it into your hand, then shuffle.
 * {2}{W}{U}{B}{R}{G}, {T}, Sacrifice this artifact: Target player gains 3 life and
 * draws a card. Each of your opponents discards a card. This artifact deals 3 damage
 * to any target. Put three +1/+1 counters on up to one target creature.
 */
val EverythingPizza = card("Everything Pizza") {
    manaCost = "{2}"
    typeLine = "Artifact — Food"
    oracleText = "When this artifact enters, search your library for a basic land card, reveal it, put it into your hand, then shuffle.\n{2}{W}{U}{B}{R}{G}, {T}, Sacrifice this artifact: Target player gains 3 life and draws a card. Each of your opponents discards a card. This artifact deals 3 damage to any target. Put three +1/+1 counters on up to one target creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
        description = "When this artifact enters, search your library for a basic land card, reveal it, put it into your hand, then shuffle."
    }

    activatedAbility {
        val player = target("target player", Targets.Player)
        val anyTarget = target("any target", Targets.Any)
        val creature = target("up to one target creature", TargetCreature(optional = true))
        cost = Costs.Composite(
            Costs.Mana("{2}{W}{U}{B}{R}{G}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.Composite(
            Effects.GainLife(3, player),
            Effects.DrawCards(1, player),
            Effects.EachOpponentDiscards(1),
            Effects.DealDamage(3, anyTarget, damageSource = EffectTarget.Self),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, creature)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "James Bousema"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df2cdaa5-9ea0-4aa5-89d3-9edf40fa2a39.jpg?1771476702"
    }
}
