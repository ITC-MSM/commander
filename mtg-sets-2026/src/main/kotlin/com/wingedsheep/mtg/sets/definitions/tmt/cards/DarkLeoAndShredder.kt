package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.sneak
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dark Leo & Shredder
 * {W}{B}
 * Legendary Creature — Mutant Ninja Turtle Human
 * 1/3
 *
 * Sneak {W}{B}
 * Attacking Ninjas you control have deathtouch.
 * Whenever Dark Leo & Shredder deal combat damage to a player, create a 1/1 black Ninja creature
 * token. Then if you control five or more Ninjas, that player loses half their life, rounded up.
 *
 * The deathtouch clause is a continuous static [GrantKeyword] over the group of attacking Ninjas
 * you control — only creatures attack, so the group is creatures with subtype Ninja that you
 * control and that are attacking, re-evaluated each application (same shape as Bone-Cairn Butcher).
 *
 * The combat-damage trigger creates the token first, then runs a [ConditionalEffect] gated on
 * [Conditions.YouControlAtLeast] 5 Ninjas: because the token is itself a Ninja and is created
 * before the check, it counts toward the five ("create … Then if you control five or more"). When
 * the gate holds, the damaged player ([Player.TriggeringPlayer]) loses half their own life rounded
 * up ([Effects.LoseHalfLife] with both `target` and `lifePlayer` pointed at that player).
 */
val DarkLeoAndShredder = card("Dark Leo & Shredder") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Mutant Ninja Turtle Human"
    oracleText = "Sneak {W}{B}\nAttacking Ninjas you control have deathtouch.\nWhenever Dark Leo & Shredder deal combat damage to a player, create a 1/1 black Ninja creature token. Then if you control five or more Ninjas, that player loses half their life, rounded up."
    power = 1
    toughness = 3

    sneak("{W}{B}")

    staticAbility {
        ability = GrantKeyword(
            Keyword.DEATHTOUCH,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Ninja").youControl()).attacking()
        )
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Ninja"),
                imageUri = "https://cards.scryfall.io/normal/front/a/7/a7b76498-d696-40d1-b7c7-91657525b44f.jpg?1771590477"
            ),
            ConditionalEffect(
                condition = Conditions.YouControlAtLeast(5, GameObjectFilter.Creature.withSubtype("Ninja")),
                effect = Effects.LoseHalfLife(
                    roundUp = true,
                    target = EffectTarget.PlayerRef(Player.TriggeringPlayer),
                    lifePlayer = Player.TriggeringPlayer
                )
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "142"
        artist = "Thomas Chamberlain-Keen"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bab93474-2f98-49a1-874f-b794bf81bd0c.jpg?1769006250"
    }
}
