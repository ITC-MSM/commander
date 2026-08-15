package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Denethor, Ruling Steward
 * {1}{W}{B}
 * Legendary Creature — Human Noble
 * 2/4
 *
 * At the beginning of your end step, if a creature died under your control this turn,
 * create a 1/1 white Human Soldier creature token.
 * {2}, Sacrifice another creature: Each opponent loses 1 life and you gain 1 life.
 */
val DenethorRulingSteward = card("Denethor, Ruling Steward") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Legendary Creature — Human Noble"
    power = 2
    toughness = 4
    oracleText = "At the beginning of your end step, if a creature died under your control this turn, create a 1/1 white Human Soldier creature token.\n{2}, Sacrifice another creature: Each opponent loses 1 life and you gain 1 life."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        // "if a creature died under your control this turn" — scoped to Denethor's controller,
        // not any player (CreatureDiedThisTurn would wrongly fire on an opponent's creature dying).
        triggerCondition = Conditions.ControlledCreatureDiedThisTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)) then
            Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Campbell White"
        flavorText = "\"Much must be risked in war.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa8654cf-431c-427b-b78f-0e48f6007e9e.jpg?1686969711"
    }
}
