package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Eaten Alive
 * {B}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a creature or pay {3}{B}.
 * Exile target creature or planeswalker.
 *
 * The additional cost is the alternative "sacrifice a creature or pay {N}" shape
 * ([Costs.additional.SacrificeOrPay]); with no creature to sacrifice only the pay path is offered.
 */
val EatenAlive = card("Eaten Alive") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or pay {3}{B}.\n" +
        "Exile target creature or planeswalker."

    additionalCost(
        Costs.additional.SacrificeOrPay(
            filter = GameObjectFilter.Creature,
            alternativeManaCost = "{3}{B}",
        )
    )

    spell {
        target = Targets.CreatureOrPlaneswalker
        effect = Effects.Exile(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Nicholas Gregory"
        flavorText = "\"If you hear a groaning noise in the cellar, don't investigate. Don't split up. " +
            "Just lock the door and barricade it.\"\n—Emili, guard captain"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7975a3c-570a-4bff-a60d-d274f758b93f.jpg?1782703667"
    }
}
