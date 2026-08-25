package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Arisen Gorgon
 * {1}{B}{B}
 * Creature — Zombie Gorgon
 * 3/3
 * This creature has deathtouch as long as you control a Liliana planeswalker. (Any amount of damage this deals to a creature is enough to destroy it.)
 *
 * The keyword is not printed on the card, so it is a [ConditionalStaticAbility] over
 * `GroupFilter.source()` rather than a `keywords(...)` line: the grant appears and disappears with
 * the Liliana, recomputed at projection instead of latched at any point in time.
 *
 * "A Liliana planeswalker" is the planeswalker filter narrowed by the planeswalker *subtype*
 * (CR 205.3j) — the same shape Court Cleric and Tezzeret's Strider use for their own walkers.
 * `Conditions.YouControl` is the battlefield-existence check; the subtype match is case-exact.
 */
val ArisenGorgon = card("Arisen Gorgon") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Gorgon"
    power = 3
    toughness = 3
    oracleText = "This creature has deathtouch as long as you control a Liliana planeswalker. (Any amount of damage this deals to a creature is enough to destroy it.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, GroupFilter.source()),
            condition = Conditions.YouControl(GameObjectFilter.Planeswalker.withSubtype("Liliana"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "292"
        artist = "Livia Prima"
        flavorText = "\"I'm not picky about what comes up, but the less decay, the better.\"\n" +
            "—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e39502fd-193c-4526-8dd8-0cd8c822552c.jpg"
    }
}
