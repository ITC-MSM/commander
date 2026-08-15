package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Colorstorm Stallion
 * {1}{U}{R}
 * Creature — Elemental Horse
 * 3/3
 * Ward {1}, haste
 * Opus — Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of
 * turn. If five or more mana was spent to cast that spell, create a token that's a copy of this
 * creature.
 *
 * "Opus" is an ability word (flavor only). The `opus { }` builder wires the spell-cast trigger and
 * the 5+ mana tier (`ContextProperty(MANA_SPENT_ON_TRIGGERING_SPELL) >= 5`). The token copy is
 * `alsoIfFiveOrMore`, so it runs in addition to the unconditional +1/+1 (see Expressive Firedancer).
 */
val ColorstormStallion = card("Colorstorm Stallion") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Elemental Horse"
    power = 3
    toughness = 3
    oracleText = "Ward {1}, haste\nOpus — Whenever you cast an instant or sorcery spell, this " +
        "creature gets +1/+1 until end of turn. If five or more mana was spent to cast that " +
        "spell, create a token that's a copy of this creature."

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{1}")))

    opus {
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        alsoIfFiveOrMore = Effects.CreateTokenCopyOfSelf()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "180"
        artist = "Lorenzo Lanfranconi"
        flavorText = "Prismari students are never afraid to let their imaginations run wild."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5b54d46-2caf-4d1b-8be1-dbd9e9dce058.jpg?1775938240"
    }
}
