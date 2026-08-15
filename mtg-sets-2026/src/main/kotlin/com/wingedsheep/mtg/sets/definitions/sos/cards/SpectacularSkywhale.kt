package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spectacular Skywhale
 * {2}{U}{R}
 * Creature — Elemental Whale
 * 1/4
 * Flying
 * Opus — Whenever you cast an instant or sorcery spell, this creature gets +3/+0 until end of
 * turn. If five or more mana was spent to cast that spell, put three +1/+1 counters on this
 * creature instead.
 *
 * "Opus" is an ability word (flavor only). The `opus { }` builder wires the spell-cast trigger
 * and the 5+ mana tier. The counters *replace* the +3/+0 buff ("instead"), so they're
 * `insteadIfFiveOrMore` (see Colorstorm Stallion for the `alsoIfFiveOrMore` sibling shape).
 */
val SpectacularSkywhale = card("Spectacular Skywhale") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Elemental Whale"
    power = 1
    toughness = 4
    oracleText = "Flying\nOpus — Whenever you cast an instant or sorcery spell, this creature gets " +
        "+3/+0 until end of turn. If five or more mana was spent to cast that spell, put three " +
        "+1/+1 counters on this creature instead."

    keywords(Keyword.FLYING)

    opus {
        effect = Effects.ModifyStats(3, 0, EffectTarget.Self)
        insteadIfFiveOrMore = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Serena Malyon"
        flavorText = "Rootha was humbled in the whale's presence. It was glorious and beautiful, all on its own terms."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c90366d5-b4ba-4772-a3c5-f138bbe7f305.jpg?1775938597"
    }
}
