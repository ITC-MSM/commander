package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brackish Trudge — Strixhaven: School of Mages #65 (canonical printing)
 * {2}{B} · Creature — Fungus Beast · 4/2
 *
 * This creature enters tapped.
 * {1}{B}: Return this card from your graveyard to your hand. Activate only if you gained life this turn.
 *
 * An unconditional [EntersTapped] replacement, plus an activated ability that functions from the
 * graveyard (`activateFromZone = Zone.GRAVEYARD`). The return is
 * [Effects.ReturnToHandFromGraveyard] — a `MoveToZone` to hand that carries the graveyard guard,
 * so a Trudge exiled from the graveyard in response does not come back from exile. "Activate only
 * if you gained life this turn" is an [ActivationRestriction.OnlyIfCondition] over
 * [Conditions.YouGainedLifeThisTurn], the `LIFE_GAINED` turn tracker read at 1 or more.
 */
val BrackishTrudge = card("Brackish Trudge") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus Beast"
    oracleText =
        "This creature enters tapped.\n" +
        "{1}{B}: Return this card from your graveyard to your hand. Activate only if you gained life this turn."
    power = 4
    toughness = 2

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(Conditions.YouGainedLifeThisTurn)
        )
        description = "{1}{B}: Return this card from your graveyard to your hand. Activate only if " +
            "you gained life this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Tomasz Jedruszek"
        flavorText = "Most trudges are covered with decaying plant matter, making them ideal breeding grounds for fungal spores."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90ba37ee-159f-421f-8d37-a7b5f1b562f0.jpg?1783927369"
    }
}
