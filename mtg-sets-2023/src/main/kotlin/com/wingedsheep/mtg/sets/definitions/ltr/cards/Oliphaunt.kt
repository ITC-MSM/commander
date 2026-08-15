package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Oliphaunt
 * {5}{R}
 * Creature — Elephant
 * 6/4
 *
 * Trample
 * Whenever this creature attacks, another target creature you control gets +2/+0 and gains trample until end of turn.
 * Mountaincycling {1}
 */
val Oliphaunt = card("Oliphaunt") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elephant"
    power = 6
    toughness = 4
    oracleText = "Trample\n" +
        "Whenever this creature attacks, another target creature you control gets +2/+0 and gains trample until end of turn.\n" +
        "Mountaincycling {1} ({1}, Discard this card: Search your library for a Mountain card, reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.Attacks
        val other = target("another target creature you control", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.ModifyStats(2, 0, other)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, other))
    }

    keywordAbility(KeywordAbility.typecycling("Mountain", ManaCost.parse("{1}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "John Di Giovanni"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/6989018c-37b1-4282-a4af-9cc97f160b4d.jpg?1687210982"
    }
}
