package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity

/**
 * Exhibition Tidecaller
 * {U}
 * Creature — Djinn Wizard
 * 0/2
 *
 * Opus — Whenever you cast an instant or sorcery spell, target player mills three cards. If five or
 * more mana was spent to cast that spell, that player mills ten cards instead.
 *
 * "Opus" is an ability word (flavor only). The `opus { }` builder wires the spell-cast trigger and
 * the 5+ mana tier. A single `target` player is declared and referenced from both the base mill and
 * the bonus mill so the same chosen player carries across the tier.
 */
val ExhibitionTidecaller = card("Exhibition Tidecaller") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn Wizard"
    power = 0
    toughness = 2
    oracleText = "Opus — Whenever you cast an instant or sorcery spell, target player mills three " +
        "cards. If five or more mana was spent to cast that spell, that player mills ten cards instead."

    opus {
        val player = target("player", Targets.Player)
        effect = Patterns.Library.mill(3, player)
        insteadIfFiveOrMore = Patterns.Library.mill(10, player)
        description = "Opus — Whenever you cast an instant or sorcery spell, target player mills " +
            "three cards. If five or more mana was spent to cast that spell, that player mills ten " +
            "cards instead."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "48"
        artist = "Tulio Brito"
        flavorText = "\"Dangerously overflowing with creative expression.\"\n—Strixhaven Star " +
            "gallery show review"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a58c364e-d0c5-41b9-8c8b-2e5a99468cc7.jpg?1775937242"
    }
}
