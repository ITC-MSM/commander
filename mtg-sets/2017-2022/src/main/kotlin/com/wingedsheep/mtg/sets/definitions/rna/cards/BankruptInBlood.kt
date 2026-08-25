package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Bankrupt in Blood — Ravnica Allegiance #62
 * {1}{B} · Sorcery
 *
 * The sacrifice is an *additional cost*, paid on announcement — so the creatures are gone
 * before anyone can respond, and the spell being countered does not give them back.
 */
val BankruptInBlood = card("Bankrupt in Blood") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice two creatures.\n" +
        "Draw three cards."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature, count = 2))

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Seb McKinnon"
        flavorText = "\"Your spirits can rest in peace, for your debts are paid.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e635c433-0398-442a-856e-1869f6bf2cfd.jpg"
    }
}
