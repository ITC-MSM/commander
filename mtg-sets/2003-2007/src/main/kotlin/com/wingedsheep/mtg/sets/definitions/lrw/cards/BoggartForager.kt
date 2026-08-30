package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect

/**
 * Boggart Forager
 * {R}
 * Creature — Goblin Rogue
 * 1/1
 * {R}, Sacrifice this creature: Target player shuffles their library.
 *
 * `ShuffleLibraryEffect` defaults to the controller's library, so the target handle has to be
 * passed explicitly — otherwise the Forager would shuffle its own controller's library no matter
 * who was targeted.
 */
val BoggartForager = card("Boggart Forager") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "{R}, Sacrifice this creature: Target player shuffles their library."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}"),
            Costs.SacrificeSelf
        )
        val player = target("target player", Targets.Player)
        effect = ShuffleLibraryEffect(target = player)
        description = "{R}, Sacrifice this creature: Target player shuffles their library."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Ron Spencer"
        flavorText = "\"Reach in this hole, lose a hand. Reach in that hole, find a sparkly.\"\n—Auntie wisdom"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbc26374-d8de-4740-b1ec-ac92cfedbebc.jpg?1783942881"
    }
}
