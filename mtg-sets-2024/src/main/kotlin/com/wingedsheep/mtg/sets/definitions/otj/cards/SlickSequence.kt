package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Slick Sequence
 * {U}{R}
 * Instant
 *
 * Slick Sequence deals 2 damage to any target. If you've cast another spell this turn, draw a card.
 *
 * "Another spell" is evaluated at resolution, when this spell is already recorded in the turn's
 * cast history, so `YouCastSpellsThisTurn(atLeast = 2)` means "this spell plus at least one other".
 */
val SlickSequence = card("Slick Sequence") {
    manaCost = "{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Slick Sequence deals 2 damage to any target. If you've cast another spell this turn, draw a card."

    spell {
        val any = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, any) then
            ConditionalEffect(
                condition = Conditions.YouCastSpellsThisTurn(atLeast = 2),
                effect = Effects.DrawCards(1),
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Fajareka Setiawan"
        flavorText = "\"Shooting first don't mean much if you ain't got proper follow-through.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/e/beb1c974-0d35-4e9f-a310-44eb2af64494.jpg?1712356219"
    }
}
