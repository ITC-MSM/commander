package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetOther
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Council of Echoes — {4}{U}{U}
 * Creature — Spirit Advisor
 * 4/4
 *
 * Flying
 * Descend 4 — When this creature enters, if there are four or more permanent cards in your
 * graveyard, return up to one target nonland permanent other than this creature to its owner's
 * hand.
 *
 * "Descend 4" is an ability word; the mechanic is an intervening-if triggered ability whose
 * condition (CR 603.4) checks that the controller has four or more permanent cards in their
 * graveyard at both the time the trigger would fire and at resolution. If the count drops
 * below four before resolution the ability fizzles.
 *
 * "Up to one" makes the single target requirement optional (minimum 0). If the controller
 * declines to choose any target the effect resolves as a no-op. The target must be a nonland
 * permanent other than the Council itself ([TargetOther] excludes the source).
 */
val CouncilOfEchoes = card("Council of Echoes") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit Advisor"
    power = 4
    toughness = 4
    oracleText = "Flying\nDescend 4 — When this creature enters, if there are four or more permanent cards in your graveyard, return up to one target nonland permanent other than this creature to its owner's hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        val permanent = target(
            "up to one target nonland permanent other than this creature",
            TargetOther(baseRequirement = TargetPermanent(count = 1, optional = true, filter = TargetFilter.NonlandPermanent))
        )
        effect = Effects.ReturnToHand(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Fariba Khamseh"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85ad358d-a520-4d57-82b0-d2297da1fbde.jpg?1782694568"
    }
}
