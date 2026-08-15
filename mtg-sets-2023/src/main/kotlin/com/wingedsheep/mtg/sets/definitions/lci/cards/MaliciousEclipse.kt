package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Malicious Eclipse
 * {1}{B}{B}
 * Sorcery
 * All creatures get -2/-2 until end of turn. If a creature an opponent controls would die this turn,
 * exile it instead.
 */
val MaliciousEclipse = card("Malicious Eclipse") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "All creatures get -2/-2 until end of turn. If a creature an opponent controls would " +
        "die this turn, exile it instead."

    spell {
        effect = Effects.Composite(
            listOf(
                // Install the death→exile replacement first so it's active when the -2/-2 kills are
                // checked as state-based actions after this spell resolves. A floating grant
                // (GrantedReplacementEffect keyed on the caster, not the sorcery) — persists after the
                // sorcery leaves; the zone-change redirect path reads it (Forgotten Cellar idiom).
                Effects.GrantReplacementEffect(
                    replacement = RedirectZoneChange(
                        newDestination = Zone.EXILE,
                        appliesTo = EventPattern.ZoneChangeEvent(
                            filter = GameObjectFilter.Creature.opponentControls(),
                            from = Zone.BATTLEFIELD,
                            to = Zone.GRAVEYARD
                        )
                    ),
                    target = EffectTarget.Self,
                    duration = Duration.EndOfTurn
                ),
                Effects.ForEachInGroup(
                    filter = GroupFilter.AllCreatures,
                    effect = ModifyStatsEffect(-2, -2, EffectTarget.Self)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Campbell White"
        flavorText = "Aclazotz laughed as the dark shards closed around Chimil, plunging the Core into " +
            "unnatural darkness. At long last, his bloody reign would begin."
        imageUri = "https://cards.scryfall.io/normal/front/2/7/2796fffa-8cbf-4ec9-91a8-7b6f39fd50ec.jpg?1782694523"
    }
}
