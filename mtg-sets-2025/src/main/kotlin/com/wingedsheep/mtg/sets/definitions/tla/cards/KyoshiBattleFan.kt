package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kyoshi Battle Fan
 * {2}
 * Artifact — Equipment
 * When this Equipment enters, create a 1/1 white Ally creature token, then attach this Equipment to it.
 * Equipped creature gets +1/+0.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The ETB token-and-attach is composed from two existing primitives chained through the token
 * pipeline (same shell as Final Fantasy's Job-select Equipment): [Effects.CreateToken] makes the
 * 1/1 white Ally and publishes its entity id to the [CREATED_TOKENS] slot, then
 * [Effects.AttachEquipment] reads that slot back ([EffectTarget.PipelineTarget]) to attach the
 * source Equipment to the freshly-created token.
 */
val KyoshiBattleFan = card("Kyoshi Battle Fan") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, create a 1/1 white Ally creature token, then attach this Equipment to it.\n" +
        "Equipped creature gets +1/+0.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Ally")
            ),
            Effects.AttachEquipment(EffectTarget.PipelineTarget(CREATED_TOKENS, 0))
        )
    }

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "257"
        artist = "VEN"
        flavorText = "Kyoshi Warriors wield traditional war fans to honor their namesake."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/257e6862-96fe-4312-aff2-509d0696843a.jpg?1776653964"
    }
}
