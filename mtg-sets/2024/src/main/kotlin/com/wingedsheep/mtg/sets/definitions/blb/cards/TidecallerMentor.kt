package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Tidecaller Mentor {1}{U}{B}
 * Creature — Rat Wizard
 * 3/3
 *
 * Menace
 * Threshold — When this creature enters, if there are seven or more cards in your
 * graveyard, return up to one target nonland permanent to its owner's hand.
 */
val TidecallerMentor = card("Tidecaller Mentor") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Rat Wizard"
    power = 3
    toughness = 3
    oracleText = "Menace\nThreshold — When this creature enters, if there are seven or more cards in your graveyard, return up to one target nonland permanent to its owner's hand."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.CardsInGraveyardAtLeast(7)
        val permanent = target(
            "up to one target nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent)
        )
        effect = Effects.ReturnToHand(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Irina Nordsol"
        flavorText = "\"The same tides that bring us knowledge can sweep us away.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa10ffac-7cc2-41ef-b8a0-9431923c0542.jpg?1721427215"
    }
}
