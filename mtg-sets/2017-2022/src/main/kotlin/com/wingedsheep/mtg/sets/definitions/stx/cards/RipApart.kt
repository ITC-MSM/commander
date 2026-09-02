package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rip Apart — Strixhaven: School of Mages #225 (canonical printing)
 * {R}{W} · Sorcery
 *
 * Choose one —
 * • Rip Apart deals 3 damage to target creature or planeswalker.
 * • Destroy target artifact or enchantment.
 *
 * A choose-one `modal` spell whose two modes each carry their own target: the burn mode binds
 * [Targets.CreatureOrPlaneswalker], the removal mode binds [Targets.ArtifactOrEnchantment] and
 * resolves as a plain [Effects.Destroy].
 */
val RipApart = card("Rip Apart") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Sorcery"
    oracleText =
        "Choose one —\n" +
        "• Rip Apart deals 3 damage to target creature or planeswalker.\n" +
        "• Destroy target artifact or enchantment."

    spell {
        modal {
            mode("Rip Apart deals 3 damage to target creature or planeswalker") {
                val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
                effect = Effects.DealDamage(3, victim)
            }
            mode("Destroy target artifact or enchantment") {
                val permanent = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
                effect = Effects.Destroy(permanent)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Anna Podedworna"
        flavorText = "Torn from history. Torn from memory. Torn from reality."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3b5b510-fd5c-415d-98b0-386e7508f7af.jpg?1783927298"
    }
}
