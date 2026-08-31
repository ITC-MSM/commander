package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Summon the School
 * {3}{W}
 * Kindred Sorcery — Merfolk
 * Create two 1/1 blue Merfolk Wizard creature tokens.
 * Tap four untapped Merfolk you control: Return this card from your graveyard to your hand.
 *
 * The recursion is an activated ability of a card in the graveyard (`activateFromZone`), and its
 * only cost is the tap — `Costs.TapPermanents` already means "untapped ~ you control", so the
 * Merfolk it taps include the two Wizards this spell just made.
 *
 * Ruling (2024-06-07): the card was printed as "Tribal"; that type is now spelled "Kindred" with no
 * change in function. That matters here — the card is a Merfolk in the graveyard too, though
 * nothing on it counts itself.
 */
val SummonTheSchool = card("Summon the School") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Kindred Sorcery — Merfolk"
    oracleText = "Create two 1/1 blue Merfolk Wizard creature tokens.\n" +
        "Tap four untapped Merfolk you control: Return this card from your graveyard to your hand."

    spell {
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk", "Wizard"),
            imageUri = "https://cards.scryfall.io/normal/front/5/2/526da544-23dd-42b8-8c00-c3609eea4489.jpg?1783942838",
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 4,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK),
        )
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Dave Dorman"
        flavorText = "\"When merrows talk, listeners grow fins.\"\n—Kithkin saying"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13a4c124-216b-44b1-b49a-3db3f033e4cd.jpg?1783942909"
        ruling("2024-06-07", "This cards was originally printed with the \"tribal\" card type. That card type has been replaced with \"kindred\". This change does not affect the gameplay function of this card.")
    }
}
