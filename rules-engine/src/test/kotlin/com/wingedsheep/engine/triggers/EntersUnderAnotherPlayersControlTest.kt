package com.wingedsheep.engine.triggers

import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * "Whenever another creature **you control** enters" must read the entering permanent's
 * *controller*, not its owner.
 *
 * Reanimation out of a graveyard under someone else's control (Virtue of Persistence,
 * Shark Shredder — `Effects.PutOntoBattlefieldUnderYourControl`) is the case where the two differ
 * while the permanent is on the battlefield: the card is still owned by the player whose graveyard
 * it came from, but the reanimator controls it. `ZoneChangeEvent` carries only `ownerId` (the
 * `lastKnown` snapshot is populated for battlefield *exits* only), so a controller predicate that
 * fell back to `ownerId` on an entry fired the owner's "creature you control enters" payoffs —
 * Virulent Emissary, Haliya Guided by Light, Essence Channeler downstream of them — off a creature
 * their opponent had just stolen out of their graveyard.
 */
class EntersUnderAnotherPlayersControlTest : FunSpec({

    // "Whenever another creature you control enters, you gain 1 life." (Virulent Emissary's shape.)
    val observer = card("Life Observer") {
        manaCost = "{0}"
        typeLine = "Creature — Human"
        power = 0
        toughness = 1
        triggeredAbility {
            trigger = Triggers.OtherCreatureEnters
            effect = Effects.GainLife(1)
        }
    }

    val reanimated = CardDefinition.creature(
        name = "Reanimated Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = emptySet(),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(observer, reanimated))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Mountain" to 20))
        return driver
    }

    /** Put [cardName] onto [controller]'s battlefield while [owner] still owns the card. */
    fun GameTestDriver.putOnBattlefieldOwnedBy(
        controller: EntityId,
        owner: EntityId,
        cardName: String
    ): EntityId {
        val id = putCreatureOnBattlefield(controller, cardName)
        replaceState(
            state.updateEntity(id) { container ->
                val card = container.get<CardComponent>()!!
                container.with(card.copy(ownerId = owner))
                    .with(OwnerComponent(owner))
                    .with(ControllerComponent(controller))
            }
        )
        return id
    }

    fun lifeObserverTriggers(driver: GameTestDriver, event: ZoneChangeEvent) =
        TriggerDetector(driver.cardRegistry)
            .detectTriggers(driver.state, listOf(event))
            .filter { it.sourceName == "Life Observer" }

    context("'another creature you control enters' when owner != controller") {

        /**
         * GIVEN both players control a "whenever another creature you control enters" observer,
         *   AND player 2 reanimates a creature card player 1 owns, under player 2's control,
         * WHEN the enter-the-battlefield event is detected,
         * THEN only player 2's observer triggers — the owner's does not.
         */
        test("a creature reanimated under the opponent's control triggers the reanimator's payoff, not the owner's") {
            val driver = createDriver()
            driver.putCreatureOnBattlefield(driver.player1, "Life Observer")
            driver.putCreatureOnBattlefield(driver.player2, "Life Observer")

            val bear = driver.putOnBattlefieldOwnedBy(
                controller = driver.player2,
                owner = driver.player1,
                cardName = "Reanimated Bear"
            )

            val entered = ZoneChangeEvent(
                entityId = bear,
                entityName = "Reanimated Bear",
                fromZone = Zone.GRAVEYARD,
                toZone = Zone.BATTLEFIELD,
                // The event carries the card's OWNER, as ZoneTransitionService always does.
                ownerId = driver.player1
            )

            val triggers = lifeObserverTriggers(driver, entered)

            triggers shouldHaveSize 1
            triggers.single().controllerId shouldBe driver.player2
        }

        /**
         * The ordinary case must be unchanged: a creature entering under its own owner's control
         * still fires that player's observer and not the opponent's.
         */
        test("a creature entering under its owner's control still triggers only that player's payoff") {
            val driver = createDriver()
            driver.putCreatureOnBattlefield(driver.player1, "Life Observer")
            driver.putCreatureOnBattlefield(driver.player2, "Life Observer")

            val bear = driver.putCreatureOnBattlefield(driver.player1, "Reanimated Bear")

            val entered = ZoneChangeEvent(
                entityId = bear,
                entityName = "Reanimated Bear",
                fromZone = Zone.GRAVEYARD,
                toZone = Zone.BATTLEFIELD,
                ownerId = driver.player1
            )

            val triggers = lifeObserverTriggers(driver, entered)

            triggers shouldHaveSize 1
            triggers.single().controllerId shouldBe driver.player1
        }
    }
})
