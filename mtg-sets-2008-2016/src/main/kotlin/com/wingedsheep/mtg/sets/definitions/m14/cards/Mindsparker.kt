package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mindsparker
 * {1}{R}{R}
 * Creature — Elemental
 * 3/2
 * First strike
 * Whenever an opponent casts a white or blue instant or sorcery spell, this creature deals 2 damage
 * to that player.
 *
 * The trigger matches any opponent instant or sorcery whose color set includes white or blue
 * ([GameObjectFilter.InstantOrSorcery] narrowed by [GameObjectFilter.withAnyColor]); "that player" is
 * the caster of the triggering spell ([Player.TriggeringPlayer]). It is a mandatory triggered ability
 * (not a "may"), so it always deals 2 damage to that player.
 */
val Mindsparker = card("Mindsparker") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 2
    oracleText = "First strike\n" +
        "Whenever an opponent casts a white or blue instant or sorcery spell, this creature deals 2 damage to that player."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.opponentCasts(
            GameObjectFilter.InstantOrSorcery.withAnyColor(Color.WHITE, Color.BLUE)
        )
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "146"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a94295dc-d078-4f3f-9856-bd0a1899a9ca.jpg?1782713919"
    }
}
