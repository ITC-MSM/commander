package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thunderdrum Soloist
 * {1}{R}
 * Creature — Dwarf Bard
 * 1/3
 * Reach
 * Opus — Whenever you cast an instant or sorcery spell, this creature deals 1 damage to each
 * opponent. If five or more mana was spent to cast that spell, this creature deals 3 damage to
 * each opponent instead.
 *
 * "Opus" is an ability word (flavor only). The `opus { }` builder wires the spell-cast trigger
 * and the 5+ mana tier. The 5+ mana payoff *replaces* the base damage ("instead"), so it's
 * `insteadIfFiveOrMore`. Damage is dealt by this creature (`damageSource = Self`).
 */
val ThunderdrumSoloist = card("Thunderdrum Soloist") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Bard"
    power = 1
    toughness = 3
    oracleText = "Reach\nOpus — Whenever you cast an instant or sorcery spell, this creature deals " +
        "1 damage to each opponent. If five or more mana was spent to cast that spell, this " +
        "creature deals 3 damage to each opponent instead."

    keywords(Keyword.REACH)

    opus {
        effect = Effects.DealDamage(
            1,
            EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.Self
        )
        insteadIfFiveOrMore = Effects.DealDamage(
            3,
            EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/590d1d95-ed13-4121-899f-f5a2d8a6617a.jpg?1775937905"
    }
}
