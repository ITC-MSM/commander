package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Reiver Demon — Mirrodin #75
 * {4}{B}{B}{B}{B} · Creature — Demon · 6/6 · Rare
 *
 * Flying
 * When this creature enters, if you cast it from your hand, destroy all nonartifact, nonblack
 * creatures. They can't be regenerated.
 *
 * The intervening-if is [Conditions.WasCastFromHand] on the enters trigger (CR 603.4) — the same
 * cast-origin marker Phage the Untouchable reads with the negated form. Reanimating or blinking the
 * Demon gets a 6/6 flier and no wipe.
 *
 * The wipe itself is [Effects.DestroyAll] over `Creature.nonartifact().notColor(BLACK)` with
 * `noRegenerate = true` for "they can't be regenerated" (functional errata of the printed "bury").
 * Reiver Demon is black, so it is outside its own filter and never needs an explicit self-exclusion;
 * an artifact creature or another black creature survives too.
 */
val ReiverDemon = card("Reiver Demon") {
    manaCost = "{4}{B}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "When this creature enters, if you cast it from your hand, destroy all nonartifact, " +
        "nonblack creatures. They can't be regenerated."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCastFromHand
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.Creature.nonartifact().notColor(Color.BLACK),
            noRegenerate = true
        )
        description = "When this creature enters, if you cast it from your hand, destroy all " +
            "nonartifact, nonblack creatures. They can't be regenerated."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Brom"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d109abda-982f-4907-8b64-ec63e138bc42.jpg?1783944545"
        ruling(
            "2013-09-20",
            "If a creature (such as Clone) enters the battlefield as a copy of this creature, the " +
                "copy's \"enters-the-battlefield\" ability will still trigger as long as you cast " +
                "that creature spell from your hand."
        )
    }
}
