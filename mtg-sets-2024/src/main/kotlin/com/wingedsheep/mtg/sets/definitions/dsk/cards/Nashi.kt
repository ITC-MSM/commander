package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Nashi, Searcher in the Dark
 * {U}{B}
 * Legendary Creature — Rat Ninja Wizard
 * 2/2
 *
 * Menace
 * Whenever Nashi deals combat damage to a player, you mill that many cards. You may put any
 * number of legendary and/or enchantment cards from among them into your hand. If you put no
 * cards into your hand this way, put a +1/+1 counter on Nashi.
 *
 * Mirrors the "mill then recover from among the milled" pipeline (cf. [CacheGrab]): the combat
 * damage amount ([ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT], cf. [GishathSunsAvatar]) drives the
 * mill; the milled cards stay addressable in the `milled` collection after they hit the
 * graveyard, so [SelectFromCollectionEffect] restricts the "from among them" choice to exactly
 * those cards (legendary and/or enchantment). The +1/+1 counter is gated on nothing having been
 * moved to hand — [Conditions.Not] over [Conditions.CollectionContainsMatch] on the moved pile.
 */
val Nashi = card("Nashi, Searcher in the Dark") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Rat Ninja Wizard"
    power = 2
    toughness = 2
    oracleText = "Menace\nWhenever Nashi deals combat damage to a player, you mill that many cards. " +
        "You may put any number of legendary and/or enchantment cards from among them into your hand. " +
        "If you put no cards into your hand this way, put a +1/+1 counter on Nashi."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val damageDealt = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
        effect = Effects.Composite(
            listOf(
                // You mill that many cards.
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = damageDealt, player = Player.You),
                    storeAs = "milled",
                ),
                MoveCollectionEffect(
                    from = "milled",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.You),
                ),
                // You may put any number of legendary and/or enchantment cards from among them into your hand.
                SelectFromCollectionEffect(
                    from = "milled",
                    selection = SelectionMode.ChooseAnyNumber,
                    filter = GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.Or(listOf(CardPredicate.IsLegendary, CardPredicate.IsEnchantment)),
                        ),
                    ),
                    storeSelected = "toHand",
                    showAllCards = true,
                    prompt = "Put any number of legendary and/or enchantment cards into your hand",
                    selectedLabel = "Put in hand",
                    remainderLabel = "Leave in graveyard",
                ),
                MoveCollectionEffect(
                    from = "toHand",
                    destination = CardDestination.ToZone(Zone.HAND, Player.You),
                ),
                // If you put no cards into your hand this way, put a +1/+1 counter on Nashi.
                ConditionalEffect(
                    condition = Conditions.Not(Conditions.CollectionContainsMatch("toHand")),
                    effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "223"
        artist = "Johan Grenier"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fbf9e1e-43f2-499e-844d-22fc10dbad06.jpg?1726286698"
    }
}
