package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

val ProftSinisterMastermind = card("Proft, Sinister Mastermind") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Rogue"
    power = 5
    toughness = 5
    oracleText = "Threshold — You can't cast this spell unless there are seven or more cards in your graveyard.\n" +
        "Menace\n" +
        "{B}, Discard this card: Target creature gets -3/-1 until end of turn."

    spell {
        castOnlyIf(Conditions.CardsInGraveyardAtLeast(7))
    }

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-3, -1, creature, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Lindsey Look"
        flavorText = "If the crime is violent, it's the Rakdos. If it's covert, it's the Dimir. If it's intricate, it's him."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d36b0e06-cb82-4c48-bf35-e76f109116f6.jpg?1789127196"
        inBooster = false
    }
}
