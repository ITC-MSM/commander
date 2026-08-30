package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Kessig Malcontents — Avacyn Restored #142
 * {2}{R} · Creature — Human Warrior · 3/1
 *
 * When this creature enters, it deals damage to target player or planeswalker equal to the number
 * of Humans you control.
 *
 * "Humans" is a bare tribal noun, so the count is over *permanents* with the subtype, not only
 * creatures — the Malcontents itself is one of them, since it is already on the battlefield when
 * the trigger resolves. The amount is read at resolution by
 * [DynamicAmounts.battlefield], so removal in response shrinks the damage.
 */
val KessigMalcontents = card("Kessig Malcontents") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 1
    oracleText = "When this creature enters, it deals damage to target player or planeswalker equal to the number " +
        "of Humans you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype("Human")
            ).count(),
            t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "John Stanko"
        flavorText = "Discontent is a powerful weapon in the hands of a mob."
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dce9a30f-a850-4826-a255-ce511d567b60.jpg?1783940682"
    }
}
