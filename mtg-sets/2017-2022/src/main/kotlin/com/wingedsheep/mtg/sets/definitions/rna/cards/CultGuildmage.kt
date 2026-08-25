package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Cult Guildmage — Ravnica Allegiance #164
 * {B}{R} · Creature — Human Shaman · 2 / 2
 *
 * The Rakdos entry in the RNA Guildmage cycle. "Activate only as a sorcery" is
 * [TimingRule.SorcerySpeed] on that one ability, not a separate restriction; the ping targets
 * an opponent *or* a planeswalker, which is its own target requirement rather than a filtered
 * permanent target.
 */
val CultGuildmage = card("Cult Guildmage") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{3}{B}, {T}: Target player discards a card. Activate only as a sorcery.\n" +
        "{R}, {T}: This creature deals 1 damage to target opponent or planeswalker."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.Tap)
        val player = target("target", Targets.Player)
        effect = Patterns.Hand.discardCards(1, player)
        timing = TimingRule.SorcerySpeed
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val victim = target("target", Targets.OpponentOrPlaneswalker)
        effect = Effects.DealDamage(1, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0536c2fa-7402-49a1-9016-dcf5633ca9ef.jpg"
    }
}
