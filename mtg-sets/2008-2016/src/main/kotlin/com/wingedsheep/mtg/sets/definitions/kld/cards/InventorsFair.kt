package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Inventors' Fair
 *
 * Legendary Land
 *
 * At the beginning of your upkeep, if you control three or more artifacts, you gain 1 life.
 * {T}: Add {C}.
 * {4}, {T}, Sacrifice Inventors' Fair: Search your library for an artifact card, reveal it, put
 * it into your hand, then shuffle. Activate only if you control three or more artifacts.
 *
 * The upkeep clause is an intervening-"if" (CR 603.4) — checked both on trigger and on
 * resolution — while the tutor's identical wording is an [ActivationRestriction.OnlyIfCondition]
 * checked at activation. Same [Conditions.YouControlAtLeast] count, two different rules positions.
 */
val InventorsFair = card("Inventors' Fair") {
    manaCost = ""
    typeLine = "Legendary Land"
    oracleText = "At the beginning of your upkeep, if you control three or more artifacts, you gain 1 life.\n" +
        "{T}: Add {C}.\n" +
        "{4}, {T}, Sacrifice Inventors' Fair: Search your library for an artifact card, reveal it, put it into your hand, then shuffle. Activate only if you control three or more artifacts."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Artifact,
            reveal = true
        )
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/275471e3-ded1-40ac-91ef-369dce5764d9.jpg?1783937144"
    }
}
