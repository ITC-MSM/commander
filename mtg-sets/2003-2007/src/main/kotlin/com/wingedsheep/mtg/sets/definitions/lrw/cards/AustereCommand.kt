package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Austere Command
 * {4}{W}{W}
 * Sorcery
 * Choose two —
 * • Destroy all artifacts.
 * • Destroy all enchantments.
 * • Destroy all creatures with mana value 3 or less.
 * • Destroy all creatures with mana value 4 or greater.
 *
 * The four modes are four independent `Effects.DestroyAll` gathers, so each resolves against the
 * battlefield as it stands when *that* mode runs — the sequential resolution the 2020-11-10 rulings
 * describe, not one simultaneous sweep. That's why an artifact creature caught by both creature
 * modes has to be regenerated twice, and why a card exiled "until ~ leaves the battlefield" can
 * return between modes and be destroyed by a later one.
 */
val AustereCommand = card("Austere Command") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose two —\n" +
        "• Destroy all artifacts.\n" +
        "• Destroy all enchantments.\n" +
        "• Destroy all creatures with mana value 3 or less.\n" +
        "• Destroy all creatures with mana value 4 or greater."

    spell {
        modal(chooseCount = 2) {
            mode("Destroy all artifacts") {
                effect = Effects.DestroyAll(GameObjectFilter.Artifact)
            }
            mode("Destroy all enchantments") {
                effect = Effects.DestroyAll(GameObjectFilter.Enchantment)
            }
            mode("Destroy all creatures with mana value 3 or less") {
                effect = Effects.DestroyAll(GameObjectFilter.Creature.manaValueAtMost(3))
            }
            mode("Destroy all creatures with mana value 4 or greater") {
                effect = Effects.DestroyAll(GameObjectFilter.Creature.manaValueAtLeast(4))
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "3"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ee73fe8-d52b-43bb-ab91-5545192be676.jpg?1783942918"
        ruling("2020-11-10", "Each of the chosen modes happens sequentially. If a permanent has an ability that triggers whenever it or another permanent is destroyed, it will see permanents destroyed at the same time as it or before it, but not permanents destroyed by later modes.")
        ruling("2020-11-10", "If the first and last modes are chosen, an artifact creature with mana value 4 or greater will have to be regenerated twice to survive. This is because the modes happen sequentially, and the regeneration \"shield\" is used up by the first one. The same is true with any other combination of modes that covers one permanent twice.")
    }
}
