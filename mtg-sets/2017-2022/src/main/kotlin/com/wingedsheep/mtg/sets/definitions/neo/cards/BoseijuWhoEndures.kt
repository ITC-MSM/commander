package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Boseiju, Who Endures — Kamigawa: Neon Dynasty #266 (canonical printing)
 * Legendary Land · Rare
 *
 * {T}: Add {G}.
 * Channel — {1}{G}, Discard this card: Destroy target artifact, enchantment, or nonbasic land an
 * opponent controls. That player may search their library for a land card with a basic land type,
 * put it onto the battlefield, then shuffle. This ability costs {1} less to activate for each
 * legendary creature you control.
 *
 * One of the five NEO channel lands; see [OtawaraSoaringCity] for the shape they share.
 *
 * The consolation fetch is the Assassin's Trophy shape: it belongs to the *destroyed permanent's*
 * controller, not to you, so it runs under [Effects.ForEachPlayer] with [Player.ControllerOf] —
 * that rebinds `Player.You` inside the search to that player, which is what makes
 * [Patterns.Library.searchLibrary] (hard-wired to `Player.You`) read the right library. Two
 * consequences fall out of the ordering rather than needing a gate: an indestructible target
 * still hands its controller the land, and declining the search skips the shuffle with it.
 *
 * "A land card **with a basic land type**" is deliberately not "a basic land card" — a shockland
 * (`Land — Forest Island`) or Dryad Arbor qualifies and a basic is merely the common case. Hence
 * the explicit [CardPredicate.HasBasicLandType] union rather than [GameObjectFilter.BasicLand].
 */
val BoseijuWhoEndures = card("Boseiju, Who Endures") {
    typeLine = "Legendary Land"
    colorIdentity = "G"
    oracleText = "{T}: Add {G}.\n" +
        "Channel — {1}{G}, Discard this card: Destroy target artifact, enchantment, or nonbasic " +
        "land an opponent controls. That player may search their library for a land card with a " +
        "basic land type, put it onto the battlefield, then shuffle. This ability costs {1} less " +
        "to activate for each legendary creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    // Channel — {1}{G}, Discard this card (from hand): Naya-style destroy + consolation fetch.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        genericCostReduction =
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature.legendary()).count()
        val t = target(
            "target artifact, enchantment, or nonbasic land an opponent controls",
            TargetPermanent(
                filter = TargetFilter(
                    (GameObjectFilter.Artifact or
                        GameObjectFilter.Enchantment or
                        GameObjectFilter.NonbasicLand).opponentControls()
                )
            )
        )
        effect = Effects.Destroy(t) then
            Effects.ForEachPlayer(
                Player.ControllerOf("the destroyed permanent"),
                listOf(
                    MayEffect(
                        Patterns.Library.searchLibrary(
                            filter = GameObjectFilter(
                                cardPredicates = listOf(
                                    CardPredicate.IsLand,
                                    CardPredicate.Or(
                                        listOf(
                                            CardPredicate.HasBasicLandType("Plains"),
                                            CardPredicate.HasBasicLandType("Island"),
                                            CardPredicate.HasBasicLandType("Swamp"),
                                            CardPredicate.HasBasicLandType("Mountain"),
                                            CardPredicate.HasBasicLandType("Forest")
                                        )
                                    )
                                )
                            ),
                            count = 1,
                            destination = SearchDestination.BATTLEFIELD
                        )
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Chris Ostrowski"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/2135ac5a-187b-4dc9-8f82-34e8d1603416.jpg?1783923818"
    }
}
