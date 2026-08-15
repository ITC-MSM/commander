package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Sidequest: Play Blitzball // World Champion, Celestial Weapon — Final Fantasy #158
 * {2}{R} · Enchantment // Legendary Artifact — Equipment
 *
 * Front — Sidequest: Play Blitzball:
 *   At the beginning of combat on your turn, target creature you control gets +2/+0 until end of turn.
 *   At the end of combat on your turn, if a player was dealt 6 or more combat damage this turn,
 *   transform this enchantment, then attach it to a creature you control.
 *
 * Back — World Champion, Celestial Weapon:
 *   Double Overdrive — Equipped creature gets +2/+0 and has double strike.
 *   Equip {3}
 *
 * The end-of-combat ability is an intervening-"if": the 6-combat-damage check gates the whole
 * trigger, and "a player" is existential over every player (including you) — modelled by
 * [Conditions.aPlayerWasDealtCombatDamageThisTurnAtLeast], which reads a per-player combat-damage
 * running total rather than summing every player together. On resolution it transforms (turning
 * this permanent into the Equipment), then chooses a creature you control to attach to. The attach
 * is a resolution-time choice, NOT a target: the pipeline's `selectTarget` auto-picks a lone
 * creature, pauses to choose among several, and — crucially — no-ops when you control none, so the
 * enchantment still transforms even if you have no creature to hold the weapon.
 */
private val WorldChampionCelestialWeapon = card("World Champion, Celestial Weapon") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Double Overdrive — Equipped creature gets +2/+0 and has double strike.\n" +
        "Equip {3}"

    // Double Overdrive — Equipped creature gets +2/+0 and has double strike.
    staticAbility {
        ability = ModifyStats(2, 0)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Ittoku"
        flavorText = "\"Sorry for making you wait, Yuna. I had some promises to keep, ya?\""
        imageUri = "https://cards.scryfall.io/normal/back/3/1/31e2ad37-73cf-4858-8a3a-fc1165cd21a7.jpg?1782686482"
    }
}

private val SidequestPlayBlitzballFront = card("Sidequest: Play Blitzball") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "At the beginning of combat on your turn, target creature you control gets +2/+0 " +
        "until end of turn.\n" +
        "At the end of combat on your turn, if a player was dealt 6 or more combat damage this " +
        "turn, transform this enchantment, then attach it to a creature you control."

    // At the beginning of combat on your turn, target creature you control gets +2/+0 until end of turn.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.ModifyStats(2, 0, t)
    }

    // At the end of combat on your turn, if a player was dealt 6 or more combat damage this turn,
    // transform this enchantment, then attach it to a creature you control.
    triggeredAbility {
        trigger = Triggers.YourEndOfCombat
        triggerCondition = Conditions.aPlayerWasDealtCombatDamageThisTurnAtLeast(6)
        effect = Effects.Pipeline {
            // Transform this enchantment (it becomes World Champion, Celestial Weapon)...
            run(TransformEffect(EffectTarget.Self))
            // ...then attach it to a creature you control. Chosen at resolution, not targeted:
            // auto-selects a lone creature, pauses to choose among several, no-ops with none.
            val host = selectTarget(
                TargetCreature(filter = TargetFilter.Creature.youControl()),
                name = "host",
            )
            ifNotEmpty(host) {
                run(Effects.AttachEquipment(EffectTarget.PipelineTarget(host.key)))
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Ittoku"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31e2ad37-73cf-4858-8a3a-fc1165cd21a7.jpg?1782686482"
    }
}

val SidequestPlayBlitzball: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = SidequestPlayBlitzballFront,
    backFace = WorldChampionCelestialWeapon,
)
