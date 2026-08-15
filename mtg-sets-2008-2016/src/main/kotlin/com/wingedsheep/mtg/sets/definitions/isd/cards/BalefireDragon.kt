package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Balefire Dragon
 * {5}{R}{R}
 * Creature — Dragon
 * 6/6
 * Flying
 * Whenever this creature deals combat damage to a player, it deals that much damage to
 * each creature that player controls.
 *
 * The damaged player is the [Player.TriggeringPlayer] of the combat-damage trigger; "that much"
 * is [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]. The board sweep composes the group-damage
 * pattern over the creatures that player controls (cf. Radiating Lightning).
 */
val BalefireDragon = card("Balefire Dragon") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying\nWhenever this creature deals combat damage to a player, it deals that much " +
        "damage to each creature that player controls."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Patterns.Group.dealDamageToAll(
            DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            GroupFilter(
                GameObjectFilter.Creature.targetPlayerControls(
                    EffectTarget.PlayerRef(Player.TriggeringPlayer)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "129"
        artist = "Eric Deschamps"
        flavorText = "If it comes for you, die boldly or die swiftly—for die you will."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0dce4ac-f472-4f3b-b01a-eff0902a578f.jpg?1782714754"
    }
}
