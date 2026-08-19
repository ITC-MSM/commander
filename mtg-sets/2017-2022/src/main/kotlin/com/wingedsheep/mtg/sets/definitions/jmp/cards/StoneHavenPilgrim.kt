package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stone Haven Pilgrim
 * {1}{W}
 * Creature — Kor Cleric
 * 2/2
 *
 * Whenever this creature attacks, if you control an artifact or enchantment, this creature gets +1/+1 and gains lifelink until end of turn.
 */
val StoneHavenPilgrim = card("Stone Haven Pilgrim") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Cleric"
    oracleText = "Whenever this creature attacks, if you control an artifact or enchantment, this creature gets +1/+1 and gains lifelink until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        interveningIf = Conditions.YouControl(GameObjectFilter.ArtifactOrEnchantment)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self))
        description = "Whenever this creature attacks, if you control an artifact or enchantment, " +
            "this creature gets +1/+1 and gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Cristi Balanescu"
        flavorText = "The persistence to overcome obstacles is the most esteemed of kor virtues."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cd3287d-e4d8-4670-a2dd-b683055ae4b9.jpg?1783930508"
    }
}
