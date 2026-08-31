package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gate Hound — Ravnica: City of Guilds #19 (canonical printing, only printing)
 * {2}{W} · Creature — Dog · 1/1
 *
 * Creatures you control have vigilance as long as this creature is enchanted.
 *
 * Printed as "Creature — Hound"; the type line here is the modern Hound→Dog errata Scryfall
 * serves.
 *
 * A lord effect behind a gate, so it is one `staticAbility` with both halves filled in rather
 * than anything bespoke:
 *
 * - The gate is `Conditions.SourceMatches(GameObjectFilter.Any.enchanted())` — "as long as *this
 *   creature* is enchanted", i.e. it has at least one Aura attached (CR 303.4). `enchanted()`
 *   deliberately ignores who controls the Aura, which is right: an opponent's Aura on the Hound
 *   still turns the vigilance on, and that is the printed behaviour.
 * - The grant is `GroupFilter.AllCreaturesYouControl`, not "other creatures" — the Hound is one
 *   of the creatures you control, so it gains vigilance too.
 *
 * Both halves are continuous and re-read every projection pass, so attaching or removing the Aura
 * flips the whole team's vigilance on and off with no event of its own.
 */
val GateHound = card("Gate Hound") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    power = 1
    toughness = 1
    oracleText = "Creatures you control have vigilance as long as this creature is enchanted."

    staticAbility {
        condition = Conditions.SourceMatches(GameObjectFilter.Any.enchanted())
        ability = GrantKeyword(Keyword.VIGILANCE, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Ralph Horsley"
        flavorText = "\"Ditri, I leave this checkpoint in your capable teeth.\"\n—Kitov, nightguard patrol"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06732c6f-c3b5-4c68-82a0-655cffff79db.jpg?1783943700"
    }
}
