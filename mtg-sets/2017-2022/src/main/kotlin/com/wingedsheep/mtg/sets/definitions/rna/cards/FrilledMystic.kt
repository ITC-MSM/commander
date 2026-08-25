package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frilled Mystic — Ravnica Allegiance #174
 * {G}{G}{U}{U} · Creature — Elf Lizard Wizard · 3 / 2
 *
 * Flash plus an enters-counter is the Draining Whelk shape: cast it in response, and the
 * counter resolves from the trigger. `optional = true` is the printed "you may" — the DSL
 * lowers it to a `Gate.MayDecide` around the counter, asked at resolution, while the target is
 * still locked in when the trigger goes on the stack (CR 603.3d).
 */
val FrilledMystic = card("Frilled Mystic") {
    manaCost = "{G}{G}{U}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Elf Lizard Wizard"
    power = 3
    toughness = 2
    oracleText = "Flash\n" +
        "When this creature enters, you may counter target spell."

    keywords(Keyword.FLASH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        target("target", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Randy Vargas"
        flavorText = "\"Your arrival was expected...and unwelcome.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50595d02-edad-48a6-b10c-6fa859cc88bb.jpg"
    }
}
