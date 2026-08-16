package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Irreverent Revelers
 * {2}{R}
 * Creature — Satyr
 * 2/2
 * When this creature enters, choose one —
 * • Destroy target artifact.
 * • This creature gains haste until end of turn.
 *
 * A modal ETB via [ModalEffect.chooseOne]. Only the first mode targets; the second grants haste to
 * the source permanent itself ([EffectTarget.Self]) for the default end-of-turn duration.
 */
val IrreverentRevelers = card("Irreverent Revelers") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Satyr"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, choose one —\n• Destroy target artifact.\n• This creature gains haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                target = Targets.Artifact,
                description = "Destroy target artifact."
            ),
            Mode.noTarget(
                effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self),
                description = "This creature gains haste until end of turn."
            )
        )
        description = "When this creature enters, choose one — Destroy target artifact. • This creature gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Milivoj Ćeran"
        flavorText = "To some satyrs, only blasphemy is sacred."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55405dca-3555-45ff-be1c-e276fe1a0c2e.jpg?1783931550"
    }
}
