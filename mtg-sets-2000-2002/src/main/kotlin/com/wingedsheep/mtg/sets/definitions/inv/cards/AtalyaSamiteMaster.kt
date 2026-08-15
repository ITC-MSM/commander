package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Atalya, Samite Master
 * {3}{W}{W}
 * Legendary Creature — Human Cleric
 * 2/3
 * {X}, {T}: Choose one —
 * • Prevent the next X damage that would be dealt to target creature this turn. Spend only
 *   white mana on X.
 * • You gain X life. Spend only white mana on X.
 *
 * The `xManaRestriction = {WHITE}` on the activated ability forces the `{X}` to be paid with
 * white mana only (honored by the mana solver and the activated-ability payment path).
 */
val AtalyaSamiteMaster = card("Atalya, Samite Master") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 3
    oracleText = "{X}, {T}: Choose one —\n" +
        "• Prevent the next X damage that would be dealt to target creature this turn. " +
        "Spend only white mana on X.\n" +
        "• You gain X life. Spend only white mana on X."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{X}"), Costs.Tap)
        xManaRestriction = setOf(Color.WHITE)
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.PreventNextDamage(DynamicAmount.XValue, EffectTarget.ContextTarget(0)),
                Targets.Creature,
                "Prevent the next X damage that would be dealt to target creature this turn"
            ),
            Mode.noTarget(
                Effects.GainLife(DynamicAmount.XValue),
                "You gain X life"
            )
        )
        description = "{X}, {T}: Choose one — Prevent the next X damage that would be dealt to " +
            "target creature this turn; or you gain X life. Spend only white mana on X."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90500e7a-f76d-453a-bda0-d56d3f7c7534.jpg?1562924117"
    }
}
