package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Careless Celebrant
 * {1}{R}
 * Creature — Satyr Shaman
 * 2/1
 *
 * When this creature dies, it deals 2 damage to target creature or planeswalker an opponent controls.
 *
 * A plain [Triggers.Dies] — no `triggerZone`, which would replace the default `{BATTLEFIELD}` and
 * leave the trigger unindexed.
 *
 * "Creature or planeswalker **an opponent controls**" carries a controller predicate, so it is a
 * filtered [TargetObject] rather than the SDK's bare `TargetCreatureOrPlaneswalker` (which holds no
 * filter at all and only spells the unqualified phrase).
 *
 * No `damageSource`: "it deals" is the source dealing the damage, which is what the effect already
 * means with the field left null.
 */
val CarelessCelebrant = card("Careless Celebrant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Satyr Shaman"
    power = 2
    toughness = 1
    oracleText = "When this creature dies, it deals 2 damage to target creature or planeswalker an opponent controls."

    triggeredAbility {
        trigger = Triggers.Dies
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls())),
        )
        effect = Effects.DealDamage(2, victim)
        description = "When this creature dies, it deals 2 damage to target creature or " +
            "planeswalker an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Mathias Kollros"
        flavorText = "\"Renata was mesmerized by the satyr's dance of gleeful indifference, of reckless grace and bright disaster.\"\n—Luphea of Setessa, *Histories*"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bac6bdd4-b25b-41f6-835d-7d1570cdb951.jpg"
    }
}
