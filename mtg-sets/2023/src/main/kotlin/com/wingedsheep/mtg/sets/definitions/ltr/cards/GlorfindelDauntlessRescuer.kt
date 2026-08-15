package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glorfindel, Dauntless Rescuer
 * {2}{G}
 * Legendary Creature — Elf Noble
 * 3/2
 *
 * Whenever you scry, choose one and Glorfindel gets +1/+1 until end of turn.
 * • Glorfindel must be blocked this turn if able.
 * • Glorfindel can't be blocked by more than one creature each combat this turn.
 *
 * The +1/+1 always happens (it sits before the bullet list), so it is folded into each mode
 * (exactly one mode is chosen → +1/+1 once). Mode 1 = `MustBeBlockedEffect(Self, allCreatures =
 * false)` ("must be blocked if able"); mode 2 grants `AbilityFlag.CANT_BE_BLOCKED_BY_MORE_THAN_ONE`
 * for the turn.
 */
val GlorfindelDauntlessRescuer = card("Glorfindel, Dauntless Rescuer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elf Noble"
    power = 3
    toughness = 2
    oracleText = "Whenever you scry, choose one and Glorfindel gets +1/+1 until end of turn.\n" +
        "• Glorfindel must be blocked this turn if able.\n" +
        "• Glorfindel can't be blocked by more than one creature each combat this turn."

    // Folding the +1/+1 into each mode (rather than Composite(pump, modal)) keeps it on the
    // engine's modal-decision path.
    triggeredAbility {
        trigger = Triggers.WheneverYouScry
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.Composite(
                    listOf(
                        Effects.ModifyStats(1, 1, EffectTarget.Self),
                        MustBeBlockedEffect(EffectTarget.Self, allCreatures = false)
                    )
                ),
                "Glorfindel gets +1/+1 until end of turn and must be blocked this turn if able"
            ),
            Mode.noTarget(
                Effects.Composite(
                    listOf(
                        Effects.ModifyStats(1, 1, EffectTarget.Self),
                        Effects.GrantKeyword(
                            AbilityFlag.CANT_BE_BLOCKED_BY_MORE_THAN_ONE,
                            EffectTarget.Self,
                            Duration.EndOfTurn
                        )
                    )
                ),
                "Glorfindel gets +1/+1 until end of turn and can't be blocked by more than one creature each combat this turn"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        flavorText = "On the Elf-lord's brow sat wisdom, and in his hand was strength."
        artist = "Viko Menezes"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/baf7a546-8a8b-4396-ab64-9a5b9abffe79.jpg?1686969418"
    }
}
