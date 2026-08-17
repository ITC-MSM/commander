package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Courier's Briefcase
 * {1}{G}
 * Artifact — Treasure
 * When this artifact enters, create a 1/1 green and white Citizen creature token.
 * {T}, Sacrifice this artifact: Add one mana of any color.
 * {W}{U}{B}{R}{G}, {T}, Sacrifice this artifact: Draw three cards.
 *
 * The card *is* a Treasure rather than making one, so the Treasure mana ability is spelled out here
 * the way Buried Treasure spells it out — [Costs.Composite] of tap plus sacrifice-self over
 * [Effects.AddAnyColorMana], flagged `manaAbility`. The five-color ability adds the mana atom in
 * front of the same tap/sacrifice pair.
 */
val CouriersBriefcase = card("Courier's Briefcase") {
    manaCost = "{1}{G}"
    colorIdentity = "BGRUW"
    typeLine = "Artifact — Treasure"
    oracleText = "When this artifact enters, create a 1/1 green and white Citizen creature token.\n{T}, Sacrifice this artifact: Add one mana of any color.\n{W}{U}{B}{R}{G}, {T}, Sacrifice this artifact: Draw three cards."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Citizen"),
        )
        description = "When this artifact enters, create a 1/1 green and white Citizen creature token."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{U}{B}{R}{G}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Josu Hernaiz"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f375674-7ae2-4430-b1f3-c26fa5b201d1.jpg?1783923106"
    }
}
