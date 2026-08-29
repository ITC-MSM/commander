package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.withId

/**
 * Return to Nature
 * {1}{G}
 * Instant
 *
 * Choose one —
 * • Destroy target artifact.
 * • Destroy target enchantment.
 * • Exile target card from a graveyard.
 *
 * Three modes, each with its own single target. Every mode binds its requirement under the same
 * name, so each mode's effect reads its own chosen object; the modes are deliberately left without
 * hand-written descriptions so each one's text derives from its effect.
 */
val ReturnToNature = card("Return to Nature") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target enchantment.\n" +
        "• Exile target card from a graveyard."

    spell {
        val chosen = EffectTarget.BoundVariable("target")
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                effect = Effects.Destroy(chosen),
                target = Targets.Artifact.withId("target")
            ),
            Mode.withTarget(
                effect = Effects.Destroy(chosen),
                target = Targets.Enchantment.withId("target")
            ),
            Mode.withTarget(
                effect = Effects.Exile(chosen),
                target = Targets.CardInGraveyard.withId("target")
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Alayna Danner"
        flavorText = "\"Yes, nature is stronger. You don't see little buildings sprouting on trees.\"\n—Emmara"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/085e3129-591b-46ec-ac8b-cff428927c01.jpg"
    }
}
