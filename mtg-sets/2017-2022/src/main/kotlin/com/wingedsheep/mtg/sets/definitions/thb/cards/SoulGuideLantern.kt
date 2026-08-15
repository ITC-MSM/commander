package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soul-Guide Lantern
 * {1}
 * Artifact
 *
 * When this artifact enters, exile target card from a graveyard.
 * {T}, Sacrifice this artifact: Exile each opponent's graveyard.
 * {1}, {T}, Sacrifice this artifact: Draw a card.
 *
 * Canonical printing lives in Theros Beyond Death (the card's earliest real printing);
 * later sets — including Foundations — contribute only a [com.wingedsheep.sdk.model.Printing] row.
 */
val SoulGuideLantern = card("Soul-Guide Lantern") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, exile target card from a graveyard.\n" +
        "{T}, Sacrifice this artifact: Exile each opponent's graveyard.\n" +
        "{1}, {T}, Sacrifice this artifact: Draw a card."

    // When this artifact enters, exile target card from a graveyard.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.CardInGraveyard)
        effect = Effects.Move(t, Zone.EXILE)
    }

    // {T}, Sacrifice this artifact: Exile each opponent's graveyard.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.ExileOpponentsGraveyards()
    }

    // {1}, {T}, Sacrifice this artifact: Draw a card.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Cliff Childs"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c850b94-75c9-4457-8b5e-1193352d6fcb.jpg?1782707504"
    }
}
