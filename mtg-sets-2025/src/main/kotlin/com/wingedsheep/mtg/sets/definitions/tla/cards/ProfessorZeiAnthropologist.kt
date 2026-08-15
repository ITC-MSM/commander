package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Professor Zei, Anthropologist
 * {U/R}{U/R}
 * Legendary Creature — Human Advisor Ally
 * 0/3
 * {T}, Discard a card: Draw a card.
 * {1}, {T}, Sacrifice Professor Zei: Return target instant or sorcery card from your
 *   graveyard to your hand. Activate only during your turn.
 */
val ProfessorZeiAnthropologist = card("Professor Zei, Anthropologist") {
    manaCost = "{U/R}{U/R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Advisor Ally"
    power = 0
    toughness = 3
    oracleText = "{T}, Discard a card: Draw a card.\n" +
        "{1}, {T}, Sacrifice Professor Zei: Return target instant or sorcery card from your " +
        "graveyard to your hand. Activate only during your turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        target = TargetObject(filter = TargetFilter.InstantOrSorceryInYourGraveyard)
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        description = "{1}, {T}, Sacrifice Professor Zei: Return target instant or sorcery " +
            "card from your graveyard to your hand. Activate only during your turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Pauline Voss"
        flavorText = "\"I'm not leaving. I can't. I've spent too long trying to find this " +
            "place. There's not another collection of knowledge like this on earth.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27baccc0-7e25-4f39-be0d-31cd98ca0dc5.jpg?1764121762"
    }
}
