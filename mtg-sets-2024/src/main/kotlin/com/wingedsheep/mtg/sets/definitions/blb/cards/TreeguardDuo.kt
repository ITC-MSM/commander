package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Treeguard Duo
 * {3}{G}
 * Creature — Frog Rabbit
 * 3/4
 * When this creature enters, until end of turn, target creature you control gains
 * vigilance and gets +X/+X, where X is the number of creatures you control.
 */
val TreeguardDuo = card("Treeguard Duo") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Frog Rabbit"
    oracleText = "When this creature enters, until end of turn, target creature you control gains vigilance and gets +X/+X, where X is the number of creatures you control."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature you control", TargetCreature(filter = TargetFilter.CreatureYouControl))
        val x = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature)
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, creature)
            .then(Effects.ModifyStats(x, x, creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Mila Pesic"
        flavorText = "\"You shall not pass me,\" croaked the frog. \"Nor me,\" growled the rabbit."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89c8456e-c971-42b7-abf3-ff5ae1320abe.jpg?1721426977"
    }
}
