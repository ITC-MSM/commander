package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfSourceEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Dreadfeast Demon — Innistrad: Crimson Vow #108
 * {5}{B}{B} · Creature — Demon · Rare · 6/6
 * Artist: Andrew Mar
 *
 * Flying
 * At the beginning of your end step, sacrifice a non-Demon creature. If you do, create a token
 * that's a copy of this creature.
 *
 * The "sacrifice … If you do, …" is a gated composite ([Effects.IfYouDo]): the copy runs only when
 * the sacrifice actually happened (CR 608.2 "if you do" — if there's nothing to sacrifice, the copy
 * is skipped). The sacrifice is *mandatory* but requires a valid non-Demon creature, so it is
 * modeled as a gather → `chooseExactly(1)` → `sacrifice` pipeline whose terminal move is the
 * gate's success probe (`SuccessCriterion.Auto` infers success from the graveyard growing):
 *   - no non-Demon fodder → nothing eligible → empty move → graveyard unchanged → no copy;
 *   - exactly one non-Demon → auto-selected (no prompt) → sacrificed → copy;
 *   - several → controller must pick one to sacrifice → copy.
 * The `notSubtype(Demon)` filter keeps Dreadfeast Demon itself (and any Demon token copies it
 * spawns) out of the fodder pool. The copy reuses the general [CreateTokenCopyOfSourceEffect]
 * "copy of this creature" primitive.
 */
val DreadfeastDemon = card("Dreadfeast Demon") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "At the beginning of your end step, sacrifice a non-Demon creature. If you do, create a " +
        "token that's a copy of this creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.IfYouDo(
            action = Effects.Pipeline {
                val fodder = gather(GameObjectFilter.Creature.notSubtype(Subtype.DEMON), player = Player.You)
                val chosen = chooseExactly(
                    1,
                    from = fodder,
                    useTargetingUI = true,
                    prompt = "Choose a non-Demon creature to sacrifice"
                )
                sacrifice(chosen)
            },
            ifYouDo = CreateTokenCopyOfSourceEffect()
        )
        description = "At the beginning of your end step, sacrifice a non-Demon creature. If you " +
            "do, create a token that's a copy of this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "108"
        artist = "Andrew Mar"
        flavorText = "By the sixth day of darkness, the lake was more blood than water."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/269199ea-2106-4299-ade0-10cce1320434.jpg?1783924865"
    }
}
