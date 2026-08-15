package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Energy Flux
 * {2}{U}
 * Enchantment
 * All artifacts have "At the beginning of your upkeep, sacrifice this artifact unless you pay {2}."
 *
 * The artifact-flavored sibling of Vile Consumption: a [GrantTriggeredAbility] static over
 * [GroupFilter.AllArtifacts] gives every artifact on the battlefield — including opponents' — its
 * own beginning-of-upkeep trigger. Because the granted trigger uses [Triggers.YourUpkeep], the
 * engine resolves "your upkeep" against the *affected artifact's controller* (TriggerMatcher keys
 * the step off each granted permanent's controller, not Energy Flux's controller), so each artifact
 * is taxed on its own controller's upkeep. [PayOrSufferEffect] then lets that controller
 * (EffectTarget.Controller, the default) pay {2}; if they decline, the granted artifact itself
 * (EffectTarget.Self) is sacrificed.
 */
val EnergyFlux = card("Energy Flux") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "All artifacts have \"At the beginning of your upkeep, sacrifice this artifact unless you pay {2}.\""

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YourUpkeep.event,
                binding = Triggers.YourUpkeep.binding,
                effect = PayOrSufferEffect(
                    cost = Costs.pay.Mana("{2}"),
                    suffer = Effects.SacrificeTarget(EffectTarget.Self)
                )
            ),
            filter = GroupFilter.AllArtifacts
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Kaja Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd1f624b-e8f2-462f-838a-7cb9e8fda988.jpg?1562934930"
    }
}
