package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Early Winter
 * {4}{B}
 * Instant
 *
 * Choose one —
 * - Exile target creature.
 * - Target opponent exiles an enchantment they control.
 */
val EarlyWinter = card("Early Winter") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Exile target creature.\n• Target opponent exiles an enchantment they control."

    spell {
        modal(chooseCount = 1) {
            mode("Exile target creature") {
                val t = target("target creature to exile", Targets.Creature)
                effect = Effects.Exile(t)
            }
            mode("Target opponent exiles an enchantment they control") {
                // The opponent is the target; THEY pick which of their enchantments to
                // exile (so hexproof on the enchantment is irrelevant, and the mode is
                // legal even if they control none).
                target("target opponent", Targets.Opponent)
                effect = Effects.Pipeline {
                    val enchantments = gather(
                        CardSource.FromZone(Zone.BATTLEFIELD, Player.ContextPlayer(0), GameObjectFilter.Enchantment),
                        name = "theirEnchantments"
                    )
                    val chosen = chooseExactly(
                        1, from = enchantments,
                        chooser = Chooser.TargetPlayer,
                        prompt = "Choose an enchantment to exile",
                        name = "chosenEnchantment"
                    )
                    exile(chosen, owner = Player.ContextPlayer(0))
                }
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Andrew Mar"
        flavorText = "\"The climate changed and the world suffered.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/0/5030e6ac-211d-4145-8c87-998a8351a467.jpg?1721426407"
    }
}
