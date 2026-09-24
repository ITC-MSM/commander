package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.EmitSurveiledEventEffect
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Chandra, Chill of Compliance
 * {1}{U}{U}
 * Legendary Planeswalker — Chandra
 * Starting Loyalty: 3
 *
 * - The first +1 is the surveil pipeline written out so the "put into your graveyard this way"
 *   collection can be read afterwards: the graveyard move records what actually arrived
 *   (`storeMovedAs`), that is filtered to noncreature, nonland cards, and the survivor moves on to
 *   the hand. `EmitSurveiledEventEffect` still fires, so "whenever you surveil" triggers see it.
 * - The second +1 is a loyalty ability, not a mana ability (loyalty abilities never are), so it
 *   uses the stack; its {U} carries the negated creature card-type restriction The Emperor of
 *   Palamecia uses for "spend this mana only to cast a noncreature spell".
 * - −X is Stall Out's tap-then-stun composite with the count read from the chosen X.
 * - −6 is a permanent emblem carrying a "whenever you cast a spell" trigger.
 */
val ChandraChillOfCompliance = card("Chandra, Chill of Compliance") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Chandra"
    startingLoyalty = 3
    oracleText = "+1: Surveil 1. If you put a noncreature, nonland card into your graveyard this " +
        "way, put that card into your hand.\n" +
        "+1: Add {U}. Spend this mana only to cast a noncreature spell.\n" +
        "−X: Tap target artifact or creature. Put X stun counters on it.\n" +
        "−6: You get an emblem with \"Whenever you cast a spell, draw a card.\""

    // +1: Surveil 1. If you put a noncreature, nonland card into your graveyard this way, put
    //     that card into your hand.
    loyaltyAbility(+1) {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                    storeAs = "surveiled",
                ),
                SelectFromCollectionEffect(
                    from = "surveiled",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "toGraveyard",
                    storeRemainder = "toTop",
                    selectedLabel = "Put in graveyard",
                    remainderLabel = "Put on top",
                ),
                MoveCollectionEffect(
                    from = "toGraveyard",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    storeMovedAs = "milled",
                ),
                MoveCollectionEffect(
                    from = "toTop",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top),
                    order = CardOrder.ControllerChooses,
                ),
                EmitSurveiledEventEffect(),
                FilterCollectionEffect(
                    from = "milled",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Noncreature.withCardPredicate(CardPredicate.IsNonland)),
                    storeMatching = "returned",
                ),
                MoveCollectionEffect(
                    from = "returned",
                    destination = CardDestination.ToZone(Zone.HAND),
                ),
            )
        )
        description = "Surveil 1. If you put a noncreature, nonland card into your graveyard this " +
            "way, put that card into your hand."
    }

    // +1: Add {U}. Spend this mana only to cast a noncreature spell.
    loyaltyAbility(+1) {
        effect = Effects.AddMana(
            Color.BLUE,
            restriction = ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                cardType = CardType.CREATURE,
                negated = true,
            ),
        )
        description = "Add {U}. Spend this mana only to cast a noncreature spell."
    }

    // −X: Tap target artifact or creature. Put X stun counters on it.
    loyaltyAbilityX {
        val t = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrArtifact)),
        )
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddDynamicCounters(Counters.STUN, DynamicAmount.XValue, t),
        )
        description = "Tap target artifact or creature. Put X stun counters on it."
    }

    // −6: You get an emblem with "Whenever you cast a spell, draw a card."
    loyaltyAbility(-6) {
        effect = Effects.CreateGlobalTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastSpell.event,
                binding = Triggers.YouCastSpell.binding,
                effect = Effects.DrawCards(1),
                descriptionOverride = "Whenever you cast a spell, draw a card.",
            ),
            descriptionOverride = "Whenever you cast a spell, draw a card.",
        )
        description = "You get an emblem with \"Whenever you cast a spell, draw a card.\""
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "212"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/240f58ab-944c-4f4c-9df9-5f40b132bf3e.jpg?1788329242"
        inBooster = false
    }
}
