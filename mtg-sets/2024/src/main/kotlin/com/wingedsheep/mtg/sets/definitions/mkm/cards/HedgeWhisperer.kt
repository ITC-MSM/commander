package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Hedge Whisperer — Murders at Karlov Manor #165
 * {G} · Creature — Elf Druid Detective · 0/3 · Uncommon
 *
 * The animation keeps the target's existing Land type and lasts only while Hedge Whisperer remains
 * tapped. If the Whisperer leaves before the ability resolves, the source-tapped duration is already
 * false and the animation does not begin, matching the card's ruling.
 */
val HedgeWhisperer = card("Hedge Whisperer") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid Detective"
    oracleText = "You may choose not to untap this creature during your untap step.\n" +
        "{3}{G}, {T}, Collect evidence 4: Target land you control becomes a 5/5 green Plant Boar " +
        "creature with haste for as long as this creature remains tapped. It's still a land. " +
        "Activate only as a sorcery. (To collect evidence 4, exile cards with total mana value 4 " +
        "or greater from your graveyard.)"
    power = 0
    toughness = 3

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{G}"), Costs.Tap, Costs.CollectEvidence(4))
        timing = TimingRule.SorcerySpeed
        val land = target(
            "target land you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.youControl())),
        )
        effect = Effects.BecomeCreature(
            target = land,
            power = 5,
            toughness = 5,
            keywords = setOf(Keyword.HASTE),
            creatureTypes = setOf("Plant", "Boar"),
            colors = setOf("G"),
            duration = Duration.WhileSourceTapped("Hedge Whisperer"),
        )
        description = "Target land you control becomes a 5/5 green Plant Boar creature with haste " +
            "for as long as Hedge Whisperer remains tapped. It's still a land. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/4627adcd-ace7-4777-a7e6-fc80ac6b9dfe.jpg?1783912865"
        ruling("2024-02-02", "If Hedge Whisperer leaves the battlefield before its activated ability resolves, the target land won't become a creature at all.")
        ruling("2024-02-02", "If you can't exile enough cards to meet or exceed the required mana value, you can't choose to collect evidence at all.")
        ruling("2024-02-02", "Once you've announced that you're casting a spell, players can't take actions until you've finished doing so. Notably, opponents can't try to remove cards from your graveyard to stop you from collecting evidence.")
    }
}
