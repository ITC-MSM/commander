package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lesser Masticore
 * {2}
 * Artifact Creature — Masticore
 * 2/2
 * As an additional cost to cast this spell, discard a card.
 * {4}: This creature deals 1 damage to target creature.
 * Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)
 *
 * Persist is engine-live: [Keyword.PERSIST] is read by the death-trigger detector, so the keyword
 * alone carries the reminder text's behaviour and no triggered ability is authored for it.
 */
val LesserMasticore = card("Lesser Masticore") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Masticore"
    power = 2
    toughness = 2
    oracleText = "As an additional cost to cast this spell, discard a card.\n" +
        "{4}: This creature deals 1 damage to target creature.\n" +
        "Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)"

    keywords(Keyword.PERSIST)

    additionalCost(Costs.additional.DiscardCards(1))

    activatedAbility {
        cost = Costs.Mana("{4}")
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(1, t)
        description = "{4}: This creature deals 1 damage to target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Wisnu Tan"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4c7cba5-6111-40ce-828a-e811301bb283.jpg?1783933074"
    }
}
