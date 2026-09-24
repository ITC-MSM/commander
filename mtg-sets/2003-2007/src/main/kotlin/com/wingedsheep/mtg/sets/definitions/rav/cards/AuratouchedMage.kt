package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Auratouched Mage
 * {5}{W}
 * Creature — Human Wizard
 * 3/3
 * When this creature enters, search your library for an Aura card that could enchant it. If this
 * creature is still on the battlefield, put that Aura card onto the battlefield attached to it.
 * Otherwise, reveal the Aura card and put it into your hand. Then shuffle.
 *
 * The search pool is `couldEnchant(Source)` — each Aura card's printed enchant restriction tested
 * against the Mage as it currently exists (so an Aura with "enchant artifact" qualifies if the
 * Mage has been made an artifact, per the ruling). Searching for a card with a stated quality may
 * fail to find (CR 701.23b), so the choice is up to one. The Aura then enters attached to the Mage
 * through `MoveCollectionEffect.attachTo` — the effect names the host, so there is no enchant
 * choice — or, with the Mage gone, goes to hand revealed.
 */
val AuratouchedMage = card("Auratouched Mage") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, search your library for an Aura card that could enchant it. " +
        "If this creature is still on the battlefield, put that Aura card onto the battlefield attached to it. " +
        "Otherwise, reveal the Aura card and put it into your hand. Then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    Zone.LIBRARY,
                    Player.You,
                    GameObjectFilter.Enchantment.withSubtype("Aura").couldEnchant(EntityReference.Source)
                ),
                storeAs = "searchable"
            ),
            SelectFromCollectionEffect(
                from = "searchable",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                storeSelected = "found",
                prompt = "Search for an Aura card that could enchant Auratouched Mage"
            ),
            ConditionalEffect(
                Conditions.SourceInZone(Zone.BATTLEFIELD),
                MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    attachTo = EffectTarget.Self
                ),
                MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                )
            ),
            ShuffleLibraryEffect(),
            // CR 701.23 — the search happened whether or not anything was found.
            EmitLibrarySearchedEventEffect
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/942d6414-11e1-4822-9c5d-e4f96846a85f.jpg?1783943709"
        ruling(
            "2005-10-01",
            "Any Aura card you find must be able to enchant Auratouched Mage as it currently exists, or as " +
                "it most recently existed on the battlefield if it's no longer on the battlefield. If an effect " +
                "has made the Mage an artifact, for example, you could search for an Aura with \"enchant artifact.\""
        )
    }
}
