package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CompositeStaticAbility
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ReplaceDrawWithEffect
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

private val emptyLibrary = Exists(
    player = Player.You,
    zone = Zone.LIBRARY,
    negate = true,
)

/**
 * Living Conundrum — Murders at Karlov Manor #63
 * {4}{U} · Creature — Elemental · 2/5
 *
 * The empty-library draw replacement replaces each individual impossible draw with a no-op. The
 * second ability is one conditional continuous effect spanning Layer 6 (flying and vigilance) and
 * Layer 7b (base 10/10), so its components are bundled under one identity and one condition.
 */
val LivingConundrum = card("Living Conundrum") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    oracleText = "Hexproof\n" +
        "If you would draw a card while your library has no cards in it, skip that draw instead.\n" +
        "As long as there are no cards in your library, this creature has base power and " +
        "toughness 10/10 and has flying and vigilance."
    power = 2
    toughness = 5

    keywords(Keyword.HEXPROOF)

    replacementEffect(
        ReplaceDrawWithEffect(
            replacementEffect = Effects.Composite(),
            restrictions = listOf(emptyLibrary),
        )
    )

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CompositeStaticAbility(
                listOf(
                    SetBasePowerToughnessStatic(10, 10, GroupFilter.source()),
                    GrantKeyword(Keyword.FLYING, GroupFilter.source()),
                    GrantKeyword(Keyword.VIGILANCE, GroupFilter.source()),
                )
            ),
            condition = emptyLibrary,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/" +
            "97fb11c7-7b7f-4bdb-a022-53e28ebadecc.jpg?1783912908"

        ruling(
            "2024-02-02",
            "If you're instructed to draw more than one card and you have fewer cards in your " +
                "library, you draw each card in your library, then skip the remaining draws.",
        )
        ruling(
            "2024-02-02",
            "If two or more replacement effects would apply to a card-drawing event, the player " +
                "drawing the card chooses the order in which to apply them.",
        )
    }
}
