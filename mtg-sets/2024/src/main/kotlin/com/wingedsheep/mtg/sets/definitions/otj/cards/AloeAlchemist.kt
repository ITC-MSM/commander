package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Aloe Alchemist
 * {1}{G}
 * Creature — Plant Warlock
 * 3/2
 * Trample
 * When this card becomes plotted, target creature gets +3/+2 and gains trample until end of turn.
 * Plot {1}{G}
 */
val AloeAlchemist = card("Aloe Alchemist") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Warlock"
    power = 3
    toughness = 2
    oracleText = "Trample\n" +
        "When this card becomes plotted, target creature gets +3/+2 and gains trample until end of turn.\n" +
        "Plot {1}{G} (You may pay {1}{G} and exile this card from your hand. Cast it as a sorcery on a later turn without paying its mana cost. Plot only as a sorcery.)"

    keywords(Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.plot("{1}{G}"))

    triggeredAbility {
        trigger = Triggers.BecomesPlotted
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(power = 3, toughness = 2, target = t),
            Effects.GrantKeyword(Keyword.TRAMPLE, target = t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "152"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69f2f632-b6cc-4092-acd5-a6b152e90488.jpg?1712355875"

        ruling("2024-04-12", "Plot abilities are written \"Plot [cost],\" which means \"Any time you have priority during your main phase while the stack is empty, you may pay [cost] and exile this card from your hand. It becomes plotted.\"")
        ruling("2024-04-12", "Aloe Alchemist's triggered ability triggers when it becomes plotted, not when you cast it from exile. You choose the target as the ability is put onto the stack.")
        ruling("2024-04-12", "You can't cast a plotted card on the same turn it became plotted. On any future turn, you may cast that card from exile without paying its mana cost during your main phase while the stack is empty.")
    }
}
