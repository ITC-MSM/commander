package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Necromaster Dragon
 * {3}{U}{B}
 * Creature — Dragon
 * 4 / 4
 *
 * Flying
 * Whenever this creature deals combat damage to a player, you may pay {2}. If you do, create a 2/2
 * black Zombie creature token and each opponent mills two cards.
 *
 * "You may pay {2}. If you do, …" is [MayPayManaEffect] — one gate whose consequence runs only when
 * the mana is actually paid, rather than an `optional` trigger wrapped around a payment. The
 * consequence is a single sentence with two halves, so both live in one [Effects.Composite].
 *
 * The mill half says "each opponent", which is the *player* argument to the mill recipe and not a
 * loop around it: `Patterns.Library.mill(2, EffectTarget.PlayerRef(Player.EachOpponent))` gathers
 * and moves each opponent's top two in one pipeline. Wrapping it in a `ForEachPlayer` would say the
 * same thing twice.
 */
val NecromasterDragon = card("Necromaster Dragon") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever this creature deals combat damage to a player, you may pay {2}. If you do, create a 2/2 black Zombie creature token and each opponent mills two cards."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.Composite(
                Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.BLACK),
                    creatureTypes = setOf("Zombie")
                ),
                Patterns.Library.mill(2, EffectTarget.PlayerRef(Player.EachOpponent))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "226"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/2/5/2574aaf5-8397-4a88-b047-1b4dbb176930.jpg?1783938571"
    }
}
