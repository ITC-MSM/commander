package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gaius van Baelsar
 * {2}{B}{B}
 * Legendary Creature — Human Soldier
 * 3/2
 *
 * When Gaius van Baelsar enters, choose one —
 * • Each player sacrifices a creature token of their choice.
 * • Each player sacrifices a nontoken creature of their choice.
 * • Each player sacrifices an enchantment of their choice.
 *
 * A modal ETB trigger ([ModalEffect.chooseOne]). Each mode is a symmetric edict over
 * [Player.Each]: every player sacrifices one matching permanent of their own choosing, so no
 * targeting is involved (the choice is made by each player as the ability resolves).
 */
val GaiusVanBaelsar = card("Gaius van Baelsar") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Soldier"
    power = 3
    toughness = 2
    oracleText = "When Gaius van Baelsar enters, choose one —\n" +
        "• Each player sacrifices a creature token of their choice.\n" +
        "• Each player sacrifices a nontoken creature of their choice.\n" +
        "• Each player sacrifices an enchantment of their choice."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.Sacrifice(
                    GameObjectFilter.Creature.token(),
                    count = 1,
                    target = EffectTarget.PlayerRef(Player.Each),
                ),
                "Each player sacrifices a creature token of their choice",
            ),
            Mode.noTarget(
                Effects.Sacrifice(
                    GameObjectFilter.Creature.nontoken(),
                    count = 1,
                    target = EffectTarget.PlayerRef(Player.Each),
                ),
                "Each player sacrifices a nontoken creature of their choice",
            ),
            Mode.noTarget(
                Effects.Sacrifice(
                    GameObjectFilter.Enchantment,
                    count = 1,
                    target = EffectTarget.PlayerRef(Player.Each),
                ),
                "Each player sacrifices an enchantment of their choice",
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Nino Is"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4ee8ba5-6a79-4652-b2a4-a3dae804bc28.jpg?1748706145"
    }
}
