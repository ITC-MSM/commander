package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.flurry
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cori-Steel Cutter — Tarkir: Dragonstorm #103
 * {1}{R} · Artifact — Equipment
 *
 * Equipped creature gets +1/+1 and has trample and haste.
 * Flurry — Whenever you cast your second spell each turn, create a 1/1 white Monk creature
 * token with prowess. You may attach this Equipment to it.
 * Equip {1}{R}
 */
val CoriSteelCutter = card("Cori-Steel Cutter") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and has trample and haste.\n" +
        "Flurry — Whenever you cast your second spell each turn, create a 1/1 white Monk creature " +
        "token with prowess. You may attach this Equipment to it. (Whenever you cast a noncreature " +
        "spell, the token gets +1/+1 until end of turn.)\n" +
        "Equip {1}{R}"

    staticAbility {
        ability = ModifyStats(+1, +1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, Filters.EquippedCreature)
    }

    // Flurry — create the Monk, then optionally attach this Equipment to it.
    flurry {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Monk"),
            keywords = setOf(Keyword.PROWESS),
            imageUri = "https://cards.scryfall.io/normal/front/6/3/633d2d10-def7-426f-8496-ed6b45684299.jpg?1742421122"
        ).then(
            MayEffect(
                effect = Effects.AttachEquipment(EffectTarget.PipelineTarget(CREATED_TOKENS, 0)),
                descriptionOverride = "You may attach this Equipment to it"
            )
        )
    }

    equipAbility("{1}{R}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "103"
        artist = "Xabi Gaztelua"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/490eb213-9ae2-4b45-abec-6f1dfc83792a.jpg?1779102209"
    }
}
