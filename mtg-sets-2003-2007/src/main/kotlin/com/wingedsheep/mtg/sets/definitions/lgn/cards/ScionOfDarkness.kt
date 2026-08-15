package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Scion of Darkness
 * {5}{B}{B}{B}
 * Creature — Avatar
 * 6/6
 * Trample
 * Whenever Scion of Darkness deals combat damage to a player, you may put target
 * creature card from that player's graveyard onto the battlefield under your control.
 * Cycling {3}
 */
val ScionOfDarkness = card("Scion of Darkness") {
    manaCost = "{5}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Avatar"
    power = 6
    toughness = 6
    oracleText = "Trample\nWhenever Scion of Darkness deals combat damage to a player, you may put target creature card from that player's graveyard onto the battlefield under your control.\nCycling {3}"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val t = target("target", TargetObject(filter = TargetFilter.CreatureInGraveyard.ownedByOpponent()))
        effect = MayEffect(Effects.PutOntoBattlefieldUnderYourControl(t))
    }

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "79"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/497c2629-1263-48a4-9c31-7f052808b2b8.jpg?1562909883"
        ruling("2004-10-04", "If this card destroys a creature in combat and at the same time (using its Trample ability) it damages a player, you will be able to target the destroyed creature (if it was a card and not a token) to be brought back.")
    }
}
