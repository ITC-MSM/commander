package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Itzquinth, Firstborn of Gishath
 * {R}{G}
 * Legendary Creature — Dinosaur
 * 2/3
 * Haste
 * When Itzquinth enters, you may pay {2}. When you do, target Dinosaur you control deals
 * damage equal to its power to another target creature.
 *
 * The "When you do" phrasing is a reflexive trigger (CR 603.12), modeled as a gated effect
 * ([MayPayManaEffect]) where paying {2} unlocks the bite. The ETB trigger resolves to the
 * "Pay {2}?" decision first; only after payment does the reflexive trigger go on the stack
 * and prompt for its two targets:
 *   - t1 (index 0): target Dinosaur you control — any Creature with subtype Dinosaur you
 *     control (including Itzquinth itself, which IS a Dinosaur).
 *   - t2 (index 1): another target creature — [TargetOther] ensures this differs from t1.
 * Damage amount = t1's power at resolution ([DynamicAmounts.targetPower(0)]), and the source
 * of the damage is t1 ([damageSource = t1]), so effects that care about the dealer reference
 * the Dinosaur, not Itzquinth.
 */
val ItzquinthFirstbornOfGishath = card("Itzquinth, Firstborn of Gishath") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Dinosaur"
    power = 2
    toughness = 3
    oracleText = "Haste\nWhen Itzquinth enters, you may pay {2}. When you do, target Dinosaur you control deals damage equal to its power to another target creature."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        // t1 (index 0): the Dinosaur you control that deals the damage.
        val t1 = target(
            "target Dinosaur you control",
            TargetCreature(filter = TargetFilter.Creature.withSubtype("Dinosaur").youControl())
        )
        // t2 (index 1): must be a different creature from t1 (the "another" constraint).
        val t2 = target(
            "another target creature",
            TargetOther(TargetCreature())
        )
        // "you may pay {2}. When you do" → Gate.MayPay; if paid, t1 deals damage to t2.
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = DealDamageEffect(DynamicAmounts.targetPower(0), t2, damageSource = t1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "230"
        artist = "Lars Grant-West"
        flavorText = "\"Dinosaurs have no concept of royalty, but they recognize the scent of the mightiest among them.\"\n—Atla Palani, nest tender"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/7112c366-b36a-4bc8-aa64-6bad16bebc39.jpg?1782694426"
    }
}
