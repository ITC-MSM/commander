package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vivien Reid
 * {3}{G}{G}
 * Legendary Planeswalker — Vivien
 * Starting Loyalty: 5
 *
 * +1: Look at the top four cards of your library. You may reveal a creature or land card
 *     from among them and put it into your hand. Put the rest on the bottom of your library
 *     in a random order.
 * −3: Destroy target artifact, enchantment, or creature with flying.
 * −8: You get an emblem with "Creatures you control get +2/+2 and have vigilance, trample,
 *     and indestructible."
 */
val VivienReid = card("Vivien Reid") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Planeswalker — Vivien"
    startingLoyalty = 5
    oracleText = "+1: Look at the top four cards of your library. You may reveal a creature or land card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.\n−3: Destroy target artifact, enchantment, or creature with flying.\n−8: You get an emblem with \"Creatures you control get +2/+2 and have vigilance, trample, and indestructible.\""

    // +1: Look at the top four cards of your library. You may reveal a creature or land card
    // from among them and put it into your hand. Put the rest on the bottom of your library in
    // a random order. (Star Charter shape — the reveal is optional, filtered, and the remainder
    // defaults to the bottom in a random order.)
    loyaltyAbility(+1) {
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.CreatureOrLand,
            prompt = "You may reveal a creature or land card to put into your hand"
        )
    }

    // −3: Destroy target artifact, enchantment, or creature with flying. A single battlefield
    // target whose legal set is the union of "artifact", "enchantment", and "creature with
    // flying" (the flying clause is projection-evaluated, so creatures that gain/lose flying are
    // included/excluded correctly).
    loyaltyAbility(-3) {
        val t = target(
            "permanent",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.ArtifactOrEnchantment or
                        GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
                )
            )
        )
        effect = Effects.Destroy(t)
    }

    // −8: You get an emblem with "Creatures you control get +2/+2 and have vigilance, trample,
    // and indestructible."
    loyaltyAbility(-8) {
        effect = Effects.CreatePermanentEmblem(
            groupFilter = GroupFilter.AllCreaturesYouControl,
            powerBonus = 2,
            toughnessBonus = 2,
            grantedKeywords = listOf(
                Keyword.VIGILANCE.name,
                Keyword.TRAMPLE.name,
                Keyword.INDESTRUCTIBLE.name
            ),
            emblemDescription = "Creatures you control get +2/+2 and have vigilance, trample, and indestructible."
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "208"
        artist = "Anna Steinbauer"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/681fbd66-b622-4f20-a860-f101aff21109.jpg?1562302655"
    }
}
