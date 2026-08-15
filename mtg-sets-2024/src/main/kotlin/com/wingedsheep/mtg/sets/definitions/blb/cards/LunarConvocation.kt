package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lunar Convocation
 * {W}{B}
 * Enchantment
 *
 * At the beginning of your end step, if you gained life this turn, each opponent loses 1 life.
 * At the beginning of your end step, if you gained and lost life this turn,
 * create a 1/1 black Bat creature token with flying.
 * {1}{B}, Pay 2 life: Draw a card.
 */
val LunarConvocation = card("Lunar Convocation") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your end step, if you gained life this turn, each opponent loses 1 life.\n" +
        "At the beginning of your end step, if you gained and lost life this turn, " +
        "create a 1/1 black Bat creature token with flying.\n" +
        "{1}{B}, Pay 2 life: Draw a card."

    // At the beginning of your end step, if you gained life this turn, each opponent loses 1 life.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouGainedLifeThisTurn
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    // At the beginning of your end step, if you gained and lost life this turn,
    // create a 1/1 black Bat creature token with flying.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouGainedAndLostLifeThisTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Bat"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/1/0/100c0127-49dd-4a78-9c88-1881e7923674.jpg?1721425184"
        )
    }

    // {1}{B}, Pay 2 life: Draw a card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.PayLife(2))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "223"
        artist = "Pavel Kolomeyets"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9ee50d4-c878-457b-964d-29c039ce9852.jpg?1721427132"
    }
}
