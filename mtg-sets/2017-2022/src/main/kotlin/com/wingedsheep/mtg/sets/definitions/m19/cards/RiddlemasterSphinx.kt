package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Riddlemaster Sphinx
 * {4}{U}{U}
 * Creature — Sphinx
 * 5/5
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * When this creature enters, you may return target creature an opponent controls to its owner's hand.
 *
 * The target is mandatory at announcement — the trigger carries a `targetRequirement`, so a creature
 * is chosen when the ability goes on the stack — while the "you may" is only the resolution-time
 * yes/no. `optional = true` lowers to that consent gate.
 */
val RiddlemasterSphinx = card("Riddlemaster Sphinx") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 5
    toughness = 5
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "When this creature enters, you may return target creature an opponent controls to its owner's hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target", TargetCreature(filter = TargetFilter.CreatureOpponentControls))
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "287"
        artist = "Ryan Yee"
        flavorText = "\"Safe passage requires only a simple answer to a simple question, traveler.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/3/037f6792-ab41-4bcd-a0a3-a4af4a801eb7.jpg"
    }
}
