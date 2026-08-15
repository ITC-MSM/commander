package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.PoeticIngenuity
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Poetic Ingenuity (LCI #161) — {2}{R} Enchantment.
 *
 *  - Whenever one or more Dinosaurs you control attack, create that many Treasure tokens.
 *  - Whenever you cast an artifact spell, create a 3/1 red Dinosaur creature token.
 *    This ability triggers only once each turn.
 *
 * Both abilities compose existing primitives (group-attack trigger [Triggers.YouAttackWithFilter]
 * with an [DynamicAmount.AggregateBattlefield] attacker count feeding [Effects.CreateTreasure];
 * a `youCastSpell(Artifact)` trigger with `oncePerTurn = true` minting a [Effects.CreateToken]),
 * so this scenario test is the behavioural gate.
 */
class PoeticIngenuityScenarioTest : FunSpec({

    val projector = StateProjector()

    // A plain non-legendary Dinosaur so multiple copies can attack together (no legend rule).
    val testDinosaur = CardDefinition.creature(
        name = "Test Dinosaur",
        manaCost = ManaCost.parse("{G}"),
        subtypes = setOf(Subtype("Dinosaur")),
        power = 2,
        toughness = 2,
    )

    // A cheap non-creature artifact, so casting it isolates the Dinosaur *creature* token count.
    val testArtifact = CardDefinition(
        name = "Test Artifact",
        manaCost = ManaCost.parse("{1}"),
        typeLine = TypeLine.artifact(),
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        // Treasure is a registry-backed predefined token; register it so CreateTreasure can mint one.
        driver.registerCards(TestCards.all + PredefinedTokens.Treasure)
        driver.registerCard(PoeticIngenuity)
        driver.registerCard(testDinosaur)
        driver.registerCard(testArtifact)
        return driver
    }

    /** Resolve the stack to completion (no player decisions are expected). */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (!isPaused && stackSize > 0 && guard++ < 50) bothPass()
    }

    fun GameTestDriver.treasureCount(player: EntityId): Int =
        getPermanents(player).count { getCardName(it) == "Treasure" }

    fun GameTestDriver.dinosaurTokenCount(player: EntityId): Int =
        getPermanents(player).count { getCardName(it) == "Dinosaur Token" }

    /** From the controller's main phase, cross the opponent's turn and land on the next own main. */
    fun GameTestDriver.advanceToMyNextMain(me: EntityId) {
        passPriorityUntil(Step.END, maxPasses = 300)        // my end step (cleanup resets oncePerTurn)
        passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)  // opponent's main
        passPriorityUntil(Step.END, maxPasses = 300)        // opponent's end step
        passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)  // my next main
        activePlayer shouldBe me
    }

    test("two attacking Dinosaurs create two Treasures; a non-Dinosaur attacker doesn't count") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Poetic Ingenuity")
        val dino1 = driver.putCreatureOnBattlefield(player, "Test Dinosaur")
        val dino2 = driver.putCreatureOnBattlefield(player, "Test Dinosaur")
        val lion = driver.putCreatureOnBattlefield(player, "Savannah Lions") // a non-Dinosaur (Cat)
        driver.removeSummoningSickness(dino1)
        driver.removeSummoningSickness(dino2)
        driver.removeSummoningSickness(lion)

        driver.treasureCount(player) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(dino1, dino2, lion), opponent)
        driver.resolveStack()

        withClue("two attacking Dinosaurs → two Treasures (the attacking Cat is not counted)") {
            driver.treasureCount(player) shouldBe 2
        }
    }

    test("a single attacking Dinosaur creates exactly one Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Poetic Ingenuity")
        val dino = driver.putCreatureOnBattlefield(player, "Test Dinosaur")
        driver.removeSummoningSickness(dino)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(dino), opponent)
        driver.resolveStack()

        withClue("one attacking Dinosaur → one Treasure") {
            driver.treasureCount(player) shouldBe 1
        }
    }

    test("casting an artifact spell creates a 3/1 Dinosaur creature token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Poetic Ingenuity")
        driver.dinosaurTokenCount(player) shouldBe 0

        val artifact = driver.putCardInHand(player, "Test Artifact")
        driver.giveMana(player, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(player, artifact)
        driver.resolveStack()

        withClue("casting an artifact spell mints one Dinosaur token") {
            driver.dinosaurTokenCount(player) shouldBe 1
        }
        val token = driver.getPermanents(player).first { driver.getCardName(it) == "Dinosaur Token" }
        val projected = projector.project(driver.state)
        withClue("the token is a 3/1") {
            projected.getPower(token) shouldBe 3
            projected.getToughness(token) shouldBe 1
        }
    }

    test("the artifact-cast trigger fires only once each turn, then resets next turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Poetic Ingenuity")

        // Cast two artifact spells in the same turn — only the first mints a token.
        val first = driver.putCardInHand(player, "Test Artifact")
        driver.giveMana(player, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(player, first)
        driver.resolveStack()
        driver.dinosaurTokenCount(player) shouldBe 1

        val second = driver.putCardInHand(player, "Test Artifact")
        driver.giveMana(player, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(player, second)
        driver.resolveStack()
        withClue("a second artifact spell the same turn must not mint another token") {
            driver.dinosaurTokenCount(player) shouldBe 1
        }

        // Next turn the cap resets — casting an artifact mints a token again.
        driver.advanceToMyNextMain(player)
        val third = driver.putCardInHand(player, "Test Artifact")
        driver.giveMana(player, com.wingedsheep.sdk.core.Color.RED, 1)
        driver.castSpell(player, third)
        driver.resolveStack()
        withClue("the cap resets each turn, so next turn's cast mints another token") {
            driver.dinosaurTokenCount(player) shouldBe 2
        }
    }
})
