package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayLifeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Savra, Queen of the Golgari
 * {2}{B}{G}
 * Legendary Creature — Elf Shaman
 * 2/2
 * Whenever you sacrifice a black creature, you may pay 2 life. If you do, each other player
 * sacrifices a creature of their choice.
 * Whenever you sacrifice a green creature, you may gain 2 life.
 *
 * Both triggers use the bare-article [Triggers.YouSacrificeA] template, which fires once per
 * matching creature sacrificed and counts Savra sacrificing *herself* — she is both black and
 * green, so sacrificing Savra triggers both abilities (her own ruling), and a black-green
 * creature likewise triggers both.
 *
 * "You may pay 2 life. If you do, …" is the [Gate.MayPay] pay-then-payoff over [PayLifeEffect],
 * the shape [com.wingedsheep.mtg.sets.definitions.fin.cards.SeymourFlux] uses; the plain "you may
 * gain 2 life" is the builder's `optional = true`. "Each other player" renders as
 * [Player.EachOpponent], this corpus's standing spelling for it (Syphon Soul), and the sacrificing
 * player picks their own creature — `ForceSacrificeEffect` names who sacrifices, not what.
 */
val SavraQueenOfTheGolgari = card("Savra, Queen of the Golgari") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Shaman"
    power = 2
    toughness = 2
    oracleText = "Whenever you sacrifice a black creature, you may pay 2 life. If you do, each " +
        "other player sacrifices a creature of their choice.\n" +
        "Whenever you sacrifice a green creature, you may gain 2 life."

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Creature.withColor(Color.BLACK))
        effect = GatedEffect(
            gate = Gate.MayPay(PayLifeEffect(2)),
            then = Effects.Sacrifice(
                GameObjectFilter.Creature,
                target = EffectTarget.PlayerRef(Player.EachOpponent)
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Creature.withColor(Color.GREEN))
        optional = true
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "225"
        artist = "Scott M. Fischer"
        flavorText = "\"Nature's most raw beauty is the circle: perfect in its continuance, with " +
            "no break between death and life.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15accf35-a174-4ba5-a633-cc46da848dbe.jpg"
    }
}
