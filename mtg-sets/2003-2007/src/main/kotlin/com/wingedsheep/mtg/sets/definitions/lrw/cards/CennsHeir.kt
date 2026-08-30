package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cenn's Heir
 * {1}{W}
 * Creature — Kithkin Soldier
 * 1/1
 * Whenever this creature attacks, it gets +1/+1 until end of turn for each other attacking Kithkin.
 *
 * The printed count has no controller clause, so it is [Player.Each] rather than the more common
 * `Player.You` — in a multiplayer attack a teammate's Kithkin counts too. "Other" is the aggregate's
 * own `excludeSelf`, not a subtract-one, so the count stays right even in the corner where the source
 * has stopped being a Kithkin by the time the trigger resolves.
 */
val CennsHeir = card("Cenn's Heir") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature attacks, it gets +1/+1 until end of turn for each other attacking Kithkin."

    triggeredAbility {
        trigger = Triggers.Attacks
        val otherAttackingKithkin = DynamicAmount.AggregateBattlefield(
            Player.Each,
            GameObjectFilter.Creature.withSubtype(Subtype.KITHKIN).attacking(),
            excludeSelf = true
        )
        effect = Effects.ModifyStats(
            power = otherAttackingKithkin,
            toughness = otherAttackingKithkin,
            target = EffectTarget.Self
        )
        description = "Whenever this creature attacks, it gets +1/+1 until end of turn for each other attacking Kithkin."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Steven Belledin"
        flavorText = "\"His home clachan's familial spirit bolsters his own, but he will be ready to preside over the town as cenn only after he learns to project that strength to others.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7610d2f-e35f-42fa-a3fb-fcae54bf5a1d.jpg?1783942917"
    }
}
