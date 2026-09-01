package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Seshiro the Anointed
 * {4}{G}{G}
 * Legendary Creature — Snake Monk
 * 3/4
 *
 * Other Snake creatures you control get +2/+2.
 * Whenever a Snake you control deals combat damage to a player, you may draw a card.
 *
 * Two different Snake filters, because the two lines print two different nouns. The lord says
 * "Snake **creatures**", so it is [GameObjectFilter.Creature]`.withSubtype(SNAKE).youControl()` with
 * `excludeSelf` for the printed "Other"; the trigger says a bare "a Snake you control", which is any
 * *permanent* with the subtype, so that one is [GameObjectFilter.Permanent].
 *
 * The trigger is a [GrantTriggeredAbility]: each of your Snakes carries its own copy of "whenever
 * *this* deals combat damage to a player", so the granted ability keeps the default SELF binding and
 * the source of the damage is the Snake that bears it. The printed "you may" is an explicit
 * [Gate.MayDecide] around the draw.
 */
val SeshiroTheAnointed = card("Seshiro the Anointed") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Snake Monk"
    power = 3
    toughness = 4
    oracleText = "Other Snake creatures you control get +2/+2.\n" +
        "Whenever a Snake you control deals combat damage to a player, you may draw a card."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 2,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.SNAKE).youControl(),
                excludeSelf = true
            )
        )
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = GatedEffect(
                    gate = Gate.MayDecide(),
                    then = Effects.DrawCards(1)
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SNAKE).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Daren Bader"
        flavorText = "His family was the first to reach out to the human monks. He soon knew as " +
            "many koans as he did blade strikes."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/3823e561-5a0a-4020-9322-a2b481499807.jpg?1783944282"
    }
}
