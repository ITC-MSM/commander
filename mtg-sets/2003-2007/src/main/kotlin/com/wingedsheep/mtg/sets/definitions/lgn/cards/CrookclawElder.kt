package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Crookclaw Elder
 * {5}{U}
 * Creature — Bird Wizard
 * 3/2
 * Flying
 * Tap two untapped Birds you control: Draw a card.
 * Tap two untapped Wizards you control: Target creature gains flying until end of turn.
 */
val CrookclawElder = card("Crookclaw Elder") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Wizard"
    power = 3
    toughness = 2
    oracleText = "Flying\nTap two untapped Birds you control: Draw a card.\nTap two untapped Wizards you control: Target creature gains flying until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.TapPermanents(2, GameObjectFilter.Creature.withSubtype("Bird"))
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.TapPermanents(2, GameObjectFilter.Creature.withSubtype("Wizard"))
        val creature = target("creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8ced7275-3935-4bba-877d-81282bd171fd.jpg?1562923490"
    }
}
