package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Squirming Emergence — {1}{B}{G}
 * Sorcery
 * Rare — The Lost Caverns of Ixalan #241
 * Artist: Simon Dominic
 *
 * "Fathomless descent — Return to the battlefield target nonland permanent card in your
 *  graveyard with mana value less than or equal to the number of permanent cards in your
 *  graveyard."
 *
 * The target is a nonland permanent card in your own graveyard whose mana value is capped by a
 * DYNAMIC amount: the number of permanent cards in your graveyard. Modeled as a graveyard
 * [TargetObject] filtered by [CardPredicate.IsNonland] + [CardPredicate.IsPermanent] +
 * [CardPredicate.ManaValueAtMostDynamic] over
 * [DynamicAmount.Count]`(You, GRAVEYARD, Permanent)` — the same "fathomless descent" count Song of
 * Stupefaction reads. The count includes the targeted card itself while it is still in the
 * graveyard (the cap is evaluated at target-selection time, so a lone permanent card with MV ≤ 1
 * is a legal target). The chosen card is returned via [Effects.PutOntoBattlefield] (untapped),
 * the graveyard-to-battlefield reanimation primitive shared with Helping Hand.
 */
val SquirmingEmergence = card("Squirming Emergence") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Fathomless descent — Return to the battlefield target nonland permanent card " +
        "in your graveyard with mana value less than or equal to the number of permanent cards " +
        "in your graveyard."

    spell {
        val t = target(
            "target nonland permanent card in your graveyard with mana value less than or " +
                "equal to the number of permanent cards in your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.IsNonland,
                            CardPredicate.IsPermanent,
                            CardPredicate.ManaValueAtMostDynamic(
                                DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Permanent)
                            )
                        ),
                        controllerPredicate = ControllerPredicate.OwnedByYou
                    ),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefield(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ee16629-f9be-4cdb-bf52-1d640781ee00.jpg?1782694418"
    }
}
