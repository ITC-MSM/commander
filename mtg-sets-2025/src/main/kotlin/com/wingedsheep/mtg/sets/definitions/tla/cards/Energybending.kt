package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Energybending — {2} Instant — Lesson
 *
 * Lands you control gain all basic land types until end of turn.
 * Draw a card.
 *
 * Modeled with existing primitives: a [Effects.ForEachInGroup] over every land you
 * control (snapshotted at resolution) whose body adds each of the five basic land
 * types to that land in addition to its other types (Rule 305.7 — "gain", not
 * "become", so the land keeps its existing types and mana abilities and picks up
 * the mana ability of every basic type) until end of turn, followed by drawing a
 * card.
 */
val Energybending = card("Energybending") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Instant — Lesson"
    oracleText = "Lands you control gain all basic land types until end of turn.\nDraw a card."

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                filter = GroupFilter.AllLands.youControl(),
                effect = Effects.Composite(
                    Effects.AddSubtype("Plains", EffectTarget.Self, Duration.EndOfTurn),
                    Effects.AddSubtype("Island", EffectTarget.Self, Duration.EndOfTurn),
                    Effects.AddSubtype("Swamp", EffectTarget.Self, Duration.EndOfTurn),
                    Effects.AddSubtype("Mountain", EffectTarget.Self, Duration.EndOfTurn),
                    Effects.AddSubtype("Forest", EffectTarget.Self, Duration.EndOfTurn)
                )
            ),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Hisashi Momose"
        flavorText = "\"Darkness thrives in the void, but always yields to purifying light.\"\n—The Lion-Turtle"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c085441-e023-4032-89a0-d24ce5060ac3.jpg?1778833146"
    }
}
