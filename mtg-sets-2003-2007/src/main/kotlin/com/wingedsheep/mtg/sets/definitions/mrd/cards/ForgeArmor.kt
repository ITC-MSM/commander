package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Forge Armor — Mirrodin #92
 * {4}{R} · Instant
 *
 * As an additional cost to cast this spell, sacrifice an artifact.
 * Put X +1/+1 counters on target creature, where X is the sacrificed artifact's mana value.
 *
 * The spell-side twin of Bosh, Iron Golem, composed the same way as Eldritch Evolution: the
 * additional cost binds the sacrificed artifact to [EntityReference.Sacrificed] at cost payment,
 * and the counter count reads [EntityNumericProperty.ManaValue] off that snapshot at resolution —
 * mana value is a printed characteristic, so it still reads correctly from the graveyard.
 *
 * The additional cost is paid at cast time (CR 601.2h), so an artifact sacrificed to it is gone
 * before the spell resolves; sacrificing a {0} artifact is legal and simply puts no counters on
 * the target. An {X} artifact on the battlefield has X = 0 (CR 202.3b).
 */
val ForgeArmor = card("Forge Armor") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice an artifact.\n" +
        "Put X +1/+1 counters on target creature, where X is the sacrificed artifact's mana value."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Artifact))

    spell {
        target = Targets.Creature
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmount.EntityProperty(
                EntityReference.Sacrificed(0),
                EntityNumericProperty.ManaValue
            ),
            target = EffectTarget.ContextTarget(0)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "92"
        artist = "Tony Szczudlo"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/2873b6d5-af76-498c-bc2d-a26c36be3cbd.jpg?1783944541"
    }
}
