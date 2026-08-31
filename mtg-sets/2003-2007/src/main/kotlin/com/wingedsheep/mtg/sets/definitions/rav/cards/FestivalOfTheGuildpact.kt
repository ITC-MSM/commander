package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Festival of the Guildpact
 * {X}{W}
 * Instant
 *
 * Prevent the next X damage that would be dealt to you this turn.
 * Draw a card.
 *
 * The shield is untargeted and always protects the caster, so the prevention takes
 * [EffectTarget.Controller] rather than a chosen target. X is the value announced as the spell was
 * cast, read at resolution by [DynamicAmount.XValue].
 *
 * Both halves happen on resolution, in printed order: per the ruling the card is drawn when the
 * spell resolves, not later when damage is actually prevented, so this is a plain sequence and not
 * a rider on the shield.
 */
val FestivalOfTheGuildpact = card("Festival of the Guildpact") {
    manaCost = "{X}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent the next X damage that would be dealt to you this turn.\nDraw a card."

    spell {
        effect = Effects.PreventNextDamage(DynamicAmount.XValue, EffectTarget.Controller)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Alex Horley-Orlandelli"
        flavorText = "Everyone knows that violence at the Festival brings the worst of luck, so for at least one day a year arms are laid down in favor of brimming goblets."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d15fc49c-07a9-4e55-a977-24f7b7c2df1a.jpg?1783943700"
        ruling("2005-10-01", "You draw a card when Festival of the Guildpact resolves, not when damage is prevented.")
    }
}
