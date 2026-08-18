package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cave People
 * {1}{R}{R}
 * Creature — Human
 * 1/4
 * Whenever this creature attacks, it gets +1/-2 until end of turn.
 * {1}{R}{R}, {T}: Target creature gains mountainwalk until end of turn.
 *
 * The attack trigger pumps the attacker itself ([EffectTarget.Self]); the activated ability can
 * hand mountainwalk to any creature, not just this one.
 */
val CavePeople = card("Cave People") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human"
    power = 1
    toughness = 4
    oracleText = "Whenever this creature attacks, it gets +1/-2 until end of turn.\n" +
        "{1}{R}{R}, {T}: Target creature gains mountainwalk until end of turn. (It can't be " +
        "blocked as long as defending player controls a Mountain.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(1, -2, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}{R}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.MOUNTAINWALK, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Drew Tucker"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72746a5d-faa1-44b7-97b5-0ef9302a3c13.jpg?1783947936"
    }
}
