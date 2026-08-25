package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity

/**
 * Clamor Shaman — Ravnica Allegiance #96
 * {2}{R} · Creature — Goblin Shaman · 1 / 1
 *
 * Riot plus an attack trigger. "Can't block this turn" is [Effects.CantBlock] — a
 * combat restriction on the targeted creature, not an evasion grant on the attacker, so a
 * second blocker is unaffected.
 */
val ClamorShaman = card("Clamor Shaman") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 1
    toughness = 1
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)\n" +
        "Whenever this creature attacks, target creature an opponent controls can't block this turn."

    riot()
    triggeredAbility {
        trigger = Triggers.Attacks
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.CantBlock(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Tomasz Jedruszek"
        flavorText = "\"Little goblin. Big noise.\"\n" +
        "—Ruric Thar"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3f073d7-f60a-44c1-aec9-cf42bbdb3153.jpg"
    }
}
