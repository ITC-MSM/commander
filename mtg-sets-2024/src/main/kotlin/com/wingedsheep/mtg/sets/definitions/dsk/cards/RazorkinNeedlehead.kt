package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Razorkin Needlehead
 * {R}{R}
 * Creature — Human Assassin
 * 2/2
 * This creature has first strike during your turn.
 * Whenever an opponent draws a card, this creature deals 1 damage to them.
 */
val RazorkinNeedlehead = card("Razorkin Needlehead") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Assassin"
    power = 2
    toughness = 2
    oracleText = "This creature has first strike during your turn.\n" +
        "Whenever an opponent draws a card, this creature deals 1 damage to them."

    // First strike only while it's your turn — a conditionally-active continuous static ability.
    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.source())
        condition = Conditions.IsYourTurn
    }

    // Whenever an opponent draws a card, this creature deals 1 damage to that player.
    triggeredAbility {
        trigger = Triggers.OpponentDraws
        effect = Effects.DealDamage(
            1,
            EffectTarget.PlayerRef(Player.TriggeringPlayer),
            damageSource = EffectTarget.Self,
        )
        description = "Whenever an opponent draws a card, this creature deals 1 damage to them."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "153"
        artist = "Riccardo Federici"
        flavorText = "\"Stitch your eyes, stitch your skin, stitch your mouth into a grin.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc73b963-23c0-46d2-853a-34a8b463994e.jpg?1726286422"
    }
}
