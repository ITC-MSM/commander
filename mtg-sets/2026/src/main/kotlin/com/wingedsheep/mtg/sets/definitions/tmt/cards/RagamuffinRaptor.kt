package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ragamuffin Raptor
 * {4}{G}
 * Creature — Dinosaur
 * 4/3
 *
 * When this creature enters, return up to one target creature or Food
 * card from your graveyard to your hand.
 */
val RagamuffinRaptor = card("Ragamuffin Raptor") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "When this creature enters, return up to one target creature or Food card from your graveyard to your hand."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "creature or Food card from your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.Or(
                                listOf(
                                    CardPredicate.IsCreature,
                                    CardPredicate.HasSubtype(Subtype("Food")),
                                )
                            )
                        )
                    ).ownedByYou(),
                    zone = Zone.GRAVEYARD,
                )
            )
        )
        effect = Effects.ReturnToHand(card)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Inkognit"
        flavorText = "Bebop and Rocksteady's new Cretaceous stowaway quickly developed a taste for leftovers . . . and ears."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2edc1d40-3154-4ec0-88c7-faf35fd5e560.jpg?1772578991"
    }
}
