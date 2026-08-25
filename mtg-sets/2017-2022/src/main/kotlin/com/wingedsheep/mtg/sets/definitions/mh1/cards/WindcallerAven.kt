package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Windcaller Aven — Modern Horizons #77
 * {4}{U}{U} · Creature — Bird Wizard · 4/3
 *
 * Flying
 * Cycling {U}
 * When you cycle this card, target creature gains flying until end of turn.
 *
 * The cycling trigger (CR 702.29b) goes on the stack from the discard and resolves from the
 * graveyard, so the Aven grants flying to something else without ever having been on the
 * battlefield — a one-blue-mana evasion trick that also replaces itself.
 *
 * `Effects.GrantKeyword` defaults to [com.wingedsheep.sdk.scripting.Duration.EndOfTurn], which is
 * exactly "until end of turn"; the grant is a Layer 6 continuous effect the projection applies, so
 * a creature that gains flying this way is seen as flying by every blocker check.
 */
val WindcallerAven = card("Windcaller Aven") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Wizard"
    power = 4
    toughness = 3
    oracleText = "Flying\n" +
        "Cycling {U} ({U}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, target creature gains flying until end of turn."

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.cycling("{U}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Uriah Voth"
        flavorText = "Every cry lifts her allies toward the thunder."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88d26568-be8f-4a04-82b6-4c37b55d4cfd.jpg?1783933133"
    }
}
