package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Insidious Fungus
 * {G}
 * Creature — Fungus
 * 1/2
 *
 * {2}, Sacrifice this creature: Choose one —
 * • Destroy target artifact.
 * • Destroy target enchantment.
 * • Draw a card. Then you may put a land card from your hand onto the battlefield tapped.
 *
 * Modal activated ability (CR 700.2 — choose one). Cost is `{2}` plus sacrificing this
 * creature; the modes are chosen at resolution via [ModalEffect.chooseOne]. The first two
 * modes target an artifact / enchantment; the third draws then optionally puts a land from
 * hand onto the battlefield tapped via [Patterns.Hand.putFromHand] (the "you may" is the
 * choose-up-to-one selection inside that pattern).
 */
val InsidiousFungus = card("Insidious Fungus") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 1
    toughness = 2
    oracleText = "{2}, Sacrifice this creature: Choose one —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target enchantment.\n" +
        "• Draw a card. Then you may put a land card from your hand onto the battlefield tapped."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf)
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.Destroy(EffectTarget.ContextTarget(0)),
                Targets.Artifact,
                "Destroy target artifact"
            ),
            Mode.withTarget(
                Effects.Destroy(EffectTarget.ContextTarget(0)),
                Targets.Enchantment,
                "Destroy target enchantment"
            ),
            Mode.noTarget(
                Effects.Composite(
                    Effects.DrawCards(1),
                    Patterns.Hand.putFromHand(GameObjectFilter.Land, entersTapped = true)
                ),
                "Draw a card. Then you may put a land card from your hand onto the battlefield tapped"
            )
        )
        description = "{2}, Sacrifice this creature: Choose one — Destroy target artifact; " +
            "or destroy target enchantment; or draw a card, then you may put a land card from " +
            "your hand onto the battlefield tapped."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "186"
        artist = "Slawomir Maniak"
        flavorText = "Wherever it grows, wood rots, walls turn gray, and flesh becomes fertilizer."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d60d2e62-06da-410a-81ed-6cebb2632fb6.jpg?1726286558"
    }
}
