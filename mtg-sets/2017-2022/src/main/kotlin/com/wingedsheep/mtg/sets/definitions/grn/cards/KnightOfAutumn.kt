package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.withId

/**
 * Knight of Autumn
 * {1}{G}{W}
 * Creature — Dryad Knight
 * 2/1
 * When this creature enters, choose one —
 * • Put two +1/+1 counters on this creature.
 * • Destroy target artifact or enchantment.
 * • You gain 4 life.
 */
val KnightOfAutumn = card("Knight of Autumn") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Dryad Knight"
    oracleText = "When this creature enters, choose one —\n" +
        "• Put two +1/+1 counters on this creature.\n" +
        "• Destroy target artifact or enchantment.\n" +
        "• You gain 4 life."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect(
            modes = listOf(
                Mode.noTarget(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
                ),
                Mode.withTarget(
                    Effects.Destroy(EffectTarget.BoundVariable("target")),
                    Targets.ArtifactOrEnchantment.withId("target")
                ),
                Mode.noTarget(Effects.GainLife(4))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "183"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/3028075c-5fc5-4942-a984-1ffcf7a8933d.jpg?1783934130"
    }
}
