package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Stormkeld Vanguard // Bear Down
 * {4}{G}{G}
 * Creature — Giant Warrior
 * 6/7
 *
 * This creature can't be blocked by creatures with power 2 or less.
 *
 * Adventure: Bear Down — {1}{G}, Sorcery — Adventure
 * Destroy target artifact or enchantment.
 *
 * The evasion is a permanent [CantBeBlockedBy] static keyed on blocker power via
 * [GameObjectFilter.Creature.powerAtMost]. (CR 715: Adventure cards. Casting the Adventure exiles
 * the card on resolution and lets the caster cast it as the creature spell while it remains in exile.)
 */
val StormkeldVanguard = card("Stormkeld Vanguard") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Giant Warrior"
    oracleText = "This creature can't be blocked by creatures with power 2 or less."
    power = 6
    toughness = 7

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    adventure("Bear Down") {
        manaCost = "{1}{G}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Destroy target artifact or enchantment. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.ArtifactOrEnchantment)
            effect = Effects.Destroy(t)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "187"
        artist = "Aldo Domínguez"
        flavorText = "He rides like thunder and strikes like lightning."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bacb1fe5-0adf-461f-b698-9d09a8728c63.jpg?1783915077"
    }
}
