package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AnyPlayerMayPayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Desecration Demon
 * {2}{B}{B}
 * Creature — Demon
 * 6/6
 *
 * Flying
 * At the beginning of each combat, any opponent may sacrifice a creature of their choice.
 * If a player does, tap this creature and put a +1/+1 counter on it.
 *
 * Modeled with [Triggers.EachCombat] (beginning of combat on every player's turn) and an
 * [AnyPlayerMayPayEffect] scoped to [Player.EachOpponent] — only the controller's opponents are
 * offered the sacrifice. The reflexive consequence (tap self, add one +1/+1 counter) fires once
 * if any opponent sacrifices, matching the ruling that Desecration Demon gets at most one counter
 * per combat regardless of how many creatures were sacrificed.
 */
val DesecrationDemon = card("Desecration Demon") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "At the beginning of each combat, any opponent may sacrifice a creature of their choice. " +
        "If a player does, tap this creature and put a +1/+1 counter on it."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EachCombat
        effect = AnyPlayerMayPayEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Creature, count = 1),
            consequence = Effects.Tap(EffectTarget.Self)
                .then(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)),
            eligiblePlayers = Player.EachOpponent
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "63"
        artist = "Jason Chan"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/8242fade-754c-4404-b3fb-f3cccf84b3b6.jpg?1782714261"
    }
}
