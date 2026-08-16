package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Winterflame
 * {1}{U}{R}
 * Instant
 * Choose one or both —
 * • Tap target creature.
 * • Winterflame deals 2 damage to target creature.
 */
val Winterflame = card("Winterflame") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n• Tap target creature.\n• Winterflame deals 2 damage to target creature."

    spell {
        // "Choose one or both" is the *count*, not a third mode: `chooseCount = 2` with
        // `minChooseCount = 1` (CR 700.2). Spelling the "both" branch as an extra mode looked
        // equivalent and is a different card — the spell would report one chosen mode to
        // `SpellCastEvent.chosenModesCount`, and a copy effect changing modes would see three.
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Tap target creature") {
                val t = target("creature to tap", TargetCreature())
                effect = Effects.Tap(t)
            }
            mode("Winterflame deals 2 damage to target creature") {
                val t = target("creature to damage", TargetCreature())
                effect = DealDamageEffect(2, t)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Richard Wright"
        flavorText = "\"The mountains scream with the dragons' throats.\"\n—Chianul, Who Whispers Twice"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8924ab9-fa55-4348-b67d-b2b9e48a357a.jpg?1562792511"
    }
}
