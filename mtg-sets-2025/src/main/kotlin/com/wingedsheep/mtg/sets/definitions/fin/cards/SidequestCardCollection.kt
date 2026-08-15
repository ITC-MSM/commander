package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sidequest: Card Collection // Magicked Card — Final Fantasy #73
 * {3}{U} · Enchantment // Artifact — Vehicle · 4/4
 *
 * Front — Sidequest: Card Collection:
 *   When this enchantment enters, draw three cards, then discard two cards.
 *   At the beginning of your end step, if eight or more cards are in your graveyard,
 *   transform this enchantment.
 *
 * Back — Magicked Card:
 *   Flying
 *   Crew 1
 *
 * The end-step transform is an intervening-"if" (checked when the trigger would go on the
 * stack and again on resolution) over the card count of your graveyard.
 */
private val MagickedCard = card("Magicked Card") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Artifact — Vehicle"
    oracleText = "Flying\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Jurijus Chitrovas"
        imageUri = "https://cards.scryfall.io/normal/back/8/a/8ac3d2c9-5978-4cfb-a746-c901decff093.jpg?1782686540"
    }
}

private val SidequestCardCollectionFront = card("Sidequest: Card Collection") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, draw three cards, then discard two cards.\n" +
        "At the beginning of your end step, if eight or more cards are in your graveyard, transform this enchantment."

    // When this enchantment enters, draw three cards, then discard two cards.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.Discard(2),
        )
    }

    // At the beginning of your end step, if eight or more cards are in your graveyard,
    // transform this enchantment.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.CardsInGraveyardAtLeast(8)
        effect = TransformEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Erikas Perl"
        flavorText = "The glory of the tourney awaits!"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ac3d2c9-5978-4cfb-a746-c901decff093.jpg?1782686540"

        ruling(
            "2025-06-06",
            "Sidequest: Card Collection's last ability checks at the moment it would trigger to " +
                "see if you have eight or more cards in your graveyard. If you don't, the ability " +
                "won't trigger at all. If it does trigger, the ability will check again as it " +
                "tries to resolve."
        )
    }
}

val SidequestCardCollection: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = SidequestCardCollectionFront,
    backFace = MagickedCard,
)
