package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

val MindleechMass = card("Mindleech Mass") {
    manaCost = "{5}{U}{B}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Horror"
    oracleText = "Trample\nWhenever this creature deals combat damage to a player, you may look at that player's hand. If you do, you may cast a spell from among those cards without paying its mana cost."
    power = 6
    toughness = 6

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        optional = true
        effect = Effects.Composite(
            LookAtTargetHandEffect(EffectTarget.PlayerRef(Player.TriggeringPlayer)),
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.HAND, Player.TriggeringPlayer),
                storeAs = "opponentsHand"
            ),
            SelectFromCollectionEffect(
                from = "opponentsHand",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter.Nonland,
                storeSelected = "spellToCast",
                showAllCards = true,
                prompt = "You may cast a nonland card without paying its mana cost",
                selectedLabel = "Cast for free"
            ),
            Effects.CastFromCollectionWithoutPayingCost("spellToCast")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "215"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cd1ace7-d4fe-4f96-9434-7ab1442bf36f.jpg?1783943617"
        ruling("2024-01-12", "If you cast a permanent spell this way, it will enter the battlefield under your control when it resolves. If you cast an instant or sorcery spell this way, that card will be put into its owner's graveyard when it resolves.")
        ruling("2024-01-12", "The spell you cast via the triggered ability is cast as part of the resolution of that ability. Timing restrictions based on the card's type are ignored. Other restrictions, such as \"Cast [this spell] only during an opponent's turn,\" are not.")
        ruling("2024-01-12", "If you cast a spell without paying its mana cost, you can't choose to cast it for any alternative costs, such as overload costs. You can pay additional costs, such as kicker costs. If the spell has any mandatory additional costs, you must pay those.")
    }
}
