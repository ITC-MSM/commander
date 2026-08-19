package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cloudthresher
 * {2}{G}{G}{G}{G}
 * Creature — Elemental
 * 7/7
 * Flash
 * Reach
 * When this creature enters, it deals 2 damage to each creature with flying and each player.
 * Evoke {2}{G}{G}
 *
 * The sweep is untargeted: a `ForEachInGroup` over the fliers plus a `ForEachPlayer` over every
 * player, both dealing damage from Cloudthresher itself. Note it hits *each* player, its
 * controller included.
 */
val Cloudthresher = card("Cloudthresher") {
    manaCost = "{2}{G}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 7
    toughness = 7
    oracleText = "Flash\nReach\nWhen this creature enters, it deals 2 damage to each creature with " +
        "flying and each player.\nEvoke {2}{G}{G} (You may cast this spell for its evoke cost. If " +
        "you do, it's sacrificed when it enters.)"

    keywords(Keyword.FLASH, Keyword.REACH)

    evoke = "{2}{G}{G}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING)),
                DealDamageEffect(2, EffectTarget.Self)
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(DealDamageEffect(2, EffectTarget.Controller))
            )
        )
        description = "it deals 2 damage to each creature with flying and each player."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "202"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c759c561-829d-483d-a433-fe1213f20236.jpg?1783942865"
    }
}
