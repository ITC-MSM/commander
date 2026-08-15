package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Cut In
 * {3}{R}
 * Sorcery
 *
 * Cut In deals 4 damage to target creature.
 * Create a Young Hero Role token attached to up to one target creature you control.
 *
 * Two independent targets: the mandatory "target creature" that takes 4 damage (any creature), and
 * the optional ("up to one") creature you control that gains the Young Hero Role. The Young Hero
 * Role token carries the attack trigger ("Whenever this creature attacks, if its toughness is 3 or
 * less, put a +1/+1 counter on it."); [Effects.CreateRoleToken] handles the "replace an existing
 * Role" rule. Same optional-role shape as Eriette's Whisper.
 */
val CutIn = card("Cut In") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Cut In deals 4 damage to target creature.\n" +
        "Create a Young Hero Role token attached to up to one target creature you control. " +
        "(If you control another Role on it, put that one into the graveyard. Enchanted creature has " +
        "\"Whenever this creature attacks, if its toughness is 3 or less, put a +1/+1 counter on it.\")"

    spell {
        val damaged = target("target creature", TargetCreature())
        val roleTarget = target(
            "creature you control",
            TargetCreature(optional = true, filter = TargetFilter.CreatureYouControl)
        )
        effect = Effects.Composite(
            Effects.DealDamage(4, damaged),
            Effects.CreateRoleToken("Young Hero Role", roleTarget)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ea4d40a-7657-4ff8-9fc2-915b99432275.jpg?1783915097"
    }
}
