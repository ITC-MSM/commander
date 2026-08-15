package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantMayCastFromLinkedExile
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate

/**
 * Valgavoth, Terror Eater
 * {6}{B}{B}{B}
 * Legendary Creature — Elder Demon
 * 9/9
 *
 * Flying, lifelink
 * Ward—Sacrifice three nonland permanents.
 * If a card you didn't control would be put into an opponent's graveyard from anywhere,
 * exile it instead.
 * During your turn, you may play cards exiled with Valgavoth. If you cast a spell this way,
 * pay life equal to its mana value rather than pay its mana cost.
 *
 * Modeling notes:
 *  - The graveyard-replacement is the reusable [RedirectZoneChange] with `linkToSource = true`,
 *    so every card it exiles is tethered to Valgavoth's `LinkedExileComponent`. The filter scopes
 *    it to nontoken cards whose owner is an opponent ("an opponent's graveyard") that the source's
 *    controller doesn't control ("a card you didn't control") — a stolen opponent's permanent that
 *    dies under your control is therefore not exiled (CR-faithful via `Not(ControlledByYou)`).
 *  - The play permission is [GrantMayCastFromLinkedExile]: it waives the mana cost
 *    (`withoutPayingManaCost`) and substitutes the alternative life cost
 *    `PayLifeEqualToManaValueOfSpell`, so casting a spell this way pays life equal to its mana
 *    value rather than its mana cost. Lands exiled with Valgavoth are played for free (no spell,
 *    no life). The grant is timed to your turn (`duringYourTurnOnly`).
 */
val ValgavothTerrorEater = card("Valgavoth, Terror Eater") {
    manaCost = "{6}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Elder Demon"
    power = 9
    toughness = 9
    oracleText = "Flying, lifelink\n" +
        "Ward—Sacrifice three nonland permanents.\n" +
        "If a card you didn't control would be put into an opponent's graveyard from anywhere, exile it instead.\n" +
        "During your turn, you may play cards exiled with Valgavoth. If you cast a spell this way, pay life equal to its mana value rather than pay its mana cost."

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    // Ward—Sacrifice three nonland permanents.
    keywordAbility(KeywordAbility.wardSacrifice(GameObjectFilter.NonlandPermanent, count = 3))

    // If a card you didn't control would be put into an opponent's graveyard from anywhere,
    // exile it instead — and link it to Valgavoth so the last ability can play it.
    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            linkToSource = true,
            appliesTo = ZoneChangeEvent(
                to = Zone.GRAVEYARD,
                filter = GameObjectFilter(
                    cardPredicates = listOf(CardPredicate.IsNontoken),
                    controllerPredicate = ControllerPredicate.And(
                        listOf(
                            ControllerPredicate.OwnedByOpponent,
                            ControllerPredicate.Not(ControllerPredicate.ControlledByYou),
                        )
                    ),
                ),
            ),
        )
    )

    // During your turn, you may play cards exiled with Valgavoth, paying life equal to a spell's
    // mana value rather than its mana cost.
    staticAbility {
        ability = GrantMayCastFromLinkedExile(
            filter = GameObjectFilter.Any,
            duringYourTurnOnly = true,
            withoutPayingManaCost = true,
            additionalCost = Costs.additional.PayLifeEqualToManaValueOfSpell,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "120"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/7740ff55-67bb-409e-90f7-2c2c8b8c770a.jpg?1726286296"
    }
}
