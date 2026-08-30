package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantProtection
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hunted Horror — Ravnica: City of Guilds #90
 * {B}{B} · Creature — Horror · 7/7
 *
 * Trample
 * When this creature enters, target opponent creates two 3/3 green Centaur creature tokens with
 * protection from black.
 *
 * The black member of the Hunted cycle, and the most extreme trade in it: a two-mana 7/7 trampler
 * against two 3/3s the Horror itself can never fight through. Protection from black (CR 702.16) is
 * exactly what makes the Centaurs a real answer rather than chump blockers — the Horror can't be
 * blocked *by* them profitably because they can block it and take no damage from it, and it can't
 * be targeted or enchanted by the black removal that would otherwise clear them.
 *
 * The Centaurs' protection is a [GrantProtection] on the token's own `staticAbilities`, scoped to
 * [GroupFilter.source] so it applies to the token itself rather than to something it's attached to.
 * That projects `PROTECTION_FROM_BLACK` onto the token, which is the single keyword every
 * protection leg in the engine reads — combat damage, block legality, targeting, and aura
 * attachment all consult it. (A creature *card* with printed protection uses
 * `keywordAbility(KeywordAbility.Protection(...))` instead, but a token has no printed keyword
 * abilities, so the static-ability route is the one available and the one that projects.)
 *
 * As with the rest of the cycle the tokens enter under the *targeted opponent's* control via
 * [Effects.CreateToken]'s `controller`; if the opponent is an illegal target on resolution the
 * trigger is removed from the stack and no Centaurs appear.
 */
val HuntedHorror = card("Hunted Horror") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    oracleText = "Trample\n" +
        "When this creature enters, target opponent creates two 3/3 green Centaur creature " +
        "tokens with protection from black."
    power = 7
    toughness = 7

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Centaur"),
            count = 2,
            controller = opponent,
            staticAbilities = listOf(GrantProtection(Color.BLACK, GroupFilter.source())),
        )
        description = "When this creature enters, target opponent creates two 3/3 green Centaur " +
            "creature tokens with protection from black."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "90"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ca680e3-2d10-4a32-8b0c-8b0c6be96540.jpg?1783943667"
    }
}
