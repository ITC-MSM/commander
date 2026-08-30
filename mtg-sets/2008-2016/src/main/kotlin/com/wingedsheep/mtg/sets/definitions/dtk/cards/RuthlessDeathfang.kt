package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Ruthless Deathfang
 * {4}{U}{B}
 * Creature — Dragon
 * 4 / 4
 *
 * Flying
 * Whenever you sacrifice a creature, target opponent sacrifices a creature of their choice.
 *
 * The printed article is bare — "a creature", not "another creature" — so this is
 * [Triggers.YouSacrificeA], the per-permanent template that *counts the Dragon sacrificing itself*
 * ([com.wingedsheep.sdk.scripting.TriggerBinding.ANY]), and not [Triggers.YouSacrificeAnother].
 * Per-permanent also means CR 603.2c multiplicity: sacrificing two creatures to one cost fires it
 * twice, which is the whole point of the singular wording.
 *
 * "Sacrifices a creature of their choice" names the player who must sacrifice, so the effect is
 * [Effects.Sacrifice] pointed at the target opponent — the chooser is the sacrificing player, which
 * is what that facade already models. (The bare-imperative sibling `SacrificeOwn` would make *you*
 * sacrifice instead.)
 */
val RuthlessDeathfang = card("Ruthless Deathfang") {
    manaCost = "{4}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever you sacrifice a creature, target opponent sacrifices a creature of their choice."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Creature)
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Sacrifice(GameObjectFilter.Creature, 1, opponent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Filip Burburan"
        flavorText = "\"Bring forth the dead, their skull-grins and rattle-bones. We will feast upon their wailing ghosts.\"\n—Silumgar, translated from Draconic"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93d99f0d-f5d4-4480-ac5e-fe9ff808416e.jpg?1783938570"
    }
}
