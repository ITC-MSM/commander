package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Generous Visitor — Kamigawa: Neon Dynasty #185 (canonical printing)
 * {G} · Creature — Spirit · 1/1
 *
 * Whenever you cast an enchantment spell, put a +1/+1 counter on target creature.
 *
 * The counter goes on *target* creature, not on this one, so the Visitor can grow itself or feed
 * a better body — NEO's enchantment-creature commons make either line live.
 */
val GenerousVisitor = card("Generous Visitor") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever you cast an enchantment spell, put a +1/+1 counter on target creature."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        val t = target("creature to grow", TargetCreature())
        effect = Effects.AddCounters("+1/+1", 1, t)
        description = "Whenever you cast an enchantment spell, put a +1/+1 counter on target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Iris Compiet"
        flavorText = "It is said that one must modestly refuse a gift three times before accepting " +
            "it to show humility before the kami."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1066ccd-a932-4d05-98da-11ae4675364e.jpg?1783923849"
    }
}
