package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hyrax Tower Scout
 * {2}{G}
 * Creature — Human Scout
 * 3/3
 *
 * When this creature enters, untap target creature.
 *
 * A plain self-ETB into [Effects.Untap] — the untap half of `TapUntapEffect`. The slot is the
 * unrestricted [Targets.Creature]: the printed line says "target creature", with no controller
 * clause, so the requirement carries only the `IsCreature` predicate.
 */
val HyraxTowerScout = card("Hyrax Tower Scout") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Scout"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, untap target creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.Untap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Micah Epstein"
        flavorText = "The scouts of Hyrax Tower keep watch at the edge of Setessan territory, protecting the polis from inhuman monsters and enemy armies."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb7f2638-d757-4df6-90b0-b616534dd3a0.jpg"
    }
}
