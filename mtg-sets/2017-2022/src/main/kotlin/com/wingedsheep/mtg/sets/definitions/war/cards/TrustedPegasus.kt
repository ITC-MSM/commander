package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Trusted Pegasus — War of the Spark #36 (canonical printing)
 * {2}{W}
 * Creature — Pegasus
 * 2/2
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * Whenever this creature attacks, target attacking creature without flying gains flying until
 * end of turn.
 *
 * The target filter carries both halves of "attacking creature without flying":
 * [TargetFilter.AttackingCreature] is a *state* predicate read off the battlefield,
 * `withoutKeyword(Keyword.FLYING)` a card predicate. The pegasus itself already flies, so it can
 * never pick itself even though it is one of the attackers.
 */
val TrustedPegasus = card("Trusted Pegasus") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "Whenever this creature attacks, target attacking creature without flying gains flying " +
        "until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val grounded = target(
            "target",
            TargetCreature(filter = TargetFilter.AttackingCreature.withoutKeyword(Keyword.FLYING))
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, grounded)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Chris Rahn"
        flavorText = "\"Would you give your life to save this world?\" Gideon murmured. The pegasus snorted and spread wide its mighty wings."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89bbcaf5-80e9-43f2-b470-de6cbed6a95a.jpg"
    }
}
