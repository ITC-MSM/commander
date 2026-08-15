package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Scathing Shadelock // Venomous Words — Secrets of Strixhaven #98
 * {4}{B} · Creature — Snake Warlock · 4/6
 *
 * At the beginning of your first main phase, this creature becomes prepared.
 * (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Venomous Words — {B} · Sorcery: Target creature you control gets +2/+0 and gains
 * deathtouch until end of turn.
 *
 * Prepare (Secrets of Strixhaven): Scathing Shadelock does NOT enter prepared (no PREPARED
 * keyword). It becomes prepared via its first-main-phase trigger through [Effects.BecomePrepared].
 * Becoming prepared creates a copy of its prepare spell ("Venomous Words") in exile that its
 * controller may cast for {B}; casting that copy unprepares the creature. Modeled via
 * `CardLayout.PREPARE` + the `prepare(name) { }` DSL, like Maelstrom Artisan / Leech Collector.
 */
val ScathingShadelock = card("Scathing Shadelock") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Snake Warlock"
    power = 4
    toughness = 6
    oracleText = "At the beginning of your first main phase, this creature becomes prepared. " +
        "(While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Venomous Words — the prepare spell.
    prepare("Venomous Words") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        oracleText = "Target creature you control gets +2/+0 and gains deathtouch until end of turn."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
            effect = Effects.Composite(
                Effects.ModifyStats(2, 0, t),
                Effects.GrantKeyword(Keyword.DEATHTOUCH, t),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03e664cd-c3a6-4263-b2d8-dd99058fb8ec.jpg?1775937593"
    }
}
