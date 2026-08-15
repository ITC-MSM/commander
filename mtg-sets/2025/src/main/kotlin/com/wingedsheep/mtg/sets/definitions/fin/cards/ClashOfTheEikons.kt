package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Clash of the Eikons
 * {G}
 * Sorcery
 * Choose one or more —
 * • Target creature you control fights target creature an opponent controls.
 * • Remove a lore counter from target Saga you control. (Removing lore counters doesn't cause
 *   chapter abilities to trigger.)
 * • Put a lore counter on target Saga you control.
 *
 * A choose-one-or-more modal spell ([modal] with `chooseCount = 3, minChooseCount = 1`), each mode
 * carrying its own independent targets. Adding a lore counter advances the targeted Saga normally
 * (its chapter ability triggers, per CR 714); removing one never triggers a chapter. Both reuse the
 * generic [Effects.AddCounters] / [Effects.RemoveCounters] over the [Counters.LORE] type.
 */
val ClashOfTheEikons = card("Clash of the Eikons") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose one or more —\n" +
        "• Target creature you control fights target creature an opponent controls.\n" +
        "• Remove a lore counter from target Saga you control. (Removing lore counters doesn't " +
        "cause chapter abilities to trigger.)\n" +
        "• Put a lore counter on target Saga you control."

    spell {
        modal(chooseCount = 3, minChooseCount = 1) {
            mode("Target creature you control fights target creature an opponent controls") {
                val yourCreature = target(
                    "creature you control",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.youControl()))
                )
                val theirCreature = target(
                    "creature an opponent controls",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.opponentControls()))
                )
                effect = Effects.Fight(yourCreature, theirCreature)
            }
            mode("Remove a lore counter from target Saga you control") {
                val saga = target(
                    "Saga you control",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Enchantment.withSubtype(Subtype.SAGA).youControl()))
                )
                effect = Effects.RemoveCounters(Counters.LORE, 1, saga)
            }
            mode("Put a lore counter on target Saga you control") {
                val saga = target(
                    "Saga you control",
                    TargetPermanent(filter = TargetFilter(GameObjectFilter.Enchantment.withSubtype(Subtype.SAGA).youControl()))
                )
                effect = Effects.AddCounters(Counters.LORE, 1, saga)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Gal Or"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75c18134-f517-4a68-8640-0426b3cd4f6c.jpg?1748706434"
    }
}
